package net.shadowmage.ancientwarfare.core.interfaces;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface IItemClickable {

    /*
     * called server side if onRightClickClient returns true
     */
    public void onRightClick(EntityPlayer player, ItemStack stack);

    /*
     * called server side if onLeftClickClient returns true
     */
    public void onLeftClick(EntityPlayer player, ItemStack stack);
}
