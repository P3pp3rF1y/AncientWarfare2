package net.shadowmage.ancientwarfare.structure.util;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;

public class RespawnData implements IRespawnData {
	private BlockPos respawnPos = BlockPos.ORIGIN;
	private NBTTagCompound spawnerSettings = new NBTTagCompound();
	private boolean canRespawn = false;
	private long spawnTime = 0;

	@Override
	public boolean canRespawn() {
		return canRespawn;
	}

	@Override
	public BlockPos getRespawnPos() {
		return respawnPos;
	}

	@Override
	public NBTTagCompound getSpawnerSettings() {
		return spawnerSettings;
	}

	@Override
	public long getSpawnTime() {
		return spawnTime;
	}

	@Override
	public void setRespawnPos(BlockPos pos) {
		respawnPos = pos;
		canRespawn = !respawnPos.equals(BlockPos.ORIGIN);
	}

	@Override
	public void setSpawnerSettings(NBTTagCompound tag) {
		spawnerSettings = tag;
	}

	@Override
	public void setSpawnTime(long time) {
		spawnTime = time;
	}
}
