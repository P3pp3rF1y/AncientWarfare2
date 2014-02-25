/**
   Copyright 2012 John Cummens (aka Shadowmage, Shadowmage4513)
   This software is distributed under the terms of the GNU General Public License.
   Please see COPYING for precise license information.

   This file is part of Ancient Warfare.

   Ancient Warfare is free software: you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation, either version 3 of the License, or
   (at your option) any later version.

   Ancient Warfare is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with Ancient Warfare.  If not, see <http://www.gnu.org/licenses/>.
 */
package shadowmage.ancient_structures.common.template.build;

import shadowmage.ancient_framework.common.utils.BlockPosition;
import shadowmage.ancient_framework.common.utils.BlockTools;
import shadowmage.ancient_structures.common.template.StructureTemplate;

public class StructureBB
{

public BlockPosition min;
public BlockPosition max;

public StructureBB(int x, int y, int z, int face, StructureTemplate template)
  {
  this(x, y, z, face, template.xSize, template.ySize, template.zSize, template.xOffset, template.yOffset, template.zOffset);
  }

public StructureBB(int x, int y, int z, int face, int xSize, int ySize, int zSize, int xOffset, int yOffset, int zOffset)
  {
  this.setFromStructure(x, y, z, face, xSize, ySize, zSize, xOffset, yOffset, zOffset);     
  }

public final StructureBB setFromStructure(int x, int y, int z, int face, int xSize, int ySize, int zSize, int xOffset, int yOffset, int zOffset)
  {
  int destXSize = xSize;
  int destZSize = zSize;
  
  int turns = ((face+2)%4);
  int swap;
  for(int i = 0; i<turns; i++)
    {
    swap = destXSize;
    destXSize = destZSize;
    destZSize = swap;
    }
    
  /**
   * here we take the back-left corner in template space
   */
  BlockPosition destinationKey = new BlockPosition(0, 0, 0);
  
  /**
   * and we rotate that corner into local space
   */
  BlockTools.rotateInArea(destinationKey, xSize, zSize, turns);
  
  /**
   * we are placing destination1 to be the back-let corner of the structure. offset it by the rotated corner to get the correct corner
   */
  BlockPosition destination1 = new BlockPosition(x-destinationKey.x, y-destinationKey.y, z-destinationKey.z);
  
  /**
   * next, offset the back-left corner by the structures build-key offsets
   */
  destination1.moveLeft(face, xOffset);
  destination1.moveForward(face, zOffset);
  destination1.y-=yOffset;
  
  /**
   * copy position to make the front-right corner.
   */
  BlockPosition destination2 = new BlockPosition(destination1);
  
  /**
   * offset this position directly by the size of the structure to get the actual front-right corner
   */
  destination2.offset(destXSize-1, ySize-1, destZSize-1);            
  
  /**
   * calculate structure bounding box min/max from destination 1 and destination 2
   */
  this.min = BlockTools.getMin(destination1, destination2);
  this.max = BlockTools.getMax(destination1, destination2);
  return this;
  }

public BlockPosition getPositionInTemplate(int x, int y, int z, int face)
  {
  BlockPosition pos = new BlockPosition(x-min.x, y-min.y, z-min.z);
  int turns = ( face + 2 ) % 4;
  BlockTools.rotateInArea(pos, this.getXSize(), this.getZSize(), turns);  
  return pos;
  }

public StructureBB(BlockPosition pos1, BlockPosition pos2)
  {
  this.min = BlockTools.getMin(pos1, pos2);
  this.max = BlockTools.getMax(pos1, pos2);
  }

@Override
public String toString()
  {
  return min.toString() + " : " +max.toString();
  }

/**
 * does the input bb share any blocks with this bounding box?
 * @param bb
 * @return
 */
public boolean collidesWith(StructureBB bb)
  {
  if(max.x < bb.min.x || max.y < bb.min.y || max.z < bb.min.z || min.x > bb.max.x || min.y > bb.max.y || min.z > bb.max.z)
    {//separation of axis...the early-out version of containment testing
    return false;
    }  
  return true;
  }

/**
 * can be used to contract by specifying negative amounts...
 * @param amt
 */
public void expand(int x, int y, int z)
  {
  min.x-=x;
  min.y-=y;
  min.z-=z;
  max.x+=x;
  max.y+=y;
  max.z+=z;
  }

public int getXSize()
  {
  return max.x-min.x+1;
  }

public int getZSize()
  {
  return max.z-min.z+1;
  }

public int getCenterX()
  {
  return min.x + (getXSize()/2);
  }

public int getCenterZ()
  {
  return min.z + (getZSize()/2);
  }

/**
 * 0-- z++==forward x++==left
 * 1-- x--==forward z++==left
 * 2-- z--==forward x--==left
 * 3-- x++==forward z--==left
 */

public void getFrontCorners(int face, BlockPosition min, BlockPosition max)
  {  
  getFLCorner(face, min);
  getFRCorner(face, max);
  int minX = Math.min(min.x, max.x);
  int maxX = Math.max(min.x, max.x);
  int minZ = Math.min(min.z, max.z);
  int maxZ = Math.max(min.z, max.z);
  min.x = minX;
  min.z = minZ;
  max.x = maxX;
  max.z = maxZ;
  }

public void getLeftCorners(int face, BlockPosition min, BlockPosition max)
  {
  getFLCorner(face, min);
  getRLCorner(face, max);
  int minX = Math.min(min.x, max.x);
  int maxX = Math.max(min.x, max.x);
  int minZ = Math.min(min.z, max.z);
  int maxZ = Math.max(min.z, max.z);
  min.x = minX;
  min.z = minZ;
  max.x = maxX;
  max.z = maxZ;
  }

public void getRearCorners(int face, BlockPosition min, BlockPosition max)
  {
  getRLCorner(face, min);
  getRRCorner(face, max);
  int minX = Math.min(min.x, max.x);
  int maxX = Math.max(min.x, max.x);
  int minZ = Math.min(min.z, max.z);
  int maxZ = Math.max(min.z, max.z);
  min.x = minX;
  min.z = minZ;
  max.x = maxX;
  max.z = maxZ;
  }

public void getRightCorners(int face, BlockPosition min, BlockPosition max)
  {
  getFRCorner(face, min);
  getRRCorner(face, max);
  int minX = Math.min(min.x, max.x);
  int maxX = Math.max(min.x, max.x);
  int minZ = Math.min(min.z, max.z);
  int maxZ = Math.max(min.z, max.z);
  min.x = minX;
  min.z = minZ;
  max.x = maxX;
  max.z = maxZ;
  }

public BlockPosition getFLCorner(int face, BlockPosition out)
  {
  switch(face)
  {
  case 0:
  return out.reassign(max.x, min.y, min.z);
  
  case 1:
  return out.reassign(max.x, min.y, max.z);
  
  case 2:
  return out.reassign(min.x, min.y, max.z);
  
  case 3:
  return out.reassign(min.x, min.y, min.z);  
  }
  return out;
  }

public BlockPosition getFRCorner(int face, BlockPosition out)
  {
  switch(face)
  {
  case 0:
  return out.reassign(min.x, min.y, min.z);
  
  case 1:
  return out.reassign(max.x, min.y, min.z);
  
  case 2:
  return out.reassign(max.x, min.y, max.z);
  
  case 3:
  return out.reassign(min.x, min.y, max.z);  
  }
  return out;
  }

public BlockPosition getRLCorner(int face, BlockPosition out)
  {
  switch(face)
  {
  case 0:
  return out.reassign(max.x, min.y, max.z);
  
  case 1:
  return out.reassign(min.x, min.y, max.z);
  
  case 2:
  return out.reassign(min.x, min.y, min.z);
  
  case 3:
  return out.reassign(max.x, min.y, min.z);  
  }
  return out;
  }

public BlockPosition getRRCorner(int face, BlockPosition out)
  {
  switch(face)
  {
  case 0:
  return out.reassign(min.x, min.y, max.z);
  
  case 1:
  return out.reassign(min.x, min.y, min.z);
  
  case 2:
  return out.reassign(max.x, min.y, min.z);
  
  case 3:
  return out.reassign(max.x, min.y, max.z);  
  }
  return out;
  }

public boolean isPositionInBoundingBox(int x, int y, int z)
  {
  return x>=min.x && x<=max.x && y>=min.y && y<=max.y && z>=min.z && z<=max.z;
  }

}
