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

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import shadowmage.ancient_warfare.common.research.ResearchGoalNumbers;

public class VehicleArmorObsidian extends VehicleArmorBase {

	/**
	 * @param armorType
	 */
	public VehicleArmorObsidian(int armorType) {
		super(armorType);
		this.displayName = "item.vehicleArmor.obsidian";
		this.tooltip = "item.vehicleArmor.obsidian.tooltip";
		this.general = 2.5f;
		this.explosive = 7.f;
		this.fire = 2.5f;
		this.iconTexture = "armorObsidian1";
		this.neededResearch.add(ResearchGoalNumbers.iron3);
		this.addNeededResource(new ItemStack(Block.obsidian, 5), false);
	}

}
