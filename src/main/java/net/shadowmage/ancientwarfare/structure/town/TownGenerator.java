package net.shadowmage.ancientwarfare.structure.town;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplate;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplateManager;
import net.shadowmage.ancientwarfare.structure.town.TownTemplate.TownStructureEntry;

/**
 * Responsible for constructing the town -- leveling the area, placing the structures, constructing walls
 * @author Shadowmage
 *
 */
public class TownGenerator
{

private Random rng;
private World world;
private TownBoundingArea area;
private TownTemplate template;
private TownPartCollection town;

private List<StructureTemplate> templatesToGenerate = new ArrayList<StructureTemplate>();
List<StructureTemplate> cosmeticTemplatesToGenerate = new ArrayList<StructureTemplate>();

public TownGenerator(World world, TownBoundingArea area, TownTemplate template)
  {
  this.world = world;
  this.area = area;
  this.template = template;  
  this.rng = new Random();
  this.area.townCenterX = area.getBlockMinX() + area.getBlockWidth()/2;
  this.area.townCenterZ = area.getBlockMinZ() + area.getBlockLength()/2;  
  this.area.wallSize = template.getWallSize(); 
  this.town = new TownPartCollection(area, template.getTownBlockSize(), template.getTownPlotSize(), rng);
  }

public void generate()
  {
  fillStructureMap();
  doGeneration();
  }

/**
 * add initial generation entries to list of structures to attempt to generate
 */
private void fillStructureMap()
  {
  int min, max, gen;
  for(TownStructureEntry e : template.getStructureEntries())
    {
    StructureTemplate t = StructureTemplateManager.instance().getTemplate(e.templateName);
    if(t==null){continue;}
    min = e.min;
    max = e.max;
    gen = min + (max-min>0 ? rng.nextInt(max-min): 0);
    for(int i = 0; i < gen; i++)
      {
      templatesToGenerate.add(t);
      }
    }
  for(TownStructureEntry e : template.getCosmeticEntries())
    {
    StructureTemplate t = StructureTemplateManager.instance().getTemplate(e.templateName);  
    if(t==null){continue;}
    gen = e.min;
    for(int i = 0; i < gen; i++)
      {
      this.cosmeticTemplatesToGenerate.add(t);
      }
    }
  }

private void doGeneration()
  {
  TownGeneratorBorders.generateBorders(world, area);  
  TownGeneratorBorders.levelTownArea(world, area);
  TownGeneratorWalls.generateWalls(world, area, template, rng);
  this.town.generateGrid();
  this.town.generateRoads(world);
  StructureTemplate townHall = null;
  if(template.getTownHallEntry()!=null){townHall=StructureTemplateManager.instance().getTemplate(template.getTownHallEntry().templateName);}
  this.town.generateStructures(world, townHall, templatesToGenerate, cosmeticTemplatesToGenerate);
  }

}
