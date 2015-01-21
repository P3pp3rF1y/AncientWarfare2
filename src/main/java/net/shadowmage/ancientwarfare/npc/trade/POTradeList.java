package net.shadowmage.ancientwarfare.npc.trade;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;

import java.util.ArrayList;
import java.util.List;

public class POTradeList {

    List<POTrade> tradeList = new ArrayList<POTrade>();

    public POTradeList() {
    }

    public void decrementTrade(int index) {
        if (index <= 0 || index >= tradeList.size()) {
            return;
        }
        POTrade t = tradeList.remove(index);
        tradeList.add(index - 1, t);
    }

    public void incrementTrade(int index) {
        if (index < 0 || index >= tradeList.size() - 1) {
            return;
        }
        POTrade t = tradeList.remove(index);
        tradeList.add(index + 1, t);
    }

    public void deleteTrade(int index) {
        if (index < 0 || index >= tradeList.size()) {
            return;
        }
        tradeList.remove(index);
    }

    public void addNewTrade() {
        tradeList.add(new POTrade());
    }

    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        NBTTagList list = new NBTTagList();
        for (int i = 0; i < this.tradeList.size(); i++) {
            list.appendTag(this.tradeList.get(i).writeToNBT(new NBTTagCompound()));
        }
        tag.setTag("tradeList", list);
        return tag;
    }

    public void readFromNBT(NBTTagCompound tag) {
        tradeList.clear();
        NBTTagList list = tag.getTagList("tradeList", Constants.NBT.TAG_COMPOUND);
        POTrade t;
        for (int i = 0; i < list.tagCount(); i++) {
            t = new POTrade();
            t.readFromNBT(list.getCompoundTagAt(i));
            tradeList.add(t);
        }
    }

    public void getTrades(List<POTrade> trades) {
        trades.addAll(tradeList);
    }

    public void performTrade(EntityPlayer player, IInventory tradeInput, IInventory storage, int integer) {
        tradeList.get(integer).perfromTrade(player, tradeInput, storage);
    }

}
