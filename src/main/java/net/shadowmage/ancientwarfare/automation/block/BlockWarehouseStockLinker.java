package net.shadowmage.ancientwarfare.automation.block;

import com.google.common.collect.ImmutableMap;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.automation.gui.GuiWarehouseStockLinker;
import net.shadowmage.ancientwarfare.automation.item.ItemBlockWarehouseStockLinker;
import net.shadowmage.ancientwarfare.automation.tile.warehouse2.TileWarehouseStockLinker;
import net.shadowmage.ancientwarfare.automation.tile.warehouse2.TileWarehouseStockLinker.WarehouseStockFilter;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.IRotatableBlock;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.RotationType;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.core.util.WorldTools;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static net.shadowmage.ancientwarfare.core.render.property.CoreProperties.FACING;

public class BlockWarehouseStockLinker extends BlockBaseAutomation implements IRotatableBlock {
	private static final PropertyBool LIT = PropertyBool.create("lit");
	private static final Map<EnumFacing, AxisAlignedBB> AABBS;
	public final List<WarehouseStockFilter> filters = new ArrayList<>();

	static {
		float wmin = 0.125f;
		float wmax = 0.875f;
		float hmin = 0.375f;
		float hmax = 0.875f;
		AABBS = ImmutableMap.of(EnumFacing.WEST, new AxisAlignedBB(wmax, hmin, 0, 1.f, hmax, 1), EnumFacing.EAST, new AxisAlignedBB(0, hmin, 0, wmin, hmax, 1), EnumFacing.SOUTH, new AxisAlignedBB(0, hmin, 0, 1, hmax, wmin), EnumFacing.NORTH, new AxisAlignedBB(0, hmin, wmax, 1, hmax, 1));
	}

	public BlockWarehouseStockLinker(String regName) {
		super(Material.ROCK, regName);
		setHardness(2.f);
	}

	@Override
	public boolean hasTileEntity(IBlockState state) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileWarehouseStockLinker();
	}

	@Override
	public boolean canProvidePower(IBlockState iBlockState) {
		return true;
	}

	@Override
	public int getWeakPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
		return isPowered(blockAccess, pos) ? 15 : 0;
	}

	@Override
	public int getStrongPower(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
		boolean doPower = isPowered(world, pos);

		if (!doPower) {
			return 0;
		} else {
			return state.getValue(FACING) == side ? 15 : 0;
		}
	}

	private boolean isPowered(IBlockAccess world, BlockPos pos) {
		boolean doPower = false;
		TileEntity tileentity = world.getTileEntity(pos);
		if (tileentity instanceof TileWarehouseStockLinker) {
			TileWarehouseStockLinker tileWarehouseStockLinker = (TileWarehouseStockLinker) tileentity;
			doPower = tileWarehouseStockLinker.getEqualityHandle();
		}
		return doPower;
	}

	public static void setState(boolean active, World world, BlockPos pos){
		IBlockState state = world.getBlockState(pos);
		TileEntity tile = world.getTileEntity(pos);

		if (active){
			world.setBlockState(pos, state.withProperty(LIT, true));
		}
		else {
			world.setBlockState(pos, state.withProperty(LIT, false));
		}

		if (tile != null)
		{
			tile.validate();
			world.setTileEntity(pos, tile);
		}
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase player, ItemStack stack) {
		super.onBlockPlacedBy(world, pos, state, player, stack);
		if (!world.isRemote && stack.getTagCompound() != null) {
			TileEntity tileentity = world.getTileEntity(pos);
			if (tileentity instanceof TileWarehouseStockLinker) {
				TileWarehouseStockLinker tileWarehouseStockLinker = (TileWarehouseStockLinker) tileentity;
				NBTTagCompound tag = stack.getTagCompound().getCompoundTag(ItemBlockWarehouseStockLinker.WAREHOUSE_POS_TAG);
				tileWarehouseStockLinker.setWarehousePos(NBTUtil.getPosFromTag(tag));
			}
		}
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, FACING, LIT);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(FACING, BlockTools.getHorizontalFacingFromMeta(meta));
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(FACING).ordinal();
	}

	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
		return getDefaultState().withProperty(FACING, placer.getHorizontalFacing()).withProperty(LIT, false);
	}

	@Override
	public RotationType getRotationType() {
		return RotationType.FOUR_WAY;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess world, BlockPos pos, EnumFacing side) {
		return true;
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
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean invertFacing() {
		return true;
	}

	@Nullable
	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
		return NULL_AABB;
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return AABBS.get(state.getValue(FACING));
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		return WorldTools.clickInteractableTileWithHand(world, pos, player, hand);
	}

	@Override
	public boolean eventReceived(IBlockState state, World world, BlockPos pos, int id, int param) {
		super.eventReceived(state, world, pos, id, param);
		return WorldTools.sendClientEventToTile(world, pos, id, param);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerClient() {
		super.registerClient();

		NetworkHandler.registerGui(NetworkHandler.GUI_WAREHOUSE_STOCK_LINKER, GuiWarehouseStockLinker.class);
	}
}
