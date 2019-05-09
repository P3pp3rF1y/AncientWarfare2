package net.shadowmage.ancientwarfare.structure.template.build.validation.border;

import net.shadowmage.ancientwarfare.structure.template.build.validation.border.points.BorderPoint;
import net.shadowmage.ancientwarfare.structure.template.build.validation.border.points.PointType;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class BorderMatrix {
	private BorderPoint[][] borderPoints;
	private final int fullXSize;
	private final int fullZSize;
	private Map<PointType, Set<BorderPoint>> typePoints = new HashMap<>();

	private BorderMatrix(int fullXSize, int fullZSize) {
		this.fullXSize = fullXSize;

		this.fullZSize = fullZSize;
	}

	public BorderMatrix(int xSize, int zSize, int borderSize) {
		this(xSize + 2 * borderSize + 2 * 2, zSize + 2 * borderSize + 2 * 2);
		borderPoints = initMatrix(fullXSize, fullZSize);
	}

	public Optional<BorderPoint> getPoint(int x, int z) {
		if (borderPoints.length == 0 || x < 0 || x >= borderPoints.length || z < 0 || z >= borderPoints[0].length) {
			return Optional.empty();
		}

		return Optional.ofNullable(borderPoints[x][z]);
	}

	Optional<BorderPoint> getPoint(HorizontalCoords coords) {
		return getPoint(coords.getX(), coords.getZ());
	}

	private BorderPoint[][] initMatrix(int fullXSize, int fullZSize) {
		BorderPoint[][] ret = new BorderPoint[fullXSize][];

		for (int x = 0; x < fullXSize; x++) {
			ret[x] = new BorderPoint[fullZSize];
		}

		return ret;
	}

	public boolean isEmpty(HorizontalCoords point) {
		return !getPoint(point).isPresent();
	}

	public BorderPoint addPoint(int x, int z, PointType type) {
		BorderPoint point = new BorderPoint(x, z, type);
		addPoint(point);
		return point;
	}

	private void addTypePoint(PointType type, BorderPoint point) {
		if (!typePoints.containsKey(type)) {
			typePoints.put(type, new HashSet<>());
		}
		typePoints.get(type).add(point);
	}

	public void addPoint(HorizontalCoords coords, PointType type) {
		addPoint(coords.getX(), coords.getZ(), type);
	}

	public int getFullXSize() {
		return fullXSize;
	}

	public int getFullZSize() {
		return fullZSize;
	}

	public Set<BorderPoint> getPointsOfType(PointType type) {
		return typePoints.get(type);
	}

	public void addPoint(BorderPoint point) {
		borderPoints[point.getX()][point.getZ()] = point;
		addTypePoint(point.getType(), point);
	}
}
