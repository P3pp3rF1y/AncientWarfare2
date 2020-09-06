package net.shadowmage.ancientwarfare.structure.item;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.util.NBTBuilder;
import net.shadowmage.ancientwarfare.structure.block.BlockCoffin;
import net.shadowmage.ancientwarfare.structure.block.BlockWoodenCoffin;
import net.shadowmage.ancientwarfare.structure.init.AWStructureBlocks;

@SuppressWarnings("squid:MaximumInheritanceDepth")
public class ItemBlockWoodenCoffin extends ItemBlockCoffin {
	private static final String VARIANT_TAG = "variant";

	public ItemBlockWoodenCoffin(Block block) {
		super(block);
	}

	@Override
	public boolean mayPlace(World world, BlockPos pos, EnumFacing sidePlacedOn, EntityPlayer placer) {
		return canPlace(world, pos, sidePlacedOn, placer);
	}

	public static boolean canPlace(World world, BlockPos pos, EnumFacing sidePlacedOn, EntityPlayer placer) {
		return canPlaceHorizontal(world, pos, sidePlacedOn, placer) || canPlaceVertical(world, pos, sidePlacedOn);
	}

	private static boolean canPlaceVertical(World world, BlockPos pos, EnumFacing sidePlacedOn) {
		for (int offset = 0; offset < 3; offset++) {
			if (!mayPlaceAt(world, pos.offset(EnumFacing.UP, offset), sidePlacedOn, offset == 0)) {
				return false;
			}
		}
		return true;
	}

	public static boolean canPlaceHorizontal(World world, BlockPos pos, EnumFacing sidePlacedOn, EntityLivingBase placer) {
		EnumFacing facing = placer.getHorizontalFacing();
		for (int offset = 1; offset < 3; offset++) {
			if (!mayPlaceAt(world, pos.offset(facing, offset), sidePlacedOn, false)) {
				return false;
			}
		}
		return true;
	}

	public static BlockWoodenCoffin.Variant getVariant(ItemStack stack) {
		//noinspection ConstantConditions
		return stack.hasTagCompound() ? BlockWoodenCoffin.Variant.fromName(stack.getTagCompound().getString(VARIANT_TAG)) : BlockWoodenCoffin.Variant.getDefault();
	}

	public static ItemStack getVariantStack(BlockCoffin.IVariant variant) {
		ItemStack stack = new ItemStack(AWStructureBlocks.WOODEN_COFFIN);
		stack.setTagCompound(new NBTBuilder().setString(VARIANT_TAG, variant.getName()).build());
		return stack;
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		if (!stack.hasTagCompound()) {
			return super.getUnlocalizedName(stack);
		}

		//noinspection ConstantConditions
		return String.format("%s.%s", super.getUnlocalizedName(stack), stack.getTagCompound().getString(VARIANT_TAG));
	}
}
