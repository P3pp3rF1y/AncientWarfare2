package net.shadowmage.ancientwarfare.vehicle.item;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.vehicle.armors.IVehicleArmor;
import net.shadowmage.ancientwarfare.vehicle.registry.ArmorRegistry;

import javax.annotation.Nullable;
import java.util.List;

public class ItemArmor extends ItemBaseVehicle {
	public ItemArmor() {
		super("armor");
		setHasSubtypes(true);
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		if (isInCreativeTab(tab)) {
			for (IVehicleArmor armor : ArmorRegistry.getArmorTypes()) {
				items.add(armor.getArmorType().ordinal(), armor.getArmorStack(1));
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getItemStackDisplayName(ItemStack stack) {
		return I18n.format(ArmorRegistry.getArmorForStack(stack).getDisplayName());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		tooltip.add(I18n.format(ArmorRegistry.getArmorForStack(stack).getDisplayTooltip()));
	}
}
