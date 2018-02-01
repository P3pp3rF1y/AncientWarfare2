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

package shadowmage.ancient_warfare.common.registry;

import net.minecraft.item.ItemStack;
import shadowmage.ancient_warfare.common.item.AWItemBase;
import shadowmage.ancient_warfare.common.item.ItemLoader;
import shadowmage.ancient_warfare.common.registry.entry.Description;
import shadowmage.ancient_warfare.common.vehicles.armors.IVehicleArmorType;
import shadowmage.ancient_warfare.common.vehicles.armors.VehicleArmorIron;
import shadowmage.ancient_warfare.common.vehicles.armors.VehicleArmorObsidian;
import shadowmage.ancient_warfare.common.vehicles.armors.VehicleArmorStone;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ArmorRegistry {

	public static IVehicleArmorType armorStone = new VehicleArmorStone(0);
	public static IVehicleArmorType armorIron = new VehicleArmorIron(1);
	public static IVehicleArmorType armorObsidian = new VehicleArmorObsidian(2);

	private ArmorRegistry() {
	}

	private static ArmorRegistry INSTANCE;
	private Map<Integer, IVehicleArmorType> armorInstances = new HashMap<Integer, IVehicleArmorType>();

	public static ArmorRegistry instance() {
		if (INSTANCE == null) {
			INSTANCE = new ArmorRegistry();
		}
		return INSTANCE;
	}

	public void registerArmorTypes() {
		this.registerArmorType(armorStone);
		this.registerArmorType(armorIron);
		this.registerArmorType(armorObsidian);
	}

	public Collection<IVehicleArmorType> getArmorTypes() {
		return this.armorInstances.values();
	}

	public void registerArmorType(IVehicleArmorType armor) {
		AWItemBase item = ItemLoader.armorItem;
		Description d = ItemLoader.instance().addSubtypeInfoToItem(item, armor.getGlobalArmorType(), armor.getDisplayName(), "", armor.getDisplayTooltip());
		d.setIconTexture(armor.getIconTexture(), armor.getGlobalArmorType());
		d.addDisplayStack(new ItemStack(item, 1, armor.getGlobalArmorType()));
		this.armorInstances.put(armor.getGlobalArmorType(), armor);
	}

	public IVehicleArmorType getArmorType(int type) {
		return this.armorInstances.get(type);
	}

	public IVehicleArmorType getArmorForStack(ItemStack stack) {
		if (stack != null && stack.itemID == ItemLoader.armorItem.itemID) {
			return armorInstances.get(stack.getItemDamage());
		}
		return null;
	}

}
