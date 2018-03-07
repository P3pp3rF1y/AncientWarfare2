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
import net.shadowmage.ancientwarfare.vehicle.upgrades.IVehicleUpgradeType;
import net.shadowmage.ancientwarfare.vehicle.upgrades.VehicleUpgradeAim;
import net.shadowmage.ancientwarfare.vehicle.upgrades.VehicleUpgradePitchDown;
import net.shadowmage.ancientwarfare.vehicle.upgrades.VehicleUpgradePitchUp;
import net.shadowmage.ancientwarfare.vehicle.upgrades.VehicleUpgradePower;
import net.shadowmage.ancientwarfare.vehicle.upgrades.VehicleUpgradeReload;
import net.shadowmage.ancientwarfare.vehicle.upgrades.VehicleUpgradeSpeed;
import net.shadowmage.ancientwarfare.vehicle.upgrades.VehicleUpgradeTurretPitch;
import shadowmage.ancient_warfare.common.item.ItemLoader;
import shadowmage.ancient_warfare.common.registry.entry.Description;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class VehicleUpgradeRegistry {

	public static final IVehicleUpgradeType speedUpgrade = new VehicleUpgradeSpeed(0);
	public static final IVehicleUpgradeType aimUpgrade = new VehicleUpgradeAim(1);
	public static final IVehicleUpgradeType reloadUpgrade = new VehicleUpgradeReload(2);

	public static final IVehicleUpgradeType powerUpgrade = new VehicleUpgradePower(4);
	public static final IVehicleUpgradeType pitchExtUpgrade = new VehicleUpgradeTurretPitch(5);
	public static final IVehicleUpgradeType pitchUpUpgrade = new VehicleUpgradePitchUp(6);
	public static final IVehicleUpgradeType pitchDownUpgrade = new VehicleUpgradePitchDown(7);

	private Map<Integer, IVehicleUpgradeType> upgradeTypeMap = new HashMap<Integer, IVehicleUpgradeType>();

	private VehicleUpgradeRegistry() {
	}

	public static VehicleUpgradeRegistry instance() {
		if (INSTANCE == null) {
			INSTANCE = new VehicleUpgradeRegistry();
		}
		return INSTANCE;
	}

	private static VehicleUpgradeRegistry INSTANCE;

	public Collection<IVehicleUpgradeType> getUpgradeList() {
		return this.upgradeTypeMap.values();
	}

	/**
	 * called during init to register upgrade types as items
	 */
	public void registerUpgrades() {
		this.registerUpgrade(speedUpgrade);
		this.registerUpgrade(aimUpgrade);
		this.registerUpgrade(reloadUpgrade);
		this.registerUpgrade(powerUpgrade);
		this.registerUpgrade(pitchExtUpgrade);
		this.registerUpgrade(pitchUpUpgrade);
		this.registerUpgrade(pitchDownUpgrade);
	}

	/**
	 * @param dmg
	 * @param targetType
	 * @param upgrade
	 */
	public void registerUpgrade(IVehicleUpgradeType upgrade) {
		this.upgradeTypeMap.put(upgrade.getUpgradeId(), upgrade);
		Description d = ItemLoader.instance()
				.addSubtypeInfoToItem(ItemLoader.vehicleUpgrade, upgrade.getUpgradeId(), upgrade.getDisplayName(), "", upgrade.getDisplayTooltip());
		d.setIconTexture(upgrade.getIconTexture(), upgrade.getUpgradeId());
		d.addDisplayStack(new ItemStack(ItemLoader.vehicleUpgrade, 1, upgrade.getUpgradeId()));
	}

	public IVehicleUpgradeType getUpgrade(int type) {
		return this.upgradeTypeMap.get(type);
	}

	public IVehicleUpgradeType getUpgrade(ItemStack stack) {
		if (stack == null) {
			return null;
		}
		if (stack.itemID == ItemLoader.vehicleUpgrade.itemID) {
			return this.upgradeTypeMap.get(stack.getItemDamage());
		}
		return null;
	}

	public boolean isStackUpgradeItem(ItemStack stack) {
		if (stack == null) {
			return false;
		}
		if (stack.itemID == ItemLoader.vehicleUpgrade.itemID) {
			return true;
		}
		return false;
	}

}
