package net.shadowmage.ancientwarfare.core.input;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface IScrollableItem {
	boolean onScrollUp(World world, EntityPlayer player, ItemStack stack);
	boolean onScrollDown(World world, EntityPlayer player, ItemStack stack);
}
