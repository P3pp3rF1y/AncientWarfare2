package net.shadowmage.ancientwarfare.core.tile;

import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.shadowmage.ancientwarfare.core.inventory.InventoryBasic;
import net.shadowmage.ancientwarfare.core.item.ItemResearchBook;
import net.shadowmage.ancientwarfare.core.research.ResearchTracker;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;

public class TileResearchStation extends TileEntity
{

public InventoryBasic bookInventory = new InventoryBasic(1);


int testTimeCount = 0;

public TileResearchStation()
  {
  // TODO Auto-generated constructor stub
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

@Override
public void updateEntity()
  {
  if(worldObj.isRemote){return;}
  if(testTimeCount==0 && getCrafterName()!=null)
    {
    int goal = ResearchTracker.instance().getCurrentGoal(worldObj, getCrafterName());
    if(goal==-1)//no selection
      {
      List<Integer> queue = ResearchTracker.instance().getResearchQueueFor(worldObj, getCrafterName());
      if(!queue.isEmpty())
        {
        ResearchTracker.instance().startResearch(worldObj, getCrafterName(), queue.get(0));        
        }
      }
    else
      {
      ResearchTracker.instance().finishResearch(worldObj, getCrafterName(), goal);
      List<Integer> queue = ResearchTracker.instance().getResearchQueueFor(worldObj, getCrafterName());
      if(!queue.isEmpty())
        {
        ResearchTracker.instance().startResearch(worldObj, getCrafterName(), queue.get(0));
        }      
      }
    testTimeCount = 80;
    }
  
  if(testTimeCount>0)
    {
    testTimeCount--;    
    }
  }

@Override
public void readFromNBT(NBTTagCompound tag)
  {
  super.readFromNBT(tag);
  InventoryTools.readInventoryFromNBT(bookInventory, tag.getCompoundTag("bookInventory"));
//  InventoryTools.readInventoryFromNBT(extraSlots, tag.getCompoundTag("extraInventory"));
//  InventoryTools.readInventoryFromNBT(result, tag.getCompoundTag("resultInventory"));
//  InventoryTools.readInventoryFromNBT(layoutMatrix, tag.getCompoundTag("layoutMatrix"));
  }

@Override
public void writeToNBT(NBTTagCompound tag)
  {
  super.writeToNBT(tag);
  
  NBTTagCompound inventoryTag = new NBTTagCompound();
  InventoryTools.writeInventoryToNBT(bookInventory, inventoryTag);
  tag.setTag("bookInventory", inventoryTag);
  
//  inventoryTag = new NBTTagCompound();
//  InventoryTools.writeInventoryToNBT(extraSlots, inventoryTag);
//  tag.setTag("extraInventory", inventoryTag);
//  
//  inventoryTag = new NBTTagCompound();
//  InventoryTools.writeInventoryToNBT(result, inventoryTag);
//  tag.setTag("resultInventory", inventoryTag);
//  
//  inventoryTag = new NBTTagCompound();
//  InventoryTools.writeInventoryToNBT(layoutMatrix, inventoryTag);
//  tag.setTag("layoutMatrix", inventoryTag);
  
  }

}
