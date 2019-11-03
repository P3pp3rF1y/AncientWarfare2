package net.shadowmage.ancientwarfare.automation.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import net.shadowmage.ancientwarfare.automation.tile.warehouse2.TileWarehouseStorage;
import net.shadowmage.ancientwarfare.automation.tile.warehouse2.WarehouseStorageFilter;
import net.shadowmage.ancientwarfare.core.container.ContainerTileBase;
import net.shadowmage.ancientwarfare.core.inventory.ItemHashEntry;
import net.shadowmage.ancientwarfare.core.inventory.ItemQuantityMap;
import net.shadowmage.ancientwarfare.core.util.NBTHelper;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class ContainerWarehouseStorage extends ContainerTileBase<TileWarehouseStorage> {
	private static final String REQ_ITEM_TAG = "reqItem";
	private static final String IS_SHIFT_CLICK_TAG = "isShiftClick";
	private static final String IS_RIGHT_CLICK_TAG = "isRightClick";
	private static final String SLOT_CLICK_TAG = "slotClick";
	private static final String FILTER_LIST_TAG = "filterList";
	private static final String CHANGE_LIST_TAG = "changeList";
	private boolean shouldSynch = true;
	private ItemQuantityMap cache = new ItemQuantityMap();
	public ItemQuantityMap itemMap = new ItemQuantityMap();
	public List<WarehouseStorageFilter> filters = new ArrayList<>();

	public ContainerWarehouseStorage(EntityPlayer player, int x, int y, int z) {
		super(player, x, y, z);
		tileEntity.addViewer(this);

		filters.addAll(tileEntity.getFilters());
		addPlayerSlots(148 + 8);
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

	public void handleClientRequestSpecific(ItemStack stack, boolean isShiftClick, boolean isRightClick) {
		NBTTagCompound tag = new NBTTagCompound();
		if (!stack.isEmpty()) {
			ItemStack copy = stack.copy();
			copy.setCount(Math.min(stack.getCount(), stack.getMaxStackSize()));
			tag.setTag(REQ_ITEM_TAG, copy.writeToNBT(new NBTTagCompound()));
		}
		tag.setBoolean(IS_SHIFT_CLICK_TAG, isShiftClick);
		tag.setBoolean(IS_RIGHT_CLICK_TAG, isRightClick);
		NBTTagCompound pktTag = new NBTTagCompound();
		pktTag.setTag(SLOT_CLICK_TAG, tag);
		sendDataToServer(pktTag);
	}

	@Override
	public void sendInitData() {
		NBTTagCompound tag = new NBTTagCompound();
		NBTHelper.writeSerializablesTo(tag, FILTER_LIST_TAG, filters);
		sendDataToClient(tag);
	}

	public void sendFiltersToServer() {
		NBTTagCompound tag = new NBTTagCompound();
		NBTHelper.writeSerializablesTo(tag, FILTER_LIST_TAG, filters);
		sendDataToServer(tag);
	}

	@Override
	public void handlePacketData(NBTTagCompound tag) {
		if (tag.hasKey(FILTER_LIST_TAG)) {
			List<WarehouseStorageFilter> deserializedFilters = NBTHelper.deserializeListFrom(tag, FILTER_LIST_TAG, WarehouseStorageFilter::new);
			if (player.world.isRemote) {
				this.filters = deserializedFilters;
				refreshGui();
			} else {
				tileEntity.setFilters(deserializedFilters);
			}
		} else if (tag.hasKey(SLOT_CLICK_TAG)) {
			NBTTagCompound reqTag = tag.getCompoundTag(SLOT_CLICK_TAG);
			@Nonnull ItemStack item = ItemStack.EMPTY;
			if (reqTag.hasKey(REQ_ITEM_TAG)) {
				item = new ItemStack(reqTag.getCompoundTag(REQ_ITEM_TAG));
			}
			tileEntity.handleSlotClick(player, item, reqTag.getBoolean(IS_SHIFT_CLICK_TAG), reqTag.getBoolean(IS_RIGHT_CLICK_TAG));
		} else if (tag.hasKey(CHANGE_LIST_TAG)) {
			handleChangeList(tag.getTagList(CHANGE_LIST_TAG, Constants.NBT.TAG_COMPOUND));
			refreshGui();
		}
		super.handlePacketData(tag);
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		if (shouldSynch) {
			synchItemMaps();
			shouldSynch = false;
		}
	}

	private void handleChangeList(NBTTagList changeList) {
		NBTTagCompound tag;
		for (int i = 0; i < changeList.tagCount(); i++) {
			tag = changeList.getCompoundTagAt(i);
			itemMap.putEntryFromNBT(tag);
		}
	}

	private void synchItemMaps() {
		/*
		 *
         * need to loop through this.itemMap and compare quantities to warehouse.itemMap
         *    add any changes to change-list
         * need to loop through warehouse.itemMap and find new entries
         *    add any new entries to change-list
         */

		cache.clear();
		tileEntity.addItems(cache);
		ItemQuantityMap warehouseItemMap = cache;
		NBTTagList changeList = new NBTTagList();
		for (ItemHashEntry wrap : this.itemMap.keySet()) {
			int qty = this.itemMap.getCount(wrap);
			if (qty != warehouseItemMap.getCount(wrap)) {
				qty = warehouseItemMap.getCount(wrap);
				changeList.appendTag(warehouseItemMap.writeEntryToNBT(wrap));
				this.itemMap.put(wrap, qty);
			}
		}
		for (ItemHashEntry entry : warehouseItemMap.keySet()) {
			if (!itemMap.contains(entry)) {
				int qty = warehouseItemMap.getCount(entry);
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

	public void onStorageInventoryUpdated() {
		shouldSynch = true;
	}

	public void onFilterListUpdated() {
		this.filters.clear();
		this.filters.addAll(tileEntity.getFilters());
		sendInitData();
	}

	@Override
	public void onContainerClosed(EntityPlayer par1EntityPlayer) {
		tileEntity.removeViewer(this);
		super.onContainerClosed(par1EntityPlayer);
	}

}
