package net.shadowmage.ancientwarfare.vehicle.item;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.core.util.ModelLoaderHelper;

import javax.annotation.Nullable;
import java.util.List;

public class ItemUpgrade extends ItemBaseVehicle {
	private String tooltipName;
	private String vehicleUpgradeTooltipName;

	public ItemUpgrade(ResourceLocation registryName) {
		super(registryName.getResourcePath());
		tooltipName = "item." + registryName.getResourcePath() + ".tooltip";
		vehicleUpgradeTooltipName = "item.vehicle_upgrade_tooltip";
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		tooltip.add(I18n.format(tooltipName));
		tooltip.add(I18n.format(vehicleUpgradeTooltipName));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerClient() {
		ModelLoaderHelper.registerItem(this, (i, m) -> new ModelResourceLocation(new ResourceLocation(AncientWarfareCore.MOD_ID, "vehicle/upgrade"), "variant=" + getRegistryName().getResourcePath()));
	}
}
