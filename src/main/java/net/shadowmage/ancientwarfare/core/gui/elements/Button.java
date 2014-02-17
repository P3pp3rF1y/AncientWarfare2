package net.shadowmage.ancientwarfare.core.gui.elements;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ResourceLocation;
import net.shadowmage.ancientwarfare.core.gui.ActionListener;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase.ActivationEvent;
import net.shadowmage.ancientwarfare.core.gui.GuiElement;

public class Button extends GuiElement
{

int width;
int height;
String text;
int textX;
int textY;

public Button(int topLeftX, int topLeftY, int width, int height, String text)
  {
  super(topLeftX, topLeftY);
  this.mouseInterface = true;
  this.width = width;
  this.height = height;
  this.text = text;
  this.addNewListener(new ActionListener(ActionListener.MOUSE_UP)
    {      
    @Override
    public boolean onActivationEvent(ActivationEvent evt)
      {
      if(enabled && visible)
        {
        Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.func_147674_a(new ResourceLocation("gui.button.press"), 1.0F));        
        }
      return true;
      }
    });
  
  }

@Override
public boolean isMouseOverElement(int mouseX, int mouseY)
  {
  return mouseX >= renderX && mouseX < renderX + width && mouseY >= renderY && mouseY < renderY + height;
  }

@Override
public void render(int mouseX, int mouseY, float partialTick)
  {
  if(visible)
    {
    Minecraft.getMinecraft().renderEngine.bindTexture(widgetTexture1);   
    int textureSize = 256;
    int startX = 0;
    int startY = enabled ? isMouseOverElement(mouseX, mouseY) ? 80 : 40 : 0;
    int usedWidth = 256;
    int usedHeight = 40;  
    renderQuarteredTexture(textureSize, textureSize, startX, startY, usedWidth, usedHeight, renderX, renderY, width, height);    
    }  
  }

}
