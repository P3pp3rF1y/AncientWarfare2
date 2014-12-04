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
package net.shadowmage.ancientwarfare.structure.template.build;

import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplate;

public class StructureBB
{

public BlockPosition min;
public BlockPosition max;

public StructureBB(int minX, int minY, int minZ, int maxX, int maxY, int maxZ)
  {
  this(new BlockPosition(minX, minY, minZ), new BlockPosition(maxX, maxY, maxZ));
  }

public StructureBB(int x, int y, int z, int face, StructureTemplate template)
  {
  this(x, y, z, face, template.xSize, template.ySize, template.zSize, template.xOffset, template.yOffset, template.zOffset);
  }

public StructureBB(int x, int y, int z, int face, int xSize, int ySize, int zSize, int xOffset, int yOffset, int zOffset)
  {
  this.setFromStructure(x, y, z, face, xSize, ySize, zSize, xOffset, yOffset, zOffset);     
  }

public StructureBB(BlockPosition pos1, BlockPosition pos2)
  {
  this.min = BlockTools.getMin(pos1, pos2);
  this.max = BlockTools.getMax(pos1, pos2);
  }

public final StructureBB setFromStructure(int x, int y, int z, int face, int xSize, int ySize, int zSize, int xOffset, int yOffset, int zOffset)
  {   
  /**
   * we simply take the clicked on position
   */
  BlockPosition c1 = new BlockPosition(x,y,z);
  /**
   * and walk left/forward/down by the structure offsets
   */
  c1.moveLeft(face, xOffset);  
  c1.moveForward(face, zOffset);
  c1.y-=yOffset;    
  /**
   * the second corner starts as a copy of the first corner
   */
  BlockPosition c2 = c1.copy();
  /**
   * which then walks right, backwards, and up to arrive at the actual second corner
   */
  c2.moveRight(face, xSize-1);
  c2.moveForward(face, -(zSize -1));
  c2.y+=ySize-1;
  
  /**
   * finally, set the min/max of this BB to the min/max of the two corners
   */
  this.min = BlockTools.getMin(c1, c2);
  this.max = BlockTools.getMax(c1, c2); 
  return this;
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
    {
    return false;
    }  
  return true;
  }

/**
 * can be used to contract by specifying negative amounts...
 * @param amt
 */
public StructureBB expand(int x, int y, int z)
  {
  min.x-=x;
  min.y-=y;
  min.z-=z;
  max.x+=x;
  max.y+=y;
  max.z+=z;
  return this;
  }

public StructureBB offset(int x, int y, int z)
  {
  min.x += x;
  min.y += y;
  min.z += z;
  max.x += x;
  max.y += y;
  max.z += z;
  return this;
  }

public int getXSize(){return max.x-min.x+1;}

public int getZSize(){return max.z-min.z+1;}

public int getCenterX(){return min.x + (getXSize()/2);}

public int getCenterZ(){return min.z + (getZSize()/2);}

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

public boolean isPositionInBoundingBox(int x, int y, int z){return x>=min.x && x<=max.x && y>=min.y && y<=max.y && z>=min.z && z<=max.z;}
public boolean isPositionInBoundingBox(BlockPosition pos){return isPositionInBoundingBox(pos.x, pos.y, pos.z);}

public StructureBB copy()
  {
  return new StructureBB(min.copy(), max.copy());
  }

}
