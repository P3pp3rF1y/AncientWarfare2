package net.shadowmage.ancientwarfare.npc.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcFactionTrader;
import net.shadowmage.ancientwarfare.npc.trade.FactionTradeList;

public class ContainerNpcFactionTradeView extends ContainerNpcBase<NpcFactionTrader> {

    public final FactionTradeList tradeList;

    public ContainerNpcFactionTradeView(EntityPlayer player, int x, int y, int z) {
        super(player, x);
        this.tradeList = entity.getTradeList();
        this.entity.startTrade(player);

        addPlayerSlots();
    }

    @Override
    public void sendInitData() {
        tradeList.updateTradesForView();
        NBTTagCompound tag = new NBTTagCompound();
        tradeList.writeToNBT(tag);

        NBTTagCompound packetTag = new NBTTagCompound();
        packetTag.setTag("tradeData", tag);
        sendDataToClient(packetTag);
    }

    @Override
    public void handlePacketData(NBTTagCompound tag) {
        if (tag.hasKey("tradeData")) {
            tradeList.readFromNBT(tag.getCompoundTag("tradeData"));
        }
        if (tag.hasKey("doTrade")) {
            tradeList.performTrade(player, tag.getInteger("doTrade"));
        }
        refreshGui();
    }

    @Override
    public void onContainerClosed(EntityPlayer player) {
        this.entity.closeTrade();
        super.onContainerClosed(player);
    }

}
