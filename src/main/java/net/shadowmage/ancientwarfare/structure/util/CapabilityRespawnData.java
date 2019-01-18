package net.shadowmage.ancientwarfare.structure.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.shadowmage.ancientwarfare.structure.AncientWarfareStructure;

public class CapabilityRespawnData {
	private CapabilityRespawnData() {}

	private static final String RESPAWN_POS_TAG = "respawnPos";
	private static final String SPAWNER_SETTINGS_TAG = "spawnerSettings";
	private static final String SPAWN_TIME_TAG = "spawnTime";

	@CapabilityInject(IRespawnData.class)
	@SuppressWarnings({"squid:S1444", "squid:S3008"})
	public static Capability<IRespawnData> RESPAWN_DATA_CAPABILITY = null;

	public static void register() {
		CapabilityManager.INSTANCE.register(IRespawnData.class, new Capability.IStorage<IRespawnData>() {
			@Override
			public NBTBase writeNBT(Capability<IRespawnData> capability, IRespawnData instance, EnumFacing side) {
				NBTTagCompound tag = new NBTTagCompound();
				if (instance.canRespawn()) {
					tag.setLong(RESPAWN_POS_TAG, instance.getRespawnPos().toLong());
					tag.setTag(SPAWNER_SETTINGS_TAG, instance.getSpawnerSettings());
					tag.setLong(SPAWN_TIME_TAG, instance.getSpawnTime());
				}

				return tag;
			}

			@Override
			public void readNBT(Capability<IRespawnData> capability, IRespawnData instance, EnumFacing side, NBTBase nbt) {
				if (nbt instanceof NBTTagCompound) {
					NBTTagCompound tag = (NBTTagCompound) nbt;
					if (tag.hasKey(RESPAWN_POS_TAG)) {
						instance.setRespawnPos(BlockPos.fromLong(tag.getLong(RESPAWN_POS_TAG)));
						instance.setSpawnerSettings(tag.getCompoundTag(SPAWNER_SETTINGS_TAG));
						instance.setSpawnTime(tag.getLong(SPAWN_TIME_TAG));
					}
				}
			}
		}, RespawnData::new);
	}

	public static void onAttach(AttachCapabilitiesEvent<Entity> event) {
		Entity entity = event.getObject();
		if (entity instanceof EntityLivingBase) {
			event.addCapability(new ResourceLocation(AncientWarfareStructure.MOD_ID, "respawn_data"),
					new RespawnDataCapabilityProvider());
		}
	}
}
