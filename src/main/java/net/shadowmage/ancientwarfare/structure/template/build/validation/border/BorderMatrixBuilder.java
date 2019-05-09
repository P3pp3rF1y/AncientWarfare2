package net.shadowmage.ancientwarfare.structure.template.build.validation.border;

import net.shadowmage.ancientwarfare.structure.template.build.validation.border.points.BorderPoint;
import net.shadowmage.ancientwarfare.structure.template.build.validation.border.points.PointType;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;

public class BorderMatrixBuilder {
	private final BorderMatrix borderMatrix;
	private final int borderSize;
	private final int xSize;
	private final int zSize;

	public BorderMatrixBuilder(int xSize, int zSize, int borderSize) {
		this.xSize = xSize;
		this.zSize = zSize;
		borderMatrix = new BorderMatrix(xSize, zSize, borderSize);
		this.borderSize = borderSize;
	}

	public BorderMatrix build() {
		inializePoints();

		return borderMatrix;
	}

	private void inializePoints() {
		addStructureBorder();
		HorizontalCoords fillStartPoint = new HorizontalCoords(borderSize + 2 + xSize / 2, (borderSize + 2 + zSize / 2));
		if (xSize > 2 && zSize > 2) {
			fillStructureInside(fillStartPoint);
		}

		new BorderPointFiller(borderMatrix, borderSize)
				.fillInBorderPointsToSmooth(getFirstPointToSmooth());

		surroundWith(PointType.SMOOTHED_BORDER, PointType.OUTER_BORDER);
		surroundWith(PointType.OUTER_BORDER, PointType.REFERENCE_POINT);

		setOuterBorderAndReferencePoints();
	}

	private HorizontalCoords getFirstPointToSmooth() {
		Set<BorderPoint> borderPoints = borderMatrix.getPointsOfType(PointType.STRUCTURE_BORDER);
		BorderPoint firstBorderPoint = borderPoints.iterator().next();

		HorizontalCoords firstCoords = new HorizontalCoords(firstBorderPoint.getX(), firstBorderPoint.getZ());
		for (HorizontalCoords offset : HorizontalCoords.ADJACENT_OFFSETS) {
			HorizontalCoords point = firstCoords.add(offset);
			if (borderMatrix.isEmpty(point)) {
				return point;
			}
		}
		throw new IllegalArgumentException("Incorrect state of matrix or structure data, structure border point should always have snmoothed point on one side");
	}

	private void addStructureBorder() {
		addRingPoints(new HorizontalCoords(2 + borderSize, 2 + borderSize),
				new HorizontalCoords(borderMatrix.getFullXSize() - 2 - borderSize - 1, borderMatrix.getFullZSize() - 2 - borderSize - 1), PointType.STRUCTURE_BORDER);
	}

	private void addRingPoints(HorizontalCoords min, HorizontalCoords max, PointType type) {
		for (int x = min.getX(); x <= max.getX(); x++) {
			borderMatrix.addPoint(x, min.getZ(), type);
			borderMatrix.addPoint(x, max.getZ(), type);
		}
		for (int z = min.getZ() + 1; z <= max.getZ() - 1; z++) {
			borderMatrix.addPoint(min.getX(), z, type);
			borderMatrix.addPoint(max.getX(), z, type);
		}
	}

	private void fillStructureInside(HorizontalCoords fillStartPoint) {
		Set<HorizontalCoords> pointsToFill = new HashSet<>();
		pointsToFill.add(fillStartPoint);
		while (!pointsToFill.isEmpty()) {
			Iterator<HorizontalCoords> it = pointsToFill.iterator();
			HorizontalCoords nextToFill = it.next();
			it.remove();

			borderMatrix.addPoint(nextToFill.getX(), nextToFill.getZ(), PointType.STRUCTURE_INSIDE);

			addAdjacentIfEmpty(pointsToFill, new HorizontalCoords(nextToFill.getX() + 1, nextToFill.getZ()));
			addAdjacentIfEmpty(pointsToFill, new HorizontalCoords(nextToFill.getX() - 1, nextToFill.getZ()));
			addAdjacentIfEmpty(pointsToFill, new HorizontalCoords(nextToFill.getX(), nextToFill.getZ() + 1));
			addAdjacentIfEmpty(pointsToFill, new HorizontalCoords(nextToFill.getX(), nextToFill.getZ() - 1));
		}
	}

	private void addAdjacentIfEmpty(Set<HorizontalCoords> pointsToFill, HorizontalCoords adjacent) {
		if (borderMatrix.isEmpty(adjacent)) {
			pointsToFill.add(adjacent);
		}
	}

	private void surroundWith(PointType innerType, PointType outerType) {
		addPointsNextTo(innerType, outerType);
		fillSpacesInType(innerType, outerType);
	}

	private void addPointsNextTo(PointType innerType, PointType outerType) {
		for (BorderPoint point : borderMatrix.getPointsOfType(innerType)) {
			if (point.getDistanceToBorder() < borderSize) {
				continue;
			}
			HorizontalCoords current = new HorizontalCoords(point.getX(), point.getZ());
			for (HorizontalCoords offset : HorizontalCoords.ADJACENT_OFFSETS) {
				HorizontalCoords adjacent = current.add(offset);
				if (borderMatrix.isEmpty(adjacent)) {
					borderMatrix.addPoint(adjacent, outerType);
					borderMatrix.getPoint(adjacent).ifPresent(p ->
							updateBorderCoordsIfCloser(p, point.getStructureBorderDistance() + 1, point.getClosestBorderPoint()));
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private boolean updateBorderCoordsIfCloser(BorderPoint point, int distance, BorderPoint borderPoint) {
		return point.updateBorderCoordsIfCloser(distance, borderPoint);
	}

	private void fillSpacesInType(PointType innerType, PointType outerType) {
		Set<BorderPoint> pointsToFill = new HashSet<>();
		HorizontalCoords innerTypePointOffset = null;
		for (BorderPoint point : borderMatrix.getPointsOfType(outerType)) {
			int innerTypeCount = 0;
			Set<HorizontalCoords> empty = new HashSet<>();
			HorizontalCoords current = new HorizontalCoords(point.getX(), point.getZ());
			for (HorizontalCoords offset : HorizontalCoords.ADJACENT_OFFSETS) {
				HorizontalCoords adjacent = current.add(offset);
				Optional<BorderPoint> adjPoint = borderMatrix.getPoint(adjacent);
				if (adjPoint.isPresent()) {
					BorderPoint adjacentPoint = adjPoint.get();
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
					if (borderMatrix.isEmpty(adjacent)) {
						addPointToFill(outerType, pointsToFill, point, adjacent);
					}
				}
			}
		}
		pointsToFill.forEach(borderMatrix::addPoint);
	}

	private void addPointToFill(PointType outerType, Set<BorderPoint> pointsToFill, BorderPoint point, HorizontalCoords coord) {
		int x = coord.getX();
		int z = coord.getZ();
		BorderPoint newPoint = new BorderPoint(x, z, outerType);
		updateBorderCoordsIfCloser(newPoint, point.getStructureBorderDistance() + 1, point.getClosestBorderPoint());
		pointsToFill.add(newPoint);
	}

	private void setOuterBorderAndReferencePoints() {
		for (BorderPoint outerBorderPoint : borderMatrix.getPointsOfType(PointType.OUTER_BORDER)) {
			Set<BorderPoint> borderPointsToSmooth = getPointsToClosestStructureBorder(outerBorderPoint);
			BorderPoint referencePoint = getReferencePoint(outerBorderPoint);

			for (BorderPoint pointToSmooth : borderPointsToSmooth) {
				pointToSmooth.setOuterBorderAndReferencePoint(outerBorderPoint, referencePoint);
			}
		}
	}

	private BorderPoint getReferencePoint(BorderPoint outerBorderPoint) {
		BorderPoint structureBorderPoint = outerBorderPoint.getClosestBorderPoint();
		int xDiff = outerBorderPoint.getX() - structureBorderPoint.getX();
		int zDiff = outerBorderPoint.getZ() - structureBorderPoint.getZ();

		int higherValue = Math.max(Math.abs(xDiff), Math.abs(zDiff));
		float incrementX = (float) xDiff / higherValue;
		float incrementZ = (float) zDiff / higherValue;

		float currentX = outerBorderPoint.getX() + incrementX;
		float currentZ = outerBorderPoint.getZ() + incrementZ;

		BorderPoint referencePoint;
		do {
			Optional<BorderPoint> refPoint = borderMatrix.getPoint((int) currentX, (int) currentZ);

			if (!refPoint.isPresent() || (refPoint.get().getType() != PointType.REFERENCE_POINT && refPoint.get().getType() != PointType.OUTER_BORDER)) {
				throw new IllegalArgumentException("point mismatch, there's supposed to be a reference point here or at most another border");
			}
			referencePoint = refPoint.get();

			currentX += incrementX;
			currentZ += incrementZ;
		} while (referencePoint.getType() != PointType.REFERENCE_POINT);

		return referencePoint;
	}

	private Set<BorderPoint> getPointsToClosestStructureBorder(BorderPoint outerBorderPoint) {
		BorderPoint structureBorderPoint = outerBorderPoint.getClosestBorderPoint();
		int xDiff = structureBorderPoint.getX() - outerBorderPoint.getX();
		int zDiff = structureBorderPoint.getZ() - outerBorderPoint.getZ();

		int totalSteps = Math.max(Math.abs(xDiff), Math.abs(zDiff));
		float incrementX = ((float) xDiff) / totalSteps;
		float incrementZ = ((float) zDiff) / totalSteps;

		Set<BorderPoint> points = new HashSet<>();

		for (int step = 1; step < totalSteps; step++) {
			borderMatrix.getPoint(outerBorderPoint.getX() + (int) (step * incrementX), outerBorderPoint.getZ() + (int) (step * incrementZ)).ifPresent(smoothPoint -> {
				if (!smoothPoint.hasOuterBorderPointSet()) {
					points.add(smoothPoint);
				}
			});
		}
		return points;
	}
}
