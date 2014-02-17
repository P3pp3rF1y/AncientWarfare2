package net.shadowmage.ancientwarfare.core.gui.elements;

import java.util.ArrayList;
import java.util.List;

import net.shadowmage.ancientwarfare.core.gui.GuiElement;
import net.shadowmage.ancientwarfare.core.gui.Listener;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase.ActivationEvent;

public class Composite extends GuiElement
{

private List<GuiElement> elements = new ArrayList<GuiElement>();

public Composite(int topLeftX, int topLeftY, int width, int height)
  {
  super(topLeftX, topLeftY, width, height); 
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
        for(GuiElement element : elements)
          {
          element.handleMouseInput(evt);
          }
        }
      return true;
      }
    });
  }

@Override
public void render(int mouseX, int mouseY, float partialTick)
  {
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
  
  }

protected void resetViewport()
  {
  
  }

@Override
public void updateRenderPosition(int guiLeft, int guiTop)
  {
  super.updateRenderPosition(guiLeft, guiTop);
  for(GuiElement element : this.elements)
    {
    element.updateRenderPosition(0, 0);
    }
  }

}
