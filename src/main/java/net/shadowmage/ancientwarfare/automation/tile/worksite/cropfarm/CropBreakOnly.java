package net.shadowmage.ancientwarfare.automation.tile.worksite.cropfarm;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;
import net.shadowmage.ancientwarfare.core.util.parsing.BlockStateMatcher;
import net.shadowmage.ancientwarfare.core.util.parsing.ItemStackMatcher;

import java.util.Collections;
import java.util.List;

public class CropBreakOnly implements ICrop {

	private BlockStateMatcher stateMatcher;
	private ItemStackMatcher stackMatcher;

	CropBreakOnly() {
	}

	public CropBreakOnly(BlockStateMatcher stateMatcher, ItemStackMatcher stackMatcher) {
		this.stateMatcher = stateMatcher;
		this.stackMatcher = stackMatcher;
	}

	@Override
	public List<BlockPos> getPositionsToHarvest(World world, BlockPos pos, IBlockState state) {
		return Collections.singletonList(pos);
	}

	@Override
	public boolean canBeFertilized(IBlockState state, World world, BlockPos pos) {
		return false;
	}

	@Override
	public boolean harvest(World world, IBlockState state, BlockPos pos, int fortune, IItemHandler inventory) {
		Block block = state.getBlock();
		NonNullList<ItemStack> stacks = NonNullList.create();

		block.getDrops(stacks, world, pos, state, fortune);

		if (!InventoryTools.canInventoryHold(inventory, stacks)) {
			return false;
		}

		if (!BlockTools.breakBlockNoDrops(world, pos, state)) {
			return false;
		}

		InventoryTools.insertOrDropItems(inventory, stacks, world, pos);
		return true;
	}

	@Override
	public boolean matches(IBlockState state) {
		return stateMatcher.test(state);
	}

	@Override
	public boolean matches(ItemStack stack) {
		return stackMatcher.test(stack);
	}

	@Override
	public boolean isPlantable(ItemStack stack) {
		return false;
	}
}
