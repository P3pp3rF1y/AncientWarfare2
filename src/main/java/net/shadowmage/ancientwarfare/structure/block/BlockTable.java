package net.shadowmage.ancientwarfare.structure.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.core.util.RayTraceUtils;
import net.shadowmage.ancientwarfare.structure.util.BlockStateProperties;
import net.shadowmage.ancientwarfare.structure.util.WoodVariantHelper;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class BlockTable extends BlockBaseStructure {
	private static final PropertyBool LEG_NORTHEAST = PropertyBool.create("leg_northeast");
	private static final PropertyBool LEG_SOUTHEAST = PropertyBool.create("leg_southeast");
	private static final PropertyBool LEG_SOUTHWEST = PropertyBool.create("leg_southwest");
	private static final PropertyBool LEG_NORTHWEST = PropertyBool.create("leg_northwest");

	public BlockTable() {
		super(Material.WOOD, "table");
	}

	@Override
	public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
		WoodVariantHelper.getSubBlocks(this, items);
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		if (!stack.hasTagCompound()) {
			return;
		}
		world.setBlockState(pos, state.withProperty(BlockStateProperties.VARIANT, WoodVariantHelper.getVariant(stack)));
	}

	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
		return WoodVariantHelper.getPickBlock(this, state);
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, BlockStateProperties.VARIANT, LEG_NORTHEAST, LEG_NORTHWEST, LEG_SOUTHEAST, LEG_SOUTHWEST);
	}

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
		LegPositions legPositions = getLegPositions(world, pos);

		state = state.withProperty(LEG_NORTHEAST, legPositions.northEast);
		state = state.withProperty(LEG_SOUTHEAST, legPositions.southEast);
		state = state.withProperty(LEG_SOUTHWEST, legPositions.southWest);
		state = state.withProperty(LEG_NORTHWEST, legPositions.northWest);
		return state;
	}

	private BlockTable.LegPositions getLegPositions(IBlockAccess world, BlockPos pos) {
		boolean north = world.getBlockState(pos.north()).getBlock() == this;
		boolean east = world.getBlockState(pos.east()).getBlock() == this;
		boolean south = world.getBlockState(pos.south()).getBlock() == this;
		boolean west = world.getBlockState(pos.west()).getBlock() == this;

		return new LegPositions(!(north || east), !(south || east), !(south || west), !(north || west));
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(BlockStateProperties.VARIANT, WoodVariant.byMeta(meta));
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(BlockStateProperties.VARIANT).getMeta();
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
		return face == EnumFacing.UP ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
	}

	private static final AxisAlignedBB TOP_AABB = new AxisAlignedBB(0, 14 / 16D, 0, 1, 1, 1);
	private static final AxisAlignedBB LEG_NORTHWEST_AABB = new AxisAlignedBB(0, 0, 0, 2 / 16D, 14 / 16D, 2 / 16D);
	private static final AxisAlignedBB LEG_NORTHEAST_AABB = new AxisAlignedBB(14 / 16D, 0, 0, 1, 14 / 16D, 2 / 16D);
	private static final AxisAlignedBB LEG_SOUTHEAST_AABB = new AxisAlignedBB(14 / 16D, 0, 14 / 16D, 1, 14 / 16D, 1);
	private static final AxisAlignedBB LEG_SOUTHWEST_AABB = new AxisAlignedBB(0, 0, 14 / 16D, 2 / 16D, 14 / 16D, 1);

	@Nullable
	@Override
	public RayTraceResult collisionRayTrace(IBlockState blockState, World world, BlockPos pos, Vec3d start, Vec3d end) {
		return RayTraceUtils.raytraceMultiAABB(getAABBs(world, pos), pos, start, end, (rtr, aabb) -> rtr);
	}

	private List<AxisAlignedBB> getAABBs(World world, BlockPos pos) {
		LegPositions legPositions = getLegPositions(world, pos);

		List<AxisAlignedBB> aabbs = new ArrayList<>();
		aabbs.add(TOP_AABB);
		if (legPositions.northEast) {
			aabbs.add(LEG_NORTHEAST_AABB);
		}
		if (legPositions.northWest) {
			aabbs.add(LEG_NORTHWEST_AABB);
		}
		if (legPositions.southEast) {
			aabbs.add(LEG_SOUTHEAST_AABB);
		}
		if (legPositions.southWest) {
			aabbs.add(LEG_SOUTHWEST_AABB);
		}
		return aabbs;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World world, BlockPos pos) {
		EntityPlayerSP player = Minecraft.getMinecraft().player;
		return RayTraceUtils.getSelectedBoundingBox(getAABBs(world, pos), pos, player);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerClient() {
		WoodVariantHelper.registerClient(this, propString -> "leg_northeast=true,leg_northwest=true,leg_southeast=true,leg_southwest=true," + propString);
	}

	@Override
	public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		WoodVariantHelper.getDrops(this, drops, state);
	}

	private static class LegPositions {
		private final boolean northEast;
		private final boolean southEast;
		private final boolean southWest;
		private final boolean northWest;

		private LegPositions(boolean northEast, boolean southEast, boolean southWest, boolean northWest) {
			this.northEast = northEast;
			this.southEast = southEast;
			this.southWest = southWest;
			this.northWest = northWest;
		}
	}
}
