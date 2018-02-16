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
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;
import net.shadowmage.ancientwarfare.vehicle.config.AWVehicleStatics;
import net.shadowmage.ancientwarfare.vehicle.entity.VehicleBase;
import net.shadowmage.ancientwarfare.vehicle.item.AWVehicleItems;
import net.shadowmage.ancientwarfare.vehicle.missiles.IAmmo;
import net.shadowmage.ancientwarfare.vehicle.missiles.MissileBase;
import net.shadowmage.ancientwarfare.vehicle.network.PacketVehicle;
import net.shadowmage.ancientwarfare.vehicle.registry.VehicleAmmoEntry;

import java.util.ArrayList;
import java.util.List;

public class VehicleAmmoHelper implements INBTSerializable<NBTTagCompound> {

	private VehicleBase vehicle;

	public int currentAmmoType = 0;

	private List<VehicleAmmoEntry> ammoEntries = new ArrayList<VehicleAmmoEntry>();
	//private HashMap<Integer, VehicleAmmoEntry> ammoTypes = new HashMap<Integer, VehicleAmmoEntry>();//local ammo type to global entry

	public VehicleAmmoHelper(VehicleBase vehicle) {
		this.vehicle = vehicle;
	}

	public int getCountOf(IAmmo type) {
		for (VehicleAmmoEntry entry : this.ammoEntries) {
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
		if (currentAmmoType >= 0 && currentAmmoType < this.ammoEntries.size()) {
			int removed = InventoryTools
					.removeItems(vehicle.inventory.ammoInventory, new ItemStack(AWVehicleItems.ammo, getCurrentAmmoType().getAmmoType()), num).getCount();
			VehicleAmmoEntry entry = this.ammoEntries.get(this.currentAmmoType);
			int origCount = entry.ammoCount;
			entry.ammoCount -= removed;
			if (entry.ammoCount < 0) {
				entry.ammoCount = 0;
			}
			if (entry.ammoCount != origCount) {
				NBTTagCompound tag = new NBTTagCompound();
				tag.setInteger("num", this.currentAmmoType);
				tag.setInteger("cnt", entry.ammoCount);
				PacketVehicle pkt = new PacketVehicle();
				pkt.setParams(vehicle);
				pkt.setAmmoUpdate(tag);
				NetworkHandler.sendToAllTracking(vehicle, pkt);
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
		if (this.ammoEntries.size() > 0 && this.currentAmmoType < this.ammoEntries.size()) {
			return this.ammoEntries.get(currentAmmoType).ammoCount;
		}
		return 0;
	}

	public boolean hasNoAmmo() {
		return this.ammoEntries.size() == 0;
	}

	public void addUseableAmmo(IAmmo ammo) {
		VehicleAmmoEntry ent = new VehicleAmmoEntry(ammo);
		this.ammoEntries.add(ent);
	}

	/**
	 * client-side ammo selection by ammo type
	 *
	 * @param type
	 */
	public void handleClientAmmoSelection(IAmmo type) {
		int foundIndex = -1;
		VehicleAmmoEntry entry;
		for (int i = 0; i < this.ammoEntries.size(); i++) {
			entry = this.ammoEntries.get(i);
			if (entry != null && entry.baseAmmoType == type) {
				foundIndex = i;
				break;
			}
		}
		if (foundIndex >= 0) {
			this.handleClientAmmoSelection(foundIndex);
		}
	}

	/**
	 * client-side ammo selection by number
	 *
	 * @param type
	 */
	public void handleClientAmmoSelection(int type) {
		if (type >= 0 && type <= this.ammoEntries.size() && type != this.currentAmmoType) {
			NBTTagCompound innerTag = new NBTTagCompound();
			innerTag.setInteger("num", type);
			PacketVehicle pkt = new PacketVehicle();
			pkt.setParams(vehicle);
			pkt.setAmmoSelect(innerTag);
			NetworkHandler.sendToServer(pkt);
		}
	}

	/**
	 * client-side input from delta (used by keybind to change)
	 *
	 * @param delta
	 */
	public void handleAmmoSelectInput(int delta) {
		if (this.ammoEntries.size() > 0) {
			int test = this.currentAmmoType + delta;
			while (test < 0) {
				test += this.ammoEntries.size();
			}
			while (test >= this.ammoEntries.size()) {
				test -= this.ammoEntries.size();
			}
			if (test >= 0) {
				this.handleClientAmmoSelection(test);
			}
		}
	}

	/**
	 * client AND server method to process valid ammo-type change packets. *
	 *
	 * @param tag
	 */
	public void handleAmmoSelectPacket(NBTTagCompound tag) {
		int num = tag.getInteger("num");
		if (num >= 0 && num < this.ammoEntries.size() && num != this.currentAmmoType) {
			this.currentAmmoType = num;
			if (!vehicle.world.isRemote) {
				NBTTagCompound innerTag = new NBTTagCompound();
				innerTag.setInteger("num", num);
				PacketVehicle pkt = new PacketVehicle();
				pkt.setParams(vehicle);
				pkt.setAmmoSelect(innerTag);
				NetworkHandler.sendToAllTracking(vehicle, pkt);
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

	/**
	 * sent to clients when ammo is used from firing...
	 * CLIENT ONLY
	 *
	 * @param tag
	 */
	public void handleAmmoCountUpdate(NBTTagCompound tag) {
		//  Config.logDebug("updating single ammo type");
		int num = tag.getInteger("num");
		if (num >= 0 && num < this.ammoEntries.size()) {
			int count = tag.getInteger("cnt");
			this.ammoEntries.get(num).ammoCount = count;
		}
	}

	/**
	 * SERVER ONLY....
	 */
	public void updateAmmoCounts() {
		if (vehicle.world.isRemote) {
			return;
		}
		//  Config.logDebug("counting ammos!!");
		for (VehicleAmmoEntry ent : this.ammoEntries) {
			ent.ammoCount = 0;
		}
		List<VehicleAmmoEntry> counts = vehicle.inventory.getAmmoCounts();

		NBTTagCompound tag = new NBTTagCompound();
		NBTTagList tagList = new NBTTagList();
		for (VehicleAmmoEntry count : counts) {
			NBTTagCompound entryTag = new NBTTagCompound();
			for (VehicleAmmoEntry ent : this.ammoEntries) {
				if (ent.baseAmmoType == count.baseAmmoType) {
					ent.ammoCount = count.ammoCount;
				}
			}
			entryTag.setInteger("type", count.baseAmmoType.getAmmoType());
			entryTag.setInteger("count", count.ammoCount);
			tagList.appendTag(entryTag);
		}
		tag.setTag("list", tagList);
		PacketVehicle pkt = new PacketVehicle();
		pkt.setParams(vehicle);
		pkt.setAmmoData(tag);
		NetworkHandler.sendToAllTracking(vehicle, pkt);
	}

	/**
	 * CLIENT ONLY--version of above
	 *
	 * @param tag
	 */
	public void handleAmmoUpdatePacket(NBTTagCompound tag) {
		//  Config.logDebug("receiving ammo count client update");
		for (VehicleAmmoEntry ent : this.ammoEntries) {
			ent.ammoCount = 0;
		}
		NBTTagList tagList = tag.getTagList("list", Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < tagList.tagCount(); i++) {
			NBTTagCompound entryTag = (NBTTagCompound) tagList.get(i);
			int type = entryTag.getInteger("type");
			int count = entryTag.getInteger("count");
			for (VehicleAmmoEntry ent : this.ammoEntries) {
				if (ent.baseAmmoType.getAmmoType() == type) {
					ent.ammoCount = count;
					break;
				}
			}
		}
	}

	public IAmmo getCurrentAmmoType() {
		if (!AWVehicleStatics.soldiersUseAmmo && vehicle.getControllingPassenger() instanceof NpcBase) {
			NpcBase npc = (NpcBase) vehicle.getControllingPassenger();
			return vehicle.vehicleType.getAmmoForSoldierRank(npc.rank);
		}
		if (currentAmmoType < this.ammoEntries.size() && currentAmmoType >= 0) {
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
		tag.setInteger("am", currentAmmoType);
		NBTTagList tagList = new NBTTagList();
		for (VehicleAmmoEntry ent : this.ammoEntries) {
			NBTTagCompound entryTag = new NBTTagCompound();
			entryTag.setInteger("num", ent.baseAmmoType.getAmmoType());
			entryTag.setInteger("cnt", ent.ammoCount);
			//    Config.logDebug("writing ammo count "+entryTag.getInteger("num")+","+entryTag.getInteger("cnt"));
			tagList.appendTag(entryTag);
		}
		tag.setTag("list", tagList);
		return tag;
	}

	@Override
	public void deserializeNBT(NBTTagCompound tag) {
		for (VehicleAmmoEntry ent : this.ammoEntries) {
			ent.ammoCount = 0;
		}
		this.currentAmmoType = tag.getInteger("am");
		NBTTagList tagList = tag.getTagList("list", Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < tagList.tagCount(); i++) {
			NBTTagCompound entryTag = (NBTTagCompound) tagList.get(i);
			int num = entryTag.getInteger("num");
			int cnt = entryTag.getInteger("cnt");
			for (VehicleAmmoEntry ent : this.ammoEntries) {
				if (ent.baseAmmoType.getAmmoType() == num) {
					ent.ammoCount = cnt;
					break;
				}
			}
		}
	}

}
