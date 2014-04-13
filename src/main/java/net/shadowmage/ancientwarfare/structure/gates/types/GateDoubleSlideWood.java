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
package net.shadowmage.ancientwarfare.structure.gates.types;

import net.minecraft.block.Block;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.structure.entity.EntityGate;

public class GateDoubleSlideWood extends Gate
{

/**
 * @param id
 */
public GateDoubleSlideWood(int id, String tex)
  {
  super(id, tex); 
  this.displayName = "item.gate.8";
  this.tooltip = "item.gate.8.tooltip";
//  this.texture = "gateWood1.png";
  this.iconTexture = "gateWoodDouble";
  }

@Override
public void setInitialBounds(EntityGate gate, BlockPosition pos1,   BlockPosition pos2)
  {
  BlockPosition min = BlockTools.getMin(pos1, pos2);
  BlockPosition max = BlockTools.getMax(pos1, pos2);
  boolean wideOnXAxis = min.x!=max.x;
  float width = wideOnXAxis ? max.x-min.x+1 : max.z-min.z + 1;
  float xOffset = wideOnXAxis ? width*0.5f: 0.5f;
  float zOffset = wideOnXAxis ? 0.5f : width*0.5f;
  gate.pos1 = pos1;
  gate.pos2 = pos2;
  gate.edgeMax = width * 0.5f;
  gate.setPosition(min.x+xOffset, min.y, min.z+zOffset);  
  }


}
