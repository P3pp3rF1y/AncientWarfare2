package net.shadowmage.ancientwarfare.core.gui.elements;

import net.shadowmage.ancientwarfare.core.gui.GuiElement;
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

}
