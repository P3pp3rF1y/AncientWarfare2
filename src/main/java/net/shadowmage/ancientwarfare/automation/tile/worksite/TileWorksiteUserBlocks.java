package net.shadowmage.ancientwarfare.automation.tile.worksite;

import net.minecraft.nbt.NBTTagCompound;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;


public abstract class TileWorksiteUserBlocks extends TileWorksiteBlockBased
{

private byte[] targetMap = new byte[16*16];

public TileWorksiteUserBlocks()
  {
  
  }

@Override
public boolean userAdjustableBlocks()
  {
  return true;
  }

protected boolean isTarget(BlockPosition p)
  {
  int x = p.x - bbMin.x;
  int z = p.z - bbMin.z;
  return targetMap[z*16 + x]==1;
  }

protected boolean isTarget(int x1, int y1)
  {
  int x = x1 - bbMin.x;
  int z = y1 - bbMin.z;
  return targetMap[z*16 + x]==1;
  }

public void onTargetsAdjusted()
  {
  //TODO implement to check target blocks, clear invalid ones
  }

@Override
protected void onBoundsSet()
  {
  int w = bbMax.x - bbMin.x + 1;
  int h = bbMax.z - bbMin.z + 1;
  for(int x = 0; x < w; x++)
    {
    for(int z = 0; z< h; z++)
      {
      targetMap[z*16 + x] = (byte)1;
      }
    }
  }

@Override
public void writeToNBT(NBTTagCompound tag)
  {
  super.writeToNBT(tag);
  tag.setByteArray("targetMap", targetMap);
  }

@Override
public void readFromNBT(NBTTagCompound tag)
  {
  super.readFromNBT(tag);
  if(tag.hasKey("targetMap")){targetMap = tag.getByteArray("targetMap");}
  }

public byte[] getTargetMap()
  {
  return targetMap;
  }

public void setTargetBlocks(byte[] targets)
  {
  targetMap = targets;
  }

}
