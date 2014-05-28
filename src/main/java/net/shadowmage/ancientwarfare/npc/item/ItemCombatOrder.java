package net.shadowmage.ancientwarfare.npc.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;

public class ItemCombatOrder extends ItemOrders
{

public ItemCombatOrder(String name)
  {
  super(name);
  // TODO Auto-generated constructor stub
  }

@Override
public void onRightClick(EntityPlayer player, ItemStack stack)
  {
  NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_NPC_COMBAT_ORDER, 0, 0, 0);
  }

@Override
public void onKeyAction(EntityPlayer player, ItemStack stack)
  {
//  CombatOrder order;
  // TODO Auto-generated method stub

  }

}
