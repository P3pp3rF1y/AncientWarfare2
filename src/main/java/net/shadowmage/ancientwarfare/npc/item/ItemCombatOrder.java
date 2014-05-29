package net.shadowmage.ancientwarfare.npc.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.npc.orders.CombatOrder;

public class ItemCombatOrder extends ItemOrders
{

public ItemCombatOrder(String name)
  {
  super(name);
  this.setTextureName("ancientwarfare:npc/combat_order");
  }

@Override
public void onRightClick(EntityPlayer player, ItemStack stack)
  {
  NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_NPC_COMBAT_ORDER, 0, 0, 0);
  }

@Override
public void onKeyAction(EntityPlayer player, ItemStack stack)
  {
  CombatOrder order = CombatOrder.getCombatOrder(stack);
  if(order==null){return;}
  if(player.isSneaking())
    {
    order.clearPatrol();
    CombatOrder.writeCombatOrder(stack, order);
    }
  else
    {
    BlockPosition pos = BlockTools.getBlockClickedOn(player, player.worldObj, false);
    if(pos!=null)
      {
      order.addPatrolPoint(player.worldObj, pos);
      CombatOrder.writeCombatOrder(stack, order);
      }    
    }
  }

}
