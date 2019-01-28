package net.shadowmage.ancientwarfare.structure.block.altar;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import static net.shadowmage.ancientwarfare.core.render.property.CoreProperties.FACING;

public class BlockAltarLectern extends BlockAltarTop {
	private static final AxisAlignedBB AABB_NORTH = new AxisAlignedBB(0, 0, 0, 1D, 9 / 16D, 14 / 16D);
	private static final AxisAlignedBB AABB_SOUTH = new AxisAlignedBB(0, 0, 2 / 16D, 1D, 9 / 16D, 1D);
	private static final AxisAlignedBB AABB_WEST = new AxisAlignedBB(0, 0, 0, 14 / 16D, 9 / 16D, 1D);
	private static final AxisAlignedBB AABB_EAST = new AxisAlignedBB(2 / 16D, 0, 0, 1D, 9 / 16D, 1D);

	public BlockAltarLectern() {
		super(Material.WOOD, "altar_lectern");
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, FACING);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(FACING, EnumFacing.getHorizontal(meta));
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(FACING).getHorizontalIndex();
	}

	@Override
	public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		return getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		switch (state.getValue(FACING)) {
			case NORTH:
				return AABB_NORTH;
			case SOUTH:
				return AABB_SOUTH;
			case WEST:
				return AABB_WEST;
			case EAST:
				return AABB_EAST;
		}
		return new AxisAlignedBB(2 / 16D, 0, 0, 1D, 9 / 16D, 1D);
	}
}
