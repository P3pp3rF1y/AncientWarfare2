package net.shadowmage.ancientwarfare.npc.orders;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
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

private static class TradeRestockEntry
{
BlockPosition withdrawPoint;
int withdrawSide;
BlockPosition depositPoint;
int depositSide;

public void readFromNBT(NBTTagCompound tag)
  {
  // TODO Auto-generated method stub
  }

public NBTTagCompound writeToNBT(NBTTagCompound tag)
  {
  // TODO Auto-generated method stub
  return tag;
  }
}

private static class TradeEntry
{
List<ItemStack> input = new ArrayList<ItemStack>();
List<ItemStack> output = new ArrayList<ItemStack>();

public void readFromNBT(NBTTagCompound tag)
  {
  // TODO Auto-generated method stub
  }

public NBTTagCompound writeToNBT(NBTTagCompound tag)
  {
  // TODO Auto-generated method stub
  return tag;
  }
}

private static class TradePoint
{
BlockPosition position;
int delay;

public void readFromNBT(NBTTagCompound tag)
  {
  // TODO Auto-generated method stub
  }

public NBTTagCompound writeToNBT(NBTTagCompound tag)
  {
  // TODO Auto-generated method stub
  return tag;
  }
}

}
