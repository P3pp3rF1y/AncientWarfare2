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

public class ContainerWarehouseControl extends ContainerTileBase<TileWarehouseBase> {

    public ItemQuantityMap itemMap = new ItemQuantityMap();
    public ItemQuantityMap cache = new ItemQuantityMap();
    boolean shouldUpdate = true;
    public int maxStorage = 0;
    public int currentStored = 0;


    public ContainerWarehouseControl(EntityPlayer player, int x, int y, int z) {
        super(player, x, y, z);
        addPlayerSlots(8, 142, 4);
        tileEntity.addViewer(this);
    }

    @Override
    public void onContainerClosed(EntityPlayer par1EntityPlayer) {
        tileEntity.removeViewer(this);
        super.onContainerClosed(par1EntityPlayer);
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

    @Override
    public void handlePacketData(NBTTagCompound tag) {
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
        }
        if (tag.hasKey("maxStorage")) {
            maxStorage = tag.getInteger("maxStorage");
        }
        currentStored = itemMap.getTotalItemCount();
        refreshGui();
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

    public void onWarehouseInventoryUpdated() {
        shouldUpdate = true;
    }

}
