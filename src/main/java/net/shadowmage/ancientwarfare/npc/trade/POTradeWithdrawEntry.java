package net.shadowmage.ancientwarfare.npc.trade;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;

public final class POTradeWithdrawEntry extends POTradeTransferEntry {

    @Override
    protected TransferType getDefaultType() {
        return POTradeWithdrawType.ALL_OF;
    }

    @Override
    public void toggleType() {
        int o = getType().ordinal();
        o++;
        if (o >= POTradeWithdrawType.values().length) {
            o = 0;
        }
        setType(getTypeFrom(o));
    }

    @Override
    protected TransferType getTypeFrom(int type) {
        return POTradeWithdrawType.values()[type];
    }

    public enum POTradeWithdrawType implements TransferType {
        ALL_OF {
            @Override
            public void doTransfer(IInventory storage, IInventory move, EnumFacing side, ItemStack filter) {
                int count = InventoryTools.getCountOf(move, side, filter);
                if (count > 0) {
                    InventoryTools.transferItems(move, storage, filter, count, side, null);
                }
            }
        },
        FILL_TO {
            @Override
            public void doTransfer(IInventory storage, IInventory move, EnumFacing side, ItemStack filter) {
                int count = InventoryTools.getCountOf(storage, null, filter);
                if (count < filter.getCount()) {
                    InventoryTools.transferItems(move, storage, filter, filter.getCount() - count, side, null);
                }
            }
        }
    }
}
