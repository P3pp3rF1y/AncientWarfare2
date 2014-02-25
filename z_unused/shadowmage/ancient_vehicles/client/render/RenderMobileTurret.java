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

import shadowmage.ancient_vehicles.client.model.ModelVehicleBase;
import shadowmage.ancient_vehicles.common.vehicle.EntityVehicle;
import shadowmage.ancient_vehicles.common.vehicle.VehicleStats;

public class RenderMobileTurret extends RenderVehicle
{


public RenderMobileTurret()
  {
  
  }

@Override
public void renderVehicle(EntityVehicle vehicle, float partialTick)
  {
  ModelVehicleBase model = RenderVehicleProxy.getModel(vehicle.getVehicleType().getName());  
  VehicleStats stats = vehicle.getVehicleStats();
  model.setPieceRotation("frontAxle", stats.wheelAngle + partialTick * stats.wheelSpeed, 0, 0);
  model.setPieceRotation("rearAxle", stats.wheelAngle + partialTick * stats.wheelSpeed, 0, 0);
  model.setPieceRotation("turretYawAxle", 0, stats.turretYaw + partialTick * stats.turretYawSpeed, 0);
  model.setPieceRotation("turretPitchAxle", stats.turretPitch + partialTick * stats.turretPitchSpeed, 0, 0);
  model.renderModel();  
  }

}
