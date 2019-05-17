package net.shadowmage.ancientwarfare.structure.block;

import com.google.common.collect.ImmutableMap;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.structure.util.RotationLimit;

import java.util.Map;

import static net.shadowmage.ancientwarfare.core.render.property.CoreProperties.FACING;
import static net.shadowmage.ancientwarfare.core.render.property.CoreProperties.VISIBLE;

public class BlockWoodenThrone extends BlockSeat {
	public BlockWoodenThrone() {
		super(Material.WOOD, "wooden_throne");
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, FACING, VISIBLE);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(FACING, EnumFacing.getHorizontal(meta & 3)).withProperty(VISIBLE, ((meta >> 2) & 1) > 0);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(FACING).getHorizontalIndex() | (state.getValue(VISIBLE) ? 1 : 0) << 2;
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return state.getValue(VISIBLE) ? EnumBlockRenderType.MODEL : EnumBlockRenderType.INVISIBLE;
	}

	private static final Map<EnumFacing, AxisAlignedBB> TOP_AABBs = ImmutableMap.of(
			EnumFacing.NORTH, new AxisAlignedBB(0, 0, 13 / 16D, 1, 1, 1),
			EnumFacing.SOUTH, new AxisAlignedBB(0, 0, 0, 1, 1, 3 / 16D),
			EnumFacing.EAST, new AxisAlignedBB(0, 0, 0, 3 / 16D, 1, 1),
			EnumFacing.WEST, new AxisAlignedBB(13 / 16D, 0, 0, 1, 1, 1)
	);

	private static final Map<EnumFacing, AxisAlignedBB> BOTTOM_AABBs = ImmutableMap.of(
			EnumFacing.NORTH, new AxisAlignedBB(0, 0, 1 / 16D, 1, 1, 1),
			EnumFacing.SOUTH, new AxisAlignedBB(0, 0, 0, 1, 1, 15 / 16D),
			EnumFacing.EAST, new AxisAlignedBB(0, 0, 0, 15 / 16D, 1, 1),
			EnumFacing.WEST, new AxisAlignedBB(1 / 16D, 0, 0, 1, 1, 1)
	);

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return state.getValue(VISIBLE) ? BOTTOM_AABBs.get(state.getValue(FACING)) : TOP_AABBs.get(state.getValue(FACING));
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		world.setBlockState(pos.up(), state.withProperty(FACING, placer.getHorizontalFacing().getOpposite()).withProperty(VISIBLE, false));
		world.setBlockState(pos, state.withProperty(FACING, placer.getHorizontalFacing().getOpposite()).withProperty(VISIBLE, true));
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (state.getValue(VISIBLE)) {
			return super.onBlockActivated(world, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
		}
		IBlockState stateDown = world.getBlockState(pos.down());
		return stateDown.getBlock().onBlockActivated(world, pos.down(), stateDown, playerIn, hand, facing, hitX, hitY, hitZ);
	}

	@Override
	public RotationLimit getRotationLimit(World world, BlockPos seatPos, IBlockState state) {
		return new RotationLimit.FacingQuarter(state.getValue(FACING));
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		BlockPos otherPos = state.getValue(VISIBLE) ? pos.up() : pos.down();
		if (!world.isAirBlock(otherPos)) {
			world.setBlockToAir(otherPos);
		}
		super.breakBlock(world, pos, state);
	}
}
