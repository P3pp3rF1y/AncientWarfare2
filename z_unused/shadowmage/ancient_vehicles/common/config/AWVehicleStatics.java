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
package shadowmage.ancient_vehicles.common.config;

import java.io.File;
import java.util.logging.Logger;

import shadowmage.ancient_framework.common.config.ModConfiguration;
import shadowmage.ancient_framework.common.config.Statics;

public class AWVehicleStatics extends ModConfiguration
{

public static final String vehicleDefinitionsFile = Statics.ASSETS_PATH +"/definitions/vehicle.csv";
public static final String vehicleTooltipsFile = Statics.ASSETS_PATH +"/definitions/vehicletooltips.csv";
public static final String vehicleResearchFile = Statics.ASSETS_PATH +"/definitions/vehicleresearch.csv";
public static final String vehicleUpgradeFile = Statics.ASSETS_PATH +"/definitions/vehicleupgrades.csv";
public static final String vehicleAmmoFile = Statics.ASSETS_PATH +"/definitions/vehicleammos.csv";

public AWVehicleStatics(File configFile, Logger log, String version)
  {
  super(configFile, log, version);
  }

@Override
public void initializeCategories()
  {

  }

@Override
public void initializeValues()
  {

  }

}
