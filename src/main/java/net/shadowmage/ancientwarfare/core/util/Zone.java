package net.shadowmage.ancientwarfare.core.util;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.INBTSerializable;

public class Zone implements INBTSerializable<NBTTagCompound> {

	public BlockPos min, max;

	public Zone(BlockPos p1, BlockPos p2) {
		min = BlockTools.getMin(p1, p2);
		max = BlockTools.getMax(p1, p2);
	}

	public Zone() {

	}

	/*
	 * does the input share any block position with this zone ?
	 */
	public boolean crossWith(Zone z) {
		if (max.getX() < z.min.getX() || max.getY() < z.min.getY() || max.getZ() < z.min.getZ() || min.getX() > z.max.getX() || min.getY() > z.max.getY() || min
				.getZ() > z.max.getZ()) {
			return false;
		}
		return true;
	}

	public boolean isPositionIn(int x, int y, int z) {
		if (x < min.getX() || y < min.getY() || z < min.getZ() || x > max.getX() || z > max.getZ() || y > max.getY()) {
			return false;
		}
		return true;
	}

	public boolean isPositionIn(BlockPos pos) {
		return isPositionIn(pos.getX(), pos.getY(), pos.getZ());
	}

	public boolean equals(BlockPos min, BlockPos max) {
		return min.equals(this.min) && max.equals(this.max);
	}

	@Override
	public boolean equals(Object object) {
		return object instanceof Zone && this.equals(((Zone) object).min, ((Zone) object).max);
	}

	@Override
	public int hashCode() {
		return 31 * min.hashCode() + max.hashCode();
	}

	@Override
	public String toString() {
		return String.format("From %s to %s", min, max);
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setLong("min", min.toLong());
		tag.setLong("max", max.toLong());
		return tag;
	}

	@Override
	public void deserializeNBT(NBTTagCompound tag) {
		min = BlockPos.fromLong(tag.getLong("min"));
		max = BlockPos.fromLong(tag.getLong("max"));
	}
}
