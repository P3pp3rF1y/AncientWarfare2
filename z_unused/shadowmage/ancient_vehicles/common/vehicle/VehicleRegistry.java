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
package shadowmage.ancient_vehicles.common.vehicle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.minecraft.item.ItemStack;
import shadowmage.ancient_framework.common.item.AWItemBase;
import shadowmage.ancient_vehicles.AWVehicles;
import shadowmage.ancient_vehicles.common.config.AWVehicleStatics;

public class VehicleRegistry
{

private static HashMap<String, Class <? extends VehicleFiringHelper>> firingHelpers = new HashMap<String, Class<? extends VehicleFiringHelper>>();
private static HashMap<String, Object> moveTypes = new HashMap<String, Object>();

public static void loadVehicles()
  {
  registerVehicleHelperTypes();
  List<VehicleType> types = loadFromDefinitions(AWVehicleStatics.vehicleDefinitionsFile, AWVehicleStatics.vehicleTooltipsFile, AWVehicleStatics.vehicleResearchFile, AWVehicleStatics.vehicleUpgradeFile, AWVehicleStatics.vehicleAmmoFile);
  AWVehicles.instance.config.log("loaded: "+types.size() + " vehicle definitions");
  }

private static void registerVehicleHelperTypes()
  {
  /**
   * TODO register vehicle helpers -- firing and move
   * TODO figure out move-types
   */
  }

public static void registerVehicleItemData(AWItemBase item)
  {
  item.addIcon(0, "");//TODO make default vehicle icon
  for(VehicleType t : VehicleType.vehicleTypesByName.values())
    {
    if(t.isSuvivalEnabled() || t.isCreativeEnabled())
      {
      item.addDisplayName(t.getId(), t.name);
      item.addDisplayStack(t.getId(), new ItemStack(item, 1, t.getId()));
      item.addTooltip(t.getId(), t.tooltips);
      }
    if(t.isSuvivalEnabled())
      {
      //register recipes and research
      /**
       * TODO research and crafting systems
       */  
      }
    }
  }

public static Class<? extends VehicleFiringHelper> getFiringHelperClass(String name)
  {
  return firingHelpers.get(name);
  }

public static Object getMoveType(String name)
  {
  return moveTypes.get(name);
  }

private static List<VehicleType> loadFromDefinitions(String definitions, String tooltips, String research, String upgrades, String ammos)
  {
  List<VehicleType> types = new ArrayList<VehicleType>();
  try
    {
    
    String[] lineBits;
    VehicleType type;
    List<String> lines = getLinesFrom(definitions);
    for(String line : lines)
      {
      lineBits = line.split(",", -1);
      type = VehicleType.parseFromCSV(lineBits);
      if(type!=null)
        {
        types.add(type);
        }
      }
    
    lines = getLinesFrom(tooltips);
    for(String line : lines)
      {
      lineBits = line.split(",", -1);
      type = VehicleType.getVehicleType(lineBits[0]);
      if(type==null){continue;}
      type.parseTooltips(lineBits);
      }
    
    lines = getLinesFrom(research);
    for(String line : lines)
      {
      lineBits = line.split(",", -1);
      type = VehicleType.getVehicleType(lineBits[0]);
      if(type==null){continue;}
      type.parseResearch(lineBits);
      }
    
    lines = getLinesFrom(upgrades);
    for(String line : lines)
      {
      lineBits = line.split(",", -1);
      type = VehicleType.getVehicleType(lineBits[0]);
      if(type==null){continue;}
      type.parseUpgrades(lineBits);
      }
    
    lines = getLinesFrom(ammos);
    for(String line : lines)
      {
      lineBits = line.split(",", -1);
      type = VehicleType.getVehicleType(lineBits[0]);
      if(type==null){continue;}
      type.parseAmmoTypes(lineBits);
      }
    } 
  catch (IOException e)
    {  
    e.printStackTrace();
    }
  return types;
  }

private static List<String> getLinesFrom(String resourcePath) throws IOException
  {
  List<String> lines = new ArrayList<String>();
  InputStream is = AWVehicles.instance.getClass().getResourceAsStream(resourcePath);
  BufferedReader reader = new BufferedReader(new InputStreamReader(is));
  String line;
  while((line = reader.readLine())!=null)
    {
    if(line.startsWith("#")){continue;}
    lines.add(line);
    }
  reader.close();
  is.close();
  return lines;
  }

}
