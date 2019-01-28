package net.shadowmage.ancientwarfare.structure.block.altar;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import javax.annotation.Nullable;

public class BlockAltarCandle extends BlockAltarTop {
	private static final AxisAlignedBB CANDLE_AABB = new AxisAlignedBB(6 / 16D, 0, 6 / 16D, 10 / 16D, 10 / 16D, 10 / 16D);

	public BlockAltarCandle() {
		super(Material.IRON, "altar_candle");
	}

	@Nullable
	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
		return NULL_AABB;
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return CANDLE_AABB;
	}
}
