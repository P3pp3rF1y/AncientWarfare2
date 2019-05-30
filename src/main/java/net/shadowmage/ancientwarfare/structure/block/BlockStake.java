package net.shadowmage.ancientwarfare.structure.block;

import com.google.common.collect.ImmutableMap;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.util.ModelLoaderHelper;
import net.shadowmage.ancientwarfare.core.util.WorldTools;
import net.shadowmage.ancientwarfare.structure.gui.GuiStake;
import net.shadowmage.ancientwarfare.structure.render.StakeRenderer;
import net.shadowmage.ancientwarfare.structure.render.property.TopBottomPart;
import net.shadowmage.ancientwarfare.structure.tile.TileStake;

import javax.annotation.Nullable;
import java.util.Map;

import static net.shadowmage.ancientwarfare.core.render.property.CoreProperties.FACING;
import static net.shadowmage.ancientwarfare.core.render.property.CoreProperties.VISIBLE;
import static net.shadowmage.ancientwarfare.structure.render.property.StructureProperties.TOP_BOTTOM_PART;
import static net.shadowmage.ancientwarfare.structure.render.property.TopBottomPart.BOTTOM;
import static net.shadowmage.ancientwarfare.structure.render.property.TopBottomPart.TOP;

public class BlockStake extends BlockBaseStructure {
	private static final PropertyBool ON_FIRE = PropertyBool.create("on_fire");

	public BlockStake() {
		super(Material.WOOD, "stake");
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, FACING, VISIBLE, TOP_BOTTOM_PART, ON_FIRE);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState()
				.withProperty(FACING, EnumFacing.getHorizontal(meta & 3))
				.withProperty(TOP_BOTTOM_PART, TopBottomPart.byMeta((meta >> 2) & 1))
				.withProperty(VISIBLE, ((meta >> 3) & 1) == 1);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(FACING).getHorizontalIndex()
				| state.getValue(TOP_BOTTOM_PART).getMeta() << 2
				| (state.getValue(VISIBLE) ? 1 : 0) << 3;
	}

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
		if (state.getValue(TOP_BOTTOM_PART) == BOTTOM) {
			return state.withProperty(ON_FIRE, WorldTools.getTile(world, pos, TileStake.class).map(TileStake::burns).orElse(false));
		}
		return state.withProperty(ON_FIRE, false);
	}

	@Override
	public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
		TopBottomPart part = state.getValue(TOP_BOTTOM_PART);
		BlockPos tePos = pos;
		if (!state.getValue(VISIBLE)) {
			tePos = pos.down(2);
		} else if (part == TOP) {
			tePos = pos.down();
		}
		return WorldTools.getTile(world, tePos, TileStake.class).map(te -> part == BOTTOM && te.burns() || te.isEntityOnFire()).orElse(false) ? 15 : 0;
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
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return state.getValue(VISIBLE) ? super.getRenderType(state) : EnumBlockRenderType.INVISIBLE;
	}

	@Override
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (state.getValue(TOP_BOTTOM_PART) != BOTTOM) {
			return false;
		}

		if (!world.isRemote) {
			NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_STAKE, pos);
		}
		return true;
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		world.setBlockState(pos.up(2), state.withProperty(FACING, placer.getHorizontalFacing().getOpposite()).withProperty(TOP_BOTTOM_PART, TopBottomPart.TOP).withProperty(VISIBLE, false));
		world.setBlockState(pos.up(), state.withProperty(FACING, placer.getHorizontalFacing().getOpposite()).withProperty(TOP_BOTTOM_PART, TopBottomPart.TOP).withProperty(VISIBLE, true));
		world.setBlockState(pos, state.withProperty(FACING, placer.getHorizontalFacing().getOpposite()).withProperty(TOP_BOTTOM_PART, BOTTOM).withProperty(VISIBLE, true));
	}

	private static final AxisAlignedBB POST_AABB = new AxisAlignedBB(6 / 16D, 0, 6 / 16D, 10 / 16D, 1, 10 / 16D);

	private static final Map<EnumFacing, AxisAlignedBB> MIDDLE_AABB = ImmutableMap.of(
			EnumFacing.NORTH, new AxisAlignedBB(3 / 16D, 0, 0, 13 / 16D, 1, 11 / 16D),
			EnumFacing.SOUTH, new AxisAlignedBB(3 / 16D, 0, 5 / 16D, 13 / 16D, 1, 1),
			EnumFacing.EAST, new AxisAlignedBB(5 / 16D, 0, 3 / 16D, 1, 1, 13 / 16D),
			EnumFacing.WEST, new AxisAlignedBB(0, 0, 3 / 16D, 11 / 16D, 1, 13 / 16D)
	);

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		TopBottomPart part = state.getValue(TOP_BOTTOM_PART);
		if (part == BOTTOM) {
			return super.getBoundingBox(state, source, pos);
		}
		return state.getValue(VISIBLE) ? MIDDLE_AABB.get(state.getValue(FACING)) : POST_AABB;
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		if (state.getValue(TOP_BOTTOM_PART) != BOTTOM) {
			IBlockState currentState = state;
			BlockPos currentPos = pos;
			for (int i = 1; i < 3 && (currentState.getValue(TOP_BOTTOM_PART) != BOTTOM); i++) {
				currentPos = pos.down(i);
				currentState = world.getBlockState(currentPos);
				if (currentState.getBlock() != this) {
					return;
				}
			}
			if (currentState.getValue(TOP_BOTTOM_PART) == BOTTOM) {
				currentState.getBlock().breakBlock(world, currentPos, currentState);
				return;
			}
		}

		world.setBlockToAir(pos.up(2));
		world.setBlockToAir(pos.up());
		world.setBlockToAir(pos);
		super.breakBlock(world, pos, state);
		world.checkLight(pos);
	}

	@Override
	public boolean hasTileEntity(IBlockState state) {
		return state.getValue(TOP_BOTTOM_PART) == BOTTOM;
	}

	@Nullable
	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileStake();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerClient() {
		final ResourceLocation assetLocation = new ResourceLocation(AncientWarfareCore.MOD_ID, "structure/" + getRegistryName().getResourcePath());
		ModelLoader.setCustomStateMapper(this, new StateMapperBase() {
			@Override
			@SideOnly(Side.CLIENT)
			protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
				return new ModelResourceLocation(assetLocation, getPropertyString(state.getProperties()));
			}
		});

		ModelLoaderHelper.registerItem(this, "structure", "inventory");
		ClientRegistry.bindTileEntitySpecialRenderer(TileStake.class, new StakeRenderer());
		NetworkHandler.registerGui(NetworkHandler.GUI_STAKE, GuiStake.class);
	}
}
