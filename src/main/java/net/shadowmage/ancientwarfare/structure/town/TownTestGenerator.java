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
 * Roads have no initial width
 *  width is calculated from the amount of connecting roads
 *    more connecting roads = larger width
 * 
 * Road generation:
 * Generate static roads (cross roads)
 *    While generating these, generate seeds for main cross-roads
 *    ensure that these main cross roads are not too frequent
 *    generate a 'road start node' for each of these side roads.  It is these start nodes that will be used for continued generation
 *
 * 
 * 
 * 
 */

private static class Road3
{
Direction genDirection;
int x, z;
List<RoadNode> nodes = new ArrayList<RoadNode>();
List<RoadNode> openNodes = new ArrayList<RoadNode>();
public Road3(int x, int z, Direction genDirection)
  {
  this.x = x;
  this.z = z;
  this.genDirection = genDirection;
  RoadNode n = new RoadNode();
  this.nodes.add(n);
  openNodes.add(n);
  }
}

private static class RoadNode
{
boolean[] connections = new boolean[4];//connection bits for each direction, whether the road already continues in that direction
boolean open=true;//should this node be considered for further expansion (does not prevent other roads from connecting to this node)
}


private static class Road2
{
Direction orientation;
int x, z;
int negativeXWidth;
int positiveXWidth;
int negativeZWidth;
int positiveZWidth;
int fullWidth;
int length=1;
StructureBB bb;
StructureBB growthBB;
public Road2(int x, int z, int width, Direction orientation)
  {  
  this.x = x;
  this.z = z;
  this.fullWidth = width;
  this.orientation = orientation;
  bb = new StructureBB(new BlockPosition(), new BlockPosition());
  
  if(orientation==Direction.NORTH || orientation==Direction.SOUTH)
    {
    negativeXWidth = (width/2);
    positiveXWidth = (width/2)-1;    
    }
  else if(orientation==Direction.EAST || orientation==Direction.WEST)
    {
    negativeZWidth = (width/2);
    positiveZWidth = (width/2)-1;    
    }
  bb.min.x -= negativeXWidth;
  bb.max.x += positiveXWidth;
  bb.min.z -= negativeZWidth;
  bb.max.z += positiveZWidth;
  growthBB = new StructureBB(bb.min.copy(),  bb.max.copy());
  growthBB.min.x+=orientation.xDirection;
  growthBB.max.x+=orientation.xDirection;
  growthBB.min.z+=orientation.zDirection;
  growthBB.max.z+=orientation.zDirection;
  }

public StructureBB getBounds()
  {
  return bb;
  }

public void grow()
  {
  length++;
  switch(orientation)
  {
  case NORTH:
    {
    growthBB.min.z--;
    growthBB.max.z--;
    bb.min.z--;
    break;
    }
  case EAST:
    {
    growthBB.min.x++;
    growthBB.max.x++;
    bb.max.x++;
    break;
    }
  case SOUTH:
    {
    growthBB.min.z++;
    growthBB.max.z++;
    bb.min.z++;
    break;
    }
  case WEST:
    {
    growthBB.min.x--;
    growthBB.max.x--;
    bb.max.x--;
    break;
    }
  }
  }

/**
 * Returns a bb denoting the new blocks that would be added to this road if it were to grow in length by 1 block
 * @return
 */
public StructureBB getGrowthBounds()
  {
  return growthBB;
  }

public BlockPosition getLeftStart()
  {
  BlockPosition pos=null;
  int x = getGrowthBounds().min.x, z = getGrowthBounds().min.z;
  if(orientation==Direction.NORTH){x--;}
  else if(orientation==Direction.SOUTH){x+=fullWidth;}
  else if(orientation==Direction.EAST){z--;}
  else if(orientation==Direction.WEST){z+=fullWidth;}  
  pos = new BlockPosition(x,0,z);  
  return pos;
  }

public BlockPosition getRightStart()
  {
  BlockPosition pos=null;
  int x = getGrowthBounds().min.x, z = getGrowthBounds().min.z;
  if(orientation==Direction.NORTH){x--;}
  else if(orientation==Direction.SOUTH){x+=fullWidth;}
  else if(orientation==Direction.EAST){z--;}
  else if(orientation==Direction.WEST){z+=fullWidth;}  
  pos = new BlockPosition(x,0,z);  
  return pos;
  }

}

private static class Generator
{

Random rng;
byte[] testGrid;
private int blockSize;
private int width, length;
private Direction orientation;
private List<Road2> unwalkedRoads = new ArrayList<Road2>();
private List<Road2> generatedRoads = new ArrayList<Road2>();

public Generator(int width, int length, int orientation)
  {
  this.width = width;
  this.length = length;
  this.orientation = Direction.fromFacing(orientation);
  this.testGrid = new byte[width*length];
  rng = new Random(0);
  }

public void generate()
  {
  genMainRoad();  
  while(!unwalkedRoads.isEmpty())
    {
    randomWalkRoad(unwalkedRoads.remove(0));
    }
  
  for(Road2 r : generatedRoads)
    {
    write(r.getBounds(), (byte)1);
    }
  genRandomStructures();
  int centerX = (width/2);
  int centerZ = (length/2);
  write(centerX, centerZ, (byte)9);
  String line = writeTestGrid();
  AWLog.logDebug("grid: \n"+line);
  }

private void genRandomStructures()
  {
  StructureBB bb = new StructureBB(new BlockPosition(), new BlockPosition());
  for(int i = 0; i < 20; i++)
    {
    int sw = 5 + rng.nextInt(5);//5-9 block wide
    int sl = 5 + rng.nextInt(5);//5-9 block long    
    float xr = rng.nextFloat() * rng.nextFloat();
    float zr = rng.nextFloat() * rng.nextFloat();
    float hw = width/2;
    float hl = length/2;
    int x = (int)hw + (int)(hw*xr);
    int z = (int)hl + (int)(hl*zr);
    bb.min.x = x;
    bb.min.z = z;
    bb.max.x = x+sw;
    bb.max.z = z+sl;
    if(bb.min.x>=0 && bb.min.z>=0 && bb.max.x<width && bb.max.z<length && !doesBBIntersect(bb))
      {
      write(bb, (byte)2);
      }
    }
  }

private void genMainRoad()
  {   
  int centerX = (width/2);
  int centerZ = (length/2);
  
  Direction o = orientation;
  Road2 r = genRoad(orientation, centerX, centerZ, 6);
  walkRoad(r); 
  
  o = orientation.getOpposite();
  r = genRoad(o, centerX+o.xDirection, centerZ+o.zDirection, 6);
  walkRoad(r);
  }

private Road2 genRoad(Direction orientation, int x1, int z1, int width)
  {
  return new Road2(x1, z1, width, orientation);
  }

private void walkRoad(Road2 road)
  {
  while(canRoadGrow(road))
    {
    int roll = rng.nextInt(10);
    if(roll==0)//left
      {
      
      }
    if(roll==1)//right
      {
      
      }
    road.grow();    
    }
  generatedRoads.add(road);
  }

private void randomWalkRoad(Road2 road)
  {
  while(canRoadGrow(road))
    {
    int roll = rng.nextInt(10);
    if(roll<2){break;}//0/1
    if(roll==2)//2/3
      {
      StructureBB bb = road.getGrowthBounds();
      Direction o = road.orientation.getLeft();
      }
    else if(roll==3)
      {
      StructureBB bb = road.getGrowthBounds();
      Direction o = road.orientation.getRight();      
      }
    road.grow();    
    }
  generatedRoads.add(road);
  }

private boolean canRoadGrow(Road2 road)
  {
  StructureBB bb = road.getGrowthBounds();
  if(bb.min.x<0 || bb.max.x>=width || bb.min.z<0 || bb.max.z>=length){return false;}
  return !doesBBIntersect(bb);
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
