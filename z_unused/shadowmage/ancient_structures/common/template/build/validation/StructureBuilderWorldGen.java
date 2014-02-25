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
package shadowmage.ancient_structures.common.template.build.validation;

import net.minecraft.world.World;
import shadowmage.ancient_structures.common.template.StructureTemplate;
import shadowmage.ancient_structures.common.template.build.StructureBuilder;

public class StructureBuilderWorldGen extends StructureBuilder
{

public StructureBuilderWorldGen(World world, StructureTemplate template, int face, int x, int y, int z)
  {
  super(world, template, face, x, y, z);
  }

protected void placeAir()
  {
  if(!template.getValidationSettings().isPreserveBlocks())
    {
    template.getValidationSettings().handleClearAction(world, destination.x, destination.y, destination.z, template, bb);    
    }
  }

public void instantConstruction()
  {
  template.getValidationSettings().preGeneration(world, buildOrigin.x, buildOrigin.y, buildOrigin.z, buildFace, template, bb);
  super.instantConstruction();
  }

}
