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

package net.shadowmage.ancientwarfare.vehicle.inventory;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.items.ItemStackHandler;
import net.shadowmage.ancientwarfare.vehicle.armors.IVehicleArmor;
import net.shadowmage.ancientwarfare.vehicle.entity.VehicleBase;
import net.shadowmage.ancientwarfare.vehicle.missiles.IAmmo;
import net.shadowmage.ancientwarfare.vehicle.registry.AmmoRegistry;
import net.shadowmage.ancientwarfare.vehicle.registry.ArmorRegistry;
import net.shadowmage.ancientwarfare.vehicle.registry.VehicleAmmoEntry;
import net.shadowmage.ancientwarfare.vehicle.registry.VehicleUpgradeRegistry;
import net.shadowmage.ancientwarfare.vehicle.upgrades.IVehicleUpgradeType;

import java.util.ArrayList;
import java.util.List;

public class VehicleInventory {

	private VehicleBase vehicle;

	/**
	 * individual inventories
	 */
	//TODO custom filters for the different inventory types
	public ItemStackHandler upgradeInventory;
	public ItemStackHandler ammoInventory;
	public ItemStackHandler armorInventory;
	public ItemStackHandler storageInventory;

	public VehicleInventory(VehicleBase vehicle) {
		this.vehicle = vehicle;
	}

	public void setInventorySizes(int upgrade, int ammo, int armor, int storage) {
		//TODO REFACTOR move this to constructor?
		this.ammoInventory = new AmmoStackHandler(vehicle, ammo);
		this.armorInventory = new ArmorStackHandler(vehicle, armor);
		this.storageInventory = new ItemStackHandler(storage);
		this.upgradeInventory = new UpgradeStackHandler(vehicle, upgrade);
	}

	/**
	 * if inventory is valid, write this entire inventory to the passed tag
	 *
	 * @param commonTag
	 */
	public void writeToNBT(NBTTagCompound commonTag) {
		NBTTagCompound tag = new NBTTagCompound();
		if (this.upgradeInventory != null) {
			tag.setTag("upgradeInventory", this.upgradeInventory.serializeNBT());
		}
		if (this.ammoInventory != null) {
			tag.setTag("ammoInventory", this.ammoInventory.serializeNBT());
		}
		if (this.storageInventory != null) {
			tag.setTag("storageInventory", this.storageInventory.serializeNBT());
		}
		if (this.armorInventory != null) {
			tag.setTag("armorInventory", this.armorInventory.serializeNBT());
		}
		commonTag.setTag("inventory", tag);
	}

	/**
	 * blind read method, inv tag need not even be present
	 * if present, will read the entire inventory from tag,
	 * including setting initial inventory sizes
	 *
	 * @param commonTag
	 */
	public void readFromNBT(NBTTagCompound commonTag) {
		if (!commonTag.hasKey("inventory")) {
			return;
		}
		NBTTagCompound tag = commonTag.getCompoundTag("inventory");
		if (this.upgradeInventory != null) {
			this.upgradeInventory.deserializeNBT(tag.getCompoundTag("upgradeInventory"));
		}
		if (this.ammoInventory != null) {
			this.ammoInventory.deserializeNBT(tag.getCompoundTag("ammoInventory"));
		}
		if (this.storageInventory != null) {
			this.storageInventory.deserializeNBT(tag.getCompoundTag("storageInventory"));
		}
		if (this.armorInventory != null) {
			this.armorInventory.deserializeNBT(tag.getCompoundTag("armorInventory"));
		}
	}

	public List<IVehicleArmor> getInventoryArmor() {
		ArrayList<IVehicleArmor> armors = new ArrayList<IVehicleArmor>();
		for (int i = 0; i < this.armorInventory.getSlots(); i++) {
			ItemStack stack = this.armorInventory.getStackInSlot(i);
			IVehicleArmor armor = ArmorRegistry.getArmorForStack(stack);
			if (armor != null) {
				armors.add(armor);
			}
		}
		return armors;
	}

	public List<IVehicleUpgradeType> getInventoryUpgrades() {
		ArrayList<IVehicleUpgradeType> upgrades = new ArrayList<IVehicleUpgradeType>();
		for (int i = 0; i < this.upgradeInventory.getSlots(); i++) {
			ItemStack stack = this.upgradeInventory.getStackInSlot(i);
			IVehicleUpgradeType upgrade = VehicleUpgradeRegistry.instance().getUpgrade(stack);
			if (upgrade != null) {
				upgrades.add(upgrade);
			}
		}
		return upgrades;
	}

	public List<VehicleAmmoEntry> getAmmoCounts() {
		ArrayList<VehicleAmmoEntry> counts = new ArrayList<VehicleAmmoEntry>();
		for (int i = 0; i < this.ammoInventory.getSlots(); i++) {
			ItemStack stack = this.ammoInventory.getStackInSlot(i);
			IAmmo ammo = AmmoRegistry.getAmmoForStack(stack);
			if (ammo != null) {
				boolean found = false;
				for (VehicleAmmoEntry ent : counts) {
					if (ent.baseAmmoType == ammo) {
						found = true;
						ent.ammoCount += stack.getCount();
						break;
					}
				}
				if (!found) {
					counts.add(new VehicleAmmoEntry(ammo));
					counts.get(counts.size() - 1).ammoCount += stack.getCount();
				}
			}
		}
		return counts;
	}

	public boolean isAmmoValid(ItemStack stack) {
		IAmmo type = AmmoRegistry.getAmmoForStack(stack);
		return type != null && vehicle.vehicleType.isAmmoValidForInventory(type);
	}

	public boolean isArmorValid(ItemStack stack) {
		IVehicleArmor armor = ArmorRegistry.getArmorForStack(stack);
		return armor != null && vehicle.vehicleType.isArmorValid(armor);
	}

	public boolean isUpgradeValid(ItemStack stack) {
		IVehicleUpgradeType upgrade = VehicleUpgradeRegistry.instance().getUpgrade(stack);
		return upgrade != null && vehicle.vehicleType.isUpgradeValid(upgrade);
	}

}
