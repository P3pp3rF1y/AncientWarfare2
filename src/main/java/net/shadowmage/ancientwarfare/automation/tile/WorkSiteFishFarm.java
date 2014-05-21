package net.shadowmage.ancientwarfare.automation.tile;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemFishFood;
import net.minecraft.item.ItemStack;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.InventorySided;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.RelativeSide;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.RotationType;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;

public class WorkSiteFishFarm extends TileWorksiteBounded
{

public boolean harvestFish = true;
public boolean harvestInk = true;

private int waterBlockCount = 0;

private int waterRescanDelay = 0;

public WorkSiteFishFarm()
  {
  this.inventory = new InventorySided(this, RotationType.FOUR_WAY, 27);
  this.inventory.setAccessibleSideDefault(RelativeSide.TOP, RelativeSide.TOP, InventoryTools.getIndiceArrayForSpread(0, 27)); 
  }

@Override
public void updateEntity()
  {
  super.updateEntity();
  if(waterRescanDelay>0)
    {
    waterRescanDelay--;
    }
  }

@Override
protected boolean processWork()
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
        addStackToInventory(fishStack, RelativeSide.TOP);
        return true;
        }
      if(ink)
        {
        ItemStack inkItem = new ItemStack(Items.dye,1,0);
        addStackToInventory(inkItem, RelativeSide.TOP);
        return true;
        }      
      }
    }
  return false;
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
public boolean onBlockClicked(EntityPlayer player)
  {
  if(!player.worldObj.isRemote)
    {
    NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_WORKSITE_FISH_FARM, xCoord, yCoord, zCoord);
    return true;
    }
  return false;
  }

@Override
public void openAltGui(EntityPlayer player)
  {
  NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_WORKSITE_FISH_CONTROL, xCoord, yCoord, zCoord);
  }

@Override
protected boolean hasWorksiteWork()
  {
  return waterBlockCount>0;
  }

}
