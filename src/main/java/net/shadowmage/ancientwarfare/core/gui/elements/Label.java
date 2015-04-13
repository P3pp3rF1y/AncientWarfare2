package net.shadowmage.ancientwarfare.core.gui.elements;

import net.minecraft.client.Minecraft;
import net.minecraft.util.StatCollector;

public class Label extends GuiElement {

    private String text;
    private boolean renderCentered = false;

    public Label(int topLeftX, int topLeftY, String text) {
        super(topLeftX, topLeftY);
        setText(text);
        this.height = 8;
        this.width = Minecraft.getMinecraft().fontRenderer.getStringWidth(this.text);
    }

    public Label setRenderCentered() {
        this.renderCentered = true;
        return this;
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTick) {
        if (visible) {
            if (renderCentered) {
                int len = Minecraft.getMinecraft().fontRenderer.getStringWidth(text) / 2;
                Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(text, renderX - len, renderY, 0xffffffff);
            } else {
                Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(text, renderX, renderY, 0xffffffff);
            }
        }
    }

    public void setText(String text) {
        if (text == null) {
            text = "";
        }
        this.text = StatCollector.translateToLocal(text);
    }

    public String getText() {
        return text;
    }

}
