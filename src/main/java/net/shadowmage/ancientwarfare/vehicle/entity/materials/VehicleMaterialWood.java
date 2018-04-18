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

package net.shadowmage.ancientwarfare.vehicle.entity.materials;

public class VehicleMaterialWood extends VehicleMaterial {
	public VehicleMaterialWood() {
		super(5);
		MaterialLevel level0 = new MaterialLevel();
		MaterialLevel level1 = new MaterialLevel();
		MaterialLevel level2 = new MaterialLevel();
		MaterialLevel level3 = new MaterialLevel();
		MaterialLevel level4 = new MaterialLevel();
		this.materialLevels[0] = level0;
		this.materialLevels[1] = level1;
		this.materialLevels[2] = level2;
		this.materialLevels[3] = level3;
		this.materialLevels[4] = level4;
		level0.accuracyFactor = 0.85f;
		level1.accuracyFactor = 0.9f;
		level2.accuracyFactor = 0.93f;
		level3.accuracyFactor = 0.96f;
		level4.accuracyFactor = 1.f;
		level0.hpFactor = 0.75f;
		level1.hpFactor = 1.f;
		level2.hpFactor = 1.2f;
		level3.hpFactor = 1.4f;
		level4.hpFactor = 2.f;
		level0.misfireChance = 0.1f;
		level1.misfireChance = 0.06f;
		level2.misfireChance = 0.03f;
		level3.misfireChance = 0.01f;
		level4.misfireChance = 0.002f;
		level0.speedForwardFactor = 0.85f;
		level1.speedForwardFactor = 1.f;
		level2.speedForwardFactor = 1.15f;
		level3.speedForwardFactor = 1.05f;
		level4.speedForwardFactor = 0.95f;
		level0.speedStrafeFactor = 0.85f;
		level1.speedStrafeFactor = 1.f;
		level2.speedStrafeFactor = 1.15f;
		level3.speedStrafeFactor = 1.05f;
		level4.speedStrafeFactor = 0.95f;
		level0.weightFactor = 1.25f;
		level1.weightFactor = 1.f;
		level2.weightFactor = 1.1f;
		level3.weightFactor = 1.3f;
		level4.weightFactor = 1.7f;
		level0.displayName = "Rough Wood";
		level1.displayName = "Treated Wood";
		level2.displayName = "Ironshod Wood";
		level3.displayName = "Iron Core Wood";
		level4.displayName = "Iron Substitute";
	}
}
