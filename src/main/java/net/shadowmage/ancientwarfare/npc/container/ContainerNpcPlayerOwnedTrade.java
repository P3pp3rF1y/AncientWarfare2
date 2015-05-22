package net.shadowmage.ancientwarfare.npc.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.nbt.NBTTagCompound;
import net.shadowmage.ancientwarfare.core.inventory.InventoryBackpack;
import net.shadowmage.ancientwarfare.core.item.ItemBackpack;
import net.shadowmage.ancientwarfare.npc.entity.NpcTrader;
import net.shadowmage.ancientwarfare.npc.trade.TradeList;

public class ContainerNpcPlayerOwnedTrade extends ContainerNpcBase<NpcTrader> {

    public TradeList tradeList;
    public final InventoryBackpack storage;

    public ContainerNpcPlayerOwnedTrade(EntityPlayer player, int x, int y, int z) {
        super(player, x);
        this.tradeList = entity.getTradeList();
        this.entity.startTrade(player);

        int startY = 240 - 4 - 8 - 4 * 18;
        addPlayerSlots(8, startY, 4);

        storage = ItemBackpack.getInventoryFor(entity.getHeldItem());
        if (storage != null) {
            for (int i = 0; i < storage.getSizeInventory(); i++) {
                /**
                 * add backpack items to slots in container so that they are synchronized to client side inventory/container
                 * --will be used to validate trades on client-side
                 */
                addSlotToContainer(new Slot(storage, i, 100000, 100000));
            }
        }
    }

    @Override
    public void sendInitData() {
        if (tradeList != null) {
            NBTTagCompound tag = new NBTTagCompound();
            tradeList.writeToNBT(tag);

            NBTTagCompound packetTag = new NBTTagCompound();
            packetTag.setTag("tradeData", tag);
            sendDataToClient(packetTag);
        }
    }

    @Override
    public void handlePacketData(NBTTagCompound tag) {
        if (tag.hasKey("tradeData")) {
            tradeList = new TradeList();
            tradeList.readFromNBT(tag.getCompoundTag("tradeData"));
        }
        if (tag.hasKey("doTrade")) {
            tradeList.performTrade(player, storage, tag.getInteger("doTrade"));
        }
        refreshGui();
    }

    @Override
    public void onContainerClosed(EntityPlayer player) {
        this.entity.closeTrade();
        if (storage != null) {
            ItemBackpack.writeBackpackToItem(storage, this.entity.getHeldItem());
        }
        super.onContainerClosed(player);
    }
}
