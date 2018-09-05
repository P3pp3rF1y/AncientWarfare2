package net.shadowmage.ancientwarfare.core;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.shadowmage.ancientwarfare.core.init.AWCoreItems;

public class AWCoreTab extends CreativeTabs {
	public AWCoreTab() {
		super("tabs.awcore");
	}

	@Override
	public ItemStack getTabIconItem() {
		return new ItemStack(AWCoreItems.RESEARCH_BOOK);
	}
}
