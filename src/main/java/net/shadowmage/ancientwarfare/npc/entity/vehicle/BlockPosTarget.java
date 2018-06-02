package net.shadowmage.ancientwarfare.npc.entity.vehicle;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockPosTarget implements ITarget {
	private BlockPos pos;

	BlockPosTarget(BlockPos pos) {
		this.pos = pos;
	}

	@Override
	public double getX() {
		return pos.getX() + 0.5D;
	}

	@Override
	public double getY() {
		return pos.getY() + 0.5D;
	}

	@Override
	public double getZ() {
		return pos.getZ() + 0.5D;
	}

	@Override
	public boolean exists(World world) {
		return !world.isAirBlock(pos);
	}

	public NBTTagCompound serializeToNBT(NBTTagCompound tag) {
		tag.setLong("targetPos", pos.toLong());
		return tag;
	}
}
