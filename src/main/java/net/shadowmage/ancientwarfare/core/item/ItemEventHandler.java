package net.shadowmage.ancientwarfare.core.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class ItemEventHandler
{

@SubscribeEvent
public void onItemUse(PlayerInteractEvent evt)
  {
  if(evt.action==Action.LEFT_CLICK_BLOCK)
    {
    EntityPlayer player = evt.entityPlayer;
    ItemStack stack = evt.entityPlayer.inventory.getCurrentItem();
    
    if(stack==null || !(stack.getItem() instanceof ItemClickable))
      {
      return;
      }
    ItemClickable item = (ItemClickable) stack.getItem();
    if(item.hasLeftClick)
      {
      evt.setCanceled(true);  
      if(!player.worldObj.isRemote)
        {
        item.onLeftClick(stack, player, item.getMovingObjectPositionFromPlayer(player.worldObj, player, true));        
        }    
      }
    }
  else if(evt.action==Action.RIGHT_CLICK_AIR || evt.action==Action.RIGHT_CLICK_BLOCK)
    {
    EntityPlayer player = evt.entityPlayer;
    ItemStack stack = evt.entityPlayer.inventory.getCurrentItem();
    
    if(stack==null || !(stack.getItem() instanceof ItemClickable))
      {
      return;
      }
    ItemClickable item = (ItemClickable) stack.getItem();
    if(!player.worldObj.isRemote)
      {
      item.onRightClick(stack, player, item.getMovingObjectPositionFromPlayer(player.worldObj, player, true));
      evt.setCanceled(true);
      }
    }
  }

}
