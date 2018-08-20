package net.shadowmage.ancientwarfare.automation;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.shadowmage.ancientwarfare.core.init.AWCoreItems;

public class AWAutomationTab extends CreativeTabs {
	public AWAutomationTab() {
		super("tabs.automation");
	}

	@Override
	public ItemStack getTabIconItem() {
		return new ItemStack(AWCoreItems.IRON_HAMMER);
	}
}
