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
import shadowmage.ancient_warfare.common.crafting.ResourceListRecipe;

public class AmmoSoldierArrow extends Ammo {

	public AmmoSoldierArrow(int ammoType, int damage, boolean flaming) {
		super(ammoType);
		this.ammoWeight = 1.f;
		this.renderScale = 0.125f;
		this.vehicleDamage = damage;
		this.entityDamage = damage;
		this.isArrow = true;
		this.isRocket = false;
		this.isPersistent = true;
		this.isFlaming = flaming;
		this.isCraftable = false;

		if (flaming) {
			this.iconTexture = "ammoArrowFlame1";
			this.configName = "soldier_arrow_flame_" + damage;
		} else {
			this.iconTexture = "ammoArrow1";
			this.configName = "soldier_arrow_" + damage;
		}
		if (damage <= 5) {
			this.modelTexture = Config.texturePath + "models/ammo/arrowWood.png";
		} else {
			this.modelTexture = Config.texturePath + "models/ammo/arrowIron.png";
		}
	}

	@Override
	public void onImpactWorld(World world, float x, float y, float z, MissileBase missile, MovingObjectPosition hit) {

	}

	@Override
	public void onImpactEntity(World world, Entity ent, float x, float y, float z, MissileBase missile) {
		if (!world.isRemote) {
			ent.attackEntityFrom(DamageType.causeEntityMissileDamage(missile.shooterLiving, isFlaming, false), this.getEntityDamage());
		}
	}

	@Override
	public ResourceListRecipe constructRecipe() {
		return null;
	}
}
