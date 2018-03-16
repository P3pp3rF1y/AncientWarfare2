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

package net.shadowmage.ancientwarfare.vehicle.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.items.SlotItemHandler;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.vehicle.entity.IEntityContainerSynch;
import net.shadowmage.ancientwarfare.vehicle.entity.VehicleBase;
import net.shadowmage.ancientwarfare.vehicle.item.AWVehicleItems;
import net.shadowmage.ancientwarfare.vehicle.item.ItemAmmo;
import net.shadowmage.ancientwarfare.vehicle.item.ItemArmor;

import javax.annotation.Nonnull;

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
		super(openingPlayer);
		//TODO figure out what to do with synch param
		this.vehicle = vehicle;
		int y;
		int x;
		int slotNum;
		int xPos;
		int yPos;

		storageY = 4 + 10 + 20;
		int invHeight = (vehicle.inventory.storageInventory.getSlots() / 9 + (vehicle.inventory.storageInventory.getSlots() % 9 == 0 ? 0 : 1)) * 18;
		invHeight = invHeight > 3 * 18 ? 3 * 18 : invHeight;
		extrasY = storageY + (invHeight == 0 ? 0 : 10) + invHeight;
		playerY = extrasY + 4 + 10 + 2 * 18;
		this.addPlayerSlots(8, playerY, 4);

		for (y = 0; y < 2; y++) {
			for (x = 0; x < 3; x++) {
				slotNum = y * 3 + x;
				if (slotNum < vehicle.inventory.ammoInventory.getSlots()) {
					xPos = 8 + x * 18;
					yPos = y * 18 + extrasY;
					this.addSlotToContainer(new SlotItemHandler(vehicle.inventory.ammoInventory, slotNum, xPos, yPos));
				}
			}
		}
		for (y = 0; y < 2; y++) {
			for (x = 0; x < 3; x++) {

				slotNum = y * 3 + x;
				if (slotNum < vehicle.inventory.upgradeInventory.getSlots()) {
					xPos = 8 + x * 18 + 3 * 18 + 5;
					yPos = y * 18 + extrasY;
					this.addSlotToContainer(new SlotItemHandler(vehicle.inventory.upgradeInventory, slotNum, xPos, yPos));
				}
			}
		}
		for (y = 0; y < 2; y++) {
			for (x = 0; x < 3; x++) {
				slotNum = y * 3 + x;
				if (slotNum < vehicle.inventory.armorInventory.getSlots()) {
					xPos = 8 + x * 18 + 6 * 18 + 2 * 5;
					yPos = y * 18 + extrasY;
					this.addSlotToContainer(new SlotItemHandler(vehicle.inventory.armorInventory, slotNum, xPos, yPos));
				}
			}
		}

		storageSlots = new Slot[vehicle.inventory.storageInventory.getSlots()];
		for (y = 0; y < vehicle.inventory.storageInventory.getSlots() / 9; y++) {
			for (x = 0; x < 9; x++) {
				slotNum = y * 9 + x;
				if (slotNum < vehicle.inventory.storageInventory.getSlots()) {
					xPos = 8 + x * 18;
					yPos = y * 18 + storageY;
					if (slotNum >= 27) {
						xPos = -1000;
						yPos = -1000;
					}
					Slot slot = new SlotItemHandler(vehicle.inventory.storageInventory, slotNum, xPos, yPos);
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

		for (y = 0; y < vehicle.inventory.storageInventory.getSlots() / 9; y++) {
			for (x = 0; x < 9; x++) {
				slotNum = y * 9 + x;
				if (slotNum < vehicle.inventory.storageInventory.getSlots()) {

					if (y < row || y >= row + 3) {
						xPos = -1000;
						yPos = -1000;
					} else {
						xPos = 8 + x * 18;
						yPos = storageY + curRow * 18;
					}
					storageSlots[slotNum].xPos = xPos;
					storageSlots[slotNum].yPos = yPos;
				}
			}
			if (y >= row && y < row + 3) {
				curRow++;
			}
		}
	}

	@Nonnull
	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slotClickedIndex) {
		ItemStack slotStackCopy = ItemStack.EMPTY;
		Slot theSlot = this.inventorySlots.get(slotClickedIndex);
		if (theSlot != null && theSlot.getHasStack()) {
			ItemStack slotStack = theSlot.getStack();
			slotStackCopy = slotStack.copy();
			int ammoSlots = vehicle.inventory.ammoInventory.getSlots();
			int upgradeSlots = vehicle.inventory.upgradeInventory.getSlots();
			int armorSlots = vehicle.inventory.armorInventory.getSlots();
			int storageSlots = vehicle.inventory.storageInventory.getSlots();
			if (slotClickedIndex < 36)//player slots...
			{
				if (slotStackCopy.getItem() instanceof ItemAmmo) {
					if (!this.mergeItemStack(slotStack, 36, 36 + ammoSlots, false)) {
						return ItemStack.EMPTY;
					}
				} else if (slotStackCopy.getItem() == AWVehicleItems.upgrade) {
					if (!this.mergeItemStack(slotStack, 36 + ammoSlots, 36 + ammoSlots + upgradeSlots, false))//merge into upgrade inventory
					{
						return ItemStack.EMPTY;
					}
				} else if (slotStackCopy.getItem() instanceof ItemArmor) {
					if (!this.mergeItemStack(slotStack, 36 + ammoSlots + upgradeSlots, 36 + ammoSlots + upgradeSlots + armorSlots, false))//merge into armor inventory
					{
						return ItemStack.EMPTY;
					}
				} else//attempt merge into storage inventory, if vehicle has one...
				{
					if (!this.mergeItemStack(slotStack, 36 + ammoSlots + upgradeSlots + armorSlots, 36 + ammoSlots + upgradeSlots + armorSlots + storageSlots,
							false))//merge into storage inventory
					{
						return ItemStack.EMPTY;
					}
				}
			} else if (slotClickedIndex >= 36 && slotClickedIndex < 36 + ammoSlots + upgradeSlots + armorSlots + storageSlots)//vehicle slots, merge to player inventory
			{
				if (!this.mergeItemStack(slotStack, 0, 36, true))//merge into player inventory
				{
					return ItemStack.EMPTY;
				}
			}
			theSlot.onSlotChanged();

			if (slotStack.getCount() == slotStackCopy.getCount()) {
				return ItemStack.EMPTY;
			}
			theSlot.onTake(player, slotStack);
		}
		return slotStackCopy;
	}

	@Override
	public void handlePacketData(NBTTagCompound tag) {
	}

	@Override
	public boolean canInteractWith(EntityPlayer var1) {
		return super.canInteractWith(var1) && var1 != null && var1.getDistanceToEntity(vehicle) < 8.d;
	}

}
