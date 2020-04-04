package net.shadowmage.ancientwarfare.automation;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.automation.block.BlockChunkLoaderSimple;
import net.shadowmage.ancientwarfare.automation.block.BlockFlywheelStorage;
import net.shadowmage.ancientwarfare.automation.block.BlockTorqueBase;
import net.shadowmage.ancientwarfare.automation.block.BlockWarehouseStorage;
import net.shadowmage.ancientwarfare.core.init.AWCoreItems;
import net.shadowmage.ancientwarfare.core.util.SortItemsFirstComparator;

import java.util.Comparator;

import static net.shadowmage.ancientwarfare.automation.init.AWAutomationBlocks.*;

public class AWAutomationTab extends CreativeTabs {
	public AWAutomationTab() {
		super("tabs.automation");
	}

	@Override
	public ItemStack getTabIconItem() {
		return new ItemStack(AWCoreItems.IRON_HAMMER);
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
					TREE_FARM,
					CROP_FARM,
					FRUIT_FARM,
					ANIMAL_FARM,
					FISH_FARM,
					QUARRY,
					AUTO_CRAFTING,
					WAREHOUSE_CONTROL,
					WAREHOUSE_INTERFACE, WAREHOUSE_CRAFTING,
					BlockWarehouseStorage.class,
					WAREHOUSE_STOCK_VIEWER,
					WAREHOUSE_STOCK_LINKER,
					MAILBOX,
					BlockTorqueBase.class,
					BlockFlywheelStorage.class,
					WINDMILL_BLADE,
					BlockChunkLoaderSimple.class
			);
		}

		return comparator;
	}
}
