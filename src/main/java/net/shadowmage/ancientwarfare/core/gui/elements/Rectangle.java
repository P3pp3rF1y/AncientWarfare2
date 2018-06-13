package net.shadowmage.ancientwarfare.core.gui.elements;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class Rectangle extends GuiElement {

	private final int color, hoverColor;

	public Rectangle(int topLeftX, int topLeftY, int width, int height, int color, int hoverColor) {
		super(topLeftX, topLeftY, width, height);
		this.color = color;
		this.hoverColor = hoverColor;
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTick) {
		GlStateManager.disableTexture2D();
		GlStateManager.disableLighting();
		setColor(getColor(mouseX, mouseY));
		GlStateManager.glBegin(GL11.GL_QUADS);
		GL11.glVertex2f(renderX, renderY);
		GL11.glVertex2f(renderX, renderY + height);
		GL11.glVertex2f(renderX + width, renderY + height);
		GL11.glVertex2f(renderX + width, renderY);
		GlStateManager.glEnd();
		GlStateManager.color(1.f, 1.f, 1.f, 1.f);
	}

	protected int getColor(int mouseX, int mouseY) {
		return isMouseOverElement(mouseX, mouseY) ? hoverColor : color;
	}

	private void setColor(int color) {
		float r, g, b, a;
		r = (color >> 24) & 255;
		g = (color >> 16) & 255;
		b = (color >> 8) & 255;
		a = (color >> 0) & 255;
		r = (float) r / 255.f;
		g = (float) g / 255.f;
		b = (float) b / 255.f;
		a = (float) a / 255.f;
		GlStateManager.color(r, g, b, a);
	}

}
