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
import shadowmage.ancient_warfare.common.config.Config;
import shadowmage.ancient_warfare.common.item.ItemLoader;
import shadowmage.ancient_warfare.common.research.ResearchGoalNumbers;
import shadowmage.ancient_warfare.common.utils.ItemStackWrapperCrafting;

public class AmmoFlameShot extends Ammo {

	/**
	 * @param ammoType
	 * @param weight
	 */
	public AmmoFlameShot(int ammoType, int weight) {
		super(ammoType);
		this.isPersistent = false;
		this.isArrow = false;
		this.isRocket = false;
		this.isFlaming = true;
		this.ammoWeight = weight;
		float scaleFactor = weight + 45.f;
		this.renderScale = (weight / scaleFactor) * 2;
		this.iconTexture = "ammoFlame1";
		this.configName = "flame_shot_" + weight;
		this.vehicleDamage = 8;
		this.entityDamage = 8;
		this.modelTexture = Config.texturePath + "models/ammo/ammoStoneShot.png";

		this.neededResearch.add(ResearchGoalNumbers.flammables2);
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

		this.resources.add(new ItemStackWrapperCrafting(ItemLoader.flameCharge, explosives, false, false));
		this.resources.add(new ItemStackWrapperCrafting(ItemLoader.clayCasing, cases, false, false));
	}

	@Override
	public void onImpactWorld(World world, float x, float y, float z, MissileBase missile, MovingObjectPosition hit) {
		if (!world.isRemote) {
			int bx = (int) x;
			int by = (int) y + 2;
			int bz = (int) z;
			this.igniteBlock(world, bx, by, bz, 5);
			if (this.ammoWeight >= 15) {
				this.igniteBlock(world, bx - 1, by, bz, 5);
				this.igniteBlock(world, bx + 1, by, bz, 5);
				this.igniteBlock(world, bx, by, bz - 1, 5);
				this.igniteBlock(world, bx, by, bz + 1, 5);
			}
			if (ammoWeight >= 30) {
				this.igniteBlock(world, bx - 1, by, bz - 1, 5);
				this.igniteBlock(world, bx - 1, by, bz + 1, 5);
				this.igniteBlock(world, bx + 1, by, bz - 1, 5);
				this.igniteBlock(world, bx + 1, by, bz + 1, 5);
				this.igniteBlock(world, bx - 2, by, bz, 5);
				this.igniteBlock(world, bx + 2, by, bz, 5);
				this.igniteBlock(world, bx, by, bz - 2, 5);
				this.igniteBlock(world, bx, by, bz + 2, 5);
			}
			if (ammoWeight >= 45) {
				this.igniteBlock(world, bx - 1, by, bz - 2, 5);
				this.igniteBlock(world, bx + 1, by, bz - 2, 5);
				this.igniteBlock(world, bx - 1, by, bz + 2, 5);
				this.igniteBlock(world, bx + 1, by, bz + 2, 5);
				this.igniteBlock(world, bx - 2, by, bz - 1, 5);
				this.igniteBlock(world, bx - 2, by, bz + 1, 5);
				this.igniteBlock(world, bx + 2, by, bz - 1, 5);
				this.igniteBlock(world, bx + 2, by, bz + 1, 5);
			}
		}
	}

	@Override
	public void onImpactEntity(World world, Entity ent, float x, float y, float z, MissileBase missile) {
		if (!world.isRemote) {
			ent.attackEntityFrom(DamageType.causeEntityMissileDamage(missile.shooterLiving, true, false), this.getEntityDamage());
			ent.setFire(3);
			onImpactWorld(world, x, y, z, missile, null);
		}
	}

}
