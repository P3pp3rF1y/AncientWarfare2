package net.shadowmage.ancientwarfare.structure.town;

import java.util.ArrayList;
import java.util.List;

public class TownTemplate
{

private String townTypeName;

private boolean biomeWhiteList;
private List<String> biomeList;

private boolean dimensionWhiteList;
private List<Integer> dimensionList;

private int minSize = 50;
private int maxSize = 256;
int maxValue = 500;

private int wallStyle;//0==no wall
private int wallSize;
private int wallHeight;

/**
 * A specific template to be generated at the center of town, as the town-hall.  All roads and other buildings will be constructed -around- this one
 */
private TownStructureEntry townHallEntry;
private List<TownStructureEntry> structureEntries;

public TownTemplate(String townTypeName)
  {
  this.townTypeName = townTypeName;
  biomeList = new ArrayList<String>();
  dimensionList = new ArrayList<Integer>();
  structureEntries = new ArrayList<TownStructureEntry>();
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
public final int getWallStyle(){return wallStyle;}
public final int getWallSize(){return wallSize;}
public final int getWallHeight(){return wallHeight;}
public final void setWallStyle(int wallStyle){this.wallStyle = wallStyle;}
public final void setWallSize(int wallSize){this.wallSize = wallSize;}
public final void setWallHeight(int wallHeight){this.wallHeight = wallHeight;}

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

}
