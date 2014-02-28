package net.shadowmage.ancientwarfare.core.gui.elements;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase.ActivationEvent;
import net.shadowmage.ancientwarfare.core.gui.Listener;
import net.shadowmage.ancientwarfare.core.util.RenderTools;

import org.lwjgl.opengl.GL11;


/**
 * base class for gui elements that contain other elements
 * E.G. Scrollable area, Tabbed area, Multi-button controls
 * 
 * @author John
 *
 */
public class Composite extends GuiElement
{

private static LinkedList<Viewport> viewportStack = new LinkedList<Viewport>();
protected List<GuiElement> elements = new ArrayList<GuiElement>();

public Composite(int topLeftX, int topLeftY, int width, int height)
  {
  super(topLeftX, topLeftY, width, height); 
  addDefaultListeners();
  this.keyboardInterface = true;
  this.mouseInterface = true;
  }

/**
 * sub-classes should override this method to add their custom default listeners. * 
 */
protected void addDefaultListeners()
  {
  this.addNewListener(new Listener(Listener.ALL_EVENTS)
    {
    @Override
    public boolean onEvent(GuiElement widget, ActivationEvent evt)
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
        AWLog.logDebug("sending mouse event to elements...");
        if(isMouseOverElement(evt.mx, evt.my))
          {
          /**
           * adjust mouse event position for relative to composite
           */
          int x = evt.mx;
          int y = evt.my;
//          evt.mx-=renderX;
//          evt.my-=renderY;
          for(GuiElement element : elements)
            {
            element.handleMouseInput(evt);
            }
          evt.mx = x;
          evt.my = y;
          }
        else if(evt.type==Listener.MOUSE_UP)
          {
          for(GuiElement element : elements)
            {
            element.setSelected(false);
            }
          }
        }
      return true;
      }
    });
  }

@Override
public void render(int mouseX, int mouseY, float partialTick)
  {
  if(!isMouseOverElement(mouseX, mouseY))
    {
    mouseX = Integer.MIN_VALUE;
    mouseY = Integer.MIN_VALUE;
    }
  else
    {
    }    
  setViewport();
  Minecraft.getMinecraft().renderEngine.bindTexture(backgroundTextureLocation);
  RenderTools.renderQuarteredTexture(256, 256, 0, 0, 256, 240, renderX, renderY, width, height);
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
  int x, y, w, h;
  int tlx, tly, brx, bry;
  x = renderX;
  y = renderY;
  w = width;
  h = height;
  tlx = x;
  tly = y;
  brx = x + w;
  bry = y + h;
  
  Viewport p = viewportStack.peek();
  if(p!=null)
    {
    if(tlx<p.x){tlx = p.x;}
    if(brx>p.x+p.w){brx = p.x + p.w;}
    if(tly<p.y){tly = p.y;}
    if(bry>p.y+p.h){bry = p.y+p.h;}    
    }
  x = tlx;
  y = tly;
  w = brx - tlx;
  h = bry - tly;
  
  Minecraft mc = Minecraft.getMinecraft();
  ScaledResolution scaledRes = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);
  int guiScale = scaledRes.getScaleFactor();
  GL11.glEnable(GL11.GL_SCISSOR_TEST);  
  
  GL11.glScissor(x*guiScale, mc.displayHeight - y*guiScale - h*guiScale, w*guiScale, h*guiScale);
  
  viewportStack.push(new Viewport(x, y, w, h));
  }

protected void resetViewport()
  {  
  Viewport p = viewportStack.poll();
  if(p==null)
    {
    GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }
  p = viewportStack.peek();
  if(p!=null)
    {
    Minecraft mc = Minecraft.getMinecraft();
    ScaledResolution scaledRes = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);
    int guiScale = scaledRes.getScaleFactor();
    GL11.glEnable(GL11.GL_SCISSOR_TEST);  
    
    GL11.glScissor(p.x*guiScale, mc.displayHeight - p.y*guiScale - p.h*guiScale, p.w*guiScale, p.h*guiScale);    
    }
  else
    {
    GL11.glDisable(GL11.GL_SCISSOR_TEST);    
    }
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
    element.updateRenderPosition(renderX, renderY);
    }
  }

/**
 * class used to represent a currently drawable portion of the screen.
 * Used in a stack for figuring out what composites may draw where
 * @author John
 *
 */
private class Viewport
{
int x, y, w, h;
private Viewport(int x, int y, int w, int h)
  {
  this.x = x;
  this.y = y;
  this.h = h;
  this.w = w;
  }
}

}
