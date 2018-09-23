package net.shadowmage.ancientwarfare.npc;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.core.util.SortItemsFirstComparator;
import net.shadowmage.ancientwarfare.npc.init.AWNPCItems;
import net.shadowmage.ancientwarfare.npc.item.ItemCommandBaton;
import net.shadowmage.ancientwarfare.npc.item.ItemOrders;

import java.util.Comparator;

import static net.shadowmage.ancientwarfare.npc.init.AWNPCBlocks.TOWN_HALL;
import static net.shadowmage.ancientwarfare.npc.init.AWNPCItems.BARD_INSTRUMENT;
import static net.shadowmage.ancientwarfare.npc.init.AWNPCItems.NPC_SPAWNER;

public class AWNPCTab extends CreativeTabs {
	public AWNPCTab() {
		super("tabs.npc");
	}

	@Override
	public ItemStack getTabIconItem() {
		return new ItemStack(AWNPCItems.NPC_SPAWNER);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void displayAllRelevantItems(NonNullList<ItemStack> items) {
		super.displayAllRelevantItems(items);
		items.sort(getComparator());
	}

	private Comparator<ItemStack> comparator = null;

	private Comparator<ItemStack> getComparator() {
		if (comparator == null) {
			comparator = new SortItemsFirstComparator(
					TOWN_HALL,
					ItemOrders.class,
					ItemCommandBaton.class,
					BARD_INSTRUMENT,
					NPC_SPAWNER
			);
		}

		return comparator;
	}
}
