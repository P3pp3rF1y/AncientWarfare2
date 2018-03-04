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
import net.shadowmage.ancientwarfare.vehicle.registry.AmmoRegistry;

public class AmmoHwachaRocketAirburst extends Ammo {

	public AmmoHwachaRocketAirburst() {
		super("ammo_hwacha_rocket_airburst");
		this.entityDamage = 0;
		this.vehicleDamage = 0;
		this.isArrow = true;
		this.isPersistent = false;
		this.isRocket = true;
		this.isProximityAmmo = true;
		this.groundProximity = 12.f;
		this.entityProximity = 10f;
		this.ammoWeight = 1.4f;
		this.renderScale = 0.2f;
		this.configName = "hwacha_rocket_airburst";
		//		this.iconTexture = "ammoRocketAirburst1"; TODO rendering
		this.modelTexture = new ResourceLocation(AncientWarfareCore.modID, "model/vehicle/ammo/arrowWood");

/* TODO recipe
		this.numCrafted = 6;
		this.neededResearch.add(ResearchGoalNumbers.rockets3);
		this.neededResearch.add(ResearchGoalNumbers.ballistics3);
		this.neededResearch.add(ResearchGoalNumbers.explosives2);
		this.resources.add(new ItemStackWrapperCrafting(ItemLoader.rocketCharge, 1, false, false));
		this.resources.add(new ItemStackWrapperCrafting(new ItemStack(Item.stick), 6, false, false));
		this.resources.add(new ItemStackWrapperCrafting(new ItemStack(Item.feather), 1, false, false));
		this.resources.add(new ItemStackWrapperCrafting(new ItemStack(Item.ingotIron), 1, false, false));
		this.resources.add(new ItemStackWrapperCrafting(ItemLoader.clusterCharge, 1, false, false));
		this.resources.add(new ItemStackWrapperCrafting(ItemLoader.explosiveCharge, 1, false, false));
*/
	}

	@Override
	public void onImpactWorld(World world, float x, float y, float z, MissileBase missile, RayTraceResult hit) {
		if (!world.isRemote) {
			this.spawnAirBurst(world, x, y, z, 10, AmmoRegistry.ammoBallShot, 4, missile.shooterLiving);
			missile.setDead();
		}
	}

	@Override
	public void onImpactEntity(World world, Entity ent, float x, float y, float z, MissileBase missile) {
		if (!world.isRemote) {
			this.spawnAirBurst(world, x, y, z, 10, AmmoRegistry.ammoBallShot, 4, missile.shooterLiving);
			missile.setDead();
		}
	}

}
