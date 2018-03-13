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

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;
import net.shadowmage.ancientwarfare.vehicle.config.AWVehicleStatics;
import net.shadowmage.ancientwarfare.vehicle.entity.VehicleBase;
import net.shadowmage.ancientwarfare.vehicle.missiles.IAmmo;
import net.shadowmage.ancientwarfare.vehicle.missiles.MissileBase;
import net.shadowmage.ancientwarfare.vehicle.network.PacketAmmoSelect;
import net.shadowmage.ancientwarfare.vehicle.network.PacketAmmoUpdate;
import net.shadowmage.ancientwarfare.vehicle.network.PacketSingleAmmoUpdate;
import net.shadowmage.ancientwarfare.vehicle.registry.AmmoRegistry;
import net.shadowmage.ancientwarfare.vehicle.registry.VehicleAmmoEntry;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

public class VehicleAmmoHelper implements INBTSerializable<NBTTagCompound> {

	private VehicleBase vehicle;

	public ResourceLocation currentAmmoType = null;

	private LinkedHashMap<ResourceLocation, VehicleAmmoEntry> ammoEntries = new LinkedHashMap<>();

	public VehicleAmmoHelper(VehicleBase vehicle) {
		this.vehicle = vehicle;
	}

	public int getCountOf(IAmmo type) {
		for (VehicleAmmoEntry entry : ammoEntries.values()) {
			if (entry.baseAmmoType == type) {
				return entry.ammoCount;
			}
		}
		return 0;
	}

	/**
	 * SERVER ONLY relays changes to clients to update a single ammo type, also handles updating underlying inventory...
	 *
	 * @param num
	 */
	public void decreaseCurrentAmmo(int num) {
		if (vehicle.world.isRemote) {
			return;
		}
		if (ammoEntries.containsKey(currentAmmoType)) {
			int removed = InventoryTools.removeItems(vehicle.inventory.ammoInventory, new ItemStack(AmmoRegistry.getItem(currentAmmoType)), num).getCount();
			VehicleAmmoEntry entry = this.ammoEntries.get(this.currentAmmoType);
			int origCount = entry.ammoCount;
			entry.ammoCount -= removed;
			if (entry.ammoCount < 0) {
				entry.ammoCount = 0;
			}
			if (entry.ammoCount != origCount) {
				NetworkHandler.sendToAllTracking(vehicle, new PacketSingleAmmoUpdate(vehicle, currentAmmoType.toString(), entry.ammoCount));
			}
		}
	}

	/**
	 * get the current EFFECTIVE ammo count (not actual).  Soldiers use this to fool
	 * the vehicle into firing infinite rounds.
	 *
	 * @return
	 */
	public int getCurrentAmmoCount() {
		if (!AWVehicleStatics.soldiersUseAmmo && vehicle.getControllingPassenger() instanceof NpcBase) {
			return 64;
		}
		if (ammoEntries.containsKey(currentAmmoType)) {
			return this.ammoEntries.get(currentAmmoType).ammoCount;
		}
		return 0;
	}

	public boolean hasNoAmmo() {
		return this.ammoEntries.size() == 0;
	}

	public void addUseableAmmo(IAmmo ammo) {
		VehicleAmmoEntry ent = new VehicleAmmoEntry(ammo);
		this.ammoEntries.put(ammo.getRegistryName(), ent);
	}

	public void setNextAmmo() {
		boolean selectNext = false;
		for (ResourceLocation ammoName : ammoEntries.keySet()) {
			if (selectNext) {
				currentAmmoType = ammoName;
				handleClientAmmoSelection(currentAmmoType);
				return;
			}
			if (ammoName.equals(currentAmmoType)) {
				selectNext = true;
			}
		}

		//select the first if none was selected above
		Iterator<ResourceLocation> it = ammoEntries.keySet().iterator();
		if (it.hasNext()) {
			currentAmmoType = it.next();
			handleClientAmmoSelection(currentAmmoType);
		}
	}

	public void setPreviousAmmo() {
		ResourceLocation lastName = null;
		for (ResourceLocation ammoName : ammoEntries.keySet()) {
			if (ammoName.equals(currentAmmoType)) {
				if (lastName == null) {
					break;
				} else {
					currentAmmoType = lastName;
					handleClientAmmoSelection(currentAmmoType);
					return;
				}
			}
			lastName = ammoName;
		}

		//select the last if none was selected above
		Iterator<ResourceLocation> it = ammoEntries.keySet().iterator();
		ResourceLocation last = null;
		while (it.hasNext()) {
			last = it.next();
		}
		if (last != null) {
			currentAmmoType = last;
			handleClientAmmoSelection(currentAmmoType);
		}
	}

	public void handleClientAmmoSelection(ResourceLocation ammoRegistryName) {
		if (!ammoRegistryName.equals(currentAmmoType)) {
			NetworkHandler.sendToServer(new PacketAmmoSelect(vehicle, ammoRegistryName.toString()));
		}
	}

	public void updateSelectedAmmo(String ammoRegistryName) {
		if (!ammoRegistryName.equals(currentAmmoType.toString())) {
			this.currentAmmoType = new ResourceLocation(ammoRegistryName);
			if (!vehicle.world.isRemote) {
				NetworkHandler.sendToAllTracking(vehicle, new PacketAmmoSelect(vehicle, ammoRegistryName));
			}
			float maxPower = vehicle.firingHelper.getAdjustedMaxMissileVelocity();
			if (!vehicle.canAimPower()) {
				vehicle.localLaunchPower = maxPower;
			} else if (vehicle.localLaunchPower > maxPower) {
				vehicle.localLaunchPower = maxPower;
				if (vehicle.world.isRemote && vehicle.firingHelper.clientLaunchSpeed > maxPower) {
					vehicle.firingHelper.clientLaunchSpeed = maxPower;
				}
			}
		}
	}

	public void updateAmmoCount(String ammoRegistryName, int count) {
		ResourceLocation rl = new ResourceLocation(ammoRegistryName);
		if (ammoEntries.containsKey(rl)) {
			this.ammoEntries.get(rl).ammoCount = count;
		}
	}

	public void updateAmmoCounts() {
		if (vehicle.world.isRemote) {
			return;
		}
		//  Config.logDebug("counting ammos!!");
		for (VehicleAmmoEntry ent : this.ammoEntries.values()) {
			ent.ammoCount = 0;
		}
		List<VehicleAmmoEntry> counts = vehicle.inventory.getAmmoCounts();

		for (VehicleAmmoEntry count : counts) {
			for (VehicleAmmoEntry ent : this.ammoEntries.values()) {
				if (ent.baseAmmoType == count.baseAmmoType) {
					ent.ammoCount = count.ammoCount;
				}
			}
		}
		PacketAmmoUpdate pkt = new PacketAmmoUpdate(vehicle, serializeAmmo(new NBTTagCompound()));
		NetworkHandler.sendToAllTracking(vehicle, pkt);
	}

	public IAmmo getCurrentAmmoType() {
		if (!AWVehicleStatics.soldiersUseAmmo && vehicle.getControllingPassenger() instanceof NpcBase) {
			NpcBase npc = (NpcBase) vehicle.getControllingPassenger();
			return vehicle.vehicleType.getAmmoForSoldierRank(npc.getLevelingStats().getLevel());
		}
		if (ammoEntries.containsKey(currentAmmoType)) {
			VehicleAmmoEntry entry = this.ammoEntries.get(currentAmmoType);
			if (entry != null) {
				return entry.baseAmmoType;
			}
		}
		return null;
	}

	public MissileBase getMissile(float x, float y, float z, float mx, float my, float mz) {
		IAmmo ammo = this.getCurrentAmmoType();
		if (ammo != null) {
			MissileBase missile = new MissileBase(vehicle.world);
			missile.setMissileParams(ammo, x, y, z, mx, my, mz);
			missile.setMissileCallback(vehicle);
			return missile;
		}
		return null;
	}

	public MissileBase getMissile2(float x, float y, float z, float yaw, float pitch, float velocity) {
		IAmmo ammo = this.getCurrentAmmoType();
		if (ammo != null) {
			MissileBase missile = new MissileBase(vehicle.world);
			if (ammo.hasSecondaryAmmo()) {
				ammo = ammo.getSecondaryAmmoType();
			}
			missile.setMissileParams2(ammo, x, y, z, yaw, pitch, velocity);
			missile.setMissileCallback(vehicle);
			missile.setLaunchingEntity(vehicle);
			missile.setShooter(vehicle.getControllingPassenger());
			return missile;
		}
		return null;
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setString("currentAmmoType", currentAmmoType.toString());
		serializeAmmo(tag);
		return tag;
	}

	public NBTTagCompound serializeAmmo(NBTTagCompound tag) {
		NBTTagList tagList = new NBTTagList();
		for (VehicleAmmoEntry ent : this.ammoEntries.values()) {
			NBTTagCompound entryTag = new NBTTagCompound();
			entryTag.setString("type", ent.baseAmmoType.getRegistryName().toString());
			entryTag.setInteger("cnt", ent.ammoCount);
			tagList.appendTag(entryTag);
		}
		tag.setTag("list", tagList);
		return tag;
	}

	@Override
	public void deserializeNBT(NBTTagCompound tag) {
		for (VehicleAmmoEntry ent : this.ammoEntries.values()) {
			ent.ammoCount = 0;
		}
		this.currentAmmoType = new ResourceLocation(tag.getString("currentAmmoType"));
		deserializeAmmo(tag);
	}

	public void updateAmmo(NBTTagCompound tag) {
		for (VehicleAmmoEntry ent : this.ammoEntries.values()) {
			ent.ammoCount = 0;
		}
		deserializeAmmo(tag);
	}

	private void deserializeAmmo(NBTTagCompound tag) {
		NBTTagList ammo = tag.getTagList("list", Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < ammo.tagCount(); i++) {
			NBTTagCompound entryTag = (NBTTagCompound) ammo.get(i);
			String type = entryTag.getString("type");
			int count = entryTag.getInteger("count");
			for (VehicleAmmoEntry ent : this.ammoEntries.values()) {
				if (ent.baseAmmoType.getRegistryName().toString().equals(type)) {
					ent.ammoCount = count;
					break;
				}
			}
		}
	}

}
