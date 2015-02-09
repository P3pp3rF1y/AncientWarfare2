package net.shadowmage.ancientwarfare.core.item;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import net.shadowmage.ancientwarfare.core.interfaces.IItemClickable;

public class ItemEventHandler {

    @SubscribeEvent
    public void onItemUse(PlayerInteractEvent evt) {
        ItemStack stack = evt.entityPlayer.getCurrentEquippedItem();
        if (stack != null && stack.getItem() instanceof IItemClickable) {
            IItemClickable clickable = (IItemClickable) stack.getItem();
            if (evt.action == Action.LEFT_CLICK_BLOCK && clickable.cancelLeftClick(evt.entityPlayer, stack)) {
                evt.setCanceled(true);
            } else if ((evt.action == Action.RIGHT_CLICK_AIR || evt.action == Action.RIGHT_CLICK_BLOCK) && clickable.cancelRightClick(evt.entityPlayer, stack)) {
                evt.setCanceled(true);
            }
        }
    }

}
