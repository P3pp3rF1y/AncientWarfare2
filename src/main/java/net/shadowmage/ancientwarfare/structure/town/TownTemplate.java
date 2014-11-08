package net.shadowmage.ancientwarfare.structure.town;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

public class TownTemplate
{

private String townTypeName;

private boolean biomeWhiteList;
private List<String> biomeList = new ArrayList<String>();

private boolean dimensionWhiteList;
private List<Integer> dimensionList = new ArrayList<Integer>();

private int minSize = 3;
private int maxSize = 9;
int maxValue = 500;

private int wallStyle;//0==no wall, 1==random walls, 2==by pattern
private int wallSize;
private List<TownWallEntry> cornerWalls = new ArrayList<TownWallEntry>();
private List<TownWallEntry> gateWalls = new ArrayList<TownWallEntry>();
private HashMap<Integer, List<TownWallEntry>> wallPieces = new HashMap<Integer, List<TownWallEntry>>();//wall pieces by type
private HashMap<Integer, int[]> wallPatterns = new HashMap<Integer, int[]>();

private int roadWidth = 3;
private Block roadFillBlock = Blocks.gravel;

/**
 * the nominal size of a town-block, in blocks
 */
private int townBlockSize;

/**
 * how many structures should attempt to be generated on each side of a town block
 */
private int structuresPerBlock;

/**
 * A specific template to be generated at the center of town, as the town-hall.  All roads and other buildings will be constructed -around- this one
 */
private TownStructureEntry townHallEntry;
private List<TownStructureEntry> structureEntries = new ArrayList<TownStructureEntry>();

public TownTemplate(String townTypeName)
  {
  this.townTypeName = townTypeName;
  }

public final String getTownTypeName(){return townTypeName;}
public final boolean isBiomeWhiteList(){return biomeWhiteList;}
public final void setBiomeWhiteList(boolean biomeWhiteList){this.biomeWhiteList = biomeWhiteList;}
public final List<String> getBiomeList(){return biomeList;}
public final boolean isDimensionWhiteList(){return dimensionWhiteList;}
public final void setDimensionWhiteList(boolean dimensionWhiteList){this.dimensionWhiteList = dimensionWhiteList;}
public final List<Integer> getDimensionList(){return dimensionList;}
public final List<TownStructureEntry> getStructureEntries(){return structureEntries;}
public final int getMinSize(){return minSize;}
public final void setMinSize(int minSize){this.minSize = minSize;}
public final int getMaxSize(){return maxSize;}
public final void setMaxSize(int maxSize){this.maxSize = maxSize;}
public final int getMaxValue(){return maxValue;}
public final void setMaxValue(int maxValue){this.maxValue = maxValue;}
public final TownStructureEntry getTownHallEntry(){return townHallEntry;}
public final void setTownHallEntry(TownStructureEntry townHallEntry){this.townHallEntry = townHallEntry;}
public final int getRoadWidth(){return roadWidth;}
public final Block getRoadFillBlock(){return roadFillBlock;}
public final void setRoadWidth(int roadWidth){this.roadWidth = roadWidth;}
public final void setRoadFillBlock(Block roadFillBlock){this.roadFillBlock = roadFillBlock==null? this.roadFillBlock : roadFillBlock;}
public final List<TownWallEntry> getCornerWalls(){return cornerWalls;}
public final List<TownWallEntry> getGateWalls(){return gateWalls;}
public final List<TownWallEntry> getWalls(int type){return wallPieces.get(type);}
public final int[] getWallPattern(int size){return wallPatterns.get(size);}
public final int getWallStyle(){return wallStyle;}
public final int getWallSize(){return wallSize;}
public final void setWallStyle(int wallStyle){this.wallStyle = wallStyle;}
public final void setWallSize(int wallSize){this.wallSize = wallSize;}
public final void addWallPattern(int size, int[] pattern){wallPatterns.put(size, pattern);}
public final void addCornerWall(String name, int weight){cornerWalls.add(new TownWallEntry(name, weight));}
public final void addGateWall(String name, int weight){gateWalls.add(new TownWallEntry(name, weight));}
public final void addWall(int type, String name, int weight)
  {
  if(!wallPieces.containsKey(type)){wallPieces.put(type, new ArrayList<TownWallEntry>());}
  wallPieces.get(type).add(new TownWallEntry(name, weight));
  }
public final int getTownBlockSize(){return townBlockSize;}
public final int getStructuresPerBlock(){return structuresPerBlock;}

public static final class TownStructureEntry
{
String templateName;
int min;//min # to generate
int max;//max # to generate
int value;// generation value
int weight;// selection weight
boolean cosmetic;//stuff like torches, what would this flag -actually- be used for?
public TownStructureEntry(String name, int min, int max, int value, int weight)
  {
  this.templateName = name;
  this.min = min;
  this.max = max;
  this.value = value;
  this.weight = weight;
  }
}

public static final class TownWallEntry
{
String templateName;
int weight;
public TownWallEntry(String name, int weight)
  {
  this.templateName=name;
  this.weight=weight;  
  }
}

}
