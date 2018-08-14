package net.shadowmage.ancientwarfare.automation.tile.worksite.fruitfarm;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCocoa;
import net.minecraft.block.BlockOldLog;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;
import net.shadowmage.ancientwarfare.core.util.parsing.BlockStateMatcher;
import net.shadowmage.ancientwarfare.core.util.parsing.ItemStackMatcher;

import java.util.Optional;
import java.util.stream.Stream;

public class FruitCocoa implements IFruit {
	private BlockStateMatcher stateMatcher;
	private ItemStackMatcher stackMatcher;

	public FruitCocoa() {
		this.stateMatcher = new BlockStateMatcher(Blocks.COCOA);
		this.stackMatcher = new ItemStackMatcher.Builder(Items.DYE).setMeta(EnumDyeColor.BROWN.getDyeDamage()).build();
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
	public boolean canPlant(World world, BlockPos pos, IBlockState state) {
		return world.isAirBlock(pos) && Stream.of(EnumFacing.HORIZONTALS).anyMatch(h -> isJungleLog(world.getBlockState(pos.offset(h))));
	}

	private boolean isJungleLog(IBlockState state) {
		return state.getBlock() == Blocks.LOG && state.getValue(BlockOldLog.VARIANT) == BlockPlanks.EnumType.JUNGLE;
	}

	@Override
	public boolean plant(World world, BlockPos plantPos) {
		if (!world.isAirBlock(plantPos)) {
			return false;
		}

		Optional<EnumFacing> facing = Stream.of(EnumFacing.HORIZONTALS).filter(h -> isJungleLog(world.getBlockState(plantPos.offset(h)))).findFirst();

		return facing.isPresent() && plantBean(world, plantPos, facing.get());
	}

	private boolean plantBean(World world, BlockPos pos, EnumFacing facing) {
		return world.setBlockState(pos, Blocks.COCOA.getDefaultState().withProperty(BlockCocoa.FACING, facing).withProperty(BlockCocoa.AGE, 0));
	}

	@Override
	public boolean isRipe(IBlockState state) {
		return state.getValue(BlockCocoa.AGE) == 2;
	}

	@Override
	public boolean pick(World world, IBlockState state, BlockPos pos, int fortune, IItemHandler inventory) {
		NonNullList<ItemStack> drops = NonNullList.create();
		Blocks.COCOA.getDrops(drops, world, pos, state, fortune);

		if (!InventoryTools.canInventoryHold(inventory, drops)) {
			return false;
		}

		IBlockState newState = state.withProperty(BlockCocoa.AGE, 0);

		world.setBlockState(pos, newState);
		world.playEvent(2001, pos, Block.getStateId(newState));

		//remove that one cocoa bean that was just "replanted"
		InventoryTools.removeItem(drops, s -> s.getItem() == Items.DYE && EnumDyeColor.byDyeDamage(s.getMetadata()) == EnumDyeColor.BROWN, 1);

		InventoryTools.insertOrDropItems(inventory, drops, world, pos);

		return true;
	}

	@Override
	public boolean isPlantable() {
		return true;
	}
}
