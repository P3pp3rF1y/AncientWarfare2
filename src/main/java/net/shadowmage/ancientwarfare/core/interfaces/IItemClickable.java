package net.shadowmage.ancientwarfare.core.interfaces;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface IItemClickable {

    /**
     * called on client before activation on server.<br>
     * do any client-side pre-processing/validation here.<br>
     * must return true for onRightClick to be called on server,<br>
     * else no packet will be sent, and action is effectively<br>
     * client-side only
     *
     * @return true to send activation packet to server
     */
    public boolean onRightClickClient(EntityPlayer player, ItemStack stack);

    /**
     * Should the left-click action be cancelled (not trigger vanilla interaction)?
     */
    public boolean cancelRightClick(EntityPlayer player, ItemStack stack);

    /**
     * called server side if onRightClickClient returns true
     */
    public void onRightClick(EntityPlayer player, ItemStack stack);

    /**
     * called on client before activation on server.<br>
     * do any client-side pre-processing/validation here.<br>
     * must return true for onLeftClick to be called on server,<br>
     * else no packet will be sent, and action is effectively<br>
     * client-side only
     *
     * @return true to send activation packet to server
     */
    public boolean onLeftClickClient(EntityPlayer player, ItemStack stack);

    /**
     * Should the left-click action be cancelled (not trigger vanilla interaction)?
     */
    public boolean cancelLeftClick(EntityPlayer player, ItemStack stack);

    /**
     * called server side if onLeftClickClient returns true
     */
    public void onLeftClick(EntityPlayer player, ItemStack stack);
}
