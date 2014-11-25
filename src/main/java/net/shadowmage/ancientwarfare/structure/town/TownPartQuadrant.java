package net.shadowmage.ancientwarfare.structure.town;

import java.util.List;

import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.structure.template.build.StructureBB;

public class TownPartQuadrant
{

protected StructureBB bb;
private Direction xDir, zDir;
protected int xDivs, zDivs;
private boolean roadBorders[];
protected TownPartBlock blocks[];

public TownPartQuadrant(Direction xDir, Direction zDir, StructureBB bb, boolean[] borders)
  {  
  this.xDir= xDir;
  this.zDir = zDir;
  this.bb = bb;
  this.roadBorders = borders;  
  }

public boolean hasRoadBorder(Direction d)
  {
  return roadBorders[d.ordinal()];
  }

protected void setRoadBorder(Direction d, boolean val)
  {
  roadBorders[d.ordinal()]=val;
  }

public void subdivide(int blockSize, int plotSize)
  {
  int totalWidth = (bb.max.x - bb.min.x);
  int totalLength = (bb.max.z - bb.min.z);  
  int widthToUse = totalWidth;
  int lengthToUse = totalLength;
  
  widthToUse--;//the forced road edge for first block
  lengthToUse--;//the forced road edge for first block
  while(widthToUse>0)
    {    
    widthToUse-=blockSize;
    widthToUse-=2;//end edge of block + front edge of next block
    xDivs++;
    }
  while(lengthToUse>0)
    {
    lengthToUse-=blockSize;
    lengthToUse-=2;
    zDivs++;
    }
  
  blocks = new TownPartBlock[xDivs*zDivs];
  int xStart, xEnd;
  int zStart, zEnd;
  int xSize, zSize;
  int xIndex, zIndex;
  TownPartBlock block;

  widthToUse = totalWidth;
  xStart = xDir.xDirection < 0 ? bb.max.x-1 : bb.min.x+1;
  for(int x = 0; x<xDivs; x++)
    {    
    xSize = widthToUse > blockSize ? blockSize : widthToUse;
    xEnd = xStart + xDir.xDirection * (xSize - 1);    
    xIndex = xDir == Direction.WEST? (xDivs-1)-x : x;

    zStart = zDir.zDirection<0 ? bb.max.z-1 : bb.min.z+1;
    lengthToUse = (bb.max.z - bb.min.z);
    for(int z = 0; z<zDivs; z++)
      {           
      zSize = lengthToUse > blockSize ? blockSize : lengthToUse;
      zEnd = zStart + zDir.zDirection * (zSize - 1);
      zIndex = zDir==Direction.NORTH ? (zDivs-1)-z : z;
      
      block = new TownPartBlock(this, new StructureBB(new BlockPosition(xStart, 0, zStart), new BlockPosition(xEnd, 0, zEnd)), xIndex, zIndex, getBorders(xIndex, zIndex));
      setBlock(block, xIndex, zIndex);
      block.subdivide(plotSize);
      
      lengthToUse -= (blockSize+2);
      zStart = zEnd + zDir.zDirection * 3;
      }
    
    widthToUse -= (blockSize+2);
    xStart = xEnd + xDir.xDirection * 3;
    }
  }

private void setBlock(TownPartBlock tb, int x, int z)
  {
  blocks[getIndex(x, z)]=tb;
  }

protected TownPartBlock getBlock(int x, int z)
  {
  return blocks[getIndex(x, z)];
  }

private int getIndex(int x, int z)
  {
  return z*xDivs + x;
  }

private boolean[] getBorders(int x, int z)
  {
  boolean[] borders = new boolean[4];
  if(zDir==Direction.NORTH)
    {
    borders[Direction.SOUTH.ordinal()]=true;//has south
    borders[Direction.NORTH.ordinal()]=z>0;//not on northern edge
    }
  else//zDir==Direction.SOUTH
    {
    borders[Direction.NORTH.ordinal()]=true;//has south
    borders[Direction.SOUTH.ordinal()]=z < zDivs-1;//not on souther edge
    }
  if(xDir==Direction.WEST)
    {
    borders[Direction.EAST.ordinal()]=true;//has east
    borders[Direction.WEST.ordinal()]=x>0;
    }
  else
    {
    borders[Direction.WEST.ordinal()]=true;
    borders[Direction.EAST.ordinal()]= x < xDivs - 1;
    }  
  return borders;
  }

public void addBlocks(List<TownPartBlock> blocks)
  {
  for(int i = 0; i< this.blocks.length; i++)
    {
    blocks.add(this.blocks[i]);
    }
  }

public Direction getXDir()
  {
  return xDir;
  }

public Direction getZDir()
  {
  return zDir;
  }

}
