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

import net.minecraft.item.ItemStack;
import net.shadowmage.ancientwarfare.vehicle.item.AWVehicleItems;

import java.util.HashSet;
import java.util.Set;

public abstract class VehicleArmorBase implements IVehicleArmor {

	ArmorType armorType;
	String displayName = "";
	String tooltip = "";
	float general = 0.f;
	float explosive = 0.f;
	float fire = 0.f;
	float weight = 50.f;
	Set<Integer> neededResearch = new HashSet<>();

	public VehicleArmorBase(ArmorType armorType) {
		this.armorType = armorType;
	}

	@Override
	public ArmorType getArmorType() {
		return this.armorType;
	}

	@Override
	public String getDisplayName() {
		return displayName;
	}

	@Override
	public String getDisplayTooltip() {
		return tooltip;
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
	public ItemStack getArmorStack(int qty) {
		return new ItemStack(AWVehicleItems.armor, qty, armorType.ordinal());
	}

/* TODO armor rendering
	@Override
	public String getIconTexture() {
		return "ancientwarfare:armor/" + iconTexture;
	}
*/

/* TODO research recipes
	protected void addNeededResearch(Integer res) {
		this.neededResearch.add(res);
	}

	protected void addNeededResource(ItemStack stack, boolean dmg) {
		this.neededResources.add(new ItemStackWrapperCrafting(stack, dmg, false));
	}
*/

	@Override
	public Set<Integer> getNeededResearch() {
		return this.neededResearch;
	}

}
