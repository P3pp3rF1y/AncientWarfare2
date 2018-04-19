package net.shadowmage.ancientwarfare.npc.trade;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.IItemHandler;

import java.util.ArrayList;
import java.util.List;

public class POTradeRestockData {
	private BlockPos withdrawPoint;
	private EnumFacing withdrawSide = EnumFacing.DOWN;
	private List<POTradeWithdrawEntry> withdrawList = new ArrayList<>();
	private BlockPos depositPoint;
	private EnumFacing depositSide = EnumFacing.UP;
	private List<POTradeDepositEntry> depositList = new ArrayList<>();

	public BlockPos getDepositPoint() {
		return depositPoint;
	}

	public BlockPos getWithdrawPoint() {
		return withdrawPoint;
	}

	public EnumFacing getDepositSide() {
		return depositSide;
	}

	public EnumFacing getWithdrawSide() {
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

	public void setDepositPoint(BlockPos pos, EnumFacing side) {
		depositPoint = pos;
		depositSide = side;
	}

	public void setWithdrawPoint(BlockPos pos, EnumFacing side) {
		withdrawPoint = pos;
		withdrawSide = side;
	}

	public void doDeposit(IItemHandler storage, IItemHandler deposit) {
		for (POTradeDepositEntry aDeposit : depositList) {
			aDeposit.process(storage, deposit);
		}
	}

	public void doWithdraw(IItemHandler storage, IItemHandler withdraw) {
		for (POTradeWithdrawEntry aWithdraw : withdrawList) {
			aWithdraw.process(storage, withdraw);
		}
	}

	public void readFromNBT(NBTTagCompound tag) {
		if (tag.hasKey("withdrawPoint")) {
			withdrawPoint = BlockPos.fromLong(tag.getLong("withdrawPoint"));
			withdrawSide = EnumFacing.VALUES[tag.getByte("withdrawSide")];
		}
		if (tag.hasKey("depositPoint")) {
			depositPoint = BlockPos.fromLong(tag.getLong("depositPoint"));
			depositSide = EnumFacing.VALUES[tag.getByte("depositSide")];
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
			tag.setLong("withdrawPoint", withdrawPoint.toLong());
			tag.setByte("withdrawSide", (byte) withdrawSide.ordinal());
		}
		if (depositPoint != null) {
			tag.setLong("depositPoint", depositPoint.toLong());
			tag.setByte("depositSide", (byte) depositSide.ordinal());
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
