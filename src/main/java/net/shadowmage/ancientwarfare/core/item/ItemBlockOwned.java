package net.shadowmage.ancientwarfare.core.item;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.owner.IOwnable;
import net.shadowmage.ancientwarfare.core.util.WorldTools;

public class ItemBlockOwned extends ItemBlockBase {

	public ItemBlockOwned(Block block) {
		super(block);
	}

	@Override
	public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState) {
		boolean val = super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, newState);
		if (val) {
			WorldTools.getTile(world, pos, IOwnable.class).ifPresent(t -> t.setOwner(player));
		}
		return val;
	}

}
