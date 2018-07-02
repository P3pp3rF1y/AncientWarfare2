package net.shadowmage.ancientwarfare.structure.tile;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.items.ItemStackHandler;
import net.shadowmage.ancientwarfare.core.tile.TileUpdatable;
import net.shadowmage.ancientwarfare.structure.item.AWStructuresItems;
import net.shadowmage.ancientwarfare.structure.item.ItemStructureScanner;
import net.shadowmage.ancientwarfare.structure.item.ItemStructureSettings;

import javax.annotation.Nonnull;

public class TileStructureScanner extends TileUpdatable {
	private ItemStackHandler scannerInventory = new ItemStackHandler(1) {
		@Nonnull
		@Override
		public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
			return stack.getItem() == AWStructuresItems.structureScanner && readyToExport(stack) ? super.insertItem(slot, stack, simulate) : stack;
		}

		private boolean readyToExport(ItemStack stack) {
			ItemStructureSettings scanSettings = ItemStructureSettings.getSettingsFor(stack);
			return ItemStructureScanner.readyToExport(scanSettings);
		}
	};
	private boolean boundsActive;

	public ItemStackHandler getScannerInventory() {
		return scannerInventory;
	}

	@Override
	protected void writeUpdateNBT(NBTTagCompound tag) {
		super.writeUpdateNBT(tag);
		tag.setTag("scannerInventory", scannerInventory.serializeNBT());
		tag.setBoolean("boundsActive", boundsActive);
	}

	@Override
	protected void handleUpdateNBT(NBTTagCompound tag) {
		super.handleUpdateNBT(tag);
		scannerInventory.deserializeNBT(tag.getCompoundTag("scannerInventory"));
		boundsActive = tag.getBoolean("boundsActive");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		NBTTagCompound tag = super.writeToNBT(compound);
		tag.setTag("scannerInventory", scannerInventory.serializeNBT());
		tag.setBoolean("boundsActive", boundsActive);
		return tag;
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		scannerInventory.deserializeNBT(compound.getCompoundTag("scannerInventory"));
		boundsActive = compound.getBoolean("boundsActive");
	}

	public boolean getBoundsActive() {
		return boundsActive;
	}

	public void toggleBounds() {
		boundsActive = !boundsActive;
	}
}
