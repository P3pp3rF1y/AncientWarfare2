package net.shadowmage.ancientwarfare.structure.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.network.PacketGui;
import net.shadowmage.ancientwarfare.structure.block.AWStructuresBlocks;
import net.shadowmage.ancientwarfare.structure.tile.SpawnerSettings;

import javax.annotation.Nonnull;

public class ContainerSpawnerAdvancedInventoryBase extends ContainerBase {

	SpawnerSettings settings;
	IItemHandler inventory;

	public ContainerSpawnerAdvancedInventoryBase(EntityPlayer player, int x, int y, int z) {
		super(player);
	}

	protected void addSettingsInventorySlots() {
		int xPos;
		int yPos;
		int slotNum;

		for (int y = 0; y < 3; y++) {
			yPos = y * 18 + 8;
			for (int x = 0; x < 3; x++) {
				xPos = x * 18 + 8;//TODO find offset
				slotNum = y * 3 + x;
				addSlotToContainer(new SlotItemHandler(inventory, slotNum, xPos, yPos) {
					@Override
					public boolean isItemValid(@Nonnull ItemStack stack) {
						if (stack.getItem() instanceof ItemBlock) {
							ItemBlock block = (ItemBlock) stack.getItem();
							if (block.getBlock() == AWStructuresBlocks.advancedSpawner) {
								return false;
							}
						}
						return true;
					}
				});
			}
		}
	}

	public void sendSettingsToServer() {
		NBTTagCompound tag = new NBTTagCompound();
		settings.writeToNBT(tag);

		PacketGui pkt = new PacketGui();
		pkt.setTag("spawnerSettings", tag);
		NetworkHandler.sendToServer(pkt);
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slotClickedIndex) {
		@Nonnull ItemStack slotStackCopy = ItemStack.EMPTY;
		Slot theSlot = this.getSlot(slotClickedIndex);
		if (theSlot != null && theSlot.getHasStack()) {
			@Nonnull ItemStack slotStack = theSlot.getStack();
			slotStackCopy = slotStack.copy();
			int playerSlotEnd = playerSlots;
			int storageSlots = playerSlotEnd + 9;
			if (slotClickedIndex < playerSlotEnd)//player slots...
			{
				if (!this.mergeItemStack(slotStack, playerSlotEnd, storageSlots, false))//merge into storage inventory
				{
					return ItemStack.EMPTY;
				}
			} else if (slotClickedIndex < storageSlots)//storage slots, merge to player inventory
			{
				if (!this.mergeItemStack(slotStack, 0, playerSlotEnd, true))//merge into player inventory
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
			theSlot.onTake(player, slotStack);
		}
		return slotStackCopy;
	}

}
