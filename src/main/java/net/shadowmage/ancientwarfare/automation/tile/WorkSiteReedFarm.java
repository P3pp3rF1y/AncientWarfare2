package net.shadowmage.ancientwarfare.automation.tile;

import java.util.Collection;
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
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.InventorySided;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.RelativeSide;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.RotationType;
import net.shadowmage.ancientwarfare.core.inventory.ItemSlotFilter;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;

public class WorkSiteReedFarm extends TileWorksiteUserBlocks
{

Set<BlockPosition> cocoaToPlant;
Set<BlockPosition> cactusToPlant;
Set<BlockPosition> reedToPlant;
Set<BlockPosition> blocksToHarvest;

int reedCount;
int cactusCount;
int cocoaCount;

boolean shouldCountResources;

public WorkSiteReedFarm()
  {
  this.shouldCountResources = true;
    
  cocoaToPlant = new HashSet<BlockPosition>();
  cactusToPlant = new HashSet<BlockPosition>();
  reedToPlant = new HashSet<BlockPosition>();
  blocksToHarvest = new HashSet<BlockPosition>();
  
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
protected boolean processWork()
  {
  if(!blocksToHarvest.isEmpty())
    {
    Iterator<BlockPosition> it = blocksToHarvest.iterator();
    BlockPosition p;
    while(it.hasNext())
      {
      p = it.next();
      it.remove();
      return harvestBlock(p);      
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
        return true;
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
        return true;
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
        return true;
        }
      }
    }
  return false;    
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

private boolean harvestBlock(BlockPosition p)
  {
  Block block = worldObj.getBlock(p.x, p.y, p.z);
  if(block==Blocks.cactus || block==Blocks.reeds || block==Blocks.cocoa)
    {
    List<ItemStack> items = BlockTools.breakBlock(worldObj, p.x, p.y, p.z, 0);
    for(ItemStack item : items)
      {
      addStackToInventory(item, RelativeSide.TOP);
      }
    return true;
    }  
  return false;
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
public boolean onBlockClicked(EntityPlayer player)
  {
  if(!player.worldObj.isRemote)
    {
    NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_WORKSITE_REED_FARM, xCoord, yCoord, zCoord);
    return true;
    }
  return false;
  }

@Override
protected void fillBlocksToProcess(Collection<BlockPosition> targets)
  { 
  targets.addAll(getUserSetTargets());  
  }

@Override
protected void scanBlockPosition(BlockPosition pos)
  {
  Block block;
  block = worldObj.getBlock(pos.x, pos.y, pos.z);    
  if(block==Blocks.cactus)//find top of cactus, harvest from top down (leave 1 at bottom)
    {
    for(int y = pos.y + 4; y>pos.y; y--)
      {
      block = worldObj.getBlock(pos.x, y, pos.z);
      if(block==Blocks.cactus)
        {
        blocksToHarvest.add(new BlockPosition(pos.x, y, pos.z));
        }
      }
    }
  else if(block==Blocks.reeds)//find top of reeds, harvest from top down (leave 1 at bottom)
    {
    for(int y = pos.y + 4; y>pos.y; y--)
      {
      block = worldObj.getBlock(pos.x, y, pos.z);
      if(block==Blocks.reeds)
        {
        blocksToHarvest.add(new BlockPosition(pos.x, y, pos.z));
        }
      }
    }
  else if(block==Blocks.cocoa)
    {
    int meta = worldObj.getBlockMetadata(pos.x, pos.y, pos.z);
    if(meta >= 8 && meta <=11)
      {
      blocksToHarvest.add(pos.copy());
      }
    }
  else if(block==Blocks.air)//check for plantability for each type
    {
    if(Blocks.cactus.canBlockStay(worldObj, pos.x, pos.y, pos.z))
      {
      cactusToPlant.add(pos.copy());
      }
    else if(Blocks.reeds.canBlockStay(worldObj, pos.x, pos.y, pos.z))
      {
      reedToPlant.add(pos.copy());
      }
    else if(Blocks.cocoa.canBlockStay(worldObj, pos.x, pos.y, pos.z))
      {
      cocoaToPlant.add(pos.copy());
      }
    }
  }

@Override
protected boolean hasWorksiteWork()
  {
  return (reedCount>0 && !reedToPlant.isEmpty()) || (cactusCount>0 && !cactusToPlant.isEmpty()) || (cocoaCount>0 && !cocoaToPlant.isEmpty()) || !blocksToHarvest.isEmpty();
  }

@Override
protected void updateBlockWorksite()
  {
  worldObj.theProfiler.startSection("Count Resources");  
  if(shouldCountResources){countResources();}  
  worldObj.theProfiler.endSection();
  }

}
