package net.shadowmage.ancientwarfare.npc.trade;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;
import net.shadowmage.ancientwarfare.npc.trade.POTradeRestockData.POTradeWithdrawType;

public class POTradeWithdrawEntry {
    private POTradeWithdrawType type = POTradeWithdrawType.ALL_OF;
    private ItemStack filter;

    public ItemStack getFilter() {
        return filter;
    }

    public void setFilter(ItemStack stack) {
        filter = stack;
    }

    public void setType(POTradeWithdrawType type) {
        this.type = type == null ? POTradeWithdrawType.ALL_OF : type;
    }

    public POTradeWithdrawType getType() {
        return type;
    }

    public void toggleType() {
        int o = type.ordinal();
        o++;
        if (o >= POTradeWithdrawType.values().length) {
            o = 0;
        }
        this.type = POTradeWithdrawType.values()[o];
    }

    public void process(IInventory storage, IInventory withdraw, int side) {
        if (filter == null) {
            return;
        }
        switch (type) {
            case ALL_OF: {
                int count = InventoryTools.getCountOf(withdraw, side, filter);
                if (count > 0) {
                    InventoryTools.transferItems(withdraw, storage, filter, count, side, -1);
                }
                break;
            }
            case FILL_TO: {
                int count = InventoryTools.getCountOf(storage, -1, filter);
                if (count < filter.stackSize) {
                    InventoryTools.transferItems(withdraw, storage, filter, filter.stackSize - count, side, -1);
                }
                break;
            }
        }
    }

    public void readFromNBT(NBTTagCompound tag) {
        if (tag.hasKey("item")) {
            filter = InventoryTools.readItemStack(tag.getCompoundTag("item"));
        }
        type = POTradeWithdrawType.values()[tag.getInteger("type")];
    }

    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        if (filter != null) {
            tag.setTag("item", InventoryTools.writeItemStack(filter, new NBTTagCompound()));
        }
        tag.setInteger("type", type.ordinal());
        return tag;
    }
}
