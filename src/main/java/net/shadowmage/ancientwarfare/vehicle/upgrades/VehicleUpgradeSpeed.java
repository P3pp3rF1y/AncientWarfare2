/**
 * Copyright 2012 John Cummens (aka Shadowmage, Shadowmage4513)
 * This software is distributed under the terms of the GNU General Public License.
 * Please see COPYING for precise license information.
 * <p>
 * This file is part of Ancient Warfare.
 * <p>
 * Ancient Warfare is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * Ancient Warfare is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with Ancient Warfare.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.shadowmage.ancientwarfare.vehicle.upgrades;

import net.shadowmage.ancientwarfare.vehicle.entity.VehicleBase;

public class VehicleUpgradeSpeed extends VehicleUpgradeBase {

	/**
	 * @param num
	 */
	public VehicleUpgradeSpeed(int num) {
		super(num);
		this.displayName = "item.vehicleUpgrade.speed";
		this.tooltip = "item.vehicleUpgrade.speed.tooltip1";
		this.iconTexture = "upgradeSpeed1";
/* TODO recipe
		this.neededResearch.add(ResearchGoalNumbers.mobility3);
		this.resources.add(new ItemStackWrapperCrafting(new ItemStack(Block.planks, 3), true, false));
		this.resources.add(new ItemStackWrapperCrafting(new ItemStack(Item.ingotIron, 3), false, false));
*/
	}

	@Override
	public void applyVehicleEffects(VehicleBase vehicle) {
		//  Config.logDebug("prev vehicle max speed: "+vehicle.currentForwardSpeedMax);
		vehicle.currentForwardSpeedMax += 0.25f * 0.05f;
		//  Config.logDebug("new vehicle max speed: "+vehicle.currentForwardSpeedMax);
	}

}
