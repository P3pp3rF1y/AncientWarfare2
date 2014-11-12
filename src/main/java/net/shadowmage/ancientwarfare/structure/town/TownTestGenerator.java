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
  Generator gen = new Generator(5*16, 5*16, 0);
  gen.generate();  
  }

/**
 * split town into quadrants
 *  each quadrant gets a generation direction (2) denoting the direction towards its exterior corner
 *  quadrants split themselves into town blocks based on town block input size, starting from the center of the town and extending outward; partial blocks will be on the outside of the town
 *  
 * 
 * split town blocks into town plots
 *  generation area for plots is shrunk by 1 block (x/z) for roads along the borders of town-blocks
 *  each plot gets a generation direction from the quadrant that contains it
 *  generation of plots occurs the same as generation of blocks, except using plot size (i.e. partials along the exterior borders)
 *    
 * each plot will be the target for building construction
 *  structures may use more than a single plot, up to and including the entire town block
 *  construction will happen at the town-block level
 *  each structure
 * 
 */

private static class TownPlot
{

TownBlock block;
StructureBB bb;//bb of the plot
boolean[] roadBorders;//what directions are adjacent to a road, can be 0-2 total sides (0=center plot, cannot have struct, can only merge with other plots or be 'cosmetic' structs)

public TownPlot(TownBlock block, StructureBB bb, boolean[] borders)
  {
  this.block = block;
  this.bb = bb;
  roadBorders = borders;
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

boolean[] roadBorders;
int plotSize = 10;
TownQuadrant quadrant;
StructureBB bb;
List<TownPlot> plots;

public TownBlock(TownQuadrant quadrant, StructureBB bb, boolean[] roadBorders)
  {
  this.quadrant = quadrant;
  this.bb = bb;
  this.roadBorders = roadBorders;
  AWLog.logDebug("created new town block: "+bb);
  plots = new ArrayList<TownPlot>();
  }

public void subdivide()
  {
  int xWidth = (bb.max.x - bb.min.x)+1;
  int zLength = (bb.max.z - bb.min.z)+1;
  if(roadBorders[1]){xWidth--;}
  if(roadBorders[3]){xWidth--;}
  if(roadBorders[0]){zLength--;}
  if(roadBorders[2]){zLength--;}
  int xDivs, zDivs;
  xDivs = xWidth/plotSize;
  if(xWidth%plotSize!=0){xDivs++;}
  zDivs = zLength/plotSize;
  if(zLength%plotSize!=0){zDivs++;}  
  
  AWLog.logDebug("dividing block into plots; "+xWidth+" : "+zLength+" :: "+xDivs+" : "+zDivs);
  int widthToUse, lengthToUse;
  int xStart, xEnd;
  int zStart, zEnd;  
  int xSize, zSize;
  
  boolean[] roadBorders;
  
  xStart = quadrant.xDir<0 ? bb.max.x : bb.min.x;
  if(quadrant.xDir<0 && this.roadBorders[1]){xStart--;}//if generating west && has eastern road
  else if(quadrant.xDir>0 && this.roadBorders[3]){xStart++;}//if generating east && has western road
  
  widthToUse = xWidth;
  for(int x = 0; x<xDivs; x++)
    {
    xSize = widthToUse > plotSize ? plotSize : widthToUse;
    xEnd = xStart + (xSize-1) * quadrant.xDir;
    
    zStart = quadrant.zDir<0 ? bb.max.z : bb.min.z;
    if(quadrant.zDir<0 && this.roadBorders[2]){zStart--;}//generation is to the north && has southern road
    else if(quadrant.zDir>0 && this.roadBorders[0]){zStart++;}//generation is to the south && has northern road
    lengthToUse = zLength;
    for(int z = 0; z<zDivs; z++)
      {
      roadBorders = new boolean[4];
      setRoadBorders(x==0, z==0, x==xDivs-1, z==zDivs-1, roadBorders);
      zSize = lengthToUse > plotSize ? plotSize : lengthToUse;
      zEnd = zStart + quadrant.zDir * (zSize - 1);
      
      plots.add(new TownPlot(this, new StructureBB(new BlockPosition(xStart, 0, zStart), new BlockPosition(xEnd, 0, zEnd)), roadBorders));      
      
      lengthToUse -= plotSize;
      zStart = zEnd + quadrant.zDir;
      }
    
    widthToUse -= plotSize;
    xStart = xEnd + quadrant.xDir;
    }
  }

private void setRoadBorders(boolean startX, boolean startZ, boolean endX, boolean endZ, boolean[] borders)
  {
  if(startX)
    {
    if(quadrant.xDir>0){borders[3]=true;}//w
    else{borders[1]=true;}//e
    }
  if(endX)
    {
    if(quadrant.xDir>0){borders[1]=true;}//e
    else{borders[3]=true;}//w
    }
  if(startZ)
    {
    if(quadrant.zDir>0){borders[0]=true;}//n
    else{borders[2]=true;}//s
    }
  if(endZ)
    {
    if(quadrant.zDir>0){borders[2]=true;}//s
    else{borders[0]=true;}//n
    }
  }

}

private static class TownQuadrant
{
int xDir;
int zDir;
StructureBB bb;
List<TownBlock> blocks;
int blockSize;
boolean roadBorders[];

public TownQuadrant(Direction xDir, Direction zDir, int x, int z, int width, int length, int blockSize)
  {  
  this.xDir = xDir.xDirection;
  this.zDir = zDir.zDirection;
  roadBorders = new boolean[4];
  roadBorders[0]=this.zDir < 0;
  roadBorders[2]=!roadBorders[0];
  roadBorders[1]=this.xDir < 0;
  roadBorders[3]=!roadBorders[1];
  
  BlockPosition startPos = new BlockPosition(x, 0, z);
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
  int widthToUse = (bb.max.x - bb.min.x);
  int lengthToUse = (bb.max.z - bb.min.z);
  int xDivs = widthToUse/blockSize;
  if(widthToUse%blockSize!=0){xDivs++;}
  int zDivs = lengthToUse/blockSize;
  if(lengthToUse%blockSize!=0){zDivs++;}
    
  int xStart, xEnd;
  int zStart, zEnd;
  int xSize, zSize;
  boolean roadBorders[];

  xStart = xDir < 0 ? bb.max.x-1 : bb.min.x+1;  
  for(int x = 0; x<xDivs; x++)
    {    
    xSize = widthToUse > blockSize ? blockSize : widthToUse;
    xEnd = xStart + xDir * (xSize - 1); 

    zStart = zDir<0 ? bb.max.z-1 : bb.min.z+1;
    lengthToUse = (bb.max.z - bb.min.z);
    for(int z = 0; z<zDivs; z++)
      {
      roadBorders = new boolean[4];
      roadBorders[0] = zDir>0 || (zDir<0 && z < zDivs-1);//has road on north side if generation direction is south or is not the last block in that direciton
      roadBorders[2] = zDir<0 || (zDir>0 && z < zDivs-1);//has road on south side if generation direction is north or is not the last block in that direction
      roadBorders[1] = xDir<0 || (xDir>0 && x < xDivs-1);//has road on east side if generation direction is west or is not the last block in that direction
      roadBorders[3] = xDir>0 || (xDir<0 && x < xDivs-1);//has road on west side if generation direction is east or is not the last block in that direction      
      
      zSize = lengthToUse > blockSize ? blockSize : lengthToUse;
      zEnd = zStart + zDir * (zSize - 1);
      
      blocks.add(new TownBlock(this, new StructureBB(new BlockPosition(xStart, 0, zStart), new BlockPosition(xEnd, 0, zEnd)), roadBorders));
      
      lengthToUse -= blockSize;
      zStart = zEnd + zDir;
      }
    
    widthToUse -= blockSize;
    xStart = xEnd + xDir;
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
  this.blockSize = 32;
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
  
  TownQuadrant tq = new TownQuadrant(Direction.WEST, Direction.NORTH, centerX-1, centerZ-1, halfWidth, halfLength, blockSize);
  tq.subdivide();
  write(tq);
  
  tq = new TownQuadrant(Direction.EAST, Direction.NORTH, centerX+0, centerZ-1, halfWidth, halfLength, blockSize);
  tq.subdivide();
  write(tq);
  
  tq = new TownQuadrant(Direction.EAST, Direction.SOUTH, centerX+0, centerZ+0, halfWidth, halfLength, blockSize);
  tq.subdivide();
  write(tq);
  
  tq = new TownQuadrant(Direction.WEST, Direction.SOUTH, centerX-1, centerZ+0, halfWidth, halfLength, blockSize);
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
  for(int x = tq.bb.min.x; x<=tq.bb.max.x; x++)
    {
    write(x, tq.bb.min.z, (byte)1);
    write(x, tq.bb.max.z, (byte)1);
    }
  for(int z = tq.bb.min.z; z<=tq.bb.max.z; z++)
    {
    write(tq.bb.min.x, z, (byte)1);
    write(tq.bb.max.x, z, (byte)1);
    }
  for(TownBlock tb : tq.blocks)
    {
    write(tb);
    }
  }

private void write(TownBlock tb)
  {
  for(int x = tb.bb.min.x; x<=tb.bb.max.x; x++)
    {
    write(x, tb.bb.min.z, (byte)2);
    write(x, tb.bb.max.z, (byte)2);
    }
  for(int z = tb.bb.min.z; z<=tb.bb.max.z; z++)
    {
    write(tb.bb.min.x, z, (byte)2);
    write(tb.bb.max.x, z, (byte)2);
    }
  for(TownPlot tp : tb.plots)
    {
    write(tp);
    }
  }

private void write(TownPlot tp)
  {
  for(int x = tp.bb.min.x; x<=tp.bb.max.x; x++)
    {
    write(x, tp.bb.min.z, (byte)3);
    write(x, tp.bb.max.z, (byte)3);
    }
  for(int z = tp.bb.min.z; z<=tp.bb.max.z; z++)
    {
    write(tp.bb.min.x, z, (byte)3);
    write(tp.bb.max.x, z, (byte)3);
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
