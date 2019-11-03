package net.shadowmage.ancientwarfare.core.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.shadowmage.ancientwarfare.core.interfaces.IItemKeyInterface;
import net.shadowmage.ancientwarfare.core.interfaces.IItemKeyInterface.ItemAltFunction;

import javax.annotation.Nonnull;

public class PacketItemInteraction extends PacketBase {

	private byte altFunction;

	public PacketItemInteraction() {}

	public PacketItemInteraction(ItemAltFunction altFunction) {
		this.altFunction = (byte) altFunction.ordinal();
	}

	@Override
	protected void writeToStream(ByteBuf data) {
		data.writeByte(altFunction);
	}

	@Override
	protected void readFromStream(ByteBuf data) {
		altFunction = data.readByte();
	}

	@Override
	protected void execute(EntityPlayer player) {
		if (!executeKeyPress(player, EnumHand.MAIN_HAND)) {
			executeKeyPress(player, EnumHand.OFF_HAND);
		}
	}

	private boolean executeKeyPress(EntityPlayer player, EnumHand hand) {
		@Nonnull ItemStack stack = player.getHeldItem(hand);
		if (stack.isEmpty()) {
			return false;
		}

		if (altFunction >= 0 && altFunction < ItemAltFunction.values().length && stack.getItem() instanceof IItemKeyInterface) {
			((IItemKeyInterface) stack.getItem()).onKeyAction(player, stack, ItemAltFunction.values()[altFunction]);
			return true;
		}
		return false;
	}

}
