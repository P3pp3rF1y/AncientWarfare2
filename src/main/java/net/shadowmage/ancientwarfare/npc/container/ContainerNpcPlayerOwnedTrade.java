package net.shadowmage.ancientwarfare.npc.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.shadowmage.ancientwarfare.core.api.AWItems;
import net.shadowmage.ancientwarfare.core.inventory.InventoryBackpack;
import net.shadowmage.ancientwarfare.core.item.ItemBackpack;
import net.shadowmage.ancientwarfare.core.util.EntityTools;
import net.shadowmage.ancientwarfare.npc.entity.NpcTrader;
import net.shadowmage.ancientwarfare.npc.trade.TradeList;

public class ContainerNpcPlayerOwnedTrade extends ContainerNpcBase<NpcTrader> {

    private EnumHand hand;
    public TradeList tradeList;
    public final InventoryBackpack storage;

    public ContainerNpcPlayerOwnedTrade(EntityPlayer player, int x, int y, int z) {
        super(player, x);
        this.tradeList = entity.getTradeList();
        this.entity.startTrade(player);

        addPlayerSlots();
        this.hand = EntityTools.getHandHoldingItem(entity, AWItems.backpack);
        storage =  hand != null ? ItemBackpack.getInventoryFor(entity.getHeldItem(hand)) : null;
        if (storage != null) {
            for (int i = 0; i < storage.getSizeInventory(); i++) {
                /*
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
            NBTTagCompound packetTag = new NBTTagCompound();
            packetTag.setTag("tradeData", tradeList.serializeNBT());
            sendDataToClient(packetTag);
        }
    }

    @Override
    public void handlePacketData(NBTTagCompound tag) {
        if (tag.hasKey("tradeData")) {
            tradeList = new TradeList();
            tradeList.deserializeNBT(tag.getCompoundTag("tradeData"));
        }
        else if (tag.hasKey("doTrade")) {
            tradeList.performTrade(player, storage, tag.getInteger("doTrade"));
        }
        refreshGui();
    }

    @Override
    public void onContainerClosed(EntityPlayer player) {
        this.entity.closeTrade();
        if (storage != null) {
            ItemBackpack.writeBackpackToItem(storage, this.entity.getHeldItem(hand));
        }
        super.onContainerClosed(player);
    }

    public void doTrade(int tradeIndex) {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger("doTrade", tradeIndex);
        sendDataToServer(tag);
    }
}
