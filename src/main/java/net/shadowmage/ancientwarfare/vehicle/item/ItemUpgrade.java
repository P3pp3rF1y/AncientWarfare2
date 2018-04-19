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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemUpgrade extends ItemBaseVehicle {
	private String tooltipName;

	public ItemUpgrade(ResourceLocation registryName) {
		super(registryName.getResourcePath());
		tooltipName = "item." + registryName.getResourcePath() + ".tooltip";
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		tooltip.add(I18n.format(tooltipName));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerClient() {
		final Map<String, ModelResourceLocation> modelLocations = new HashMap<>();

		ModelLoaderHelper.registerItem(this, (i, m) -> new ModelResourceLocation(new ResourceLocation(AncientWarfareCore.modID, "vehicle/upgrade"), "variant=" + getRegistryName().getResourcePath()));
	}
}
