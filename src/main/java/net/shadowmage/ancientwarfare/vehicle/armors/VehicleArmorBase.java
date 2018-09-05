/*
   Copyright 2012 John Cummens (aka Shadowmage, Shadowmage4513)
   This software is distributed under the terms of the GNU General Public License.
   Please see COPYING for precise license information.

   This file is part of Ancient Warfare.

   Ancient Warfare is free software: you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation, either version 3 of the License, or
   (at your option) any later version.

   Ancient Warfare is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with Ancient Warfare.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.shadowmage.ancientwarfare.vehicle.armors;

import net.minecraft.util.ResourceLocation;
import net.shadowmage.ancientwarfare.vehicle.AncientWarfareVehicles;

import java.util.HashSet;
import java.util.Set;

public abstract class VehicleArmorBase implements IVehicleArmor {

	float general = 0.f;
	float explosive = 0.f;
	float fire = 0.f;
	float weight = 50.f;
	Set<Integer> neededResearch = new HashSet<>();
	private ResourceLocation registryName;

	public VehicleArmorBase(String regName) {
		registryName = new ResourceLocation(AncientWarfareVehicles.MOD_ID, regName);
	}

	@Override
	public ResourceLocation getRegistryName() {
		return registryName;
	}

	@Override
	public float getGeneralDamageReduction() {
		return general;
	}

	@Override
	public float getExplosiveDamageReduction() {
		return explosive;
	}

	@Override
	public float getFireDamageReduction() {
		return fire;
	}

	@Override
	public float getArmorWeight() {
		return weight;
	}

	@Override
	public Set<Integer> getNeededResearch() {
		return this.neededResearch;
	}

}
