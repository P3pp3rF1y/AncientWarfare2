package net.shadowmage.ancientwarfare.structure.block;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
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
import net.shadowmage.ancientwarfare.core.util.WorldTools;
import net.shadowmage.ancientwarfare.structure.tile.TileChair;
import net.shadowmage.ancientwarfare.structure.util.RotationLimit;
import net.shadowmage.ancientwarfare.structure.util.WoodVariantHelper;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

import static net.shadowmage.ancientwarfare.core.render.property.CoreProperties.FACING;
import static net.shadowmage.ancientwarfare.core.render.property.CoreProperties.VISIBLE;
import static net.shadowmage.ancientwarfare.structure.util.BlockStateProperties.VARIANT;

public class BlockChair extends BlockSeat {
	private static final Vec3d SEAT_OFFSET = new Vec3d(0.5, 0.47, 0.5);

	public BlockChair() {
		super(Material.WOOD, "chair");
	}

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
		return state.withProperty(FACING, WorldTools.getTile(world, pos, TileChair.class).map(TileChair::getPrimaryFacing).orElse(EnumFacing.NORTH));
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
		world.setBlockState(pos, state.withProperty(VARIANT, WoodVariantHelper.getVariant(stack)).withProperty(VISIBLE, true));
		WorldTools.getTile(world, pos, TileChair.class).ifPresent(te -> te.setPrimaryFacing(placer.getHorizontalFacing().getOpposite()));
		world.setBlockState(pos.up(), getDefaultState().withProperty(VARIANT, WoodVariantHelper.getVariant(stack)).withProperty(VISIBLE, false));
		WorldTools.getTile(world, pos.up(), TileChair.class).ifPresent(te -> {
			te.setPrimaryFacing(placer.getHorizontalFacing().getOpposite());
			te.setMainBlockPos(pos);
		});
	}

	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
		return WoodVariantHelper.getPickBlock(this, state);
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, VARIANT, FACING, VISIBLE);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(VARIANT, WoodVariant.byMeta(meta & 7)).withProperty(VISIBLE, ((meta >> 3) & 1) > 0);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(VARIANT).getMeta() | (state.getValue(VISIBLE) ? 1 : 0) << 3;
	}

	@Override
	public boolean hasTileEntity(IBlockState state) {
		return true;
	}

	@Nullable
	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileChair();
	}

	private static final List<AxisAlignedBB> AABBs = ImmutableList.of(
			new AxisAlignedBB(1 / 16D, 9 / 16D, 1 / 16D, 15 / 16D, 11 / 16D, 15 / 16D),
			new AxisAlignedBB(1 / 16D, 0, 1 / 16D, 3 / 16D, 9 / 16D, 3 / 16D),
			new AxisAlignedBB(13 / 16D, 0, 1 / 16D, 15 / 16D, 9 / 16D, 3 / 16D),
			new AxisAlignedBB(13 / 16D, 0, 13 / 16D, 15 / 16D, 9 / 16D, 15 / 16D),
			new AxisAlignedBB(1 / 16D, 0, 13 / 16D, 3 / 16D, 9 / 16D, 15 / 16D));

	private static final Map<EnumFacing, List<AxisAlignedBB>> BOTTOM_AABBs = ImmutableMap.of(
			EnumFacing.NORTH, new ImmutableList.Builder<AxisAlignedBB>().addAll(AABBs)
					.add(new AxisAlignedBB(1 / 16D, 11 / 16D, 13 / 16D, 15 / 16D, 1, 15 / 16D)).build(),
			EnumFacing.SOUTH, new ImmutableList.Builder<AxisAlignedBB>().addAll(AABBs)
					.add(new AxisAlignedBB(1 / 16D, 11 / 16D, 1 / 16D, 15 / 16D, 1, 3 / 16D)).build(),
			EnumFacing.EAST, new ImmutableList.Builder<AxisAlignedBB>().addAll(AABBs)
					.add(new AxisAlignedBB(1 / 16D, 11 / 16D, 1 / 16D, 3 / 16D, 1, 15 / 16D)).build(),
			EnumFacing.WEST, new ImmutableList.Builder<AxisAlignedBB>().addAll(AABBs)
					.add(new AxisAlignedBB(13 / 16D, 11 / 16D, 1 / 16D, 15 / 16D, 1, 15 / 16D)).build()
	);

	private static final Map<EnumFacing, AxisAlignedBB> TOP_AABBs = ImmutableMap.of(
			EnumFacing.NORTH, new AxisAlignedBB(1 / 16D, 0, 13 / 16D, 15 / 16D, 9 / 16D, 15 / 16D),
			EnumFacing.SOUTH, new AxisAlignedBB(1 / 16D, 0, 1 / 16D, 15 / 16D, 9 / 16D, 3 / 16D),
			EnumFacing.EAST, new AxisAlignedBB(1 / 16D, 0, 1 / 16D, 3 / 16D, 9 / 16D, 15 / 16D),
			EnumFacing.WEST, new AxisAlignedBB(13 / 16D, 0, 1 / 16D, 15 / 16D, 9 / 16D, 15 / 16D)
	);

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

	@Nullable
	@Override
	public RayTraceResult collisionRayTrace(IBlockState blockState, World world, BlockPos pos, Vec3d start, Vec3d end) {
		EnumFacing facing = WorldTools.getTile(world, pos, TileChair.class).map(TileChair::getPrimaryFacing).orElse(EnumFacing.NORTH);
		return blockState.getValue(VISIBLE) ? RayTraceUtils.raytraceMultiAABB(BOTTOM_AABBs.get(facing), pos, start, end, (rtr, aabb) -> rtr) :
				rayTrace(pos, start, end, TOP_AABBs.get(facing));
	}

	@Nullable
	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess world, BlockPos pos) {
		return blockState.getValue(VISIBLE) ? AABBs.get(0) : TOP_AABBs.get(WorldTools.getTile(world, pos, TileChair.class).map(TileChair::getPrimaryFacing).orElse(EnumFacing.NORTH));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World world, BlockPos pos) {
		EnumFacing facing = WorldTools.getTile(world, pos, TileChair.class).map(TileChair::getPrimaryFacing).orElse(EnumFacing.NORTH);
		if (!state.getValue(VISIBLE)) {
			return TOP_AABBs.get(facing).offset(pos);
		}
		EntityPlayerSP player = Minecraft.getMinecraft().player;
		return RayTraceUtils.getSelectedBoundingBox(BOTTOM_AABBs.get(facing), pos, player);
	}

	@Override
	protected Vec3d getSeatOffset() {
		return SEAT_OFFSET;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerClient() {
		WoodVariantHelper.registerClient(this, propString -> "facing=north," + propString + ",visible=true");
	}

	@Override
	public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		WoodVariantHelper.getDrops(this, drops, state);
	}

	@Override
	public RotationLimit getRotationLimit(World world, BlockPos seatPos, IBlockState state) {
		return new RotationLimit.FacingThreeQuarters(WorldTools.getTile(world, seatPos, TileChair.class).map(TileChair::getPrimaryFacing).orElse(EnumFacing.NORTH));
	}
}
