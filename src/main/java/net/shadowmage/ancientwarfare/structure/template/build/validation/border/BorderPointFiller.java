package net.shadowmage.ancientwarfare.structure.template.build.validation.border;

import net.shadowmage.ancientwarfare.structure.template.build.validation.border.points.BorderPoint;
import net.shadowmage.ancientwarfare.structure.template.build.validation.border.points.PointType;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

public class BorderPointFiller {
	private BorderMatrix borderMatrix;
	private int borderSize;
	private Set<HorizontalCoords> toProcess = new LinkedHashSet<>();

	public BorderPointFiller(BorderMatrix borderMatrix, int borderSize) {
		this.borderMatrix = borderMatrix;
		this.borderSize = borderSize;
	}

	void fillInBorderPointsToSmooth(HorizontalCoords firstToSmooth) {
		toProcess.add(firstToSmooth);

		while (!toProcess.isEmpty()) {
			Iterator<HorizontalCoords> it = toProcess.iterator();
			HorizontalCoords current = it.next();
			it.remove();

			boolean newPoint = false;
			if (borderMatrix.isEmpty(current)) {
				borderMatrix.addPoint(current, PointType.SMOOTHED_BORDER);
				newPoint = true;
			}
			Optional<BorderPoint> point = borderMatrix.getPoint(current);
			if (point.isPresent()) {
				checkAroundPoint(current, point.get(), newPoint);
			}
		}
	}

	private void checkAroundPoint(HorizontalCoords current, BorderPoint point, boolean newPoint) {
		Set<HorizontalCoords> potentiallyFill = new HashSet<>();
		boolean recheckSurroundingDistances = false;
		Set<HorizontalCoords> toRecheckDistances = new HashSet<>();
		for (HorizontalCoords offset : HorizontalCoords.ADJACENT_OFFSETS) {
			HorizontalCoords adjacent = current.add(offset);

			Optional<BorderPoint> adjacentPoint = borderMatrix.getPoint(adjacent);
			if (!adjacentPoint.isPresent()) {
				potentiallyFill.add(adjacent);
			} else if (newPoint && adjacentPoint.get().getType() == PointType.STRUCTURE_BORDER) {
				updateBorderCoordsIfCloser(point, 1, adjacentPoint.get());
			} else if (adjacentPoint.get().getType() == PointType.SMOOTHED_BORDER) {
				if (newPoint && !updateBorderCoordsIfCloser(point, adjacentPoint.get().getStructureBorderDistance() + 1, adjacentPoint.get().getClosestBorderPoint())) {
					toProcess.add(adjacent);
				} else if (!newPoint) {
					if (updateBorderCoordsIfCloser(point, adjacentPoint.get().getStructureBorderDistance() + 1, adjacentPoint.get().getClosestBorderPoint())) {
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

	@SuppressWarnings("unchecked")
	private boolean updateBorderCoordsIfCloser(BorderPoint point, int distance, BorderPoint borderPoint) {
		return point.updateBorderCoordsIfCloser(distance, borderPoint);
	}
}
