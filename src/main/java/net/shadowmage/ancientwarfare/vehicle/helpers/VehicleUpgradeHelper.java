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

package net.shadowmage.ancientwarfare.vehicle.helpers;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.util.INBTSerializable;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.vehicle.armors.IVehicleArmor;
import net.shadowmage.ancientwarfare.vehicle.entity.VehicleBase;
import net.shadowmage.ancientwarfare.vehicle.missiles.DamageType;
import net.shadowmage.ancientwarfare.vehicle.network.PacketVehicle;
import net.shadowmage.ancientwarfare.vehicle.registry.ArmorRegistry;
import net.shadowmage.ancientwarfare.vehicle.registry.VehicleUpgradeRegistry;
import net.shadowmage.ancientwarfare.vehicle.upgrades.IVehicleUpgradeType;

import java.util.ArrayList;
import java.util.List;

public class VehicleUpgradeHelper implements INBTSerializable<NBTTagCompound> {

	/**
	 * currently installed upgrades, will be iterated through linearly to call upgrade.applyEffects, multiple upgrades may have cumulative effects
	 */
	private List<IVehicleUpgradeType> upgrades = new ArrayList<IVehicleUpgradeType>();
	private List<IVehicleArmor> installedArmor = new ArrayList<>();

	/**
	 * list of all upgrades that are valid for this vehicle, used by inventoryChecking to see whether it can be installed or not
	 */
	private List<IVehicleUpgradeType> validUpgrades = new ArrayList<IVehicleUpgradeType>();
	private List<IVehicleArmor> validArmorTypes = new ArrayList<>();
	private VehicleBase vehicle;

	public VehicleUpgradeHelper(VehicleBase vehicle) {
		this.vehicle = vehicle;
	}

	public int getLocalUpgradeType(IVehicleUpgradeType upgrade) {
		for (int i = 0; i < validUpgrades.size(); i++) {
			if (validUpgrades.get(i) == upgrade) {
				return i;
			}
		}
		return -1;
	}

	public IVehicleUpgradeType getUpgradeFromLocal(int local) {
		if (local >= 0 && local < this.validUpgrades.size()) {
			return this.validUpgrades.get(local);
		}
		return null;
	}

	public int getLocalArmorType(IVehicleArmor armor) {
		for (int i = 0; i < validArmorTypes.size(); i++) {
			if (validArmorTypes.get(i) == armor) {
				return i;
			}
		}
		return -1;
	}

	public IVehicleArmor getArmorFromLocal(int local) {
		if (local >= 0 && local < this.validArmorTypes.size()) {
			return this.validArmorTypes.get(local);
		}
		return null;
	}

	/**
	 * SERVER ONLY
	 */
	public void updateUpgrades() {
		if (vehicle.world.isRemote) {
			return;
		}
		this.upgrades.clear();
		List<IVehicleUpgradeType> upgrades = vehicle.inventory.getInventoryUpgrades();
		for (IVehicleUpgradeType up : upgrades) {
			if (this.validUpgrades.contains(up)) {
				this.upgrades.add(up);
			}
		}
		NBTTagCompound tag = new NBTTagCompound();
		serializeUpgrades(tag);

		this.installedArmor.clear();
		List<IVehicleArmor> armors = vehicle.inventory.getInventoryArmor();
		for (IVehicleArmor ar : armors) {
			//    Config.logDebug("installed armor: "+ar.getDisplayName());
			if (this.validArmorTypes.contains(ar)) {
				this.installedArmor.add(ar);
			} else {
				//      Config.logDebug("invalid armor! this vehicle has: "+this.validArmorTypes.size()+" valid armor types");
				//      for(IVehicleArmor type : this.validArmorTypes)
				//        {
				//        Config.logDebug(type.getDisplayName());
				//        }
			}
		}

		serializeInstalledArmors(tag);

		PacketVehicle pkt = new PacketVehicle();
		pkt.setParams(vehicle);
		pkt.setUpgradeData(tag);
		NetworkHandler.sendToAllTracking(vehicle, pkt);
		this.updateUpgradeStats();
	}

	private void serializeUpgrades(NBTTagCompound tag) {
		int len = this.upgrades.size();
		int[] upInts = new int[len];
		for (int i = 0; i < this.upgrades.size(); i++) {
			upInts[i] = this.upgrades.get(i).getUpgradeGlobalTypeNum();
		}

		tag.setIntArray("ints", upInts);
	}

	private void serializeInstalledArmors(NBTTagCompound tag) {
		int[] armorTypes = new int[installedArmor.size()];
		for (int i = 0; i < armorTypes.length; i++) {
			armorTypes[i] = installedArmor.get(i).getArmorType().ordinal();
		}

		tag.setIntArray("armors", armorTypes);
	}

	/**
	 * CLIENT ONLY..receives the packet sent above, and sets upgrade list directly from registry
	 */
	public void handleUpgradePacketData(NBTTagCompound tag) {
		this.upgrades.clear();
		deserializeUpgrades(tag);

		this.installedArmor.clear();
		deserializeInstalledArmor(tag);

		this.updateUpgradeStats();
	}

	private void deserializeInstalledArmor(NBTTagCompound tag) {
		int[] arInts = tag.getIntArray("armors");
		for (int i = 0; i < arInts.length; i++) {
			IVehicleArmor armor = ArmorRegistry.getArmorType(arInts[i]);
			if (armor != null) {
				this.installedArmor.add(armor);
			}
		}
	}

	private void deserializeUpgrades(NBTTagCompound tag) {
		int[] upInts = tag.getIntArray("ints");
		for (int i = 0; i < upInts.length; i++) {
			int up = upInts[i];
			IVehicleUpgradeType upgrade = VehicleUpgradeRegistry.instance().getUpgrade(up);
			if (upgrade != null) {
				this.upgrades.add(upgrade);
			}
		}
	}

	/**
	 * reset stats to base stats
	 * iterate through upgrades, applying their effects each in turn (multiple same upgrades are cumulative)
	 */
	public void updateUpgradeStats() {
		vehicle.resetCurrentStats();
		for (IVehicleUpgradeType upgrade : this.upgrades) {
			upgrade.applyVehicleEffects(vehicle);
		}
		for (IVehicleArmor armor : this.installedArmor) {
			//    Config.logDebug("updating armor stats");
			vehicle.currentExplosionResist += armor.getExplosiveDamageReduction();
			vehicle.currentFireResist += armor.getFireDamageReduction();
			vehicle.currentGenericResist += armor.getGeneralDamageReduction();
			vehicle.currentWeight += armor.getArmorWeight();
		}
	}

	public void addValidArmor(IVehicleArmor armor) {
		if (armor != null && !this.validArmorTypes.contains(armor)) {
			this.validArmorTypes.add(armor);
		}
	}

	public void addValidArmor(int type) {
		IVehicleArmor armor = ArmorRegistry.getArmorType(type);
		if (armor != null && !this.validArmorTypes.contains(armor)) {
			this.validArmorTypes.add(armor);
		}
	}

	public void addValidUpgrade(IVehicleUpgradeType upgrade) {
		if (upgrade != null && !this.validUpgrades.contains(upgrade)) {
			this.validUpgrades.add(upgrade);
		}
	}

	public void addValidUpgrade(int type) {
		IVehicleUpgradeType upgrade = VehicleUpgradeRegistry.instance().getUpgrade(type);
		if (upgrade != null && !this.validUpgrades.contains(upgrade)) {
			this.validUpgrades.add(upgrade);
		}
	}

	public float getScaledDamage(DamageSource src, float amt) {
		float floatAmt = (float) amt;
		if (src == DamageType.explosiveMissile || src.isExplosion()) {
			return floatAmt * (1 - (vehicle.currentExplosionResist * 0.01f));
		} else if (src == DamageType.fireMissile || src == DamageSource.IN_FIRE || src == DamageSource.LAVA || src == DamageSource.ON_FIRE || src
				.isFireDamage()) {
			return floatAmt * (1 - (vehicle.currentFireResist * 0.01f));
		}
		return floatAmt * (1 - (vehicle.currentGenericResist * 0.01f));
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound tag = new NBTTagCompound();

		serializeUpgrades(tag);
		serializeInstalledArmors(tag);

		return tag;
	}

	@Override
	public void deserializeNBT(NBTTagCompound tag) {
		this.upgrades.clear();
		deserializeUpgrades(tag);

		this.installedArmor.clear();
		deserializeInstalledArmor(tag);
	}

	public boolean hasUpgrade(IVehicleUpgradeType upgrade) {
		return this.upgrades.contains(upgrade);
	}

}
