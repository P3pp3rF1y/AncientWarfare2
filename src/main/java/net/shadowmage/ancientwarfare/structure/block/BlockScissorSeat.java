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
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
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

public class BlockScissorSeat extends BlockSeat {
	public BlockScissorSeat() {
		super(Material.WOOD, "scissor_seat");
	}

	@Override
	public RotationLimit getRotationLimit(World world, BlockPos seatPos, IBlockState state) {
		return new RotationLimit.FacingQuarter(state.getValue(FACING));
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		world.setBlockState(pos, state.withProperty(FACING, placer.getHorizontalFacing().getOpposite()));
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, FACING);
	}

	@Override
	protected Vec3d getSeatOffset() {
		return new Vec3d(0.5, 0.27, 0.5);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(FACING, EnumFacing.HORIZONTALS[meta & 3]);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(FACING).getHorizontalIndex();
	}

	private static final Map<EnumFacing, List<AxisAlignedBB>> AABBs = ImmutableMap.of(
			EnumFacing.NORTH, new ImmutableList.Builder<AxisAlignedBB>()
					.add(new AxisAlignedBB(3 / 16D, 0, 3 / 16D, 13 / 16D, 8 / 16D, 12 / 16D))
					.add(new AxisAlignedBB(1.5 / 16D, 11 / 16D, 12.5 / 16D, 14.5 / 16D, 15.5 / 16D, 13.5 / 16D))
					.add(new AxisAlignedBB(2 / 16D, 8 / 16D, 2 / 16D, 3 / 16D, 11 / 16D, 13 / 16D))
					.add(new AxisAlignedBB(13 / 16D, 8 / 16D, 2 / 16D, 14 / 16D, 11 / 16D, 13 / 16D))
					.build(),
			EnumFacing.SOUTH, new ImmutableList.Builder<AxisAlignedBB>()
					.add(new AxisAlignedBB(3 / 16D, 0, 4 / 16D, 13 / 16D, 8 / 16D, 13 / 16D))
					.add(new AxisAlignedBB(1.5 / 16D, 11 / 16D, 2.5 / 16D, 14.5 / 16D, 15.5 / 16D, 3.5 / 16D))
					.add(new AxisAlignedBB(2 / 16D, 8 / 16D, 3 / 16D, 3 / 16D, 11 / 16D, 14 / 16D))
					.add(new AxisAlignedBB(13 / 16D, 8 / 16D, 3 / 16D, 14 / 16D, 11 / 16D, 14 / 16D))
					.build(),
			EnumFacing.EAST, new ImmutableList.Builder<AxisAlignedBB>()
					.add(new AxisAlignedBB(4 / 16D, 0, 3 / 16D, 13 / 16D, 8 / 16D, 13 / 16D))
					.add(new AxisAlignedBB(2.5 / 16D, 11 / 16D, 1.5 / 16D, 3.5 / 16D, 15.5 / 16D, 14.5 / 16D))
					.add(new AxisAlignedBB(3 / 16D, 8 / 16D, 2 / 16D, 14 / 16D, 11 / 16D, 3 / 16D))
					.add(new AxisAlignedBB(3 / 16D, 8 / 16D, 13 / 16D, 14 / 16D, 11 / 16D, 14 / 16D))
					.build(),
			EnumFacing.WEST, new ImmutableList.Builder<AxisAlignedBB>()
					.add(new AxisAlignedBB(3 / 16D, 0, 3 / 16D, 12 / 16D, 8 / 16D, 13 / 16D))
					.add(new AxisAlignedBB(12.5 / 16D, 11 / 16D, 1.5 / 16D, 13.5 / 16D, 15.5 / 16D, 14.5 / 16D))
					.add(new AxisAlignedBB(2 / 16D, 8 / 16D, 2 / 16D, 13 / 16D, 11 / 16D, 3 / 16D))
					.add(new AxisAlignedBB(2 / 16D, 8 / 16D, 13 / 16D, 13 / 16D, 11 / 16D, 14 / 16D))
					.build()
	);

	@Nullable
	@Override
	public RayTraceResult collisionRayTrace(IBlockState blockState, World world, BlockPos pos, Vec3d start, Vec3d end) {

		EnumFacing facing = blockState.getValue(FACING);

		return RayTraceUtils.raytraceMultiAABB(AABBs.get(facing), pos, start, end, (rtr, aabb) -> rtr);
	}

	@Override
	public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes,
			@Nullable Entity entityIn, boolean isActualState) {
		AABBs.get(state.getValue(FACING)).forEach(aabb -> addCollisionBoxToList(pos, entityBox, collidingBoxes, aabb));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World world, BlockPos pos) {
		EnumFacing facing = state.getValue(FACING);
		EntityPlayerSP player = Minecraft.getMinecraft().player;
		return RayTraceUtils.getSelectedBoundingBox(AABBs.get(facing), pos, player);
	}
}
