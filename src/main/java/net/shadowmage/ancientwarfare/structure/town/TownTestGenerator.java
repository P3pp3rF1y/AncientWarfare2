package net.shadowmage.ancientwarfare.structure.town;

import java.util.ArrayList;
import java.util.List;

import net.shadowmage.ancientwarfare.core.config.AWLog;
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


public static byte[] testGrid;

public static void test()
  {
  Generator gen = new Generator(3*16, 3*16, 0);
  gen.generate();
  
  Integer.parseInt("foo");
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
public Road(int x, int z, int width, Direction orientation)
  {
  this.startX = x;
  this.startZ = z;
  this.width = width;
  this.orientation = orientation;
  }
}

private static class Generator
{
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
  }

public void generate()
  {
  genMainRoad();  
  while(!unwalkedRoads.isEmpty())
    {
    walkRoad(unwalkedRoads.remove(0));
    }
  }

private void genMainRoad()
  {   
  Direction road1Orientation = orientation;
  Direction road2Orientation = orientation.getOpposite();
  int centerX = (width/2);
  int centerZ = (length/2);
  int roadWidth = 6;
  int halfWidth = roadWidth/2;  
  int startX = centerX - (halfWidth*orientation.xDirection), startZ = centerZ - (halfWidth*orientation.zDirection);
  
  Road r1, r2;
  }

private void walkRoad(Road road)
  {
  
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
  AWLog.logDebug("writing x, z: "+x+":"+z);
  testGrid[getIndex(x, z)]=val;
  }

private int getIndex(int x, int z)
  {
  return z*width + x;
  }

private String writeTestGrid(int width, int length)
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
NORTH (0  , 1),
EAST  (1  , 0),
SOUTH (0  ,-1),
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
