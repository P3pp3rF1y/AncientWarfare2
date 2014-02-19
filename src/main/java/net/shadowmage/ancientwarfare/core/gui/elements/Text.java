package net.shadowmage.ancientwarfare.core.gui.elements;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.gui.Listener;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase.ActivationEvent;

/**
 * Text input field
 * height = 12px
 * @author Shadowmage
 *
 */
public class Text extends GuiElement
{

String text;
int cursorIndex;
FontRenderer fr;

public Text(int topLeftX, int topLeftY, int width, String defaultText)
  {
  super(topLeftX, topLeftY, width, 12);
  fr = Minecraft.getMinecraft().fontRenderer;
  this.text = defaultText;
  
  this.addNewListener(new Listener(Listener.MOUSE_UP)
    {
    @Override
    public boolean onEvent(ActivationEvent evt)
      {
      if(enabled && visible && isMouseOverElement(evt.mx, evt.my))
        {
        setSelected(true);
        }
      else
        {
        setSelected(false);
        }
      return true;
      }
    });
  this.addNewListener(new Listener(Listener.KEY_UP)
    {
    @Override
    public boolean onEvent(ActivationEvent evt)
      {
      if(enabled && visible && selected)
        {
        handleKeyInput(evt.key, evt.ch);
        }
      return true;
      }
    });
  this.keyboardInterface = true;
  this.mouseInterface = true;
  }

protected void handleKeyInput(int keyCode, char ch)
  {
  
  }

@Override
public void render(int mouseX, int mouseY, float partialTick)
  {
  GL11.glDisable(GL11.GL_LIGHTING);
  GL11.glDisable(GL11.GL_TEXTURE_2D);
  GL11.glColor4f(0.9f, 0.9f, 0.9f, 1.f);//lt-grey, for outline box
  
  GL11.glBegin(GL11.GL_QUADS);
  GL11.glVertex2f(renderX, renderY);
  GL11.glVertex2f(renderX, renderY+height);
  GL11.glVertex2f(renderX+width, renderY+height);
  GL11.glVertex2f(renderX+width, renderY);
  GL11.glEnd();

  GL11.glColor4f(0.0f, 0.0f, 0.0f, 1.f);//black, for the input box
  GL11.glBegin(GL11.GL_QUADS);
  GL11.glVertex2f(renderX+1, renderY+1);
  GL11.glVertex2f(renderX+1, renderY+height-1);
  GL11.glVertex2f(renderX+width-1, renderY+height-1);
  GL11.glVertex2f(renderX+width-1, renderY+1);
  GL11.glEnd();   

  GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.f);
  GL11.glEnable(GL11.GL_TEXTURE_2D);
  
  fr.drawString(text, renderX+2, renderY+2, 0xffffffff);

  if(selected)
    {
    int w = 0;
    for(int i = 0; i < cursorIndex - 1; i++)
      {
      w+=fr.getCharWidth(text.charAt(i));
      }
    fr.drawString("_", renderX+2+w, renderY+3, 0xffff0000);
    }

  GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.f);
  }

}
