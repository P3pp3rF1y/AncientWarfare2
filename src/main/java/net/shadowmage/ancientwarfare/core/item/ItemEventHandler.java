package net.shadowmage.ancientwarfare.core.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.interfaces.IItemClickable;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.network.PacketItemInteraction;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class ItemEventHandler
{

@SubscribeEvent
public void onItemUse(PlayerInteractEvent evt)
  {
  ItemStack stack = evt.entityPlayer.inventory.getCurrentItem();
  if(stack!=null && stack.getItem() instanceof IItemClickable)
    {
    IItemClickable clickable = (IItemClickable)stack.getItem();
    boolean send = false;
    int type = 1;
    if(evt.action==Action.LEFT_CLICK_BLOCK)
      {
      send = clickable.onLeftClickClient(evt.entityPlayer, stack);
      }
    else if(evt.action==Action.RIGHT_CLICK_AIR)//if you catch click on block event too, it double-triggers events, as click on block is checked before click on air
      {
      send = clickable.onRightClickClient(evt.entityPlayer, stack);          
      type = 2;
      }    
    if(send)
      {
      PacketItemInteraction pkt = new PacketItemInteraction(type);
      NetworkHandler.sendToServer(pkt);
      }    
    evt.setCanceled(true);
    }  
  }

}
