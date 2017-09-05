package net.shadowmage.ancientwarfare.npc.trade;

import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;

import java.util.ArrayList;
import java.util.List;

public class POTradeRestockData {
    private BlockPos withdrawPoint;
    private int withdrawSide;
    private List<POTradeWithdrawEntry> withdrawList = new ArrayList<>();
    private BlockPos depositPoint;
    private int depositSide;
    private List<POTradeDepositEntry> depositList = new ArrayList<>();

    public BlockPos getDepositPoint() {
        return depositPoint;
    }

    public BlockPos getWithdrawPoint() {
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

    public void setDepositPoint(BlockPos pos, int side) {
        depositPoint = pos;
        depositSide = side;
    }

    public void setWithdrawPoint(BlockPos pos, int side) {
        withdrawPoint = pos;
        withdrawSide = side;
    }

    public void doDeposit(IInventory storage, IInventory deposit, int side) {
        for (POTradeDepositEntry aDeposit : depositList) {
            aDeposit.process(storage, deposit, side);
        }
    }

    public void doWithdraw(IInventory storage, IInventory withdraw, int side) {
        for (POTradeWithdrawEntry aWithdraw : withdrawList) {
            aWithdraw.process(storage, withdraw, side);
        }
    }

    public void readFromNBT(NBTTagCompound tag) {
        if (tag.hasKey("withdrawPoint")) {
            withdrawPoint = new BlockPos(tag.getCompoundTag("withdrawPoint"));
            withdrawSide = tag.getInteger("withdrawSide");
        }
        if (tag.hasKey("depositPoint")) {
            depositPoint = new BlockPos(tag.getCompoundTag("depositPoint"));
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
        for (POTradeDepositEntry aDeposit : this.depositList) {
            depositTagList.appendTag(aDeposit.writeToNBT(new NBTTagCompound()));
        }
        tag.setTag("depositList", depositTagList);

        NBTTagList withdrawTagList = new NBTTagList();
        for (POTradeWithdrawEntry aWithdraw : this.withdrawList) {
            withdrawTagList.appendTag(aWithdraw.writeToNBT(new NBTTagCompound()));
        }
        tag.setTag("withdrawList", withdrawTagList);
        return tag;
    }
}
