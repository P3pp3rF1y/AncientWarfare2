package net.shadowmage.ancientwarfare.automation.tile.worksite.fruitfarm;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;
import net.shadowmage.ancientwarfare.core.util.parsing.BlockStateMatcher;
import net.shadowmage.ancientwarfare.core.util.parsing.PropertyState;
import net.shadowmage.ancientwarfare.core.util.parsing.PropertyStateMatcher;

public class FruitPicked implements IFruit {
	private BlockStateMatcher stateMatcher;
	private PropertyStateMatcher ripeStateMatcher;
	private PropertyState newState;

	public FruitPicked(BlockStateMatcher stateMatcher, PropertyStateMatcher ripeStateMatcher, PropertyState newState) {
		this.stateMatcher = stateMatcher;
		this.ripeStateMatcher = ripeStateMatcher;
		this.newState = newState;
	}

	@Override
	public boolean matches(IBlockState state) {
		return stateMatcher.test(state);
	}

	@Override
	public boolean isRipe(IBlockState state) {
		return ripeStateMatcher.test(state);
	}

	@Override
	public boolean pick(World world, IBlockState state, BlockPos pos, int fortune, IItemHandler inventory) {
		NonNullList<ItemStack> drops = getDrops(world, state, pos, fortune);

		if (drops.isEmpty() || !InventoryTools.canInventoryHold(inventory, drops)) {
			return false;
		}

		world.setBlockState(pos, newState.update(state));

		putInInventory(world, pos, inventory, drops);

		return true;
	}

	protected NonNullList<ItemStack> getDrops(World world, IBlockState state, BlockPos pos, int fortune) {
		//using deprecated getDrops here just because of pam's harvestcraft, change to proper one in the future
		return InventoryTools.toNonNullList(state.getBlock().getDrops(world, pos, state, fortune));
	}

	protected void putInInventory(World world, BlockPos pos, IItemHandler inventory, NonNullList<ItemStack> drops) {
		InventoryTools.insertOrDropItems(inventory, drops, world, pos);
	}

	@Override
	public boolean isPlantable() {
		return false;
	}
}
