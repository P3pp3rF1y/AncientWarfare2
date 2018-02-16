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
import net.shadowmage.ancientwarfare.vehicle.armors.IVehicleArmor;
import net.shadowmage.ancientwarfare.vehicle.entity.VehicleBase;
import net.shadowmage.ancientwarfare.vehicle.missiles.DamageType;
import net.shadowmage.ancientwarfare.vehicle.network.PacketVehicle;
import net.shadowmage.ancientwarfare.vehicle.registry.ArmorRegistry;
import net.shadowmage.ancientwarfare.vehicle.registry.VehicleUpgradeRegistry;
import net.shadowmage.ancientwarfare.vehicle.upgrades.IVehicleUpgradeType;
import shadowmage.ancient_warfare.common.interfaces.INBTSerializable;
import shadowmage.ancient_warfare.common.vehicles.armors.IVehicleArmorType;

import java.util.ArrayList;
import java.util.List;

public class VehicleUpgradeHelper implements INBTSerializable {

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

	public IVehicleArmorType getArmorFromLocal(int local) {
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
		int len = this.upgrades.size();
		int[] upInts = new int[len];
		for (int i = 0; i < this.upgrades.size(); i++) {
			upInts[i] = this.upgrades.get(i).getUpgradeGlobalTypeNum();
		}

		tag.setIntArray("ints", upInts);

		this.installedArmor.clear();
		List<IVehicleArmorType> armors = vehicle.inventory.getInventoryArmor();
		for (IVehicleArmorType ar : armors) {
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
		int[] arInts = new int[this.installedArmor.size()];
		for (int i = 0; i < this.installedArmor.size(); i++) {
			arInts[i] = this.installedArmor.get(i).getGlobalArmorType();
		}
		tag.setIntArray("ints2", arInts);

		PacketVehicle pkt = new PacketVehicle();
		pkt.setParams(vehicle);
		pkt.setUpgradeData(tag);
		pkt.sendPacketToAllTrackingClients(vehicle);
		this.updateUpgradeStats();
	}

	/**
	 * CLIENT ONLY..receives the packet sent above, and sets upgrade list directly from registry
	 */
	public void handleUpgradePacketData(NBTTagCompound tag) {
		this.upgrades.clear();
		int[] upInts = tag.getIntArray("ints");
		for (int i = 0; i < upInts.length; i++) {
			int up = upInts[i];
			IVehicleUpgradeType upgrade = VehicleUpgradeRegistry.instance().getUpgrade(up);
			if (upgrade != null) {
				this.upgrades.add(upgrade);
			}
		}

		this.installedArmor.clear();
		int[] arInts = tag.getIntArray("ints2");
		for (int i = 0; i < arInts.length; i++) {
			IVehicleArmorType armor = ArmorRegistry.instance().getArmorType(arInts[i]);
			if (armor != null) {
				this.installedArmor.add(armor);
			}
		}

		this.updateUpgradeStats();
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
		for (IVehicleArmorType armor : this.installedArmor) {
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
		IVehicleArmorType armor = ArmorRegistry.instance().getArmorType(type);
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
		} else if (src == DamageType.fireMissile || src == DamageType.inFire || src == DamageType.lava || src == DamageType.onFire || src.isFireDamage()) {
			return floatAmt * (1 - (vehicle.currentFireResist * 0.01f));
		}
		return floatAmt * (1 - (vehicle.currentGenericResist * 0.01f));
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound tag = new NBTTagCompound();
		int[] ints = new int[this.upgrades.size()];
		for (int i = 0; i < this.upgrades.size(); i++) {
			ints[i] = this.upgrades.get(i).getUpgradeGlobalTypeNum();
		}
		tag.setIntArray("ints", ints);

		int[] ints2 = new int[this.installedArmor.size()];
		for (int i = 0; i < this.installedArmor.size(); i++) {
			ints2[i] = this.installedArmor.get(i).getGlobalArmorType();
		}
		tag.setIntArray("ints2", ints2);
		return tag;
	}

	@Override
	public void deserializeNBT(NBTTagCompound tag) {
		this.upgrades.clear();
		int[] ints = tag.getIntArray("ints");
		for (int i = 0; i < ints.length; i++) {
			this.upgrades.add(VehicleUpgradeRegistry.instance().getUpgrade(ints[i]));
		}
		this.installedArmor.clear();
		ints = tag.getIntArray("ints2");
		for (int i = 0; i < ints.length; i++) {
			this.installedArmor.add(ArmorRegistry.instance().getArmorType(ints[i]));
		}
	}

	public boolean hasUpgrade(IVehicleUpgradeType upgrade) {
		return this.upgrades.contains(upgrade);
	}

}
