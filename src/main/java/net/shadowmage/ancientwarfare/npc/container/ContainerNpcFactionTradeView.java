package net.shadowmage.ancientwarfare.npc.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcFactionTrader;
import net.shadowmage.ancientwarfare.npc.faction.FactionTracker;
import net.shadowmage.ancientwarfare.npc.registry.FactionRegistry;
import net.shadowmage.ancientwarfare.npc.registry.StandingChanges;
import net.shadowmage.ancientwarfare.npc.trade.FactionTradeList;

public class ContainerNpcFactionTradeView extends ContainerNpcBase<NpcFactionTrader> {
	private static final String DO_TRADE_TAG = "doTrade";
	private static final String TRADE_DATA_TAG = "tradeData";
	public final FactionTradeList tradeList;

	@SuppressWarnings("unused") //used in reflection
	public ContainerNpcFactionTradeView(EntityPlayer player, int x, int y, int z) {
		super(player, x);
		this.tradeList = entity.getTradeList();
		this.entity.startTrade(player);

		addPlayerSlots();
	}

	@Override
	public void sendInitData() {
		tradeList.updateTradesForView();
		NBTTagCompound packetTag = new NBTTagCompound();
		packetTag.setTag(TRADE_DATA_TAG, tradeList.serializeNBT());
		sendDataToClient(packetTag);
	}

	@Override
	public void handlePacketData(NBTTagCompound tag) {
		if (tag.hasKey(TRADE_DATA_TAG)) {
			tradeList.deserializeNBT(tag.getCompoundTag(TRADE_DATA_TAG));
		} else if (tag.hasKey(DO_TRADE_TAG) && tradeList.performTrade(player, tag.getInteger(DO_TRADE_TAG))) {
			FactionTracker.INSTANCE.adjustStandingFor(entity.world, player.getName(), entity.getFaction(), FactionRegistry.getFaction(entity.getFaction()).getStandingChange(StandingChanges.TRADE));
		}
		refreshGui();
	}

	@Override
	public void onContainerClosed(EntityPlayer player) {
		this.entity.closeTrade();
		super.onContainerClosed(player);
	}

	public void doTrade(int tradeNum) {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger(DO_TRADE_TAG, tradeNum);
		sendDataToServer(tag);
	}

	public World getWorld() {
		return player.world;
	}
}
