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

public class ItemAmmoBallShot extends ItemAmmo {

	public ItemAmmoBallShot() {
		super("ammo_ball_shot");
		this.renderScale = 0.15f;
		this.ammoWeight = 1.f;
		this.entityDamage = 5;
		this.vehicleDamage = 5;
		this.isPersistent = false;
		this.configName = "ball_shot";
/* TODO rendering
		this.iconTexture = "ammoStone1";
*/
		this.isCraftable = false;
		this.modelTexture = new ResourceLocation(AncientWarfareCore.modID, "model/vehicle/ammo/ammoStoneShot");
	}

	@Override
	public void onImpactWorld(World world, float x, float y, float z, MissileBase missile, RayTraceResult hit) {
		//NOOP
	}

	@Override
	public void onImpactEntity(World world, Entity ent, float x, float y, float z, MissileBase missile) {
		if (!world.isRemote) {
			ent.attackEntityFrom(DamageType.causeEntityMissileDamage(missile.shooterLiving, false, false), this.getEntityDamage());
		}
	}

/* TODO recipe - add a new one?
	@Override
	public ResourceListRecipe constructRecipe() {
		return null;
	}
*/

}
