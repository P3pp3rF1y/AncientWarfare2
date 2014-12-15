package net.shadowmage.ancientwarfare.structure.town;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.structure.config.AWStructureStatics;

public class TownTemplateManager
{

private TownTemplateManager(){}
private static TownTemplateManager INSTANCE = new TownTemplateManager();
public static TownTemplateManager instance(){return INSTANCE;}

private HashMap<String, TownTemplate> templates = new HashMap<String, TownTemplate>();

private List<TownTemplate> searchCache = new ArrayList<TownTemplate>();

public void loadTemplate(TownTemplate template)
  {
  templates.put(template.getTownTypeName(), template);
  }

public TownTemplate getTemplate(String name)
  {
  return templates.get(name);
  }

public TownTemplate selectTemplateForGeneration(World world, int x, int z, TownBoundingArea area)
  {  
  TownTemplate selection = null;
  int width = area.getChunkWidth();
  int length = area.getChunkLength();
  
  int min = Math.min(width, length);
  int templateMinimumSize;
  
  AWLog.logDebug("min chunk size for template selection: "+min);
  
  String biomeName = AWStructureStatics.getBiomeName(world.getBiomeGenForCoords(x, z));   
  int totalWeight = 0; 
  for(TownTemplate t : templates.values())
    {
    AWLog.logDebug("template min size: "+t.getMinSize());
    templateMinimumSize = t.getMinSize();
    if(min >= templateMinimumSize && isBiomeValid(biomeName, t))
      {      
      AWLog.logDebug("Validating town template: "+t.getTownTypeName() + "  "+min + " "+templateMinimumSize);
      searchCache.add(t);
      totalWeight += t.getSelectionWeight();
      }
    }    
  if(!searchCache.isEmpty() && totalWeight>0)
    {
    totalWeight = world.rand.nextInt(totalWeight);
    for(TownTemplate t : searchCache)
      {
      totalWeight -= t.getSelectionWeight();
      if(totalWeight < 0)
        {
        selection = t;
        break;
        }
      }
    }
  searchCache.clear();
  return selection;
  }

private boolean isBiomeValid(String biome, TownTemplate t)
  { 
  boolean contains = t.getBiomeList().contains(biome);
  boolean wl = t.isBiomeWhiteList();
  AWLog.logDebug("biome check: "+contains+ " :: "+wl);
  return true;
//  return (wl && contains) || (!wl && !contains);
  }

}
