package net.shadowmage.ancientwarfare.npc.trade;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.IItemHandler;
import net.shadowmage.ancientwarfare.core.util.OrderingList;

public abstract class TradeList<T extends Trade> extends OrderingList<T> implements INBTSerializable<NBTTagCompound> {
	public final void addNewTrade() {
		add(getNewTrade());
	}

	protected abstract T getNewTrade();

	public void performTrade(EntityPlayer player, IItemHandler storage, int integer) {
		get(integer).performTrade(player, storage);
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound tag = new NBTTagCompound();
		NBTTagList list = new NBTTagList();
		for (T aTrade : this.points) {
			list.appendTag(aTrade.writeToNBT(new NBTTagCompound()));
		}
		tag.setTag("tradeList", list);
		return tag;
	}

	@Override
	public void deserializeNBT(NBTTagCompound tag) {
		clear();
		NBTTagList list = tag.getTagList("tradeList", Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < list.tagCount(); i++) {
			T t = getNewTrade();
			t.readFromNBT(list.getCompoundTagAt(i));
			if (t.isValid()) {
				add(t);
			}
		}
	}
}
