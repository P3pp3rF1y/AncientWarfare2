package net.shadowmage.ancientwarfare.structure.town;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplate;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplateManager;
import net.shadowmage.ancientwarfare.structure.template.build.StructureBB;
import net.shadowmage.ancientwarfare.structure.town.TownTemplate.TownStructureEntry;

/**
 * Responsible for constructing the town -- leveling the area, placing the structures, constructing walls
 * @author Shadowmage
 *
 */
public class TownGenerator
{

public final TownTemplate template;
public final World world;
public final Random rng;
public final int blockSize;
public final int plotSize;
public final StructureBB maximalBounds;
public final StructureBB exteriorBounds;//maximal, shrunk by borderSize (16 blocks), maximal area encompassing extents of exterior buffer zone
public final StructureBB wallsBounds;//exterior shrunk by exteriorSize (configurable), maximal area encompassing extents of walls
public final StructureBB townBounds;//walls shrunk by wallSize (configurable), town generation area
public final TownPartQuadrant[] quadrants = new TownPartQuadrant[4];
public final TownPartQuadrant[] externalQuadrants = new TownPartQuadrant[8];//may be null refs if no exterior area is denoted
public final List<StructureTemplate> uniqueTemplatesToGenerate = new ArrayList<StructureTemplate>();//depleted as used
public final List<StructureTemplate> mainTemplatesToGenerate = new ArrayList<StructureTemplate>();//depleted as used
public final List<StructureTemplate> houseTemplatesToGenerate = new ArrayList<StructureTemplate>();//weighted list
public final List<StructureTemplate> cosmeticTemplatesToGenerate = new ArrayList<StructureTemplate>();//weighted list
public final List<StructureTemplate> exteriorTemplatesToGenerate = new ArrayList<StructureTemplate>();//weighted list

private byte[] testGrid;//TODO used for debug output, can be removed later

public TownGenerator(World world, TownBoundingArea area, TownTemplate template)
  {
  this.world = world;
  this.template = template;  
  this.rng = new Random(0);//TODO seed random from chunk coordinates  
  
  int y1 = area.getSurfaceY()+1;
  int y2 = y1+20;
  
  area.wallSize = template.getWallSize();
  area.exteriorSize = template.getExteriorSize();
    
  this.maximalBounds = new StructureBB(area.getBlockMinX(), y1, area.getBlockMinZ(), area.getBlockMaxX(), y2, area.getBlockMaxZ());
  this.exteriorBounds = new StructureBB(area.getExteriorMinX(), y1, area.getExteriorMinZ(), area.getExteriorMaxX(), y2, area.getExteriorMaxZ());
  this.wallsBounds = new StructureBB(area.getWallMinX(), y1, area.getWallMinZ(), area.getWallMaxX(), y2, area.getWallMaxZ());
  this.townBounds = new StructureBB(area.getTownMinX(), y1, area.getTownMinZ(), area.getTownMaxX(), y2, area.getTownMaxZ());

  this.blockSize = template.getTownBlockSize();
  this.plotSize = template.getTownPlotSize();    
  int width = maximalBounds.getXSize();
  int length = maximalBounds.getZSize();
  this.testGrid = new byte[width*length];
  }

public void generate()
  {
  determineStructuresToGenerate();
  TownGeneratorBorders.generateBorders(world, this);  
  TownGeneratorBorders.levelTownArea(world, this);
  generateGrid();
  generateRoads();
  TownGeneratorWalls.generateWalls(world, this, template, rng);
  TownGeneratorStructures.generateStructures(this);
  }

/**
 * add initial generation entries to list of structures to attempt to generate
 */
private void determineStructuresToGenerate()
  {
  int gen;
  for(TownStructureEntry e : template.getUniqueStructureEntries())
    {
    StructureTemplate t = StructureTemplateManager.instance().getTemplate(e.templateName);
    if(t==null){continue;}
    uniqueTemplatesToGenerate.add(t);
    }
  
  for(TownStructureEntry e : template.getMainStructureEntries())
    {
    StructureTemplate t = StructureTemplateManager.instance().getTemplate(e.templateName);
    if(t==null){continue;}
    mainTemplatesToGenerate.add(t);
    }
  
  for(TownStructureEntry e : template.getHouseStructureEntries())
    {
    StructureTemplate t = StructureTemplateManager.instance().getTemplate(e.templateName);
    if(t==null){continue;}    
    gen = e.min;    
    for(int i = 0; i < gen; i++)
      {
      houseTemplatesToGenerate.add(t);      
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
  
  for(TownStructureEntry e : template.getExteriorStructureEntries())
    {
    StructureTemplate t = StructureTemplateManager.instance().getTemplate(e.templateName);
    if(t==null){continue;}    
    gen = e.min;    
    for(int i = 0; i < gen; i++)
      {
      exteriorTemplatesToGenerate.add(t);      
      }
    }
  }

/**
 * Splits up the town into quadrants<br>
 * quadrants into blocks<br>
 * and blocks into plots<br>
 */
private void generateGrid()
  {
  final int centerX = maximalBounds.getCenterX();
  final int centerZ = maximalBounds.getCenterZ();  
  final int y1 = townBounds.min.y;  
  final int y2 = townBounds.max.y;
  
  StructureBB bb;
  TownPartQuadrant tq;
  boolean[] roadBorders;
  
  //northwest quadrant, pre-shrunk for road borders
  roadBorders = new boolean[]{true, false, false, true};
  bb = new StructureBB(new BlockPosition(townBounds.min.x, y1, townBounds.min.z), new BlockPosition(centerX-2, y2, centerZ-2));
  tq = new TownPartQuadrant(Direction.WEST, Direction.NORTH, bb, roadBorders, this);
  tq.subdivide(template.getTownBlockSize(), template.getTownPlotSize(), true);
  write(tq);
  quadrants[0]=tq;
  
  //northeast quadrant
  roadBorders = new boolean[]{true, true, false, false};
  bb = new StructureBB(new BlockPosition(centerX+1, y1, townBounds.min.z), new BlockPosition(townBounds.max.x, y2, centerZ-2));
  tq = new TownPartQuadrant(Direction.EAST, Direction.NORTH, bb, roadBorders, this);
  tq.subdivide(template.getTownBlockSize(), template.getTownPlotSize(), true);
  write(tq);
  quadrants[1]=tq;
  
  //southeast quadrant
  roadBorders = new boolean[]{false, true, true, false};
  bb = new StructureBB(new BlockPosition(centerX+1, y1, centerZ+1), new BlockPosition(townBounds.max.x, y2, townBounds.max.z));
  tq = new TownPartQuadrant(Direction.EAST, Direction.SOUTH, bb, roadBorders, this);
  tq.subdivide(template.getTownBlockSize(), template.getTownPlotSize(), true);
  write(tq);
  quadrants[2]=tq;
  
  //southwest quadrant
  roadBorders = new boolean[]{false, false, true, true};
  bb = new StructureBB(new BlockPosition(townBounds.min.x, y1, centerZ+1), new BlockPosition(centerX-2, y2, townBounds.max.z));
  tq = new TownPartQuadrant(Direction.WEST, Direction.SOUTH, bb, roadBorders, this);
  tq.subdivide(template.getTownBlockSize(), template.getTownPlotSize(), true);
  write(tq);
  quadrants[3]=tq;
  
  write(centerX, centerZ, (byte)9);
  write(centerX-1, centerZ, (byte)9);
  write(centerX, centerZ-1, (byte)9);
  write(centerX-1, centerZ-1, (byte)9);
    
  String line = writeTestGrid();
  AWLog.logDebug("grid: \n"+line);
  
  if(template.getExteriorSize()>0)
    {
    generateExteriorGrid();
    }
  }

private void generateExteriorGrid()
  {
  TownPartQuadrant tq;
  StructureBB bb;
  boolean[] roadBorders;
  List<TownPartBlock> blocks = new ArrayList<TownPartBlock>();
  int centerX = maximalBounds.getCenterX();
  int centerZ = maximalBounds.getCenterZ();
  int y1 = maximalBounds.min.y;
  int y2 = maximalBounds.max.y;
  int minX = exteriorBounds.min.x;
  int minZ = exteriorBounds.min.z;
  
  //1, northwest
  roadBorders = new boolean[]{false, false, false, true};//  
  bb = new StructureBB(new BlockPosition(minX, y1, minZ), new BlockPosition(centerX-2, y2, wallsBounds.min.z - 1));
  tq = new TownPartQuadrant(Direction.WEST, Direction.NORTH, bb, roadBorders, this);
  tq.subdivide(template.getExteriorSize()*16, template.getTownPlotSize(), false);
  generateRoads(world, tq);
  tq.addBlocks(blocks);
  externalQuadrants[0]=tq;
  
  //2, northeast
  roadBorders = new boolean[]{false, true, false, false};
  bb = new StructureBB(new BlockPosition(centerX, y1, 0), new BlockPosition(exteriorBounds.max.x, y2, wallsBounds.min.z - 1));
  tq = new TownPartQuadrant(Direction.EAST, Direction.NORTH, bb, roadBorders, this);
  tq.subdivide(template.getExteriorSize()*16, template.getTownPlotSize(), false);
  generateRoads(world, tq);
  tq.addBlocks(blocks);
  externalQuadrants[1]=tq;
  
  //3 west, north-part
  roadBorders = new boolean[]{true, false, false, false};
  bb = new StructureBB(new BlockPosition(exteriorBounds.min.x, y1, wallsBounds.min.z), new BlockPosition(wallsBounds.min.x - 1, y2, centerZ-1));
  tq = new TownPartQuadrant(Direction.WEST, Direction.NORTH, bb, roadBorders, this);
  tq.subdivide(template.getExteriorSize()*16, template.getTownPlotSize(), false);
  generateRoads(world, tq);
  tq.addBlocks(blocks);
  externalQuadrants[2]=tq;
  
  //4 east, north-part
  roadBorders = new boolean[]{true, false, false, false};
  bb = new StructureBB(new BlockPosition(wallsBounds.max.x + 1, y1, wallsBounds.min.z), new BlockPosition(exteriorBounds.max.x, y2, centerZ-1));
  tq = new TownPartQuadrant(Direction.EAST, Direction.NORTH, bb, roadBorders, this);
  tq.subdivide(template.getExteriorSize()*16, template.getTownPlotSize(), false);
  generateRoads(world, tq);
  tq.addBlocks(blocks);
  externalQuadrants[3]=tq;
  
  //5 west, south-part
  roadBorders = new boolean[]{false, false, true, false};
  bb = new StructureBB(new BlockPosition(exteriorBounds.min.x, y1, centerZ), new BlockPosition(wallsBounds.min.x - 1, y2, wallsBounds.max.z));
  tq = new TownPartQuadrant(Direction.WEST, Direction.NORTH, bb, roadBorders, this);
  tq.subdivide(template.getExteriorSize()*16, template.getTownPlotSize(), false);
  generateRoads(world, tq);
  tq.addBlocks(blocks);
  externalQuadrants[4]=tq;
  
  //6 east, south-part
  roadBorders = new boolean[]{false, false, true, false};
  bb = new StructureBB(new BlockPosition(wallsBounds.max.x + 1, y1, centerZ - 1), new BlockPosition(exteriorBounds.max.x, y2, wallsBounds.max.z));
  tq = new TownPartQuadrant(Direction.EAST, Direction.NORTH, bb, roadBorders, this);
  tq.subdivide(template.getExteriorSize()*16, template.getTownPlotSize(), false);
  generateRoads(world, tq);
  tq.addBlocks(blocks);
  externalQuadrants[5]=tq;
  
  //7 southwest
  roadBorders = new boolean[]{false, false, false, true};
  bb = new StructureBB(new BlockPosition(exteriorBounds.min.x, y1, wallsBounds.max.z+1), new BlockPosition(centerX-1, y2, exteriorBounds.max.z));
  tq = new TownPartQuadrant(Direction.WEST, Direction.SOUTH, bb, roadBorders, this);
  tq.subdivide(template.getExteriorSize()*16, template.getTownPlotSize(), false);
  generateRoads(world, tq);
  tq.addBlocks(blocks);
  externalQuadrants[6]=tq;
  
  //8 southeast 
  roadBorders = new boolean[]{false, true, false, false};
  bb = new StructureBB(new BlockPosition(centerX, y1, wallsBounds.max.z + 1), new BlockPosition(exteriorBounds.max.x, y2, exteriorBounds.max.z));
  tq = new TownPartQuadrant(Direction.EAST, Direction.SOUTH, bb, roadBorders, this);
  tq.subdivide(template.getExteriorSize()*16, template.getTownPlotSize(), false);
  generateRoads(world, tq);
  tq.addBlocks(blocks);  
  externalQuadrants[7]=tq;  
  }

private void generateRoads()
  {
  for(TownPartQuadrant tq : quadrants)
    {
    generateRoads(world, tq);
    }
  generateAdditionalRoads();
  }

/**
 * generates roads on the extent of a townquadrant
 * @param world
 * @param tq
 */
private void generateRoads(World world, TownPartQuadrant tq)
  {
  int minX = tq.bb.min.x;
  int maxX = tq.bb.max.x;
  if(tq.hasRoadBorder(Direction.WEST)){minX--;}
  if(tq.hasRoadBorder(Direction.EAST)){maxX++;}
  for(int x = minX; x<=maxX; x++)
    {
    if(tq.hasRoadBorder(Direction.NORTH)){genRoadBlock(world, x, tq.bb.min.y-1, tq.bb.min.z-1);}//north
    if(tq.hasRoadBorder(Direction.SOUTH)){genRoadBlock(world, x, tq.bb.min.y-1, tq.bb.max.z+1);}//south
    }
  int minZ = tq.bb.min.z;
  int maxZ = tq.bb.max.z;
  if(tq.hasRoadBorder(Direction.NORTH)){minZ--;}
  if(tq.hasRoadBorder(Direction.SOUTH)){maxZ++;}
  for(int z = minZ; z<=maxZ; z++)
    {
    if(tq.hasRoadBorder(Direction.WEST)){genRoadBlock(world, tq.bb.min.x-1, tq.bb.min.y-1, z);}//west
    if(tq.hasRoadBorder(Direction.EAST)){genRoadBlock(world, tq.bb.max.x+1, tq.bb.min.y-1, z);}//east
    }
  for(TownPartBlock tb : tq.blocks)
    {
    generateRoads(world, tb);
    }
  }

/**
 * Generates roads on the extends of a townblock
 * @param world
 * @param tb
 */
private void generateRoads(World world, TownPartBlock tb)
  {
  int minX = tb.bb.min.x;
  int maxX = tb.bb.max.x;
  if(tb.hasRoadBorder(Direction.WEST)){minX--;}
  if(tb.hasRoadBorder(Direction.EAST)){maxX++;}
  for(int x = minX; x<=maxX; x++)
    {
    if(tb.hasRoadBorder(Direction.NORTH)){genRoadBlock(world, x, tb.bb.min.y-1, tb.bb.min.z-1);}//north
    if(tb.hasRoadBorder(Direction.SOUTH)){genRoadBlock(world, x, tb.bb.min.y-1, tb.bb.max.z+1);}//south
    }
  int minZ = tb.bb.min.z;
  int maxZ = tb.bb.max.z;
  if(tb.hasRoadBorder(Direction.NORTH)){minZ--;}
  if(tb.hasRoadBorder(Direction.SOUTH)){maxZ++;}
  for(int z = minZ; z<=maxZ; z++)
    {
    if(tb.hasRoadBorder(Direction.WEST)){genRoadBlock(world, tb.bb.min.x-1, tb.bb.min.y-1, z);}//west
    if(tb.hasRoadBorder(Direction.EAST)){genRoadBlock(world, tb.bb.max.x+1, tb.bb.min.y-1, z);}//east
    }
  }

/**
 * generates roads running from the edges of the 'townBounds' to the edges of the 'wallBounds'
 */
private void generateAdditionalRoads()
  {
  int minX, minZ, maxX, maxZ, y;
  y = maximalBounds.min.y - 1;
  
  //northern road
  minX = maximalBounds.getCenterX()-2;
  maxX = minX+3;
  minZ = exteriorBounds.min.z;
  maxZ = townBounds.min.z - 1;  
  for(int x = minX; x<=maxX; x++)
    {
    for(int z = minZ; z<=maxZ; z++)
      {
      genRoadBlock(world, x, y, z);
      }
    }
  
  //eastern road
  minX = townBounds.max.x + 1;
  minZ = maximalBounds.getCenterZ()-2;
  maxX = exteriorBounds.max.x;
  maxZ = minZ+3;
  for(int x = minX; x<=maxX; x++)
    {
    for(int z = minZ; z<=maxZ; z++)
      {
      genRoadBlock(world, x, y, z);
      }
    }
  
  //southern road
  minX = maximalBounds.getCenterX()-2;
  minZ = townBounds.max.z+1;
  maxX = minX + 3;
  maxZ = exteriorBounds.max.z;
  for(int x = minX; x<=maxX; x++)
    {
    for(int z = minZ; z<=maxZ; z++)
      {
      genRoadBlock(world, x, y, z);
      }
    }
  
  //western road
  minX = exteriorBounds.min.x;
  minZ = maximalBounds.getCenterZ()-2;
  maxX = townBounds.min.x - 1;
  maxZ = minZ + 3;
  for(int x = minX; x<=maxX; x++)
    {
    for(int z = minZ; z<=maxZ; z++)
      {
      genRoadBlock(world, x, y, z);
      }
    }
  }

private void genRoadBlock(World world, int x, int y, int z)
  {
  Block block = template.getRoadFillBlock();
  int meta = template.getRoadFillMeta();
  world.setBlock(x, y, z, block, meta, 3);
  world.setBlock(x, y-1, z, Blocks.cobblestone, 0, 3);
  }

private void write(TownPartQuadrant tq)
  {
  for(int x = tq.bb.min.x; x<=tq.bb.max.x; x++)
    {
    write(x, tq.bb.min.z, (byte)1);
    write(x, tq.bb.max.z, (byte)1);
    }
  for(int z = tq.bb.min.z; z<=tq.bb.max.z; z++)
    {
    write(tq.bb.min.x, z, (byte)1);
    write(tq.bb.max.x, z, (byte)1);
    }
  for(TownPartBlock tb : tq.blocks)
    {
    write(tb);
    }
  }

private void write(TownPartBlock tb)
  {
  for(int x = tb.bb.min.x; x<=tb.bb.max.x; x++)
    {
    write(x, tb.bb.min.z, (byte)2);
    write(x, tb.bb.max.z, (byte)2);
    }
  for(int z = tb.bb.min.z; z<=tb.bb.max.z; z++)
    {
    write(tb.bb.min.x, z, (byte)2);
    write(tb.bb.max.x, z, (byte)2);
    }
  for(TownPartPlot tp : tb.plots)
    {
    write(tp);
    }
  }

private void write(TownPartPlot tp)
  {
  for(int x = tp.bb.min.x; x<=tp.bb.max.x; x++)
    {
    write(x, tp.bb.min.z, (byte)3);
    write(x, tp.bb.max.z, (byte)3);
    }
  for(int z = tp.bb.min.z; z<=tp.bb.max.z; z++)
    {
    write(tp.bb.min.x, z, (byte)3);
    write(tp.bb.max.x, z, (byte)3);
    }
  }

private void write(int x, int z, byte val)
  {
  x-=maximalBounds.min.x;
  z-=maximalBounds.min.z;
  testGrid[getIndex(x, z)]=val;
  }

private int getIndex(int x, int z)
  {
  int width = maximalBounds.getXSize();
  return z*width + x;
  }

private String writeTestGrid()
  {
  int width = maximalBounds.getXSize();
  int length = maximalBounds.getZSize();
  String out = "";
  for(int z = 0; z < length; z++)
    {
    for(int x = 0; x< width; x++)
      {
      out = out + String.valueOf(testGrid[getIndex(x,z)]);
      if(x<width-1)
        {
        out = out + ",";
        }
      }
    if(z<length-1)
      {
      out = out+"\n";
      }
    }
  return out;
  }

public static class TownPartBlockComparator implements Comparator<TownPartBlock>
{

@Override
public int compare(TownPartBlock o1, TownPartBlock o2)
  {
  if(o1.distFromTownCenter<o2.distFromTownCenter){return -1;}
  else if(o1.distFromTownCenter>o2.distFromTownCenter){return 1;}  
  return 0;
  }
}

}
