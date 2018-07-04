package net.shadowmage.ancientwarfare.structure.tile;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.ItemStackHandler;
import net.shadowmage.ancientwarfare.core.tile.TileUpdatable;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.structure.block.BlockStructureScanner;
import net.shadowmage.ancientwarfare.structure.item.AWStructuresItems;
import net.shadowmage.ancientwarfare.structure.item.ItemStructureScanner;
import net.shadowmage.ancientwarfare.structure.item.ItemStructureSettings;

import javax.annotation.Nonnull;

public class TileStructureScanner extends TileUpdatable {
	private ItemStackHandler scannerInventory = new ItemStackHandler(1) {
		@Nonnull
		@Override
		public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
			return stack.getItem() == AWStructuresItems.structureScanner ? super.insertItem(slot, stack, simulate) : stack;
		}

		@Override
		protected void onContentsChanged(int slot) {
			super.onContentsChanged(slot);

			ItemStack stack = getStackInSlot(slot);
			world.setBlockState(pos, world.getBlockState(pos).withProperty(BlockStructureScanner.FACING,
					stack.getItem() == AWStructuresItems.structureScanner && readyToExport(stack) ? EnumFacing.UP : facing));
		}

		private boolean readyToExport(ItemStack stack) {
			ItemStructureSettings scanSettings = ItemStructureSettings.getSettingsFor(stack);
			return ItemStructureScanner.readyToExport(scanSettings);
		}
	};
	private boolean boundsActive;
	private EnumFacing facing = EnumFacing.NORTH;

	public ItemStackHandler getScannerInventory() {
		return scannerInventory;
	}

	@Override
	protected void writeUpdateNBT(NBTTagCompound tag) {
		super.writeUpdateNBT(tag);
		tag.setTag("scannerInventory", scannerInventory.serializeNBT());
		tag.setBoolean("boundsActive", boundsActive);
		tag.setByte("facing", (byte) facing.ordinal());
	}

	@Override
	protected void handleUpdateNBT(NBTTagCompound tag) {
		super.handleUpdateNBT(tag);
		scannerInventory.deserializeNBT(tag.getCompoundTag("scannerInventory"));
		boundsActive = tag.getBoolean("boundsActive");
		facing = EnumFacing.VALUES[tag.getByte("facing")];
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		NBTTagCompound tag = super.writeToNBT(compound);
		tag.setTag("scannerInventory", scannerInventory.serializeNBT());
		tag.setBoolean("boundsActive", boundsActive);
		tag.setByte("facing", (byte) facing.ordinal());
		return tag;
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		scannerInventory.deserializeNBT(compound.getCompoundTag("scannerInventory"));
		boundsActive = compound.getBoolean("boundsActive");
		facing = EnumFacing.VALUES[compound.getByte("facing")];
	}

	public boolean getBoundsActive() {
		return boundsActive;
	}

	public void toggleBounds() {
		boundsActive = !boundsActive;
	}

	public void setFacing(EnumFacing facing) {
		this.facing = facing;
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		ItemStack scanner = scannerInventory.getStackInSlot(0);
		if (scanner.getItem() != AWStructuresItems.structureScanner) {
			return super.getRenderBoundingBox();
		}

		ItemStructureSettings settings = ItemStructureSettings.getSettingsFor(scanner);

		BlockPos min = BlockTools.getMin(settings.getPos1(), settings.getPos2());
		BlockPos max = BlockTools.getMax(settings.getPos1(), settings.getPos2());
		return new AxisAlignedBB(min, max);
	}
}
