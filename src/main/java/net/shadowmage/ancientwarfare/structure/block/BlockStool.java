package net.shadowmage.ancientwarfare.structure.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.structure.util.BlockStateProperties;
import net.shadowmage.ancientwarfare.structure.util.RotationLimit;
import net.shadowmage.ancientwarfare.structure.util.WoodVariantHelper;

public class BlockStool extends BlockSeat {
	private static final Vec3d SEAT_OFFSET = new Vec3d(0.5, 0.35, 0.5);
	private static final AxisAlignedBB STOOL_AABB = new AxisAlignedBB(3 / 16D, 0D, 3 / 16D, 13 / 16D, 9 / 16D, 13 / 16D);

	public BlockStool() {
		super(Material.WOOD, "stool");
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
	protected Vec3d getSeatOffset() {
		return SEAT_OFFSET;
	}

	@Override
	public RotationLimit getRotationLimit(World world, BlockPos pos, IBlockState state) {
		return RotationLimit.NO_LIMIT;
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return STOOL_AABB;
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
