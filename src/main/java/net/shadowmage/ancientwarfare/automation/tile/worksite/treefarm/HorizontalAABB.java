package net.shadowmage.ancientwarfare.automation.tile.worksite.treefarm;

import net.minecraft.util.math.BlockPos;

public class HorizontalAABB {
	private int minX;
	private int minZ;
	private int maxX;
	private int maxZ;

	public HorizontalAABB(BlockPos pos) {
		minX = pos.getX();
		maxX = pos.getX();
		minZ = pos.getZ();
		maxZ = pos.getZ();
	}

	public void include(BlockPos pos) {
		this.minX = Math.min(this.minX, pos.getX());
		this.minZ = Math.min(this.minZ, pos.getZ());
		this.maxX = Math.max(this.maxX, pos.getX());
		this.maxZ = Math.max(this.maxZ, pos.getZ());
	}

	public int distanceTo(BlockPos pos) {
		int xDistance = pos.getX() > minX && pos.getX() < maxX ? 0 : Math.min(Math.abs(pos.getX() - minX), Math.abs(pos.getX() - maxX));
		int zDistance = pos.getZ() > minZ && pos.getZ() < maxZ ? 0 : Math.min(Math.abs(pos.getZ() - minZ), Math.abs(pos.getZ() - maxZ));

		return Math.max(xDistance, zDistance);
	}
}
