package net.shadowmage.ancientwarfare.npc.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.shadowmage.ancientwarfare.core.util.RenderTools;
import net.shadowmage.ancientwarfare.npc.config.AWNPCStatics;
import net.shadowmage.ancientwarfare.npc.item.ItemOrders;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public final class RenderWorkLines {

	public static final RenderWorkLines INSTANCE = new RenderWorkLines();

	private final List<BlockPos> positionList;

	private RenderWorkLines() {
		positionList = new ArrayList<>();
	}

	@SubscribeEvent
	public void renderLastEvent(RenderWorldLastEvent evt) {
		boolean render = AWNPCStatics.renderWorkPoints.getBoolean();
		if (!render) {
			return;
		}
		Minecraft mc = Minecraft.getMinecraft();
		if (mc == null) {
			return;
		}
		EntityPlayer player = mc.player;
		if (player == null) {
			return;
		}
		@Nonnull ItemStack stack = player.getHeldItemMainhand();
		Item item = stack.getItem();
		if (stack.isEmpty() || !(stack.getItem() instanceof ItemOrders)) {
			stack = player.getHeldItemOffhand();
			item = stack.getItem();
		}
		if (stack.isEmpty() || !(stack.getItem() instanceof ItemOrders)) {
			return;
		}

		GlStateManager.color(1.f, 1.f, 1.f, 1.f);
		positionList.addAll(((ItemOrders) item).getPositionsForRender(stack));

		if (positionList.size() > 0) {
			renderListOfPoints(player, evt.getPartialTicks());
			positionList.clear();
		}
	}

	private void renderListOfPoints(EntityPlayer player, float partialTick) {
		AxisAlignedBB bb = new AxisAlignedBB(0, 0, 0, 1, 1, 1);
		BlockPos prev = null;
		int index = 1;
		for (BlockPos point : positionList) {
			bb = new AxisAlignedBB(0, 0, 0, 1, 1, 1);
			bb = bb.offset(point.getX(), point.getY(), point.getZ());
			bb = RenderTools.adjustBBForPlayerPos(bb, player, partialTick);
			RenderTools.drawOutlinedBoundingBox(bb, 1.f, 1.f, 1.f);
			renderTextAt(player, point.getX() + 0.5d, point.getY() + 1.5d, point.getZ() + 0.5d, String.valueOf(index), partialTick);
			if (prev != null) {
				renderLineBetween(player, point.getX() + 0.5d, point.getY() + 0.5d, point.getZ() + 0.5d, prev.getX() + 0.5d, prev.getY() + 0.5d,
						prev.getZ() + 0.5d, partialTick);
			}
			prev = point;
			index++;
		}
	}

	private void renderLineBetween(EntityPlayer player, double x1, double y1, double z1, double x2, double y2, double z2, float partialTick) {
		double ox = RenderTools.getRenderOffsetX(player, partialTick);
		double oy = RenderTools.getRenderOffsetY(player, partialTick);
		double oz = RenderTools.getRenderOffsetZ(player, partialTick);
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GlStateManager.color(1.f, 1.f, 1.f, 0.4F);
		GlStateManager.disableTexture2D();
		GlStateManager.disableLighting();
		GlStateManager.glBegin(GL11.GL_LINE_LOOP);
		GL11.glVertex3d(x1 - ox, y1 - oy, z1 - oz);
		GL11.glVertex3d(x2 - ox, y2 - oy, z2 - oz);
		GlStateManager.glEnd();
		GlStateManager.enableTexture2D();
		GlStateManager.disableBlend();
	}

	private void renderTextAt(EntityPlayer player, double x, double y, double z, String text, float partialTick) {
		double ox = RenderTools.getRenderOffsetX(player, partialTick);
		double oy = RenderTools.getRenderOffsetY(player, partialTick);
		double oz = RenderTools.getRenderOffsetZ(player, partialTick);
		x -= ox;
		y -= oy;
		z -= oz;
		float f = 1.6F;
		float f1 = 0.016666668F * f;
		GlStateManager.pushMatrix();
		GlStateManager.translate((float) x, (float) y, (float) z);
		GlStateManager.rotate(-player.rotationYaw, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotate(player.rotationPitch, 1.0F, 0.0F, 0.0F);
		GlStateManager.scale(-f1, -f1, f1);
		GlStateManager.disableLighting();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		Minecraft.getMinecraft().fontRenderer.drawString(text, 0, 0, 0xffffffff);
		GlStateManager.popMatrix();
	}

}
