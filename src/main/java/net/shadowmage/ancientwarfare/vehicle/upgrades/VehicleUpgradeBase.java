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

package net.shadowmage.ancientwarfare.vehicle.upgrades;

import net.minecraft.util.ResourceLocation;
import net.shadowmage.ancientwarfare.vehicle.AncientWarfareVehicles;

public abstract class VehicleUpgradeBase implements IVehicleUpgradeType {

	private ResourceLocation registryName;
	/*
		List<ItemStackWrapperCrafting> resources = new ArrayList<ItemStackWrapperCrafting>();
		HashSet<Integer> neededResearch = new HashSet<Integer>();
	*/

	public VehicleUpgradeBase(String name) {
		registryName = new ResourceLocation(AncientWarfareVehicles.modID, name);
	}

	@Override
	public ResourceLocation getRegistryName() {
		return registryName;
	}

/* TODO recipe
	@Override
	public ResourceListRecipe constructRecipe() {
		ResourceListRecipe recipe = new ResourceListRecipe(getUpgradeStack(1), RecipeType.VEHICLE_MISC);
		recipe.addNeededResearch(getNeededResearch());
		if (!this.resources.isEmpty()) {
			recipe.addResources(resources);
		} else {
			recipe.addResource(new ItemStack(Item.paper), 1, false, false);
		}
		return recipe;
	}

	@Override
	public Collection<Integer> getNeededResearch() {
		return this.neededResearch;
	}
*/

}
