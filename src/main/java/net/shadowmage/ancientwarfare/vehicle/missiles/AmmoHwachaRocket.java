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
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import shadowmage.ancient_warfare.common.item.ItemLoader;
import shadowmage.ancient_warfare.common.research.ResearchGoalNumbers;
import shadowmage.ancient_warfare.common.utils.ItemStackWrapperCrafting;

public class AmmoHwachaRocket extends Ammo {

	public static float burnTimeFactor = 3.f;
	public static float accelerationFactor = 0.01f;

	/**
	 * @param ammoType
	 */
	public AmmoHwachaRocket(int ammoType) {
		super(ammoType);
		this.entityDamage = 6;
		this.vehicleDamage = 6;
		this.isArrow = true;
		this.isPersistent = true;
		this.isRocket = true;
		this.ammoWeight = 1.f;
		this.renderScale = 0.2f;
		this.configName = "hwacha_rocket";
		this.iconTexture = "ammoRocket1";
		this.modelTexture = new ResourceLocation(AncientWarfareCore.modID, "model/vehicle/ammo/arrowWood");

		this.numCrafted = 12;
		this.neededResearch.add(ResearchGoalNumbers.rockets1);
		this.neededResearch.add(ResearchGoalNumbers.ballistics1);
		this.resources.add(new ItemStackWrapperCrafting(ItemLoader.rocketCharge, 1, false, false));
		this.resources.add(new ItemStackWrapperCrafting(new ItemStack(Item.stick), 12, false, false));
		this.resources.add(new ItemStackWrapperCrafting(new ItemStack(Item.feather), 2, false, false));
		this.resources.add(new ItemStackWrapperCrafting(new ItemStack(Item.ingotIron), 1, false, false));
	}

	@Override
	public void onImpactWorld(World world, float x, float y, float z, MissileBase missile, MovingObjectPosition hit) {

	}

	@Override
	public void onImpactEntity(World world, Entity ent, float x, float y, float z, MissileBase missile) {
		if (!world.isRemote) {
			ent.attackEntityFrom(DamageType.causeEntityMissileDamage(missile.shooterLiving, false, false), this.getEntityDamage());
		}
	}

}
