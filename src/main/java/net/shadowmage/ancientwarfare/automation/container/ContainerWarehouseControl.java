package net.shadowmage.ancientwarfare.automation.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import net.shadowmage.ancientwarfare.automation.tile.warehouse2.TileWarehouse;
import net.shadowmage.ancientwarfare.core.container.ContainerTileBase;
import net.shadowmage.ancientwarfare.core.inventory.ItemHashEntry;
import net.shadowmage.ancientwarfare.core.inventory.ItemQuantityMap;
import net.shadowmage.ancientwarfare.core.util.InventoryTools.ComparatorItemStack.SortOrder;
import net.shadowmage.ancientwarfare.core.util.InventoryTools.ComparatorItemStack.SortType;

import javax.annotation.Nonnull;

public class ContainerWarehouseControl extends ContainerTileBase<TileWarehouse> {
	private static final String SLOT_CLICK_TAG = "slotClick";
	private static final String REQ_ITEM_TAG = "reqItem";
	private static final String CHANGE_LIST_TAG = "changeList";
	private static final String MAX_STORAGE_TAG = "maxStorage";
	private static final String SORT_TYPE_TAG = "sortType";
	private static final String SORT_ORDER_TAG = "sortOrder";
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
		if (tag.hasKey(SLOT_CLICK_TAG)) {
			NBTTagCompound reqTag = tag.getCompoundTag(SLOT_CLICK_TAG);
			@Nonnull ItemStack item = ItemStack.EMPTY;
			if (reqTag.hasKey(REQ_ITEM_TAG)) {
				item = new ItemStack(reqTag.getCompoundTag(REQ_ITEM_TAG));
			}
			tileEntity.handleSlotClick(player, item, reqTag.getBoolean("isShiftClick"), reqTag.getBoolean("isRightClick"));
		} else if (tag.hasKey(CHANGE_LIST_TAG)) {
			handleChangeList(tag.getTagList(CHANGE_LIST_TAG, Constants.NBT.TAG_COMPOUND));
		} else {
			if (tag.hasKey(MAX_STORAGE_TAG)) {
				maxStorage = tag.getInteger(MAX_STORAGE_TAG);
			}
			if (tag.hasKey(SORT_TYPE_TAG)) {
				tileEntity.setSortType(SortType.values()[tag.getByte(SORT_TYPE_TAG)]);
			}
			if (tag.hasKey(SORT_ORDER_TAG)) {
				tileEntity.setSortOrder(SortOrder.values()[tag.getByte(SORT_ORDER_TAG)]);
			}
		}
		currentStored = itemMap.getTotalItemCount();
		refreshGui();
	}

	public void handleClientRequestSpecific(ItemStack stack, boolean isShiftClick, boolean isRightClick) {
		NBTTagCompound tag = new NBTTagCompound();
		if (!stack.isEmpty()) {
			ItemStack copy = stack.copy();
			copy.setCount(Math.min(stack.getCount(), stack.getMaxStackSize()));
			tag.setTag(REQ_ITEM_TAG, copy.writeToNBT(new NBTTagCompound()));
		}
		tag.setBoolean("isShiftClick", isShiftClick);
		tag.setBoolean("isRightClick", isRightClick);
		NBTTagCompound pktTag = new NBTTagCompound();
		pktTag.setTag(SLOT_CLICK_TAG, tag);
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
			tag.setInteger(MAX_STORAGE_TAG, maxStorage);
			tag.setByte(SORT_ORDER_TAG, (byte) getSortOrder().ordinal());
			tag.setByte(SORT_TYPE_TAG, (byte) getSortType().ordinal());
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
			tag.setTag(CHANGE_LIST_TAG, changeList);
			sendDataToClient(tag);
		}
	}

	public void onWarehouseInventoryUpdated() {
		shouldUpdate = true;
	}

	public SortType getSortType() {
		return tileEntity.getSortType();
	}

	public void setSortType(SortType sortType) {
		tileEntity.setSortType(sortType);
		NBTTagCompound tag = new NBTTagCompound();
		tag.setByte(SORT_TYPE_TAG, (byte) sortType.ordinal());
		sendDataToServer(tag);
	}

	public SortOrder getSortOrder() {
		return tileEntity.getSortOrder();
	}

	public void setSortOrder(SortOrder sortOrder) {
		tileEntity.setSortOrder(sortOrder);
		NBTTagCompound tag = new NBTTagCompound();
		tag.setByte(SORT_ORDER_TAG, (byte) sortOrder.ordinal());
		sendDataToServer(tag);
	}
}
