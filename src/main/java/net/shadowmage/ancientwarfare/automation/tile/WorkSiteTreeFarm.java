package net.shadowmage.ancientwarfare.automation.tile;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.Constants;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.InventorySided;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.RelativeSide;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.RotationType;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.inventory.ItemSlotFilter;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;

public class WorkSiteTreeFarm extends TileWorksiteBase
{

/**
 * flag should be set to true whenever updating inventory internally (e.g. harvesting blocks) to prevent
 * unnecessary inventory rescanning.  should be set back to false after blocks are added to inventory
 */
private boolean shouldCountResources = true;
int saplingCount;
int bonemealCount;
Set<BlockPosition> blocksToChop;
List<BlockPosition> blocksToPlant;
List<BlockPosition> blocksToFertilize;
/**
 * 
 */
public WorkSiteTreeFarm()
  {
  this.canUserSetBlocks = true;
  this.canUpdate = true;
  
  blocksToChop = new HashSet<BlockPosition>();
  blocksToPlant = new ArrayList<BlockPosition>();
  blocksToFertilize = new ArrayList<BlockPosition>();
  
  this.inventory = new InventorySided(this, RotationType.FOUR_WAY, 33);
  int[] topIndices = InventoryTools.getIndiceArrayForSpread(0, 27);
  int[] frontIndices = InventoryTools.getIndiceArrayForSpread(27, 3);
  int[] bottomIndices = InventoryTools.getIndiceArrayForSpread(30, 3);  
  this.inventory.setAccessibleSideDefault(RelativeSide.TOP, RelativeSide.TOP, topIndices);
  this.inventory.setAccessibleSideDefault(RelativeSide.FRONT, RelativeSide.FRONT, frontIndices);//saplings
  this.inventory.setAccessibleSideDefault(RelativeSide.BOTTOM, RelativeSide.BOTTOM, bottomIndices);//bonemeal
  ItemSlotFilter filter = new ItemSlotFilter()
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
protected void addWorkTargets(List<BlockPosition> targets)
  {
  targets.addAll(blocksToChop);
  targets.addAll(blocksToFertilize);
  targets.addAll(blocksToPlant);
  }

private void countResources()
  {
  shouldCountResources = false;
  saplingCount = 0;
  bonemealCount = 0;
  ItemStack stack;
  for(int i = 27; i < 33; i++)
    {
    stack = inventory.getStackInSlot(i);
    if(stack==null){continue;}
    if(stack.getItem() instanceof ItemBlock)
      {
      ItemBlock item = (ItemBlock) stack.getItem();
      if(item.field_150939_a instanceof BlockSapling)
        {
        saplingCount+=stack.stackSize;
        }
      }
    else if(stack.getItem()==Items.dye && stack.getItemDamage()==15)
      {
      bonemealCount+=stack.stackSize;
      }
    }
  AWLog.logDebug("rescanned inventory.  bonemeal: "+bonemealCount+" saplings: "+saplingCount);
  }

@Override
public void updateEntity()
  {
  super.updateEntity();
  if(worldObj.isRemote){return;}
  if(shouldCountResources){countResources();}  
  if(worldObj.getWorldTime()%20==0)
    {
    pickupSaplings();
    }
  }

@Override
protected boolean processWork()
  {
  BlockPosition position;
  if(!blocksToChop.isEmpty())
    {
    Iterator<BlockPosition> it = blocksToChop.iterator();
    position = it.next();
    it.remove();
    List<ItemStack> items = BlockTools.breakBlock(worldObj, getOwningPlayer(), position.x, position.y, position.z, 0);
    for(ItemStack item : items)
      {
      addStackToInventory(item, RelativeSide.TOP);
      }
    return true;
    }
  else if(saplingCount>0 && !blocksToPlant.isEmpty())
    {
    ItemStack stack = null;
    int slot = 27;
    for(int i = 27; i<30; i++)
      {
      stack = inventory.getStackInSlot(i);
      if(stack!=null && stack.getItem() instanceof ItemBlock && ((ItemBlock)stack.getItem()).field_150939_a instanceof BlockSapling)
        {        
        slot = i;
        break;
        }
      else
        {
        stack = null;        
        }
      } 
    if(stack!=null)//e.g. a sapling stack is present
      {
      Iterator<BlockPosition> it = blocksToPlant.iterator();
      while(it.hasNext() && (position=it.next())!=null)
        {
        it.remove();
        if(worldObj.isAirBlock(position.x, position.y, position.z))
          {
          Block block = ((ItemBlock)stack.getItem()).field_150939_a;
          worldObj.setBlock(position.x, position.y, position.z, block, stack.getItemDamage(), 3);
          saplingCount--;
          inventory.decrStackSize(slot, 1);
          return true;
          }
        }      
      }
    }
  else if(bonemealCount>0 && !blocksToFertilize.isEmpty())
    {
    Iterator<BlockPosition> it = blocksToFertilize.iterator();
    while(it.hasNext() && (position=it.next())!=null)
      {
      it.remove();
      Block block = worldObj.getBlock(position.x, position.y, position.z);
      if(block instanceof BlockSapling)
        {        
        ItemStack stack = null;
        for(int i = 30; i<33; i++)
          {
          stack = inventory.getStackInSlot(i);
          if(stack!=null && stack.getItem()==Items.dye&&stack.getItemDamage()==15)
            {        
            bonemealCount--;
            ItemDye.applyBonemeal(stack, worldObj, position.x, position.y, position.z, AncientWarfareCore.proxy.getFakePlayer((WorldServer) worldObj, owningPlayer));
            if(stack.stackSize<=0){inventory.setInventorySlotContents(i, null);}  
            block = worldObj.getBlock(position.x, position.y, position.z);
            if(block instanceof BlockSapling)
              {
              blocksToFertilize.add(position);//possible concurrent access exception?
              //technically, it would be, except by the time it hits this inner block, it is already
              //done iterating, as it will immediately hit the following break statement, and break
              //out of the iterating loop before the next element would have been iterated over
              }
            else if(block.getMaterial()==Material.wood)
              {
              TreeFinder.findAttachedTreeBlocks(block, worldObj, position.x, position.y, position.z, blocksToChop);
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

private void pickupSaplings()
  {
  BlockPosition p1 = getWorkBoundsMin();
  BlockPosition p2 = getWorkBoundsMax().copy().offset(1, 1, 1);
  AxisAlignedBB bb = AxisAlignedBB.getAABBPool().getAABB(p1.x, p1.y, p1.z, p2.x, p2.y, p2.z);
  List<EntityItem> items = worldObj.getEntitiesWithinAABB(EntityItem.class, bb);
  ItemStack stack;
  for(EntityItem item : items)
    {
    stack = item.getEntityItem();
    if(stack==null){continue;}
    if(stack.getItem()==Items.apple)
      {
      item.setDead();
      addStackToInventory(stack, RelativeSide.TOP);
      continue;
      }
    if(stack.getItem() instanceof ItemBlock)
      {
      ItemBlock ib = (ItemBlock)stack.getItem();
      if(ib.field_150939_a instanceof BlockSapling)
        {
        item.setDead();
        addStackToInventory(stack, RelativeSide.FRONT);
        }
      }
    }
  }

private void addTreeBlocks(BlockPosition base)
  {
  TreeFinder.findAttachedTreeBlocks(worldObj.getBlock(base.x, base.y, base.z), worldObj, base.x, base.y, base.z, blocksToChop);
  }

@Override
public WorkType getWorkType()
  {  
  return WorkType.FORESTRY;
  }

@Override
public boolean onBlockClicked(EntityPlayer player)
  {
  if(!player.worldObj.isRemote)
    {
    NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_WORKSITE_TREE_FARM, xCoord, yCoord, zCoord);
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

@Override
public void writeToNBT(NBTTagCompound tag)
  {
  super.writeToNBT(tag);
  if(!blocksToChop.isEmpty())
    {
    NBTTagList chopList = new NBTTagList();
    NBTTagCompound posTag;
    for(BlockPosition position : blocksToChop)
      {
      posTag = new NBTTagCompound();
      position.writeToNBT(posTag);
      chopList.appendTag(posTag);
      }
    tag.setTag("targetList", chopList);    
    }
  }

@Override
public void readFromNBT(NBTTagCompound tag)
  {
  super.readFromNBT(tag);
  if(tag.hasKey("targetList"))
    {
    NBTTagList chopList = tag.getTagList("targetList", Constants.NBT.TAG_COMPOUND);
    for(int i = 0; i < chopList.tagCount(); i++)
      {
      blocksToChop.add(new BlockPosition(chopList.getCompoundTagAt(i)));
      }
    }
  this.shouldCountResources = true;
  }

@Override
protected void fillBlocksToProcess()
  { 
  Set<BlockPosition> targets = new HashSet<BlockPosition>();
  targets.addAll(getUserSetTargets());  
  targets.removeAll(blocksToChop);
  targets.removeAll(blocksToFertilize);
  targets.removeAll(blocksToPlant);
  blocksToUpdate.addAll(targets);  
  }

@Override
protected void scanBlockPosition(BlockPosition pos)
  {
  Block block;
  if(worldObj.isAirBlock(pos.x, pos.y, pos.z))
    {
    block = worldObj.getBlock(pos.x, pos.y-1, pos.z);
    if(block==Blocks.dirt || block==Blocks.grass)
      {
      blocksToPlant.add(pos.copy().reassign(pos.x, pos.y, pos.z));
      }
    }
  else
    {
    block = worldObj.getBlock(pos.x, pos.y, pos.z);
    if(block instanceof BlockSapling)
      {
      blocksToFertilize.add(pos.copy().reassign(pos.x, pos.y, pos.z));
      }
    else if(block.getMaterial()==Material.wood && !blocksToChop.contains(pos))
      {
      BlockPosition p1 = pos.copy().reassign(pos.x, pos.y, pos.z);
      if(!blocksToChop.contains(p1))
        {
        addTreeBlocks(p1);          
        }
      }
    }
  }

}
