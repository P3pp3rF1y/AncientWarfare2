package net.shadowmage.ancientwarfare.automation.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import net.shadowmage.ancientwarfare.automation.tile.warehouse2.IWarehouseStorageTile;
import net.shadowmage.ancientwarfare.automation.tile.warehouse2.TileControlled;
import net.shadowmage.ancientwarfare.automation.tile.warehouse2.TileWarehouseStorage;
import net.shadowmage.ancientwarfare.automation.tile.warehouse2.WarehouseStorageFilter;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.container.ContainerTileBase;
import net.shadowmage.ancientwarfare.core.inventory.ItemQuantityMap;
import net.shadowmage.ancientwarfare.core.inventory.ItemQuantityMap.ItemHashEntry;

import java.util.ArrayList;
import java.util.List;

public class ContainerWarehouseStorage extends ContainerTileBase<TileWarehouseStorage> {

    public int guiHeight;
    public int areaSize;
    int playerSlotsSize;
    int playerSlotsY;

    boolean shouldSynch = true;
    public ItemQuantityMap itemMap = new ItemQuantityMap();
    public ItemQuantityMap cache = new ItemQuantityMap();

    public List<WarehouseStorageFilter> filters = new ArrayList<WarehouseStorageFilter>();

    public ContainerWarehouseStorage(EntityPlayer player, int x, int y, int z) {
        super(player, x, y, z);
        tileEntity.addViewer(this);

        areaSize = 5 * 18 + 16;
        playerSlotsY = 148 + 8;
        playerSlotsSize = 8 + 4 + 4 * 18;
        guiHeight = playerSlotsY + playerSlotsSize;

        filters.addAll(tileEntity.getFilters());
        addPlayerSlots(8, playerSlotsY, 4);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slotClickedIndex) {
        if (player.worldObj.isRemote) {
            return null;
        }
        Slot slot = this.getSlot(slotClickedIndex);
        if (slot == null || !slot.getHasStack()) {
            return null;
        }
        ItemStack stack = slot.getStack();
        stack = tileEntity.tryAdd(stack);
        if (stack == null) {
            slot.putStack(null);
        }
        detectAndSendChanges();
        return null;
    }

    public void handleClientRequestSpecific(ItemStack stack, boolean isShiftClick) {
        NBTTagCompound tag = new NBTTagCompound();
        if (stack != null) {
            tag.setTag("reqItem", stack.writeToNBT(new NBTTagCompound()));
        }
        tag.setBoolean("isShiftClick", isShiftClick);
        NBTTagCompound pktTag = new NBTTagCompound();
        pktTag.setTag("slotClick", tag);
        sendDataToServer(pktTag);
    }

    @Override
    public void sendInitData() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setTag("filterList", WarehouseStorageFilter.writeFilterList(filters));
        sendDataToClient(tag);
    }

    public void sendFiltersToServer() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setTag("filterList", WarehouseStorageFilter.writeFilterList(filters));
        sendDataToServer(tag);
    }

    @Override
    public void handlePacketData(NBTTagCompound tag) {
        if (tag.hasKey("filterList")) {
            List<WarehouseStorageFilter> filters = WarehouseStorageFilter.readFilterList(tag.getTagList("filterList", Constants.NBT.TAG_COMPOUND), new ArrayList<WarehouseStorageFilter>());
            if (player.worldObj.isRemote) {
                this.filters.clear();
                this.filters.addAll(filters);
                refreshGui();
            } else {
                tileEntity.setFilters(filters);
            }
        }
        if (tag.hasKey("slotClick")) {
            NBTTagCompound reqTag = tag.getCompoundTag("slotClick");
            ItemStack item = null;
            if (reqTag.hasKey("reqItem")) {
                item = ItemStack.loadItemStackFromNBT(reqTag.getCompoundTag("reqItem"));
            }
            tileEntity.handleSlotClick(player, item, reqTag.getBoolean("isShiftClick"));
        }
        if (tag.hasKey("changeList")) {
            handleChangeList(tag.getTagList("changeList", Constants.NBT.TAG_COMPOUND));
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
        int qty;
        ItemHashEntry wrap = null;
        for (int i = 0; i < changeList.tagCount(); i++) {
            tag = changeList.getCompoundTagAt(i);
            wrap = ItemHashEntry.readFromNBT(tag);
            qty = tag.getInteger("qty");
            if (qty == 0) {
                itemMap.remove(wrap);
            } else {
                itemMap.put(wrap, qty);
            }
        }
    }

    private void synchItemMaps() {
        /**
         *
         * need to loop through this.itemMap and compare quantities to warehouse.itemMap
         *    add any changes to change-list
         * need to loop through warehouse.itemMap and find new entries
         *    add any new entries to change-list
         */

        cache.clear();
        tileEntity.addItems(cache);
        ItemQuantityMap warehouseItemMap = cache;
        int qty;
        NBTTagList changeList = new NBTTagList();
        NBTTagCompound tag;
        for (ItemHashEntry wrap : this.itemMap.keySet()) {
            qty = this.itemMap.getCount(wrap);
            if (qty != warehouseItemMap.getCount(wrap)) {
                qty = warehouseItemMap.getCount(wrap);
                tag = wrap.writeToNBT(new NBTTagCompound());
                tag.setInteger("qty", qty);
                changeList.appendTag(tag);
                this.itemMap.put(wrap, qty);
            }
        }
        for (ItemHashEntry entry : warehouseItemMap.keySet()) {
            if (!itemMap.contains(entry)) {
                qty = warehouseItemMap.getCount(entry);
                tag = ItemHashEntry.writeToNBT(entry, new NBTTagCompound());
                tag.setInteger("qty", qty);
                changeList.appendTag(tag);
                this.itemMap.put(entry, qty);
            }
        }
        if (changeList.tagCount() > 0) {
            tag = new NBTTagCompound();
            tag.setTag("changeList", changeList);
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
