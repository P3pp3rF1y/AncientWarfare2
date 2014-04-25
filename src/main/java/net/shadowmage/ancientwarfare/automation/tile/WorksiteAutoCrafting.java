package net.shadowmage.ancientwarfare.automation.tile;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.WeakHashMap;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.Team;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.shadowmage.ancientwarfare.core.crafting.AWCraftingManager;
import net.shadowmage.ancientwarfare.core.interfaces.IWorkSite;
import net.shadowmage.ancientwarfare.core.interfaces.IWorker;
import net.shadowmage.ancientwarfare.core.inventory.InventoryBasic;
import net.shadowmage.ancientwarfare.core.item.ItemResearchBook;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;

public class WorksiteAutoCrafting extends TileEntity implements IInventory, IWorkSite, ISidedInventory
{

private InventoryBasic bookSlot;
private InventoryBasic outputInventory;
private InventoryBasic resourceInventory;
private InventoryCrafting craftMatrix;
private int maxWorkers = 2;
protected String owningPlayer;
private Set<IWorker> workers = Collections.newSetFromMap( new WeakHashMap<IWorker, Boolean>());
int[] outputSlotIndices;
int[] resourceSlotIndices;
ItemStack[] matrixShadow = new ItemStack[9];//shadow copy of input matrix
private ItemStack craftItem;//the item that will be crafted from input
boolean hasResourcesForCraft;//set from onInventoryChanged() to check if there are enough resources in input inventory to craft the next item

public WorksiteAutoCrafting()
  {
  Container dummy = new Container()
    {
    @Override
    public boolean canInteractWith(EntityPlayer var1)
      {
      return true;
      }
    @Override
    public void onCraftMatrixChanged(IInventory par1iInventory)
      {
      onLayoutMatrixChanged(par1iInventory);
      super.onCraftMatrixChanged(par1iInventory);
      }
    };
  craftMatrix = new InventoryCrafting(dummy, 3, 3);
  resourceInventory = new InventoryBasic(27);
  outputInventory = new InventoryBasic(9);
  resourceSlotIndices = new int[27];
  for(int i = 0; i < 27; i++)
    {
    resourceSlotIndices[i] = i;
    }
  outputSlotIndices = new int[9];
  for(int i = 0, k = 27; i<9; i++, k++)
    {
    outputSlotIndices[i] = k;
    }
  bookSlot = new InventoryBasic(1);
  }

public String getCrafterName()
  {
  return ItemResearchBook.getResearcherName(bookSlot.getStackInSlot(0));
  }

@Override
public final boolean canHaveWorker(IWorker worker)
  {
  if(!worker.getWorkTypes().contains(getWorkType()) || worker.getTeam() != this.getTeam())
    {
    return false;
    }
  if(workers.contains(worker))
    {
    return true;
    }
  return workers.size()<maxWorkers;
  }

@Override
public final boolean addWorker(IWorker worker)
  {
  if(workers.size()<maxWorkers || workers.contains(worker))
    {
    workers.add(worker);
    return true;
    }
  return false;
  }

@Override
public final void removeWorker(IWorker worker)
  {
  workers.remove(worker);
  }

public final void setOwningPlayer(String name)
  {
  this.owningPlayer = name;
  }

@Override
public final boolean canUpdate()
  {
  return true;
  }

@Override
public void doPlayerWork(EntityPlayer player)
  {
  if(hasWork())
    {
    craftItem();
    }
  }

@Override
public boolean hasWork()
  {  
  return craftItem!=null && hasResourcesForCraft;
  }

@Override
public void doWork(IWorker worker)
  {
  if(hasWork())
    {
    craftItem();    
    }
  }

private void craftItem()
  {
  
  }

@Override
public WorkType getWorkType()
  {
  return WorkType.CRAFTING;
  }

@Override
public final Team getTeam()
  {  
  if(owningPlayer!=null)
    {
    worldObj.getScoreboard().getPlayersTeam(owningPlayer);
    }
  return null;
  }

@Override
public BlockPosition getWorkBoundsMin()
  {
  return null;
  }

@Override
public BlockPosition getWorkBoundsMax()
  {
  return null;
  }

@Override
public List<BlockPosition> getWorkTargets()
  {
  return Collections.emptyList();
  }

@Override
public boolean hasWorkBounds()
  {
  return false;
  }

/***************************************INVENTORY METHODS************************************************/
private void onLayoutMatrixChanged(IInventory matrix)
  {
  //TODO
  //this.result.setInventorySlotContents(0, AWCraftingManager.INSTANCE.findMatchingRecipe(layoutMatrix, worldObj, getCrafterName()));
  }

@Override
public int getSizeInventory()
  {
  return 27+9;
  }

@Override
public ItemStack getStackInSlot(int slotIndex)
  {
  if(slotIndex>=27)
    {
    slotIndex-=27;
    return outputInventory.getStackInSlot(slotIndex);
    }
  return resourceInventory.getStackInSlot(slotIndex);
  }

@Override
public ItemStack decrStackSize(int slot, int amount)
  {
  if(slot>=27)
    {
    slot-=27;
    return outputInventory.decrStackSize(slot, amount);
    }
  return resourceInventory.decrStackSize(slot, amount);
  }

@Override
public ItemStack getStackInSlotOnClosing(int var1)
  {
  if(var1>=27)
    {
    var1-=27;
    return outputInventory.getStackInSlotOnClosing(var1);
    }
  return resourceInventory.getStackInSlotOnClosing(var1);
  }

@Override
public void setInventorySlotContents(int var1, ItemStack var2)
  {
  if(var1>=27)
    {
    var1-=27;
    outputInventory.setInventorySlotContents(var1, var2);
    return;
    }
  resourceInventory.setInventorySlotContents(var1, var2);
  }

@Override
public String getInventoryName()
  {
  return "aw.autocrafting";
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
  }

@Override
public boolean isUseableByPlayer(EntityPlayer var1)
  {
  return false;
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
public int[] getAccessibleSlotsFromSide(int side)
  {
  ForgeDirection d = ForgeDirection.getOrientation(side);  
  if(d==ForgeDirection.UP)
    {
    return resourceSlotIndices;
    }
  else if(d==ForgeDirection.DOWN)
    {
    return outputSlotIndices;
    }
  return null;
  }

@Override
public boolean canInsertItem(int slot, ItemStack var2, int side)
  {
  ForgeDirection d = ForgeDirection.getOrientation(side);
  if(d==ForgeDirection.UP)
    {
    return true;//top, insert-only
    }
  return false;
  }

@Override
public boolean canExtractItem(int slot, ItemStack var2, int side)
  {
  ForgeDirection d = ForgeDirection.getOrientation(side);
  if(d==ForgeDirection.DOWN)
    {
    return true;//bottom, extract only
    }
  return false;
  }

}
