package net.shadowmage.ancientwarfare.automation.tile;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.BlockSapling;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.shadowmage.ancientwarfare.automation.interfaces.IWorker;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.inventory.InventorySide;
import net.shadowmage.ancientwarfare.core.inventory.InventorySided;
import net.shadowmage.ancientwarfare.core.inventory.InventorySided.SlotItemFilter;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;

public class WorkSiteTreeFarm extends TileWorksiteBase
{

/**
 * need to find a way to setup the scanning for the tree-farm.
 * problems:
 *    1.) do not want to use constant polling
 *    2.) do not want to use sporadic polling
 *    3.) cannot find an event-driven method without wasting npc-work ticks, or doing massive updates to find work when work is reqeusted and not available (still wastes work ticks)
 *    4.) how to handle other mod-added sapling types?  Does forestry saplings extend vanilla saplings?
 *
 * solutions:
 *    1.) use sporadic polling -- prefer not to, but might be necessary...easiest to implement
 *    2.) allow npcs to waste 1 work tick every x seconds/minutes to trigger a rescan if no work is available
 *        hasWork  = !targets.isEmpty || (targets.isEmpty() && workScanDelay<=0)
 *    3.) --it makes sense to have an NPC do the 'scanning', but limit the frequency of scanning to 1/minute max
 *        to limit how it would effect entities jumping around to do work at multiple sites.
 *
 * other problems:
 *    1.) should allow invalid work-block references?  How to handle -- just do nothing for that work tick, or pull next work-block?
 *        presents problems of needing to occasionally re-validate stored work-target blocks.
 *        Probably should just dump all stored refs when rescanning, let the rescan do the validation, and allow
 *        workers to do no work for a couple ticks occasionally.
 *        
 */

boolean hasBonemeal;
boolean hasSapling;
int workerRescanDelay;
List<BlockPosition> blocksToChop;
List<BlockPosition> blocksToPlant;
List<BlockPosition> blocksToFertilize;

/**
 * 
 */
public WorkSiteTreeFarm()
  {
  this.canUserSetBlocks = true;
  this.canUpdate = true;
  this.maxWorkers = 1;
  
  blocksToChop = new ArrayList<BlockPosition>();
  blocksToPlant = new ArrayList<BlockPosition>();
  blocksToFertilize = new ArrayList<BlockPosition>();
  
  this.inventory = new InventorySided(27 + 3 + 3, this);
  
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
      if(stack.getItem() instanceof ItemBlock)
        {
        ItemBlock item = (ItemBlock) stack.getItem();
        return item.field_150939_a instanceof BlockSapling;//item.field_150939_a == ItemBlock.block
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
    
  filter = new SlotItemFilter()
    {
    @Override
    public boolean isItemValid(ItemStack stack)
      {
      if(stack==null){return true;}
      return stack.getItem() == Items.dye && stack.getItemDamage()==15;//TODO verify item dmg (its either 0 or 15)
      }
    };
  this.inventory.addSlotViewMap(InventorySide.REAR, 8, (3*18)+18+12+8+12, "guistrings.inventory.side.rear");
  for(int i = 30, k = 0; i < 33; i++, k++)
    {
    this.inventory.addSidedMapping(InventorySide.REAR, i, true, true);
    this.inventory.addSlotViewMapping(InventorySide.REAR, i, (k%9)*18, (k/9)*18);
    this.inventory.addSlotFilter(i, filter);
    }  
  }

@Override
public void updateEntity()
  {
  super.updateEntity();
  if(worldObj.isRemote){return;}
  }

@Override
public void markDirty()
  {
  super.markDirty();
  AWLog.logDebug("markDirty called in TE");
  }

@Override
public void doPlayerWork(EntityPlayer player)
  {
  
  }

@Override
public boolean hasWork()
  {  
  return workerRescanDelay<=0 || hasWorkBlock();
  }

private boolean hasWorkBlock()
  {
  if(!blocksToChop.isEmpty()){return true;}
  else if(!blocksToPlant.isEmpty() && hasSapling){return true;}
  else if(!blocksToFertilize.isEmpty() && hasBonemeal){return true;}
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
  if(!player.worldObj.isRemote)
    {
    NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_WORKSITE_INVENTORY, xCoord, yCoord, zCoord);
    return true;
    }
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
