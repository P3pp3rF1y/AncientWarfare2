package net.shadowmage.ancientwarfare.npc.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.util.EntityTools;
import net.shadowmage.ancientwarfare.npc.init.AWNPCItems;
import net.shadowmage.ancientwarfare.npc.orders.TradeOrder;

public class ContainerTradeOrder extends ContainerBase {

	private EnumHand hand;
	public final TradeOrder orders;

	public ContainerTradeOrder(EntityPlayer player, int x, int y, int z) {
		super(player);
		this.hand = EntityTools.getHandHoldingItem(player, AWNPCItems.TRADE_ORDER);
		orders = TradeOrder.getTradeOrder(player.getHeldItem(hand));

		addPlayerSlots((256 - (9 * 18)) / 2, 240 - 4 - 8 - 4 * 18, 4);
	}

	@Override
	public void handlePacketData(NBTTagCompound tag) {
		if (tag.hasKey("tradeOrder")) {
			orders.deserializeNBT(tag.getCompoundTag("tradeOrder"));
		}
	}

	@Override
	public void onContainerClosed(EntityPlayer par1EntityPlayer) {
		if (!player.world.isRemote) {
			orders.write(player.getHeldItem(hand));
		}
		super.onContainerClosed(par1EntityPlayer);
	}

	public void onClose() {
		NBTTagCompound outer = new NBTTagCompound();
		outer.setTag("tradeOrder", orders.serializeNBT());
		sendDataToServer(outer);
	}
}
