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
import net.minecraft.item.Item;
import net.minecraft.util.RayTraceResult;
import net.minecraft.world.World;
import shadowmage.ancient_warfare.common.utils.ItemStackWrapperCrafting;

public class AmmoBallistaBolt extends Ammo {

	/**
	 * @param ammoType
	 */
	public AmmoBallistaBolt(int ammoType) {
		super(ammoType);
		this.ammoWeight = 2.f;
		this.renderScale = 0.3f;
		this.vehicleDamage = 18;
		this.entityDamage = 18;
		this.isArrow = true;
		this.isRocket = false;
		this.isPersistent = true;
		this.iconTexture = "ammoBolt1";
		this.configName = "ballist_bolt";
		this.modelTexture = new ResourceLocation(AncientWarfareCore.modID, "model/vehicle/ammo/arrowWood");

		this.resources.add(new ItemStackWrapperCrafting(Item.stick, 5));
		this.resources.add(new ItemStackWrapperCrafting(Item.ingotIron, 2));
		this.resources.add(new ItemStackWrapperCrafting(Item.feather, 5));
		this.numCrafted = 8;
	}

	@Override
	public void onImpactWorld(World world, float x, float y, float z, MissileBase missile, RayTraceResult hit) {

	}

	@Override
	public void onImpactEntity(World world, Entity ent, float x, float y, float z, MissileBase missile) {
		if (!world.isRemote) {
			ent.attackEntityFrom(DamageType.causeEntityMissileDamage(missile.shooterLiving, false, false), this.getEntityDamage());
		}
	}

}
