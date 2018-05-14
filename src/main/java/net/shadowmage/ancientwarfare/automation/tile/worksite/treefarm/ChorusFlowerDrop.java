package net.shadowmage.ancientwarfare.automation.tile.worksite.treefarm;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class ChorusFlowerDrop implements IBlockExtraDrop {

	@Override
	public boolean matches(IBlockState state) {
		return state.getBlock() == Blocks.CHORUS_FLOWER;
	}

	@Override
	public NonNullList<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		return NonNullList.from(ItemStack.EMPTY, new ItemStack(Item.getItemFromBlock(Blocks.CHORUS_FLOWER)));
	}
}
