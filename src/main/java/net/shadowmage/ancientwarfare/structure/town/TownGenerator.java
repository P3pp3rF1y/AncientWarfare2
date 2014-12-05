package net.shadowmage.ancientwarfare.structure.town;

import java.util.ArrayList;
import java.util.Collections;
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
import net.shadowmage.ancientwarfare.structure.template.build.StructureBuilder;
import net.shadowmage.ancientwarfare.structure.town.TownTemplate.TownStructureEntry;

/**
 * Responsible for constructing the town -- leveling the area, placing the structures, constructing walls
 * @author Shadowmage
 *
 */
public class TownGenerator
{


private byte[] testGrid;

public final int blockSize;
public final int plotSize;

StructureBB maximalBounds;
StructureBB exteriorBounds;//maximal, shrunk by borderSize (16 blocks), maximal area encompassing extents of exterior buffer zone
StructureBB wallsBounds;//exterior shrunk by exteriorSize (configurable), maximal area encompassing extents of walls
StructureBB townBounds;//walls shrunk by wallSize (configurable), town generation area

/**
 * 0=nw<br>
 * 1=ne<br>
 * 2=se<br>
 * 3=sw
 */
TownPartQuadrant[] quadrants = new TownPartQuadrant[4];
private Random rng;
private World world;
private TownTemplate template;

private List<StructureTemplate> templatesToGenerate = new ArrayList<StructureTemplate>();
private List<StructureTemplate> cosmeticTemplatesToGenerate = new ArrayList<StructureTemplate>();

//private List<StructureTemplate> exterior
//private List<StructureTemplate> uniques
//private List<StructureTemplate> mains
//private List<StructureTemplate> houses
//private List<StructureTemplate> cosmetics

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
  TownGeneratorBorders.generateBorders(world, this);  
  TownGeneratorBorders.levelTownArea(world, this);
  generateGrid();
  generateRoads();
  TownGeneratorWalls.generateWalls(world, this, template, rng);
  StructureTemplate townHall = null;
  if(template.getTownHallEntry()!=null){townHall=StructureTemplateManager.instance().getTemplate(template.getTownHallEntry().templateName);}
  generateStructures(world, townHall);
  }

private void addQuadrant(int index, TownPartQuadrant tq)
  {
  quadrants[index]=tq;
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
  addQuadrant(0, tq);
  
  //northeast quadrant
  roadBorders = new boolean[]{true, true, false, false};
  bb = new StructureBB(new BlockPosition(centerX+1, y1, townBounds.min.z), new BlockPosition(townBounds.max.x, y2, centerZ-2));
  tq = new TownPartQuadrant(Direction.EAST, Direction.NORTH, bb, roadBorders, this);
  tq.subdivide(template.getTownBlockSize(), template.getTownPlotSize(), true);
  write(tq);
  addQuadrant(1, tq);
  
  //southeast quadrant
  roadBorders = new boolean[]{false, true, true, false};
  bb = new StructureBB(new BlockPosition(centerX+1, y1, centerZ+1), new BlockPosition(townBounds.max.x, y2, townBounds.max.z));
  tq = new TownPartQuadrant(Direction.EAST, Direction.SOUTH, bb, roadBorders, this);
  tq.subdivide(template.getTownBlockSize(), template.getTownPlotSize(), true);
  write(tq);
  addQuadrant(2, tq);
  
  //southwest quadrant
  roadBorders = new boolean[]{false, false, true, true};
  bb = new StructureBB(new BlockPosition(townBounds.min.x, y1, centerZ+1), new BlockPosition(centerX-2, y2, townBounds.max.z));
  tq = new TownPartQuadrant(Direction.WEST, Direction.SOUTH, bb, roadBorders, this);
  tq.subdivide(template.getTownBlockSize(), template.getTownPlotSize(), true);
  write(tq);
  addQuadrant(3, tq);
  
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
//  TownPartQuadrant tq;
//  StructureBB bb;
//  boolean[] roadBorders;
//  List<TownPartBlock> blocks = new ArrayList<TownPartBlock>();
//  int centerX = maximalBounds.getCenterX();
//  int centerZ = maximalBounds.getCenterZ();
//  int y1 = maximalBounds.min.y;
//  int y2 = maximalBounds.max.y;
//  int minX = exteriorBounds.min.x;
//  int minZ = exteriorBounds.min.z;
//  
//  //1, northwest
//  roadBorders = new boolean[]{false, false, false, true};//  
//  bb = new StructureBB(new BlockPosition(minX, y1, minZ), new BlockPosition(centerX-2, y2, wallsBounds.min.z - 1));
//  tq = new TownPartQuadrant(Direction.WEST, Direction.NORTH, bb, roadBorders, this);
//  tq.subdivide(template.getExteriorSize()*16, template.getTownPlotSize(), false);
//  generateRoads(world, tq);
//  tq.addBlocks(blocks);  
//  //2, northeast
//  roadBorders = new boolean[]{false, true, false, false};
//  bb = new StructureBB(new BlockPosition(centerX, 0, 0), new BlockPosition(width, 0, area.getWallMinZ()-1));
//  tq = new TownPartQuadrant(Direction.EAST, Direction.NORTH, bb, roadBorders, boundingArea);
//  tq.subdivide(template.getExteriorSize()*16, template.getTownPlotSize());
//  generateRoads(world, tq);
//  tq.addBlocks(blocks);
//  
//  //3 west, north-part
//  roadBorders = new boolean[]{true, false, false, false};
//  bb = new StructureBB(new BlockPosition(0, 0, area.getWallMinZ()), new BlockPosition(area.getWallMinX()-1, 0, centerZ-1));
//  tq = new TownPartQuadrant(Direction.WEST, Direction.NORTH, bb, roadBorders, boundingArea);
//  tq.subdivide(template.getExteriorSize()*16, template.getTownPlotSize());
//  generateRoads(world, tq);
//  tq.addBlocks(blocks);
//  
//  //4 east, north-part
//  roadBorders = new boolean[]{true, false, false, false};
//  bb = new StructureBB(new BlockPosition(area.getWallMaxX()+1, 0, area.getWallMinZ()), new BlockPosition(width, 0, centerZ-1));
//  tq = new TownPartQuadrant(Direction.EAST, Direction.NORTH, bb, roadBorders, boundingArea);
//  tq.subdivide(template.getExteriorSize()*16, template.getTownPlotSize());
//  generateRoads(world, tq);
//  tq.addBlocks(blocks);
//  
//  //5 west, south-part
//  roadBorders = new boolean[]{false, false, true, false};
//  bb = new StructureBB(new BlockPosition(0, 0, centerZ), new BlockPosition(area.getWallMinX()-1, 0, area.getWallMaxZ()));
//  tq = new TownPartQuadrant(Direction.WEST, Direction.NORTH, bb, roadBorders, boundingArea);
//  tq.subdivide(template.getExteriorSize()*16, template.getTownPlotSize());
//  generateRoads(world, tq);
//  tq.addBlocks(blocks);
//  
//  //6 east, south-part
//  roadBorders = new boolean[]{false, false, true, false};
//  bb = new StructureBB(new BlockPosition(area.getWallMaxX()+1, 0, centerZ-1), new BlockPosition(width, 0, area.getWallMaxZ()));
//  tq = new TownPartQuadrant(Direction.EAST, Direction.NORTH, bb, roadBorders, boundingArea);
//  tq.subdivide(template.getExteriorSize()*16, template.getTownPlotSize());
//  generateRoads(world, tq);
//  tq.addBlocks(blocks);
//  
//  //7 southwest
//  roadBorders = new boolean[]{false, false, false, true};
//  bb = new StructureBB(new BlockPosition(0, 0, area.getWallMaxZ()+1), new BlockPosition(centerX-1, 0, length));
//  tq = new TownPartQuadrant(Direction.WEST, Direction.SOUTH, bb, roadBorders, boundingArea);
//  tq.subdivide(template.getExteriorSize()*16, template.getTownPlotSize());
//  generateRoads(world, tq);
//  tq.addBlocks(blocks);
//  
//  roadBorders = new boolean[]{false, true, false, false};
//  bb = new StructureBB(new BlockPosition(centerX, 0, area.getWallMaxZ()+1), new BlockPosition(width, 0, length));
//  tq = new TownPartQuadrant(Direction.EAST, Direction.SOUTH, bb, roadBorders, boundingArea);
//  tq.subdivide(template.getExteriorSize()*16, template.getTownPlotSize());
//  generateRoads(world, tq);
//  tq.addBlocks(blocks);  
//  
//  for(TownPartBlock block : blocks)
//    {
//    //TODO add special exterior gen pass
//    }
  }

private void generateStructures(World world, StructureTemplate townHall)
  {  
  List<TownPartBlock> blocks = new ArrayList<TownPartBlock>();
  for(TownPartQuadrant tq : this.quadrants)
    {
    tq.addBlocks(blocks);
    }
  sortBlocksByDistance(blocks);  
  if(townHall!=null)
    {
    generateTownHall(world, townHall);    
    }
  for(TownPartBlock block : blocks)//first pass, actual structures
    {
    generateStructures(world, block);
    if(templatesToGenerate.isEmpty()){break;}//have generated all structures, no reason in continuing anymore
    }
  for(TownPartBlock block : blocks)//second pass, cosmetic stuff
    {
    generateCosmetics(world, block);
    }
  TownStructureEntry e = template.getLamp();
  if(e!=null)
    {
    StructureTemplate lamp = StructureTemplateManager.instance().getTemplate(e.templateName);
    for(TownPartBlock block : blocks)
      {
      generateLamps(block, lamp);
      }    
    }
  }

private void generateTownHall(World world, StructureTemplate townHall)
  {
  int quadrantNumber = rng.nextInt(4);
  TownPartQuadrant tq = quadrants[quadrantNumber];//southeast quadrant 
  TownPartBlock block = null;
  TownPartPlot plot = null;  
  int bx = tq.getXDir()==Direction.EAST ? 0 : tq.xDivs - 1;
  int bz = tq.getZDir()==Direction.SOUTH ? 0 : tq.zDivs - 1;
  block = tq.getBlock(bx, bz);
  int px = tq.getXDir()==Direction.EAST? 0 : block.plotsWidth-1;
  int pz = tq.getZDir()==Direction.SOUTH? 0 : block.plotsLength-1;
  plot = block.getPlot(px, pz);
  generateForPlot(world, plot, townHall);
  }

/**
 * attempts to expand the input plot, merging it with neighbor plots
 * @param plot
 * @param x how many blocks to add in the x direction
 * @param z how many blocks to add in the z direction
 * @return
 */
private boolean expandPlot(TownPartPlot plot, int xSize, int zSize)
  {
  return plot.expand(xSize, zSize);  
  }

private void generateLamps(TownPartBlock block, StructureTemplate lamp)
  {
  int tx, tz;
  int wx, wz;
  int y = block.bb.min.y;
  for(tx = block.bb.min.x; tx<=block.bb.max.x; tx++)
    {    
    wx = tx;
    if(wx%4!=0){continue;}
    wz = block.bb.min.z;
    if(world.getBlock(wx, y, wz)==Blocks.air && world.getBlock(wx, y+1, wz)==Blocks.air)
      {
      generateLamp(wx, y, wz, lamp);
      }
    wz = block.bb.max.z;
    if(world.getBlock(wx, y, wz)==Blocks.air && world.getBlock(wx, y+1, wz)==Blocks.air)
      {
      generateLamp(wx, y, wz, lamp);
      }
    }
  for(tz = block.bb.min.z; tz<=block.bb.max.z; tz++)
    {   
    wz = tz ;
    if(wz%4!=0){continue;}
    wx = block.bb.min.x;
    if(world.getBlock(wx, y, wz)==Blocks.air && world.getBlock(wx, y+1, wz)==Blocks.air)
      {
      generateLamp(wx, y, wz, lamp);
      }
    wx = block.bb.max.x;
    if(world.getBlock(wx, y, wz)==Blocks.air && world.getBlock(wx, y+1, wz)==Blocks.air)
      {
      generateLamp(wx, y, wz, lamp);
      }
    }
  }

private void generateLamp(int x, int y, int z, StructureTemplate lamp)
  {
  new StructureBuilder(world, lamp, 0, x, y, z).instantConstruction();
  }

private void generateCosmetics(World world, TownPartBlock block)
  {
  int maxRetry = 1;//TODO base this off of townblock distance from center
  for(TownPartPlot plot : block.plots)
    {
    if(plot.closed){continue;}        
    for(int i = 0; i < maxRetry; i++)
      {
      if(this.template.getCosmeticEntries().isEmpty()){break;}
      if(generateCosmeticForPlot(world, plot, getRandomCosmeticTemplate())){break;}
      }
    }
  }

private void generateStructures(World world, TownPartBlock block)
  {  
  int maxRetry = 1;//TODO base this off of townblock distance from center
  for(TownPartPlot plot : block.plots)
    {
    if(plot.closed){continue;}
    if(!plot.hasRoadBorder()){continue;}//no borders
    if(templatesToGenerate.isEmpty()){break;}
    for(int i = 0; i < maxRetry; i++)
      {
      if(templatesToGenerate.isEmpty()){break;}
      if(generateForPlot(world, plot, getRandomTemplate())){break;}
      }
    }
  }

/**
 * attempt to generate a structure at the given plot
 * @param world
 * @param plot
 * @return true if generated
 */
private boolean generateCosmeticForPlot(World world, TownPartPlot plot, StructureTemplate template)
  {  
  int expansion = this.template.getTownBuildingWidthExpansion();
  int face = rng.nextInt(4);//select random face  
  int width = face==0 || face==2 ? template.xSize : template.zSize;
  int length = face==0 || face==2 ? template.zSize : template.xSize;
  width+=expansion;
  length+=expansion;
  if(plot.getWidth()<width || plot.getLength()<length)
    {
    if(!expandPlot(plot, width, length))
      {
      return false;
      }
    }  
  plot.markClosed();
  width-=expansion;
  length-=expansion;
  generateStructure(world, plot, template, face, width, length, true); 
  return true;
  }

/**
 * attempt to generate a structure at the given plot
 * @param world
 * @param plot
 * @return true if generated
 */
private boolean generateForPlot(World world, TownPartPlot plot, StructureTemplate template)
  {  
  int expansion = this.template.getTownBuildingWidthExpansion();
  int face = rng.nextInt(4);//select random face  
  for(int i = 0, f=face; i < 4; i++, f++)//and then iterate until a valid face is found
    {
    if(f>3){f=0;}
    if(plot.roadBorders[f])
      {
      face = f;
      break;
      }
    }
  face = (face+2)%4;//reverse face from road edge...
  int width = face==0 || face==2 ? template.xSize : template.zSize;
  int length = face==0 || face==2 ? template.zSize : template.xSize;
  if(face==0 || face==2){width+=expansion;}//temporarily expand the size of the bb by the town-template building expansion size, ensures there is room around buildings
  else{length+=expansion;}
  if(plot.getWidth()<width || plot.getLength()<length)
    {
    if(!expandPlot(plot, width, length))
      {
      return false;
      }
    }  
  plot.markClosed();
  if(face==0 || face==2){width-=expansion;}
  else{length-=expansion;}
  generateStructure(world, plot, template, face, width, length, false); 
  removeTemplate(template);//remove template from 'to generate' list
  return true;
  }

/**
 * pull a random template from the generation list, does not remove
 * @return
 */
private StructureTemplate getRandomTemplate()
  {
  if(templatesToGenerate.size()==0){return null;}
  int rng = this.rng.nextInt(this.templatesToGenerate.size());
  return templatesToGenerate.get(rng);
  }

/**
 * pull a random template from the cosmetic list, does not remove
 * @return
 */
private StructureTemplate getRandomCosmeticTemplate()
  {
  List<TownStructureEntry> cosmeticTemplatesToGenerate = this.template.getCosmeticEntries();
  if(cosmeticTemplatesToGenerate.size()==0){return null;}
  int rng = this.rng.nextInt(cosmeticTemplatesToGenerate.size());
  TownStructureEntry entry = cosmeticTemplatesToGenerate.get(rng);
  return entry==null ? null : StructureTemplateManager.instance().getTemplate(entry.templateName);
  }

/**
 * remove a template from the list after successfully generated
 * @param t
 */
private void removeTemplate(StructureTemplate t)
  {
  templatesToGenerate.remove(t);
  }

/**
 * 
 * @param world the world object that is currently being generated
 * @param plot the pre-expanded plot that will have the structure generated on it
 * @param template the template to be generated
 * @param face generation orientation for the structure
 * @param width rotated structure x-dimension
 * @param length rotated structure z-dimension
 * @param center should the structure be centered in plot, or placed along road-edge?
 */
private void generateStructure(World world, TownPartPlot plot, StructureTemplate template, int face, int width, int length, boolean center)
  {  
  int plotWidth = plot.getWidth();
  int plotLength = plot.getLength();
  int extraWidth = plotWidth - width;//unused width portion of the plot
  int extraLength = plotLength - length;//unused length portion of the plot
  
  int wAdj;
  int lAdj;
  
  if(center)
    {
    wAdj = extraWidth/2;    
    lAdj = extraLength/2;
    }
  else
    {
    wAdj = (face==0 || face==2) ? extraWidth/2 : face==1 ? extraWidth : 0;
    lAdj = (face==1 || face==3) ? extraLength/2 : face==2 ? extraLength : 0;
    }
    
  //find corners of the bb for the structure  
  BlockPosition min = new BlockPosition(plot.bb.min.x+wAdj, townBounds.min.y, plot.bb.min.z+lAdj);
  BlockPosition max = new BlockPosition(min.x + (width-1), template.ySize, min.z+(length-1));
  StructureBB bb = new StructureBB(min, max);
  
  BlockPosition buildKey = bb.getRLCorner(face, new BlockPosition());
  buildKey.moveRight(face, template.xOffset);
  buildKey.moveBack(face, template.zOffset);  
  buildKey.y -= template.yOffset;
  bb.offset(0, -template.yOffset, 0);
  StructureBuilder b = new StructureBuilder(world, template, face, buildKey, bb);
  b.instantConstruction();  
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
    if(tq.hasRoadBorder(Direction.NORTH)){genRoadTestBlock(world, x, tq.bb.min.y-1, tq.bb.min.z-1);}//north
    if(tq.hasRoadBorder(Direction.SOUTH)){genRoadTestBlock(world, x, tq.bb.min.y-1, tq.bb.max.z+1);}//south
    }
  int minZ = tq.bb.min.z;
  int maxZ = tq.bb.max.z;
  if(tq.hasRoadBorder(Direction.NORTH)){minZ--;}
  if(tq.hasRoadBorder(Direction.SOUTH)){maxZ++;}
  for(int z = minZ; z<=maxZ; z++)
    {
    if(tq.hasRoadBorder(Direction.WEST)){genRoadTestBlock(world, tq.bb.min.x-1, tq.bb.min.y-1, z);}//west
    if(tq.hasRoadBorder(Direction.EAST)){genRoadTestBlock(world, tq.bb.max.x+1, tq.bb.min.y-1, z);}//east
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
    if(tb.hasRoadBorder(Direction.NORTH)){genRoadTestBlock(world, x, tb.bb.min.y-1, tb.bb.min.z-1);}//north
    if(tb.hasRoadBorder(Direction.SOUTH)){genRoadTestBlock(world, x, tb.bb.min.y-1, tb.bb.max.z+1);}//south
    }
  int minZ = tb.bb.min.z;
  int maxZ = tb.bb.max.z;
  if(tb.hasRoadBorder(Direction.NORTH)){minZ--;}
  if(tb.hasRoadBorder(Direction.SOUTH)){maxZ++;}
  for(int z = minZ; z<=maxZ; z++)
    {
    if(tb.hasRoadBorder(Direction.WEST)){genRoadTestBlock(world, tb.bb.min.x-1, tb.bb.min.y-1, z);}//west
    if(tb.hasRoadBorder(Direction.EAST)){genRoadTestBlock(world, tb.bb.max.x+1, tb.bb.min.y-1, z);}//east
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
      genRoadTestBlock(world, x, y, z);
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
      genRoadTestBlock(world, x, y, z);
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
      genRoadTestBlock(world, x, y, z);
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
      genRoadTestBlock(world, x, y, z);
      }
    }
  }

private void genRoadTestBlock(World world, int x, int y, int z)
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

private void sortBlocksByDistance(List<TownPartBlock> blocks)
  {
  Collections.sort(blocks, new TownPartBlockComparator());
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
