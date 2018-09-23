package net.shadowmage.ancientwarfare.npc.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.shadowmage.ancientwarfare.core.init.AWCoreItems;
import net.shadowmage.ancientwarfare.core.util.EntityTools;
import net.shadowmage.ancientwarfare.npc.entity.NpcTrader;
import net.shadowmage.ancientwarfare.npc.trade.TradeList;

public class ContainerNpcPlayerOwnedTrade extends ContainerNpcBase<NpcTrader> {

	private EnumHand hand;
	public TradeList tradeList;
	public final IItemHandler storage;

	public ContainerNpcPlayerOwnedTrade(EntityPlayer player, int x, int y, int z) {
		super(player, x);
		this.tradeList = entity.getTradeList();
		this.entity.startTrade(player);

		addPlayerSlots();
		this.hand = EntityTools.getHandHoldingItem(entity, AWCoreItems.BACKPACK);
		storage = hand != null ? entity.getHeldItem(hand).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null) : null;
		if (storage != null) {
			for (int i = 0; i < storage.getSlots(); i++) {
				/*
				 * add backpack items to slots in container so that they are synchronized to client side inventory/container
                 * --will be used to validate trades on client-side
                 */
				addSlotToContainer(new SlotItemHandler(storage, i, 100000, 100000));
			}
		}
	}

	@Override
	public void sendInitData() {
		if (tradeList != null) {
			NBTTagCompound packetTag = new NBTTagCompound();
			packetTag.setTag("tradeData", tradeList.serializeNBT());
			sendDataToClient(packetTag);
		}
	}

	@Override
	public void handlePacketData(NBTTagCompound tag) {
		if (tag.hasKey("tradeData")) {
			tradeList = new TradeList();
			tradeList.deserializeNBT(tag.getCompoundTag("tradeData"));
		} else if (tag.hasKey("doTrade")) {
			tradeList.performTrade(player, storage, tag.getInteger("doTrade"));
		}
		refreshGui();
	}

	@Override
	public void onContainerClosed(EntityPlayer player) {
		this.entity.closeTrade();
		super.onContainerClosed(player);
	}

	public void doTrade(int tradeIndex) {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger("doTrade", tradeIndex);
		sendDataToServer(tag);
	}
}
