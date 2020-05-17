package net.shadowmage.ancientwarfare.structure.block;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
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
import net.shadowmage.ancientwarfare.core.util.InventoryTools;
import net.shadowmage.ancientwarfare.core.util.WorldTools;
import net.shadowmage.ancientwarfare.structure.item.ItemBlockGravestone;
import net.shadowmage.ancientwarfare.structure.render.RenderLootInfo;
import net.shadowmage.ancientwarfare.structure.tile.TileGravestone;

import javax.annotation.Nullable;

import static net.shadowmage.ancientwarfare.core.render.property.CoreProperties.FACING;

public class BlockGravestone extends BlockBaseStructure {
	private static final AxisAlignedBB AABB_NORTH = new AxisAlignedBB(0, 0, 6 / 16D, 1, 19 / 16D, 10 / 16D);
	private static final AxisAlignedBB AABB_SOUTH = new AxisAlignedBB(0, 0, 6 / 16D, 1, 19 / 16D, 10 / 16D);
	private static final AxisAlignedBB AABB_WEST = new AxisAlignedBB(6 / 16D, 0, 0, 10 / 16D, 19 / 16D, 1D);
	private static final AxisAlignedBB AABB_EAST = new AxisAlignedBB(6 / 16D, 0, 0, 10 / 16D, 19 / 16D, 1D);

	private static final PropertyInteger VARIANT = PropertyInteger.create("variant", 1, 8);

	public BlockGravestone() {
		super(Material.ROCK, "gravestone");
		setHardness(4F);
		setSoundType(SoundType.STONE);
	}

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
		return state.withProperty(VARIANT, WorldTools.getTile(world, pos, TileGravestone.class).map(TileGravestone::getVariant).orElse(1));
	}

	@Override
	public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
		for (int variant = 1; variant <= 8; variant++) {
			items.add(ItemBlockGravestone.getVariantStack(variant));
		}
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, VARIANT, FACING);
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
	public boolean hasTileEntity(IBlockState state) {
		return true;
	}

	@Nullable
	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileGravestone();
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		if (!stack.hasTagCompound()) {
			return;
		}

		world.setBlockState(pos, state.withProperty(VARIANT, ItemBlockGravestone.getVariant(stack)));
		WorldTools.getTile(world, pos, TileGravestone.class).ifPresent(te -> te.setPrimaryFacing(placer.getHorizontalFacing().getOpposite()));
		WorldTools.getTile(world, pos, TileGravestone.class).ifPresent(te -> te.setVariant(ItemBlockGravestone.getVariant(stack)));
		super.onBlockPlacedBy(world, pos, state, placer, stack);
	}

	@Override
	public void onBlockHarvested(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
		if (player.capabilities.isCreativeMode) {
			return;
		}
		WorldTools.getTile(world, pos, TileGravestone.class)
				.ifPresent(te -> InventoryTools.dropItemInWorld(world, ItemBlockGravestone.getVariantStack(te.getVariant()), pos));
	}

	@Override
	public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		//drops handled in onBlockHarvested
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		WorldTools.getTile(world, pos, TileGravestone.class).ifPresent(te -> te.activate(player)); // only runestones can be activated
		return true;
	}

	@Override
	public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		return getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite()).withProperty(VARIANT, ItemBlockGravestone.getVariant(placer.getActiveItemStack()));
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		switch (state.getValue(FACING)) {
			case NORTH:
				return AABB_NORTH;
			case SOUTH:
				return AABB_SOUTH;
			case WEST:
				return AABB_WEST;
			case EAST:
				return AABB_EAST;
		}
		return new AxisAlignedBB(0, 0, 6 / 16D, 1, 19 / 16D, 10 / 16D);
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

	@SideOnly(Side.CLIENT)
	public void registerClient() {
		//noinspection ConstantConditions
		ResourceLocation baseLocation = new ResourceLocation(AncientWarfareCore.MOD_ID, "structure/" + getRegistryName().getResourcePath());

		ModelLoader.setCustomStateMapper(this, new StateMapperBase() {
			@Override
			@SideOnly(Side.CLIENT)
			protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
				return new ModelResourceLocation(baseLocation, getPropertyString(state.getProperties()));
			}
		});

		String modelPropString = "facing=west,variant=%d";

		ModelLoader.setCustomMeshDefinition(Item.getItemFromBlock(this), stack -> {
			if (!stack.hasTagCompound()) {
				return new ModelResourceLocation(baseLocation, String.format(modelPropString, 8));
			}

			return new ModelResourceLocation(baseLocation, String.format(modelPropString, ItemBlockGravestone.getVariant(stack)));
		});

		for (int variant = 1; variant < 9; variant++) {
			ModelLoader.registerItemVariants(Item.getItemFromBlock(this),
					new ModelResourceLocation(baseLocation, String.format(modelPropString, variant)));
		}

		ClientRegistry.bindTileEntitySpecialRenderer(TileGravestone.class, new RenderLootInfo<>());
	}

	public void spawnPotionParticle() { // TODO: if we want the runestones to spawn some particles
	}
}