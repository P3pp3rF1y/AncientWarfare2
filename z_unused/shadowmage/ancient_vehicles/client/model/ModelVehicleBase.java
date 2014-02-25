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
package shadowmage.ancient_vehicles.client.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.Entity;
import shadowmage.ancient_framework.common.utils.Trig;

public abstract class ModelVehicleBase extends ModelBase
{

HashMap<String, ModelRendererVehicle> boxMap = new HashMap<String, ModelRendererVehicle>();
List<ModelRendererVehicle> baseBoxes = new ArrayList<ModelRendererVehicle>();

@Override
public abstract void render(Entity par1Entity, float par2, float par3, float par4, float par5, float par6, float par7);

protected void addPiece(ModelRendererVehicle piece)
  {  
  boxMap.put(piece.boxName, piece);
  Iterator<ModelRendererVehicle> it = piece.childModels.iterator();
  ModelRendererVehicle child;
  while(it.hasNext() && (child=it.next())!=null)
    {
    addPiece(child);
    }
  }

protected void setPieceRotation(ModelRendererVehicle piece, float x, float y, float z)
  {
  piece.rotateAngleX = x * Trig.TORADIANS;
  piece.rotateAngleY = y * Trig.TORADIANS;
  piece.rotateAngleZ = z * Trig.TORADIANS;      
  }

public void setPieceRotation(String piece, float x, float y, float z)
  {
  ModelRendererVehicle rend = this.boxMap.get(piece);
  if(rend!=null)
    {
    setPieceRotation(rend, x, y, z);
    }
  }

public void renderModel()
  {
  /**
   * TODO find a way to make a list of base pieces -- those with no parent
   */
  }

public void parseFromFile(List<String> lines)
  {
  
  }

}
