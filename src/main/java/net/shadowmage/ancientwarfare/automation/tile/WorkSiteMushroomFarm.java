package net.shadowmage.ancientwarfare.automation.tile;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.shadowmage.ancientwarfare.automation.config.AWAutomationStatics;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.interfaces.IWorker;
import net.shadowmage.ancientwarfare.core.inventory.InventorySide;
import net.shadowmage.ancientwarfare.core.inventory.InventorySided;
import net.shadowmage.ancientwarfare.core.inventory.InventorySided.SlotItemFilter;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.core.util.BlockTools;

public class WorkSiteMushroomFarm extends TileWorksiteBase
{

Set<BlockPosition> blocksToHarvest;
Set<BlockPosition> blocksToPlantMushroom;
Set<BlockPosition> blocksToPlantNetherWart;
int mushroomCount;
int netherWartCount;
int workerRescanDelay;
boolean shouldCountResources;

public WorkSiteMushroomFarm()
  {
  this.canUserSetBlocks = true;
  this.canUpdate = true;
  this.maxWorkers = 1;
  this.shouldSendWorkTargets = true;
  this.shouldCountResources = true;
  
  blocksToHarvest = new HashSet<BlockPosition>();
  blocksToPlantMushroom = new HashSet<BlockPosition>();
  blocksToPlantNetherWart = new HashSet<BlockPosition>();
  this.inventory = new InventorySided(27 + 3, this);
  
  this.inventory.addSlotViewMap(InventorySide.TOP, 8, 8, "guistrings.inventory.side.top");
  for(int i =0; i <27; i++)
    {
    this.inventory.addSidedMapping(InventorySide.TOP, i, true, true);
    this.inventory.addSlotViewMapping(InventorySide.TOP, i, (i%9)*18, (i/9)*18);
    }
    
  SlotItemFilter filter = new SlotItemFilter()
    {
    @Override
    public boolean isItemValid(ItemStack stack)
      {
      if(stack==null){return true;}
      Item item = stack.getItem();
      if(item==Items.nether_wart)
        {       
        return true;
        }
      else if(item instanceof ItemBlock)
        {
        ItemBlock ib = (ItemBlock)item;
        if(ib.field_150939_a==Blocks.red_mushroom || ib.field_150939_a==Blocks.brown_mushroom)
          {
          return true;
          }
        }      
      return false;
      }
    };
  this.inventory.addSlotViewMap(InventorySide.FRONT, 8, (3*18)+12+8, "guistrings.inventory.side.front");
  for(int i = 27, k = 0; i<30; i++, k++)
    {
    this.inventory.addSidedMapping(InventorySide.LEFT, i, true, true);
    this.inventory.addSidedMapping(InventorySide.RIGHT, i, true, true);
    this.inventory.addSlotViewMapping(InventorySide.FRONT, i, (k%9)*18, (k/9)*18);
    this.inventory.addSlotFilter(i, filter);
    }
  }

@Override
public void updateEntity()
  {
  super.updateEntity();
  if(worldObj.isRemote){return;}
  if(workerRescanDelay>0){workerRescanDelay--;}
  if(shouldCountResources){countResources();}
  }

private void countResources()
  {
  AWLog.logDebug("counting resources...");
  this.mushroomCount = 0;
  this.netherWartCount = 0;
  this.shouldCountResources = false;
  ItemStack item;
  for(int i = 27; i < 30; i++)
    {
    item = inventory.getStackInSlot(i);
    if(item==null){continue;}
    if(item.getItem()==Items.nether_wart)
      {
      netherWartCount+=item.stackSize;
      }
    if(item.getItem() instanceof ItemBlock)
      {
      ItemBlock ib = (ItemBlock)item.getItem();
      if(ib.field_150939_a==Blocks.red_mushroom || ib.field_150939_a==Blocks.brown_mushroom)
        {
        mushroomCount+=item.stackSize;
        }
      }
    }
  AWLog.logDebug("resources...: "+mushroomCount+" :: "+netherWartCount);
  }

@Override
public void doPlayerWork(EntityPlayer player)
  {
  //noop
  }

@Override
public boolean hasWork()
  {  
  return canWork() && (workerRescanDelay<=0 || hasWorkBlock());
  }

private boolean hasWorkBlock()
  {
  return !blocksToHarvest.isEmpty() || (!blocksToPlantMushroom.isEmpty() && mushroomCount>0) || (!blocksToPlantNetherWart.isEmpty() && netherWartCount>0);
  }

@Override
public void doWork(IWorker worker)
  {
  if(workerRescanDelay<=0 || !hasWorkBlock())
    {
    rescan();
    }  
  if(hasWorkBlock())
    {
    processWork();
    }
  }

private void rescan()
  {
  AWLog.logDebug("rescanning mushroom farm..");
  this.workerRescanDelay = AWAutomationStatics.automationWorkerRescanTicks;
  this.blocksToHarvest.clear();
  this.blocksToPlantMushroom.clear();
  this.blocksToPlantNetherWart.clear();
  
  Block block;
  for(BlockPosition pos : getUserSetTargets())
    {
    pos = pos.copy();
    if(worldObj.isAirBlock(pos.x, pos.y, pos.z))
      {
      block = worldObj.getBlock(pos.x, pos.y-1, pos.z);
      if(block==Blocks.soul_sand)
        {
        blocksToPlantNetherWart.add(pos);
        }
      else if(Blocks.brown_mushroom.canPlaceBlockAt(worldObj, pos.x, pos.y, pos.z))
        {
        AWLog.logDebug("added block to plant: "+pos);
        blocksToPlantMushroom.add(pos);
        }
      else
        {
        AWLog.logDebug("could not select block for planting: "+pos);
        }
      }
    else//not an air block, check for harvestable nether-wart
      {
      block = worldObj.getBlock(pos.x, pos.y, pos.z);
      if(block==Blocks.nether_wart && worldObj.getBlockMetadata(pos.x, pos.y, pos.z)>=3)        
        {
        blocksToHarvest.add(pos);
        }
      else if(block==Blocks.red_mushroom || block==Blocks.brown_mushroom)
        {
        Set<BlockPosition> harvestSet = new HashSet<BlockPosition>();
        TreeFinder.findAttachedTreeBlocks(block, worldObj, pos.x, pos.y, pos.z, harvestSet);
        for(BlockPosition tp : harvestSet)
          {
          if(!getUserSetTargets().contains(tp) && !blocksToHarvest.contains(tp))//don't harvest user-set planting blocks...
            {
            blocksToHarvest.add(tp);
            }
          }
        }
      }
    }
  this.markDirty();
  AWLog.logDebug("found: "+blocksToPlantMushroom.size()+" mushroom targets and "+blocksToPlantNetherWart.size() +" nether wart targets");
  }

private void processWork()
  {
  Iterator<BlockPosition> it;
  if(!blocksToPlantMushroom.isEmpty())
    {
    AWLog.logDebug("planting mushroom...");
    it = blocksToPlantMushroom.iterator();
    BlockPosition pos;
    ItemStack item;
    for(int i = 27; i < 30; i++)
      {
      item = inventory.getStackInSlot(i);
      if(item==null){continue;}
      if(item.getItem() instanceof ItemBlock)
        {
        ItemBlock ib = (ItemBlock)item.getItem();
        if(ib.field_150939_a==Blocks.red_mushroom || ib.field_150939_a==Blocks.brown_mushroom)
          {
          while(it.hasNext() && (pos=it.next())!=null)
            {
            it.remove();
            if(worldObj.isAirBlock(pos.x, pos.y, pos.z) && ib.field_150939_a.canBlockStay(worldObj, pos.x, pos.y, pos.z))
              {
              worldObj.setBlock(pos.x, pos.y, pos.z, ib.field_150939_a);
              //plant the mushroom, decrease stack size
              item.stackSize--;
              mushroomCount--;
              if(item.stackSize<=0){inventory.setInventorySlotContents(i, null);}
              break;              
              }
            } 
          break;
          }
        }
      }    
    }
  else if(!blocksToPlantNetherWart.isEmpty())
    {
    AWLog.logDebug("planting nether wart");
    it = blocksToPlantNetherWart.iterator();
    BlockPosition pos;
    ItemStack item;
    for(int i = 27; i < 30; i++)
      {
      item = inventory.getStackInSlot(i);
      if(item==null){continue;}
      if(item.getItem()==Items.nether_wart)
        {
        while(it.hasNext() && (pos=it.next())!=null)
          {
          it.remove();
          if(worldObj.isAirBlock(pos.x, pos.y, pos.z) && Blocks.nether_wart.canBlockStay(worldObj, pos.x, pos.y, pos.z))
            {
            worldObj.setBlock(pos.x, pos.y, pos.z, Blocks.nether_wart);
            item.stackSize--;
            netherWartCount--;
            if(item.stackSize<=0){inventory.setInventorySlotContents(i, null);}
            break;              
            }
          } 
        break;
        }
      }     
    }
  else if(!blocksToHarvest.isEmpty())
    {
    AWLog.logDebug("harvesting mushroom..");
    it = blocksToHarvest.iterator();
    BlockPosition pos;
    Block block;
    while(it.hasNext() && (pos=it.next())!=null)
      {
      it.remove();
      block = worldObj.getBlock(pos.x, pos.y, pos.z);
      if(block==Blocks.nether_wart || block==Blocks.red_mushroom || block==Blocks.brown_mushroom_block)
        {
        List<ItemStack> blockDrops = BlockTools.breakBlock(worldObj, getOwningPlayer(), pos.x, pos.y, pos.z, 0);
        for(ItemStack item : blockDrops)
          {
          addStackToInventory(item, InventorySide.TOP);
          }
        break;
        }
      }
    }
  this.markDirty();
  }

@Override
public WorkType getWorkType()
  {
  return WorkType.FARMING;
  }

@Override
public void onInventoryChanged()
  {
  this.shouldCountResources = true;
  }

@Override
public void initWorkSite()
  {
  //noop
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
  targets.addAll(blocksToPlantMushroom);
  targets.addAll(blocksToHarvest);  
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
