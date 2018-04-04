package net.shadowmage.ancientwarfare.npc.orders;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;
import net.shadowmage.ancientwarfare.npc.item.ItemTradeOrder;
import net.shadowmage.ancientwarfare.npc.trade.POTradeRestockData;
import net.shadowmage.ancientwarfare.npc.trade.POTradeRoute;
import net.shadowmage.ancientwarfare.npc.trade.TradeList;

public class TradeOrder implements INBTSerializable<NBTTagCompound> {

	private POTradeRoute tradeRoute = new POTradeRoute();
	private POTradeRestockData restockEntry = new POTradeRestockData();
	private TradeList tradeList = new TradeList();

	public TradeOrder() {
	}

	public static TradeOrder getTradeOrder(ItemStack stack) {
		if (!stack.isEmpty() && stack.getItem() instanceof ItemTradeOrder) {
			TradeOrder order = new TradeOrder();
			if (stack.hasTagCompound() && stack.getTagCompound().hasKey("orders")) {
				order.deserializeNBT(stack.getTagCompound().getCompoundTag("orders"));
			}
			return order;
		}
		return null;
	}

	public void write(ItemStack stack) {
		if (!stack.isEmpty() && stack.getItem() instanceof ItemTradeOrder) {
			stack.setTagInfo("orders", serializeNBT());
		}
	}

	public TradeList getTradeList() {
		return tradeList;
	}

	public POTradeRoute getRoute() {
		return tradeRoute;
	}

	public POTradeRestockData getRestockData() {
		return restockEntry;
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setTag("tradeList", tradeList.serializeNBT());
		tag.setTag("tradeRoute", tradeRoute.writeToNBT(new NBTTagCompound()));
		tag.setTag("restockEntry", restockEntry.writeToNBT(new NBTTagCompound()));
		return tag;
	}

	@Override
	public void deserializeNBT(NBTTagCompound tag) {
		tradeList = new TradeList();
		tradeRoute = new POTradeRoute();
		restockEntry = new POTradeRestockData();
		tradeList.deserializeNBT(tag.getCompoundTag("tradeList"));
		tradeRoute.readFromNBT(tag.getCompoundTag("tradeRoute"));
		restockEntry.readFromNBT(tag.getCompoundTag("restockEntry"));
	}
}
