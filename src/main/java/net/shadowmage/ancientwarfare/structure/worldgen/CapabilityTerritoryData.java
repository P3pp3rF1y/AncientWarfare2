package net.shadowmage.ancientwarfare.structure.worldgen;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.shadowmage.ancientwarfare.core.util.InjectionTools;
import net.shadowmage.ancientwarfare.structure.AncientWarfareStructure;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CapabilityTerritoryData {
	private CapabilityTerritoryData() {}

	@CapabilityInject(ITerritoryData.class)
	public static Capability<ITerritoryData> TERRITORY_DATA = InjectionTools.nullValue();

	public static void register() {
		CapabilityManager.INSTANCE.register(ITerritoryData.class, new Capability.IStorage<ITerritoryData>() {
					@Override
					public NBTBase writeNBT(Capability<ITerritoryData> capability, ITerritoryData instance, EnumFacing side) {
						return instance.serializeNBT();
					}

					@Override
					public void readNBT(Capability<ITerritoryData> capability, ITerritoryData instance, EnumFacing side, NBTBase nbt) {
						if (!(instance instanceof TerritoryData))
							throw new IllegalArgumentException("Can not deserialize to an instance that isn't the default implementation");
						instance.deserializeNBT((NBTTagCompound) nbt);
					}
				},
				TerritoryData::new);
	}

	public static void onAttach(AttachCapabilitiesEvent<World> event) {
		event.addCapability(new ResourceLocation(AncientWarfareStructure.MOD_ID, "territory_data"), new TerritoryDataCapabilityProvider());
	}

	public static class TerritoryDataCapabilityProvider implements ICapabilitySerializable<NBTTagCompound> {
		private ITerritoryData territoryData = CapabilityTerritoryData.TERRITORY_DATA.getDefaultInstance();

		@Override
		public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
			return capability == CapabilityTerritoryData.TERRITORY_DATA;
		}

		@Nullable
		@Override
		public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
			return capability == CapabilityTerritoryData.TERRITORY_DATA ? CapabilityTerritoryData.TERRITORY_DATA.cast(territoryData) : null;
		}

		@Override
		public NBTTagCompound serializeNBT() {
			//noinspection ConstantConditions
			return (NBTTagCompound) CapabilityTerritoryData.TERRITORY_DATA.writeNBT(territoryData, null);
		}

		@Override
		public void deserializeNBT(NBTTagCompound nbt) {
			CapabilityTerritoryData.TERRITORY_DATA.readNBT(territoryData, null, nbt);
		}
	}
}