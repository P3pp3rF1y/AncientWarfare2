/**
   Copyright 2012-2013 John Cummens (aka Shadowmage, Shadowmage4513)
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

import net.minecraft.world.World;
import shadowmage.ancient_framework.common.utils.BlockPosition;
import shadowmage.ancient_framework.common.utils.BlockTools;
import shadowmage.ancient_structures.common.template.StructureTemplate;
import shadowmage.ancient_structures.common.template.rule.TemplateRule;
import shadowmage.ancient_structures.common.template.rule.TemplateRuleEntity;

public class StructureBuilder
{

protected StructureTemplate template;
protected World world;
protected BlockPosition buildOrigin;
protected int buildFace;
protected int turns;
protected int maxPriority = 4;
protected int currentPriority;//current build priority...may not be needed anymore?
protected int currentX, currentY, currentZ;//coords in template
protected int destXSize, destYSize, destZSize;
protected BlockPosition destination;

protected StructureBB bb;

protected boolean isFinished = false;

public StructureBuilder(World world, StructureTemplate template, int face, int x, int y, int z)
  {
  this.world = world;
  this.template = template;
  this.buildFace = face;
  buildOrigin = new BlockPosition(x,y,z);
  destination = new BlockPosition();   
  currentX = currentY = currentZ = 0;
  destXSize = template.xSize;
  destYSize = template.ySize;
  destZSize = template.zSize;
  currentPriority = 0;
  
  turns = ((face+2)%4);
  int swap;
  for(int i = 0; i<turns; i++)
    {
    swap = destXSize;
    destXSize = destZSize;
    destZSize = swap;
    }
  
  bb = new StructureBB(x, y, z, face, template);  
  /**
   * initialize the first target destination so that the structure is ready to start building when called on to build
   */
  incrementDestination();
  }

public void instantConstruction()
  {
  while(!this.isFinished)
    {
    this.placeCurrentPosition();
    }
  this.placeEntities();
  }

protected void placeEntities()
  {   
  TemplateRuleEntity[] rules = template.getEntityRules();
  for(TemplateRuleEntity rule : rules)
    {
    if(rule==null){continue;}
    destination.x = rule.x;
    destination.y = rule.y;
    destination.z = rule.z;
    BlockTools.rotateInArea(destination, destXSize, destZSize, turns);
    destination.offsetBy(bb.min);
    rule.handlePlacement(world, turns, destination.x, destination.y, destination.z);
    }
  }

protected void placeCurrentPosition()
  {
  TemplateRule rule = template.getRuleAt(currentX, currentY, currentZ);
  if(rule!=null)
    {
    placeRule(rule);
    }
  else
    {
    placeAir();
    }
  if(incrementPosition())
    {
    incrementDestination();
    }
  else
    {
    this.isFinished = true;
    }
  }

protected void placeAir()
  {
  world.setBlockToAir(destination.x, destination.y, destination.z);  
  }

protected void placeRule(TemplateRule rule)
  {  
  if(rule.shouldPlaceOnBuildPass(world, turns, destination.x, destination.y, destination.z, currentPriority))
    {
    rule.handlePlacement(world, turns, destination.x, destination.y, destination.z);    
    }
  }

protected void incrementDestination()
  {
  destination.reassign(currentX, currentY, currentZ);
  BlockTools.rotateInArea(destination, template.xSize, template.zSize, turns);
  destination.offsetBy(bb.min);
  }

/**
 * return true if could increment position
 * return false if template is finished
 * @return
 */
protected boolean incrementPosition()
  {
  currentX++;
  if(currentX>=template.xSize)
    {
    currentX = 0;
    currentZ++;
    if(currentZ>=template.zSize)
      {
      currentZ = 0;
      currentY++;
      if(currentY>=template.ySize)
        {
        currentY = 0;
        currentPriority++;
        if(currentPriority>maxPriority)
          {
          currentPriority = 0;
          return false;
          }
        }
      }
    }
  return true;
  }

}
