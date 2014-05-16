package net.shadowmage.ancientwarfare.automation.tile;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

public class QuarryPattern
{

public static final int CLEAR = 0;
public static final int SOLID_FILL = 1;
public static final int TORCH_FLOOR = 2;
public static final int TORCH_WALL = 3;
public static final int LADDER_0 = 4;
public static final int LADDER_1 = 5;

private List<QuarryLayer> layers = new ArrayList<QuarryLayer>();
private int xSize, zSize;

private int posX, posY, posZ;

public QuarryPattern(int xSize, int zSize)
  {
  this.xSize = xSize;
  this.zSize = zSize;
  layers.add(new QuarryLayer(xSize, zSize));
  }

public int getVal(int x, int y, int z)
  {
  y=y%layers.size();
  return layers.get(y).getVal(x, z);
  }

/**
 * return true if pattern is finished--no more work will be done
 * @return
 */
public boolean performAction(World world, WorkSiteQuarry quarry, int minX, int minZ, int yStart, boolean hasTorch, boolean hasLadder, boolean hasFill)
  {
  int tx = minX+posX, ty=yStart-posY, tz=minZ+posZ;
  int action = getVal(posX, posY, posZ);
  switch(action)
  {
  case CLEAR:
    {
    handleClear(quarry, tx, ty, tz);
    }
    break;
  case SOLID_FILL:
    {
    handleSolidFill(quarry, tx, ty, tz);
    }
    break;
  case LADDER_0:
    {
    handleLadder0(quarry, tx, ty, tz, hasLadder, hasFill);
    }
    break;
  case LADDER_1:
    {
    handleLadder1(quarry, tx, ty, tz, hasLadder, hasFill);
    }
    break;
  case TORCH_FLOOR:
    {
    handleTorchFloor(quarry, tx, ty, tz, hasTorch, hasFill);
    }
    break;
  case TORCH_WALL:
    {
    handleTorchWall(quarry, tx, ty, tz, hasTorch, hasFill);
    }
    break;
  default:
    {
    handleClear(quarry, tx, ty, tz);
    }
    break;
  }
  return findNextPosition(quarry, minX, yStart, minZ);
  }

private void handleClear(WorkSiteQuarry quarry, int x, int y, int z)
  {
  
  }

private void handleSolidFill(WorkSiteQuarry quarry, int x, int y, int z)
  {
  
  }

private void handleTorchWall(WorkSiteQuarry quarry, int x, int y, int z, boolean hasTorch, boolean hasFill)
  {
  
  }

private void handleTorchFloor(WorkSiteQuarry quarry, int x, int y, int z, boolean hasTorch, boolean hasFill){}

private void handleLadder0(WorkSiteQuarry quarry, int x, int y, int z, boolean hasLadder, boolean hasFill){}

private void handleLadder1(WorkSiteQuarry quarry, int x, int y, int z, boolean hasLadder, boolean hasFill){}

private boolean findNextPosition(WorkSiteQuarry quarry, int minX, int startY, int minZ)
  {
  World world = quarry.getWorldObj();
  while(!isPositionValid(world, minX, startY, minZ))
    {
    if(!incrementPosition(startY))
      {
      return false;
      }
    }
  return true;
  }

private boolean incrementPosition(int yStart)
  {
  posX++;
  if(posX>=xSize)
    {
    posX=0;
    posZ++;
    if(posZ>=zSize)
      {
      posZ=0;
      posY++;
      if(yStart-posY<=0)
        {
        return false;
        }
      }
    }
  return true;
  }

private boolean isPositionValid(World world, int minX, int startY, int minZ)
  {
  return true;
  }

private boolean isPositionValid(World world, int action, int x, int y, int z)
  {
  return true;
  }

public NBTTagCompound writeToNBT(NBTTagCompound tag)
  {
  NBTTagList layerList = new NBTTagList();
  for(QuarryLayer layer : layers)
    {
    layerList.appendTag(layer.writeToNBT(new NBTTagCompound()));
    }
  tag.setTag("layerList", layerList);
  tag.setInteger("xSize", xSize);
  tag.setInteger("zSize", zSize);
  return tag;
  }

public void readFromNBT(NBTTagCompound tag)
  {
  layers.clear();
  xSize = tag.getInteger("xSize");
  zSize = tag.getInteger("zSize");
  NBTTagList layerList = tag.getTagList("layerList", Constants.NBT.TAG_COMPOUND);
  for(int i = 0; i < layerList.tagCount();i++)
    {
    layers.add(new QuarryLayer(layerList.getCompoundTagAt(i), xSize, zSize));
    }
  }

public static final class QuarryLayer
{
int xSize, zSize;
byte[] pattern;

private QuarryLayer(NBTTagCompound tag, int xSize, int zSize)
  {
  this.xSize = xSize;
  this.zSize = zSize;
  readFromNBT(tag);
  }

public QuarryLayer(int xSize, int zSize)
  {
  this.xSize = xSize;
  this.zSize = zSize;
  this.pattern = new byte[xSize*zSize];
  }

public QuarryLayer(QuarryLayer layer)
  {
  this.xSize = layer.xSize;
  this.zSize = layer.zSize;
  this.pattern = new byte[layer.pattern.length];
  for(int i = 0; i < pattern.length; i++)
    {
    this.pattern[i] = layer.pattern[i];
    }
  }

public int getVal(int x, int z)
  {
  return pattern[x*zSize+z];
  }

public void setVal(int x, int z, int val)
  {
  pattern[x*zSize+z]=(byte)val;
  }

public NBTTagCompound writeToNBT(NBTTagCompound tag)
  {
  tag.setByteArray("pattern", pattern);
  return tag;
  }

public final void readFromNBT(NBTTagCompound tag)
  {
  this.pattern = tag.getByteArray("pattern");
  }
}

}
