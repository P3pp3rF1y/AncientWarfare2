package net.shadowmage.ancientwarfare.structure.town;

import java.util.List;

public class TownTemplate
{

private String townTypeName;
private String duplicateSearchName;

private boolean biomeWhiteList;
private List<String> biomeList;

private boolean dimensionWhiteList;
private List<Integer> dimensionList;

private int wallStyle;
private int minSize;
private int maxSize;
int maxValue;

private List<TownStructureEntry> structureEntries;

public TownTemplate(String townTypeName, String duplicateSearchName)
  {
  this.townTypeName = townTypeName;
  this.duplicateSearchName = duplicateSearchName;
  }

public final String getTownTypeName(){return townTypeName;}

public final void setTownTypeName(String townTypeName){this.townTypeName = townTypeName;}

public final String getDuplicateSearchName(){return duplicateSearchName;}

public final void setDuplicateSearchName(String duplicateSearchName){this.duplicateSearchName = duplicateSearchName;}

public final boolean isBiomeWhiteList(){return biomeWhiteList;}

public final void setBiomeWhiteList(boolean biomeWhiteList){this.biomeWhiteList = biomeWhiteList;}

public final List<String> getBiomeList(){return biomeList;}

public final void setBiomeList(List<String> biomeList){this.biomeList = biomeList;}

public final boolean isDimensionWhiteList(){return dimensionWhiteList;}

public final void setDimensionWhiteList(boolean dimensionWhiteList){this.dimensionWhiteList = dimensionWhiteList;}

public final List<Integer> getDimensionList(){return dimensionList;}

public final void setDimensionList(List<Integer> dimensionList){this.dimensionList = dimensionList;}

public static final class TownStructureEntry
{
int min;//min # to generate
int max;//max # to generate
int value;// generation value
int weight;// selection weight
boolean cosmetic;//stuff like torches
}


}
