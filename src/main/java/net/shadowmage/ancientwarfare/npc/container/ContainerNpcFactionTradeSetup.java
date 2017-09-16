package net.shadowmage.ancientwarfare.npc.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcFactionTrader;
import net.shadowmage.ancientwarfare.npc.trade.FactionTradeList;

public class ContainerNpcFactionTradeSetup extends ContainerNpcBase<NpcFactionTrader> {

    public final FactionTradeList tradeList;
    public boolean tradesChanged = false;

    public ContainerNpcFactionTradeSetup(EntityPlayer player, int x, int y, int z) {
        super(player, x);
        this.tradeList = entity.getTradeList();
        this.entity.startTrade(player);

        addPlayerSlots();
    }

    @Override
    public void sendInitData() {
        tradeList.updateTradesForView();
        NBTTagCompound packetTag = new NBTTagCompound();
        packetTag.setTag("tradeData", tradeList.serializeNBT());
        sendDataToClient(packetTag);
    }

    @Override
    public void handlePacketData(NBTTagCompound tag) {
        if (tag.hasKey("tradeData")) {
            tradeList.deserializeNBT(tag.getCompoundTag("tradeData"));
        }
        refreshGui();
    }

    @Override
    public void onContainerClosed(EntityPlayer p_75134_1_) {
        this.entity.closeTrade();
        super.onContainerClosed(p_75134_1_);
    }

    public void onGuiClosed() {
        if (player.world.isRemote && tradesChanged) {
            tradeList.removeEmptyTrades();
            NBTTagCompound packetTag = new NBTTagCompound();
            packetTag.setTag("tradeData", tradeList.serializeNBT());
            sendDataToServer(packetTag);
        }
    }

}
