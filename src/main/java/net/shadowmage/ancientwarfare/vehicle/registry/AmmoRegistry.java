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
import net.shadowmage.ancientwarfare.vehicle.missiles.IAmmo;
import net.shadowmage.ancientwarfare.vehicle.missiles.ItemAmmo;
import net.shadowmage.ancientwarfare.vehicle.missiles.MissileBase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AmmoRegistry {

	private AmmoRegistry() {
	}

	private static AmmoRegistry INSTANCE;

	private Map<Integer, IAmmo> ammoInstances = new HashMap<Integer, IAmmo>();

	public static AmmoRegistry instance() {
		if (INSTANCE == null) {
			INSTANCE = new AmmoRegistry();
		}
		return INSTANCE;
	}

	public void registerAmmoTypes() {
		AWEntityRegistry.registerEntity(MissileBase.class, "entity.missile", 165, 5, true);

		/**
		 * debug..these will need to use the itemRegistry method..
		 */
		for (ItemAmmo ammo : ItemAmmo.ammoTypes) {
			if (ammo != null) {
				ammo.setEnabled(Config.getConfig().get("f_ammo_config", ammo.getConfigName() + ".enabled", ammo.isEnabled()).getBoolean(ammo.isEnabled()));
				if (ammo.isEnabled()) {
					ammo.setEntityDamage(Config.getConfig().get("f_ammo_config", ammo.getConfigName() + ".ent_damage", ammo.getEntityDamage())
							.getInt(ammo.getEntityDamage()));
					ammo.setVehicleDamage(Config.getConfig().get("f_ammo_config", ammo.getConfigName() + ".veh_damage", ammo.getVehicleDamage())
							.getInt(ammo.getVehicleDamage()));
					this.registerAmmoTypeWithItem(ammo);
				}
			}
		}
	}

	public List<IAmmo> getAmmoTypes() {
		List<IAmmo> ammosList = new ArrayList<IAmmo>();
		for (Integer key : this.ammoInstances.keySet()) {
			IAmmo t = this.ammoInstances.get(key);
			if (t != null) {
				ammosList.add(t);
			}
		}
		return ammosList;
	}

	/**
	 * used by structure gen to fill get ammo types to fill vehicles with
	 *
	 * @param type
	 * @return
	 */
	public IAmmo getAmmoEntry(int type) {
		return this.ammoInstances.get(type);
	}

	public void registerAmmoTypeWithItem(IAmmo ammo) {
		AWItemBase item = ItemLoader.ammoItem;
		List<String> tips = ammo.getDisplayTooltip();
		Description d = ItemLoader.instance().addSubtypeInfoToItem(item, ammo.getAmmoType(), ammo.getDisplayName());
		for (String tip : tips) {
			d.addTooltip(tip, ammo.getAmmoType());
		}
		//  d.addTooltip("Weight: "+ammo.getAmmoWeight(), ammo.getAmmoType());
		//  d.addTooltip("Entity Damage: "+ammo.getEntityDamage(), ammo.getAmmoType());
		//  d.addTooltip("Vehicle Damage: "+ammo.getVehicleDamage(), ammo.getAmmoType());
		//  if(ammo.isFlaming())
		//    {
		//    d.addTooltip("Flaming -- ignites targets when hit", ammo.getAmmoType());
		//    }
		//  if(ammo.isProximityAmmo())
		//    {
		//    d.addTooltip("Proximity -- detonates near targets", ammo.getAmmoType());
		//    }
		//  if(ammo.isPenetrating())
		//    {
		//    d.addTooltip("Penetrating -- does not stop on impact", ammo.getAmmoType());
		//    }
		//  if(ammo.getSecondaryAmmoType() != null && ammo.getSecondaryAmmoTypeCount()>0)
		//    {
		//    d.addTooltip("Cluster ammunition, spawns "+ammo.getSecondaryAmmoTypeCount()+" submunitions", ammo.getAmmoType());
		//    IAmmo t = ammo.getSecondaryAmmoType();
		//    d.addTooltip("Submunition Entity Damage: "+t.getEntityDamage(), ammo.getAmmoType());
		//    d.addTooltip("Submunition Vehicle Damage: "+t.getVehicleDamage(), ammo.getAmmoType());
		//    }
		d.addDisplayStack(ammo.getDisplayStack());
		d.setIconTexture(ammo.getIconTexture(), ammo.getAmmoType());
		this.registerAmmoType(ammo);
	}

	public void registerAmmoType(IAmmo ammo) {
		if (ammo == null) {
			return;
		}
		int type = ammo.getAmmoType();
		if (!this.ammoInstances.containsKey(type)) {
			this.ammoInstances.put(type, ammo);
		} else {
			Config.logError("Attempt to register a duplicate ammo type for number: " + type);
			Config.logError("Ammo attempting to being registered: " + ammo.getDisplayName());
		}
	}

	public IAmmo getAmmoForStack(ItemStack stack) {
		if (stack == null || stack.itemID != ItemLoader.ammoItem.itemID) {
			return null;
		}
		return this.ammoInstances.get(stack.getItemDamage());
	}

}
