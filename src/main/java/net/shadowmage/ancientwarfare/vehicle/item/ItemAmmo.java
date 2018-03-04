package net.shadowmage.ancientwarfare.vehicle.item;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class ItemAmmo extends ItemBaseVehicle {
	String tooltipName;

	public ItemAmmo(ResourceLocation registryName) {
		super(registryName.getResourcePath());

		setUnlocalizedName("ammo." + registryName.getResourcePath());
		tooltipName = "ammo." + registryName.getResourcePath() + ".tooltip";
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flagIn) {
		tooltip.add(I18n.format(tooltipName));
	}
}
