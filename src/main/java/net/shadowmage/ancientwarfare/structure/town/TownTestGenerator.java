package net.shadowmage.ancientwarfare.structure.town;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
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
//  Integer.parseInt("foo");
  }

/**Gen algorithm:
 * 
 * Generation of structures goes along roads.
 * Initial road is generated across town.  Has two start nodes, one for each cardinal direction that the road is stretching.
 * Roads are added to a 'roads to walk' list once generated.
 * Roads have a start node that determines the starting location of the road
 * Roads have an orientation that denotes what direction the road runs.
 * Roads have a set 'road width', that decreases as the road goes further from the main-street
 * 
 * while(!roadsToWalk.isEmpty)
 *  pull first road from list.  Walk along the road attempting to generate structures on the left and right
 *  keep track of how many blocks walked and what type of road
 *  after every X blocks walked, plot the start to a side road rather than try for a building.
 *    Side roads are smaller in width than main roads (6->4->2 min)
 *    add newly generated side roads to roadsToWalk list.
 *    after a building is generated on a road, fill in the road up to that position (front door node for the structure)
 * 
 */

private static class Road
{
Direction orientation;
int startX, startZ;
int width;//should generally be 4 or 6, might be 2 for side-alleys
int leftWidth;
int rightWidth;
int genType;
int length;
int blocksTilSideRoadCheckLeft = 10;
int blocksTilSideRoadCheckRight = 16;
int lastBuildingEdgePos = -1;
BlockPosition min, max;
BlockPosition genPos;

public Road(int x, int z, int width, int genType, Direction orientation)
  {
  this.startX = x;
  this.startZ = z;
  this.width = width;
  this.genType = genType;
  this.orientation = orientation;  
  this.leftWidth = (width/2);
  this.rightWidth = (width/2)-1;
  this.length=1;
  this.blocksTilSideRoadCheckLeft = width*2;
  this.blocksTilSideRoadCheckRight = width*2;
  
  min = new BlockPosition(x, 0, z);
  max = new BlockPosition(x, 0, z);
  
  int olx = getLeftX();
  int olz = getLeftZ();
  int orx = getRightX();
  int orz = getRightZ();
  if(olx>0){max.x+=rightWidth;}
  else if(olx<0){min.x-=leftWidth;}
  if(orx>0){max.x+=rightWidth;}
  else if(orx<0){min.x-=leftWidth;}
  if(olz>0){max.z+=rightWidth;}
  else if(olz<0){min.z-=leftWidth;}
  if(orz>0){max.z+=rightWidth;}
  else if(orz<0){min.z-=leftWidth;}
  genPos = new BlockPosition(startX, 0, startZ);
  AWLog.logDebug("created road start: "+min+" :: "+max+" width: "+width);
  }

public int getMoveX(){return orientation.xDirection;}
public int getMoveZ(){return orientation.zDirection;}
public int getLeftX(){return orientation.getLeft().xDirection;}
public int getRightX(){return orientation.getRight().xDirection;}
public int getLeftZ(){return orientation.getLeft().zDirection;}
public int getRightZ(){return orientation.getRight().zDirection;}

public void grow()
  {
  int x = getMoveX();
  int z = getMoveZ();
  genPos.x+=x;
  genPos.z+=z;
  if(x<0){min.x--;}
  else if(x>0){max.x++;}
  if(z<0){min.z--;}
  else if(z>0){max.z++;}
  length++;
  }

public boolean canGrow(int width, int length)
  {
  int x = getMoveX();
  if(x < 0 && min.x <=0){return false;}//cannot grow west
  else if(x > 0 && max.x >= width-1){return false;}//cannot grow east
  int z = getMoveZ();
  if(z < 0 && min.z <=0){return false;}//cannot grow north
  else if(z > 0 && max.z >= length-1){return false;}//cannot grow south
  return true;
  }

public void gen(int width, int length, List<Road> roads)
  {
  if(this.width>2)//try and create sub-road
    {
    roadGenUpdate(width, length, roads);
    }
  }

private void roadGenUpdate(int width, int length, List<Road> roads)
  {
  if(blocksTilSideRoadCheckLeft>0){blocksTilSideRoadCheckLeft--;}
  if(blocksTilSideRoadCheckRight>0){blocksTilSideRoadCheckRight--;}
  if(blocksTilSideRoadCheckLeft<=0)
    {
    Road r = tryRoad(width, length, orientation.getLeft());
    if(r!=null)
      {
      roads.add(r);
      blocksTilSideRoadCheckLeft=this.width*2;
      }
    }
//  if(blocksTilSideRoadCheckRight==0)
//    {
//    Road r = tryRoad(width, length, orientation.getRight());
//    if(r!=null)
//      {
//      roads.add(r);
//      blocksTilSideRoadCheckRight=this.width*2;
//      }
//    }
  }

private Road tryRoad(int width, int length, Direction o)
  {
  int roadWidth = this.width-2;
  if(roadWidth<1){return null;}
  int x = genPos.x;
  if(o.xDirection<0){x -= (leftWidth+1);}
  if(o.xDirection>0){x += (rightWidth+1);}
  int z = genPos.z;
  if(o.zDirection<0){z -= (leftWidth+1);}
  if(o.zDirection>0){z += (rightWidth+1);}
  if(x<0 || x>=width || z<0 || z>=length){return null;}
  Road r = new Road(x, z, roadWidth, genType+1, o);
  if(r.min.x<0 || r.min.z<0 || r.max.x>=width || r.max.z >=length){return null;}
  return r;
  }

}

private static class Generator
{
Random rng;
byte[] testGrid;
private int width, length;
private Direction orientation;
private List<Road> unwalkedRoads = new ArrayList<Road>();

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
    walkRoad(unwalkedRoads.remove(0));
    }
  int centerX = (width/2);
  int centerZ = (length/2);
  write(centerX, centerZ, (byte)9);
  String line = writeTestGrid();
  AWLog.logDebug("grid: \n"+line);
  }

private void genMainRoad()
  {   
  int centerX = (width/2);
  int centerZ = (length/2);
  Road r = genRoad(orientation, centerX, centerZ, 6, 0);
  r.blocksTilSideRoadCheckLeft = 0;
  unwalkedRoads.add(r); 
  Direction o = orientation.getOpposite();
  unwalkedRoads.add(r = genRoad(o, centerX+o.xDirection, centerZ+o.zDirection, 6, 0));
  r.blocksTilSideRoadCheckLeft = 0;
  }

private Road genRoad(Direction orientation, int x1, int z1, int width, int type)
  {
  return new Road(x1, z1, width, type, orientation);
  }

private void walkRoad(Road road)
  {
  int leftRoadCheck;
  int rightRoadCheck;
  while(road.canGrow(width, length))
    {
    road.grow();
    road.gen(width, length, unwalkedRoads);
    }
  for(int x = road.min.x; x<= road.max.x; x++)
    {
    for(int z = road.min.z; z<= road.max.z; z++)
      {
      write(x, z, (byte)1);
      }
    }
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
