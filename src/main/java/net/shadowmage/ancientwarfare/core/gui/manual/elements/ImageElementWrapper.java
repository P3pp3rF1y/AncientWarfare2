package net.shadowmage.ancientwarfare.core.gui.manual.elements;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.shadowmage.ancientwarfare.core.gui.manual.GuiManual;
import net.shadowmage.ancientwarfare.core.gui.manual.IElementWrapperCreator;
import net.shadowmage.ancientwarfare.core.manual.ImageElement;
import net.shadowmage.ancientwarfare.core.util.TextureUtils;

import java.util.List;

public class ImageElementWrapper extends BaseElementWrapper<ImageElement> {
	private static final int BOTTOM_PADDING = 4;
	private final ResourceLocation texture;

	public ImageElementWrapper(GuiManual gui, int topLeftY, int width, int height, ImageElement element) {
		super(gui, 0, topLeftY, width, height, element);

		texture = TextureUtils.getTextureLocation("registry/manual/" + getElement().getPath().replaceFirst("^/", ""));
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTick) {
		super.render(mouseX, mouseY, partialTick);
		Minecraft.getMinecraft().renderEngine.bindTexture(texture);
		int textureWidth = getElement().getWidth();
		int textureHeight = getElement().getHeight();
		float scale = getScale(width, textureWidth);
		int scaledWidth = (int) (scale * textureWidth);
		int padding = Math.min(0, (width - scaledWidth) / 2);

		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		Gui.drawScaledCustomSizeModalRect(renderX + padding, renderY, 0, 0, textureWidth, textureHeight, scaledWidth, (int) (textureHeight * scale),
				textureWidth, textureHeight);
	}

	private static float getScale(int width, int textureWidth) {
		return width < textureWidth ? (float) width / textureWidth : 1.0f;
	}

	public static class Creator implements IElementWrapperCreator<ImageElement> {
		@Override
		public List<BaseElementWrapper<ImageElement>> construct(GuiManual gui, int topLeftY, int width, int remainingPageHeight, int emptyPageHeight, ImageElement element) {
			int scaledHeight = (int) (getScale(width, element.getWidth()) * element.getHeight());
			return ImmutableList.of(new ImageElementWrapper(gui, remainingPageHeight < scaledHeight ? 0 : topLeftY, width, scaledHeight + BOTTOM_PADDING, element));
		}
	}
}
