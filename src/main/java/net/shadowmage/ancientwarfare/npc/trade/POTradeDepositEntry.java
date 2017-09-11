package net.shadowmage.ancientwarfare.npc.trade;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;

public final class POTradeDepositEntry extends POTradeTransferEntry {

    @Override
    public TransferType getDefaultType() {
        return POTradeDepositType.ALL_OF;
    }

    @Override
    public void toggleType() {
        int o = getType().ordinal();
        o++;
        if (o >= POTradeDepositType.values().length) {
            o = 0;
        }
        setType(POTradeDepositType.values()[o]);
    }

    @Override
    protected TransferType getTypeFrom(int type) {
        return POTradeDepositType.values()[type];
    }

    public enum POTradeDepositType implements TransferType {
        ALL_OF {
            @Override
            public void doTransfer(IInventory storage, IInventory move, EnumFacing side, ItemStack filter) {
                int count = InventoryTools.getCountOf(storage, null, filter);
                if (count > 0) {
                    InventoryTools.transferItems(storage, move, filter, count, null, side);
                }
            }
        },
        QUANTITY {
            @Override
            public void doTransfer(IInventory storage, IInventory move, EnumFacing side, ItemStack filter) {
                int count = InventoryTools.getCountOf(storage, null, filter);
                if (count > filter.getCount()) {
                    count = filter.getCount();
                }
                InventoryTools.transferItems(storage, move, filter, count, null, side);
            }
        },
        DEPOSIT_EXCESS {
            @Override
            public void doTransfer(IInventory storage, IInventory move, EnumFacing side, ItemStack filter) {
                int count = InventoryTools.getCountOf(storage, null, filter);
                if (count > filter.getCount()) {
                    InventoryTools.transferItems(storage, move, filter, count - filter.getCount(), null, side);
                }
            }
        }
    }
}
