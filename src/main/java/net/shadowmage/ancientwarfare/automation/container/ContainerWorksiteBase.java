package net.shadowmage.ancientwarfare.automation.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.shadowmage.ancientwarfare.automation.tile.worksite.TileWorksiteBoundedInventory;
import net.shadowmage.ancientwarfare.core.container.ContainerTileBase;

import javax.annotation.Nonnull;

public class ContainerWorksiteBase<T extends TileWorksiteBoundedInventory> extends ContainerTileBase<T> {
	public int guiHeight, topLabel, frontLabel, bottomLabel, rearLabel, leftLabel, rightLabel, playerLabel;
	private int tileEntitySlots = 0;
	protected static final int LABEL_GAP = 12;

	public ContainerWorksiteBase(EntityPlayer player, int x, int y, int z) {
		super(player, x, y, z);

		int layerY = 8;

		topLabel = layerY;
		layerY += LABEL_GAP;
		addSlots(tileEntity.mainInventory, 8, layerY);
	}

	protected int addSlots(IItemHandler inventory, int xPosStart, int yPosStart) {
		int x1, y1, xPos, yPos;
		int maxY = 0;
		for (int i = 0, slotNum = 0; i < inventory.getSlots(); i++, slotNum++) {
			x1 = i % 9;
			y1 = i / 9;
			xPos = xPosStart + x1 * 18;
			yPos = yPosStart + y1 * 18;
			if (yPos + 18 > maxY) {
				maxY = yPos + 18;
			}
			SlotItemHandler slot = new SlotItemHandler(inventory, slotNum, xPos, yPos);
			addSlotToContainer(slot);
			tileEntitySlots++;
		}
		return maxY;
	}

	/*
	 * @return should always return null for normal implementation, not sure wtf the rest of the code is about
	 */
	@Override
	public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int slotClickedIndex) {
		Slot slot = this.getSlot(slotClickedIndex);
		if (slot == null || !slot.getHasStack()) {
			return ItemStack.EMPTY;
		}

		@Nonnull ItemStack stackFromSlot = slot.getStack();
		if (slotClickedIndex < tileEntitySlots) {
			this.mergeItemStack(stackFromSlot, tileEntitySlots, tileEntitySlots + playerSlots, false);
		} else {
			this.mergeItemStack(stackFromSlot, 0, tileEntitySlots, true);
		}
		if (stackFromSlot.getCount() == 0) {
			slot.putStack(ItemStack.EMPTY);
		} else {
			slot.onSlotChanged();
		}
		return ItemStack.EMPTY;
	}

}
