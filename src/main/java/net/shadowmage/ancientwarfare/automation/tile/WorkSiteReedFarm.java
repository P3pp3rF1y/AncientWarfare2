package net.shadowmage.ancientwarfare.automation.tile;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLog;
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
import net.shadowmage.ancientwarfare.core.util.InventoryTools;

public class WorkSiteReedFarm extends TileWorksiteBase
{

Set<BlockPosition> cocoaToPlant;
Set<BlockPosition> cactusToPlant;
Set<BlockPosition> reedToPlant;
Set<BlockPosition> blocksToHarvest;

int reedCount;
int cactusCount;
int cocoaCount;

int workerRescanDelay;
boolean shouldCountResources;

public WorkSiteReedFarm()
  {
  this.canUserSetBlocks = true;
  this.canUpdate = true;
  this.maxWorkers = 1;
  this.shouldSendWorkTargets = true;
  this.shouldCountResources = true;
    
  cocoaToPlant = new HashSet<BlockPosition>();
  cactusToPlant = new HashSet<BlockPosition>();
  reedToPlant = new HashSet<BlockPosition>();
  blocksToHarvest = new HashSet<BlockPosition>();
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
      if(item==Items.dye && stack.getItemDamage()==3)
        {       
        return true;
        }
      if(item==Items.reeds)
        {
        return true;
        }
      if(item instanceof ItemBlock)
        {
        ItemBlock block = (ItemBlock)item;
        Block blk = block.field_150939_a;
        if(blk==Blocks.cactus)
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

@Override
public boolean hasWork()
  {  
  return canWork() && (workerRescanDelay<=0 || hasWorkBlock());
  }

private boolean hasWorkBlock()
  {
  return !blocksToHarvest.isEmpty() || (!cactusToPlant.isEmpty() && cactusCount>0)  || (cocoaToPlant.isEmpty() && cocoaCount>0) || (!reedToPlant.isEmpty() && reedCount>0);
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
  this.workerRescanDelay = AWAutomationStatics.automationWorkerRescanTicks;
  this.cactusToPlant.clear();
  this.reedToPlant.clear();
  this.cocoaToPlant.clear();
  this.blocksToHarvest.clear();
  Block block;
  for(BlockPosition p : this.getUserSetTargets())
    {
    block = worldObj.getBlock(p.x, p.y, p.z);    
    if(block==Blocks.cactus)//find top of cactus, harvest from top down (leave 1 at bottom)
      {
      for(int y = p.y + 4; y>p.y; y--)
        {
        block = worldObj.getBlock(p.x, y, p.z);
        if(block==Blocks.cactus)
          {
          blocksToHarvest.add(new BlockPosition(p.x, y, p.z));
          }
        }
      }
    else if(block==Blocks.reeds)//find top of reeds, harvest from top down (leave 1 at bottom)
      {
      for(int y = p.y + 4; y>p.y; y--)
        {
        block = worldObj.getBlock(p.x, y, p.z);
        if(block==Blocks.reeds)
          {
          blocksToHarvest.add(new BlockPosition(p.x, y, p.z));
          }
        }
      }
    else if(block==Blocks.cocoa)
      {
      int meta = worldObj.getBlockMetadata(p.x, p.y, p.z);
      if(meta >= 8 && meta <=11)
        {
        blocksToHarvest.add(p.copy());
        }
      }
    else if(block==Blocks.air)//check for plantability for each type
      {
      if(Blocks.cactus.canBlockStay(worldObj, p.x, p.y, p.z))
        {
        cactusToPlant.add(p.copy());
        }
      else if(Blocks.reeds.canBlockStay(worldObj, p.x, p.y, p.z))
        {
        reedToPlant.add(p.copy());
        }
      else if(Blocks.cocoa.canBlockStay(worldObj, p.x, p.y, p.z))
        {
        cocoaToPlant.add(p.copy());
        }
      }
    }
  }

private void processWork()
  {
  if(!blocksToHarvest.isEmpty())
    {
    Iterator<BlockPosition> it = blocksToHarvest.iterator();
    BlockPosition p;
    while(it.hasNext())
      {
      p = it.next();
      it.remove();
      harvestBlock(p);
      break;
      }
    }
  else if(cocoaCount>0 && !cocoaToPlant.isEmpty())
    {
    Iterator<BlockPosition> it = cocoaToPlant.iterator();
    BlockPosition p;
    while(it.hasNext())
      {
      p = it.next();
      it.remove();
      if(plantCocoa(p))
        {
        break;
        }
      }
    }
  else if(reedCount>0 && !reedToPlant.isEmpty())
    {
    Iterator<BlockPosition> it = reedToPlant.iterator();
    BlockPosition p;
    while(it.hasNext())
      {
      p = it.next();
      it.remove();
      if(plantReeds(p))
        {
        break;
        }
      }
    }
  else if(cactusCount>0 && !cactusToPlant.isEmpty())
    {
    Iterator<BlockPosition> it = cactusToPlant.iterator();
    BlockPosition p;
    while(it.hasNext())
      {
      p = it.next();  
      it.remove();   
      if(plantCactus(p))
        {
        break;
        }
      }
    }
  }

private boolean removeItem(Item item)
  {
  ItemStack stack;
  for(int i =27; i<30; i++)
    {
    stack = inventory.getStackInSlot(i);
    if(stack==null){continue;}
    if(stack.getItem()==item)
      {
      inventory.decrStackSize(i, 1);
      return true;
      }
    }
  return false;
  }

private void harvestBlock(BlockPosition p)
  {
  Block block = worldObj.getBlock(p.x, p.y, p.z);
  if(block==Blocks.cactus || block==Blocks.reeds || block==Blocks.cocoa)
    {
    List<ItemStack> items = BlockTools.breakBlock(worldObj, p.x, p.y, p.z, 0);
    for(ItemStack item : items)
      {
      addStackToInventory(item, InventorySide.TOP);
      }
    }  
  }

private boolean plantCactus(BlockPosition p)
  {
  if(!worldObj.isAirBlock(p.x, p.y, p.z) || !Blocks.cactus.canBlockStay(worldObj, p.x, p.y, p.z))
    {
    return false;
    }
  if(removeItem(Item.getItemFromBlock(Blocks.cactus)))
    {
    worldObj.setBlock(p.x, p.y, p.z, Blocks.cactus);
    cactusCount--;
    return true;
    }
  return false;
  }

private boolean plantReeds(BlockPosition p)
  {
  if(!worldObj.isAirBlock(p.x, p.y, p.z) || !Blocks.reeds.canBlockStay(worldObj, p.x, p.y, p.z))
    {
    return false;
    }
  if(removeItem(Items.reeds))
    {
    worldObj.setBlock(p.x, p.y, p.z, Blocks.reeds);
    reedCount--;
    return true;
    }
  return false;
  }

private boolean plantCocoa(BlockPosition p)
  {
  if(!worldObj.isAirBlock(p.x, p.y, p.z) || !Blocks.cocoa.canBlockStay(worldObj, p.x, p.y, p.z))
    {
    return false;
    }
  int meta = -1;
  if(isJungleLog(p.x-1, p.y,p.z))
    {
    meta = getDirection(p.x, p.y, p.z, p.x-1, p.y, p.z);
    }
  else if(isJungleLog(p.x+1, p.y, p.z))
    {
    meta = getDirection(p.x, p.y, p.z, p.x+1, p.y, p.z);
    }
  else if(isJungleLog(p.x, p.y, p.z-1))
    {
    meta = getDirection(p.x, p.y, p.z, p.x, p.y, p.z-1);
    }
  else if( isJungleLog(p.x, p.y, p.z+1))
    {
    meta = getDirection(p.x, p.y, p.z, p.x, p.y, p.z+1);
    }
  boolean removedItem = false;
  ItemStack stack;
  for(int i =27; i<30; i++)
    {
    stack = inventory.getStackInSlot(i);
    if(stack==null){continue;}
    if(stack.getItem() == Items.dye && stack.getItemDamage()==3)
      {
      inventory.decrStackSize(i, 1);
      removedItem =  true;
      break;
      }
    }
  if(removedItem)
    {
    worldObj.setBlock(p.x, p.y, p.z, Blocks.cocoa, meta, 2);
    cocoaCount--;
    return true;
    }
  return false;
  }

protected boolean isJungleLog(int x, int y, int z)
  {
  return worldObj.getBlock(x, y, z)==Blocks.log && BlockLog.func_150165_c(worldObj.getBlockMetadata(x, y, z))==3;
  }

protected int getDirection(int x, int y, int z, int x1, int y1, int z1)
  {
  if(z1>z)
    {
    return 0;
    }
  else if(z1<z)
    {
    return 2;
    }
  else if(x1<x)
    {
    return 1;
    }
  else if(x1>x)
    {
    return 3;
    }
  return -1;
  }

private void countResources()
  {
  shouldCountResources = false;
  cactusCount = 0;
  reedCount = 0;
  cocoaCount = 0;
  ItemStack stack;
  Item item;
  for(int i = 27; i <30; i++)
    {
    stack = inventory.getStackInSlot(i);
    if(stack==null){continue;}
    item = stack.getItem();
    if(item==Items.dye && stack.getItemDamage()==3)
      {
      cocoaCount+=stack.stackSize;
      }
    else if(item==Items.reeds)
      {
      reedCount += stack.stackSize;
      }
    else if(item instanceof ItemBlock)
      {
      ItemBlock ib = (ItemBlock)item;
      if(ib.field_150939_a == Blocks.cactus)
        {
        cactusCount += stack.stackSize;
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
  this.shouldCountResources = true;
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
protected void addWorkTargets(List<BlockPosition> targets)
  {
  targets.addAll(blocksToHarvest);
  targets.addAll(cactusToPlant);
  targets.addAll(cocoaToPlant);
  targets.addAll(reedToPlant);
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
