package net.shadowmage.ancientwarfare.core.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.shadowmage.ancientwarfare.core.input.IItemKeyInterface;
import net.shadowmage.ancientwarfare.core.input.IItemKeyInterface.ItemAltFunction;
import net.shadowmage.ancientwarfare.core.input.IScrollableItem;

import javax.annotation.Nonnull;

public class PacketItemMouseScroll extends PacketBase {
	private boolean scrollUp;

	public PacketItemMouseScroll() {}

	public PacketItemMouseScroll(boolean scrollUp) {
		this.scrollUp = scrollUp;
	}

	@Override
	protected void writeToStream(ByteBuf data) {
		data.writeBoolean(scrollUp);
	}

	@Override
	protected void readFromStream(ByteBuf data) {
		scrollUp = data.readBoolean();
	}

	@Override
	protected void execute(EntityPlayer player) {
		ItemStack stack = player.getHeldItemMainhand();
		Item item = stack.getItem();
		if (item instanceof IScrollableItem){
			if (scrollUp) {
				((IScrollableItem) item).onScrollUp(player.world, player, stack);
			} else {
				((IScrollableItem) item).onScrollDown(player.world, player, stack);
			}
		}
	}
}
