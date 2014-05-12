package net.shadowmage.ancientwarfare.core.inventory;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;

public class InventorySided extends InventoryBasic implements IInventory, ISidedInventory
{

private static final int[] EMTPY_INDICES = new int[0]; 

Map<Integer, List<ItemSlotFilter>> filtersBySlot = new HashMap<Integer, List<ItemSlotFilter>>();
InventorySide[] sideMap = new InventorySide[6];//access side by MC side
Map<InventorySide, int[]> sideIndices = new EnumMap<InventorySide, int[]>(InventorySide.class);
boolean[] extractFlags = new boolean[6];//canIns
boolean[] insertFlags = new boolean[6];

public InventorySided(int size)
    {
    super(size);
    }

public void setAccessibleSlots(InventorySide side, int[] slots, boolean insert, boolean extract)
  {
  sideIndices.put(side, slots);
  insertFlags[side.ordinal()]=insert;
  extractFlags[side.ordinal()]=extract;
  }

public void setAccessibleSide(int mcSide, InventorySide side)
  {
  sideMap[mcSide]=side;
  }

public void setSlotFilters(int slot, List<ItemSlotFilter> filters)
  {
  filtersBySlot.put(slot, filters);
  }

@Override
public int[] getAccessibleSlotsFromSide(int var1)
  {
  InventorySide side = sideMap[var1];
  if(side==null || !sideIndices.containsKey(side))
    {
    return EMTPY_INDICES;
    }
  return sideIndices.get(side);
  }

@Override
public boolean canInsertItem(int slot, ItemStack stack, int side)
  {
  if(isItemValidForSlot(slot, stack))
    {
    InventorySide iside = sideMap[side];
    if(iside!=null)
      {
      return insertFlags[iside.ordinal()];
      }
    }
  return false;
  }

@Override
public boolean canExtractItem(int slot, ItemStack stack, int side)
  {
  InventorySide iside = sideMap[side];
  if(iside!=null)
    {
    return extractFlags[iside.ordinal()];
    }
  return false;
  }

@Override
public boolean isItemValidForSlot(int slot, ItemStack stack)
  {
  List<ItemSlotFilter> filters = filtersBySlot.get(slot);
  if(filters==null || filters.isEmpty()){return true;}
  for(ItemSlotFilter filter : filters)
    {
    if(filter.isItemValid(stack))
      {
      return true;
      }
    }
  return false;
  }

}
