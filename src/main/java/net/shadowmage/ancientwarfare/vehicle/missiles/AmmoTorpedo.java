/**
 * Copyright 2012-2013 John Cummens (aka Shadowmage, Shadowmage4513)
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

public class AmmoTorpedo extends Ammo {

	public AmmoTorpedo(int weight) {
		super("ammo_torpedo_" + weight);
		this.isEnabled = false;
		this.isPersistent = false;
		this.isArrow = true;
		this.isRocket = false;
		this.isTorpedo = true;
		this.ammoWeight = weight;
		this.configName = "torpedo_" + weight;
		this.entityDamage = weight * 2;
		this.vehicleDamage = weight * 2;
		float scaleFactor = weight + 45.f;
		this.renderScale = (weight / scaleFactor) * 2;
		this.modelTexture = new ResourceLocation(AncientWarfareCore.modID, "textures/model/vehicle/ammo/ammo_stone_shot.png");
	}

	@Override
	public void onImpactWorld(World world, float x, float y, float z, MissileBase missile, RayTraceResult hit) {
		if (!world.isRemote) {
			float maxPower = 7.f;
			float powerPercent = ammoWeight / 45.f;
			float power = maxPower * powerPercent;
			this.createExplosion(world, missile, x, y, z, power);
		}
	}

	@Override
	public void onImpactEntity(World world, Entity ent, float x, float y, float z, MissileBase missile) {
		if (!world.isRemote) {
			float maxPower = 7.f;
			float powerPercent = ammoWeight / 45.f;
			float power = maxPower * powerPercent;
			this.createExplosion(world, missile, x, y, z, power);
		}
	}

}
