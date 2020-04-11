package net.shadowmage.ancientwarfare.structure.render;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.structure.event.IBoxRenderer;

import javax.annotation.Nonnull;

@SideOnly(Side.CLIENT)
public class StructureBoundingBoxRenderer {
	@SubscribeEvent
	public void handleRenderLastEvent(RenderWorldLastEvent evt) {
		Minecraft mc = Minecraft.getMinecraft();
		if (mc == null) {
			return;
		}
		EntityPlayer player = mc.player;
		if (player == null) {
			return;
		}
		for (EnumHand hand : EnumHand.values()) {
			@Nonnull ItemStack stack = player.getHeldItem(hand);
			Item item;
			if (stack.isEmpty() || (item = stack.getItem()) == Items.AIR) {
				return;
			}
			if (item instanceof IBoxRenderer) {
				((IBoxRenderer) item).renderBox(player, hand, stack, evt.getPartialTicks());
			}
		}
	}
}
