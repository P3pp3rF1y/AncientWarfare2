package net.shadowmage.ancientwarfare.npc;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.shadowmage.ancientwarfare.npc.init.AWNPCItems;

public class AWNPCTab extends CreativeTabs {
	public AWNPCTab() {
		super("tabs.npc");
	}

	@Override
	public ItemStack getTabIconItem() {
		return new ItemStack(AWNPCItems.NPC_SPAWNER);
	}
}
