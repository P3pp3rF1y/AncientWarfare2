package net.shadowmage.ancientwarfare.structure.tile;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.items.ItemStackHandler;
import net.shadowmage.ancientwarfare.structure.item.AWStructuresItems;
import net.shadowmage.ancientwarfare.structure.item.ItemStructureScanner;
import net.shadowmage.ancientwarfare.structure.item.ItemStructureSettings;

import javax.annotation.Nonnull;

public class TileStructureScanner extends TileEntity {
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

	public ItemStackHandler getScannerInventory() {
		return scannerInventory;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		NBTTagCompound tag = super.writeToNBT(compound);
		tag.setTag("scannerInventory", scannerInventory.serializeNBT());
		return tag;
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		scannerInventory.deserializeNBT(compound.getCompoundTag("scannerInventory"));
	}
}
