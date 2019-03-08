package net.shadowmage.ancientwarfare.structure.template.build.validation.border;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.core.util.TriFunction;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

public class SmoothedBorderFiller {
	private World world;
	private SmoothingMatrix smoothingMatrix;
	private int borderSize;
	private Set<HorizontalCoords> toProcess = new LinkedHashSet<>();

	public SmoothedBorderFiller(World world, SmoothingMatrix smoothingMatrix, int borderSize) {
		this.world = world;
		this.smoothingMatrix = smoothingMatrix;
		this.borderSize = borderSize;
	}

	void fillInBorderPointsToSmooth(HorizontalCoords firstToSmooth, TriFunction<Integer, Integer, Integer, Integer> getYBelowFloatingIsland) {
		toProcess.add(firstToSmooth);

		while (!toProcess.isEmpty()) {
			Iterator<HorizontalCoords> it = toProcess.iterator();
			HorizontalCoords current = it.next();
			it.remove();

			boolean newPoint = false;
			if (smoothingMatrix.isEmpty(current)) {
				addSmoothedBorderPoint(getYBelowFloatingIsland, current);
				newPoint = true;
			}
			Optional<SmoothingPoint> point = smoothingMatrix.getPoint(current);
			if (point.isPresent()) {
				checkAroundPoint(current, (SmoothedBorderPoint) point.get(), newPoint);
			}
		}
	}

	private void checkAroundPoint(HorizontalCoords current, SmoothedBorderPoint point, boolean newPoint) {
		Set<HorizontalCoords> potentiallyFill = new HashSet<>();
		boolean recheckSurroundingDistances = false;
		Set<HorizontalCoords> toRecheckDistances = new HashSet<>();
		for (HorizontalCoords offset : HorizontalCoords.ADJACENT_OFFSETS) {
			HorizontalCoords adjacent = current.add(offset);

			Optional<SmoothingPoint> adjacentPoint = smoothingMatrix.getPoint(adjacent);
			if (!adjacentPoint.isPresent()) {
				potentiallyFill.add(adjacent);
			} else if (newPoint && adjacentPoint.get().getType() == SmoothingPoint.Type.STRUCTURE_BORDER) {
				point.updateBorderCoordsIfCloser(1, adjacentPoint.get());
			} else if (adjacentPoint.get().getType() == SmoothingPoint.Type.SMOOTHED_BORDER) {
				if (newPoint && !point.updateBorderCoordsIfCloser(adjacentPoint.get().getStructureBorderDistance() + 1, adjacentPoint.get().getClosestBorderPoint())) {
					toProcess.add(adjacent);
				} else if (!newPoint) {
					if (point.updateBorderCoordsIfCloser(adjacentPoint.get().getStructureBorderDistance() + 1, adjacentPoint.get().getClosestBorderPoint())) {
						recheckSurroundingDistances = true;
					} else {
						toRecheckDistances.add(new HorizontalCoords(adjacent.getX(), adjacent.getZ()));
					}
				}
			}
		}
		if (point.getDistanceToBorder() < borderSize) {
			toProcess.addAll(potentiallyFill);
		}
		if (recheckSurroundingDistances) {
			toProcess.addAll(toRecheckDistances);
		}
	}

	private void addSmoothedBorderPoint(TriFunction<Integer, Integer, Integer, Integer> getYBelowFloatingIsland, HorizontalCoords current) {
		BlockPos currentPos = new BlockPos(getMinPos().getX() + current.getX(),
				getYBelowFloatingIsland.apply(getMinPos().getX() + current.getX(),
						BlockTools.getTopFilledHeight(world, getMinPos().getX() + current.getX(), getMinPos().getZ() + current.getZ(), false),
						getMinPos().getZ() + current.getZ()),
				getMinPos().getZ() + current.getZ());
		smoothingMatrix.addPoint(new SmoothedBorderPoint(current.getX(), current.getZ(), currentPos));
	}

	private BlockPos getMinPos() {
		return smoothingMatrix.getMinPos();
	}

}
