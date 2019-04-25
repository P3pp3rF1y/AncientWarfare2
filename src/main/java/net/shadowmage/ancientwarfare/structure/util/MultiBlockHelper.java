package net.shadowmage.ancientwarfare.structure.util;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MultiBlockHelper {
	private MultiBlockHelper() {}

	public static EnumActionResult onMultiBlockItemUse(ItemBlock itemBlock, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ, IPlaceChecker placeChecker) {
		IBlockState state = world.getBlockState(pos);
		Block block = state.getBlock();

		if (!block.isReplaceable(world, pos)) {
			pos = pos.offset(facing);
		}

		ItemStack itemstack = player.getHeldItem(hand);

		if (!itemstack.isEmpty() && player.capabilities.allowEdit && placeChecker.mayPlace(world, pos, facing, player)) {
			int i = itemBlock.getMetadata(itemstack.getMetadata());
			IBlockState placementState = itemBlock.getBlock().getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, i, player, hand);

			if (itemBlock.placeBlockAt(itemstack, player, world, pos, facing, hitX, hitY, hitZ, placementState)) {
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

	public interface IPlaceChecker {
		boolean mayPlace(World world, BlockPos pos, EnumFacing sidePlacedOn, EntityPlayer placer);
	}
}
