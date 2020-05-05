package net.shadowmage.ancientwarfare.structure.item;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.item.ItemBlockBase;
import net.shadowmage.ancientwarfare.core.util.NBTBuilder;
import net.shadowmage.ancientwarfare.structure.block.BlockFirePit;
import net.shadowmage.ancientwarfare.structure.init.AWStructureBlocks;
import net.shadowmage.ancientwarfare.structure.util.MultiBlockHelper;

public class ItemBlockStoneCoffin extends ItemBlockBase {
	private static final String VARIANT_TAG = "variant";

	public ItemBlockStoneCoffin(Block block) {
		super(block);
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		return MultiBlockHelper.onMultiBlockItemUse(this, player, world, pos, hand, facing, hitX, hitY, hitZ, this::mayPlace);
	}

	private boolean mayPlace(World world, BlockPos pos, EnumFacing sidePlacedOn, EntityPlayer placer) {
		EnumFacing facing = placer.getHorizontalFacing();
		for (int offset = 1; offset < 4; offset++) {
			if (!mayPlaceAt(world, pos.offset(facing, offset), sidePlacedOn, offset == 0)) {
				return false;
			}
			if (!mayPlaceAt(world, pos.offset(facing.rotateYCCW(), 1).offset(facing, offset), sidePlacedOn, offset == 0)) {
				return false;
			}
		}
		return true;
	}

	private static boolean mayPlaceAt(World world, BlockPos pos, EnumFacing sidePlacedOn, boolean checkSide) {
		IBlockState state = world.getBlockState(pos);
		AxisAlignedBB axisalignedbb = state.getBlock().getDefaultState().getCollisionBoundingBox(world, pos);

		if (axisalignedbb != Block.NULL_AABB && !world.checkNoEntityCollision(axisalignedbb.offset(pos), null)) {
			return false;
		} else if (state.getMaterial() == Material.CIRCUITS && state.getBlock() == Blocks.ANVIL) {
			return true;
		} else {
			return state.getBlock().isReplaceable(world, pos) && (!checkSide || world.getBlockState(pos).getBlock().canPlaceBlockOnSide(world, pos, sidePlacedOn));
		}
	}

	public static int getVariant(ItemStack stack) {
		//noinspection ConstantConditions
		return stack.hasTagCompound() ? stack.getTagCompound().getInteger("variant") : 1;
	}

	public static ItemStack getVariantStack(int variant) {
		ItemStack stack = new ItemStack(AWStructureBlocks.STONE_COFFIN);
		stack.setTagCompound(new NBTBuilder().setInteger("variant", variant).build());
		return stack;
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		if (!stack.hasTagCompound()) {
			return super.getUnlocalizedName(stack);
		}

		//noinspection ConstantConditions
		return String.format("%s.%d", super.getUnlocalizedName(stack),
				stack.getTagCompound().getInteger(VARIANT_TAG));
	}

}
