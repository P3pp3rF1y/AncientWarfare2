package net.shadowmage.ancientwarfare.structure.block;

import codechicken.lib.model.ModelRegistryHelper;
import net.minecraft.block.BlockStandingSign;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockFaceShape;
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
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.NonNullList;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.core.util.NBTBuilder;
import net.shadowmage.ancientwarfare.core.util.WorldTools;
import net.shadowmage.ancientwarfare.structure.init.AWStructureBlocks;
import net.shadowmage.ancientwarfare.structure.render.ParticleDummyModel;
import net.shadowmage.ancientwarfare.structure.render.ProtectionFlagRenderer;
import net.shadowmage.ancientwarfare.structure.tile.TileProtectionFlag;

import javax.annotation.Nullable;
import java.util.LinkedHashSet;
import java.util.Set;

public class BlockProtectionFlag extends BlockBaseStructure {
	private static final AxisAlignedBB STANDING_AABB = new AxisAlignedBB(0.25D, 0.0D, 0.25D, 0.75D, 1.0D, 0.75D);
	private static final PropertyInteger ROTATION = PropertyInteger.create("rotation", 0, 15);

	private Set<FlagDefinition> flagDefinitions = new LinkedHashSet<>();

	public BlockProtectionFlag() {
		super(Material.WOOD, "protection_flag");
		setResistance(6000000F);
	}

	@Override
	public float getPlayerRelativeBlockHardness(IBlockState state, EntityPlayer player, World worldIn, BlockPos pos) {
		float original = super.getPlayerRelativeBlockHardness(state, player, worldIn, pos);
		return WorldTools.getTile(worldIn, pos, TileProtectionFlag.class).map(te -> te.getPlayerRelativeBlockHardness(player, original))
				.orElse(original);
	}

	@Nullable
	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
		return NULL_AABB;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isPassable(IBlockAccess worldIn, BlockPos pos) {
		return true;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean canSpawnInBlock() {
		return true;
	}

	@Override
	public boolean hasTileEntity(IBlockState state) {
		return true;
	}

	@Nullable
	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileProtectionFlag();
	}

	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
		return WorldTools.getTile(world, pos, TileProtectionFlag.class)
				.map(TileProtectionFlag::getItemStack).orElse(new ItemStack(AWStructureBlocks.PROTECTION_FLAG));
	}

	@Override
	public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, ItemStack stack) {
		if (te instanceof TileProtectionFlag) {
			spawnAsEntity(worldIn, pos, ((TileProtectionFlag) te).getItemStack());
		} else {
			super.harvestBlock(worldIn, player, pos, state, te, stack);
		}
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
		return BlockFaceShape.UNDEFINED;
	}

	@Override
	public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		drops.add(WorldTools.getTile(world, pos, TileProtectionFlag.class)
				.map(TileProtectionFlag::getItemStack).orElse(new ItemStack(AWStructureBlocks.PROTECTION_FLAG)));
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return STANDING_AABB;
	}

	@Override
	public IBlockState withRotation(IBlockState state, Rotation rot) {
		return state.withProperty(ROTATION, rot.rotate(state.getValue(ROTATION), 16));
	}

	@Override
	public IBlockState withMirror(IBlockState state, Mirror mirrorIn) {
		return state.withProperty(ROTATION, mirrorIn.mirrorRotation(state.getValue(ROTATION), 16));
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, ROTATION);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(ROTATION, meta);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(ROTATION);
	}

	@Override
	public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
		for (FlagDefinition flagDefinition : flagDefinitions) {
			ItemStack stack = new ItemStack(this);
			stack.setTagCompound(new NBTBuilder()
					.setInteger("topColor", flagDefinition.getTopColor())
					.setInteger("bottomColor", flagDefinition.getBottomColor())
					.setString("name", flagDefinition.getName())
					.build());
			items.add(stack);
		}
	}

	@Override
	public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		int rotation = MathHelper.floor((double)((placer.rotationYaw + 180.0F) * 16.0F / 360.0F) + 0.5D) & 15;
		return getDefaultState().withProperty(BlockStandingSign.ROTATION, rotation);
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		WorldTools.getTile(world, pos, TileProtectionFlag.class).ifPresent(te -> te.setFromStack(stack));
	}

	public void addFlagDefinition(FlagDefinition flagDefinition) {
		flagDefinitions.add(flagDefinition);
	}

	public static class FlagDefinition {

		private String name;
		private int topColor;
		private int bottomColor;

		public FlagDefinition(String name, int topColor, int bottomColor) {
			this.name = name;
			this.topColor = topColor;
			this.bottomColor = bottomColor;
		}

		public String getName() {
			return name;
		}

		private int getTopColor() {
			return topColor;
		}

		private int getBottomColor() {
			return bottomColor;
		}
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		WorldTools.getTile(world, pos, TileProtectionFlag.class).ifPresent(te -> te.onActivatedBy(player));
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerClient() {
		//noinspection ConstantConditions
		ModelResourceLocation modelLocation = new ModelResourceLocation(getRegistryName(), "normal");
		ModelRegistryHelper.registerItemRenderer(Item.getItemFromBlock(this), new ProtectionFlagRenderer());
		ModelRegistryHelper.register(modelLocation, ParticleDummyModel.INSTANCE);
		ModelLoader.setCustomStateMapper(this, new StateMapperBase() {
			@Override
			@SideOnly(Side.CLIENT)
			protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
				return modelLocation;
			}
		});

		ClientRegistry.bindTileEntitySpecialRenderer(TileProtectionFlag.class, new ProtectionFlagRenderer());
	}
}
