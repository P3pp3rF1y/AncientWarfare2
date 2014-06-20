package net.shadowmage.ancientwarfare.core.gui.elements;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ResourceLocation;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase.ActivationEvent;
import net.shadowmage.ancientwarfare.core.gui.Listener;
import net.shadowmage.ancientwarfare.core.interfaces.IScrollableCallback;
import net.shadowmage.ancientwarfare.core.util.RenderTools;

public class Scrollbar extends GuiElement
{

static final int borderSize = 4;//the border around the scroll bar and handle

int totalAreaSize;
int viewSize;
float viewPercent;

int handleTopMax;//the min that handleTop can move to
int handleTop;//the top of the handle, relative to renderY+borderSize
int handleWidth;
int handleHeight;

boolean pressed;//is the mouse pressed down while not over the handle?
boolean dragging;//is the mouse pressed down while over the handle?
int lastMouseY;

int topIndex;

/**
 * callback interface for when the view port represented by this scrollbar should be updated
 * called anytime the handle position is changed or the view-size changes
 */
private IScrollableCallback parent;

public Scrollbar(int topLeftX, int topLeftY, int width, int height, IScrollableCallback parentCaller)
  {
  super(topLeftX, topLeftY, width, height);
    
  this.viewSize = height - borderSize * 2;
  
  this.handleTop = 0;
  this.handleHeight = viewSize;
  this.handleWidth = width - borderSize * 2;
   
  this.setAreaSize(height);
  
  this.parent = parentCaller;
  this.addNewListener(new Listener(Listener.MOUSE_MOVED)
    {
    @Override
    public boolean onEvent(GuiElement widget, ActivationEvent evt)
      {
      if(dragging)
        {
        int dy = evt.my - lastMouseY;
        handleTop += dy;
        lastMouseY = evt.my;
        updateHandlePosition();
        }
      return true;
      }
    });
  
  this.addNewListener(new Listener(Listener.MOUSE_DOWN)
    {
    @Override
    public boolean onEvent(GuiElement widget, ActivationEvent evt)
      {
      dragging = false;
      pressed = false;
      lastMouseY = evt.my;
      if(isMouseOverHandle(evt.mx, evt.my))
        {
        dragging = true;
        }  
      else if(isMouseOverElement(evt.mx, evt.my))
        {
        pressed = true;
        }
      return true;
      }
    });
  
  this.addNewListener(new Listener(Listener.MOUSE_UP)
    {
    @Override
    public boolean onEvent(GuiElement widget, ActivationEvent evt)
      {
      if(enabled && visible && pressed && isMouseOverElement(evt.mx, evt.my) && !isMouseOverHandle(evt.mx, evt.my))
        {
        if(evt.my < renderY + borderSize + handleTop)
          {
          handleTop -= handleHeight;      
          }
        else
          {
          handleTop += handleHeight;
          }
        Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.func_147674_a(new ResourceLocation("gui.button.press"), 1.0F));
        updateHandlePosition();    
        }  
      dragging = false;
      pressed = false;
      return true;
      }
    }); 
  
  this.addNewListener(new Listener(Listener.MOUSE_WHEEL)
    {
    @Override
    public boolean onEvent(GuiElement widget, ActivationEvent evt)
      {
      if(widget.isMouseOverElement(evt.mx, evt.my))
        {
        handleTop -= (evt.mw/10);
        updateHandlePosition();
        }
      return true;
      }
    });
  }

protected boolean isMouseOverHandle(int mouseX, int mouseY)
  {
  return mouseX >= renderX + borderSize && mouseX < renderX + width - borderSize && mouseY >= renderY + borderSize +handleTop && mouseY < renderY + borderSize + handleTop + handleHeight;  
  }

/**
 * should be called from the instantiating GUI to update the viewed set Height, in pixels.
 * E.G. if you add 10 elements of 12 pixels high each with 0 padding, you would setAreaSize(120)
 * the input size may be less than the height of the element, in which case it will view the full
 * set
 * @param size
 */
public void setAreaSize(int size)
  {
  this.totalAreaSize = size;
  this.viewPercent = (float)((float) viewSize / (float)totalAreaSize);
  if(this.viewPercent>1.f)
    {
    this.viewPercent = 1.f;
    }  
  this.updateHandlePosition();
  }

@Override
public void render(int mouseX, int mouseY, float partialTick)
  {
  if(visible)
    {
    Minecraft.getMinecraft().renderEngine.bindTexture(widgetTexture1);
    RenderTools.renderQuarteredTexture(256, 256, 80, 120, 40, 128, renderX, renderY, width, height);  
    RenderTools.renderQuarteredTexture(256, 256, 120, 120, 32, 128, renderX+borderSize, renderY+borderSize+handleTop, handleWidth, handleHeight);    
    }
  }

protected void updateHandlePosition()
  {
  this.handleHeight = (int)((float)viewSize * (float)viewPercent);
  this.handleTopMax = viewSize - handleHeight;;
  if(this.handleTop> this.handleTopMax)
    {
    this.handleTop = this.handleTopMax;
    }
  if(this.handleTop<0)
    {
    this.handleTop = 0;
    }  
  if(this.parent!=null)
    {
    topIndex = (int) ((float)handleTop * (1.f / viewPercent));
    this.parent.onScrolled(topIndex);
    }
  }

public int getTopIndex()
  {
  return topIndex;
  }

}
