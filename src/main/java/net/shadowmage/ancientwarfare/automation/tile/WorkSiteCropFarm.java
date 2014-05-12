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
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.WorldServer;
import net.shadowmage.ancientwarfare.automation.config.AWAutomationStatics;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.core.interfaces.IWorker;
import net.shadowmage.ancientwarfare.core.inventory.InventorySide;
import net.shadowmage.ancientwarfare.core.inventory.InventorySidedWithContainer;
import net.shadowmage.ancientwarfare.core.inventory.ItemSlotFilter;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;

public class WorkSiteCropFarm extends TileWorksiteBase
{

Set<BlockPosition> blocksToTill;
Set<BlockPosition> blocksToHarvest;
Set<BlockPosition> blocksToPlant;
Set<BlockPosition> blocksToFertilize;
int plantableCount;
int bonemealCount;
int workerRescanDelay;
boolean shouldCountResources;

public WorkSiteCropFarm()
  {
  this.canUserSetBlocks = true;
  this.canUpdate = true;
  this.maxWorkers = 1;
  this.shouldSendWorkTargets = true;
  this.shouldCountResources = true;
  
  blocksToTill = new HashSet<BlockPosition>();
  blocksToHarvest = new HashSet<BlockPosition>();
  blocksToPlant = new HashSet<BlockPosition>();
  blocksToFertilize = new HashSet<BlockPosition>();
  
  this.inventory = new InventorySidedWithContainer(27 + 3 + 3, this);
  
  this.inventory.addSlotViewMap(InventorySide.TOP, 8, 8, "guistrings.inventory.side.top");
  for(int i =0; i <27; i++)
    {
    this.inventory.addSidedMapping(InventorySide.TOP, i, true, true);
    this.inventory.addSlotViewMapping(InventorySide.TOP, i, (i%9)*18, (i/9)*18);
    }
    
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
  this.inventory.addSlotViewMap(InventorySide.FRONT, 8, (3*18)+12+8, "guistrings.inventory.side.front");
  for(int i = 27, k = 0; i<30; i++, k++)
    {
    this.inventory.addSidedMapping(InventorySide.LEFT, i, true, true);
    this.inventory.addSidedMapping(InventorySide.RIGHT, i, true, true);
    this.inventory.addSlotViewMapping(InventorySide.FRONT, i, (k%9)*18, (k/9)*18);
    this.inventory.addSlotFilter(i, filter);
    }
    
  filter = new ItemSlotFilter()
    {
    @Override
    public boolean isItemValid(ItemStack stack)
      {
      if(stack==null){return true;}
      return stack.getItem() == Items.dye && stack.getItemDamage()==15;
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
  if(workerRescanDelay>0){workerRescanDelay--;}
  if(shouldCountResources){countResources();}
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
public boolean hasWork()
  {  
  return canWork() && (workerRescanDelay<=0 || hasWorkBlock());
  }

private boolean hasWorkBlock()
  {
  return !blocksToTill.isEmpty() || !blocksToHarvest.isEmpty() || (!blocksToPlant.isEmpty() && plantableCount>0) || (!blocksToFertilize.isEmpty() && bonemealCount>0);
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
  workerRescanDelay = AWAutomationStatics.automationWorkerRescanTicks;
  blocksToTill.clear();
  blocksToHarvest.clear();
  blocksToPlant.clear();
  blocksToFertilize.clear();  
 
  Block block;
  for(BlockPosition position : getUserSetTargets())
    {
    position = position.copy();
    block = worldObj.getBlock(position.x, position.y, position.z);
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
  this.markForUpdate();
  }

private void processWork()
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
        break;
        }
      }
    }
  else if(!blocksToHarvest.isEmpty())
    {
    List<ItemStack> blockDrops;
    it = blocksToHarvest.iterator();
    while(it.hasNext() && (position=it.next())!=null)
      {
      it.remove();
      block = worldObj.getBlock(position.x, position.y, position.z);
      if(block==Blocks.wheat || block==Blocks.carrots || block==Blocks.potatoes)
        {
        meta = worldObj.getBlockMetadata(position.x, position.y, position.z);
        if(meta<7){continue;}
        blockDrops = BlockTools.breakBlock(worldObj, position.x, position.y, position.z, 0);
        for(ItemStack item : blockDrops)
          {
          addStackToInventory(item, InventorySide.TOP);
          }
        break;
        }
      else if(block==Blocks.pumpkin || block==Blocks.melon_block)
        {
        blockDrops = BlockTools.breakBlock(worldObj, position.x, position.y, position.z, 0);
        for(ItemStack item : blockDrops)
          {
          item = InventoryTools.mergeItemStack(inventory, item, inventory.getAccessDirectionFor(InventorySide.TOP));
          if(item!=null)
            {
            InventoryTools.dropItemInWorld(worldObj, item, xCoord, yCoord, zCoord);
            }
          }
        break;
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
            if(block!=null)
              {
              worldObj.setBlock(position.x, position.y, position.z, block);
              }
            break;            
            }          
          }
        break;
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
            break;
            }
          }
        break;
        }
      }
    }
  this.markForUpdate();
  }

@Override
public WorkType getWorkType()
  {
  return WorkType.FARMING;
  }

@Override
public void onInventoryChanged()
  {
  shouldCountResources = true;
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
  targets.addAll(blocksToTill);
  targets.addAll(blocksToHarvest);
  targets.addAll(blocksToPlant);
  targets.addAll(blocksToFertilize);
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
public void doPlayerWork(EntityPlayer player)
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

}
