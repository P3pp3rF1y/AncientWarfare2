package net.shadowmage.ancientwarfare.structure.town;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.structure.template.build.StructureBB;
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



/**
 * the remaining 'cluster value' that can be used by structures to be generated in this town
 */
int remainingGenerationValue;
private HashMap<String, TownGeneratedEntry> generatedStructureMap = new HashMap<String, TownGeneratedEntry>();
private List<StructureBB> generatedBoundingBoxes = new ArrayList<StructureBB>();

public TownGenerator(World world, TownBoundingArea area, TownTemplate template)
  {
  this.world = world;
  this.area = area;
  this.template = template;  
  this.rng = new Random();
  }

public void generate()
  {
  this.area.wallSize = template.getWallSize();
  this.remainingGenerationValue = template.maxValue;  
  area.townOrientation = rng.nextInt(4);
  area.townCenterX = area.getBlockMinX() + area.getBlockWidth()/2;
  area.townCenterZ = area.getBlockMinZ() + area.getBlockLength()/2;  
  fillStructureMap();
  doGeneration();
  }

/**
 * add initial (empty) generation entries to structure map for each structure type in the template
 */
private void fillStructureMap()
  {
  for(TownStructureEntry e : template.getStructureEntries())
    {
    generatedStructureMap.put(e.templateName, new TownGeneratedEntry());
    }
  }

private void doGeneration()
  {
  TownGeneratorBorders.generateBorders(world, area);  
  TownGeneratorBorders.levelTownArea(world, area);
  TownGeneratorWalls.generateWalls(world, area, template, rng);
  TownGeneratorRoads.generateRoads(world, area, template, generatedBoundingBoxes);
  }

public static final class TownGeneratedEntry
{
int numGenerated;
}

}
