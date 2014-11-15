package net.shadowmage.ancientwarfare.structure.town;

import java.util.Random;

import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplate;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplateManager;
import net.shadowmage.ancientwarfare.structure.template.build.StructureBuilder;

public class TownGeneratorWalls
{

public static void generateWalls(World world, TownBoundingArea area, TownTemplate template, Random rng)
  {
  if(template.getWallStyle()==0){return;}
  int minX = area.getWallMinX();
  int minZ = area.getWallMinZ();
  int maxX = area.getWallMaxX();
  int maxZ = area.getWallMaxZ();
  int minY = area.getSurfaceY()+1;
  int x, z;
  int orientation;
  StructureBuilder builder;
    
  //construct N wall
  orientation = 0;
  for(int i = 1; i < area.getChunkWidth()-2; i++)
    {
    x = minX + 16*i;
    z = minZ;
    builder = new StructureBuilder(world, getWallSection(rng, template, i, area.getChunkWidth()-1), orientation, x, minY, z);
    builder.instantConstruction();
    }
  
  //construct E wall
  orientation = 1;
  for(int i = 1; i < area.getChunkLength()-2; i++)
    {
    x = maxX;
    z = minZ + 16*i;
    builder = new StructureBuilder(world, getWallSection(rng, template, i, area.getChunkLength()-1), orientation, x, minY, z);
    builder.instantConstruction();
    }
  
  //construct S wall
  orientation = 2;
  for(int i = 1; i < area.getChunkWidth()-2; i++)
    {
    x = maxX - 16*i;
    z = maxZ;
    builder = new StructureBuilder(world, getWallSection(rng, template, i, area.getChunkWidth()-1), orientation, x, minY, z);
    builder.instantConstruction();
    }
  
  //construct W wall
  orientation = 3;
  for(int i = 1; i < area.getChunkLength()-2; i++)
    {
    x = minX;
    z = maxZ - 16*i;
    builder = new StructureBuilder(world, getWallSection(rng, template, i, area.getChunkLength()-1), orientation, x, minY, z);
    builder.instantConstruction();
    }
  
  //construct NW corner
  orientation = 0;
  builder = new StructureBuilder(world, getCornerSection(rng, template), orientation, minX, minY, minZ);
  builder.instantConstruction();
  
  //construct NE corner
  orientation = 1;
  builder = new StructureBuilder(world, getCornerSection(rng, template), orientation, maxX, minY, minZ);
  builder.instantConstruction();
  
  //construct SE corner
  orientation = 2;//facing south, runs east-west
  builder = new StructureBuilder(world, getCornerSection(rng, template), orientation, maxX, minY, maxZ);
  builder.instantConstruction();
  
  //construct SW corner
  orientation = 3;//facing south, runs east-west
  builder = new StructureBuilder(world, getCornerSection(rng, template), orientation, minX, minY, maxZ);
  builder.instantConstruction();
  }

private static StructureTemplate getWallSection(Random rng, TownTemplate template, int index, int wallLength)
  {
  if(template.getWallStyle()==1)//random weighted
    {    
    return StructureTemplateManager.instance().getTemplate(template.getRandomWeightedWall(rng, 0));
    }
  else if(template.getWallStyle()==2)//patterned
    {
    return StructureTemplateManager.instance().getTemplate(template.getRandomWeightedWall(rng, template.getWallPattern(wallLength)[index]));
    }
  return null;
  }

private static StructureTemplate getCornerSection(Random rng, TownTemplate template)
  {
  return StructureTemplateManager.instance().getTemplate(template.getRandomWeightedCorner(rng));
  }

}
