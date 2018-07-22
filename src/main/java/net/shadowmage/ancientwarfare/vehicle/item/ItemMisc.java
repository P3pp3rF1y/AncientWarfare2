package net.shadowmage.ancientwarfare.vehicle.item;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.core.util.ModelLoaderHelper;

public class ItemMisc extends ItemBaseVehicle {
	public ItemMisc(String regName) {
		super(regName);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerClient() {
		ModelLoaderHelper.registerItem(this, (i, m) -> new ModelResourceLocation(new ResourceLocation(AncientWarfareCore.MOD_ID, "vehicle/misc"), "variant=" + getRegistryName().getResourcePath()));
	}
}
