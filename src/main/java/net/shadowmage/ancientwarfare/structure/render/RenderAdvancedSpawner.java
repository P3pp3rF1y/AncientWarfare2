package net.shadowmage.ancientwarfare.structure.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.world.GameType;
import net.shadowmage.ancientwarfare.structure.tile.SpawnerSettings;
import net.shadowmage.ancientwarfare.structure.tile.TileAdvancedSpawner;
import org.lwjgl.opengl.GL11;

import java.util.List;

public class RenderAdvancedSpawner extends TileEntitySpecialRenderer<TileAdvancedSpawner> {
	@Override
	public void render(TileAdvancedSpawner te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		if (!Minecraft.getMinecraft().player.isCreative() && Minecraft.getMinecraft().playerController.getCurrentGameType() != GameType.SPECTATOR) {
			return;
		}

		List<SpawnerSettings.EntitySpawnGroup> spawnGroups = te.getSettings().getSpawnGroups();
		if (spawnGroups.isEmpty()) {
			return;
		}
		List<SpawnerSettings.EntitySpawnSettings> spawnSettings = spawnGroups.get(0).getEntitiesToSpawn();
		if (spawnSettings.isEmpty()) {
			return;
		}
		String string = I18n.format(spawnSettings.get(0).getCustomNameOrEntityName());

		FontRenderer fontrenderer = Minecraft.getMinecraft().fontRenderer;
		RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();
		float f = 1.6F;
		float f1 = 0.016666668F * f;
		GlStateManager.pushMatrix();
		GlStateManager.translate((float) x + 0.5F, (float) y + 1 + 0.5F, (float) z + 0.5F);
		GlStateManager.glNormal3f(0.0F, 1.0F, 0.0F);
		GlStateManager.rotate(-renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotate(renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
		GlStateManager.scale(-f1, -f1, f1);
		GlStateManager.disableLighting();
		GlStateManager.depthMask(false);
		GlStateManager.disableDepth();
		GlStateManager.enableBlend();
		OpenGlHelper.glBlendFunc(770, 771, 1, 0);
		Tessellator tessellator = Tessellator.getInstance();

		GlStateManager.disableTexture2D();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
		int j = fontrenderer.getStringWidth(string) / 2;
		bufferBuilder.pos((double) (-j - 1), (double) (-1), 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
		bufferBuilder.pos((double) (-j - 1), (double) (8), 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
		bufferBuilder.pos((double) (j + 1), (double) (8), 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
		bufferBuilder.pos((double) (j + 1), (double) (-1), 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
		tessellator.draw();
		GlStateManager.enableTexture2D();
		fontrenderer.drawString(string, -fontrenderer.getStringWidth(string) / 2, 0, 0x20ffffff);
		GlStateManager.enableDepth();
		GlStateManager.depthMask(true);
		fontrenderer.drawString(string, -fontrenderer.getStringWidth(string) / 2, 0, 0xffffffff);
		GlStateManager.enableLighting();
		GlStateManager.disableBlend();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.popMatrix();

		super.render(te, x, y, z, partialTicks, destroyStage, alpha);
	}
}
