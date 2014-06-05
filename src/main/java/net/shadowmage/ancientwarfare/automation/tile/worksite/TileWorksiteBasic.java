package net.shadowmage.ancientwarfare.automation.tile.worksite;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.InventorySided;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.RelativeSide;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;

/**
 * abstract base class for worksite based tile-entities (or at least a template to copy from)
 * 
 * handles the management of worker references and work-bounds, as well as inventory bridge methods.
 * 
 * All implementing classes must initialize the inventory field in their constructor, or things
 * will go very crashy when the block is placed in the world.
 *  
 * @author Shadowmage
 *
 */
public abstract class TileWorksiteBasic extends TileWorksiteBase
{

public InventorySided inventory;

public TileWorksiteBasic()
  {
  
  }

public void openAltGui(EntityPlayer player)
  {
  //noop, must be implemented by individual tiles, if they have an alt-control gui
  }

/**
 * attempt to add an item stack to this worksites inventory.<br>
 * iterates through input sides in the order given, 
 * so should pick the most restrictive inventory first, 
 * least restrictive last
 * @param stack
 * @param sides
 */
public final void addStackToInventory(ItemStack stack, RelativeSide... sides)
  {
  int mcSide;
  for(RelativeSide side: sides)
    {
    mcSide = inventory.getAccessDirectionFor(side);
    stack = InventoryTools.mergeItemStack(inventory, stack, mcSide);
    if(stack==null)
      {
      break;
      }
    }
  if(stack!=null)
    {
    inventoryOverflow.add(stack);  
    }
  }

@Override
protected void updateOverflowInventory()
  {
  List<ItemStack> notMerged = new ArrayList<ItemStack>();
  Iterator<ItemStack> it = inventoryOverflow.iterator();
  ItemStack stack;
  while(it.hasNext() && (stack=it.next())!=null)
    {
    it.remove();
    stack = InventoryTools.mergeItemStack(inventory, stack, inventory.getAccessDirectionFor(RelativeSide.TOP));
    if(stack!=null)
      {
      notMerged.add(stack);
      }      
    }
  if(!notMerged.isEmpty())
    {
    inventoryOverflow.addAll(notMerged);    
    }
  }

public final boolean canWork()
  {
  return inventoryOverflow.isEmpty();
  }

@Override
public abstract boolean onBlockClicked(EntityPlayer player);

@Override
public void writeToNBT(NBTTagCompound tag)
  {
  super.writeToNBT(tag);
  if(inventory!=null)
    {
    NBTTagCompound invTag = new NBTTagCompound();
    inventory.writeToNBT(invTag);
    tag.setTag("inventory", invTag);    
    }
  }

@Override
public void readFromNBT(NBTTagCompound tag)
  {
  super.readFromNBT(tag);
  if(tag.hasKey("inventory") && inventory!=null)
    {
    inventory.readFromNBT(tag.getCompoundTag("inventory"));
    }
  }

@Override
public int getSizeInventory()
  {
  return inventory.getSizeInventory();
  }

@Override
public ItemStack getStackInSlot(int var1)
  {
  return inventory.getStackInSlot(var1);
  }

@Override
public ItemStack decrStackSize(int var1, int var2)
  {
  return inventory.decrStackSize(var1, var2);
  }

@Override
public ItemStack getStackInSlotOnClosing(int var1)
  {
  return inventory.getStackInSlotOnClosing(var1);
  }

@Override
public void setInventorySlotContents(int var1, ItemStack var2)
  {
  inventory.setInventorySlotContents(var1, var2);
  }

@Override
public String getInventoryName()
  {
  return inventory.getInventoryName();
  }

@Override
public boolean hasCustomInventoryName()
  {
  return inventory.hasCustomInventoryName();
  }

@Override
public int getInventoryStackLimit()
  {
  return inventory.getInventoryStackLimit();
  }

@Override
public boolean isUseableByPlayer(EntityPlayer var1)
  {
  return inventory.isUseableByPlayer(var1);
  }

@Override
public void openInventory()
  {
  inventory.openInventory();
  }

@Override
public void closeInventory()
  {
  inventory.closeInventory();
  }

@Override
public boolean isItemValidForSlot(int var1, ItemStack var2)
  {
  return inventory.isItemValidForSlot(var1, var2);
  }

@Override
public int[] getAccessibleSlotsFromSide(int var1)
  {
  return inventory.getAccessibleSlotsFromSide(var1);
  }

@Override
public boolean canInsertItem(int var1, ItemStack var2, int var3)
  {
  return inventory.canInsertItem(var1, var2, var3);
  }

@Override
public boolean canExtractItem(int var1, ItemStack var2, int var3)
  {
  return inventory.canExtractItem(var1, var2, var3);
  }

}
