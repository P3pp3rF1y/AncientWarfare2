package net.shadowmage.ancientwarfare.structure.town;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplate;
import net.shadowmage.ancientwarfare.structure.template.build.StructureBB;
import net.shadowmage.ancientwarfare.structure.template.build.StructureBuilder;

public class TownPartCollection
{

private Random rng;
private byte[] testGrid;
private int blockSize=32;
int width;
int length;
private Direction orientation;

/**
 * the bounds of the town as it is -in the world-.
 * the minimum values for this BB will be used to offset internal town position when doing actual generation (roads/structs)
 */
StructureBB worldBounds;
TownBoundingArea boundingArea;

/**
 * 0=nw<br>
 * 1=ne<br>
 * 2=se<br>
 * 3=sw
 */
TownPartQuadrant[] quadrants = new TownPartQuadrant[4];

public TownPartCollection(TownBoundingArea area, int blockSize, int plotSize, Random rng)
  {
  this.boundingArea = area;
  this.rng = rng;
  this.blockSize = blockSize;
  this.worldBounds = new StructureBB(new BlockPosition(area.getTownMinX(), area.getSurfaceY()+1, area.getTownMinZ()), new BlockPosition(area.getTownMaxX(), area.getSurfaceY()+1, area.getTownMaxZ()));
  this.width = (worldBounds.max.x - worldBounds.min.x)+1;
  this.length = (worldBounds.max.z - worldBounds.min.z)+1;
  this.testGrid = new byte[width*length];
  this.orientation = Direction.fromFacing(area.townOrientation);
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
  
  TownPartQuadrant tq = new TownPartQuadrant(this, Direction.WEST, Direction.NORTH, centerX-1, centerZ-1, halfWidth, halfLength, blockSize);
  tq.subdivide();
  write(tq);
  addQuadrant(0, tq);
  
  tq = new TownPartQuadrant(this, Direction.EAST, Direction.NORTH, centerX+0, centerZ-1, halfWidth, halfLength, blockSize);
  tq.subdivide();
  write(tq);
  addQuadrant(1, tq);
  
  tq = new TownPartQuadrant(this, Direction.EAST, Direction.SOUTH, centerX+0, centerZ+0, halfWidth, halfLength, blockSize);
  tq.subdivide();
  write(tq);
  addQuadrant(2, tq);
  
  tq = new TownPartQuadrant(this, Direction.WEST, Direction.SOUTH, centerX-1, centerZ+0, halfWidth, halfLength, blockSize);
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
  List<TownPartBlock> blocks = new ArrayList<TownPartBlock>();
  for(TownPartQuadrant tq : this.quadrants)
    {
    tq.addBlocks(blocks);
    }
  sortBlocksByDistance(blocks);  
  if(townHall!=null)
    {
    generateTownHall(world, blocks.get(0), townHall);    
    }
  for(TownPartBlock block : blocks)
    {
    generateStructures(world, block, toGenerate);
    if(toGenerate.isEmpty()){break;}//have generated all structures, no reason in continuing anymore
    }
  }

private void generateTownHall(World world, TownPartBlock block, StructureTemplate townHall)
  {
  int face = 2;//north
  if(block.quadrant.xDir<0){face = 1;}//west
  else if(block.quadrant.zDir<0){face = 0;}//south
  int pwid = block.plotsA.length;//width of plots array
  int plen = block.plotsA[0].length;//length of plots array  
  int px = face==1 ? pwid-1 : 0;//west
  int pz = face==2 ? plen-1 : 0;//north  
  int width = face==0 || face==2 ? townHall.xSize : townHall.zSize;
  int length = face==0 || face==2 ? townHall.zSize : townHall.xSize;  
  //grab initial plot, it should be one of the corners of the
  TownPartPlot plot = block.plotsA[px][pz];
  if(plot.getWidth()<width || plot.getLength()<length)
    {
    int w = width-plot.getWidth();
    int l = length - plot.getLength();
    if(!expandPlot(plot, w, l))
      {
      AWLog.logDebug("Could not expand plot to generate structure: "+townHall.name);
      return;
      }
    }
  generateStructure(world, plot, townHall, face, width, length);  
  }


/**
 * attempts to expand the input plot, merging it with neighbor plots
 * @param plot
 * @param x how many blocks to add in the x direction
 * @param z how many blocks to add in the z direction
 * @return
 */
private boolean expandPlot(TownPartPlot plot, int x, int z)
  {
  /**
   * check all possible combinations of plot expansion
   */
  int xIndexStart = plot.x;
  int zIndexStart = plot.z;
  
  int xStart = xIndexStart;
  int zStart = zIndexStart;
  int xEnd = xStart;
  int zEnd = zStart;
  
  
  
  
  return false;
  }

private void generateStructures(World world, TownPartBlock block, List<StructureTemplate> templates)
  {
  
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
  int lAdj = (face==1 || face==3) ? extraLength/2 : face==0 ? extraLength : 0;
  
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
  AWLog.logDebug("building structure at: "+bb);
  StructureBuilder b = new StructureBuilder(world, template, face, buildKey, bb);
  b.instantConstruction();  
  }

public void generateRoads(World world)
  {
  for(TownPartQuadrant tq : quadrants){genRoadsTest(world, tq);}
  }

private void genRoadsTest(World world, TownPartQuadrant tq)
  {
  for(int x = tq.bb.min.x; x<=tq.bb.max.x; x++)
    {
    if(tq.roadBorders[2]){genRoadTestBlock(world, x, tq.bb.min.z);}//north
    if(tq.roadBorders[0]){genRoadTestBlock(world, x, tq.bb.max.z);}//south
    }
  for(int z = tq.bb.min.z; z<=tq.bb.max.z; z++)
    {
    if(tq.roadBorders[1]){genRoadTestBlock(world, tq.bb.min.x, z);}//west
    if(tq.roadBorders[3]){genRoadTestBlock(world, tq.bb.max.x, z);}//east
    }
  for(TownPartBlock tb : tq.blocks)
    {
    genRoadsTest(world, tb);
    }
  }

private void genRoadsTest(World world, TownPartBlock tb)
  {
  for(int x = tb.bb.min.x; x<=tb.bb.max.x; x++)
    {
    if(tb.roadBorders[2]){genRoadTestBlock(world, x, tb.bb.min.z);}//north
    if(tb.roadBorders[0]){genRoadTestBlock(world, x, tb.bb.max.z);}//south
    }
  for(int z = tb.bb.min.z; z<=tb.bb.max.z; z++)
    {
    if(tb.roadBorders[1]){genRoadTestBlock(world, tb.bb.min.x, z);}//west
    if(tb.roadBorders[3]){genRoadTestBlock(world, tb.bb.max.x, z);}//east
    }
  for(TownPartPlot tp : tb.plots){genRoadsTest(world, tp);}
  }

private void genRoadsTest(World world, TownPartPlot tp)
  {
  
  }

private void genRoadTestBlock(World world, int x, int z)
  {
  x+=worldBounds.min.x;
  int y = worldBounds.min.y-1;
  z+=worldBounds.min.z;
  world.setBlock(x, y, z, Blocks.cobblestone);
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

private boolean doesBBIntersect(StructureBB bb)
  {
  for(int x = bb.min.x; x<=bb.max.x; x++)
    {
    for(int z = bb.min.z; z<=bb.max.z; z++)
      {
      if(testGrid[getIndex(x, z)]!=0)
        {
        return true;
        }
      }
    }
  return false;
  }

/**
 * 
 * @param x
 * @param z
 * @param genWidth
 * @param genLength
 * @param type
 * @param grid
 * @param gridWidth
 */
private void genBuilding(int x, int z, int genWidth, int genLength, byte type)
  {
  for(int px = x; px < x + genWidth; px++)
    {
    for(int pz = z; pz < z + genLength; pz++)
      {
      write(px, pz, type);
      }
    }
  }

private void write(StructureBB bb, byte type)
  {
  for(int x = bb.min.x; x<=bb.max.x; x++)
    {
    for(int z = bb.min.z; z<=bb.max.z; z++)
      {
      write(x, z, type);
      }
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
