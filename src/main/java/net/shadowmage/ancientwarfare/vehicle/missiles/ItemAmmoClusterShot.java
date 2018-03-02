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

public class ItemAmmoClusterShot extends ItemAmmo {

	public ItemAmmoClusterShot(int weight) {
		super("ammo_cluster_shot_" + weight);
		this.ammoWeight = weight;
		float scaleFactor = weight + 45.f;
		this.renderScale = (weight / scaleFactor) * 2;
/* TODO rendering
		this.iconTexture = "ammoCluster1";
*/
		this.configName = "cluster_shot_" + weight;
		this.modelTexture = new ResourceLocation(AncientWarfareCore.modID, "model/vehicle/ammo/ammoStoneShot");

		this.entityDamage = 5;
		this.vehicleDamage = 5;

/* TODO recipes
		this.neededResearch.add(ResearchGoalNumbers.explosives1);
		this.numCrafted = 4;
		switch (weight) {
			case 10:
				this.neededResearch.add(ResearchGoalNumbers.ballistics1);
				this.resources.add(new ItemStackWrapperCrafting(ItemLoader.explosiveCharge, 1, false, false));
				this.resources.add(new ItemStackWrapperCrafting(ItemLoader.clusterCharge, 2, false, false));
				this.resources.add(new ItemStackWrapperCrafting(ItemLoader.clayCasing, 2, false, false));
				break;

			case 15:
				this.neededResearch.add(ResearchGoalNumbers.ballistics1);
				this.resources.add(new ItemStackWrapperCrafting(ItemLoader.explosiveCharge, 1, false, false));
				this.resources.add(new ItemStackWrapperCrafting(ItemLoader.clusterCharge, 3, false, false));
				this.resources.add(new ItemStackWrapperCrafting(ItemLoader.clayCasing, 3, false, false));
				break;

			case 30:
				this.neededResearch.add(ResearchGoalNumbers.ballistics2);
				this.resources.add(new ItemStackWrapperCrafting(ItemLoader.explosiveCharge, 2, false, false));
				this.resources.add(new ItemStackWrapperCrafting(ItemLoader.clusterCharge, 6, false, false));
				this.resources.add(new ItemStackWrapperCrafting(ItemLoader.clayCasing, 6, false, false));
				break;

			case 45:
				this.neededResearch.add(ResearchGoalNumbers.ballistics3);
				this.resources.add(new ItemStackWrapperCrafting(ItemLoader.explosiveCharge, 3, false, false));
				this.resources.add(new ItemStackWrapperCrafting(ItemLoader.clusterCharge, 9, false, false));
				this.resources.add(new ItemStackWrapperCrafting(ItemLoader.clayCasing, 9, false, false));
				break;
		}
*/
	}

	@Override
	public void onImpactWorld(World world, float x, float y, float z, MissileBase missile, RayTraceResult hit) {
		if (!world.isRemote) {
			double px = hit.hitVec.x - missile.motionX;
			double py = hit.hitVec.y - missile.motionY;
			double pz = hit.hitVec.z - missile.motionZ;
			spawnGroundBurst(world, (float) px, (float) py, (float) pz, 10, ammoBallShot, (int) ammoWeight, 35, hit.sideHit, missile.shooterLiving);
		}
	}

	@Override
	public void onImpactEntity(World world, Entity ent, float x, float y, float z, MissileBase missile) {
		if (!world.isRemote) {
			spawnAirBurst(world, (float) ent.posX, (float) ent.posY + ent.height, (float) ent.posZ, 10, ammoBallShot, (int) ammoWeight, missile.shooterLiving);
		}
	}

}
