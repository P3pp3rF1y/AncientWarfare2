package net.shadowmage.ancientwarfare.structure.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.util.WorldTools;
import net.shadowmage.ancientwarfare.structure.gui.GuiLootBasket;
import net.shadowmage.ancientwarfare.structure.tile.TileLootBasket;

import javax.annotation.Nullable;
import java.util.Optional;

import static net.shadowmage.ancientwarfare.core.render.property.CoreProperties.FACING;

public class BlockLootBasket extends BlockBaseStructure {
	private static final AxisAlignedBB SINGLE_SOUTH_NORTH = new AxisAlignedBB(0D, 0D, 1 / 16D, 1D, 12 / 16D, 15 / 16D);
	private static final AxisAlignedBB SINGLE_WEST_EAST = new AxisAlignedBB(1 / 16D, 0D, 0D, 15 / 16D, 12 / 16D, 1D);
	private static final AxisAlignedBB DOUBLE_NORTH = new AxisAlignedBB(0D, 0D, 0D, 1D, 12 / 16D, 13 / 16D);
	private static final AxisAlignedBB DOUBLE_SOUTH = new AxisAlignedBB(0D, 0D, 3 / 16D, 1D, 12 / 16D, 1D);
	private static final AxisAlignedBB DOUBLE_WEST = new AxisAlignedBB(0D, 0D, 0D, 13 / 16D, 12 / 16D, 1D);
	private static final AxisAlignedBB DOUBLE_EAST = new AxisAlignedBB(3 / 16D, 0D, 0D, 1D, 12 / 16D, 1D);

	private static final PropertyBool DOUBLE = PropertyBool.create("double");
	private static final PropertyBool VISIBLE = PropertyBool.create("visible");

	public BlockLootBasket() {
		super(Material.GRASS, "loot_basket");
		setHardness(2);
		setHarvestLevel("axe", 0);
	}

	@Nullable
	@Override
	public String getHarvestTool(IBlockState state) {
		return super.getHarvestTool(state);
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, FACING, DOUBLE, VISIBLE);
	}

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
		return getDoubleDirection(worldIn, pos)
				.map(facing -> facing == EnumFacing.NORTH || facing == EnumFacing.WEST ?
						state.withProperty(DOUBLE, false).withProperty(VISIBLE, false) :
						state.withProperty(FACING, facing.rotateY()).withProperty(DOUBLE, true))
				.orElse(state.withProperty(DOUBLE, false));
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

	@Override
	public boolean hasTileEntity(IBlockState state) {
		return true;
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		if (source.getBlockState(pos.north()).getBlock() == this) {
			return DOUBLE_NORTH;
		} else if (source.getBlockState(pos.south()).getBlock() == this) {
			return DOUBLE_SOUTH;
		} else if (source.getBlockState(pos.west()).getBlock() == this) {
			return DOUBLE_WEST;
		} else if (source.getBlockState(pos.east()).getBlock() == this) {
			return DOUBLE_EAST;
		}
		return state.getValue(FACING).getAxis() == EnumFacing.Axis.X ? SINGLE_WEST_EAST : SINGLE_SOUTH_NORTH;
	}

	@Nullable
	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileLootBasket();
	}

	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
		return getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
	}

	@Override
	public boolean canPlaceBlockAt(World world, BlockPos pos) {
		int basketsAround = 0;
		for (EnumFacing facing : EnumFacing.HORIZONTALS) {
			BlockPos offsetPos = pos.offset(facing);
			if (world.getBlockState(offsetPos).getBlock() == this) {
				if (isDoubleBasket(world, offsetPos)) {
					return false;
				}
				basketsAround++;
				if (basketsAround > 1) {
					return false;
				}
			}
		}

		return true;
	}

	private Optional<EnumFacing> getDoubleDirection(IBlockAccess world, BlockPos pos) {
		for (EnumFacing facing : EnumFacing.HORIZONTALS) {
			if (world.getBlockState(pos.offset(facing)).getBlock() == this) {
				return Optional.of(facing);
			}
		}
		return Optional.empty();
	}

	private boolean isDoubleBasket(World world, BlockPos pos) {
		return getDoubleDirection(world, pos).isPresent();
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (!world.isRemote) {
			if (WorldTools.getTile(world, pos, TileLootBasket.class).map(te -> te.fillWithLootAndCheckIfGoodToOpen(player)).orElse(false)) {
				NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_LOOT_BASKET, pos);
			}
		}
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerClient() {
		super.registerClient();

		NetworkHandler.registerGui(NetworkHandler.GUI_LOOT_BASKET, GuiLootBasket.class);
	}
}
