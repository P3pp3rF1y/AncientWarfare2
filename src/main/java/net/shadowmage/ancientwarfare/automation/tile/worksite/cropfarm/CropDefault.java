package net.shadowmage.ancientwarfare.automation.tile.worksite.cropfarm;

import net.minecraft.block.IGrowable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemBlockSpecial;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.items.IItemHandler;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;

import java.util.Collections;
import java.util.List;

public class CropDefault implements ICrop {
	@Override
	public List<BlockPos> getPositionsToHarvest(World world, BlockPos pos, IBlockState state) {
		if (state.getBlock() instanceof IGrowable && !((IGrowable) state.getBlock()).canGrow(world, pos, state, world.isRemote)) {
			return Collections.singletonList(pos);
		}
		return Collections.emptyList();
	}

	@Override
	public boolean canBeFertilized(IBlockState state, World world, BlockPos pos) {
		if (!(state.getBlock() instanceof IGrowable)) {
			return false;
		}

		IGrowable growable = (IGrowable) state.getBlock();

		return growable.canGrow(world, pos, state, world.isRemote) && growable.canUseBonemeal(world, world.rand, pos, state);
	}

	@Override
	public boolean harvest(World world, IBlockState state, BlockPos pos, int fortune, IItemHandler inventory) {
		NonNullList<ItemStack> stacks = NonNullList.create();

		getDrops(stacks, world, pos, state, fortune);

		if (!InventoryTools.canInventoryHold(inventory, stacks)) {
			return false;
		}

		if (!breakCrop(world, pos, state)) {
			return false;
		}

		ItemStack plantable = InventoryTools.removeItem(stacks, i -> i.getItem() instanceof IPlantable, 1);

		if (!plantable.isEmpty()) {
			BlockTools.placeItemBlock(plantable, world, pos, EnumFacing.UP);
		}

		InventoryTools.insertOrDropItems(inventory, stacks, world, pos);
		return true;
	}

	protected boolean breakCrop(World world, BlockPos pos, IBlockState state) {
		return BlockTools.breakBlockNoDrops(world, pos, state);
	}

	protected void getDrops(NonNullList<ItemStack> stacks, World world, BlockPos pos, IBlockState state, int fortune) {
		state.getBlock().getDrops(stacks, world, pos, state, fortune);
	}

	@Override
	public boolean matches(IBlockState state) {
		return true;
	}

	@Override
	public boolean matches(ItemStack stack) {
		return true;
	}

	@Override
	public boolean isPlantable(ItemStack stack) {
		return stack.getItem() instanceof IPlantable || isPlantableItemBlock(stack) || isPlantableSpecialItemBlock(stack);
	}

	private boolean isPlantableSpecialItemBlock(ItemStack stack) {
		return stack.getItem() instanceof ItemBlockSpecial && ((ItemBlockSpecial) stack.getItem()).getBlock() instanceof IPlantable;
	}

	private boolean isPlantableItemBlock(ItemStack stack) {
		return stack.getItem() instanceof ItemBlock && ((ItemBlock) stack.getItem()).getBlock() instanceof IPlantable;
	}
}
