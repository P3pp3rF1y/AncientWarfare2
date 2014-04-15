package net.shadowmage.ancientwarfare.core.tile;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.shadowmage.ancientwarfare.core.crafting.AWCraftingManager;
import net.shadowmage.ancientwarfare.core.inventory.InventoryBasic;
import net.shadowmage.ancientwarfare.core.item.ItemResearchBook;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;

public class TileEngineeringStation extends TileEntity
{

public InventoryCrafting layoutMatrix;
public InventoryCraftResult result;
public InventoryBasic bookInventory = new InventoryBasic(1);
public InventoryBasic extraSlots = new InventoryBasic(18);

public TileEngineeringStation()
  {
  Container c = new Container()
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
      }
    };
  layoutMatrix = new InventoryCrafting(c, 3, 3);
  result = new InventoryCraftResult();
  }

@Override
public boolean canUpdate()
  {
  return true;
  }

public String getCrafterName()
  {
  return ItemResearchBook.getResearcherName(bookInventory.getStackInSlot(0));
  }

ItemStack[] matrixShadow = new ItemStack[9];

/**
 * called to shadow a copy of the input matrix, to know what to refill
 */
public void preItemCrafted()
  {
  ItemStack stack;
  for(int i = 0; i < 9; i++)
    {
    stack = layoutMatrix.getStackInSlot(i);
    matrixShadow[i] = stack==null ? null : stack.copy();
    }
  }

public void onItemCrafted()
  {
  Set<Integer> filledSlots = new HashSet<Integer>();
  Set<Integer> slotsToFill = new HashSet<Integer>();
    
  ItemStack layoutStack, testStack1;
  for(int i = 0; i < 9; i++)
    {
    if(filledSlots.contains(i)){continue;}
    layoutStack = matrixShadow[i];
    if(layoutStack==null){continue;}
    slotsToFill.clear();
    testStack1 = layoutMatrix.getStackInSlot(i);
    if(testStack1!=null && !InventoryTools.doItemStacksMatch(layoutStack, testStack1))
      {//means that the item was replaced by a container item
      
      }
    else
      {//should try and fill
      slotsToFill.add(i);
      filledSlots.add(i);
      for(int k = 0; k<9; k++)
        {
        if(k==i || filledSlots.contains(k)){continue;}
        if(InventoryTools.doItemStacksMatch(layoutStack, matrixShadow[i]))
          {
          slotsToFill.add(k);
          filledSlots.add(k);
          }
        }
      int availableCount = InventoryTools.getCountOf(extraSlots, -1, layoutStack);
      boolean filledSlot = true;
      int fillCount = 0;;
      while(availableCount>0 && filledSlot)//will exit when no more items to merge, or all merge-targets are full
        {
        filledSlot = false;
        for(Integer k : slotsToFill)
          {
          testStack1 = layoutMatrix.getStackInSlot(k);
          if(testStack1==null)
            {
            filledSlot = true;
            testStack1 = layoutStack.copy();
            testStack1.stackSize = 1;
            fillCount++;
            availableCount--;
            }
          else if(testStack1.stackSize< testStack1.getMaxStackSize())
            {
            filledSlot = true;
            testStack1.stackSize++;
            fillCount++;
            availableCount--;
            }
          }
        }
      while(fillCount > layoutStack.getMaxStackSize())
        {
        testStack1 = InventoryTools.removeItems(extraSlots, -1, layoutStack, layoutStack.getMaxStackSize());
        if(testStack1==null){break;}
        fillCount -= testStack1.stackSize;
        }
      InventoryTools.removeItems(extraSlots, -1, layoutStack, fillCount);
      }
    filledSlots.add(i);
    }
  
  /**
   * iterate through matrixShadow to find what stack is supposed to be in a slot   * 
   *    count how many occurances of that item appear in matrixShadow, add slot indices to slotsToFill, add slotIndices to filledSlots
   *    count total available item count for that item in extraInventory.
   *    calc max transferrable per-matrix slot from the total available and current count in that slot.
   *    transfer to all slots of that item-type an equal (or close to) amount of their item.
   */
  }

private void onLayoutMatrixChanged(IInventory matrix)
  {
  this.result.setInventorySlotContents(0, AWCraftingManager.INSTANCE.findMatchingRecipe(layoutMatrix, worldObj, getCrafterName()));
  }

}
