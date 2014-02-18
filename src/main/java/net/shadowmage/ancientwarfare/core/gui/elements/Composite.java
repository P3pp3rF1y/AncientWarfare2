package net.shadowmage.ancientwarfare.core.gui.elements;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.shadowmage.ancientwarfare.core.gui.GuiElement;
import net.shadowmage.ancientwarfare.core.gui.Listener;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase.ActivationEvent;

/**
 * base class for gui elements that contain other elements
 * E.G. Scrollable area, Tabbed area, Multi-button controls
 * 
 * @author John
 *
 */
public class Composite extends GuiElement
{

protected List<GuiElement> elements = new ArrayList<GuiElement>();

public Composite(int topLeftX, int topLeftY, int width, int height)
  {
  super(topLeftX, topLeftY, width, height); 
  addDefaultListeners();
  }

/**
 * sub-classes should override this method to add their custom default listeners. * 
 */
protected void addDefaultListeners()
  {
  this.addNewListener(new Listener(Listener.ALL_EVENTS)
    {
    @Override
    public boolean onEvent(ActivationEvent evt)
      {
      if((evt.type & Listener.KEY_TYPES) != 0)
        {
        for(GuiElement element : elements)
          {
          element.handleKeyboardInput(evt);
          }
        }
      else if((evt.type & Listener.MOUSE_TYPES) != 0)
        {
        /**
         * adjust mouse event position for relative to composite
         */
        int x = evt.mx;
        int y = evt.my;
        evt.mx-=renderX;
        evt.my-=renderY;
        for(GuiElement element : elements)
          {
          element.handleMouseInput(evt);
          }
        evt.mx = x;
        evt.my = y;
        }
      return true;
      }
    });
  }

@Override
public void render(int mouseX, int mouseY, float partialTick)
  {
  /**
   * adjust mouse input position for relative to composite
   */
  mouseX-=renderX;
  mouseY-=renderY;
  Minecraft.getMinecraft().renderEngine.bindTexture(backgroundTextureLocation);
  this.renderQuarteredTexture(256, 256, 0, 0, 256, 240, renderX, renderY, getWidth(), height);
  setViewport();
  for(GuiElement element : this.elements)
    {
    element.render(mouseX, mouseY, partialTick);
    }    
  resetViewport();
  }

public void addGuiElement(GuiElement element)
  {
  this.elements.add(element);
  }

protected void setViewport()
  {
  Minecraft mc = Minecraft.getMinecraft();
  GL11.glPushMatrix();
  ScaledResolution scaledRes = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);
  int guiScale = scaledRes.getScaleFactor();
  float w = this.getWidth() * guiScale;
  float h = height * guiScale;
  float x = renderX*guiScale;  
  float y = Display.getHeight() - h - renderY*guiScale;  
  float scaleY = (float)mc.displayHeight / h;  
  float scaleX = (float)mc.displayWidth / w;    
  GL11.glViewport((int)x, (int)y, (int)w, (int)h);  
  GL11.glScalef(scaleX, scaleY, 1);
  }

protected void resetViewport()
  {
  GL11.glPopMatrix();
  Minecraft mc = Minecraft.getMinecraft();
  GL11.glViewport(0, 0, mc.displayWidth, mc.displayHeight);
  }

/**
 * sub-classes of Composite should override this method
 * to feed their current scrolled position into their elements
 * (e.g. scrollbar composite will use scrollbar pos)
 */
@Override
public void updateRenderPosition(int guiLeft, int guiTop)
  {
  super.updateRenderPosition(guiLeft, guiTop);
  updateElementPositions();
  }

protected void updateElementPositions()
  {
  for(GuiElement element : this.elements)
    {
    element.updateRenderPosition(0, 0);
    }
  }

}
