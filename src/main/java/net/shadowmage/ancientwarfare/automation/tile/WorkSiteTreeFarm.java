package net.shadowmage.ancientwarfare.automation.tile;

import net.minecraft.block.BlockSapling;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.shadowmage.ancientwarfare.automation.interfaces.IWorker;
import net.shadowmage.ancientwarfare.core.inventory.InventorySide;
import net.shadowmage.ancientwarfare.core.inventory.InventorySided;
import net.shadowmage.ancientwarfare.core.inventory.InventorySided.SlotItemFilter;

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
 *
 * other problems:
 *    1.) should allow invalid work-block references?  How to handle -- just do nothing for that work tick, or pull next work-block?
 *        presents problems of needing to occasionally re-validate stored work-target blocks.
 *        Probably should just dump all stored refs when rescanning, let the rescan do the validation, and allow
 *        workers to do no work for a couple ticks occasionally.
 *        
 */
public WorkSiteTreeFarm()
  {
  this.canUserSetBlocks = true;
  this.canUpdate = true;
  this.maxWorkers = 1;
  this.inventory = new InventorySided(27 + 3 + 3, this);
  this.inventory.addSlotViewMap(InventorySide.TOP, 8, 8, "guistrings.inventory.side.top");
  for(int i =0; i <27; i++)
    {
    this.inventory.addSidedMapping(InventorySide.TOP, i, true, true);
    this.inventory.addSlotViewMapping(InventorySide.TOP, i, (i%9)*18, (i/9)*18);
    }
  this.inventory.addSlotViewMap(InventorySide.FRONT, 8, (3*18)+12+8, "guistrings.inventory.side.front");
  for(int i = 27, k = 0; i<30; i++, k++)
    {
    this.inventory.addSidedMapping(InventorySide.LEFT, i, true, true);
    this.inventory.addSidedMapping(InventorySide.RIGHT, i, true, true);
    this.inventory.addSlotViewMapping(InventorySide.FRONT, i, (k%9)*18, (k/9)*18);
    }
  this.inventory.addSlotViewMap(InventorySide.REAR, 8, (3*18)+18+12+8+12, "guistrings.inventory.side.rear");
  
  SlotItemFilter filter = new SlotItemFilter()
    {
    @Override
    public boolean isItemValid(ItemStack stack)
      {
      if(stack==null){return true;}
      if(stack.getItem() instanceof ItemBlock)
        {
        ItemBlock item = (ItemBlock) stack.getItem();
        return item.field_150939_a instanceof BlockSapling;
        }
      return false;
      }
    };
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
