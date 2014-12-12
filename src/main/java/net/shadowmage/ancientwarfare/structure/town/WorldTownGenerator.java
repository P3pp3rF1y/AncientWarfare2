package net.shadowmage.ancientwarfare.structure.town;

import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.gamedata.AWGameData;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.structure.template.build.StructureBB;
import net.shadowmage.ancientwarfare.structure.world_gen.StructureEntry;
import net.shadowmage.ancientwarfare.structure.world_gen.StructureMap;
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
  if(area==null)
    {
    AWLog.logDebug("Could not find valid position..");
    return;
    }
  TownTemplate template = TownTemplateManager.instance().selectTemplateForGeneration(world, blockX, blockZ, area);
  if(template==null)
    {
    AWLog.logDebug("Could not find template for area: "+area);
    return;
    }
  if(area.getChunkWidth() - 1 > template.getMaxSize())//shrink width down to town max size
    {
    area.chunkMaxX = area.chunkMinX + template.getMaxSize();
    }
  if(area.getChunkLength() - 1 > template.getMaxSize())//shrink length down to town max size
    {
    area.chunkMaxZ = area.chunkMinZ + template.getMaxSize();
    }
  if(!TownPlacementValidator.validateAreaForPlacement(world, area))
    {
    AWLog.logDebug("area failed validation.. "+area);
    return;
    }//cannot validate the area until bounds are possibly shrunk by selected template
  
  /**
   * add the town to the generated structure map, as a -really- large structure entry
   */
  StructureMap map = AWGameData.INSTANCE.getData("AWStructureMap", world, StructureMap.class);
  StructureBB bb = new StructureBB(new BlockPosition(area.getBlockMinX(), area.getMinY(), area.getBlockMinZ()), new BlockPosition(area.getBlockMaxX(), area.getMaxY(), area.getBlockMaxZ()));
  StructureEntry entry = new StructureEntry(bb, template.getTownTypeName(), template.getClusterValue());
  map.setGeneratedAt(world, area.getCenterX(), area.getSurfaceY(), area.getCenterZ(), 0, entry, false);
  map.markDirty();
  
  TownGenerator generator = new TownGenerator(world, area, template);
  generator.generate();
  }

}
