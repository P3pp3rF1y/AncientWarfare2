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

package net.shadowmage.ancientwarfare.vehicle.registry;

import net.minecraft.item.ItemStack;
import net.shadowmage.ancientwarfare.vehicle.armors.IVehicleArmor;
import net.shadowmage.ancientwarfare.vehicle.armors.VehicleArmorIron;
import net.shadowmage.ancientwarfare.vehicle.armors.VehicleArmorObsidian;
import net.shadowmage.ancientwarfare.vehicle.armors.VehicleArmorStone;
import net.shadowmage.ancientwarfare.vehicle.item.AWVehicleItems;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ArmorRegistry {

	public static IVehicleArmor armorStone = new VehicleArmorStone();
	public static IVehicleArmor armorIron = new VehicleArmorIron();
	public static IVehicleArmor armorObsidian = new VehicleArmorObsidian();

	private static Map<Integer, IVehicleArmor> armorInstances = new HashMap<>();

	public static void registerArmorTypes() {
		registerArmorType(armorStone);
		registerArmorType(armorIron);
		registerArmorType(armorObsidian);
	}

	public static Collection<IVehicleArmor> getArmorTypes() {
		return armorInstances.values();
	}

	public static void registerArmorType(IVehicleArmor armor) {
		Description d = ItemLoader.instance()
				.addSubtypeInfoToItem(AWVehicleItems.armor, armor.getArmorType(), armor.getDisplayName(), "", armor.getDisplayTooltip());
		d.setIconTexture(armor.getIconTexture(), armor.getArmorType());
		d.addDisplayStack(new ItemStack(AWVehicleItems.armor, 1, armor.getArmorType()));
		this.armorInstances.put(armor.getArmorType(), armor);
	}

	public static IVehicleArmor getArmorType(int typeId) {
		return armorInstances.get(typeId);
	}

	public static IVehicleArmor getArmorForStack(ItemStack stack) {
		if (stack != null && stack.itemID == ItemLoader.armorItem.itemID) {
			return armorInstances.get(stack.getItemDamage());
		}
		return null;
	}
}
