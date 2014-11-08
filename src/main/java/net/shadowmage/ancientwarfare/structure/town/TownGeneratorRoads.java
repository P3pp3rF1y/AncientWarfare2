package net.shadowmage.ancientwarfare.structure.town;

import java.util.List;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.structure.template.build.StructureBB;

public class TownGeneratorRoads
{

public static void generateRoads(World world, TownBoundingArea area, TownTemplate template, List<StructureBB> generatedBoundingBoxes)
  {
  int cx = area.getBlockMinX() + area.getBlockWidth()/2;
  int cz = area.getBlockMinZ() + area.getBlockLength()/2;
  int minX, minZ, maxX, maxZ;
  int generationOrientation = area.getTownOrientation();
  if(generationOrientation==0 || generationOrientation==2)
    {
    minX = area.getWallMinX();
    maxX = area.getWallMaxX();
    minZ = cz - (template.getRoadWidth()/2);
    maxZ = minZ + (template.getRoadWidth()-1);
    }
  else
    {
    minX = cx - (template.getRoadWidth()/2);
    maxX = minX + (template.getRoadWidth()-1);
    minZ = area.getWallMinZ();
    maxZ = area.getWallMaxZ();
    }

  for(int x = minX; x <= maxX; x++)
    {
    for(int z = minZ; z <= maxZ; z++)
      {
      world.setBlock(x, area.getSurfaceY(), z, template.getRoadFillBlock());
      world.setBlock(x, area.getSurfaceY()-1, z, Blocks.dirt);//TODO grab road underfill block from somewhere?
      }
    }

  StructureBB roadBB = new StructureBB(new BlockPosition(minX, area.getSurfaceY()-1, minZ), new BlockPosition(maxX, area.getSurfaceY(), maxZ));
  generatedBoundingBoxes.add(roadBB);
    
  }

private static void generateMainRoad()
  {
  
  }

}
