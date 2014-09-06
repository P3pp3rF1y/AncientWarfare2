package net.shadowmage.ancientwarfare.npc.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;


public class ItemTradeOrder extends ItemOrders
{

public ItemTradeOrder(String name)
  {
  super(name);
  this.setTextureName("ancientwarfare:npc/combat_order");
  }

@Override
public boolean onKeyActionClient(EntityPlayer player, ItemStack stack, ItemKey key)
  {
  return key==ItemKey.KEY_0 || key==ItemKey.KEY_1 || key==ItemKey.KEY_2;
  }

@Override
public void onRightClick(EntityPlayer player, ItemStack stack)
  {
  NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_NPC_TRADE_ORDER, 0, 0, 0);
  }

@Override
public void onKeyAction(EntityPlayer player, ItemStack stack, ItemKey key)
  {
  if(key==ItemKey.KEY_0)
    {
    //TODO add trader route point
    }
  else if(key==ItemKey.KEY_1)
    {
    //TODO set restock withdraw point and side
    }
  else if(key==ItemKey.KEY_2)
    {
    //TODO set restock deposit point and side
    }
  }

}
