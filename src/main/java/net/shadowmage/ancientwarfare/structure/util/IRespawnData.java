package net.shadowmage.ancientwarfare.structure.util;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;

public interface IRespawnData {
	boolean canRespawn();
	BlockPos getRespawnPos();
	NBTTagCompound getSpawnerSettings();
	void setRespawnPos(BlockPos pos);
	void setSpawnerSettings(NBTTagCompound tag);
}
