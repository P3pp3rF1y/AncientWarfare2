package net.shadowmage.ancientwarfare.structure.town;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import net.minecraft.block.Block;
import net.shadowmage.ancientwarfare.core.util.StringTools;
import net.shadowmage.ancientwarfare.structure.town.TownTemplate.TownStructureEntry;
import net.shadowmage.ancientwarfare.structure.town.TownTemplate.TownWallEntry;

public class TownTemplateParser
{

public static TownTemplate parseTemplate(List<String> lines)
  {
  TownTemplate template = new TownTemplate();
  Iterator<String> it = lines.iterator();
  String line;

  while(it.hasNext() && (line=it.next())!=null)
    {
    line = line.toLowerCase();
    if(line.startsWith("header:"))
      {
      parseHeader(it, template);
      }
    else if(line.startsWith("walls:"))
      {
      parseWalls(it, template);
      }
    else if(line.startsWith("wallpatterns:"))
      {
      parseWallPatterns(it, template);
      }
    else if(line.startsWith("structures:"))
      {
      parseStructures(it, template);
      }
    else if(line.startsWith("cosmetics:"))
      {
      parseCosmetics(it, template);
      }    
    }  
  if(template.isValid()){return template;}
  return null;
  }

private static void parseHeader(Iterator<String> it, TownTemplate template)
  {
  String line;
  while(it.hasNext() && (line=it.next())!=null)
    {
    line = line.toLowerCase();
    if(line.startsWith(":endheader")){break;}
    else if(line.startsWith("name")){template.setTownTypeName(StringTools.safeParseString("=", line));}
    else if(line.startsWith("minsize")){template.setMinSize(StringTools.safeParseInt("=", line));}
    else if(line.startsWith("maxsize")){template.setMaxSize(StringTools.safeParseInt("=", line));}
    else if(line.startsWith("buildingexpansion")){template.setTownBuildingWidthExpansion(StringTools.safeParseInt("=", line));}
    else if(line.startsWith("selectionweight")){template.setSelectionWeight(StringTools.safeParseInt("=", line));}
    else if(line.startsWith("clustervalue")){template.setClusterValue(StringTools.safeParseInt("=", line));}
    else if(line.startsWith("townblocksize")){template.setTownBlockSize(StringTools.safeParseInt("=", line));}
    else if(line.startsWith("townplotsize")){template.setTownPlotSize(StringTools.safeParseInt("=", line));}
    else if(line.startsWith("wallstyle")){template.setWallStyle(StringTools.safeParseInt("=", line));}
    else if(line.startsWith("wallsize")){template.setWallSize(StringTools.safeParseInt("=", line));}
    else if(line.startsWith("roadblock")){template.setRoadFillBlock((Block)Block.blockRegistry.getObject(StringTools.safeParseString("=", line)));}
    else if(line.startsWith("roadmeta")){template.setRoadFillMeta(StringTools.safeParseInt("=", line));}
    else if(line.startsWith("biomewhitelist")){template.setBiomeWhiteList(StringTools.safeParseBoolean("=", line));}
    else if(line.startsWith("biomelist")){template.setBiomeList(parseBiomeList(StringTools.safeParseString("=", line)));}
    else if(line.startsWith("dimensionwhitelist")){template.setBiomeWhiteList(StringTools.safeParseBoolean("=", line));}
    else if(line.startsWith("dimensionlist")){template.setDimensionList(parseDimensionList(StringTools.safeParseString("=", line)));}
    else if(line.startsWith("townhall")){template.setTownHallEntry(new TownStructureEntry(StringTools.safeParseString("=", line), 1, 1));}
    else if(line.startsWith("lamp")){template.setLamp(new TownStructureEntry(StringTools.safeParseString("=", line), 1, 1));}
    }  
  }

private static void parseStructures(Iterator<String> it, TownTemplate template)
  {
  String line;
  while(it.hasNext() && (line=it.next())!=null)
    {
    line = line.toLowerCase();    
    if(line.startsWith(":endstructures")){break;}
    else
      {
      TownStructureEntry e = parseStructure(line);
      if(e!=null){template.getStructureEntries().add(e);}
      }
    }  
  }

private static void parseWalls(Iterator<String> it, TownTemplate template)
  {
  String line;
  while(it.hasNext() && (line=it.next())!=null)
    {
    line = line.toLowerCase();
    if(line.startsWith(":endwalls")){break;}
    else
      {
      TownWallEntry e = parseWall(line);
      if(e!=null)
        {
        template.addWall(e);
        }
      }
    } 
  }

private static void parseWallPatterns(Iterator<String> it, TownTemplate template)
  {
  String line;
  while(it.hasNext() && (line=it.next())!=null)
    {
    line = line.toLowerCase();    
    if(line.startsWith(":endwallpaterns")){break;}
    else
      {
      String[] bits = line.split(":", -1);
      int size = StringTools.safeParseInt(bits[0]);
      int[] pattern = parseWallPattern(bits[1]);
      template.addWallPattern(size, pattern);
      }
    } 
  }

private static void parseCosmetics(Iterator<String> it, TownTemplate template)
  {
  String line;
  while(it.hasNext() && (line=it.next())!=null)
    {
    line = line.toLowerCase();    
    if(line.startsWith(":endcosmetics")){break;}
    else
      {      
      TownStructureEntry e = parseCosmetic(line);
      if(e!=null){template.getCosmeticEntries().add(e);}
      }
    } 
  }

private static List<String> parseBiomeList(String line)
  {
  String[] bits = line.split(",", -1);
  if(bits==null || bits.length<=0){return Collections.emptyList();}
  List<String> names = new ArrayList<String>();
  for(int i = 0; i< bits.length; i++){names.add(bits[i].toLowerCase());}
  return names;
  }

private static List<Integer> parseDimensionList(String line)
  {  
  String[] bits = line.split(",", -1);
  if(bits==null || bits.length<=0){return Collections.emptyList();}
  List<Integer> dims = new ArrayList<Integer>();
  for(int i = 0; i< bits.length; i++){dims.add(StringTools.safeParseInt(bits[i].toLowerCase()));}
  return dims;
  }

private static TownStructureEntry parseStructure(String line)
  {
  String[] bits = line.split(":", -1);
  return new TownStructureEntry(bits[0], StringTools.safeParseInt(bits[1]), StringTools.safeParseInt(bits[2]));
  }

private static TownStructureEntry parseCosmetic(String line)
  {
  String[] bits = line.split(":", -1);
  return new TownStructureEntry(bits[0], StringTools.safeParseInt(bits[1]), StringTools.safeParseInt(bits[1]));  
  }

private static TownWallEntry parseWall(String line)
  {
  String[] bits = line.split(":", -1);
  return new TownWallEntry(bits[0], bits[1], StringTools.safeParseInt(bits[2]), StringTools.safeParseInt(bits[3]));
  }

private static int[] parseWallPattern(String line)
  {
  String[] bits = line.split("-", -1);
  int[] pattern = new int[bits.length];
  for(int i = 0; i< bits.length; i++){pattern[i]=StringTools.safeParseInt(bits[i]);}
  return pattern;
  }

}
