package net.shadowmage.ancientwarfare.structure.town;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

public final class TownTemplate
{
private String townTypeName;//

private boolean biomeWhiteList;//
private List<String> biomeList = new ArrayList<String>();//

private boolean dimensionWhiteList;//
private List<Integer> dimensionList = new ArrayList<Integer>();//

private int minSize = 3;//size in chunks//
private int maxSize = 9;//size in chunks//

private int selectionWeight;

private int clusterValue;//value inserted into template gen map to discourage nearby random structure spawns

/**
 * the nominal size of a town-block, in blocks
 */
private int townBlockSize;//
private int townPlotSize;//
private int townBuildingWidthExpansion;//used to expand buildings by a set size, for use with structure templates that have no surrounding space in them, to ensure room between structures

private Block roadFillBlock = Blocks.gravel;//
private int roadFillMeta = 0;

private int wallStyle;//0==no wall, 1==corner only, 2==random walls, 3==by pattern//
private int wallSize;//size in blocks//

private int exteriorSize;//area outside of the walls, in blocks
private int exteriorPlotSize;//size of each plot in the area outside of the walls

private int interiorEmtpyPlotChance;

private HashMap<Integer, TownWallEntry> wallsByID = new HashMap<Integer, TownWallEntry>();
private HashMap<Integer, int[]> wallPatterns = new HashMap<Integer, int[]>();

private int cornersTotalWeight;
private List<TownWallEntry> cornerWalls = new ArrayList<TownWallEntry>();

private int gatesCenterTotalWeight;
private List<TownWallEntry> gateCenterWalls = new ArrayList<TownWallEntry>();

private int gatesLeftTotalWeight;
private List<TownWallEntry> gateLeftWalls = new ArrayList<TownWallEntry>();

private int gatesRightTotalWeight;
private List<TownWallEntry> gateRightWalls = new ArrayList<TownWallEntry>();

private int wallTotalWeights;
private List<TownWallEntry> walls = new ArrayList<TownWallEntry>();

private TownStructureEntry lamp;//
private List<TownStructureEntry> uniqueStructureEntries = new ArrayList<TownStructureEntry>();
private List<TownStructureEntry> mainStructureEntries = new ArrayList<TownStructureEntry>();
private List<TownStructureEntry> houseStructureEntries = new ArrayList<TownStructureEntry>();
private List<TownStructureEntry> cosmeticStructureEntries = new ArrayList<TownStructureEntry>();
private List<TownStructureEntry> exteriorStructureEntries = new ArrayList<TownStructureEntry>();

public TownTemplate(){}

public void setTownTypeName(String townTypeName){this.townTypeName = townTypeName;}
public final String getTownTypeName(){return townTypeName;}
public final boolean isBiomeWhiteList(){return biomeWhiteList;}
public final void setBiomeWhiteList(boolean biomeWhiteList){this.biomeWhiteList = biomeWhiteList;}
public final List<String> getBiomeList(){return biomeList;}
public final void setBiomeList(List<String> biomes){this.biomeList=biomes;}
public final boolean isDimensionWhiteList(){return dimensionWhiteList;}
public final void setDimensionWhiteList(boolean dimensionWhiteList){this.dimensionWhiteList = dimensionWhiteList;}
public final List<Integer> getDimensionList(){return dimensionList;}
public final void setDimensionList(List<Integer> dimensions){this.dimensionList=dimensions;}
public final List<TownStructureEntry> getUniqueStructureEntries(){return uniqueStructureEntries;}
public final List<TownStructureEntry> getMainStructureEntries(){return mainStructureEntries;}
public final List<TownStructureEntry> getHouseStructureEntries(){return houseStructureEntries;}
public final List<TownStructureEntry> getExteriorStructureEntries(){return exteriorStructureEntries;}
public final List<TownStructureEntry> getCosmeticEntries(){return cosmeticStructureEntries;}
public final int getMinSize(){return minSize;}
public final void setMinSize(int minSize){this.minSize = minSize;}
public final int getMaxSize(){return maxSize;}
public final void setMaxSize(int maxSize){this.maxSize = maxSize;}
public final void setTownBuildingWidthExpansion(int townBuildingWidthExpansion){this.townBuildingWidthExpansion = townBuildingWidthExpansion;}
public final int getTownBuildingWidthExpansion(){return townBuildingWidthExpansion;}
public final int getSelectionWeight(){return selectionWeight;}
public final void setSelectionWeight(int selectionWeight){this.selectionWeight = selectionWeight;}
public final int getClusterValue(){return clusterValue;}
public final void setClusterValue(int clusterValue){this.clusterValue = clusterValue;}
public final TownStructureEntry getLamp(){return lamp;}
public final void setLamp(TownStructureEntry lamp){this.lamp = lamp;}
public final Block getRoadFillBlock(){return roadFillBlock;}
public final void setRoadFillBlock(Block roadFillBlock){this.roadFillBlock = roadFillBlock==null? this.roadFillBlock : roadFillBlock;}
public final void setRoadFillMeta(int roadFillMeta){this.roadFillMeta = roadFillMeta;}
public final int getRoadFillMeta(){return roadFillMeta;}
public final int getExteriorSize(){return exteriorSize;}
public final int getExteriorPlotSize(){return exteriorPlotSize;}
public final void setExteriorSize(int exteriorSize){this.exteriorSize = exteriorSize;}
public final void setExteriorPlotSize(int exteriorPlotSize){this.exteriorPlotSize = exteriorPlotSize;}
public int getInteriorEmtpyPlotChance(){return interiorEmtpyPlotChance;}
public void setInteriorEmtpyPlotChance(int interiorEmtpyPlotChance){this.interiorEmtpyPlotChance = interiorEmtpyPlotChance;}
public final int[] getWallPattern(int size){return wallPatterns.get(size);}
public final int getWallStyle(){return wallStyle;}
public final int getWallSize(){return wallSize;}
public final void setWallStyle(int wallStyle){this.wallStyle = wallStyle;}
public final void setWallSize(int wallSize){this.wallSize = wallSize;}
public final void addWallPattern(int size, int[] pattern){wallPatterns.put(size, pattern);}

public final void addWall(TownWallEntry e)
  {  
  wallsByID.put(e.id, e);
  if(e.typeName.toLowerCase().equals("wall"))
    {
    walls.add(e);
    wallTotalWeights+=e.weight;
    }
  else if(e.typeName.toLowerCase().equals("corner"))
    {
    cornerWalls.add(e);
    cornersTotalWeight+=e.weight;
    }
  else if(e.typeName.toLowerCase().equals("gate"))
    {
    gateCenterWalls.add(e);
    gatesCenterTotalWeight+=e.weight;
    }
  else if(e.typeName.toLowerCase().equals("lgate"))
    {
    gateLeftWalls.add(e);
    gatesLeftTotalWeight+=e.weight;
    }
  else if(e.typeName.toLowerCase().equals("rgate"))
    {
    gateRightWalls.add(e);
    gatesRightTotalWeight+=e.weight;    
    }  
  }

public final int getTownBlockSize(){return townBlockSize;}
public final int getTownPlotSize(){return townPlotSize;}
public final void setTownBlockSize(int townBlockSize){this.townBlockSize = townBlockSize;}
public final void setTownPlotSize(int townPlotSize){this.townPlotSize = townPlotSize;}
public final TownWallEntry getWall(int id){return wallsByID.get(id);}
public final String getRandomWeightedWall(Random rng){return getRandomWeightedWallPiece(rng, walls, wallTotalWeights);}
public final String getRandomWeightedCorner(Random rng){return getRandomWeightedWallPiece(rng, cornerWalls, cornersTotalWeight);}
public final String getRandomWeightedGate(Random rng){return getRandomWeightedWallPiece(rng, gateCenterWalls, gatesCenterTotalWeight);}
public final String getRandomWeightedGateLeft(Random rng){return getRandomWeightedWallPiece(rng, gateLeftWalls, gatesLeftTotalWeight);}
public final String getRandomWeightedGateRight(Random rng){return getRandomWeightedWallPiece(rng, gateRightWalls, gatesRightTotalWeight);}

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

public final boolean isValid()
  {
  return townTypeName!=null && !townTypeName.isEmpty();
  }

public static final class TownStructureEntry
{
String templateName;
int min;//min # to generate
public TownStructureEntry(String name, int min)
  {
  this.templateName = name;
  this.min = min;
  }
@Override
public String toString()
  {
  return "[Town Structure: "+templateName+" :: "+min+"]";
  }
}

public static final class TownWallEntry
{
String templateName;
String typeName;
int weight;
int id;
public TownWallEntry(String name, String type, int id, int weight)
  {
  this.templateName=name;
  this.typeName = type;
  this.id = id;
  this.weight=weight;  
  }
@Override
public String toString()
  {
  return "[Town Wall: "+typeName+" :: "+id+" :: "+templateName+"]";
  }
}


}
