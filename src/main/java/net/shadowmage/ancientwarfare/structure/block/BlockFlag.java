package net.shadowmage.ancientwarfare.structure.block;

import net.minecraft.block.BlockStandingSign;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.NonNullList;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.util.NBTBuilder;

import javax.annotation.Nullable;
import java.util.LinkedHashSet;
import java.util.Set;

public class BlockFlag extends BlockBaseStructure {
	protected static final AxisAlignedBB STANDING_AABB = new AxisAlignedBB(0.25D, 0.0D, 0.25D, 0.75D, 1.0D, 0.75D);
	public static final PropertyInteger ROTATION = PropertyInteger.create("rotation", 0, 15);

	private Set<BlockFlag.FlagDefinition> flagDefinitions = new LinkedHashSet<>();

	BlockFlag(Material material, String regname) {
		super(material, regname);
		setSoundType(SoundType.WOOD);
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

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
		return BlockFaceShape.UNDEFINED;
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
		for (BlockFlag.FlagDefinition flagDefinition : flagDefinitions) {
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
		int rotation = MathHelper.floor((double) ((placer.rotationYaw + 180.0F) * 16.0F / 360.0F) + 0.5D) & 15;
		return getDefaultState().withProperty(BlockStandingSign.ROTATION, rotation);
	}

	public void addFlagDefinition(BlockFlag.FlagDefinition flagDefinition) {
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
}
