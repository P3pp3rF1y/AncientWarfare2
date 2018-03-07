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

import net.minecraft.item.ItemStack;
import net.shadowmage.ancientwarfare.vehicle.item.AWVehicleItems;

public abstract class VehicleUpgradeBase implements IVehicleUpgradeType {

	int typeNum = 0;
	String displayName = "";
	String tooltip = "";
	String iconTexture = "foo";
	/*
		List<ItemStackWrapperCrafting> resources = new ArrayList<ItemStackWrapperCrafting>();
		HashSet<Integer> neededResearch = new HashSet<Integer>();
	*/

	public VehicleUpgradeBase(int num) {
		this.typeNum = num;
	}

	@Override
	public int getUpgradeId() {
		return typeNum;
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
	public ItemStack getUpgradeStack(int qty) {
		return new ItemStack(AWVehicleItems.upgrade, qty, this.typeNum);
	}

	@Override
	public String getIconTexture() {
		return "ancientwarfare:upgrade/" + iconTexture;
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
