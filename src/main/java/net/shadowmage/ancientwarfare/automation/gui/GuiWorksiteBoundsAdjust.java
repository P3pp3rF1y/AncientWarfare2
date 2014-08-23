package net.shadowmage.ancientwarfare.automation.gui;

import net.shadowmage.ancientwarfare.automation.container.ContainerWorksiteBoundsAdjust;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.Button;
import net.shadowmage.ancientwarfare.core.gui.elements.Label;
import net.shadowmage.ancientwarfare.core.gui.elements.NumberInput;
import net.shadowmage.ancientwarfare.core.gui.elements.Rectangle;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;

public class GuiWorksiteBoundsAdjust extends GuiContainerBase
{

boolean largeMode = false;
ContainerWorksiteBoundsAdjust container;

public GuiWorksiteBoundsAdjust(ContainerBase container)
  {
  super(container);
  this.container = (ContainerWorksiteBoundsAdjust) container;
  this.shouldCloseOnVanillaKeys = true;
  if(this.container.worksite.getBoundsMaxWidth()>16){largeMode=true;}
  }

@Override
public void initElements()
  {

  }

@Override
public void setupElements()
  {
  this.clearElements();  
  Button b;
  
  b = new Button(12, 12, 40, 12, "NORTH")
    {
    @Override
    protected void onPressed()
      {
      if(container.max.z >= container.z && (container.min.x > container.x || container.max.x < container.x))
        {
        container.min.z--;
        container.max.z--;        
        }
      refreshGui();
      }
    };
  addGuiElement(b);
  
  b = new Button(12+40, 12, 40, 12, "SOUTH")
    {
    @Override
    protected void onPressed()
      {
      if(container.min.z <= container.z && (container.min.x > container.x || container.max.x < container.x))
        {
        container.min.z++;
        container.max.z++;
        }
      refreshGui();
      }
    };
  addGuiElement(b);
  
  b = new Button(12+40+40, 12, 40, 12, "WEST")
    {
    @Override
    protected void onPressed()
      {
      if(container.max.x >= container.x && (container.min.z > container.z || container.max.z < container.z))
        {
        container.min.x--;
        container.max.x--;
        }
      refreshGui();
      }
    };
  addGuiElement(b);
  
  b = new Button(12+40+40+40, 12, 40, 12, "EAST")
    {
    @Override
    protected void onPressed()
      {
      if(container.min.x <= container.x && (container.min.z > container.z || container.max.z < container.z))
        {
        container.min.x++;
        container.max.x++;
        }
      refreshGui();
      }
    };
  addGuiElement(b);  
  
  b = new Button(12, 24, 40, 12, "XSIZE-")
    {
    @Override
    protected void onPressed()
      {
      if(container.max.x - container.min.x<=0){return;}
      if(container.min.x < container.x)
        {
        container.min.x++;
        }
      else
        {
        container.max.x--;        
        }
      refreshGui();
      }
    };
  addGuiElement(b);
  
  b = new Button(12+40, 24, 40, 12, "XSIZE+")
    {
    @Override
    protected void onPressed()
      {
      if(container.max.x - container.min.x + 1 >= container.worksite.getBoundsMaxWidth()){return;}
      if(container.min.x < container.x)
        {
        container.min.x--;
        }
      else
        {
        container.max.x++;        
        }
      refreshGui();
      }
    };    
  addGuiElement(b);
  
  b = new Button(12+80, 24, 40, 12, "ZSIZE-")
    {
    @Override
    protected void onPressed()
      {
      if(container.max.z - container.min.z<=0){return;}
      if(container.min.z < container.z)
        {
        container.min.z++;
        }
      else
        {
        container.max.z--;        
        }
      refreshGui();
      }
    };
  addGuiElement(b);
  
  b = new Button(12+120, 24, 40, 12, "ZSIZE+")
    {
    @Override
    protected void onPressed()
      {
      if(container.max.z - container.min.z + 1 >= container.worksite.getBoundsMaxWidth()){return;}
      if(container.min.z < container.z)
        {
        container.min.z--;
        }
      else
        {
        container.max.z++;        
        }
      refreshGui();
      }
    };
  addGuiElement(b);
  
  Label label = new Label(12, 36, "YOFFSET");
  addGuiElement(label);
  
  NumberInput input = new NumberInput(80, 36, 40, 0, this)
    {
    @Override
    public void onValueUpdated(float value)
      {
      if(value>1.f)
        {
        value = 1.f;
        setValue(value);
        }
      else if(value<-1.f)
        {
        value = -1.f;
        setValue(value);
        }
      else
        {
        container.min.y = (int)value + container.y;
        container.max.y = (int)value + container.y;
        }
      }
    };
  input.setIntegerValue();
  input.setAllowNegative();
  addGuiElement(input);
  addLayout();
  }

private void addLayout()
  {
  
  int size = (240-68) / (container.worksite.getBoundsMaxWidth()+2);
  
  int tlx = 12 + size;
  int tly = 48 + 8 + size;  
  
  
  BlockPosition p = new BlockPosition(container.x, container.y, container.z);
  BlockPosition p1 = container.min;
  BlockPosition p2 = container.max;
  
  BlockPosition o = new BlockPosition(p.x - p1.x, p.y - p1.y, p.z - p1.z);  
  
  int w = p2.x-p1.x;
  int l = p2.z-p1.z;
  
  Rectangle r;
  
  r = new Rectangle(tlx + o.x*size, tly + o.z*size, size, size, 0x0000ffff, 0xff0000ff);
  addGuiElement(r);
  
  for(int x = 0; x <= w; x++)
    {
    for(int y = 0; y <= l; y++)
      {
      r = new Rectangle(tlx + x*size, tly + y*size, size, size, 0x000000ff, 0xffffffff);
      addGuiElement(r);
      }
    }    
  }

}
