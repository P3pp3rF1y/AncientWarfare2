package net.shadowmage.ancientwarfare.core.gui.elements;

import net.shadowmage.ancientwarfare.core.gui.Listener;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase.ActivationEvent;
import net.shadowmage.ancientwarfare.core.interfaces.IScrollableCallback;

public class CompositeScrolled extends Composite implements IScrollableCallback
{

private Scrollbar scrollbar;
int currentTop = 0;

public CompositeScrolled(int topLeftX, int topLeftY, int width, int height)
  {
  super(topLeftX, topLeftY, width, height);
  scrollbar = new Scrollbar(width-12, 0, 12, height, this);
  this.addGuiElement(scrollbar);  
  }

@Override
public void onScrolled(int newTop)
  {
  currentTop = newTop;
  for(GuiElement element : this.elements)
    {
    if(element==this.scrollbar){continue;}
    element.updateRenderPosition(0, -currentTop);
    }
  }

public void setAreaSize(int height)
  {
  this.scrollbar.setAreaSize(height);  
  }

@Override
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
        if(isMouseOverElement(evt.mx, evt.my))
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
        else
          {
          scrollbar.dragging = false;
          scrollbar.pressed = false;
          }
        }
      return true;
      }
    });
  }

}
