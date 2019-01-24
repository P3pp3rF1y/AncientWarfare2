package net.shadowmage.ancientwarfare.structure.template.build.validation.border;

import com.google.common.collect.ImmutableSet;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.structure.template.build.StructureBB;
import net.shadowmage.ancientwarfare.structure.worldgen.WorldStructureGenerator;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class SmoothingMatrix {
	private SmoothingPoint[][] smoothingPoints;
	private World world;
	private final StructureBB bb;
	private int borderSize;
	private final int fullXSize;
	private final int fullZSize;
	private Map<SmoothingPoint.Type, Set<SmoothingPoint>> typePoints = new HashMap<>();
	private final BlockPos minPos;
	private final int groundY;

	private static final Set<HorizontalCoords> ADJACENT_OFFSETS = ImmutableSet.of(new HorizontalCoords(-1, 0), new HorizontalCoords(1, 0),
			new HorizontalCoords(0, -1), new HorizontalCoords(0, 1));

	public SmoothingMatrix(World world, StructureBB bb, int borderSize, int groundY) {
		this.world = world;
		this.bb = bb;
		this.borderSize = borderSize;
		this.groundY = groundY;
		fullXSize = bb.getXSize() + 2 * borderSize + 2 * 2;
		fullZSize = bb.getZSize() + 2 * borderSize + 2 * 2;
		smoothingPoints = initMatrix(fullXSize, fullZSize);
		minPos = bb.min.add(-borderSize - 2, 0, -borderSize - 2);

		inializePoints();
		calculateNewYLevels();
		processSmoothing();
	}

	private void processSmoothing() {
		for (SmoothingPoint point : typePoints.get(SmoothingPoint.Type.SMOOTHED_BORDER)) {
			int totalY = point.getSmoothedPos().getY();
			BlockPos currentPos = point.getSmoothedPos();
			HorizontalCoords current = new HorizontalCoords(point.getX(), point.getZ());
			for (HorizontalCoords adjacent : ADJACENT_OFFSETS) {
				SmoothingPoint adjacentPoint = getPoint(current.add(adjacent));
				totalY += adjacentPoint.getSmoothedPos().getY();
			}
			int averageY = Math.round(totalY / 5f);
			if (currentPos.getY() != averageY && point instanceof SmoothedBorderPoint) {
				((SmoothedBorderPoint) point).setSmoothedPos(new BlockPos(currentPos.getX(), averageY, currentPos.getZ()));
			}
		}
	}

	@Nullable
	private SmoothingPoint getPoint(HorizontalCoords coords) {
		return getPoint(coords.getX(), coords.getZ());
	}

	@Nullable
	private SmoothingPoint getPoint(int x, int z) {
		return smoothingPoints[x][z];
	}

	private void printMatrix() {
		printTypes();
		printDistances();
		printHeights();
	}

	private void printHeights() {
		for (int x = 0; x < fullXSize; x++) {
			for (int z = 0; z < fullZSize; z++) {
				System.out.print(String.format("%02d", (getPoint(x, z) == null || !getPoint(x, z).hasSmoothedPosSet() ? 0 : getPoint(x, z).getSmoothedPos().getY())) + " ");
			}
			System.out.println();
		}
	}

	private void printDistances() {
		for (int x = 0; x < fullXSize; x++) {
			for (int z = 0; z < fullZSize; z++) {
				System.out.print((getPoint(x, z) == null || getPoint(x, z).getStructureBorderDistance() == Integer.MAX_VALUE ? "00"
						: String.format("%02d", getPoint(x, z).getStructureBorderDistance())) + " ");
			}
			System.out.println();
		}
	}

	private void printTypes() {
		for (int x = 0; x < fullXSize; x++) {
			for (int z = 0; z < fullZSize; z++) {
				System.out.print((getPoint(x, z) == null ? " " : getPoint(x, z).getType().getAcronym()) + " ");
			}
			System.out.println();
		}
	}

	private void inializePoints() {
		addStructureBorder();
		HorizontalCoords fillStartPoint = new HorizontalCoords(borderSize + 2 + ((bb.max.getX() - bb.min.getX()) / 2), (borderSize + 2 + (bb.max.getZ() - bb.min.getZ()) / 2));
		if (bb.getXSize() > 2 && bb.getZSize() > 2) {
			fillStructureInside(bb, fillStartPoint);
		}
		addReferencePoints();
		addOuterBorder();

		fillInBorderPointsToSmooth(getFirstPointToSmooth());
	}

	private void calculateNewYLevels() {
		for (SmoothingPoint outerBorderPoint : typePoints.get(SmoothingPoint.Type.OUTER_BORDER)) {
			Set<SmoothedBorderPoint> borderPointsToSmooth = getPointsToClosestStructureBorder(outerBorderPoint);
			SmoothingPoint referencePoint = getReferencePoint(outerBorderPoint);
			double outerDistToStructure = outerBorderPoint.getStructureBorderDistance();
			int structureBorderY = outerBorderPoint.getClosestBorderPoint().getWorldPos().getY();
			int yDiff = outerBorderPoint.getWorldPos().getY() - structureBorderY;
			boolean steepFormula = yDiff >= 0 ? referencePoint.getWorldPos().getY() - outerBorderPoint.getWorldPos().getY() > 2 : referencePoint.getWorldPos().getY() - outerBorderPoint.getWorldPos().getY() < -2;

			for (SmoothedBorderPoint pointToSmooth : borderPointsToSmooth) {
				double pointDist = pointToSmooth.getStructureBorderDistance();
				if (steepFormula) {
					double ratio = pointDist / outerDistToStructure;
					setNewSmoothedY(pointToSmooth, structureBorderY + (int) (yDiff * ratio * ratio));
				} else {
					double halfDist = outerDistToStructure / 2;
					int halfYDiff = yDiff / 2;
					if (pointDist <= halfDist) {
						double ratio = pointDist / halfDist;
						setNewSmoothedY(pointToSmooth, structureBorderY + (int) (halfYDiff * ratio * ratio));
					} else {
						double ratio = (outerDistToStructure - pointDist) / halfDist;
						setNewSmoothedY(pointToSmooth, outerBorderPoint.getWorldPos().getY() - (int) (halfYDiff * ratio * ratio));
					}
				}

			}
		}
	}

	private void setNewSmoothedY(SmoothedBorderPoint pointToSmooth, int newY) {
		pointToSmooth.setSmoothedPos(new BlockPos(pointToSmooth.getWorldPos().getX(), newY, pointToSmooth.getWorldPos().getZ()));
	}

	private SmoothingPoint getReferencePoint(SmoothingPoint outerBorderPoint) {
		SmoothingPoint structureBorderPoint = outerBorderPoint.getClosestBorderPoint();
		int xDiff = structureBorderPoint.getX() - outerBorderPoint.getX();
		int zDiff = structureBorderPoint.getZ() - outerBorderPoint.getZ();

		int higherValue = Math.max(Math.abs(xDiff), Math.abs(zDiff));
		float incrementX = (float) xDiff / higherValue;
		float incrementZ = (float) zDiff / higherValue;

		float currentX = outerBorderPoint.getX() - incrementX;
		float currentZ = outerBorderPoint.getZ() - incrementZ;

		SmoothingPoint referencePoint;
		do {
			referencePoint = getPoint((int) currentX, (int) currentZ);

			if (referencePoint == null || (referencePoint.getType() != SmoothingPoint.Type.REFERENCE_POINT && referencePoint.getType() != SmoothingPoint.Type.OUTER_BORDER)) {
				throw new IllegalArgumentException("point mismatch, there's supposed to be a reference point here or at most another border");
			}

			currentX += incrementX;
			currentZ += incrementZ;
		} while (referencePoint.getType() != SmoothingPoint.Type.REFERENCE_POINT);

		return referencePoint;
	}

	private Set<SmoothedBorderPoint> getPointsToClosestStructureBorder(SmoothingPoint outerBorderPoint) {
		SmoothingPoint structureBorderPoint = outerBorderPoint.getClosestBorderPoint();
		int xDiff = structureBorderPoint.getX() - outerBorderPoint.getX();
		int zDiff = structureBorderPoint.getZ() - outerBorderPoint.getZ();

		int totalSteps = Math.max(Math.abs(xDiff), Math.abs(zDiff));
		float incrementX = ((float) xDiff) / totalSteps;
		float incrementZ = ((float) zDiff) / totalSteps;

		Set<SmoothedBorderPoint> pointsToSmooth = new HashSet<>();

		for (int step = 1; step < totalSteps; step++) {
			SmoothingPoint smoothPoint = getPoint(outerBorderPoint.getX() + (int) (step * incrementX), outerBorderPoint.getZ() + (int) (step * incrementZ));
			if (smoothPoint instanceof SmoothedBorderPoint && !smoothPoint.hasSmoothedPosSet()) {
				pointsToSmooth.add((SmoothedBorderPoint) smoothPoint);
			}
		}
		return pointsToSmooth;
	}

	private void fillInBorderPointsToSmooth(HorizontalCoords firstToSmooth) {
		Set<HorizontalCoords> toFill = new LinkedHashSet<>();
		toFill.add(firstToSmooth);

		while (!toFill.isEmpty()) {
			Iterator<HorizontalCoords> it = toFill.iterator();
			HorizontalCoords current = it.next();
			it.remove();

			Set<HorizontalCoords> toRecheckDistance = new HashSet<>();
			BlockPos currentPos = new BlockPos(minPos.getX() + current.getX(),
					BlockTools.getTopFilledHeight(world, minPos.getX() + current.getX(), minPos.getZ() + current.getZ(), false),
					minPos.getZ() + current.getZ());
			SmoothedBorderPoint currentPoint = new SmoothedBorderPoint(current.getX(), current.getZ(), currentPos);
			addPoint(currentPoint);
			for (HorizontalCoords offset : ADJACENT_OFFSETS) {
				HorizontalCoords adjacent = current.add(offset);

				SmoothingPoint adjacentPoint = getPoint(adjacent);
				if (isEmpty(adjacentPoint)) {
					toFill.add(adjacent);
				} else if (adjacentPoint.getType() == SmoothingPoint.Type.STRUCTURE_BORDER) {
					currentPoint.updateBorderCoordsIfCloser(1, adjacentPoint);
				} else if (adjacentPoint.getType() == SmoothingPoint.Type.SMOOTHED_BORDER) {
					if (!currentPoint.updateBorderCoordsIfCloser(adjacentPoint.getStructureBorderDistance() + 1, adjacentPoint.getClosestBorderPoint())) {
						toRecheckDistance.add(adjacent);
					}
				} else if (adjacentPoint.getType() == SmoothingPoint.Type.OUTER_BORDER) {
					toRecheckDistance.add(adjacent);
				}
			}
			recheckDistances(toRecheckDistance);
		}
	}

	private void recheckDistances(Set<HorizontalCoords> toRecheckDistance) {
		while (!toRecheckDistance.isEmpty()) {
			Iterator<HorizontalCoords> it = toRecheckDistance.iterator();
			HorizontalCoords currentToRecheck = it.next();
			it.remove();

			toRecheckDistance.addAll(recheckPointDistanceAndGetMoreToCheck(currentToRecheck));
		}
	}

	private Set<HorizontalCoords> recheckPointDistanceAndGetMoreToCheck(HorizontalCoords currentToRecheck) {
		Set<HorizontalCoords> adjacentSmoothedOrOuter = new HashSet<>();
		boolean recheckAdjacent = false;
		SmoothingPoint currentPoint = getPoint(currentToRecheck);

		for (HorizontalCoords offset : ADJACENT_OFFSETS) {
			HorizontalCoords adjacent = currentToRecheck.add(offset);

			SmoothingPoint adjacentPoint = getPoint(adjacent);
			if (isEmpty(adjacentPoint)) {
				continue;
			}

			if (adjacentPoint.getType() == SmoothingPoint.Type.SMOOTHED_BORDER || adjacentPoint.getType() == SmoothingPoint.Type.OUTER_BORDER) {
				//noinspection ConstantConditions
				if (adjacentPoint.getStructureBorderDistance() != Integer.MAX_VALUE
						&& currentPoint.updateBorderCoordsIfCloser(adjacentPoint.getStructureBorderDistance() + 1, adjacentPoint.getClosestBorderPoint())) {
					recheckAdjacent = true;
				} else {
					adjacentSmoothedOrOuter.add(adjacent);
				}
			}
		}
		return recheckAdjacent ? adjacentSmoothedOrOuter : Collections.emptySet();
	}

	private HorizontalCoords getFirstPointToSmooth() {
		Set<SmoothingPoint> borderPoints = typePoints.get(SmoothingPoint.Type.STRUCTURE_BORDER);
		SmoothingPoint firstBorderPoint = borderPoints.iterator().next();

		HorizontalCoords firstCoords = new HorizontalCoords(firstBorderPoint.getX(), firstBorderPoint.getZ());
		for (HorizontalCoords offset : ADJACENT_OFFSETS) {
			HorizontalCoords point = firstCoords.add(offset);
			if (isEmpty(point)) {
				return point;
			}
		}
		throw new IllegalArgumentException("Incorrect state of matrix or structure data, structure border point should always have snmoothed point on one side");
	}

	private boolean isEmpty(HorizontalCoords point) {
		return isEmpty(getPoint(point));
	}

	private boolean isEmpty(@Nullable SmoothingPoint point) {
		return point == null;
	}

	private void addOuterBorder() {
		addRingPoints(new HorizontalCoords(1, 1), new HorizontalCoords(fullXSize - 2, fullZSize - 2), SmoothingPoint.Type.OUTER_BORDER);
	}

	private void addReferencePoints() {
		addRingPoints(new HorizontalCoords(0, 0), new HorizontalCoords(fullXSize - 1, fullZSize - 1), SmoothingPoint.Type.REFERENCE_POINT);
	}

	private void fillStructureInside(StructureBB bb, HorizontalCoords fillStartPoint) {
		Set<HorizontalCoords> pointsToFill = new HashSet<>();
		pointsToFill.add(fillStartPoint);
		while (!pointsToFill.isEmpty()) {
			Iterator<HorizontalCoords> it = pointsToFill.iterator();
			HorizontalCoords nextToFill = it.next();
			it.remove();

			addPoint(nextToFill.getX(), nextToFill.getZ(),
					new BlockPos(bb.min.getX() - borderSize - 2 + nextToFill.getX(), 1, bb.min.getZ() - borderSize - 2 + nextToFill.getZ()),
					SmoothingPoint.Type.STRUCTURE_INSIDE);

			addAdjacentIfEmpty(pointsToFill, new HorizontalCoords(nextToFill.getX() + 1, nextToFill.getZ()));
			addAdjacentIfEmpty(pointsToFill, new HorizontalCoords(nextToFill.getX() - 1, nextToFill.getZ()));
			addAdjacentIfEmpty(pointsToFill, new HorizontalCoords(nextToFill.getX(), nextToFill.getZ() + 1));
			addAdjacentIfEmpty(pointsToFill, new HorizontalCoords(nextToFill.getX(), nextToFill.getZ() - 1));
		}
	}

	private void addAdjacentIfEmpty(Set<HorizontalCoords> pointsToFill, HorizontalCoords adjacent) {
		if (isEmpty(adjacent)) {
			pointsToFill.add(adjacent);
		}
	}

	private void addRingPoints(HorizontalCoords min, HorizontalCoords max, SmoothingPoint.Type type) {
		addRingPoints(min, max, type, 0);
	}

	private void addRingPoints(HorizontalCoords min, HorizontalCoords max, SmoothingPoint.Type type, int yLevel) {
		for (int x = min.getX(); x <= max.getX(); x++) {
			addPoint(x, min.getZ(), type, yLevel);
			addPoint(x, max.getZ(), type, yLevel);
		}
		for (int z = min.getZ() + 1; z <= max.getZ() - 1; z++) {
			addPoint(min.getX(), z, type, yLevel);
			addPoint(max.getX(), z, type, yLevel);
		}
	}

	private void addPoint(int x, int z, SmoothingPoint.Type type, int yLevel) {
		int y = yLevel;
		if (yLevel == 0) {
			y = WorldStructureGenerator.getTargetY(world, minPos.getX() + x, minPos.getZ() + z, true);
		}
		BlockPos pos = new BlockPos(minPos.getX() + x, y, minPos.getZ() + z);
		addPoint(x, z, pos, type);
	}

	private void addPoint(int x, int z, BlockPos pos, SmoothingPoint.Type type) {
		SmoothingPoint point = new SmoothingPoint(x, z, pos, type);
		addPoint(point);
	}

	private void addPoint(SmoothingPoint point) {
		smoothingPoints[point.getX()][point.getZ()] = point;
		addTypePoint(point.getType(), point);
	}

	private void addStructureBorder() {
		addRingPoints(new HorizontalCoords(2 + borderSize, 2 + borderSize),
				new HorizontalCoords(fullXSize - 2 - borderSize - 1, fullZSize - 2 - borderSize - 1), SmoothingPoint.Type.STRUCTURE_BORDER, groundY);
	}

	private SmoothingPoint[][] initMatrix(int fullXSize, int fullZSize) {
		SmoothingPoint[][] ret = new SmoothingPoint[fullXSize][];

		for (int x = 0; x < fullXSize; x++) {
			ret[x] = new SmoothingPoint[fullZSize];
		}

		return ret;
	}

	private void addTypePoint(SmoothingPoint.Type type, SmoothingPoint point) {
		if (!typePoints.containsKey(type)) {
			typePoints.put(type, new HashSet<>());
		}
		typePoints.get(type).add(point);
	}

	public void apply(Consumer<BlockPos> handleClearing) {
		typePoints.get(SmoothingPoint.Type.SMOOTHED_BORDER).forEach(point -> levelTerrain(point, handleClearing));
	}

	private void levelTerrain(SmoothingPoint point, Consumer<BlockPos> handleClearing) {
		BlockPos originalPos = point.getWorldPos();
		BlockPos smoothedPos = point.getSmoothedPos();

		int topSolidY = WorldStructureGenerator.getTargetY(world, originalPos.getX(), originalPos.getZ(), false);
		Biome biome = world.getBiome(originalPos);
		IBlockState topBlock = biome.topBlock;
		int topNonWaterY = WorldStructureGenerator.getTargetY(world, originalPos.getX(), originalPos.getZ(), true);
		boolean seaWaterTop = false;
		if (smoothedPos.getY() <= world.getSeaLevel() && world.getBlockState(new BlockPos(smoothedPos.getX(), world.getSeaLevel() - 1, smoothedPos.getZ())).getMaterial() == Material.WATER) {
			seaWaterTop = true;
		}

		if (originalPos.getY() == smoothedPos.getY() && topSolidY == originalPos.getY() && !seaWaterTop) {
			return;
		}

		if (originalPos.getY() > smoothedPos.getY() && (!seaWaterTop || topNonWaterY > smoothedPos.getY())) {
			if (seaWaterTop) {
				BlockTools.getAllInBoxTopDown(smoothedPos, new BlockPos(smoothedPos.getX(), topNonWaterY, smoothedPos.getZ()))
						.forEach(pos -> world.setBlockState(pos, Blocks.WATER.getDefaultState()));
			} else {
				BlockTools.getAllInBoxTopDown(smoothedPos, originalPos).forEach(handleClearing);
			}
		}
		if (smoothedPos.getY() - topNonWaterY > 1) {
			IBlockState fillerBlock = biome.fillerBlock;
			BlockPos.getAllInBox(originalPos.getX(), topNonWaterY + 1, originalPos.getZ(), originalPos.getX(), smoothedPos.getY() - 1, originalPos.getZ())
					.forEach(pos -> world.setBlockState(pos, fillerBlock));
		}
		world.setBlockState(smoothedPos, topBlock);
	}
}
