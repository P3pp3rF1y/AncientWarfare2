package net.shadowmage.ancientwarfare.structure.template.build.validation.border.points;

public class BorderPoint {
	private PointType type;
	private int x;
	private int z;
	private BorderPoint outerBorderPoint = null;
	private BorderPoint referencePoint;
	private BorderPoint closestBorderPoint;
	private double distanceToBorder = Double.MAX_VALUE;
	private int structureBorderDistance = Integer.MAX_VALUE;

	public BorderPoint(int x, int z, PointType type) {
		this.x = x;
		this.z = z;
		this.type = type;
	}

	public PointType getType() {
		return type;
	}

	public int getX() {
		return x;
	}

	public int getZ() {
		return z;
	}

	public void setOuterBorderAndReferencePoint(BorderPoint outerBorder, BorderPoint referencePoint) {
		this.outerBorderPoint = outerBorder;
		this.referencePoint = referencePoint;
	}

	public boolean hasOuterBorderPointSet() {
		return outerBorderPoint != null;
	}

	public int getStructureBorderDistance() {
		return structureBorderDistance;
	}

	public BorderPoint getClosestBorderPoint() {
		return closestBorderPoint;
	}

	public double getDistanceToBorder() {
		return distanceToBorder;
	}

	public boolean updateBorderCoordsIfCloser(int distance, BorderPoint point) {
		if (distance < structureBorderDistance) {
			structureBorderDistance = distance;
			closestBorderPoint = point;
			int xDiff = Math.abs(x - closestBorderPoint.x);
			int zDiff = Math.abs(z - closestBorderPoint.z);
			distanceToBorder = Math.sqrt(((double) xDiff * xDiff) + zDiff * zDiff);
			return true;
		}
		return false;
	}

	public BorderPoint getOuterBorderPoint() {
		return outerBorderPoint;
	}

	public BorderPoint getReferencePoint() {
		return referencePoint;
	}
}
