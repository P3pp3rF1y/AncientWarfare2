package net.shadowmage.ancientwarfare.npc.trade;

import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;

import java.util.ArrayList;
import java.util.List;

public class POTradeRestockData {
    private BlockPosition withdrawPoint;
    private int withdrawSide;
    private List<POTradeWithdrawEntry> withdrawList = new ArrayList<POTradeWithdrawEntry>();
    private BlockPosition depositPoint;
    private int depositSide;
    private List<POTradeDepositEntry> depositList = new ArrayList<POTradeDepositEntry>();

    public BlockPosition getDepositPoint() {
        return depositPoint;
    }

    public BlockPosition getWithdrawPoint() {
        return withdrawPoint;
    }

    public int getDepositSide() {
        return depositSide;
    }

    public int getWithdrawSide() {
        return withdrawSide;
    }

    public void deleteDepositPoint() {
        depositPoint = null;
    }

    public void deleteWithdrawPoint() {
        withdrawPoint = null;
    }

    public List<POTradeWithdrawEntry> getWithdrawList() {
        return withdrawList;
    }

    public List<POTradeDepositEntry> getDepositList() {
        return depositList;
    }

    public void addDepositEntry() {
        depositList.add(new POTradeDepositEntry());
    }

    public void addWithdrawEntry() {
        withdrawList.add(new POTradeWithdrawEntry());
    }

    public void removeDepositEntry(int index) {
        depositList.remove(index);
    }

    public void removeWithdrawEntry(int index) {
        withdrawList.remove(index);
    }

    public void setDepositPoint(BlockPosition pos, int side) {
        depositPoint = pos;
        depositSide = side;
    }

    public void setWithdrawPoint(BlockPosition pos, int side) {
        withdrawPoint = pos;
        withdrawSide = side;
    }

    public void doDeposit(IInventory storage, IInventory deposit, int side) {
        for (int i = 0; i < depositList.size(); i++) {
            depositList.get(i).process(storage, deposit, side);
        }
    }

    public void doWithdraw(IInventory storage, IInventory withdraw, int side) {
        for (int i = 0; i < withdrawList.size(); i++) {
            withdrawList.get(i).process(storage, withdraw, side);
        }
    }

    public void readFromNBT(NBTTagCompound tag) {
        if (tag.hasKey("withdrawPoint")) {
            withdrawPoint = new BlockPosition(tag.getCompoundTag("withdrawPoint"));
            withdrawSide = tag.getInteger("withdrawSide");
        }
        if (tag.hasKey("depositPoint")) {
            depositPoint = new BlockPosition(tag.getCompoundTag("depositPoint"));
            depositSide = tag.getInteger("depositSide");
        }

        NBTTagList deposit = tag.getTagList("depositList", Constants.NBT.TAG_COMPOUND);
        POTradeDepositEntry de;
        for (int i = 0; i < deposit.tagCount(); i++) {
            de = new POTradeDepositEntry();
            de.readFromNBT(deposit.getCompoundTagAt(i));
            this.depositList.add(de);
        }

        NBTTagList withdraw = tag.getTagList("withdrawList", Constants.NBT.TAG_COMPOUND);
        POTradeWithdrawEntry we;
        for (int i = 0; i < withdraw.tagCount(); i++) {
            we = new POTradeWithdrawEntry();
            we.readFromNBT(withdraw.getCompoundTagAt(i));
            this.withdrawList.add(we);
        }
    }

    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        if (withdrawPoint != null) {
            tag.setTag("withdrawPoint", withdrawPoint.writeToNBT(new NBTTagCompound()));
            tag.setInteger("withdrawSide", withdrawSide);
        }
        if (depositPoint != null) {
            tag.setTag("depositPoint", depositPoint.writeToNBT(new NBTTagCompound()));
            tag.setInteger("depositSide", depositSide);
        }

        NBTTagList depositTagList = new NBTTagList();
        for (int i = 0; i < this.depositList.size(); i++) {
            depositTagList.appendTag(this.depositList.get(i).writeToNBT(new NBTTagCompound()));
        }
        tag.setTag("depositList", depositTagList);

        NBTTagList withdrawTagList = new NBTTagList();
        for (int i = 0; i < this.withdrawList.size(); i++) {
            withdrawTagList.appendTag(this.withdrawList.get(i).writeToNBT(new NBTTagCompound()));
        }
        tag.setTag("withdrawList", withdrawTagList);
        return tag;
    }

    public static enum POTradeWithdrawType {
        ALL_OF,
        FILL_TO
    }

    public static enum POTradeDepositType {
        ALL_OF,
        QUANTITY,
        DEPOSIT_EXCESS
    }


}
