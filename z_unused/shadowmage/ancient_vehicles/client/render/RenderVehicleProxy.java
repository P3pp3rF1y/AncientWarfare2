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
package shadowmage.ancient_vehicles.client.render;

import java.util.HashMap;

import net.minecraft.client.renderer.entity.RenderEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import shadowmage.ancient_vehicles.client.model.ModelVehicleBase;
import shadowmage.ancient_vehicles.common.vehicle.EntityVehicle;
import shadowmage.ancient_vehicles.common.vehicle.VehicleType;

public class RenderVehicleProxy extends RenderEntity
{

//vehicle renderers by vehicleType.name
private static HashMap<String, RenderVehicle> vehicleRenders = new HashMap<String, RenderVehicle>();
//vehicle models by vehicleType.name
private static HashMap<String, ModelVehicleBase> vehicleModels = new HashMap<String, ModelVehicleBase>();
//vehicle textures by vehicleType.name
private static HashMap<String, ResourceLocation> vehicleTextures = new HashMap<String, ResourceLocation>();

public static void registerRenderers()
  {
  /**
   * load vehicle renderers and models
   * textures are registered when vehicleTypes are registered
   */
  }

public static void registerVehicleType(VehicleType type)
  {
  vehicleTextures.put(type.getName(), new ResourceLocation("ancientwarfare", type.getTextureName()));
  //load model file from disk
  }

@Override
public void doRender(Entity entity, double x, double y, double z, float yaw, float partialTick)
  {
  EntityVehicle vehicle = (EntityVehicle)entity;
  RenderVehicle render = vehicleRenders.get(vehicle.getVehicleType().getName());
  if(render!=null)
    {
    /**
     * translate to position
     * rotate for yaw
     * bind texture
     */
    render.renderVehicle(vehicle, partialTick);
    render.renderVehicleFlag(vehicle, partialTick);    
    }
  else
    {
    super.doRender(entity, x, y, z, yaw, partialTick);
    }
  
  }

public static ModelVehicleBase getModel(String name)
  {
  return vehicleModels.get(name);
  }

@Override
protected ResourceLocation getEntityTexture(Entity entity)
  {
  return entity instanceof EntityVehicle ? vehicleTextures.get(((EntityVehicle)entity).getVehicleType().getName()) : null;
  }


}
