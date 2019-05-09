package net.shadowmage.ancientwarfare.structure.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.structure.util.RotationLimit;

import static net.shadowmage.ancientwarfare.core.render.property.CoreProperties.FACING;

public class BlockTribalStool extends BlockSeat {
	private static final Vec3d SEAT_OFFSET = new Vec3d(0.5, 0.4, 0.5);
	private static final AxisAlignedBB Z_AXIS_AABB = new AxisAlignedBB(0, 0, 1 / 16D, 1, 10 / 16D, 15 / 16D);
	private static final AxisAlignedBB X_AXIS_AABB = new AxisAlignedBB(1 / 16D, 0, 0, 15 / 16D, 10 / 16D, 1);

	public BlockTribalStool() {
		super(Material.WOOD, "tribal_stool");
	}

	@Override
	public RotationLimit getRotationLimit(World world, BlockPos seatPos, IBlockState state) {
		return RotationLimit.NO_LIMIT;
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, FACING);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(FACING, EnumFacing.HORIZONTALS[meta]);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(FACING).getHorizontalIndex();
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		world.setBlockState(pos, state.withProperty(FACING, placer.getHorizontalFacing().getOpposite()));
	}

	@Override
	protected Vec3d getSeatOffset() {
		return SEAT_OFFSET;
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return state.getValue(FACING).getAxis() == EnumFacing.Axis.Z ? Z_AXIS_AABB : X_AXIS_AABB;
	}
}
