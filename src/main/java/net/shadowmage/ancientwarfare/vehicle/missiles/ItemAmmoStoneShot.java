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
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;

public class ItemAmmoStoneShot extends ItemAmmo {

	public ItemAmmoStoneShot(int weight) {
		super("ammo_stone_shot_" + weight);
		this.isPersistent = false;
		this.isArrow = false;
		this.isRocket = false;
		this.ammoWeight = weight;
		//		this.iconTexture = "ammoStone1"; TODO rendering
		this.configName = "stone_shot_" + weight;
		this.entityDamage = weight;
		this.vehicleDamage = weight;
		float scaleFactor = weight + 45.f;
		this.renderScale = (weight / scaleFactor) * 2;
		this.modelTexture = new ResourceLocation(AncientWarfareCore.modID, "model/vehicle/ammo/ammoStoneShot");

/* TODO recipes
		int cases = 1;
		this.numCrafted = 10;
		switch (weight) {
			case 10:
				cases = 1;
				break;

			case 15:
				this.neededResearch.add(ResearchGoalNumbers.ballistics1);
				cases = 2;
				break;

			case 30:
				this.neededResearch.add(ResearchGoalNumbers.ballistics2);
				cases = 4;
				break;

			case 45:
				this.neededResearch.add(ResearchGoalNumbers.ballistics3);
				cases = 6;
				break;
		}

		this.resources.add(new ItemStackWrapperCrafting(ItemLoader.clayCasing, cases, false, false));
*/
	}

	@Override
	public void onImpactWorld(World world, float x, float y, float z, MissileBase missile, RayTraceResult hit) {
		if (ammoWeight >= 15 && !world.isRemote) {
			int bx = (int) x;
			int by = (int) y;
			int bz = (int) z;
			this.breakBlockAndDrop(world, bx, by, bz);
			if (ammoWeight >= 30) {
				this.breakBlockAndDrop(world, bx, by - 1, bz);
				this.breakBlockAndDrop(world, bx - 1, by, bz);
				this.breakBlockAndDrop(world, bx + 1, by, bz);
				this.breakBlockAndDrop(world, bx, by, bz - 1);
				this.breakBlockAndDrop(world, bx, by, bz + 1);
			}
			if (ammoWeight >= 45) {
				this.breakBlockAndDrop(world, bx - 1, by, bz - 1);
				this.breakBlockAndDrop(world, bx + 1, by, bz - 1);
				this.breakBlockAndDrop(world, bx - 1, by, bz + 1);
				this.breakBlockAndDrop(world, bx + 1, by, bz + 1);
			}
		}
	}

	@Override
	public void onImpactEntity(World world, Entity ent, float x, float y, float z, MissileBase missile) {
		if (!world.isRemote) {
			ent.attackEntityFrom(DamageType.causeEntityMissileDamage(missile.shooterLiving, false, false), this.getEntityDamage());
		}
	}

}
