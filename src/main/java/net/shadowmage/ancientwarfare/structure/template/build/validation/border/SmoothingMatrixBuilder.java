package net.shadowmage.ancientwarfare.structure.template.build.validation.border;

import com.google.common.collect.ImmutableSet;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.structure.template.build.StructureBB;
import net.shadowmage.ancientwarfare.structure.worldgen.WorldStructureGenerator;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;

public class SmoothingMatrixBuilder {
	private final World world;
	private final StructureBB bb;
	private final int borderSize;
	private final int groundY;
	private final SmoothingMatrix smoothingMatrix;

	public SmoothingMatrixBuilder(World world, StructureBB bb, int borderSize, int groundY) {
		smoothingMatrix = new SmoothingMatrix(bb, borderSize);
		this.world = world;
		this.bb = bb;
		this.borderSize = borderSize;
		this.groundY = groundY;
	}

	public SmoothingMatrix build() {
		inializePoints();
		calculateNewYLevelsAndBlocks();
		processSmoothing();

		return smoothingMatrix;
	}

	private void inializePoints() {
		addStructureBorder();
		HorizontalCoords fillStartPoint = new HorizontalCoords(borderSize + 2 + ((bb.max.getX() - bb.min.getX()) / 2), (borderSize + 2 + (bb.max.getZ() - bb.min.getZ()) / 2));
		if (bb.getXSize() > 2 && bb.getZSize() > 2) {
			fillStructureInside(bb, fillStartPoint);
		}

		new SmoothedBorderFiller(world, smoothingMatrix, borderSize)
				.fillInBorderPointsToSmooth(getFirstPointToSmooth(), (x, y, z) -> getYBelowFloatingIsland(x, y, z, false));

		surroundWith(SmoothingPoint.Type.SMOOTHED_BORDER, SmoothingPoint.Type.OUTER_BORDER);
		surroundWith(SmoothingPoint.Type.OUTER_BORDER, SmoothingPoint.Type.REFERENCE_POINT);
	}

	private void surroundWith(SmoothingPoint.Type innerType, SmoothingPoint.Type outerType) {
		addPointsNextTo(innerType, outerType);
		fillSpacesInType(innerType, outerType);
	}

	private void addPointsNextTo(SmoothingPoint.Type innerType, SmoothingPoint.Type outerType) {
		for (SmoothingPoint point : smoothingMatrix.getPointsOfType(innerType)) {
			if (point.getDistanceToBorder() < borderSize) {
				continue;
			}
			HorizontalCoords current = new HorizontalCoords(point.getX(), point.getZ());
			for (HorizontalCoords offset : HorizontalCoords.ADJACENT_OFFSETS) {
				HorizontalCoords adjacent = current.add(offset);
				if (smoothingMatrix.isEmpty(adjacent)) {
					addPoint(adjacent, outerType);
					smoothingMatrix.getPoint(adjacent).ifPresent(p ->
							p.updateBorderCoordsIfCloser(point.getStructureBorderDistance() + 1, point.getClosestBorderPoint()));
				}
			}
		}
	}

	private void fillSpacesInType(SmoothingPoint.Type innerType, SmoothingPoint.Type outerType) {
		Set<SmoothingPoint> pointsToFill = new HashSet<>();
		HorizontalCoords innerTypePointOffset = null;
		for (SmoothingPoint point : smoothingMatrix.getPointsOfType(outerType)) {
			int innerTypeCount = 0;
			Set<HorizontalCoords> empty = new HashSet<>();
			HorizontalCoords current = new HorizontalCoords(point.getX(), point.getZ());
			for (HorizontalCoords offset : HorizontalCoords.ADJACENT_OFFSETS) {
				HorizontalCoords adjacent = current.add(offset);
				Optional<SmoothingPoint> adjPoint = smoothingMatrix.getPoint(adjacent);
				if (adjPoint.isPresent()) {
					SmoothingPoint adjacentPoint = adjPoint.get();
					if (adjacentPoint.getType() == innerType) {
						innerTypePointOffset = offset;
						innerTypeCount++;
					}
				} else {
					empty.add(adjacent);
				}
			}
			if (innerTypeCount > 1) {
				for (HorizontalCoords coord : empty) {
					addPointToFill(outerType, pointsToFill, point, coord);
				}
			} else if (innerTypePointOffset != null) {
				for (HorizontalCoords coords : innerTypePointOffset.getPerpendicular()) {
					HorizontalCoords adjacent = current.add(coords);
					if (smoothingMatrix.isEmpty(adjacent)) {
						addPointToFill(outerType, pointsToFill, point, adjacent);
					}
				}
			}
		}
		pointsToFill.forEach(smoothingMatrix::addPoint);
	}

	private void addPointToFill(SmoothingPoint.Type outerType, Set<SmoothingPoint> pointsToFill, SmoothingPoint point, HorizontalCoords coord) {
		int x = coord.getX();
		int z = coord.getZ();
		BlockPos pos = new BlockPos(getMinPos().getX() + x, getY(x, z, 0), getMinPos().getZ() + z);
		SmoothingPoint newPoint = new SmoothingPoint(x, z, pos, outerType);
		newPoint.updateBorderCoordsIfCloser(point.getStructureBorderDistance() + 1, point.getClosestBorderPoint());
		newPoint.setBlockState(world.getBlockState(pos));
		pointsToFill.add(newPoint);
	}

	private void processSmoothing() {
		for (SmoothingPoint point : smoothingMatrix.getPointsOfType(SmoothingPoint.Type.SMOOTHED_BORDER)) {
			int totalY = point.getSmoothedPos().getY();
			BlockPos currentPos = point.getSmoothedPos();
			HorizontalCoords current = new HorizontalCoords(point.getX(), point.getZ());
			for (HorizontalCoords adjacent : HorizontalCoords.ADJACENT_OFFSETS) {
				Optional<SmoothingPoint> adjacentPoint = smoothingMatrix.getPoint(current.add(adjacent));
				if (adjacentPoint.isPresent()) {
					totalY += adjacentPoint.get().getSmoothedPos().getY();
				}
			}
			int averageY = Math.round(totalY / 5f);
			if (currentPos.getY() != averageY && point instanceof SmoothedBorderPoint) {
				((SmoothedBorderPoint) point).setSmoothedPos(new BlockPos(currentPos.getX(), averageY, currentPos.getZ()));
			}
		}
	}

	private void calculateNewYLevelsAndBlocks() {
		for (SmoothingPoint outerBorderPoint : smoothingMatrix.getPointsOfType(SmoothingPoint.Type.OUTER_BORDER)) {
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
					setNewSmoothedY(pointToSmooth, structureBorderY + (int) Math.round(yDiff * ratio * ratio));
				} else {
					double halfDist = outerDistToStructure / 2;
					int halfYDiff = yDiff / 2;
					if (pointDist <= halfDist) {
						double ratio = pointDist / halfDist;
						setNewSmoothedY(pointToSmooth, structureBorderY + (int) Math.round(halfYDiff * ratio * ratio));
					} else {
						double ratio = (outerDistToStructure - pointDist) / halfDist;
						setNewSmoothedY(pointToSmooth, outerBorderPoint.getWorldPos().getY() - (int) Math.round(halfYDiff * ratio * ratio));
					}
				}
				setBlockStateForPoint(outerBorderPoint, outerDistToStructure, yDiff, pointToSmooth, pointDist);
			}
		}
	}

	private void setBlockStateForPoint(SmoothingPoint outerBorderPoint, double outerDistToStructure, int yDiff, SmoothedBorderPoint pointToSmooth, double pointDist) {
		if (!pointToSmooth.getClosestBorderPoint().useStateForBlending()) {
			pointToSmooth.setBlockState(outerBorderPoint.getBlockState());
			return;
		}

		if (Math.round(yDiff / outerDistToStructure) < 2) {
			pointToSmooth.setBlockState(
					world.rand.nextDouble() > pointDist / outerDistToStructure ? pointToSmooth.getClosestBorderPoint().getBlockState() : outerBorderPoint.getBlockState());
		} else {
			pointToSmooth.setBlockState(world.getBiome(pointToSmooth.getWorldPos()).topBlock);
		}
	}

	private void setNewSmoothedY(SmoothedBorderPoint pointToSmooth, int newY) {
		pointToSmooth.setSmoothedPos(new BlockPos(pointToSmooth.getWorldPos().getX(), newY, pointToSmooth.getWorldPos().getZ()));
	}

	private SmoothingPoint getReferencePoint(SmoothingPoint outerBorderPoint) {
		SmoothingPoint structureBorderPoint = outerBorderPoint.getClosestBorderPoint();
		int xDiff = outerBorderPoint.getX() - structureBorderPoint.getX();
		int zDiff = outerBorderPoint.getZ() - structureBorderPoint.getZ();

		int higherValue = Math.max(Math.abs(xDiff), Math.abs(zDiff));
		float incrementX = (float) xDiff / higherValue;
		float incrementZ = (float) zDiff / higherValue;

		float currentX = outerBorderPoint.getX() + incrementX;
		float currentZ = outerBorderPoint.getZ() + incrementZ;

		SmoothingPoint referencePoint;
		do {
			Optional<SmoothingPoint> refPoint = smoothingMatrix.getPoint((int) currentX, (int) currentZ);

			if (!refPoint.isPresent() || (refPoint.get().getType() != SmoothingPoint.Type.REFERENCE_POINT && refPoint.get().getType() != SmoothingPoint.Type.OUTER_BORDER)) {
				throw new IllegalArgumentException("point mismatch, there's supposed to be a reference point here or at most another border");
			}
			referencePoint = refPoint.get();

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
			smoothingMatrix.getPoint(outerBorderPoint.getX() + (int) (step * incrementX), outerBorderPoint.getZ() + (int) (step * incrementZ)).ifPresent(smoothPoint -> {
				if (smoothPoint instanceof SmoothedBorderPoint && !smoothPoint.hasSmoothedPosSet()) {
					pointsToSmooth.add((SmoothedBorderPoint) smoothPoint);
				}
			});
		}
		return pointsToSmooth;
	}

	private HorizontalCoords getFirstPointToSmooth() {
		Set<SmoothingPoint> borderPoints = smoothingMatrix.getPointsOfType(SmoothingPoint.Type.STRUCTURE_BORDER);
		SmoothingPoint firstBorderPoint = borderPoints.iterator().next();

		HorizontalCoords firstCoords = new HorizontalCoords(firstBorderPoint.getX(), firstBorderPoint.getZ());
		for (HorizontalCoords offset : HorizontalCoords.ADJACENT_OFFSETS) {
			HorizontalCoords point = firstCoords.add(offset);
			if (smoothingMatrix.isEmpty(point)) {
				return point;
			}
		}
		throw new IllegalArgumentException("Incorrect state of matrix or structure data, structure border point should always have snmoothed point on one side");
	}

	private void addStructureBorder() {
		addRingPoints(new HorizontalCoords(2 + borderSize, 2 + borderSize),
				new HorizontalCoords(smoothingMatrix.getFullXSize() - 2 - borderSize - 1, smoothingMatrix.getFullZSize() - 2 - borderSize - 1), SmoothingPoint.Type.STRUCTURE_BORDER, groundY);
	}

	private void fillStructureInside(StructureBB bb, HorizontalCoords fillStartPoint) {
		Set<HorizontalCoords> pointsToFill = new HashSet<>();
		pointsToFill.add(fillStartPoint);
		while (!pointsToFill.isEmpty()) {
			Iterator<HorizontalCoords> it = pointsToFill.iterator();
			HorizontalCoords nextToFill = it.next();
			it.remove();

			smoothingMatrix.addPoint(nextToFill.getX(), nextToFill.getZ(),
					new BlockPos(bb.min.getX() - borderSize - 2 + nextToFill.getX(), 1, bb.min.getZ() - borderSize - 2 + nextToFill.getZ()),
					SmoothingPoint.Type.STRUCTURE_INSIDE);

			addAdjacentIfEmpty(pointsToFill, new HorizontalCoords(nextToFill.getX() + 1, nextToFill.getZ()));
			addAdjacentIfEmpty(pointsToFill, new HorizontalCoords(nextToFill.getX() - 1, nextToFill.getZ()));
			addAdjacentIfEmpty(pointsToFill, new HorizontalCoords(nextToFill.getX(), nextToFill.getZ() + 1));
			addAdjacentIfEmpty(pointsToFill, new HorizontalCoords(nextToFill.getX(), nextToFill.getZ() - 1));
		}
	}

	private void addAdjacentIfEmpty(Set<HorizontalCoords> pointsToFill, HorizontalCoords adjacent) {
		if (smoothingMatrix.isEmpty(adjacent)) {
			pointsToFill.add(adjacent);
		}
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

	private void addPoint(HorizontalCoords coords, SmoothingPoint.Type type) {
		addPoint(coords.getX(), coords.getZ(), type, 0);
	}

	private void addPoint(int x, int z, SmoothingPoint.Type type, int yLevel) {
		BlockPos pos = new BlockPos(getMinPos().getX() + x, getY(x, z, yLevel), getMinPos().getZ() + z);
		Optional<IBlockState> state = getPointBlockState(pos, type);
		if (state.isPresent()) {
			smoothingMatrix.addPoint(x, z, pos, type, state.get());
		} else {
			smoothingMatrix.addPoint(x, z, pos, type);
		}
	}

	private int getY(int x, int z, int yLevel) {
		int y = yLevel;
		if (yLevel == 0) {
			y = WorldStructureGenerator.getTargetY(world, getMinPos().getX() + x, getMinPos().getZ() + z, true);
			y = getYBelowFloatingIsland(getMinPos().getX() + x, y, getMinPos().getZ() + z);
		}
		return y;
	}

	private BlockPos getMinPos() {
		return smoothingMatrix.getMinPos();
	}

	private static final Set<Block> STRUCTURE_BORDER_BLOCK_WHITELIST = ImmutableSet.of(
			Blocks.STONE,
			Blocks.GRAVEL,
			Blocks.DIRT,
			Blocks.GRASS,
			Blocks.SAND,
			Blocks.SANDSTONE,
			Blocks.COBBLESTONE,
			Blocks.MOSSY_COBBLESTONE,
			Blocks.OBSIDIAN,
			Blocks.SNOW,
			Blocks.NETHERRACK,
			Blocks.SOUL_SAND,
			Blocks.MYCELIUM,
			Blocks.END_STONE,
			Blocks.HARDENED_CLAY,
			Blocks.CLAY,
			Blocks.GRASS_PATH,
			Blocks.ICE,
			Blocks.PACKED_ICE,
			Blocks.WATER,
			Blocks.LAVA,
			Blocks.RED_SANDSTONE,
			Blocks.STAINED_HARDENED_CLAY
	);

	private Optional<IBlockState> getPointBlockState(BlockPos pos, SmoothingPoint.Type type) {
		IBlockState state = world.getBlockState(pos);
		if (type == SmoothingPoint.Type.STRUCTURE_BORDER && !STRUCTURE_BORDER_BLOCK_WHITELIST.contains(state.getBlock())) {
			return Optional.empty();
		}
		return Optional.of(state);
	}

	private int getYBelowFloatingIsland(int x, int y, int z) {
		return getYBelowFloatingIsland(x, y, z, true);
	}

	private int getYBelowFloatingIsland(int x, int y, int z, boolean useSkippables) {
		if (y - groundY > borderSize) {
			//try restarting search in between groundY and found Y
			int startAtY = groundY + ((y - groundY) / 2);
			int newY = useSkippables ? WorldStructureGenerator.getTargetY(world, x, z, true, startAtY) :
					BlockTools.getTopFilledHeight(world, x, z, false, startAtY);
			if (newY != startAtY) {
				return newY;
			}
		}
		return y;
	}
}
