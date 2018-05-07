package net.shadowmage.ancientwarfare.automation.tile.worksite.fruitfarm;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;
import net.shadowmage.ancientwarfare.core.util.parsing.BlockStateMatcher;
import net.shadowmage.ancientwarfare.core.util.parsing.PropertyState;
import net.shadowmage.ancientwarfare.core.util.parsing.PropertyStateMatcher;

public class FruitPickedRemoveOne extends FruitPicked {
	public FruitPickedRemoveOne(BlockStateMatcher stateMatcher, PropertyStateMatcher ripeStateMatcher, PropertyState newState) {
		super(stateMatcher, ripeStateMatcher, newState);
	}

	@Override
	protected void putInInventory(World world, BlockPos pos, IItemHandler inventory, NonNullList<ItemStack> drops) {
		InventoryTools.removeItem(drops, s -> InventoryTools.doItemStacksMatchRelaxed(s, drops.get(0)), 1);

		InventoryTools.insertOrDropItems(inventory, drops, world, pos);
	}
}
