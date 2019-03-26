package net.shadowmage.ancientwarfare.structure.item;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.item.ItemBlockBase;

public class ItemBlockCoffin extends ItemBlockBase {
	public ItemBlockCoffin(Block block) {
		super(block);
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		IBlockState state = world.getBlockState(pos);
		Block block = state.getBlock();

		if (!block.isReplaceable(world, pos)) {
			pos = pos.offset(facing);
		}

		ItemStack itemstack = player.getHeldItem(hand);

		if (!itemstack.isEmpty() && player.capabilities.allowEdit && mayPlace(world, pos, facing, player)) {
			int i = getMetadata(itemstack.getMetadata());
			IBlockState placementState = this.block.getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, i, player, hand);

			if (placeBlockAt(itemstack, player, world, pos, facing, hitX, hitY, hitZ, placementState)) {
				placementState = world.getBlockState(pos);
				SoundType soundtype = placementState.getBlock().getSoundType(placementState, world, pos, player);
				world.playSound(player, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
				itemstack.shrink(1);
			}

			return EnumActionResult.SUCCESS;
		} else {
			return EnumActionResult.FAIL;
		}
	}

	private boolean mayPlace(World world, BlockPos pos, EnumFacing sidePlacedOn, EntityPlayer placer) {
		return canPlaceHorizontal(world, pos, sidePlacedOn, placer) || canPlaceVertical(world, pos, sidePlacedOn);
	}

	public static boolean canPlaceVertical(World world, BlockPos pos, EnumFacing sidePlacedOn) {
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
			if (!mayPlaceAt(world, pos.offset(facing, offset), sidePlacedOn, offset == 0)) {
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
}
