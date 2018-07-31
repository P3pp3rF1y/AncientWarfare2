package net.shadowmage.ancientwarfare.core.gui.manual;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.shadowmage.ancientwarfare.core.gui.elements.GuiElement;
import net.shadowmage.ancientwarfare.core.util.RenderTools;

import java.util.List;

public class Page extends GuiElement {
	private static final int PADDING = 7;
	private final List<BaseElementWrapper> elements;

	public static int getPadding() {
		return PADDING;
	}

	private ResourceLocation background;
	private int backgroundU;
	private int backgroundV;

	public Page(int topLeftX, int topLeftY, int width, int height, List<BaseElementWrapper> elements) {
		super(topLeftX, topLeftY, width, height);
		this.elements = elements;
	}

	public void setBackground(ResourceLocation background, int backgroundU, int backgroundV) {
		this.background = background;
		this.backgroundU = backgroundU;
		this.backgroundV = backgroundV;
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTick) {
		GlStateManager.color(1, 1, 1, 1);
		Minecraft.getMinecraft().renderEngine.bindTexture(background);
		RenderTools.renderQuarteredTexture(512, 512, backgroundU, backgroundV, width, height, renderX, renderY, width, height);

		for (BaseElementWrapper element : elements) {
			element.updateGuiPosition(renderX + PADDING, renderY + PADDING);
			element.render(mouseX, mouseY, partialTick);
		}
	}
}
