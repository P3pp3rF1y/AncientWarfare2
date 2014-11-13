package net.shadowmage.ancientwarfare.structure.town;

import java.util.ArrayList;
import java.util.List;

import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.structure.template.build.StructureBB;

public class TownPartQuadrant
{
TownPartCollection town;
int xDir;
int zDir;
StructureBB bb;
List<TownPartBlock> blocks;
int blockSize;
boolean roadBorders[];

public TownPartQuadrant(TownPartCollection town, Direction xDir, Direction zDir, int x, int z, int width, int length, int blockSize)
  {  
  this.town = town;
  this.xDir = xDir.xDirection;
  this.zDir = zDir.zDirection;
  roadBorders = new boolean[4];
  
  roadBorders[0] = this.zDir < 0;//has southern road border if generating northward
  roadBorders[2] = !roadBorders[0];//opposite of the above statement
  
  roadBorders[3] = this.xDir < 0;//has eastern border if generating westward
  roadBorders[1] = !roadBorders[1];//opposite of the above statement
  
  BlockPosition startPos = new BlockPosition(x, 0, z);
  BlockPosition endPos = startPos.copy();
  endPos.x += xDir.xDirection * (width-1);
  endPos.z += zDir.zDirection * (length-1);
  
  this.blockSize = blockSize;
  bb = new StructureBB(startPos, endPos);
  blocks = new ArrayList<TownPartBlock>();
  }

public void subdivide()
  {
  int widthToUse = (bb.max.x - bb.min.x);
  int lengthToUse = (bb.max.z - bb.min.z);
  int xDivs = widthToUse/blockSize;
  if(widthToUse%blockSize!=0){xDivs++;}
  int zDivs = lengthToUse/blockSize;
  if(lengthToUse%blockSize!=0){zDivs++;}
    
  int xStart, xEnd;
  int zStart, zEnd;
  int xSize, zSize;
  boolean roadBorders[];

  xStart = xDir < 0 ? bb.max.x-1 : bb.min.x+1;  
  for(int x = 0; x<xDivs; x++)
    {    
    xSize = widthToUse > blockSize ? blockSize : widthToUse;
    xEnd = xStart + xDir * (xSize - 1); 

    zStart = zDir<0 ? bb.max.z-1 : bb.min.z+1;
    lengthToUse = (bb.max.z - bb.min.z);
    for(int z = 0; z<zDivs; z++)
      {
      roadBorders = new boolean[4];
      roadBorders[2] = zDir>0 || (zDir<0 && z < zDivs-1);//has road on north side if generation direction is south or is not the last block in that direciton
      roadBorders[0] = zDir<0 || (zDir>0 && z < zDivs-1);//has road on south side if generation direction is north or is not the last block in that direction
      roadBorders[3] = xDir<0 || (xDir>0 && x < xDivs-1);//has road on east side if generation direction is west or is not the last block in that direction
      roadBorders[1] = xDir>0 || (xDir<0 && x < xDivs-1);//has road on west side if generation direction is east or is not the last block in that direction      
      
      zSize = lengthToUse > blockSize ? blockSize : lengthToUse;
      zEnd = zStart + zDir * (zSize - 1);
      
      blocks.add(new TownPartBlock(this, new StructureBB(new BlockPosition(xStart, 0, zStart), new BlockPosition(xEnd, 0, zEnd)), roadBorders));
      
      lengthToUse -= blockSize;
      zStart = zEnd + zDir;
      }
    
    widthToUse -= blockSize;
    xStart = xEnd + xDir;
    }
  for(TownPartBlock block : blocks){block.subdivide();}
  }

public void addBlocks(List<TownPartBlock> blocks)
  {
  blocks.addAll(this.blocks);
  }

}
