package net.shadowmage.ancientwarfare.core.gui.elements;

import net.minecraft.client.Minecraft;
import net.shadowmage.ancientwarfare.core.util.RenderTools;

public class ProgressBar extends GuiElement {

	private float progress;
	boolean red;

	public ProgressBar(int topLeftX, int topLeftY, int width, int height) {
		super(topLeftX, topLeftY, width, height);
	}

	public void setRed(boolean red) {
		this.red = red;
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTick) {
		if (visible) {
			Minecraft.getMinecraft().renderEngine.bindTexture(widgetTexture2);
			RenderTools.renderQuarteredTexture(256, 256, 0, 0, 256, 40, renderX, renderY, width, height);

			int width = this.width - 6;
			int y = 234;
			if (red) {
				y += 10;
			}
			width = (int) ((float) width * progress);
			Minecraft.getMinecraft().renderEngine.bindTexture(widgetTexture1);
			//152, 234
			RenderTools.renderQuarteredTexture(256, 256, 152, y, 104, 10, renderX + 3, renderY + 3, width, height - 6);
		}
	}

	public void setProgress(float percent) {
		this.progress = percent;
	}

}
