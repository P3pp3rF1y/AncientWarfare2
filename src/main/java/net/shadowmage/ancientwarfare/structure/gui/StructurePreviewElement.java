package net.shadowmage.ancientwarfare.structure.gui;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.GuiElement;
import net.shadowmage.ancientwarfare.core.util.RenderTools;
import net.shadowmage.ancientwarfare.core.util.Trig;
import net.shadowmage.ancientwarfare.structure.api.TemplateRuleBlock;
import net.shadowmage.ancientwarfare.structure.render.PreviewRenderer;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplateManager;
import net.shadowmage.ancientwarfare.structure.template.build.StructureBB;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class StructurePreviewElement extends GuiElement {
	private static final Cache<String, Tuple<BufferBuilder.State, Map<BlockPos, TemplateRuleBlock>>> previewCache = CacheBuilder.newBuilder().expireAfterAccess(1, TimeUnit.MINUTES).build();

	private float yRotation = 0f;

	private String templateName = "";

	public StructurePreviewElement(int topLeftX, int topLeftY, int width, int height) {
		super(topLeftX, topLeftY, width, height);
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTick) {
		RenderTools.renderColoredQuad(renderX, renderY, width, height, 0, 0, 0);

		StructureTemplateManager.getTemplate(templateName).ifPresent(template -> {
			StructureBB bb = new StructureBB(BlockPos.ORIGIN, EnumFacing.NORTH, template.getSize(), template.getOffset());

			if (bb.getXSize() * bb.getYSize() * bb.getZSize() > 4000000) {
				FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
				String message = "Structure too big to render";
				fontRenderer.drawString(message, renderX + (width - fontRenderer.getStringWidth(message)) / 2, renderY + (height - 8) / 2, 16777215);
				return;
			}

			float totalSize = Math.min(height, width);

			int x = renderX + width / 2;
			int y = renderY + height / 2;
			float scale = totalSize / Math.max(Math.max(bb.getXSize(), bb.getYSize()), bb.getZSize());
			BlockPos diff = bb.max.subtract(bb.min);
			yRotation = Trig.wrapTo360(yRotation + 0.5f);

			Minecraft mc = Minecraft.getMinecraft();
			RenderItem renderItem = mc.getRenderItem();
			GlStateManager.pushMatrix();
			TextureManager textureManager = mc.getTextureManager();
			textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
			textureManager.getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
			GlStateManager.enableRescaleNormal();
			GlStateManager.enableAlpha();
			GlStateManager.alphaFunc(516, 0.1F);
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.translate((float) x, (float) y, 100.0F + renderItem.zLevel);
			GlStateManager.scale(1.0F, -1.0F, 1.0F);
			GlStateManager.rotate(10F, 1, 0, 0);
			GlStateManager.rotate(yRotation, 0, 1, 0);
			GlStateManager.scale(scale, scale, scale);
			GlStateManager.pushMatrix();
			GlStateManager.translate(-0.5F, -0.5F, -0.5F);

			GuiContainerBase.pushViewport(renderX, renderY, width, height);

			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder bufferbuilder = tessellator.getBuffer();
			bufferbuilder.begin(7, DefaultVertexFormats.BLOCK);

			Tuple<BufferBuilder.State, Map<BlockPos, TemplateRuleBlock>> state = previewCache.getIfPresent(templateName);
			if (state == null) {
				Map<BlockPos, TemplateRuleBlock> dynamicRenderRules = new HashMap<>();
				PreviewRenderer.renderPreview(template, bb, 0, bufferbuilder, dynamicRenderRules);
				state = new Tuple<>(bufferbuilder.getVertexState(), dynamicRenderRules);
				previewCache.put(templateName, state);
			}

			bufferbuilder.setVertexState(state.getFirst());

			GlStateManager.translate(-diff.getX() / 2f - bb.min.getX(), -diff.getY() / 2f - bb.min.getY(), -diff.getZ() / 2f - bb.min.getZ());
			tessellator.draw();
			PreviewRenderer.renderDynamicRules(0, state.getSecond());

			GuiContainerBase.popViewport();

			GlStateManager.popMatrix();

			GlStateManager.disableAlpha();
			GlStateManager.disableRescaleNormal();
			GlStateManager.disableLighting();
			GlStateManager.popMatrix();
			textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
			textureManager.getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
		});
	}
}
