package net.shadowmage.ancientwarfare.structure.town;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

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
private int cornersTotalWeight;
private List<TownWallEntry> cornerWalls = new ArrayList<TownWallEntry>();
private int gatesTotalWeight;
private List<TownWallEntry> gateWalls = new ArrayList<TownWallEntry>();
private HashMap<Integer, List<TownWallEntry>> wallPieces = new HashMap<Integer, List<TownWallEntry>>();//wall pieces by type
private HashMap<Integer, Integer> wallTotalWeights = new HashMap<Integer, Integer>();
private HashMap<Integer, int[]> wallPatterns = new HashMap<Integer, int[]>();

private int roadWidth = 3;
private Block roadFillBlock = Blocks.gravel;

/**
 * the nominal size of a town-block, in blocks
 */
private int townBlockSize;
private int townPlotSize;


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
public final int[] getWallPattern(int size){return wallPatterns.get(size);}
public final int getWallStyle(){return wallStyle;}
public final int getWallSize(){return wallSize;}
public final void setWallStyle(int wallStyle){this.wallStyle = wallStyle;}
public final void setWallSize(int wallSize){this.wallSize = wallSize;}
public final void addWallPattern(int size, int[] pattern){wallPatterns.put(size, pattern);}
public final void addCornerWall(String name, int weight)
  {
  cornerWalls.add(new TownWallEntry(name, weight));
  cornersTotalWeight+=weight;
  }
public final void addGateWall(String name, int weight)
  {
  gateWalls.add(new TownWallEntry(name, weight));
  gatesTotalWeight+=weight;
  }
public final void addWall(int type, String name, int weight)
  {
  if(!wallPieces.containsKey(type)){wallPieces.put(type, new ArrayList<TownWallEntry>());}
  wallPieces.get(type).add(new TownWallEntry(name, weight));  
  if(!wallTotalWeights.containsKey(type)){wallTotalWeights.put(type, 0);}
  int w = wallTotalWeights.get(type);
  w+=weight;
  wallTotalWeights.put(type, w);
  }
public final int getTownBlockSize(){return townBlockSize;}
public final int getTownPlotSize(){return townPlotSize;}
public void setTownBlockSize(int townBlockSize){this.townBlockSize = townBlockSize;}
public void setTownPlotSize(int townPlotSize){this.townPlotSize = townPlotSize;}
public final String getRandomWeightedWall(Random rng, int type){return getRandomWeightedWallPiece(rng, wallPieces.get(type), wallTotalWeights.get(type));}
public final String getRandomWeightedCorner(Random rng){return getRandomWeightedWallPiece(rng, cornerWalls, cornersTotalWeight);}
public final String getRandomWeightedGate(Random rng){return getRandomWeightedWallPiece(rng, gateWalls, gatesTotalWeight);}

private static String getRandomWeightedWallPiece(Random rng, List<TownWallEntry> list, int totalWeight)
  {
  int roll = rng.nextInt(totalWeight);
  for(TownWallEntry e : list)
    {
    roll-=e.weight;
    if(roll<0)
      {
      return e.templateName;
      }
    }
  return null;
  }

public static final class TownStructureEntry
{
String templateName;
int min;//min # to generate
int max;//max # to generate
public TownStructureEntry(String name, int min, int max)
  {
  this.templateName = name;
  this.min = min;
  this.max = max;
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
