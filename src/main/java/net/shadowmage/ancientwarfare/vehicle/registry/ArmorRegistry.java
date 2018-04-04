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

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import net.shadowmage.ancientwarfare.vehicle.armors.IVehicleArmor;
import net.shadowmage.ancientwarfare.vehicle.armors.VehicleArmorIron;
import net.shadowmage.ancientwarfare.vehicle.armors.VehicleArmorObsidian;
import net.shadowmage.ancientwarfare.vehicle.armors.VehicleArmorStone;
import net.shadowmage.ancientwarfare.vehicle.item.ItemArmor;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ArmorRegistry {

	public static IVehicleArmor armorStone;
	public static IVehicleArmor armorIron;
	public static IVehicleArmor armorObsidian;

	private static Map<ResourceLocation, IVehicleArmor> armorInstances = new HashMap<>();
	private static Map<ResourceLocation, ItemArmor> armorItemInstances = new HashMap<>();

	public static void registerArmorTypes(IForgeRegistry<Item> registry) {
		armorStone = registerArmorType(new VehicleArmorStone(), registry);
		armorIron = registerArmorType(new VehicleArmorIron(), registry);
		armorObsidian = registerArmorType(new VehicleArmorObsidian(), registry);
	}

	public static Collection<IVehicleArmor> getArmorTypes() {
		return armorInstances.values();
	}

	public static IVehicleArmor registerArmorType(IVehicleArmor armor, IForgeRegistry<Item> registry) {

		armorInstances.put(armor.getRegistryName(), armor);
		ItemArmor item = new ItemArmor(armor.getRegistryName());
		registry.register(item);
		armorItemInstances.put(armor.getRegistryName(), item);
		return armor;
	}

	public static IVehicleArmor getArmorType(ResourceLocation registryName) {
		return armorInstances.get(registryName);
	}

	public static IVehicleArmor getArmorForStack(ItemStack stack) {
		return armorInstances.get(stack.getItem().getRegistryName());
	}
}
