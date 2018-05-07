package net.shadowmage.ancientwarfare.automation.tile.worksite.fruitfarm;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.util.parsing.BlockStateMatcher;
import net.shadowmage.ancientwarfare.core.util.parsing.PropertyState;
import net.shadowmage.ancientwarfare.core.util.parsing.PropertyStateMatcher;

public class FruitPickedDrop extends FruitPicked {
	private ItemStack drop;

	public FruitPickedDrop(BlockStateMatcher stateMatcher, PropertyStateMatcher ripeStateMatcher, PropertyState newState, ItemStack drop) {
		super(stateMatcher, ripeStateMatcher, newState);
		this.drop = drop;
	}

	@Override
	protected NonNullList<ItemStack> getDrops(World world, IBlockState state, BlockPos pos, int fortune) {
		return NonNullList.from(ItemStack.EMPTY, drop.copy());
	}
}
