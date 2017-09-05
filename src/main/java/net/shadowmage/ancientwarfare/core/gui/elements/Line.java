package net.shadowmage.ancientwarfare.core.gui.elements;

import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;

public class Line extends GuiElement {

    int lineWidth;
    int color;
    int x2, y2;

    /*
     * @param color RGBA color
     */
    public Line(int topLeftX, int topLeftY, int x2, int y2, int lineWidth, int color) {
        super(topLeftX, topLeftY, x2 - topLeftX, y2 - topLeftY);
        this.color = color;
        this.x2 = x2 - topLeftX;
        this.y2 = y2 - topLeftY;
        this.lineWidth = lineWidth;
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTick) {
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_LIGHTING);


        GL11.glLineWidth(lineWidth * getScaleFactor());
        setColor();
        GL11.glBegin(GL11.GL_LINE_LOOP);
        GL11.glVertex2f(renderX, renderY);
        GL11.glVertex2f(renderX + x2, renderY + y2);
        GL11.glEnd();
        GL11.glColor4f(1.f, 1.f, 1.f, 1.f);

        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

    private void setColor() {
        float r, g, b, a;
        r = (color & 0xff000000) >> 24;
        g = (color & 0x00ff0000) >> 16;
        b = (color & 0x0000ff00) >> 8;
        a = (color & 0x000000ff) >> 0;
        r = (float) r / 255.f;
        g = (float) g / 255.f;
        b = (float) b / 255.f;
        a = (float) a / 255.f;
        GL11.glColor4f(r, g, b, a);
    }

    private int getScaleFactor() {
        Minecraft mc = Minecraft.getMinecraft();
        int scaledWidth = mc.displayWidth;
        int scaledHeight = mc.displayHeight;
        int scaleFactor = 1;
        int guiScale = mc.gameSettings.guiScale;
        if (guiScale == 0) {
            guiScale = 1000;
        }
        while (scaleFactor < guiScale && scaledWidth / (scaleFactor + 1) >= 320 && scaledHeight / (scaleFactor + 1) >= 240) {
            ++scaleFactor;
        }
        return scaleFactor;
    }

}
