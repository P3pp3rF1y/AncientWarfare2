package net.shadowmage.ancientwarfare.structure.town;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import net.minecraft.block.Block;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.util.StringTools;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplateManager;
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
    else if(line.startsWith("uniquestructures:"))
      {
      parseUniqueStructures(it, template);
      }
    else if(line.startsWith("mainstructures:"))
      {
      parseMainStructures(it, template);
      }
    else if(line.startsWith("housestructures:"))
      {
      parseHouseStructures(it, template);
      }
    else if(line.startsWith("cosmeticstructures:"))
      {
      parseCosmetics(it, template);
      } 
    else if(line.startsWith("exteriorstructures:"))
      {
      parseExteriorStructures(it, template);
      }
    }  
  if(template.isValid()){return template;}
  return null;
  }

private static void parseHeader(Iterator<String> it, TownTemplate template)
  {
  String line;
  String lower;
  while(it.hasNext() && (line=it.next())!=null)
    {
    lower = line.toLowerCase();    
    if(lower.startsWith(":endheader")){break;}
    else if(lower.startsWith("name")){template.setTownTypeName(StringTools.safeParseString("=", line));}
    else if(lower.startsWith("minsize")){template.setMinSize(StringTools.safeParseInt("=", line));}
    else if(lower.startsWith("maxsize")){template.setMaxSize(StringTools.safeParseInt("=", line));}
    else if(lower.startsWith("buildingexpansion")){template.setTownBuildingWidthExpansion(StringTools.safeParseInt("=", line));}
    else if(lower.startsWith("selectionweight")){template.setSelectionWeight(StringTools.safeParseInt("=", line));}
    else if(lower.startsWith("clustervalue")){template.setClusterValue(StringTools.safeParseInt("=", line));}
    else if(lower.startsWith("townblocksize")){template.setTownBlockSize(StringTools.safeParseInt("=", line));}
    else if(lower.startsWith("townplotsize")){template.setTownPlotSize(StringTools.safeParseInt("=", line));}
    else if(lower.startsWith("wallstyle")){template.setWallStyle(StringTools.safeParseInt("=", line));}
    else if(lower.startsWith("wallsize")){template.setWallSize(StringTools.safeParseInt("=", line));}
    else if(lower.startsWith("exteriorsize")){template.setExteriorSize(StringTools.safeParseInt("=", line));}
    else if(lower.startsWith("exteriorplotsize")){template.setExteriorPlotSize(StringTools.safeParseInt("=", line));}
    else if(lower.startsWith("interioremptyplotchance")){template.setInteriorEmtpyPlotChance(StringTools.safeParseInt("=", line));}
    else if(lower.startsWith("randomvillagersperchunk")){template.setRandomVillagersPerChunk(StringTools.safeParseFloat("=", line));}
    else if(lower.startsWith("roadblock")){template.setRoadFillBlock((Block)Block.blockRegistry.getObject(StringTools.safeParseString("=", line)));}
    else if(lower.startsWith("roadmeta")){template.setRoadFillMeta(StringTools.safeParseInt("=", line));}
    else if(lower.startsWith("biomewhitelist")){template.setBiomeWhiteList(StringTools.safeParseBoolean("=", line));}
    else if(lower.startsWith("biomelist")){template.setBiomeList(parseBiomeList(StringTools.safeParseString("=", line)));}
    else if(lower.startsWith("dimensionwhitelist")){template.setDimensionWhiteList(StringTools.safeParseBoolean("=", line));}
    else if(lower.startsWith("dimensionlist")){template.setDimensionList(parseDimensionList(StringTools.safeParseString("=", line)));}
    else if(lower.startsWith("lamp")){template.setLamp(new TownStructureEntry(StringTools.safeParseString("=", line), 1));}
    }  
  }

private static void parseUniqueStructures(Iterator<String> it, TownTemplate template)
  {
  String line;
  while(it.hasNext() && (line=it.next())!=null)
    {
    if(line.toLowerCase().startsWith(":enduniquestructures")){break;}
    else
      {
      TownStructureEntry e = parseStructureName(line);
      if(e!=null && StructureTemplateManager.instance().getTemplate(e.templateName)!=null)
        {
        template.getUniqueStructureEntries().add(e);
        AWLog.logDebug("added first pass main structure of: "+e.templateName);
        }
      else
        {
        AWLog.logError("There was an error while loading town structure entry: "+e);
        }
      }
    }  
  }

private static void parseMainStructures(Iterator<String> it, TownTemplate template)
  {
  String line;
  while(it.hasNext() && (line=it.next())!=null)
    {
    if(line.toLowerCase().startsWith(":endmainstructures")){break;}
    else
      {
      TownStructureEntry e = parseStructureName(line);
      if(e!=null && StructureTemplateManager.instance().getTemplate(e.templateName)!=null)
        {
        template.getMainStructureEntries().add(e);
        AWLog.logDebug("added second pass main structure of: "+e.templateName);
        }
      else
        {
        AWLog.logError("There was an error while loading town structure entry: "+e);
        }
      }
    }  
  }

private static void parseHouseStructures(Iterator<String> it, TownTemplate template)
  {
  String line;
  while(it.hasNext() && (line=it.next())!=null)
    {
    if(line.toLowerCase().startsWith(":endhousestructures")){break;}
    else
      {
      TownStructureEntry e = parseStructureWeight(line);
      if(e!=null && StructureTemplateManager.instance().getTemplate(e.templateName)!=null)
        {
        template.getHouseStructureEntries().add(e);
        AWLog.logDebug("added housing structure of: "+e.templateName);
        }
      else
        {
        AWLog.logError("There was an error while loading town structure entry: "+e);
        }
      }
    }  
  }

private static void parseCosmetics(Iterator<String> it, TownTemplate template)
  {
  String line;
  while(it.hasNext() && (line=it.next())!=null)
    {
    if(line.toLowerCase().startsWith(":endcosmeticstructures")){break;}
    else
      {      
      TownStructureEntry e = parseStructureWeight(line);
      if(e!=null && StructureTemplateManager.instance().getTemplate(e.templateName)!=null)
        {
        template.getCosmeticEntries().add(e);
        AWLog.logDebug("added cosmetic structure of: "+e.templateName);
        }
      else
        {
        AWLog.logError("There was an error while loading town structure entry: "+e);
        }
      }
    } 
  }

private static void parseExteriorStructures(Iterator<String> it, TownTemplate template)
  {
  String line;
  while(it.hasNext() && (line=it.next())!=null)
    {
    if(line.toLowerCase().startsWith(":endexteriorstructures")){break;}
    else
      {
      TownStructureEntry e = parseStructureWeight(line);
      if(e!=null && StructureTemplateManager.instance().getTemplate(e.templateName)!=null)
        {
        template.getExteriorStructureEntries().add(e);
        AWLog.logDebug("added exterior structure of: "+e.templateName);
        }
      else
        {
        AWLog.logError("There was an error while loading town structure entry: "+e);
        }
      }
    }  
  }

private static void parseWalls(Iterator<String> it, TownTemplate template)
  {
  String line;
  while(it.hasNext() && (line=it.next())!=null)
    {
    if(line.toLowerCase().startsWith(":endwalls")){break;}
    else
      {
      TownWallEntry e = parseWall(line);
      if(e!=null && StructureTemplateManager.instance().getTemplate(e.templateName)!=null)
        {
        template.addWall(e);
        }
      else
        {
        AWLog.logError("There was an error while loading town wall entry: "+e);
        }
      }
    } 
  }

private static void parseWallPatterns(Iterator<String> it, TownTemplate template)
  {
  String line;
  while(it.hasNext() && (line=it.next())!=null)
    {  
    if(line.toLowerCase().startsWith(":endwallpaterns")){break;}
    else
      {
      String[] bits = line.split(":", -1);
      int size = StringTools.safeParseInt(bits[0]);
      int[] pattern = parseWallPattern(bits[1]);
      template.addWallPattern(size, pattern);
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

private static TownStructureEntry parseStructureName(String line)
  {
  String[] bits = line.split(":", -1);
  return new TownStructureEntry(bits[0], 0);
  }

private static TownStructureEntry parseStructureWeight(String line)
  {
  String[] bits = line.split(":", -1);
  return new TownStructureEntry(bits[0], StringTools.safeParseInt(bits[1]));  
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
