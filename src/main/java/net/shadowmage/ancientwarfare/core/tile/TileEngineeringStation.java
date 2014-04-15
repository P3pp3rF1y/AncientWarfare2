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
  ItemStack layoutStack, testStack1;   
  for(int i = 0; i < 9; i++)
    {
    layoutStack = matrixShadow[i];
    if(layoutStack==null){continue;}
    if(layoutMatrix.getStackInSlot(i)!=null){continue;}
    layoutMatrix.setInventorySlotContents(i, InventoryTools.removeItems(extraSlots, -1, layoutStack, 1));
    }
  }

private void onLayoutMatrixChanged(IInventory matrix)
  {
  this.result.setInventorySlotContents(0, AWCraftingManager.INSTANCE.findMatchingRecipe(layoutMatrix, worldObj, getCrafterName()));
  }

}
