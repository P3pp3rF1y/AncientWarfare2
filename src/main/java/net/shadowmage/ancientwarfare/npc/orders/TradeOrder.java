package net.shadowmage.ancientwarfare.npc.orders;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.npc.item.AWNpcItemLoader;
import net.shadowmage.ancientwarfare.npc.trade.POTradeList;

public class TradeOrder extends NpcOrders
{

TradePointRoute tradeRoute = new TradePointRoute(); 
TradeRestockEntry restockEntry = new TradeRestockEntry();
POTradeList tradeList = new POTradeList();

public TradeOrder()
  {
  // TODO Auto-generated constructor stub
  }

@Override
public void readFromNBT(NBTTagCompound tag)
  {
  tradeList.readFromNBT(tag.getCompoundTag("tradeList"));
  tradeRoute.readFromNBT(tag.getCompoundTag("tradeRoute"));
  restockEntry.readFromNBT(tag.getCompoundTag("restockEntry"));
  }

@Override
public NBTTagCompound writeToNBT(NBTTagCompound tag)
  {
  tag.setTag("tradeList", tradeList.writeToNBT(new NBTTagCompound()));
  tag.setTag("tradeRoute", tradeRoute.writeToNBT(new NBTTagCompound()));
  tag.setTag("restockEntry", restockEntry.writeToNBT(new NBTTagCompound()));
  return tag;
  }

public POTradeList getTradeList(){return tradeList;}

public static TradeOrder getTradeOrder(ItemStack stack)
  {
  if(stack!=null && stack.getItem()==AWNpcItemLoader.tradeOrder)
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
  if(stack!=null && stack.getItem()==AWNpcItemLoader.tradeOrder)
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

private static class TradePointRoute
{
private List<TradePoint> route = new ArrayList<TradePoint>();
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
