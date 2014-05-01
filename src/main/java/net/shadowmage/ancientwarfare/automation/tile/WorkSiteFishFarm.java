package net.shadowmage.ancientwarfare.automation.tile;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemFishFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.interfaces.IWorker;
import net.shadowmage.ancientwarfare.core.inventory.InventorySide;
import net.shadowmage.ancientwarfare.core.inventory.InventorySided;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;

public class WorkSiteFishFarm extends TileWorksiteBase
{

public boolean harvestFish = true;
public boolean harvestInk = true;

private int waterBlockCount = 0;

private int waterRescanDelay = 0;

public WorkSiteFishFarm()
  {
  this.canUpdate = true;
  this.maxWorkers = 2;
  this.inventory = new InventorySided(27, this);
  
  this.inventory.addSlotViewMap(InventorySide.TOP, 8, 8, "guistrings.inventory.side.top");
  for(int i =0; i <27; i++)
    {
    this.inventory.addSidedMapping(InventorySide.TOP, i, true, true);
    this.inventory.addSlotViewMapping(InventorySide.TOP, i, (i%9)*18, (i/9)*18);
    } 
  }

@Override
public void updateEntity()
  {
  if(waterRescanDelay>0)
    {
    waterRescanDelay--;
    }
  }

@Override
public boolean hasWork()
  {
  return canWork() && (waterRescanDelay<=0 || waterBlockCount>0) && (harvestFish || harvestInk);
  }

@Override
public void doWork(IWorker worker)
  {
  processWork();
  }

private void processWork()
  {
  if(waterRescanDelay<=0)
    {
    waterBlockCount = 0;
    countWater();
    waterRescanDelay = 200;
    }
  if(waterBlockCount>0)
    {
    int maxBlocks = 1280;
    float percentOfMax = (float)waterBlockCount / (float)maxBlocks;
    float check = worldObj.rand.nextFloat();
    if(check<=percentOfMax)
      {
      boolean fish = false, ink = false;
      if(harvestFish && harvestInk)
        {        
        fish = worldObj.rand.nextBoolean();
        ink = !fish;
        }
      else
        {
        fish = harvestFish;
        ink = harvestInk;
        }
      if(fish)        
        {
        int fishType = worldObj.rand.nextInt(100);
        int fishMeta = 0;
        if(fishType<60){fishMeta = ItemFishFood.FishType.COD.func_150976_a();}
        else if(fishType<85){fishMeta = ItemFishFood.FishType.SALMON.func_150976_a();}
        else if(fishType<98){fishMeta = ItemFishFood.FishType.PUFFERFISH.func_150976_a();}
        else if(fishType<100){fishMeta = ItemFishFood.FishType.CLOWNFISH.func_150976_a();}
        ItemStack fishStack = new ItemStack(Items.fish,1,fishMeta);
        addStackToInventory(fishStack, InventorySide.TOP);
        }
      if(ink)
        {
        ItemStack inkItem = new ItemStack(Items.dye,1,0);
        addStackToInventory(inkItem, InventorySide.TOP);
        }      
      }
    }
  }

private void countWater()
  {
  BlockPosition min = getWorkBoundsMin();
  BlockPosition max = getWorkBoundsMax();
  Block block;
  for(int x = min.x; x<=max.x; x++)
    {
    for(int z = min.z; z<=max.z; z++)
      {
      for(int y = min.y; y>min.y-5; y--)
        {
        block = worldObj.getBlock(x, y, z);
        if(block==Blocks.flowing_water || block==Blocks.water)
          {
          waterBlockCount++;
          }
        else
          {
          break;
          }
        }
      }
    }
  }

@Override
public WorkType getWorkType()
  {
  return WorkType.FARMING;
  }

@Override
public void onInventoryChanged()
  {
  
  }

@Override
public boolean onBlockClicked(EntityPlayer player)
  {
  if(!player.worldObj.isRemote)
    {
    NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_WORKSITE_INVENTORY, xCoord, yCoord, zCoord);
    return true;
    }
  return false;
  }

@Override
protected void addWorkTargets(List<BlockPosition> targets)
  {

  }

@Override
public void writeClientData(NBTTagCompound tag)
  {

  }

@Override
public void readClientData(NBTTagCompound tag)
  {

  }

@Override
public boolean hasAltSetupGui()
  {
  return true;
  }

@Override
public void openAltGui(EntityPlayer player)
  {
  NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_WORKSITE_FISH_CONTROL, xCoord, yCoord, zCoord);
  }

@Override
public void doPlayerWork(EntityPlayer player)
  {
  processWork();
  }

}
