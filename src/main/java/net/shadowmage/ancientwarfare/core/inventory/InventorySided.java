package net.shadowmage.ancientwarfare.core.inventory;

import java.util.HashMap;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.Constants;
import net.shadowmage.ancientwarfare.core.block.RelativeSide;

/**
 * re-mappable sided inventory.<br>
 * @author Shadowmage
 *
 */
public class InventorySided implements IInventorySaveable, ISidedInventory
{

TileEntity te;
private ItemStack[] inventorySlots;
private boolean isDirty;

/**
 * stores the mapping of base side to inventory side accessed from that side
 */
private HashMap<RelativeSide, RelativeSide> sideInventoryAccess = new HashMap<RelativeSide, RelativeSide>();

/**
 * stores the mapping of base side to accessible slots
 */
private HashMap<RelativeSide, SideAccessibilityMap> accessMap = new HashMap<RelativeSide, SideAccessibilityMap>();

public InventorySided(int size, TileEntity te)
  {
  this.te = te;
  inventorySlots = new ItemStack[size];
  RelativeSide side;
  for(int i = 0 ; i < 6; i++)
    {
    side = RelativeSide.values()[i];
    accessMap.put(side, new SideAccessibilityMap(side));
    sideInventoryAccess.put(side, side);
    }
  }

/**
 * remove a slot from being accessible from a specific block side
 * @param blockSide
 * @param slot
 */
public void removeSideMapping(RelativeSide blockSide, int slot)
  {
  accessMap.get(blockSide).removeMapping(slot);
  }

/**
 * adds a BASE mapping for the slot/side
 * @param side
 * @param slot
 * @param insert
 * @param extract
 */
public void addSidedMapping(RelativeSide side, int slot, boolean insert, boolean extract)
  {
  accessMap.get(side).addMapping(slot, insert, extract);
  }

@Override
public int[] getAccessibleSlotsFromSide(int mcSide)
  {  
  return accessMap.get(getAccessSideFor(mcSide, te.getBlockMetadata())).accessibleSlots;
  }

@Override
public boolean canInsertItem(int var1, ItemStack var2, int var3)
  {  
  return accessMap.get(getAccessSideFor(var1, te.getBlockMetadata())).canInsert(var2, var3);
  }

@Override
public boolean canExtractItem(int var1, ItemStack var2, int var3)
  {
  return accessMap.get(getAccessSideFor(var1, te.getBlockMetadata())).canExtract(var2, var3);
  }

@Override
public int getSizeInventory()
  {
  return inventorySlots.length;
  }

@Override
public ItemStack getStackInSlot(int var1)
  {
  return inventorySlots[var1];
  }

@Override
public ItemStack decrStackSize(int slotIndex, int amount)
  {
  ItemStack slotStack = inventorySlots[slotIndex];
  if(slotStack!=null)
    {
    if(amount>slotStack.stackSize){amount = slotStack.stackSize;}
    if(amount>slotStack.getMaxStackSize()){amount = slotStack.getMaxStackSize();}
    ItemStack returnStack = slotStack.copy();
    slotStack.stackSize-=amount;
    returnStack.stackSize = amount;    
    return returnStack;
    }
  return null;
  }

@Override
public ItemStack getStackInSlotOnClosing(int var1)
  {
  ItemStack slotStack = inventorySlots[var1];
  inventorySlots[var1] = null;
  return slotStack;
  }

@Override
public void setInventorySlotContents(int var1, ItemStack var2)
  {
  inventorySlots[var1] = var2;
  }

@Override
public String getInventoryName()
  {
  return "AW.InventorySided";
  }

@Override
public boolean hasCustomInventoryName()
  {
  return false;
  }

@Override
public int getInventoryStackLimit()
  {
  return 64;
  }

@Override
public void markDirty()
  {
  this.isDirty = true;
  }

@Override
public boolean isUseableByPlayer(EntityPlayer var1)
  {
  return true;
  }

@Override
public void openInventory()
  {

  }

@Override
public void closeInventory()
  {

  }

@Override
public boolean isItemValidForSlot(int var1, ItemStack var2)
  {
  return true;
  }

@Override
public void readFromNBT(NBTTagCompound tag)
  {
  NBTTagList itemList = tag.getTagList("itemList", Constants.NBT.TAG_COMPOUND);  
  NBTTagCompound itemTag;  
  ItemStack item;
  int slot;
  for(int i = 0; i < itemList.tagCount(); i++)
    {
    itemTag = itemList.getCompoundTagAt(i);
    slot = itemTag.getShort("slot");
    item = ItemStack.loadItemStackFromNBT(itemTag);
    inventorySlots[slot]=item;
    }
  int[] sideMap = tag.getIntArray("sideMap");
  
  RelativeSide baseSide;
  RelativeSide mappedSide;
  for(int i = 0; i < 6; i++)
    {
    baseSide = RelativeSide.values()[i];
    mappedSide = RelativeSide.values()[sideMap[i]];
    sideInventoryAccess.put(baseSide, mappedSide);
    }
  }

@Override
public void writeToNBT(NBTTagCompound tag)
  {
  NBTTagList itemList = new NBTTagList();
  NBTTagCompound itemTag;  
  ItemStack item;
  for(int i = 0; i < inventorySlots.length; i++)
    {
    item = inventorySlots[i];
    if(item==null){continue;}
    itemTag = new NBTTagCompound();
    item.writeToNBT(itemTag);
    itemTag.setShort("slot", (short)i);
    itemList.appendTag(itemTag);
    }
  tag.setTag("itemList", itemList);
  int[] sideMap = new int[6];
  
  RelativeSide baseSide;
  for(int i = 0; i < 6; i++)
    {
    baseSide = RelativeSide.values()[i];
    sideMap[baseSide.ordinal()] = sideInventoryAccess.get(baseSide).ordinal();
    } 
  tag.setIntArray("sideMap", sideMap);
  }

@Override
public boolean isDirty()
  {
  return isDirty;
  }

public int[] getAccessibleSlotsFor(RelativeSide blockSide)
  {
  return accessMap.get(sideInventoryAccess.get(blockSide)).accessibleSlots;
  }

/**
 * return the inventory side that should be accessed from the input mcSide for input meta
 * @param mcSide
 * @param meta
 * @return
 */
public RelativeSide getAccessSideFor(int mcSide, int meta)
  {
  return sideInventoryAccess.get(RelativeSide.getRelativeSide(mcSide, meta));
  }

/**
 * return the inventory side that should be accessed for the input relative side
 * @param baseSide
 * @return
 */
public RelativeSide getAccessSideFor(RelativeSide baseSide)
  {
  return sideInventoryAccess.get(baseSide);
  }

/**
 * set an inventory side mapping.
 * @param accessSide the side of the block that access will happen from
 * @param inventoryToAccess the side of the inventory that will be accessed from accessSide
 */
public void setSideMapping(RelativeSide accessSide, RelativeSide inventoryToAccess)
  {
  sideInventoryAccess.put(accessSide, inventoryToAccess);
  }

private class SideAccessibilityMap
{
/**
 * the original side mapping for this accessibility map
 */
RelativeSide side;

/**
 * a map of slot number to accessibility flags (canInsert, canExtract)
 */
HashMap<Integer, SidedAccessibility> slotMap = new HashMap<Integer, SidedAccessibility>();

/**
 * storage array of accessible slot indices
 */
int[] accessibleSlots;

private SideAccessibilityMap(RelativeSide side)
  {
  this.side = side;
  accessibleSlots = new int[]{};
  }

private void addMapping(int slot, boolean insert, boolean extract)
  {
  if(!slotMap.containsKey(slot))
    {
    slotMap.put(slot, new SidedAccessibility(slot, insert, extract));
    }
  else
    {
    SidedAccessibility access = slotMap.get(slot);
    access.insert = insert;
    access.extract = extract;    
    }  
  remapSidedIndices();
  }

public void removeMapping(int slot)
  {  
  slotMap.remove(slot);  
  remapSidedIndices();
  }

private void remapSidedIndices()
  {
  int[] slots = new int[slotMap.size()];
  SidedAccessibility access;
  int index = 0;
  for(Integer i : slotMap.keySet())
    {
    access = slotMap.get(i);
    slots[index] = access.slot;
    index++;
    }
  accessibleSlots = slots;
  }

private boolean canInsert(ItemStack stack, int slot)
  {
  SidedAccessibility access = slotMap.get(slot);
  if(access!=null)
    {
    return access.insert;
    }
  return false;
  }

private boolean canExtract(ItemStack stack, int slot)
  {
  SidedAccessibility access = slotMap.get(slot);
  if(access!=null)
    {
    return access.extract;
    }
  return false;
  }
}

private class SidedAccessibility
{
int slot;
boolean insert;
boolean extract;

private SidedAccessibility(int slot, boolean insert, boolean extract)
  {
  this.slot = slot;
  this.insert = insert;
  this.extract = extract;
  }
}



}
