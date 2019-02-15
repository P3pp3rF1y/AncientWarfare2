package net.shadowmage.ancientwarfare.structure.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import static net.minecraft.util.EnumBlockRenderType.INVISIBLE;
import static net.minecraft.util.EnumBlockRenderType.MODEL;
import static net.shadowmage.ancientwarfare.core.render.property.CoreProperties.FACING;

public class BlockGoldenIdol extends BlockBaseStructure {
	private static final PropertyBool BOTTOM_PART = PropertyBool.create("bottom_part");

	public BlockGoldenIdol() {
		super(Material.IRON, "golden_idol");
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, FACING, BOTTOM_PART);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(FACING, EnumFacing.getHorizontal(meta)).withProperty(BOTTOM_PART, ((meta >> 2) & 1) > 0);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(FACING).getHorizontalIndex() | (state.getValue(BOTTOM_PART) ? 1 : 0) << 2;
	}

	@Override
	public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		return getDefaultState().withProperty(FACING, placer.getHorizontalFacing()).withProperty(BOTTOM_PART, true);
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return state.getValue(BOTTOM_PART) ? MODEL : INVISIBLE;
	}

	@Override
	public boolean canPlaceBlockAt(World world, BlockPos pos) {
		return super.canPlaceBlockAt(world, pos) && super.canPlaceBlockAt(world, pos.up());
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		worldIn.setBlockState(pos.up(), state.withProperty(BOTTOM_PART, false));
	}

	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
		Boolean bottomPart = state.getValue(BOTTOM_PART);
		BlockPos otherPos = bottomPart ? pos.up() : pos.down();
		if (worldIn.getBlockState(otherPos).getBlock() != this) {
			worldIn.setBlockToAir(pos);
			if (bottomPart) {
				dropBlockAsItem(worldIn, pos, state, 0);
			}
		}
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isNormalCube(IBlockState state) {
		return false;
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
		return BlockFaceShape.UNDEFINED;
	}
}
