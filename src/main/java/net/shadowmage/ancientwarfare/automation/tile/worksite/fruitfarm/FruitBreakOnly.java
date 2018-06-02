package net.shadowmage.ancientwarfare.automation.tile.worksite.fruitfarm;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;
import net.shadowmage.ancientwarfare.core.util.parsing.BlockStateMatcher;

public class FruitBreakOnly implements IFruit {
	private BlockStateMatcher stateMatcher;

	public FruitBreakOnly(BlockStateMatcher stateMatcher) {
		this.stateMatcher = stateMatcher;
	}

	@Override
	public boolean matches(IBlockState state) {
		return stateMatcher.test(state);
	}

	@Override
	public boolean isRipe(IBlockState state) {
		return true;
	}

	@Override
	public boolean pick(World world, IBlockState state, BlockPos pos, int fortune, IItemHandler inventory) {
		NonNullList<ItemStack> drops = NonNullList.create();
		state.getBlock().getDrops(drops, world, pos, state, fortune);

		if (drops.isEmpty() || !InventoryTools.canInventoryHold(inventory, drops)) {
			return false;
		}

		BlockTools.breakBlock(world, pos, fortune, false);

		InventoryTools.insertOrDropItems(inventory, drops, world, pos);

		return true;
	}

	@Override
	public boolean isPlantable() {
		return false;
	}
}
