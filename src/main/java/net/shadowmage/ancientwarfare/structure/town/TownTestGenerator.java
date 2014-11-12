package net.shadowmage.ancientwarfare.structure.town;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.structure.template.build.StructureBB;
import net.shadowmage.ancientwarfare.structure.town.TownTemplate.TownStructureEntry;

public class TownTestGenerator
{

public static TownTemplate testTemplate;

public static void load()
  {
  testTemplate = new TownTemplate("testTown");  
  testTemplate.getStructureEntries().add(new TownStructureEntry("town_house1", 1, 1, 100, 10));
  testTemplate.getStructureEntries().add(new TownStructureEntry("town_house2", 1, 1, 100, 10));
  testTemplate.getStructureEntries().add(new TownStructureEntry("town_house3", 1, 1, 100, 10));
  testTemplate.getStructureEntries().add(new TownStructureEntry("town_house4", 1, 1, 100, 10));
  testTemplate.getStructureEntries().add(new TownStructureEntry("town_house5", 1, 1, 100, 10));
  testTemplate.getStructureEntries().add(new TownStructureEntry("town_house6", 1, 1, 100, 10));
  testTemplate.setTownHallEntry(new TownStructureEntry("town_hall1", 1, 1, 100, 10));
  testTemplate.setMinSize(90);
  testTemplate.setMaxSize(128);
  testTemplate.setMaxValue(1000);
  testTemplate.setWallStyle(1);
  testTemplate.setWallSize(7);
  testTemplate.addWall(0, "wall_straight1", 10);
  testTemplate.addCornerWall("wall_corner1", 10);
  }

public static void test()
  {
  Generator gen = new Generator(9*16, 9*16, 0);
  gen.generate();  
  }

/**
 * find optimal grid size.
 *  structure grid size = (max dimension for a non town-hall structure + 2)
 *    - this ensures that any structure can fit into any plot
 *    - perhaps adjust so that some structures take up 1x2 or 2x2 grid
 *    - use pre-selected structure grid size based on template settings? (still use 1x2 and 2x2 grids for large structs if needed)
 *  block grid size = (3*structureGridSize) + rng.nextInt(2) - 1 (so, 2-4)
 *  lay out blocks
 *  fill with structures, each block now has a discrete plot
 *  randomize which plot gets the extra bits (it doesn't matter)
 *  
 *  track front door nodes for each structure built.
 *  ensure front door nodes are connected to main roads (connect along block lines in direction of main road)
 * 
 */

private static class TownPlot
{
StructureBB bb;//bb of the plot
StructureBB structBB;//bb of the actual structure
BlockPosition doorPos;
Direction orientation;
boolean[] roadBorders;//what directions are adjacent to a road, can be 0-2 total sides (0=center plot, cannot have struct, can only merge with other plots or be 'cosmetic' structs)

public TownPlot(int x, int z, int width, int length)
  {
  bb = new StructureBB(new BlockPosition(x, 0, z), new BlockPosition(x + width-1, 0, z+length-1));
  roadBorders = new boolean[4];
  AWLog.logDebug("town plot: "+bb);
  }

/**
 * Expands THIS plot to include the passed in plot.<br>
 * The passed-in plot should be discarded as it is no longer valid
 * @param other
 */
public void merge(TownPlot other)
  {
  if(other.bb.min.x<bb.min.x){bb.min.x=other.bb.min.x;}
  if(other.bb.max.x>bb.max.x){bb.max.x=other.bb.max.x;}
  if(other.bb.min.z<bb.min.z){bb.min.z=other.bb.min.z;}
  if(other.bb.max.z>bb.max.z){bb.max.z=other.bb.max.z;}
  for(int i = 0; i < 4; i++)
    {
    if(other.roadBorders[i]){this.roadBorders[i]=true;}
    }
  }
}

private static class TownBlock
{

StructureBB bb;
List<TownPlot> plots;

public TownBlock(StructureBB bb)
  {
  this.bb = bb;
  AWLog.logDebug("created new town block: "+bb);
  plots = new ArrayList<TownPlot>();
  }

public void subdivide()
  {
  int xWidth = (bb.max.x - bb.min.x)+1;
  int zLength = (bb.max.z - bb.min.z)+1;
  int plotWidth = xWidth/3;
  int plotLength = zLength/3;
  for(int x = bb.min.x; x +plotWidth <= bb.max.x; x+=plotWidth)
    {
    for(int z = bb.min.z; z + plotLength <= bb.max.z; z+=plotLength)
      {
      plots.add(new TownPlot(x, z, plotWidth, plotLength));
      }
    }
  }

}

private static class TownQuadrant
{
BlockPosition startPos;
Direction xDirection;
Direction zDirection;
StructureBB bb;
List<TownBlock> blocks;
int blockSize;

public TownQuadrant(Direction xDir, Direction zDir, int x, int z, int width, int length, int blockSize)
  {  
  this.xDirection = xDir;
  this.zDirection = zDir;
  
  this.startPos = new BlockPosition(x, 0, z);
  BlockPosition endPos = startPos.copy();
  endPos.x += xDir.xDirection * (width-1);
  endPos.z += zDir.zDirection * (length-1);
  
  this.blockSize = blockSize;
  bb = new StructureBB(startPos, endPos);
  AWLog.logDebug("created new quadrant: "+bb);
  blocks = new ArrayList<TownBlock>();
  }

public void subdivide()
  {
  int widthToUse = (bb.max.x - bb.min.x) + 1;
  int lengthToUse = (bb.max.z - bb.min.z) + 1;
  int xDivs = widthToUse/blockSize;
  if(widthToUse%blockSize!=0){xDivs++;}
  int zDivs = lengthToUse/blockSize;
  if(lengthToUse%blockSize!=0){zDivs++;}
  
  int xDir = xDirection.xDirection;
  int zDir = zDirection.zDirection;  
  int xStart, xEnd;
  int zStart, zEnd;
  int xSize, zSize;
  
  for(int x = 0; x<xDivs; x++)
    {
    xStart = startPos.x + (xDir * x)*blockSize;
    xSize = widthToUse>blockSize ? blockSize : widthToUse;
    xEnd = xStart + xDir * (xSize-1);
    
    for(int z = 0; z<zDivs; z++)
      {
      zStart = startPos.z + (zDir * z)*blockSize;
      zSize = lengthToUse>blockSize ? blockSize : lengthToUse;
      zEnd = zStart + zDir * (zSize-1);
      
      blocks.add(new TownBlock(new StructureBB(new BlockPosition(xStart, 0, zStart), new BlockPosition(xEnd, 0, zEnd))));
      lengthToUse-=blockSize;
      }
    lengthToUse = (bb.max.z - bb.min.z) + 1;
    widthToUse -= blockSize;
    }

  for(TownBlock block : blocks){block.subdivide();}
  }

}


private static class Generator
{

Random rng;
byte[] testGrid;
private int blockSize;
private int width, length;
private Direction orientation;

public Generator(int width, int length, int orientation)
  {
  this.blockSize = 30;
  this.width = width;
  this.length = length;
  this.orientation = Direction.fromFacing(orientation);
  this.testGrid = new byte[width*length];
  rng = new Random(0);
  }

public void generate()
  { 
  int halfWidth = width/2;
  int halfLength = length/2;
  int centerX = halfWidth;
  int centerZ = halfLength;
  halfWidth-=3;
  halfLength-=3;
  
  TownQuadrant tq = new TownQuadrant(Direction.WEST, Direction.NORTH, centerX-4, centerZ-4, halfWidth, halfLength, blockSize);
  tq.subdivide();
  write(tq);
  
  tq = new TownQuadrant(Direction.EAST, Direction.NORTH, centerX+3, centerZ-4, halfWidth, halfLength, blockSize);
  tq.subdivide();
  write(tq);
  
  tq = new TownQuadrant(Direction.EAST, Direction.SOUTH, centerX+3, centerZ+3, halfWidth, halfLength, blockSize);
  tq.subdivide();
  write(tq);
  
  tq = new TownQuadrant(Direction.WEST, Direction.SOUTH, centerX-4, centerZ+3, halfWidth, halfLength, blockSize);
  tq.subdivide();
  write(tq);
  
  write(centerX, centerZ, (byte)9);
  write(centerX-1, centerZ, (byte)9);
  write(centerX, centerZ-1, (byte)9);
  write(centerX-1, centerZ-1, (byte)9);
  String line = writeTestGrid();
  AWLog.logDebug("grid: \n"+line);
  }

private void write(TownQuadrant tq)
  {
  for(TownBlock tb : tq.blocks)
    {
    write(tb);
    }
  }

private void write(TownBlock tb)
  {
  for(int x = tb.bb.min.x; x<=tb.bb.max.x; x++)
    {
    write(x, tb.bb.min.z, (byte)1);
    write(x, tb.bb.max.z, (byte)1);
    }
  for(int z = tb.bb.min.z; z<=tb.bb.max.z; z++)
    {
    write(tb.bb.min.x, z, (byte)1);
    write(tb.bb.max.x, z, (byte)1);
    }
//  for(TownPlot tp : tb.plots)
//    {
//    write(tp);
//    }
  }

private void write(TownPlot tp)
  {
  for(int x = tp.bb.min.x; x<=tp.bb.max.x; x++)
    {
    write(x, tp.bb.min.z, (byte)2);
    write(x, tp.bb.max.z, (byte)2);
    }
  for(int z = tp.bb.min.z; z<=tp.bb.max.z; z++)
    {
    write(tp.bb.min.x, z, (byte)2);
    write(tp.bb.max.x, z, (byte)2);
    }
  }

private boolean doesBBIntersect(StructureBB bb)
  {
  for(int x = bb.min.x; x<=bb.max.x; x++)
    {
    for(int z = bb.min.z; z<=bb.max.z; z++)
      {
      if(testGrid[getIndex(x, z)]!=0)
        {
        return true;
        }
      }
    }
  return false;
  }

/**
 * 
 * @param x
 * @param z
 * @param genWidth
 * @param genLength
 * @param type
 * @param grid
 * @param gridWidth
 */
private void genBuilding(int x, int z, int genWidth, int genLength, byte type)
  {
  for(int px = x; px < x + genWidth; px++)
    {
    for(int pz = z; pz < z + genLength; pz++)
      {
      write(px, pz, type);
      }
    }
  }

private void write(StructureBB bb, byte type)
  {
  for(int x = bb.min.x; x<=bb.max.x; x++)
    {
    for(int z = bb.min.z; z<=bb.max.z; z++)
      {
      write(x, z, type);
      }
    }
  }

private void write(int x, int z, byte val)
  {
  testGrid[getIndex(x, z)]=val;
  }

private int getIndex(int x, int z)
  {
  return z*width + x;
  }

private String writeTestGrid()
  {
  String out = "";
  for(int z = 0; z < length; z++)
    {
    for(int x = 0; x< width; x++)
      {
      out = out + String.valueOf(testGrid[getIndex(x,z)]);
      if(x<width-1)
        {
        out = out + ",";
        }
      }
    if(z<length-1)
      {
      out = out+"\n";
      }
    }
  return out;
  }

}

//public static final class BlockBB
//{
//int minX, minY, minZ;
//int maxX, maxY, maxZ;
//public BlockBB(int minX, int minY, int minZ, int maxX, int maxY, int maxZ)
//  {
//  
//  }
//}

public static enum Direction
{
NORTH ( 0 ,-1),
EAST  ( 1 , 0),
SOUTH ( 0 , 1),
WEST  (-1 , 0);
public final int xDirection;
public final int zDirection;
private Direction(int x, int z)
  {
  this.xDirection = x;
  this.zDirection = z;
  }
public Direction getLeft()
  {
  int o = ordinal();
  o--;
  if(o<0){o=values().length-1;}
  return values()[o];
  }
public Direction getRight()
  {
  int o = ordinal();
  o++;
  if(o>=values().length){o=0;}
  return values()[o];
  }
public Direction getOpposite()
  {
  int o = (ordinal()+2)%4;
  return values()[o];
  }
public static final Direction fromFacing(int face){return values()[face];}
}

}
