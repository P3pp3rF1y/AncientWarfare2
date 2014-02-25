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
package shadowmage.ancient_vehicles.common.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import shadowmage.ancient_framework.common.item.AWItemBase;
import shadowmage.ancient_vehicles.AWVehicles;

public class AWVehiclesItemLoader
{

private AWVehiclesItemLoader(){}
private static AWVehiclesItemLoader instance = new AWVehiclesItemLoader();
public static AWVehiclesItemLoader instance(){return instance;}

public static CreativeTabs vehiclesTab = new CreativeTabs("Ancient Vehicles")
{
@Override
public Item getTabIconItem()
  {  
  return vehicleSpawner;
  }
};//need to declare this instance prior to any items that use it as their creative tab


public static final AWItemBase vehicleSpawner = new ItemVehicleSpawner(AWVehicles.instance.config.getConfig(), "item.vehiclespawner");//.createItem("item.vehicleSpawner", ItemVehicleSpawner.class);


public void registerItems()
  {  
  //registry.addDescription(vehicleSpawner, "item.vehicleSpawner", 0, "item.vehicleSpawner.tooltip", "ancientwarfare:testIcon1");
  }

}
