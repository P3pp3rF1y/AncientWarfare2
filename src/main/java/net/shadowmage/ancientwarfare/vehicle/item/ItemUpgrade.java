package net.shadowmage.ancientwarfare.vehicle.item;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class ItemUpgrade extends ItemBaseVehicle {
	private String tooltipName;

	public ItemUpgrade(ResourceLocation registryName) {
		super(registryName.getResourcePath());
		setUnlocalizedName("item." + registryName.getResourcePath() + ".name");
		tooltipName = "item." + registryName.getResourcePath() + ".tooltip";
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		tooltip.add(I18n.format(tooltipName));
	}
}
