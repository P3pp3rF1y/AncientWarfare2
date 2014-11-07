package net.shadowmage.ancientwarfare.structure.town;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.structure.config.AWStructureStatics;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplate;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplateManager;
import net.shadowmage.ancientwarfare.structure.template.build.StructureBB;
import net.shadowmage.ancientwarfare.structure.template.build.StructureBuilder;
import net.shadowmage.ancientwarfare.structure.town.TownTemplate.TownStructureEntry;
import net.shadowmage.ancientwarfare.structure.world_gen.WorldStructureGenerator;

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
  doBorder();
  doLeveling();
  if(template.getWallStyle()!=0)
    {
    generateWalls();
    placeGates();
    }
  layMainRoad();
  generateTownhall();
  generateStructures();
  }

private void doBorder()
  {
  int minX, maxX, minZ, maxZ;  
  int step;
  int fillBase = area.minY+7;
  int levelBase = fillBase;
  
  minX = area.getBlockMinX();
  maxX = area.getWallMinX()-1;
  for(int px = minX; px <= maxX; px++)
    {    
    for(int pz = area.getBlockMinZ(); pz<=area.getBlockMaxZ(); pz++)
      {
      step = WorldStructureGenerator.getStepNumber(px, pz, area.getWallMinX(), area.getWallMaxX(), area.getWallMinZ(), area.getWallMaxZ());
      handleBorderBlock(px, pz, fillBase-step, levelBase+step, getFillBlock(px, pz, false, Blocks.dirt), getFillBlock(px, pz, true, Blocks.grass));
      }
    }  
  
  minX = area.getWallMaxX()+1;
  maxX = area.getBlockMaxX();  
  for(int px = minX; px <= maxX; px++)
    {    
    for(int pz = area.getBlockMinZ(); pz<=area.getBlockMaxZ(); pz++)
      {
      step = WorldStructureGenerator.getStepNumber(px, pz, area.getWallMinX(), area.getWallMaxX(), area.getWallMinZ(), area.getWallMaxZ()); 
      handleBorderBlock(px, pz, fillBase-step, levelBase+step, getFillBlock(px, pz, false, Blocks.dirt), getFillBlock(px, pz, true, Blocks.grass));
      }
    } 
  
  minZ = area.getBlockMinZ();
  maxZ = area.getWallMinZ()-1;
  for(int pz = minZ; pz <= maxZ; pz++)
    {
    for(int px = area.getBlockMinX(); px<=area.getBlockMaxX(); px++)
      {
      step = WorldStructureGenerator.getStepNumber(px, pz, area.getWallMinX(), area.getWallMaxX(), area.getWallMinZ(), area.getWallMaxZ());
      handleBorderBlock(px, pz, fillBase-step, levelBase+step, getFillBlock(px, pz, false, Blocks.dirt), getFillBlock(px, pz, true, Blocks.grass));
      }
    }
  
  minZ = area.getWallMaxZ()+1;
  maxZ = area.getBlockMaxZ();
  for(int pz = minZ; pz <= maxZ; pz++)
    {
    for(int px = area.getBlockMinX(); px<=area.getBlockMaxX(); px++)
      {
      step = WorldStructureGenerator.getStepNumber(px, pz, area.getWallMinX(), area.getWallMaxX(), area.getWallMinZ(), area.getWallMaxZ());
      handleBorderBlock(px, pz, fillBase-step, levelBase+step, getFillBlock(px, pz, false, Blocks.dirt), getFillBlock(px, pz, true, Blocks.grass));
      }
    }  
  }

private void handleBorderBlock(int x, int z, int fillLevel, int cutLevel, Block fillBlock, Block topBlock)
  {  
  int y = getTopFilledHeight(world.getChunkFromBlockCoords(x, z), x&15, z&15, true);
  if(y >= cutLevel)
    {
    for(int py = world.provider.getActualHeight(); py > cutLevel; py--){world.setBlockToAir(x, py, z);}
    world.setBlock(x, cutLevel, z, topBlock);
    }
  if(y <= fillLevel)
    {
    for(int py = y+1; py < fillLevel; py++)
      {
      world.setBlock(x, py, z, fillBlock);
      }
    world.setBlock(x, fillLevel, z, topBlock);
    }
  }

private void doLeveling()
  {
  int minX = area.getWallMinX();
  int minZ = area.getWallMinZ();
  int maxX = area.getWallMaxX();
  int maxZ = area.getWallMaxZ();
  
  Block fillBlock;
  int desiredTopBlockHeight = area.getSurfaceY();
  int height;
  for(int x = minX; x<=maxX; x++)
    {
    for(int z = minZ; z<=maxZ; z++)
      {
      height = getTopFilledHeight(world.getChunkFromBlockCoords(x, z), x&15, z&15, true);
      fillBlock = getFillBlock(x, z, false, Blocks.grass);
      if(height < desiredTopBlockHeight)
        {
        for(int y = height; y < desiredTopBlockHeight; y++)
          {
          world.setBlock(x, y, z, fillBlock);
          }
        }
      else if(height>desiredTopBlockHeight)
        {
        for(int y = height; y > desiredTopBlockHeight; y--){world.setBlockToAir(x, y, z);}
        }
      fillBlock = getFillBlock(x, z, true, Blocks.grass);
      world.setBlock(x, desiredTopBlockHeight, z, fillBlock);
      }
    }
  }

private void generateWalls()
  {
  wallTemplateTest(); 
  }

private void wallTemplateTest()
  {
  StructureTemplate s1 = StructureTemplateManager.instance().getTemplate("wall_straight1");
  StructureTemplate s2 = StructureTemplateManager.instance().getTemplate("wall_corner1");
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
    builder = new StructureBuilder(world, s1, orientation, x, minY, z);
    builder.instantConstruction();
    }
  
  //construct E wall
  orientation = 1;
  for(int i = 1; i < area.getChunkLength()-2; i++)
    {
    x = maxX;
    z = minZ + 16*i;
    builder = new StructureBuilder(world, s1, orientation, x, minY, z);
    builder.instantConstruction();
    }
  
  //construct S wall
  orientation = 2;
  for(int i = 1; i < area.getChunkLength()-2; i++)
    {
    x = maxX - 16*i;
    z = maxZ;
    builder = new StructureBuilder(world, s1, orientation, x, minY, z);
    builder.instantConstruction();
    }
  
  //construct W wall
  orientation = 3;
  for(int i = 1; i < area.getChunkLength()-2; i++)
    {
    x = minX;
    z = maxZ - 16*i;
    builder = new StructureBuilder(world, s1, orientation, x, minY, z);
    builder.instantConstruction();
    }
  
  //construct NW corner
  orientation = 3;
  builder = new StructureBuilder(world, s2, orientation, minX, minY, minZ);
  builder.instantConstruction();
  
  //construct NE corner
  orientation = 0;
  builder = new StructureBuilder(world, s2, orientation, maxX, minY, minZ);
  builder.instantConstruction();
  
  //construct SE corner
  orientation = 1;//facing south, runs east-west
  builder = new StructureBuilder(world, s2, orientation, maxX, minY, maxZ);
  builder.instantConstruction();
  
  //construct SW corner
  orientation = 2;//facing south, runs east-west
  builder = new StructureBuilder(world, s2, orientation, minX, minY, maxZ);
  builder.instantConstruction();
  }

/**
 * Lays down the initial main-street crossing the town
 */
private void layMainRoad()
  {
  int cx = area.getBlockMinX() + area.getBlockWidth()/2;
  int cz = area.getBlockMinZ() + area.getBlockLength()/2;
  int minX, minZ, maxX, maxZ;
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
  
  AWLog.logDebug("pre placement loop: "+minX+","+maxX+","+minZ+","+maxZ);
  for(int x = minX; x <= maxX; x++)
    {
    for(int z = minZ; z <= maxZ; z++)
      {
      AWLog.logDebug("setting block to gravel: "+x+","+area.getSurfaceY()+","+z);
      world.setBlock(x, area.getSurfaceY(), z, template.getRoadFillBlock());
      }
    }
  
  StructureBB roadBB = new StructureBB(new BlockPosition(minX, area.getSurfaceY(), minZ), new BlockPosition(maxX, area.getSurfaceY(), maxZ));
  AWLog.logDebug("generated main road: "+roadBB);
  generatedBoundingBoxes.add(roadBB);
  }

private void placeGates(){}//TODO

private void generateTownhall(){}//TODO

private void generateStructures(){}//TODO

private void generateStructure(){}//TODO

private int getTopFilledHeight(Chunk chunk, int xInChunk, int zInChunk, boolean skippables)
  {
  int maxY = chunk.getTopFilledSegment() + 16;
  Block block;
  for(int y = maxY; y > 0; y--)
    {
    block = chunk.getBlock(xInChunk, y, zInChunk);
    if(block==null || block==Blocks.air || (skippables && AWStructureStatics.skippableBlocksContains(block)) || block.getMaterial()==Material.water){continue;}
    return y;
    }
  return -1;
  }

private Block getFillBlock(int x, int z, boolean surface, Block defaultBlock)
  {
  Block block = defaultBlock;
  BiomeGenBase biome = world.getBiomeGenForCoordsBody(x, z);
  if(biome!=null && biome.topBlock!=null)
    {
    if(surface && biome.topBlock!=null){block = biome.topBlock;}
    else if(!surface && biome.fillerBlock!=null){block = biome.fillerBlock;}
    }
  return block;
  }

public static final class TownGeneratedEntry
{
int numGenerated;
}

}
