package net.shadowmage.ancientwarfare.npc.orders;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;
import net.shadowmage.ancientwarfare.npc.item.AWNpcItemLoader;
import net.shadowmage.ancientwarfare.npc.trade.POTradeList;

public class TradeOrder extends NpcOrders
{

private TradePointRoute tradeRoute = new TradePointRoute(); 
private TradeRestockEntry restockEntry = new TradeRestockEntry();
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

public TradePointRoute getRoute(){return tradeRoute;}

public TradeRestockEntry getRestockData(){return restockEntry;}

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

public static class TradeRestockEntry
{
private BlockPosition withdrawPoint;
private int withdrawSide;
private BlockPosition depositPoint;
private int depositSide;
private List<ItemStack> withdrawList = new ArrayList<ItemStack>();

public BlockPosition getDepositPoint(){return depositPoint;}

public BlockPosition getWithdrawPoint(){return withdrawPoint;}

public int getDepositSide(){return depositSide;}

public int getWithdrawSide(){return withdrawSide;}

public void deleteDepositPoint(){depositPoint=null;}

public void deleteWithdrawPoint(){withdrawPoint=null;}

public void setDepositPoint(BlockPosition pos, int side)
  {
  depositPoint = pos;
  depositSide = side;
  }

public void setWithdrawPoint(BlockPosition pos, int side)
  {
  withdrawPoint = pos;
  withdrawSide = side;
  }

public void readFromNBT(NBTTagCompound tag)
  {
  if(tag.hasKey("withdrawPoint"))
    {
    withdrawPoint = new BlockPosition(tag.getCompoundTag("withdrawPoint"));
    withdrawSide = tag.getInteger("withdrawSide");    
    }
  if(tag.hasKey("depositPoint"))
    {
    depositPoint = new BlockPosition(tag.getCompoundTag("depositPoint"));
    depositSide = tag.getInteger("depositSide");
    }
  NBTTagList refilList = tag.getTagList("withdrawList", Constants.NBT.TAG_COMPOUND);
  for(int i = 0; i< refilList.tagCount(); i++)
    {
    withdrawList.add(InventoryTools.readItemStack(refilList.getCompoundTagAt(i)));
    }
  }

public NBTTagCompound writeToNBT(NBTTagCompound tag)
  {
  if(withdrawPoint!=null)
    {
    tag.setTag("withdrawPoint", withdrawPoint.writeToNBT(new NBTTagCompound()));
    tag.setInteger("withdrawSide", withdrawSide);
    }
  if(depositPoint!=null)
    {
    tag.setTag("depositPoint", depositPoint.writeToNBT(new NBTTagCompound()));
    tag.setInteger("depositSide", depositSide);
    }
  NBTTagList list = new NBTTagList();
  for(int i = 0; i < withdrawList.size(); i++)
    {
    list.appendTag(InventoryTools.writeItemStack(withdrawList.get(i), new NBTTagCompound()));
    }
  tag.setTag("withdrawList", list);
  return tag;
  }
}

public static class TradePoint
{
private BlockPosition position = new BlockPosition();
private int delay;
private boolean shouldUpkeep;//if the npc should refill upkeep at this stop

public BlockPosition getPosition(){return position;}

public int getDelay(){return delay;}

public void setDelay(int delay){this.delay = delay;}

public void setShouldUpkeep(boolean val){this.shouldUpkeep =val;}

public boolean shouldUpkeep(){return shouldUpkeep;}

public void readFromNBT(NBTTagCompound tag)
  {
  position = new BlockPosition(tag.getCompoundTag("pos"));
  delay = tag.getInteger("delay");
  shouldUpkeep = tag.getBoolean("upkeep");
  }

public NBTTagCompound writeToNBT(NBTTagCompound tag)
  {
  tag.setTag("pos", position.writeToNBT(new NBTTagCompound()));
  tag.setInteger("delay", delay);
  tag.setBoolean("upkeep", shouldUpkeep);
  return tag;
  }
}

public static class TradePointRoute
{

private List<TradePoint> route = new ArrayList<TradePoint>();

public int size(){return route.size();}

public TradePoint get(int index){return route.get(index);}

public void decrementRoutePoint(int index)
  {
  if(index<=0 || index>=route.size()){return;}
  TradePoint p = route.remove(index);
  route.add(index-1, p);
  }

public void incrementRoutePoint(int index)
  {
  if(index<0 || index>=route.size()-1){return;}
  TradePoint p = route.remove(index);
  route.add(index+1, p);  
  }

public void deleteRoutePoint(int index)
  {
  if(index<0 || index>=route.size()){return;}
  route.remove(index);
  }

public void addRoutePoint(BlockPosition pos)
  {
  TradePoint p = new TradePoint();
  p.position = pos;
  p.delay = 20*60*1;
  p.shouldUpkeep = false;
  route.add(p);
  }

public void setPointDelay(int index, int delay)
  {
  route.get(index).setDelay(delay);
  }

public void setUpkeep(int index, boolean val)
  {
  route.get(index).setShouldUpkeep(val);
  }

public void readFromNBT(NBTTagCompound tag)
  {
  route.clear();
  NBTTagList list = tag.getTagList("route", Constants.NBT.TAG_COMPOUND);
  TradePoint p;
  for(int i = 0; i < list.tagCount(); i++)
    {
    p = new TradePoint();
    p.readFromNBT(list.getCompoundTagAt(i));
    route.add(p);
    }
  }

public NBTTagCompound writeToNBT(NBTTagCompound tag)
  {
  NBTTagList list = new NBTTagList();
  for(int i = 0; i< route.size(); i++)
    {
    list.appendTag(route.get(i).writeToNBT(new NBTTagCompound()));
    }
  tag.setTag("route", list);
  return tag;
  }
}

}
