package net.shadowmage.ancientwarfare.structure.render;

import net.minecraft.client.Minecraft;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.shadowmage.ancientwarfare.core.util.RenderTools;
import net.shadowmage.ancientwarfare.structure.util.BlockHighlightInfo;

import java.awt.*;

public class BlockHighlightRenderer {
	private static BlockHighlightInfo blockHighlightInfo = BlockHighlightInfo.EXPIRED;

	public static void setBlockHighlightInfo(BlockHighlightInfo blockHighlightInfo) {
		BlockHighlightRenderer.blockHighlightInfo = blockHighlightInfo;
	}

	@SuppressWarnings("unused")
	@SubscribeEvent
	public void handleRenderLastEvent(RenderWorldLastEvent evt) {
		Minecraft mc = Minecraft.getMinecraft();
		if (mc.world.getTotalWorldTime() < blockHighlightInfo.getExpirationTime()) {
			AxisAlignedBB bb = new AxisAlignedBB(blockHighlightInfo.getPos()).expand(0.1, 0.1, 0.1);
			bb = RenderTools.adjustBBForPlayerPos(bb, Minecraft.getMinecraft().player, evt.getPartialTicks());
			RenderTools.drawOutlinedBoundingBox(bb, Color.WHITE, true);
		}
	}
}
