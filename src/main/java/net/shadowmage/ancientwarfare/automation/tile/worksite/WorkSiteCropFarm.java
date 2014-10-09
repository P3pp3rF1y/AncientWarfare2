package net.shadowmage.ancientwarfare.automation.tile.worksite;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.world.WorldServer;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.InventorySided;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.RelativeSide;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.RotationType;
import net.shadowmage.ancientwarfare.core.inventory.ItemSlotFilter;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.upgrade.WorksiteUpgrade;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;

public class WorkSiteCropFarm extends TileWorksiteUserBlocks
{

Set<BlockPosition> blocksToTill;
Set<BlockPosition> blocksToHarvest;
Set<BlockPosition> blocksToPlant;
Set<BlockPosition> blocksToFertilize;

int plantableCount;
int bonemealCount;
boolean shouldCountResources;

public WorkSiteCropFarm()
  {
  this.shouldCountResources = true;
  
  blocksToTill = new HashSet<BlockPosition>();
  blocksToHarvest = new HashSet<BlockPosition>();
  blocksToPlant = new HashSet<BlockPosition>();
  blocksToFertilize = new HashSet<BlockPosition>();
  
  this.inventory = new InventorySided(this, RotationType.FOUR_WAY, 33)
    {
    @Override
    public void markDirty()
      {
      super.markDirty();
      shouldCountResources = true;
      }
    };
  int[] topIndices = InventoryTools.getIndiceArrayForSpread(0, 27);
  int[] frontIndices = InventoryTools.getIndiceArrayForSpread(27, 3);
  int[] bottomIndices = InventoryTools.getIndiceArrayForSpread(30, 3);  
  this.inventory.setAccessibleSideDefault(RelativeSide.TOP, RelativeSide.TOP, topIndices);
  this.inventory.setAccessibleSideDefault(RelativeSide.FRONT, RelativeSide.FRONT, frontIndices);//plantables
  this.inventory.setAccessibleSideDefault(RelativeSide.BOTTOM, RelativeSide.BOTTOM, bottomIndices);//bonemeal
  
  ItemSlotFilter filter = new ItemSlotFilter()
    {
    @Override
    public boolean isItemValid(ItemStack stack)
      {
      if(stack==null){return true;}
      Item item = stack.getItem();
      if(item==Items.carrot || item==Items.potato || item==Items.wheat_seeds || item==Items.melon_seeds || item==Items.pumpkin_seeds)
        {       
        return true;
        }
      return false;
      }
    };
  this.inventory.setFilterForSlots(filter, frontIndices);
    
  filter = new ItemSlotFilter()
    {
    @Override
    public boolean isItemValid(ItemStack stack)
      {
      if(stack==null){return true;}
      return stack.getItem() == Items.dye && stack.getItemDamage()==15;
      }
    };
  this.inventory.setFilterForSlots(filter, bottomIndices);  
  }

@Override
public void onBoundsAdjusted()
  {
  validateCollection(blocksToFertilize);
  validateCollection(blocksToHarvest);
  validateCollection(blocksToPlant);
  validateCollection(blocksToTill);
  }

private void countResources()
  {
  shouldCountResources = false;
  plantableCount = 0;
  bonemealCount = 0;
  ItemStack stack;
  Item item;
  for(int i = 27; i <30; i++)
    {    
    stack = inventory.getStackInSlot(i);
    if(stack==null){continue;}
    item = stack.getItem();
    if(item==Items.carrot || item==Items.potato || item==Items.wheat_seeds || item==Items.melon_seeds || item==Items.pumpkin_seeds)
      {       
      plantableCount+=stack.stackSize;
      }    
    }
  for(int i = 30; i <33; i++)
    {
    stack = inventory.getStackInSlot(i);
    if(stack==null){continue;}
    if(stack.getItem() == Items.dye && stack.getItemDamage()==15)
      {
      bonemealCount+=stack.stackSize;
      }
    }
  }

@Override
protected void fillBlocksToProcess(Collection<BlockPosition> targets)
  { 
  int w = bbMax.x - bbMin.x + 1;
  int h = bbMax.z - bbMin.z + 1;
  BlockPosition p;
  for(int x = 0; x < w; x++)
    {
    for(int z = 0; z< h; z++)
      {
      if(isTarget(bbMin.x+x, bbMin.z+z))
        {
        p = new BlockPosition(bbMin);
        p.offset(x, 0, z);
        targets.add(p);
        }
      }
    }  
  }

@Override
protected void scanBlockPosition(BlockPosition position)
  {
  Block block = worldObj.getBlock(position.x, position.y, position.z);
  if(worldObj.isAirBlock(position.x, position.y, position.z))
    {
    block = worldObj.getBlock(position.x, position.y-1, position.z);
    if(block==Blocks.dirt || block==Blocks.grass)
      {
      blocksToTill.add(new BlockPosition(position.x, position.y-1, position.z));
      }
    else if(block==Blocks.farmland)
      {
      blocksToPlant.add(position);
      }
    }    
  else if(block==Blocks.wheat || block==Blocks.carrots || block==Blocks.potatoes)
    {
    if(worldObj.getBlockMetadata(position.x, position.y, position.z)>=7)
      {
      blocksToHarvest.add(position);
      }
    else
      {
      blocksToFertilize.add(position);
      }
    }
  else if(block==Blocks.melon_stem || block==Blocks.pumpkin_stem)
    {
    if(worldObj.getBlockMetadata(position.x, position.y, position.z)>=7)
      {
      block = worldObj.getBlock(position.x-1, position.y, position.z);
      if(block==Blocks.melon_block || block==Blocks.pumpkin)
        {
        blocksToHarvest.add(new BlockPosition(position.x-1, position.y, position.z));
        }
      block = worldObj.getBlock(position.x+1, position.y, position.z);
      if(block==Blocks.melon_block || block==Blocks.pumpkin)
        {
        blocksToHarvest.add(new BlockPosition(position.x+1, position.y, position.z));
        }
      block = worldObj.getBlock(position.x, position.y, position.z-1);
      if(block==Blocks.melon_block || block==Blocks.pumpkin)
        {
        blocksToHarvest.add(new BlockPosition(position.x, position.y, position.z-1));
        }
      block = worldObj.getBlock(position.x, position.y, position.z+1);
      if(block==Blocks.melon_block || block==Blocks.pumpkin)
        {
        blocksToHarvest.add(new BlockPosition(position.x, position.y, position.z+1));
        }
      }
    else
      {
      blocksToFertilize.add(position);
      }      
    }
  }

@Override
protected boolean processWork()
  {
  Iterator<BlockPosition> it;
  BlockPosition position;
  Block block;
  int meta;
  if(!blocksToTill.isEmpty())
    {
    it = blocksToTill.iterator();
    while(it.hasNext() && (position=it.next())!=null)
      {
      it.remove();
      block = worldObj.getBlock(position.x, position.y, position.z);
      if(worldObj.isAirBlock(position.x, position.y+1, position.z) && (block==Blocks.grass || block==Blocks.dirt))
        {
        worldObj.setBlock(position.x, position.y, position.z, Blocks.farmland);
        return true;
        }
      }
    }
  else if(!blocksToHarvest.isEmpty())
    {
    it = blocksToHarvest.iterator();
    while(it.hasNext() && (position=it.next())!=null)
      {
      it.remove();
      block = worldObj.getBlock(position.x, position.y, position.z);
      if(block==Blocks.wheat || block==Blocks.carrots || block==Blocks.potatoes)
        {
        int fortune = getUpgrades().contains(WorksiteUpgrade.ENCHANTED_TOOLS_1)? 1 : getUpgrades().contains(WorksiteUpgrade.ENCHANTED_TOOLS_2)? 2 : 0;
        meta = worldObj.getBlockMetadata(position.x, position.y, position.z);
        if(meta<7){continue;}
        return harvestBlock(position.x, position.y, position.z, fortune, RelativeSide.FRONT, RelativeSide.TOP);
        }
      else if(block==Blocks.pumpkin || block==Blocks.melon_block)
        {
        int fortune = getUpgrades().contains(WorksiteUpgrade.ENCHANTED_TOOLS_1)? 1 : getUpgrades().contains(WorksiteUpgrade.ENCHANTED_TOOLS_2)? 2 : 0;
        return harvestBlock(position.x, position.y, position.z, fortune, RelativeSide.FRONT, RelativeSide.TOP);
        }
      }
    }
  else if(!blocksToPlant.isEmpty() && plantableCount>0)
    {
    it = blocksToPlant.iterator();
    while(it.hasNext() && (position=it.next())!=null)
      {
      it.remove();
      if(worldObj.getBlock(position.x, position.y-1, position.z)==Blocks.farmland && worldObj.isAirBlock(position.x, position.y, position.z))
        {
        ItemStack stack = null;
        Item item;
        for(int i = 27; i <30; i++)
          {
          stack = inventory.getStackInSlot(i);
          if(stack==null)
            {
            continue;
            }
          item = stack.getItem();  
          if(item==Items.wheat_seeds || item==Items.carrot || item==Items.potato || item==Items.melon_seeds || item==Items.pumpkin_seeds)
            {
            plantableCount--;
            stack.stackSize--;
            block = null;
            if(stack.stackSize<=0){inventory.setInventorySlotContents(i, null);}            
            if(item==Items.wheat_seeds){block = Blocks.wheat;}
            else if(item==Items.carrot){block = Blocks.carrots;}
            else if(item==Items.potato){block = Blocks.potatoes;}
            else if(item==Items.melon_seeds){block = Blocks.melon_stem;}
            else {block = Blocks.pumpkin_stem;}
            worldObj.setBlock(position.x, position.y, position.z, block);
            return true;         
            }          
          }
        return false;
        }
      }
    }
  else if(!blocksToFertilize.isEmpty() && bonemealCount>0)
    {
    it = blocksToFertilize.iterator();
    while(it.hasNext() && (position=it.next())!=null)
      {
      it.remove();
      block = worldObj.getBlock(position.x, position.y, position.z);
      if(block==Blocks.wheat || block==Blocks.carrots || block==Blocks.potatoes || block==Blocks.pumpkin_stem || block==Blocks.melon_stem)
        {
        ItemStack stack = null;
        Item item;
        for(int i = 30; i <33; i++)
          {
          stack = inventory.getStackInSlot(i);
          if(stack==null)
            {
            continue;
            }
          item = stack.getItem();
          if(item==Items.dye && stack.getItemDamage()==15)
            {
            bonemealCount--;
            ItemDye.applyBonemeal(stack, worldObj, position.x, position.y, position.z, AncientWarfareCore.proxy.getFakePlayer((WorldServer) worldObj, owningPlayer));
            if(stack.stackSize<=0){inventory.setInventorySlotContents(i, null);}  
            block = worldObj.getBlock(position.x, position.y, position.z);
            if(block==Blocks.wheat || block==Blocks.carrots || block==Blocks.potatoes || block==Blocks.pumpkin_stem || block==Blocks.melon_stem)
              {
              if(worldObj.getBlockMetadata(position.x, position.y, position.z)<7)
                {
                blocksToFertilize.add(position);
                }
              else
                {
                if(block==Blocks.wheat || block==Blocks.carrots || block==Blocks.potatoes)
                  {
                  blocksToHarvest.add(position);
                  }
                }
              }
            return true;
            }
          }
        return false;
        }
      }
    }
  return false;
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
    NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_WORKSITE_CROP_FARM, xCoord, yCoord, zCoord);
    return true;
    }
  return true;
  }
@Override
protected boolean hasWorksiteWork()
  {
  return (plantableCount > 0 && !blocksToPlant.isEmpty()) || (bonemealCount>0 && !blocksToFertilize.isEmpty()) || !blocksToTill.isEmpty() || !blocksToHarvest.isEmpty();
  }

@Override
protected void updateBlockWorksite()
  {
  worldObj.theProfiler.startSection("Count Resources");  
  if(shouldCountResources){countResources();}  
  worldObj.theProfiler.endSection();
  }

}
