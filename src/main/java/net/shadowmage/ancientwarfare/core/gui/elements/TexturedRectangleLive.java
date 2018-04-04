package net.shadowmage.ancientwarfare.core.gui.elements;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.shadowmage.ancientwarfare.core.util.RenderTools;

public class TexturedRectangleLive extends GuiElement {

	ResourceLocation tex;
	int tx, ty, u, v, uw, vh;
	float u1, v1, u2, v2;

	public TexturedRectangleLive(int topLeftX, int topLeftY, int width, int height, int tx, int ty, int u, int v, int uw, int vh, ResourceLocation tex) {
		super(topLeftX, topLeftY, width, height);
		this.tx = tx;//texture X size
		this.ty = ty;//texture Y size
		this.u = u;//start position in texture X axis
		this.v = v;//start position in texture Y axis
		this.uw = uw;//width of used texture
		this.vh = vh;//height of used texture

		float perX = 1.f / ((float) tx);
		float perY = 1.f / ((float) ty);
		u1 = ((float) u) * perX;
		v1 = ((float) v) * perY;
		u2 = (float) (u + uw) * perX;
		v2 = (float) (v + vh) * perY;
		this.tex = tex;
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTick) {
		if (visible) {
			Minecraft.getMinecraft().renderEngine.bindTexture(tex);
			RenderTools.renderTexturedQuad(renderX, renderY, renderX + width, renderY + height, u1, v1, u2, v2);
		}
	}

}
