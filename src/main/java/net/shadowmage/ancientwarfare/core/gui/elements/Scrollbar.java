package net.shadowmage.ancientwarfare.core.gui.elements;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ResourceLocation;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.gui.ActionListener;
import net.shadowmage.ancientwarfare.core.gui.GuiElement;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase.ActivationEvent;

public class Scrollbar extends GuiElement
{

static final int borderSize = 4;//the border around the scroll bar and handle

int width;
int height;

int totalAreaSize;
int viewSize;
float viewPercent;

int handleTopMax;//the min that handleTop can move to

int handleTop;//the top of the handle, relative to renderY+borderSize
int handleWidth;
int handleHeight;

boolean dragging;//is the mouse pressed down while over the handle?
int lastMouseY;

public Scrollbar(int topLeftX, int topLeftY, int width, int height)
  {
  super(topLeftX, topLeftY);
  this.mouseInterface = true;
  this.width = width;
  this.height = height;
  this.viewSize = height - borderSize * 2;
  this.handleTop = 0;
  this.handleHeight = viewSize;
  this.handleWidth = width - borderSize * 2;
  this.addNewListener(new ActionListener(ActionListener.MOUSE_MOVED)
    {
    @Override
    public boolean onActivationEvent(ActivationEvent evt)
      {
//      AWLog.logDebug("mouse moved event on scrollbar");
      if(!Mouse.isButtonDown(0))
        {
        dragging = false;
        }
      if(dragging)
        {
        int dy = evt.my - lastMouseY;
        AWLog.logDebug("dragging==true dy: "+dy);
        handleTop+=dy;
        lastMouseY = evt.my;
        updateHandlePosition();
        }
      return true;
      }
    });
  
  this.addNewListener(new ActionListener(ActionListener.MOUSE_DOWN)
    {
    @Override
    public boolean onActivationEvent(ActivationEvent evt)
      {
      AWLog.logDebug("mouse down event on scrollbar");
      lastMouseY = evt.my;
      dragging = true;
      return true;
      }
    });
  
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

public void setAreaSize(int size)
  {
  this.totalAreaSize = size;
  this.viewPercent = (float)((float) viewSize / (float)totalAreaSize);
  AWLog.logDebug("totalSize: "+totalAreaSize);
  AWLog.logDebug("viewSize: "+viewSize);
  AWLog.logDebug("view percent: "+viewPercent);
  
  this.updateHandlePosition();
  }

@Override
public boolean isMouseOverElement(int mouseX, int mouseY)
  {
  return mouseX >= renderX && mouseX < renderX + width && mouseY >= renderY && mouseY < renderY + height;
  }

@Override
public void render(int mouseX, int mouseY, float partialTick)
  {
  if(!isMouseOverElement(mouseX, mouseY))
    {
    GL11.glColor4f(.8f, .8f, .8f, 1.f);
    }
  else
    {
    GL11.glColor4f(1.f, 1.f, 1.f, 1.f);
    }
  Minecraft.getMinecraft().renderEngine.bindTexture(widgetTexture1);
  renderQuarteredTexture(256, 256, 80, 120, 40, 128, renderX, renderY, width, height);  
  renderQuarteredTexture(256, 256, 120, 120, 32, 128, renderX+borderSize, renderY+borderSize+handleTop, handleWidth, handleHeight);
  GL11.glColor4f(1.f, 1.f, 1.f, 1.f);
  }

public void updateHandlePosition()
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
  AWLog.logDebug("handle height: "+handleHeight);
  AWLog.logDebug("handle top: "+handleTop);
  }

}
