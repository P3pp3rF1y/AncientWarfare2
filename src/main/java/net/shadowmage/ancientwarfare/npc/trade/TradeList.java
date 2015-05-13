package net.shadowmage.ancientwarfare.npc.trade;

import com.google.common.collect.ImmutableList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Olivier on 12/05/2015.
 */
public class TradeList {

    protected final List<Trade> tradeList = new ArrayList<Trade>();

    public final void decrementTrade(int index) {
        if (index <= 0 || index >= tradeList.size()) {
            return;
        }
        Trade t = tradeList.remove(index);
        tradeList.add(index - 1, t);
    }

    public final void incrementTrade(int index) {
        if (index < 0 || index >= tradeList.size() - 1) {
            return;
        }
        Trade t = tradeList.remove(index);
        tradeList.add(index + 1, t);
    }

    public final void deleteTrade(int index) {
        if (index < 0 || index >= tradeList.size()) {
            return;
        }
        tradeList.remove(index);
    }

    public final void addNewTrade() {
        tradeList.add(getNewTrade());
    }

    protected Trade getNewTrade(){
        return new POTrade();
    }

    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        NBTTagList list = new NBTTagList();
        for (Trade aTrade : this.tradeList) {
            list.appendTag(aTrade.writeToNBT(new NBTTagCompound()));
        }
        tag.setTag("tradeList", list);
        return tag;
    }

    public void readFromNBT(NBTTagCompound tag) {
        tradeList.clear();
        NBTTagList list = tag.getTagList("tradeList", Constants.NBT.TAG_COMPOUND);
        Trade t;
        for (int i = 0; i < list.tagCount(); i++) {
            t = getNewTrade();
            t.readFromNBT(list.getCompoundTagAt(i));
            tradeList.add(t);
        }
    }

    public final List<Trade> getTrades() {
        return ImmutableList.copyOf(tradeList);
    }

    public void performTrade(EntityPlayer player, IInventory storage, int integer) {
        tradeList.get(integer).performTrade(player, storage);
    }
}
