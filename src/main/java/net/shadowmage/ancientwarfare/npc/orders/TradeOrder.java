package net.shadowmage.ancientwarfare.npc.orders;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.shadowmage.ancientwarfare.npc.item.AWNpcItemLoader;
import net.shadowmage.ancientwarfare.npc.trade.POTradeList;
import net.shadowmage.ancientwarfare.npc.trade.POTradeRestockData;
import net.shadowmage.ancientwarfare.npc.trade.POTradeRoute;

public class TradeOrder extends NpcOrders
{

private POTradeRoute tradeRoute = new POTradeRoute(); 
private POTradeRestockData restockEntry = new POTradeRestockData();
private POTradeList tradeList = new POTradeList();

public TradeOrder(){}

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

public POTradeList getTradeList(){return tradeList;}

public POTradeRoute getRoute(){return tradeRoute;}

public POTradeRestockData getRestockData(){return restockEntry;}

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

}
