package net.shadowmage.ancientwarfare.core.gui.elements;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

public class Label extends GuiElement
{

FontRenderer fr;
String text;

public Label(int topLeftX, int topLeftY, String text)
  {
  super(topLeftX, topLeftY);
  this.text = text;
  this.height = 8;
  fr = Minecraft.getMinecraft().fontRenderer;
  this.width = fr.getStringWidth(text);
  }

@Override
public void render(int mouseX, int mouseY, float partialTick)
  {
  if(visible)
    {
    fr.drawStringWithShadow(text, renderX, renderY, 0xffffffff);    
    }
  }

public void setText(String text)
  {
  this.text = text;
  }

}
