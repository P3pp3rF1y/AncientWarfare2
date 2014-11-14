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
import net.shadowmage.ancientwarfare.structure.template.build.StructureBB;
import net.shadowmage.ancientwarfare.structure.template.build.StructureBuilder;

public class TownPartCollection
{

private byte[] testGrid;

public final Random rng;
public final int blockSize;
public final int plotSize;
public final int width;
public final int length;

/**
 * the bounds of the town as it is -in the world-.
 * the minimum values for this BB will be used to offset internal town position when doing actual generation (roads/structs)
 */
public final StructureBB worldBounds;
public final TownBoundingArea boundingArea;

/**
 * 0=nw<br>
 * 1=ne<br>
 * 2=se<br>
 * 3=sw
 */
TownPartQuadrant[] quadrants = new TownPartQuadrant[4];
List<StructureTemplate> templatesToGenerate = new ArrayList<StructureTemplate>();

public TownPartCollection(TownBoundingArea area, int blockSize, int plotSize, Random rng)
  {
  this.boundingArea = area;
  this.rng = rng;
  this.blockSize = blockSize;
  this.plotSize = plotSize;
  this.worldBounds = new StructureBB(new BlockPosition(area.getTownMinX(), area.getSurfaceY()+1, area.getTownMinZ()), new BlockPosition(area.getTownMaxX(), area.getSurfaceY()+1, area.getTownMaxZ()));
  this.width = (worldBounds.max.x - worldBounds.min.x)+1;
  this.length = (worldBounds.max.z - worldBounds.min.z)+1;
  this.testGrid = new byte[width*length];
  }

public void addQuadrant(int index, TownPartQuadrant tq)
  {
  quadrants[index]=tq;
  }

/**
 * Splits up the town into quadrants<br>
 * quadrants into blocks<br>
 * and blocks into plots<br>
 */
public void generateGrid()
  {
  testGrid = new byte[width*length];
  int halfWidth = width/2;
  int halfLength = length/2;
  int centerX = halfWidth;
  int centerZ = halfLength;
  StructureBB bb;
  TownPartQuadrant tq;
  boolean[] roadBorders;
  
  //northwest quadrant, pre-shrunk for road borders
  roadBorders = new boolean[]{true, false, false, true};
  bb = new StructureBB(new BlockPosition(0, 0, 0), new BlockPosition(centerX-2, 0, centerZ-2));
  tq = new TownPartQuadrant(this, Direction.WEST, Direction.NORTH, bb, roadBorders);
  addQuadrant(0, tq);
  tq.subdivide();
  write(tq);
  
  //northeast quadrant
  roadBorders = new boolean[]{true, true, false, false};
  bb = new StructureBB(new BlockPosition(centerX+1, 0, 0), new BlockPosition(width-1, 0, centerZ-2));
  tq = new TownPartQuadrant(this, Direction.EAST, Direction.NORTH, bb, roadBorders);
  tq.subdivide();
  write(tq);
  addQuadrant(1, tq);
  
  //southeast quadrant
  roadBorders = new boolean[]{false, true, true, false};
  bb = new StructureBB(new BlockPosition(centerX+1, 0, centerZ+1), new BlockPosition(width-1, 0, length-1));
  tq = new TownPartQuadrant(this, Direction.EAST, Direction.SOUTH, bb, roadBorders);
  tq.subdivide();
  write(tq);
  addQuadrant(2, tq);
  
  //southwest quadrant
  roadBorders = new boolean[]{false, false, true, true};
  bb = new StructureBB(new BlockPosition(0, 0, centerZ+1), new BlockPosition(centerX-2, 0, length-1));
  tq = new TownPartQuadrant(this, Direction.WEST, Direction.SOUTH, bb, roadBorders);
  tq.subdivide();
  write(tq);
  addQuadrant(3, tq);
  
  write(centerX, centerZ, (byte)9);
  write(centerX-1, centerZ, (byte)9);
  write(centerX, centerZ-1, (byte)9);
  write(centerX-1, centerZ-1, (byte)9);
    
  String line = writeTestGrid();
  AWLog.logDebug("grid: \n"+line);
  }

public void generateStructures(World world, StructureTemplate townHall, List<StructureTemplate> toGenerate)
  {  
  this.templatesToGenerate.addAll(toGenerate);
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
  for(TownPartBlock block : blocks)
    {
    AWLog.logDebug("------------generating structures for block: "+block+" ----------------");
    generateStructures(world, block);
    if(templatesToGenerate.isEmpty()){break;}//have generated all structures, no reason in continuing anymore
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

private void generateStructures(World world, TownPartBlock block)
  {  
  int maxRetry = 4;//TODO base this off of townblock distance from center
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
private boolean generateForPlot(World world, TownPartPlot plot, StructureTemplate template)
  {  
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
  if(plot.getWidth()<width || plot.getLength()<length)
    {
    if(!expandPlot(plot, width, length))
      {
      AWLog.logDebug("Could not expand plot to generate structure: "+template.name);
      return false;
      }
    }  
  plot.markClosed();
  generateStructure(world, plot, template, face, width, length); 
//  removeTemplate(template);
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
 */
private void generateStructure(World world, TownPartPlot plot, StructureTemplate template, int face, int width, int length)
  {  
  int plotWidth = plot.getWidth();
  int plotLength = plot.getLength();
  int extraWidth = plotWidth - width;//unused width portion of the plot
  int extraLength = plotLength - length;//unused length portion of the plot
  
  int wAdj = (face==0 || face==2) ? extraWidth/2 : face==1 ? extraWidth : 0;
  int lAdj = (face==1 || face==3) ? extraLength/2 : face==2 ? extraLength : 0;
  
  //find corners of the bb for the structure  
  BlockPosition min = new BlockPosition(plot.bb.min.x+wAdj, 0, plot.bb.min.z+lAdj);
  BlockPosition max = new BlockPosition(min.x + (width-1), 0, min.z+(length-1));
  StructureBB bb = new StructureBB(min, max);
  
  BlockPosition buildKey = bb.getRLCorner(face, new BlockPosition());
  buildKey.moveRight(face, template.xOffset);
  buildKey.moveBack(face, template.zOffset);
  
  BlockPosition offset = worldBounds.min;
  buildKey.offset(offset.x, offset.y, offset.z);
  bb.offset(offset.x, offset.y, offset.z);
  StructureBuilder b = new StructureBuilder(world, template, face, buildKey, bb);
  b.instantConstruction();  
  }

public void generateRoads(World world)
  {
  for(TownPartQuadrant tq : quadrants){genRoadsTest(world, tq);}
  }

private void genRoadsTest(World world, TownPartQuadrant tq)
  {
  Block block = Blocks.cobblestone;
  int minX = tq.bb.min.x;
  int maxX = tq.bb.max.x;
  if(tq.hasRoadBorder(Direction.WEST)){minX--;}
  if(tq.hasRoadBorder(Direction.EAST)){maxX++;}
  for(int x = minX; x<=maxX; x++)
    {
    if(tq.hasRoadBorder(Direction.NORTH)){genRoadTestBlock(world, x, tq.bb.min.z-1, block);}//north
    if(tq.hasRoadBorder(Direction.SOUTH)){genRoadTestBlock(world, x, tq.bb.max.z+1, block);}//south
    }
  int minZ = tq.bb.min.z;
  int maxZ = tq.bb.max.z;
  if(tq.hasRoadBorder(Direction.NORTH)){minZ--;}
  if(tq.hasRoadBorder(Direction.SOUTH)){maxZ++;}
  for(int z = minZ; z<=maxZ; z++)
    {
    if(tq.hasRoadBorder(Direction.WEST)){genRoadTestBlock(world, tq.bb.min.x-1, z, block);}//west
    if(tq.hasRoadBorder(Direction.EAST)){genRoadTestBlock(world, tq.bb.max.x+1, z, block);}//east
    }
  for(TownPartBlock tb : tq.blocks)
    {
    genRoadsTest(world, tb);
    }
  }

private void genRoadsTest(World world, TownPartBlock tb)
  {
  Block block = Blocks.cobblestone;
  int minX = tb.bb.min.x;
  int maxX = tb.bb.max.x;
  if(tb.hasRoadBorder(Direction.WEST)){minX--;}
  if(tb.hasRoadBorder(Direction.EAST)){maxX++;}
  for(int x = minX; x<=maxX; x++)
    {
    if(tb.hasRoadBorder(Direction.NORTH)){genRoadTestBlock(world, x, tb.bb.min.z-1, block);}//north
    if(tb.hasRoadBorder(Direction.SOUTH)){genRoadTestBlock(world, x, tb.bb.max.z+1, block);}//south
    }
  int minZ = tb.bb.min.z;
  int maxZ = tb.bb.max.z;
  if(tb.hasRoadBorder(Direction.NORTH)){minZ--;}
  if(tb.hasRoadBorder(Direction.SOUTH)){maxZ++;}
  for(int z = minZ; z<=maxZ; z++)
    {
    if(tb.hasRoadBorder(Direction.WEST)){genRoadTestBlock(world, tb.bb.min.x-1, z, block);}//west
    if(tb.hasRoadBorder(Direction.EAST)){genRoadTestBlock(world, tb.bb.max.x+1, z, block);}//east
    }
  for(TownPartPlot tp : tb.plots){genRoadsTest(world, tp);}
  }

private void genRoadsTest(World world, TownPartPlot tp)
  {
  Block block = Blocks.netherrack;
  for(int x = tp.bb.min.x; x<=tp.bb.max.x; x++)
    {
    genRoadTestBlock(world, x, tp.bb.min.z, block);
    genRoadTestBlock(world, x, tp.bb.max.z, block);
    }
  for(int z = tp.bb.min.z; z<=tp.bb.max.z; z++)
    {
    genRoadTestBlock(world, tp.bb.min.x, z, block);
    genRoadTestBlock(world, tp.bb.max.x, z, block);
    }
  }

private void genRoadTestBlock(World world, int x, int z, Block block)
  {
  x+=worldBounds.min.x;
  int y = worldBounds.min.y-1;
  z+=worldBounds.min.z;
  world.setBlock(x, y, z, block);
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
  testGrid[getIndex(x, z)]=val;
  }

private int getIndex(int x, int z)
  {
  return z*width + x;
  }

private String writeTestGrid()
  {
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
