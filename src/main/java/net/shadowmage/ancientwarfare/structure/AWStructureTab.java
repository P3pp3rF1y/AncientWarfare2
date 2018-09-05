package net.shadowmage.ancientwarfare.structure;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.shadowmage.ancientwarfare.structure.init.AWStructureItems;

public class AWStructureTab extends CreativeTabs {
	public AWStructureTab() {
		super("tabs.structures");
	}

	@Override
	public ItemStack getTabIconItem() {
		return new ItemStack(AWStructureItems.STRUCTURE_SCANNER);
	}
}
