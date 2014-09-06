package net.shadowmage.ancientwarfare.npc.orders;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.shadowmage.ancientwarfare.npc.item.AWNpcItemLoader;

public class TradeOrder extends NpcOrders
{

public TradeOrder()
  {
  // TODO Auto-generated constructor stub
  }

@Override
public void readFromNBT(NBTTagCompound tag)
  {
  // TODO Auto-generated method stub

  }

@Override
public NBTTagCompound writeToNBT(NBTTagCompound tag)
  {
  // TODO Auto-generated method stub
  return tag;
  }

public static TradeOrder getTradeOrder(ItemStack stack)
  {
  if(stack!=null && stack.getItem()==AWNpcItemLoader.combatOrder)
    {
    TradeOrder order = new TradeOrder();
    if(stack.hasTagCompound() && stack.getTagCompound().hasKey("orders"))
      {
      order.readFromNBT(stack.getTagCompound().getCompoundTag("orders"));
      }
    return order;
    }
  return null;
  }

public static void writeTradeOrder(ItemStack stack, TradeOrder order)
  {
  if(stack!=null && stack.getItem()==AWNpcItemLoader.combatOrder)
    {
    stack.setTagInfo("orders", order.writeToNBT(new NBTTagCompound()));
    }
  }

}
