package net.shadowmage.ancientwarfare.core.input;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.network.PacketItemInteraction;

import javax.annotation.Nonnull;

@SideOnly(Side.CLIENT)
class ItemInputCallback implements IInputCallback {
	private final IItemKeyInterface.ItemAltFunction altFunction;

	ItemInputCallback(IItemKeyInterface.ItemAltFunction altFunction) {
		this.altFunction = altFunction;
	}

	@Override
	public void onKeyPressed() {
		Minecraft minecraft = Minecraft.getMinecraft();
		if (minecraft.currentScreen != null) {
			return;
		}
		if (!runAction(minecraft, EnumHand.MAIN_HAND)) {
			runAction(minecraft, EnumHand.OFF_HAND);
		}
	}

	private boolean runAction(Minecraft minecraft, EnumHand hand) {
		ItemStack stack = minecraft.player.getHeldItem(hand);
		if (stack.getItem() instanceof IItemKeyInterface && ((IItemKeyInterface) stack.getItem()).onKeyActionClient(minecraft.player, stack, altFunction)) {
			PacketItemInteraction pkt = new PacketItemInteraction(altFunction);
			NetworkHandler.sendToServer(pkt);
			return true;
		}
		return false;
	}
}
