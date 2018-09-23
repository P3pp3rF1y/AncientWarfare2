package net.shadowmage.ancientwarfare.vehicle;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.shadowmage.ancientwarfare.vehicle.init.AWVehicleItems;

public class AWVehicleTab extends CreativeTabs {
	public AWVehicleTab() {
		super("tabs.vehicles");
	}

	@Override
	public ItemStack getTabIconItem() {
		return new ItemStack(AWVehicleItems.SPAWNER);
	}
}
