package net.shadowmage.ancientwarfare.npc.orders;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;
import net.shadowmage.ancientwarfare.npc.item.AWNpcItemLoader;

public class RoutingOrder extends NpcOrders
{

private List<RoutePoint> routingPoints = new ArrayList<RoutePoint>();
int routeDimension;

public RoutingOrder()
  {  
  }

public void removePosition(int index)
  {
  if(index>=0 && index<routingPoints.size())
    {
    routingPoints.remove(index);    
    }
  }

public void incrementPosition(int index)
  {
  if(index>=1 && index<routingPoints.size())
    {
    RoutePoint entry = routingPoints.remove(index);
    routingPoints.add(index-1, entry);
    }
  }

public void decrementPosition(int index)
  {
  if(index>=0 && index<routingPoints.size()-1)
    {
    RoutePoint entry = routingPoints.remove(index);
    routingPoints.add(index+1, entry);
    }
  }

public void addRoutePoint(World world, int x, int y, int z)
  {
  RoutePoint p = new RoutePoint(x, y, z);
  routingPoints.add(p);
  AWLog.logDebug("added route point, list now contains: "+routingPoints);
  }

public void changeRouteType(int index)
  {
  if(index>=0 && index<routingPoints.size())
    {
    RoutePoint entry = routingPoints.get(index);
    entry.changeRouteType();
    }
  }

public void changeBlockSide(int index)
  {
  if(index>=0 && index<routingPoints.size())
    {
    RoutePoint entry = routingPoints.get(index);
    entry.changeBlockSide();
    }
  }

public void toggleIgnoreDamage(int index)
  {
  if(index>=0 && index<routingPoints.size())
    {
    RoutePoint entry = routingPoints.get(index);
    entry.setIgnoreDamage(!entry.getIgnoreDamage());
    }
  }

public void toggleIgnoreTag(int index)
  {
  if(index>=0 && index<routingPoints.size())
    {
    RoutePoint entry = routingPoints.get(index);
    entry.setIgnoreTag(!entry.getIgnoreTag());
    }
  }

@Override
public void readFromNBT(NBTTagCompound tag)
  {
  routingPoints.clear();
  NBTTagList entryList = tag.getTagList("entryList", Constants.NBT.TAG_COMPOUND);
  for(int i = 0; i< entryList.tagCount();i++)
    {
    routingPoints.add(new RoutePoint(entryList.getCompoundTagAt(i)));
    }
  }

@Override
public NBTTagCompound writeToNBT(NBTTagCompound tag)
  {
  NBTTagList list =new NBTTagList();
  for(RoutePoint p : routingPoints)
    {
    list.appendTag(p.writeToNBT(new NBTTagCompound()));    
    }
  tag.setTag("entryList", list);
  return tag;
  }

public static RoutingOrder getRoutingOrder(ItemStack stack)
  {
  if(stack!=null && stack.getItem()==AWNpcItemLoader.routingOrder)
    {
    RoutingOrder order = new RoutingOrder();
    if(stack.hasTagCompound() && stack.getTagCompound().hasKey("orders"))
      {
      order.readFromNBT(stack.getTagCompound().getCompoundTag("orders"));
      }
    return order;
    }
  return null;
  }

public static void writeRoutingOrder(ItemStack stack, RoutingOrder order)
  {
  if(stack!=null && stack.getItem()==AWNpcItemLoader.routingOrder)
    {
    stack.setTagInfo("orders", order.writeToNBT(new NBTTagCompound()));
    }
  }

public static class RoutePoint
{
boolean ignoreDamage, ignoreTag;
RouteType routeType = RouteType.FILL_TARGET_TO;
BlockPosition target = new BlockPosition();
int blockSide = 0;
ItemStack[] filters = new ItemStack[8];

private RoutePoint(NBTTagCompound tag){readFromNBT(tag);}
public RoutePoint(int x, int y, int z){this.target = new BlockPosition(x, y, z);}
private void changeBlockSide(){blockSide = blockSide==5? 0 : blockSide+1;}
private void changeRouteType(){routeType=routeType.next();}
public void setFilter(int index, ItemStack stack){filters[index]=stack;}
public int getBlockSide(){return blockSide;}
public void setBlockSide(int side){blockSide=side;}
public RouteType getRouteType(){return routeType;}
public BlockPosition getTarget(){return target;}
public ItemStack getFilterInSlot(int slot){return filters[slot];}
public boolean getIgnoreDamage(){return ignoreDamage;}
public boolean getIgnoreTag(){return ignoreTag;}
public void setIgnoreDamage(boolean val){ignoreDamage=val;}
public void setIgnoreTag(boolean val){ignoreTag=val;}

private int depositAllItems(ItemStack[] filters, IInventory from, IInventory to, int fromSide, int toSide)
  {
  int moved = 0;
  ItemStack stack;
  ItemStack filter;
  int stackSize = 0;  
  int fromIndices[];
  boolean shouldMove;
  if(from instanceof ISidedInventory && fromSide>=0)
    {
    fromIndices = ((ISidedInventory)from).getAccessibleSlotsFromSide(fromSide);
    }
  else
    {
    fromIndices = InventoryTools.getIndiceArrayForSpread(0, from.getSizeInventory());
    }  
  for(int index : fromIndices)
    {
    shouldMove = false;
    stack = from.getStackInSlot(index);
    if(stack==null){continue;}
    stackSize = stack.stackSize;    
    for(int i = 0; i < filters.length; i++)
      {
      filter = filters[i];
      if(filter==null){continue;}
      if(InventoryTools.doItemStacksMatch(stack, filter, ignoreDamage, ignoreTag))
        {
        shouldMove=true;
        break;
        }
      }
    if(shouldMove)
      {
      stack = InventoryTools.mergeItemStack(to, stack, toSide);
      if(stack==null){from.setInventorySlotContents(index, null);}
      }    
    if(stack==null || stack.stackSize!=stackSize)
      {
      moved++;
      from.markDirty();
      }   
    }  
  return moved;
  }

private int depositAllItemsExcept(ItemStack[] filters, IInventory from, IInventory to, int fromSide, int toSide)
  {
  int moved = 0;
  ItemStack stack;
  ItemStack filter;
  int stackSize = 0;  
  int fromIndices[];
  boolean shouldMove;
  if(from instanceof ISidedInventory && fromSide>=0)
    {
    fromIndices = ((ISidedInventory)from).getAccessibleSlotsFromSide(fromSide);
    }
  else
    {
    fromIndices = InventoryTools.getIndiceArrayForSpread(0, from.getSizeInventory());
    }  
  for(int index : fromIndices)
    {
    shouldMove = true;
    stack = from.getStackInSlot(index);
    if(stack==null){continue;}
    stackSize = stack.stackSize;    
    for(int i = 0; i < filters.length; i++)
      {
      filter = filters[i];
      if(filter==null){continue;}
      if(InventoryTools.doItemStacksMatch(stack, filter, ignoreDamage, ignoreTag))
        {
        shouldMove=false;
        break;
        }
      }
    if(shouldMove)
      {
      stack = InventoryTools.mergeItemStack(to, stack, toSide);
      if(stack==null){from.setInventorySlotContents(index, null);}
      }    
    if(stack==null || stack.stackSize!=stackSize)
      {
      moved++;
      from.markDirty();
      }
    }  
  return moved;
  }

private int fillTo(ItemStack[] filters, IInventory from, IInventory to, int fromSide, int toSide)
  {
  int moved = 0;
  int toMove = 0;
  int foundCount = 0;
  ItemStack filter;
  int m1;
  for(int i = 0; i < filters.length; i++)
    {
    filter = filters[i];
    if(filter==null){continue;}
    foundCount = InventoryTools.getCountOf(to, toSide, filter);    
    toMove = filter.stackSize;
    if(foundCount>toMove){continue;}    
    toMove-=foundCount;
    m1 = InventoryTools.transferItems(from, to, filter, toMove, fromSide, toSide, ignoreDamage, ignoreTag);
    moved += m1/filter.getMaxStackSize();
    }
  return moved;
  }

private final void readFromNBT(NBTTagCompound tag)
  {
  filters = new ItemStack[8];
  routeType = RouteType.values()[tag.getInteger("type")];
  target = new BlockPosition(tag.getCompoundTag("position"));
  blockSide = tag.getInteger("blockSide");
  ignoreDamage = tag.getBoolean("ignoreDamage");
  ignoreTag = tag.getBoolean("ignoreTag");
  NBTTagList filterList = tag.getTagList("filterList", Constants.NBT.TAG_COMPOUND);  
  
  NBTTagCompound itemTag;
  int slot;
  for(int i = 0; i < filterList.tagCount(); i++)
    {
    itemTag = filterList.getCompoundTagAt(i);
    slot = itemTag.getInteger("slot");
    filters[slot] = InventoryTools.readItemStack(itemTag);
    }
  }

private final NBTTagCompound writeToNBT(NBTTagCompound tag)
  {
  tag.setInteger("type", routeType.ordinal());
  tag.setTag("position", target.writeToNBT(new NBTTagCompound()));
  tag.setInteger("blockSide", blockSide);
  tag.setBoolean("ignoreDamage", ignoreDamage);
  tag.setBoolean("ignoreTag", ignoreTag);
  NBTTagList filterList = new NBTTagList();
  NBTTagCompound itemTag;
  for(int i = 0; i < filters.length; i++)
    {
    if(filters[i]==null){continue;}
    itemTag = InventoryTools.writeItemStack(filters[i], new NBTTagCompound());
    itemTag.setInteger("slot", i);
    filterList.appendTag(itemTag);
    }
  tag.setTag("filterList", filterList);
  return tag;
  }

}

public static enum RouteType
{
/**
 * fill target up to the specified quantity from couriers inventory
 */
FILL_TARGET_TO("route.fill.target"),

/**
 * fill courier up to the specified quantity from targets inventory 
 */
FILL_COURIER_TO("route.fill.courier"),

/**
 * deposit any of the specified items from courier into target inventory
 * (no quantity limit)
 */
DEPOSIT_ALL_OF("route.deposit.match"),

/**
 * withdraw any of the specified items from target inventory into courier inventory
 * (no quantity limit)
 */
WITHDRAW_ALL_OF("route.withdraw.match"),

/**
 * deposit all items in courier inventory, except those matching filter items
 */
DEPOSIT_ALL_EXCEPT("route.deposit.no_match"),

/**
 * withdraw all items in target inventory except those matching filters
 */
WITHDRAW_ALL_EXCEPT("route.withdraw.no_match");

final String key;
RouteType(String key){this.key = key;}

public String getTranslationKey(){return key;}

public static RouteType next(RouteType type)
  {
  return type==null ? RouteType.FILL_TARGET_TO : type.next();      
  }

public RouteType next()
  {
  int ordinal = ordinal();
  ordinal++;
  if(ordinal>=RouteType.values().length){ordinal=0;}
  return RouteType.values()[ordinal];
  }


}

/**
 * do the routing action for the courier at the given route-point.  position/distance is not checked, should check in AI before calling<br>
 * returns the number of stacks processed for determining the length the courier should 'work' at the point
 * @param p
 * @param npc
 * @param target
 * @return
 */
public int handleRouteAction(RoutePoint p, IInventory npc, IInventory target)
  {
  int side = p.getBlockSide();
  switch(p.routeType)
  {
  case FILL_COURIER_TO:
  return p.fillTo(p.filters, target, npc, side, -1);
  
  case FILL_TARGET_TO:
  return p.fillTo(p.filters, npc, target, -1, side);
    
  case DEPOSIT_ALL_EXCEPT:
  return p.depositAllItemsExcept(p.filters, npc, target, -1, side);
  
  case DEPOSIT_ALL_OF:  
  return p.depositAllItems(p.filters, npc, target, -1, side);
  
  case WITHDRAW_ALL_EXCEPT:  
  return p.depositAllItemsExcept(p.filters, target, npc, side, -1);
  
  case WITHDRAW_ALL_OF:
  return p.depositAllItems(p.filters, target, npc, side, -1);
  
  default:
  return 0;
  }
  }



public List<RoutePoint> getEntries()
  {
  return routingPoints;
  }

}
