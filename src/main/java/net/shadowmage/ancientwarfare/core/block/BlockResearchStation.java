package net.shadowmage.ancientwarfare.core.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.core.gui.research.GuiResearchStation;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.tile.TileResearchStation;
import net.shadowmage.ancientwarfare.core.util.WorldTools;

import static net.shadowmage.ancientwarfare.core.render.property.CoreProperties.FACING;

public class BlockResearchStation extends BlockBaseCore {
	public static final String HAS_BOOK_TAG = "has_book";
	public static final PropertyBool HAS_BOOK = PropertyBool.create(HAS_BOOK_TAG);

	public BlockResearchStation() {
		super(Material.WOOD, "research_station");
		setHardness(2.f);
		setDefaultState(blockState.getBaseState().withProperty(HAS_BOOK, false).withProperty(FACING, EnumFacing.NORTH));
		AncientWarfareCore.proxy.addClientRegister(this);
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, FACING, HAS_BOOK);
	}

	public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		return getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite()).withProperty(HAS_BOOK, false);
	}

	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(HAS_BOOK, (meta & 4) != 0).withProperty(FACING, EnumFacing.getHorizontal(meta & 3));
	}

	public int getMetaFromState(IBlockState state) {
		int i = 0;
		i = i | (state.getValue(FACING)).getHorizontalIndex();

		if (state.getValue(HAS_BOOK)) {
			i |= 4;
		}
		return i;
	}

	@Override
	public boolean hasTileEntity(IBlockState state) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileResearchStation();
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		return WorldTools.clickInteractableTileWithHand(world, pos, player, hand);
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

	@Override
	@SideOnly(Side.CLIENT)
	public void registerClient() {
		super.registerClient();

		NetworkHandler.registerGui(NetworkHandler.GUI_RESEARCH_STATION, GuiResearchStation.class);
	}
}