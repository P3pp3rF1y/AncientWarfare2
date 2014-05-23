package net.shadowmage.ancientwarfare.npc.orders;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;

public class UpkeepOrder extends NpcOrders
{

BlockPosition upkeepPosition;
int upkeepDimension;
int blockSide;
Block block;
int blockMeta;

public UpkeepOrder()
  {
  
  }

public void changeBlockSide()
  {
  blockSide++;
  if(blockSide>5){blockSide=0;}
  }

public void removeUpkeepPoint()
  {
  upkeepPosition = null;
  block = null;
  blockMeta = 0;
  blockSide = 0;
  upkeepDimension = 0;
  }

public int getUpkeepBlockSide()
  {
  return blockSide;
  }

public int getUpkeepDimension()
  {
  return upkeepDimension;
  }

public BlockPosition getUpkeepPosition()
  {
  return upkeepPosition;
  }

public Block getBlock()
  {
  return block;
  }

public int getBlockMeta()
  {
  return blockMeta;
  }

public boolean addUpkeepPosition(World world, BlockPosition pos)
  {
  upkeepPosition = pos;
  upkeepDimension = world.provider.dimensionId;
  blockSide = 0;
  block = world.getBlock(pos.x, pos.y, pos.z);
  blockMeta = world.getBlockMetadata(pos.x, pos.y, pos.z);
  return true;
  }

@Override
public void readFromNBT(NBTTagCompound tag)
  {
  if(tag.hasKey("upkeepPosition"))
    {
    upkeepPosition = new BlockPosition(tag.getCompoundTag("upkeepPosition"));
    upkeepDimension = tag.getInteger("dim");
    blockSide = tag.getInteger("side");
    block = Block.getBlockFromName(tag.getString("block"));
    blockMeta = tag.getInteger("blockMeta");
    }
  }

@Override
public NBTTagCompound writeToNBT(NBTTagCompound tag)
  {
  if(upkeepPosition!=null)
    {
    tag.setTag("upkeepPosition", upkeepPosition.writeToNBT(new NBTTagCompound()));
    tag.setInteger("dim", upkeepDimension);
    tag.setInteger("side", blockSide);
    tag.setString("block", Block.blockRegistry.getNameForObject(block));
    tag.setInteger("blockMeta", blockMeta);
    }
  return tag;
  }

}
