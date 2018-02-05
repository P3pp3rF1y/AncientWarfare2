/*
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

package net.shadowmage.ancientwarfare.vehicle.armors;

public class VehicleArmorStone extends VehicleArmorBase {

	public VehicleArmorStone() {
		super(ArmorType.STONE);
		this.displayName = "item.vehicleArmor.stone";
		this.tooltip = "item.vehicleArmor.stone.tooltip";
		this.general = 2.5f;
		this.explosive = 2.5f;
		this.fire = 7;
	}

/* TODO research recipe
  this.neededResearch.add(ResearchGoalNumbers.iron3);
  this.addNeededResource(new ItemStack(Blocks.STONE, 3), false);
  this.addNeededResource(new ItemStack(Items.IRON_INGOT, 2), false);
*/

}
