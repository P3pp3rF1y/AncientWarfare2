package net.shadowmage.ancientwarfare.automation.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import net.shadowmage.ancientwarfare.automation.tile.warehouse2.TileWarehouseBase;
import net.shadowmage.ancientwarfare.core.container.ContainerTileBase;
import net.shadowmage.ancientwarfare.core.inventory.ItemQuantityMap;
import net.shadowmage.ancientwarfare.core.inventory.ItemQuantityMap.ItemHashEntry;

import javax.annotation.Nonnull;

public class ContainerWarehouseControl extends ContainerTileBase<TileWarehouseBase> {

	public ItemQuantityMap itemMap = new ItemQuantityMap();
	private final ItemQuantityMap cache = new ItemQuantityMap();
	private boolean shouldUpdate = true;
	public int maxStorage = 0;
	public int currentStored = 0;

	public ContainerWarehouseControl(EntityPlayer player, int x, int y, int z) {
		super(player, x, y, z);
		addPlayerSlots(142);
		tileEntity.addViewer(this);
	}

	@Override
	public void onContainerClosed(EntityPlayer par1EntityPlayer) {
		tileEntity.removeViewer(this);
		super.onContainerClosed(par1EntityPlayer);
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slotClickedIndex) {
		if (player.world.isRemote) {
			return ItemStack.EMPTY;
		}
		Slot slot = this.getSlot(slotClickedIndex);
		if (slot == null || !slot.getHasStack()) {
			return ItemStack.EMPTY;
		}
		@Nonnull ItemStack stack = slot.getStack();
		stack = tileEntity.tryAdd(stack);
		if (stack.isEmpty()) {
			slot.putStack(ItemStack.EMPTY);
		}
		detectAndSendChanges();
		return ItemStack.EMPTY;
	}

	@Override
	public void handlePacketData(NBTTagCompound tag) {
		if (tag.hasKey("slotClick")) {
			NBTTagCompound reqTag = tag.getCompoundTag("slotClick");
			@Nonnull ItemStack item = ItemStack.EMPTY;
			if (reqTag.hasKey("reqItem")) {
				item = new ItemStack(reqTag.getCompoundTag("reqItem"));
			}
			tileEntity.handleSlotClick(player, item, reqTag.getBoolean("isShiftClick"));
		} else if (tag.hasKey("changeList")) {
			handleChangeList(tag.getTagList("changeList", Constants.NBT.TAG_COMPOUND));
		} else if (tag.hasKey("maxStorage")) {
			maxStorage = tag.getInteger("maxStorage");
		}
		currentStored = itemMap.getTotalItemCount();
		refreshGui();
	}

	public void handleClientRequestSpecific(ItemStack stack, boolean isShiftClick) {
		NBTTagCompound tag = new NBTTagCompound();
		if (!stack.isEmpty()) {
			ItemStack copy = stack.copy();
			copy.setCount(Math.min(stack.getCount(), stack.getMaxStackSize()));
			tag.setTag("reqItem", copy.writeToNBT(new NBTTagCompound()));
		}
		tag.setBoolean("isShiftClick", isShiftClick);
		NBTTagCompound pktTag = new NBTTagCompound();
		pktTag.setTag("slotClick", tag);
		sendDataToServer(pktTag);
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		if (shouldUpdate) {
			synchItemMaps();
			shouldUpdate = false;
		}
		if (maxStorage != tileEntity.getMaxStorage()) {
			maxStorage = tileEntity.getMaxStorage();
			NBTTagCompound tag = new NBTTagCompound();
			tag.setInteger("maxStorage", maxStorage);
			sendDataToClient(tag);
		}
	}

	private void handleChangeList(NBTTagList changeList) {
		for (int i = 0; i < changeList.tagCount(); i++) {
			NBTTagCompound tag = changeList.getCompoundTagAt(i);
			itemMap.putEntryFromNBT(tag);
		}
	}

	private void synchItemMaps() {
		/*
		 *
         * need to loop through this.itemMap and compare quantities to tileEntity.itemMap
         *    add any changes to change-list
         * need to loop through tileEntity.itemMap and find new entries
         *    add any new entries to change-list
         */

		cache.clear();
		tileEntity.getItems(cache);
		ItemQuantityMap warehouseItemMap = cache;
		int qty;
		NBTTagList changeList = new NBTTagList();
		for (ItemHashEntry wrap : this.itemMap.keySet()) {
			qty = this.itemMap.getCount(wrap);
			if (qty != warehouseItemMap.getCount(wrap)) {
				qty = warehouseItemMap.getCount(wrap);
				changeList.appendTag(warehouseItemMap.writeEntryToNBT(wrap));
				this.itemMap.put(wrap, qty);
			}
		}
		for (ItemHashEntry entry : warehouseItemMap.keySet()) {
			if (!itemMap.contains(entry)) {
				qty = warehouseItemMap.getCount(entry);
				changeList.appendTag(warehouseItemMap.writeEntryToNBT(entry));
				this.itemMap.put(entry, qty);
			}
		}
		if (changeList.tagCount() > 0) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setTag("changeList", changeList);
			sendDataToClient(tag);
		}
	}

	public void onWarehouseInventoryUpdated() {
		shouldUpdate = true;
	}

}
