package net.shadowmage.ancientwarfare.automation.tile;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.terraingen.SaplingGrowTreeEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.shadowmage.ancientwarfare.automation.config.AWAutomationStatics;
import net.shadowmage.ancientwarfare.automation.interfaces.IWorker;
import net.shadowmage.ancientwarfare.core.inventory.InventorySide;
import net.shadowmage.ancientwarfare.core.inventory.InventorySided;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.structure.config.AWStructureStatics;

public class WorkSiteTreeFarm extends TileWorksiteBase
{


public WorkSiteTreeFarm()
  {
  this.canUserSetBlocks = true;
  this.canUpdate = true;
  this.maxWorkers = 1;
  this.inventory = new InventorySided(27 + 3 + 3, this);
  this.inventory.addSlotViewMap(InventorySide.TOP, 8, 8, "guistrings.inventory.side.top");
  this.inventory.addSlotViewMap(InventorySide.FRONT, 8, (3*18)+12+8, "guistrings.inventory.side.front");
  this.inventory.addSlotViewMap(InventorySide.REAR, 8, (3*18)+18+12+8+12, "guistrings.inventory.side.rear");
  for(int i =0; i <27; i++)
    {
    this.inventory.addSidedMapping(InventorySide.TOP, i, true, true);
    this.inventory.addSlotViewMapping(InventorySide.TOP, i, (i%9)*18, (i/9)*18);
    }
  for(int i = 27, k = 0; i<30; i++, k++)
    {
    this.inventory.addSidedMapping(InventorySide.LEFT, i, true, true);
    this.inventory.addSidedMapping(InventorySide.RIGHT, i, true, true);
    this.inventory.addSlotViewMapping(InventorySide.FRONT, i, (k%9)*18, (k/9)*18);
    }
  for(int i = 30, k = 0; i < 33; i++, k++)
    {
    this.inventory.addSidedMapping(InventorySide.REAR, i, true, true);
    this.inventory.addSlotViewMapping(InventorySide.REAR, i, (k%9)*18, (k/9)*18);
    }  
  }

@Override
public void updateEntity()
  {
  super.updateEntity();
  if(worldObj.isRemote){return;}
  }

@Override
public void doPlayerWork(EntityPlayer player)
  {
  
  }

@Override
public boolean hasWork()
  {  
  return false;
  }

@Override
public void doWork(IWorker worker)
  {  

  }

@Override
public WorkType getWorkType()
  {  
  return WorkType.FORESTRY;
  }

@Override
public void initWorkSite()
  {
  
  }

@Override
public boolean onBlockClicked(EntityPlayer player)
  {
  return false;
  }

@Override
public void writeClientData(NBTTagCompound tag)
  {

  }

@Override
public void readClientData(NBTTagCompound tag)
  {

  }

}
