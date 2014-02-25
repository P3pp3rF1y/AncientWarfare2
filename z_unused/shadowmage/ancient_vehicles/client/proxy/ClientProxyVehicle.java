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
package shadowmage.ancient_vehicles.client.proxy;

import shadowmage.ancient_framework.client.proxy.ClientProxyBase;
import shadowmage.ancient_vehicles.client.render.RenderVehicleProxy;
import shadowmage.ancient_vehicles.common.vehicle.EntityVehicle;
import shadowmage.ancient_vehicles.common.vehicle.VehicleType;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class ClientProxyVehicle extends ClientProxyBase
{

/**
 * 
 */
public ClientProxyVehicle()
  {
  // TODO Auto-generated constructor stub
  }

@Override
public void registerGuis()
  {
  // TODO Auto-generated method stub

  }

@Override
public void registerKeybinds()
  {
  // TODO Auto-generated method stub

  }

@Override
public void registerTickHandlers()
  {
  // TODO Auto-generated method stub

  }

@Override
public void registerRenderers()
  {
  RenderVehicleProxy.registerRenderers();
  for(VehicleType type : VehicleType.vehicleTypesByName.values())
    {
    if(type.isSuvivalEnabled() || type.isCreativeEnabled())
      {
      RenderVehicleProxy.registerVehicleType(type);
      }
    } 
  RenderingRegistry.registerEntityRenderingHandler(EntityVehicle.class, new RenderVehicleProxy());
  }

@Override
public void registerEventHandlers()
  {
  // TODO Auto-generated method stub

  }

}
