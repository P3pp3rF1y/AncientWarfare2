package net.shadowmage.ancientwarfare.npc.orders;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;

public class WorkOrder extends NpcOrders
{

private WorkPriorityType priorityType = WorkPriorityType.ROUTE;

private List<WorkEntry> entries = new ArrayList<WorkEntry>();

@Override
public void readFromNBT(NBTTagCompound tag)
  {
  entries.clear();
  NBTTagList entryList = tag.getTagList("entryList", Constants.NBT.TAG_COMPOUND);
  WorkEntry entry;
  for(int i = 0; i< entryList.tagCount(); i++)
    {
    entry = new WorkEntry();
    entry.readFromNBT(entryList.getCompoundTagAt(i));
    entries.add(entry);
    }
  priorityType = WorkPriorityType.values()[tag.getInteger("priorityType")];
  }

@Override
public NBTTagCompound writeToNBT(NBTTagCompound tag)
  {
  NBTTagList entryList = new NBTTagList();
  for(WorkEntry entry : entries)
    {
    entryList.appendTag(entry.writeToNBT(new NBTTagCompound()));
    }
  tag.setTag("entryList", entryList);
  tag.setInteger("priorityType", priorityType.ordinal());
  return tag;
  }

public WorkPriorityType getPriorityType()
  {
  return priorityType;
  }

public List<WorkEntry> getEntries()
  {
  return entries;
  }

public boolean addWorkPosition(World world, BlockPosition position)
  {
  if(entries.size()<8)
    {
    entries.add(new WorkEntry(world, position, world.provider.dimensionId, priorityType==WorkPriorityType.TIMED? 600 : 0));
    AWLog.logDebug("added new work entry (call from WorkOrder)");
    return true;
    }
  return false;//return true if successfully added
  }

public void removePosition(int index)
  {
  if(index>=0 && index<entries.size())
    {
    entries.remove(index);    
    }
  }

public void incrementPosition(int index)
  {
  if(index>=1 && index<entries.size())
    {
    WorkEntry entry = entries.remove(index);
    entries.add(index-1, entry);
    }
  }

public void decrementPosition(int index)
  {
  if(index>=0 && index<entries.size()-1)
    {
    WorkEntry entry = entries.remove(index);
    entries.add(index+1, entry);
    }
  }

@Override
public String toString()
  {
  return "Work Orders size: "+entries.size()+" of type: "+priorityType;
  }

public static final class WorkEntry
{

private Block block;
int blockMeta;
private BlockPosition position;
int dimension;
int workLength;

private WorkEntry(){}//nbt constructor

public WorkEntry(World world, BlockPosition position, int dimension, int workLength)
  {
  this.setBlock(world.getBlock(position.x, position.y, position.z));
  this.blockMeta = world.getBlockMetadata(position.x, position.y, position.z);
  this.setPosition(position);
  this.dimension = dimension;
  this.workLength = workLength;
  }

public void readFromNBT(NBTTagCompound tag)
  {
  setPosition(new BlockPosition(tag.getCompoundTag("pos")));
  dimension = tag.getInteger("dim");
  workLength = tag.getInteger("length");
  setBlock(Block.getBlockFromName(tag.getString("block")));
  blockMeta = tag.getInteger("blockMeta");
  }

public NBTTagCompound writeToNBT(NBTTagCompound tag)
  {
  tag.setTag("pos", getPosition().writeToNBT(new NBTTagCompound()));
  tag.setInteger("dim", dimension);
  tag.setInteger("length", workLength);
  tag.setString("block", Block.blockRegistry.getNameForObject(getBlock()));
  tag.setInteger("blockMeta", blockMeta);
  return tag;
  }

/**
 * @return the block
 */
public Block getBlock()
  {
    return block;
  }

/**
 * @param block the block to set
 */
public void setBlock(Block block)
  {
    this.block = block;
  }

/**
 * @return the position
 */
public BlockPosition getPosition()
  {
    return position;
  }

/**
 * @param position the position to set
 */
public void setPosition(BlockPosition position)
  {
    this.position = position;
  }
}

public static enum WorkPriorityType
{
PRIORITY_LIST,
ROUTE,
TIMED
}

}
