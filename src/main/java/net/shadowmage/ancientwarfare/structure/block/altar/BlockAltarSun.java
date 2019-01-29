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

public class BlockAltarSun extends BlockAltarTop {
	private static final AxisAlignedBB AABB_NORTH_SOUTH = new AxisAlignedBB(1 / 16D, 0, 5 / 16D, 15 / 16D, 1D, 11 / 16D);
	private static final AxisAlignedBB AABB_WEST_EAST = new AxisAlignedBB(5 / 16D, 0, 1 / 16D, 11 / 16D, 1D, 15 / 16D);

	public BlockAltarSun() {
		super(Material.WOOD, "altar_sun");
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
			case SOUTH:
				return AABB_NORTH_SOUTH;
			case WEST:
			case EAST:
				return AABB_WEST_EAST;
		}
		return AABB_NORTH_SOUTH;
	}
}
