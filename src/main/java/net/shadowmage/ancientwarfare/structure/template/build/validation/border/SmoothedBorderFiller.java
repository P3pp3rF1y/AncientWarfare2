package net.shadowmage.ancientwarfare.structure.template.build.validation.border;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.core.util.TriFunction;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

public class SmoothedBorderFiller {
	private World world;
	private SmoothingMatrix smoothingMatrix;

	public SmoothedBorderFiller(World world, SmoothingMatrix smoothingMatrix) {
		this.world = world;
		this.smoothingMatrix = smoothingMatrix;
	}

	void fillInBorderPointsToSmooth(HorizontalCoords firstToSmooth, TriFunction<Integer, Integer, Integer, Integer> getYBelowFloatingIsland) {
		Set<HorizontalCoords> toFill = new LinkedHashSet<>();
		toFill.add(firstToSmooth);

		while (!toFill.isEmpty()) {
			Iterator<HorizontalCoords> it = toFill.iterator();
			HorizontalCoords current = it.next();
			it.remove();

			Set<HorizontalCoords> toRecheckDistance = new HashSet<>();
			BlockPos currentPos = new BlockPos(getMinPos().getX() + current.getX(),
					getYBelowFloatingIsland.apply(getMinPos().getX() + current.getX(),
							BlockTools.getTopFilledHeight(world, getMinPos().getX() + current.getX(), getMinPos().getZ() + current.getZ(), false),
							getMinPos().getZ() + current.getZ()),
					getMinPos().getZ() + current.getZ());
			SmoothedBorderPoint currentPoint = new SmoothedBorderPoint(current.getX(), current.getZ(), currentPos);
			smoothingMatrix.addPoint(currentPoint);
			for (HorizontalCoords offset : HorizontalCoords.ADJACENT_OFFSETS) {
				HorizontalCoords adjacent = current.add(offset);

				Optional<SmoothingPoint> adjacentPoint = smoothingMatrix.getPoint(adjacent);
				if (!adjacentPoint.isPresent()) {
					toFill.add(adjacent);
				} else if (adjacentPoint.get().getType() == SmoothingPoint.Type.STRUCTURE_BORDER) {
					currentPoint.updateBorderCoordsIfCloser(1, adjacentPoint.get());
				} else if (adjacentPoint.get().getType() == SmoothingPoint.Type.SMOOTHED_BORDER) {
					if (!currentPoint.updateBorderCoordsIfCloser(adjacentPoint.get().getStructureBorderDistance() + 1, adjacentPoint.get().getClosestBorderPoint())) {
						toRecheckDistance.add(adjacent);
					}
				} else if (adjacentPoint.get().getType() == SmoothingPoint.Type.OUTER_BORDER) {
					toRecheckDistance.add(adjacent);
				}
			}
			recheckDistances(toRecheckDistance);
		}
	}

	private BlockPos getMinPos() {
		return smoothingMatrix.getMinPos();
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
		Optional<SmoothingPoint> currentPoint = smoothingMatrix.getPoint(currentToRecheck);

		for (HorizontalCoords offset : HorizontalCoords.ADJACENT_OFFSETS) {
			HorizontalCoords adjacent = currentToRecheck.add(offset);

			Optional<SmoothingPoint> adjPoint = smoothingMatrix.getPoint(adjacent);
			if (adjPoint.isPresent()) {
				SmoothingPoint adjacentPoint = adjPoint.get();
				if (adjacentPoint.getType() == SmoothingPoint.Type.SMOOTHED_BORDER || adjacentPoint.getType() == SmoothingPoint.Type.OUTER_BORDER) {
					//noinspection ConstantConditions
					if (adjacentPoint.getStructureBorderDistance() != Integer.MAX_VALUE
							&& currentPoint.get().updateBorderCoordsIfCloser(adjacentPoint.getStructureBorderDistance() + 1, adjacentPoint.getClosestBorderPoint())) {
						recheckAdjacent = true;
					} else {
						adjacentSmoothedOrOuter.add(adjacent);
					}
				}
			}
		}
		return recheckAdjacent ? adjacentSmoothedOrOuter : Collections.emptySet();
	}
}
