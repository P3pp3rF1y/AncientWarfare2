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
package shadowmage.ancient_framework.common.utils;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public class EntityTools
{



public static Entity getEntityByUUID(World world, long msb, long lsb)
  {
  List<Entity> entities = world.loadedEntityList;
  Entity entity = null;
  if(entities!=null && !entities.isEmpty())
    {
    Iterator<Entity> it = entities.iterator();
    while(it.hasNext())
      {
      entity = it.next();
      UUID id = entity.getPersistentID();
      if(id!=null && id.getMostSignificantBits()==msb && id.getLeastSignificantBits()==lsb)
        {
        return entity;
        }
      }    
    }
  return entity;
  }


}
