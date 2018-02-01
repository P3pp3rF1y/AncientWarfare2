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

package shadowmage.ancient_warfare.common.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import shadowmage.ancient_warfare.common.interfaces.IEntityContainerSynch;
import shadowmage.ancient_warfare.common.inventory.SlotVehicleAmmo;
import shadowmage.ancient_warfare.common.inventory.SlotVehicleArmor;
import shadowmage.ancient_warfare.common.inventory.SlotVehicleUpgrade;
import shadowmage.ancient_warfare.common.item.ItemLoader;
import shadowmage.ancient_warfare.common.vehicles.VehicleBase;

import java.util.Collections;
import java.util.List;

public class ContainerVehicle extends ContainerBase {

	public VehicleBase vehicle;

	public Slot[] storageSlots;

	public int storageY;

	public int extrasY;

	public int playerY;

	/**
	 * @param openingPlayer
	 * @param synch
	 */
	public ContainerVehicle(EntityPlayer openingPlayer, IEntityContainerSynch synch, VehicleBase vehicle) {
		super(openingPlayer, synch);
		this.vehicle = vehicle;
		int y;
		int x;
		int slotNum;
		int xPos;
		int yPos;

		storageY = 4 + 10 + 20;
		int invHeight = (vehicle.inventory.storageInventory.getSizeInventory() / 9 + (vehicle.inventory.storageInventory
				.getSizeInventory() % 9 == 0 ? 0 : 1)) * 18;
		invHeight = invHeight > 3 * 18 ? 3 * 18 : invHeight;
		extrasY = storageY + (invHeight == 0 ? 0 : 10) + invHeight;
		playerY = extrasY + 4 + 10 + 2 * 18;
		this.addPlayerSlots(player, 8, playerY, 4);

		for (y = 0; y < 2; y++) {
			for (x = 0; x < 3; x++) {
				slotNum = y * 3 + x;
				if (slotNum < vehicle.inventory.ammoInventory.getSizeInventory()) {
					xPos = 8 + x * 18;
					yPos = y * 18 + extrasY;
					this.addSlotToContainer(new SlotVehicleAmmo(vehicle.inventory.ammoInventory, vehicle, slotNum, xPos, yPos));
				}
			}
		}
		for (y = 0; y < 2; y++) {
			for (x = 0; x < 3; x++) {

				slotNum = y * 3 + x;
				if (slotNum < vehicle.inventory.upgradeInventory.getSizeInventory()) {
					xPos = 8 + x * 18 + 3 * 18 + 5;
					yPos = y * 18 + extrasY;
					this.addSlotToContainer(new SlotVehicleUpgrade(vehicle.inventory.upgradeInventory, vehicle, slotNum, xPos, yPos));
				}
			}
		}
		for (y = 0; y < 2; y++) {
			for (x = 0; x < 3; x++) {
				slotNum = y * 3 + x;
				if (slotNum < vehicle.inventory.armorInventory.getSizeInventory()) {
					xPos = 8 + x * 18 + 6 * 18 + 2 * 5;
					yPos = y * 18 + extrasY;
					this.addSlotToContainer(new SlotVehicleArmor(vehicle.inventory.armorInventory, vehicle, slotNum, xPos, yPos));
				}
			}
		}

		storageSlots = new Slot[vehicle.inventory.storageInventory.getSizeInventory()];
		for (y = 0; y < vehicle.inventory.storageInventory.getSizeInventory() / 9; y++) {
			for (x = 0; x < 9; x++) {
				slotNum = y * 9 + x;
				if (slotNum < vehicle.inventory.storageInventory.getSizeInventory()) {
					xPos = 8 + x * 18;
					yPos = y * 18 + storageY;
					if (slotNum >= 27) {
						xPos = -1000;
						yPos = -1000;
					}
					Slot slot = new Slot(vehicle.inventory.storageInventory, slotNum, xPos, yPos);
					storageSlots[slotNum] = slot;
					this.addSlotToContainer(slot);
				}
			}
		}
	}

	int currentTopStorageRow = 0;

	public void nextRow() {
		this.setCurrentTopStorageRow(currentTopStorageRow + 1);
	}

	public void prevRow() {
		this.setCurrentTopStorageRow(currentTopStorageRow - 1);
	}

	public void setCurrentTopStorageRow(int row) {
		if (row < 0 || row >= storageSlots.length / 9) {
			return;
		}
		currentTopStorageRow = row;
		int x;
		int y;
		int slotNum;
		int xPos;
		int yPos;

		int curRow = 0;

		for (y = 0; y < vehicle.inventory.storageInventory.getSizeInventory() / 9; y++) {
			for (x = 0; x < 9; x++) {
				slotNum = y * 9 + x;
				if (slotNum < vehicle.inventory.storageInventory.getSizeInventory()) {

					if (y < row || y >= row + 3) {
						xPos = -1000;
						yPos = -1000;
					} else {
						xPos = 8 + x * 18;
						yPos = storageY + curRow * 18;
					}
					storageSlots[slotNum].xDisplayPosition = xPos;
					storageSlots[slotNum].yDisplayPosition = yPos;
				}
			}
			if (y >= row && y < row + 3) {
				curRow++;
			}
		}
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int slotClickedIndex) {
		ItemStack slotStackCopy = null;
		Slot theSlot = (Slot) this.inventorySlots.get(slotClickedIndex);
		if (theSlot != null && theSlot.getHasStack()) {
			ItemStack slotStack = theSlot.getStack();
			slotStackCopy = slotStack.copy();
			int ammoSlots = vehicle.inventory.ammoInventory.getSizeInventory();
			int upgradeSlots = vehicle.inventory.upgradeInventory.getSizeInventory();
			int armorSlots = vehicle.inventory.armorInventory.getSizeInventory();
			int storageSlots = vehicle.inventory.storageInventory.getSizeInventory();
			if (slotClickedIndex < 36)//player slots...
			{
				if (slotStackCopy.itemID == ItemLoader.ammoItem.itemID && vehicle.inventory.isAmmoValid(slotStackCopy))//is ammo item...
				{
					if (!this.mergeItemStack(slotStack, 36, 36 + ammoSlots, false))//merge into ammo inventory
					{
						return null;
					}
				} else if (slotStackCopy.itemID == ItemLoader.vehicleUpgrade.itemID && vehicle.inventory.isUpgradeValid(slotStackCopy))//is upgrade item...
				{
					if (!this.mergeItemStack(slotStack, 36 + ammoSlots, 36 + ammoSlots + upgradeSlots, false))//merge into upgrade inventory
					{
						return null;
					}
				} else if (slotStackCopy.itemID == ItemLoader.armorItem.itemID && vehicle.inventory.isArmorValid(slotStackCopy)) {
					if (!this.mergeItemStack(slotStack, 36 + ammoSlots + upgradeSlots, 36 + ammoSlots + upgradeSlots + armorSlots,
							false))//merge into armor inventory
					{
						return null;
					}
				} else//attempt merge into storage inventory, if vehicle has one...
				{
					if (!this.mergeItemStack(slotStack, 36 + ammoSlots + upgradeSlots + armorSlots, 36 + ammoSlots + upgradeSlots + armorSlots + storageSlots,
							false))//merge into storage inventory
					{
						return null;
					}
				}
			} else if (slotClickedIndex >= 36 && slotClickedIndex < 36 + ammoSlots + upgradeSlots + armorSlots + storageSlots)//vehicle slots, merge to player inventory
			{
				if (!this.mergeItemStack(slotStack, 0, 36, true))//merge into player inventory
				{
					return null;
				}
			}
			if (slotStack.stackSize == 0) {
				theSlot.putStack((ItemStack) null);
			} else {
				theSlot.onSlotChanged();
			}
			if (slotStack.stackSize == slotStackCopy.stackSize) {
				return null;
			}
			theSlot.onPickupFromSlot(par1EntityPlayer, slotStack);
		}
		return slotStackCopy;
	}

	@Override
	public void handlePacketData(NBTTagCompound tag) {
	}

	@Override
	public void handleInitData(NBTTagCompound tag) {
	}

	@Override
	public List<NBTTagCompound> getInitData() {
		return Collections.emptyList();
	}

	@Override
	public boolean canInteractWith(EntityPlayer var1) {
		return super.canInteractWith(var1) && var1 != null && var1.getDistanceToEntity(vehicle) < 8.d;
	}

}
