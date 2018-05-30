package net.shadowmage.ancientwarfare.automation.tile.worksite.cropfarm;

import net.minecraft.block.IGrowable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.util.parsing.BlockStateMatcher;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

public class CropTall extends CropDefault {
	private int height = 1;
	private BlockStateMatcher stateMatcher;

	public CropTall(BlockStateMatcher stateMatcher, int height) {
		this.stateMatcher = stateMatcher;
		this.height = height;
	}

	@Override
	public List<BlockPos> getPositionsToHarvest(World world, BlockPos pos, IBlockState state) {
		List<BlockPos> ret = new ArrayList<>();
		applyToCrop(world, pos, (p, s) -> {
			if (s.getBlock() instanceof IGrowable && !((IGrowable) s.getBlock()).canGrow(world, p, s, world.isRemote)) {
				ret.add(p);
			}
			return true;
		});
		return ret;
	}

	@Override
	protected boolean breakCrop(World world, BlockPos pos, IBlockState state) {
		return applyToCrop(world, pos, (p, s) -> super.breakCrop(world, p, s));
	}

	@Override
	protected void getDrops(NonNullList<ItemStack> stacks, World world, BlockPos pos, IBlockState state, int fortune) {
		applyToCrop(world, pos, (p, s) -> {
			super.getDrops(stacks, world, p, s, fortune);
			return true;
		});
	}

	private boolean applyToCrop(World world, BlockPos pos, BiFunction<BlockPos, IBlockState, Boolean> applyToBlock) {
		boolean ret = true;
		for (BlockPos curPos = new BlockPos(pos.getX(), pos.getY() + (height - 1), pos.getZ()); curPos.getY() >= pos.getY(); curPos = curPos.down()) {
			IBlockState curState = world.getBlockState(curPos);
			ret = ret && applyToBlock.apply(curPos, curState);
		}
		return ret;
	}

	@Override
	public boolean matches(IBlockState state) {
		return stateMatcher.test(state);
	}
}
