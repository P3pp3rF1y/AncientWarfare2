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

package net.shadowmage.ancientwarfare.vehicle.missiles;

import net.minecraft.entity.Entity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import shadowmage.ancient_warfare.common.item.ItemLoader;
import shadowmage.ancient_warfare.common.research.ResearchGoalNumbers;
import shadowmage.ancient_warfare.common.utils.ItemStackWrapperCrafting;

public class AmmoExplosiveShot extends Ammo {

	boolean bigExplosion;

	/**
	 * @param ammoType
	 */
	public AmmoExplosiveShot(int ammoType, int weight, boolean bigExplosion) {
		super(ammoType);
		this.ammoWeight = weight;
		this.bigExplosion = bigExplosion;
		this.entityDamage = weight;
		this.vehicleDamage = weight;
		float scaleFactor = weight + 45.f;
		this.renderScale = (weight / scaleFactor) * 2;

		if (bigExplosion) {
			this.iconTexture = "ammoHE1";
			this.configName = "high_explosive_" + weight;
			this.neededResearch.add(ResearchGoalNumbers.explosives3);
		} else {
			this.iconTexture = "ammoExplosive1";
			this.configName = "explosive_" + weight;
			this.neededResearch.add(ResearchGoalNumbers.explosives2);
		}
		this.modelTexture = new ResourceLocation(AncientWarfareCore.modID, "model/vehicle/ammo/ammoStoneShot");

		int cases = 1;
		int explosives = 1;
		this.numCrafted = 2;
		switch (weight) {
			case 10:
				this.neededResearch.add(ResearchGoalNumbers.ballistics1);
				cases = 1;
				explosives = 1;
				break;

			case 15:
				this.neededResearch.add(ResearchGoalNumbers.ballistics1);
				cases = 2;
				explosives = 2;
				break;

			case 30:
				this.neededResearch.add(ResearchGoalNumbers.ballistics2);
				cases = 4;
				explosives = 4;
				break;

			case 45:
				this.neededResearch.add(ResearchGoalNumbers.ballistics3);
				cases = 6;
				explosives = 6;
				break;
		}
		if (bigExplosion) {
			explosives *= 2;
		}

		this.resources.add(new ItemStackWrapperCrafting(ItemLoader.explosiveCharge, explosives, false, false));
		this.resources.add(new ItemStackWrapperCrafting(ItemLoader.clayCasing, cases, false, false));
	}

	@Override
	public void onImpactWorld(World world, float x, float y, float z, MissileBase missile, MovingObjectPosition hit) {
		if (!world.isRemote) {
			float maxPower = bigExplosion ? 7.f : 2.5f;
			float powerPercent = ammoWeight / 45.f;
			float power = maxPower * powerPercent;
			//    Config.logDebug("big: "+bigExplosion+" adj pwr: "+power+ "pwer percent: "+powerPercent);
			this.createExplosion(world, missile, x, y, z, power);
		}
	}

	@Override
	public void onImpactEntity(World world, Entity ent, float x, float y, float z, MissileBase missile) {
		if (!world.isRemote) {
			float maxPower = bigExplosion ? 2.5f : 7.f;
			float powerPercent = ammoWeight / 45.f;
			float power = maxPower * powerPercent;
			//    Config.logDebug("big: "+bigExplosion+" adj pwr: "+power+ "pwer percent: "+powerPercent);
			this.createExplosion(world, missile, x, y, z, power);
		}
	}

}
