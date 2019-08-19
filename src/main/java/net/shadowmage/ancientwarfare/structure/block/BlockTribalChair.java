package net.shadowmage.ancientwarfare.structure.block;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.core.util.RayTraceUtils;
import net.shadowmage.ancientwarfare.structure.util.RotationLimit;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

import static net.shadowmage.ancientwarfare.core.render.property.CoreProperties.FACING;
import static net.shadowmage.ancientwarfare.core.render.property.CoreProperties.VISIBLE;

public class BlockTribalChair extends BlockSeat {
	public BlockTribalChair() {
		super(Material.WOOD, "tribal_chair");
	}

	@Override
	public RotationLimit getRotationLimit(World world, BlockPos seatPos, IBlockState state) {
		return new RotationLimit.FacingQuarter(state.getValue(FACING));
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		BlockPos otherPos = state.getValue(VISIBLE) ? pos.up() : pos.down();
		if (world.getBlockState(otherPos).getBlock() == this) {
			world.setBlockToAir(otherPos);
		}
		super.breakBlock(world, pos, state);
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		world.setBlockState(pos, state.withProperty(FACING, placer.getHorizontalFacing().getOpposite()).withProperty(VISIBLE, true));
		world.setBlockState(pos.up(), state.withProperty(FACING, placer.getHorizontalFacing().getOpposite()).withProperty(VISIBLE, false));
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, FACING, VISIBLE);
	}

	@Override
	protected Vec3d getSeatOffset() {
		return new Vec3d(0.5, 0.35, 0.5);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(FACING, EnumFacing.HORIZONTALS[meta & 3]).withProperty(VISIBLE, ((meta >> 2) & 1) > 0);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(FACING).getHorizontalIndex() | (state.getValue(VISIBLE) ? 1 : 0) << 2;
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return state.getValue(VISIBLE) ? EnumBlockRenderType.MODEL : EnumBlockRenderType.INVISIBLE;
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (state.getValue(VISIBLE)) {
			return super.onBlockActivated(world, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
		}
		IBlockState stateDown = world.getBlockState(pos.down());
		return stateDown.getBlock().onBlockActivated(world, pos.down(), stateDown, playerIn, hand, facing, hitX, hitY, hitZ);
	}

	private static final Map<EnumFacing, List<AxisAlignedBB>> BOTTOM_AABBs = ImmutableMap.of(
			EnumFacing.NORTH, new ImmutableList.Builder<AxisAlignedBB>()
					.add(new AxisAlignedBB(0, 0, 1 / 16D, 1, 9 / 16D, 15 / 16D))
					.add(new AxisAlignedBB(0, 9 / 16D, 13 / 16D, 1, 1, 15 / 16D))
					.add(new AxisAlignedBB(0, 9 / 16D, 1 / 16D, 2 / 16D, 14 / 16D, 13 / 16D))
					.add(new AxisAlignedBB(14 / 16D, 9 / 16D, 1 / 16D, 1, 14 / 16D, 13 / 16D))
					.build(),
			EnumFacing.SOUTH, new ImmutableList.Builder<AxisAlignedBB>()
					.add(new AxisAlignedBB(0, 0, 1 / 16D, 1, 9 / 16D, 15 / 16D))
					.add(new AxisAlignedBB(0, 9 / 16D, 1 / 16D, 1, 1, 3 / 16D))
					.add(new AxisAlignedBB(0, 9 / 16D, 3 / 16D, 2 / 16D, 14 / 16D, 15 / 16D))
					.add(new AxisAlignedBB(14 / 16D, 9 / 16D, 3 / 16D, 1, 14 / 16D, 15 / 16D))
					.build(),
			EnumFacing.EAST, new ImmutableList.Builder<AxisAlignedBB>()
					.add(new AxisAlignedBB(1 / 16D, 0, 0, 15 / 16D, 9 / 16D, 1))
					.add(new AxisAlignedBB(1 / 16D, 9 / 16D, 0, 3 / 16D, 1, 1))
					.add(new AxisAlignedBB(3 / 16D, 9 / 16D, 0, 15 / 16D, 14 / 16D, 2 / 16D))
					.add(new AxisAlignedBB(3 / 16D, 9 / 16D, 14 / 16D, 15 / 16D, 14 / 16D, 1))
					.build(),
			EnumFacing.WEST, new ImmutableList.Builder<AxisAlignedBB>()
					.add(new AxisAlignedBB(1 / 16D, 0, 0, 15 / 16D, 9 / 16D, 1))
					.add(new AxisAlignedBB(13 / 16D, 9 / 16D, 0, 15 / 16D, 1, 1))
					.add(new AxisAlignedBB(1 / 16D, 9 / 16D, 0, 13 / 16D, 14 / 16D, 2 / 16D))
					.add(new AxisAlignedBB(1 / 16D, 9 / 16D, 14 / 16D, 13 / 16D, 14 / 16D, 1))
					.build()
	);

	private static final Map<EnumFacing, AxisAlignedBB> TOP_AABBs = ImmutableMap.of(
			EnumFacing.NORTH, new AxisAlignedBB(0, 0, 13 / 16D, 1, 9 / 16D, 15 / 16D),
			EnumFacing.SOUTH, new AxisAlignedBB(0, 0, 1 / 16D, 1, 9 / 16D, 3 / 16D),
			EnumFacing.EAST, new AxisAlignedBB(1 / 16D, 0, 0, 3 / 16D, 9 / 16D, 1),
			EnumFacing.WEST, new AxisAlignedBB(13 / 16D, 0, 0, 15 / 16D, 9 / 16D, 1)
	);

	@Nullable
	@Override
	public RayTraceResult collisionRayTrace(IBlockState blockState, World world, BlockPos pos, Vec3d start, Vec3d end) {
		EnumFacing facing = blockState.getValue(FACING);

		return blockState.getValue(VISIBLE) ? RayTraceUtils.raytraceMultiAABB(BOTTOM_AABBs.get(facing), pos, start, end, (rtr, aabb) -> rtr) :
				rayTrace(pos, start, end, TOP_AABBs.get(facing));
	}

	@Override
	public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes,
			@Nullable Entity entityIn, boolean isActualState) {
		if (state.getValue(VISIBLE)) {
			BOTTOM_AABBs.get(state.getValue(FACING)).forEach(aabb -> addCollisionBoxToList(pos, entityBox, collidingBoxes, aabb));

		} else {
			addCollisionBoxToList(pos, entityBox, collidingBoxes, TOP_AABBs.get(state.getValue(FACING)));
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World world, BlockPos pos) {
		EnumFacing facing = state.getValue(FACING);
		if (!state.getValue(VISIBLE)) {
			return TOP_AABBs.get(facing).offset(pos);
		}
		EntityPlayerSP player = Minecraft.getMinecraft().player;
		return RayTraceUtils.getSelectedBoundingBox(BOTTOM_AABBs.get(facing), pos, player);
	}
}
