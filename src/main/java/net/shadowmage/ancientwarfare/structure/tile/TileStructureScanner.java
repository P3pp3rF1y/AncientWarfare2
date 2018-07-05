package net.shadowmage.ancientwarfare.structure.tile;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.ItemStackHandler;
import net.shadowmage.ancientwarfare.core.tile.TileUpdatable;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.structure.item.AWStructuresItems;
import net.shadowmage.ancientwarfare.structure.item.ItemStructureScanner;
import net.shadowmage.ancientwarfare.structure.item.ItemStructureSettings;

import javax.annotation.Nonnull;

public class TileStructureScanner extends TileUpdatable {
	private static final String SCANNER_INVENTORY_TAG = "scannerInventory";
	private static final String BOUNDS_ACTIVE_TAG = "boundsActive";
	private static final String FACING_TAG = "facing";
	private ItemStackHandler scannerInventory = new ItemStackHandler(1) {
		@Nonnull
		@Override
		public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
			return stack.getItem() == AWStructuresItems.structureScanner ? super.insertItem(slot, stack, simulate) : stack;
		}

		@Override
		protected void onContentsChanged(int slot) {
			super.onContentsChanged(slot);

			if (!world.isRemote) {
				BlockTools.notifyBlockUpdate(TileStructureScanner.this);
			}
		}
	};

	private boolean boundsActive = true;
	private EnumFacing facing = EnumFacing.NORTH;
	private EnumFacing renderFacing = EnumFacing.NORTH;

	public ItemStackHandler getScannerInventory() {
		return scannerInventory;
	}

	@Override
	protected void writeUpdateNBT(NBTTagCompound tag) {
		super.writeUpdateNBT(tag);
		tag.setTag(SCANNER_INVENTORY_TAG, scannerInventory.serializeNBT());
		tag.setBoolean(BOUNDS_ACTIVE_TAG, boundsActive);
		tag.setByte(FACING_TAG, (byte) facing.ordinal());
	}

	private void updateRenderFacing() {
		ItemStack scanner = getScannerInventory().getStackInSlot(0);
		EnumFacing newRenderFacing = scanner.getItem() == AWStructuresItems.structureScanner &&
				ItemStructureScanner.readyToExport(scanner)
				? EnumFacing.UP : facing;

		if (newRenderFacing != renderFacing) {
			renderFacing = newRenderFacing;
			BlockTools.notifyBlockUpdate(this);
		}
	}

	@Override
	protected void handleUpdateNBT(NBTTagCompound tag) {
		super.handleUpdateNBT(tag);
		scannerInventory.deserializeNBT(tag.getCompoundTag(SCANNER_INVENTORY_TAG));
		boundsActive = tag.getBoolean(BOUNDS_ACTIVE_TAG);
		facing = EnumFacing.VALUES[tag.getByte(FACING_TAG)];
		updateRenderFacing();
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		NBTTagCompound tag = super.writeToNBT(compound);
		tag.setTag(SCANNER_INVENTORY_TAG, scannerInventory.serializeNBT());
		tag.setBoolean(BOUNDS_ACTIVE_TAG, boundsActive);
		tag.setByte(FACING_TAG, (byte) facing.ordinal());
		return tag;
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		scannerInventory.deserializeNBT(compound.getCompoundTag(SCANNER_INVENTORY_TAG));
		boundsActive = compound.getBoolean(BOUNDS_ACTIVE_TAG);
		facing = EnumFacing.VALUES[compound.getByte(FACING_TAG)];
	}

	public boolean getBoundsActive() {
		return boundsActive;
	}

	public void setBoundsActive(boolean boundsActive) {
		this.boundsActive = boundsActive;
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

		if (!settings.hasPos1() || !settings.hasPos2()) {
			return super.getRenderBoundingBox();
		}

		BlockPos min = BlockTools.getMin(settings.getPos1(), settings.getPos2());
		BlockPos max = BlockTools.getMax(settings.getPos1(), settings.getPos2());
		return new AxisAlignedBB(min, max);
	}

	public EnumFacing getRenderFacing() {
		return renderFacing;
	}
}
