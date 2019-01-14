package net.shadowmage.ancientwarfare.structure.util;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class RespawnDataCapabilityProvider implements ICapabilitySerializable<NBTTagCompound> {
	private IRespawnData respawnData = CapabilityRespawnData.RESPAWN_DATA_CAPABILITY.getDefaultInstance();

	@Override
	public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
		return capability == CapabilityRespawnData.RESPAWN_DATA_CAPABILITY;
	}

	@Nullable
	@Override
	public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
		return capability == CapabilityRespawnData.RESPAWN_DATA_CAPABILITY ? CapabilityRespawnData.RESPAWN_DATA_CAPABILITY.cast(respawnData) : null;
	}

	@Override
	public NBTTagCompound serializeNBT() {
		return (NBTTagCompound) CapabilityRespawnData.RESPAWN_DATA_CAPABILITY.writeNBT(respawnData, null);
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		CapabilityRespawnData.RESPAWN_DATA_CAPABILITY.readNBT(respawnData, null, nbt);
	}
}
