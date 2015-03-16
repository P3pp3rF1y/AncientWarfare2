package net.shadowmage.ancientwarfare.npc.trade;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;
import net.shadowmage.ancientwarfare.npc.trade.POTradeRestockData.POTradeDepositType;

public class POTradeDepositEntry {
    private POTradeDepositType type = POTradeDepositType.ALL_OF;
    private ItemStack filter;

    public ItemStack getFilter() {
        return filter;
    }

    public void setFilter(ItemStack stack) {
        filter = stack;
    }

    public void setType(POTradeDepositType type) {
        this.type = type == null ? POTradeDepositType.ALL_OF : type;
    }

    public POTradeDepositType getType() {
        return type;
    }

    public void toggleType() {
        int o = type.ordinal();
        o++;
        if (o >= POTradeDepositType.values().length) {
            o = 0;
        }
        this.type = POTradeDepositType.values()[o];
    }

    public void process(IInventory storage, IInventory deposit, int side) {
        if (filter == null) {
            return;
        }
        switch (type) {
            case ALL_OF: {
                int count = InventoryTools.getCountOf(storage, -1, filter);
                if (count > 0) {
                    InventoryTools.transferItems(storage, deposit, filter, count, -1, side);
                }
                break;
            }
            case DEPOSIT_EXCESS: {
                int count = InventoryTools.getCountOf(storage, -1, filter);
                if (count > filter.stackSize) {
                    InventoryTools.transferItems(storage, deposit, filter, count - filter.stackSize, -1, side);
                }
                break;
            }
            case QUANTITY: {
                int count = InventoryTools.getCountOf(storage, -1, filter);
                count = count < filter.stackSize ? count : filter.stackSize;
                InventoryTools.transferItems(storage, deposit, filter, count, -1, side);
                break;
            }
        }
    }

    public void readFromNBT(NBTTagCompound tag) {
        if (tag.hasKey("item")) {
            filter = InventoryTools.readItemStack(tag.getCompoundTag("item"));
        }
        type = POTradeDepositType.values()[tag.getInteger("type")];
    }

    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        if (filter != null) {
            tag.setTag("item", InventoryTools.writeItemStack(filter));
        }
        tag.setInteger("type", type.ordinal());
        return tag;
    }
}
