package net.shadowmage.ancientwarfare.structure.town;

import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import cpw.mods.fml.common.IWorldGenerator;

public class WorldTownGenerator implements IWorldGenerator
{

private static WorldTownGenerator instance = new WorldTownGenerator();
private WorldTownGenerator(){}
public static WorldTownGenerator instance(){return instance;}

@Override
public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider)
  {
  attemptGeneration(world, random, chunkX*16 + 7, chunkZ*16 + 7);
  }

public void attemptGeneration(World world, Random rng, int blockX, int blockZ)
  {
  TownBoundingArea area = TownPlacementValidator.findGenerationPosition(world, blockX, blockZ);
  if(area==null){return;}
  TownTemplate template = TownTemplateManager.instance().selectTemplateForGeneration(world, blockX, blockZ, area);
  if(template==null){return;}
  if(area.getChunkWidth()-1 > template.getMaxSize())
    {
    area.chunkMaxX = area.chunkMinX + template.getMaxSize();
    }
  if(area.getChunkLength() - 1 > template.getMaxSize())
    {
    area.chunkMaxZ = area.chunkMinZ + template.getMaxSize();
    }
  if(!TownPlacementValidator.validateAreaForPlacement(world, area)){return;}//cannot validate the area until bounds are possibly shrunk by selected template
  TownGenerator generator = new TownGenerator(world, area, template);
  generator.generate();
  }

}
