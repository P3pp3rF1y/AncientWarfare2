package net.shadowmage.ancientwarfare.core.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.SlotItemHandler;
import net.shadowmage.ancientwarfare.core.api.AWItems;
import net.shadowmage.ancientwarfare.core.util.EntityTools;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;

import javax.annotation.Nonnull;

public class ContainerBackpack extends ContainerBase {

	private ItemStack backpackStack;
	public final int backpackSlotIndex;
	public final EnumHand hand;
	public final int guiHeight;

	private final IItemHandler handler;

	public ContainerBackpack(EntityPlayer player, int x, int y, int z) {
		super(player);

        this.hand = EntityTools.getHandHoldingItem(player, AWItems.backpack);
		backpackStack = player.getHeldItem(hand);
		backpackSlotIndex = hand == EnumHand.MAIN_HAND ? player.inventory.currentItem : -1;

		handler = InventoryTools.cloneItemHandler(backpackStack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null));

		int xPos, yPos;
		for (int i = 0; i < handler.getSlots(); i++) {
			xPos = (i % 9) * 18 + 8;
			yPos = (i / 9) * 18 + 8;
			addSlotToContainer(new SlotItemHandler(handler, i, xPos, yPos) {
				@Override
				public boolean isItemValid(ItemStack itemStack) {
					return itemStack.getItem() != AWItems.backpack && super.isItemValid(itemStack);
				}
			});
		}
		int height = (backpackStack.getItemDamage() + 1) * 18 + 8;
		guiHeight = addPlayerSlots(height + 8) + 8;
	}

	@Override
	protected int addPlayerSlots(int tx, int ty, int gap) {
		int y;
		int x;
		int slotNum;
		int xPos;
		int yPos;
		IItemHandler playerInventory = player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP);
		for (x = 0; x < 9; ++x)//add player hotbar slots
		{
			slotNum = x;
			if (slotNum == backpackSlotIndex) {
				continue;
			}//TODO add fake slot in gui
			xPos = tx + x * 18;
			yPos = ty + gap + 3 * 18;
			this.addSlotToContainer(new SlotItemHandler(playerInventory, x, xPos, yPos));
		}
		for (y = 0; y < 3; ++y) {
			for (x = 0; x < 9; ++x) {
				slotNum = y * 9 + x + 9;// +9 is to increment past hotbar slots
				xPos = tx + x * 18;
				yPos = ty + y * 18;
				this.addSlotToContainer(new SlotItemHandler(playerInventory, slotNum, xPos, yPos));
			}
		}
		playerSlots = 35;
		return ty + (4 * 18) + gap;
	}

	@Override
	public void onContainerClosed(EntityPlayer playerIn) {
		IItemHandlerModifiable backpackHandler = (IItemHandlerModifiable) backpackStack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);

		for (int slot = 0; slot < handler.getSlots(); slot++) {
			backpackHandler.setStackInSlot(slot, handler.getStackInSlot(slot));
		}

		super.onContainerClosed(playerIn);
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int slotClickedIndex) {
		@Nonnull ItemStack slotStackCopy = ItemStack.EMPTY;
		Slot theSlot = this.getSlot(slotClickedIndex);
		int size = handler.getSlots();
		if (theSlot != null && theSlot.getHasStack()) {
			@Nonnull ItemStack slotStack = theSlot.getStack();
			slotStackCopy = slotStack.copy();
			if (slotClickedIndex < size)//clicked in backpack
			{
				if (!this.mergeItemStack(slotStack, size, size + playerSlots, false))//merge into player inventory
				{
					return ItemStack.EMPTY;
				}
			} else {
				if (!this.mergeItemStack(slotStack, 0, size, false))//merge into backpack
				{
					return ItemStack.EMPTY;
				}
			}
			if (slotStack.getCount() == 0) {
				theSlot.putStack(ItemStack.EMPTY);
			} else {
				theSlot.onSlotChanged();
			}
			if (slotStack.getCount() == slotStackCopy.getCount()) {
				return ItemStack.EMPTY;
			}
			theSlot.onTake(par1EntityPlayer, slotStack);
		}
		return slotStackCopy;
	}

}
