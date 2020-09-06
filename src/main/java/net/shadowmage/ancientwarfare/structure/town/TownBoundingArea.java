package net.shadowmage.ancientwarfare.structure.town;

public class TownBoundingArea {

	private static final int borderSize = 8;//MUST be >0 or will cause weirdness when doing...everything...

	int chunkMinX;
	int chunkMaxX;
	int chunkMinZ;
	int chunkMaxZ;
	int minY;
	int maxY;

	int wallSize = 3;//should be >0 if walls are desired (must be set by generator prior to generating, not used in validation)
	int exteriorSize = 0;//exterior buffer size, in chunks.  used to inset the walls by this amount, to allow generation of slums/farms on the exterior of the towns

	public TownBoundingArea() {}

	public TownBoundingArea(int chunkMinX, int chunkMinZ, int chunkMaxX, int chunkMaxZ, int minY, int maxY) {
		this.chunkMinX = chunkMinX;
		this.chunkMinZ = chunkMinZ;
		this.chunkMaxX = chunkMaxX;
		this.chunkMaxZ = chunkMaxZ;
		this.minY = minY;
		this.maxY = maxY;
	}

	public int getChunkWidth() {
		return (chunkMaxX - chunkMinX) + 1;
	}

	public int getChunkLength() {
		return (chunkMaxZ - chunkMinZ) + 1;
	}

	public int getChunkMinX() {
		return chunkMinX;
	}

	public int getChunkMaxX() {
		return chunkMaxX;
	}

	public int getChunkMinZ() {
		return chunkMinZ;
	}

	public int getChunkMaxZ() {
		return chunkMaxZ;
	}

	public int getBlockMinX() {
		return chunkMinX * 16;
	}

	public int getBlockMaxX() {
		return chunkMaxX * 16 + 15;
	}

	public int getBlockMinZ() {
		return chunkMinZ * 16;
	}

	public int getBlockMaxZ() {
		return chunkMaxZ * 16 + 15;
	}

	public int getBlockWidth() {
		return getBlockMaxX() - getBlockMinX() + 1;
	}

	public int getBlockLength() {
		return getBlockMaxZ() - getBlockMinZ() + 1;
	}

	public int getExteriorMinX() {
		return getBlockMinX() - 1 + borderSize;
	}

	public int getExteriorMaxX() {
		return getBlockMaxX() - 1 - borderSize;
	}

	public int getExteriorMinZ() {
		return getBlockMinZ() - 1 + borderSize;
	}

	public int getExteriorMaxZ() {
		return getBlockMaxZ() - 1 - borderSize;
	}

	public int getWallMinX() {
		return getExteriorMinX() + exteriorSize * 16;
	}

	public int getWallMaxX() {
		return getExteriorMaxX() - exteriorSize * 16;
	}

	public int getWallMinZ() {
		return getExteriorMinZ() + exteriorSize * 16;
	}

	public int getWallMaxZ() {
		return getExteriorMaxZ() - exteriorSize * 16;
	}

	public int getTownMinX() {
		return getWallMinX() + wallSize;
	}

	public int getTownMaxX() {
		return getWallMaxX() - wallSize;
	}

	public int getTownMinZ() {
		return getWallMinZ() + wallSize;
	}

	public int getTownMaxZ() {
		return getWallMaxZ() - wallSize;
	}

	public int getMinY() {
		return minY;
	}

	public int getMaxY() {
		return maxY;
	}

	public int getSurfaceY() {
		return minY + 3;
	}

	public int getCenterX() {
		return (getBlockMinX() + getBlockMaxX()) / 2;
	}

	public int getCenterZ() {
		return (getBlockMinZ() + getBlockMaxZ()) / 2;
	}

	public int getTownWidth() {
		return (getTownMaxX() - getTownMinX()) + 1;
	}

	public int getTownLength() {
		return (getTownMaxZ() - getTownMinZ()) + 1;
	}

	@Override
	public String toString() {
		int minX = getBlockMinX();
		int maxX = getBlockMaxX();
		int minZ = getBlockMinZ();
		int maxZ = getBlockMaxZ();
		return "TownArea: " + minX + "  :" + minZ + " :: " + maxX + " : " + maxZ + " size: " + getBlockWidth() + " : " + getBlockLength();
	}

	public void setSurfaceY(int surfaceY) {
		int diff = surfaceY - getSurfaceY();
		minY += diff;
		maxY += diff;
	}
}
