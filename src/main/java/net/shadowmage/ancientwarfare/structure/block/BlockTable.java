package net.shadowmage.ancientwarfare.structure.block;

import com.google.common.collect.ImmutableList;
import net.minecraft.block.material.Material;
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
import java.util.List;

public class BlockTable extends BlockBaseStructure {
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
		return new BlockStateContainer(this, BlockStateProperties.VARIANT);
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

	private static final List<AxisAlignedBB> AABBs = ImmutableList.of(
			new AxisAlignedBB(0, 14 / 16D, 0, 1, 1, 1),
			new AxisAlignedBB(0, 0, 0, 2 / 16D, 14 / 16D, 2 / 16D),
			new AxisAlignedBB(14 / 16D, 0, 0, 1, 14 / 16D, 2 / 16D),
			new AxisAlignedBB(14 / 16D, 0, 14 / 16D, 1, 14 / 16D, 1),
			new AxisAlignedBB(0, 0, 14 / 16D, 2 / 16D, 14 / 16D, 1));

	@Nullable
	@Override
	public RayTraceResult collisionRayTrace(IBlockState blockState, World worldIn, BlockPos pos, Vec3d start, Vec3d end) {
		return RayTraceUtils.raytraceMultiAABB(AABBs, pos, start, end, (rtr, aabb) -> rtr);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World worldIn, BlockPos pos) {
		EntityPlayerSP player = Minecraft.getMinecraft().player;
		return RayTraceUtils.getSelectedBoundingBox(AABBs, pos, player);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerClient() {
		WoodVariantHelper.registerClient(this);
	}

	@Override
	public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		WoodVariantHelper.getDrops(this, drops, state);
	}
}
