package net.shadowmage.ancientwarfare.npc.trade;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

import java.util.Iterator;

public final class FactionTradeList extends TradeList {

    int ticks = 0;

    @Override
    protected Trade getNewTrade() {
        return new FactionTrade();
    }

    /**
     * MUST be called from owning entity once per update tick.
     */
    public void tick() {
        ticks++;
    }

    /**
     * Should be called on server PRIOR to opening the trades GUI/container.<br>
     * Will use the internal stored tick number value for updating the trades list.<br>
     */
    public void updateTradesForView() {
        for (Trade aTrade : points) {
            ((FactionTrade) aTrade).updateTrade(ticks);
        }
        ticks = 0;
    }

    /**
     * removes any trades that have no input or output items.<br>
     * should be called before the changed list is sent from client->server from setup GUI.
     */
    public void removeEmptyTrades() {
        Iterator<Trade> it = points.iterator();
        Trade t;
        while (it.hasNext() && (t = it.next()) != null) {
            if (!((FactionTrade) t).hasItems()) {
                it.remove();
            }
        }
    }

    public void performTrade(EntityPlayer player, int tradeNum) {
        get(tradeNum).performTrade(player, null);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        tag.setInteger("ticks", ticks);
        return super.writeToNBT(tag);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        ticks = tag.getInteger("ticks");
        super.readFromNBT(tag);
    }
}
