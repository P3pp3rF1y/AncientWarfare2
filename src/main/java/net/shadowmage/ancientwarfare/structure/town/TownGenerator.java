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
 * 0=n, 1=e, 2=s, 3=w -- determines placement of town-hall building (if any) and direction that 'main street' runs.<br>
 * 0=road runs e/w, main structure is on north side of the road (facing south)<br>
 * 1=road runs n/s, main structure is on east side of the road (facing west)<br>
 * 2=road runs e/w, main structure is on south side of the road (facing north)<br>
 * 3=road runs n/s, main structure is on west side of the road (facing east)<br>
 */
int generationOrientation;

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
  this.area.wallHeight = template.getWallHeight();
  this.area.wallSize = template.getWallSize();
  this.remainingGenerationValue = template.maxValue;
  this.generationOrientation = rng.nextInt(4);
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
  TownGeneratorWalls.generateWalls(world, area, template);
  TownGeneratorRoads.generateRoads(world, area, template, generationOrientation, generatedBoundingBoxes);

  }

public static final class TownGeneratedEntry
{
int numGenerated;
}

}
