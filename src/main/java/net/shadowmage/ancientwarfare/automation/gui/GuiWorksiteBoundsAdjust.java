package net.shadowmage.ancientwarfare.automation.gui;

import net.minecraft.nbt.NBTTagCompound;
import net.shadowmage.ancientwarfare.automation.container.ContainerWorksiteBoundsAdjust;
import net.shadowmage.ancientwarfare.automation.tile.worksite.TileWorksiteUserBlocks;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.Listener;
import net.shadowmage.ancientwarfare.core.gui.elements.Button;
import net.shadowmage.ancientwarfare.core.gui.elements.GuiElement;
import net.shadowmage.ancientwarfare.core.gui.elements.Rectangle;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;

public class GuiWorksiteBoundsAdjust extends GuiContainerBase
{

boolean noTargetMode = false;
ContainerWorksiteBoundsAdjust container;

boolean boundsAdjusted = false, targetsAdjusted = false;
byte[] checkedMap = new byte[16*16];

public GuiWorksiteBoundsAdjust(ContainerBase container)
  {
  super(container);
  this.container = (ContainerWorksiteBoundsAdjust) container;
  this.shouldCloseOnVanillaKeys = true;
  if(!this.container.worksite.userAdjustableBlocks()){noTargetMode=true;}
  }

private void setChecked(int x, int y, boolean checked)
  {
  if(!noTargetMode)
    {
    checkedMap[y*16+x] = checked? (byte)1 : (byte)0;    
    }
  }

private boolean isChecked(int x, int y)
  {
  if(noTargetMode){return false;}
  return checkedMap[y*16+x]==1;
  }

@Override
public void initElements()
  {
  //read initial checked-map from container
  }

@Override
public void setupElements()
  {
  this.clearElements();  
  Button b;
  
  b = new Button(48, 12, 40, 12, "NORTH")
    {
    @Override
    protected void onPressed()
      {
      if(container.max.z >= container.z && (container.min.x > container.x || container.max.x < container.x))
        {
        container.min.z--;
        container.max.z--;
        boundsAdjusted = true;
        refreshGui();
        }
      }
    };
  addGuiElement(b);
  
  b = new Button(48+40, 12, 40, 12, "SOUTH")
    {
    @Override
    protected void onPressed()
      {
      if(container.min.z <= container.z && (container.min.x > container.x || container.max.x < container.x))
        {
        container.min.z++;
        container.max.z++;
        boundsAdjusted = true;
        refreshGui();
        }
      }
    };
  addGuiElement(b);
  
  b = new Button(48+40+40, 12, 40, 12, "WEST")
    {
    @Override
    protected void onPressed()
      {
      if(container.max.x >= container.x && (container.min.z > container.z || container.max.z < container.z))
        {
        container.min.x--;
        container.max.x--;
        boundsAdjusted = true;
        refreshGui();
        }
      }
    };
  addGuiElement(b);
  
  b = new Button(48+40+40+40, 12, 40, 12, "EAST")
    {
    @Override
    protected void onPressed()
      {
      if(container.min.x <= container.x && (container.min.z > container.z || container.max.z < container.z))
        {
        container.min.x++;
        container.max.x++;
        boundsAdjusted = true;
        refreshGui();
        }
      }
    };
  addGuiElement(b);  
  
  b = new Button(48, 24, 40, 12, "XSIZE-")
    {
    @Override
    protected void onPressed()
      {
      if(container.max.x - container.min.x<=0){return;}
      if(container.min.x < container.x)
        {
        container.min.x++;
        boundsAdjusted = true;
        refreshGui();
        }
      else
        {
        container.max.x--;     
        boundsAdjusted = true;
        refreshGui();   
        }
      }
    };
  addGuiElement(b);
  
  b = new Button(48+40, 24, 40, 12, "XSIZE+")
    {
    @Override
    protected void onPressed()
      {
      if(container.max.x - container.min.x + 1 >= container.worksite.getBoundsMaxWidth()){return;}
      if(container.min.x < container.x)
        {
        container.min.x--;
        boundsAdjusted = true;
        refreshGui();
        }
      else
        {
        container.max.x++;   
        boundsAdjusted = true;
        refreshGui();     
        }
      }
    };    
  addGuiElement(b);
  
  b = new Button(48+80, 24, 40, 12, "ZSIZE-")
    {
    @Override
    protected void onPressed()
      {
      if(container.max.z - container.min.z<=0){return;}
      if(container.min.z < container.z)
        {
        container.min.z++;
        boundsAdjusted = true;
        refreshGui();
        }
      else
        {
        container.max.z--;  
        boundsAdjusted = true;
        refreshGui();      
        }
      }
    };
  addGuiElement(b);
  
  b = new Button(48+120, 24, 40, 12, "ZSIZE+")
    {
    @Override
    protected void onPressed()
      {
      if(container.max.z - container.min.z + 1 >= container.worksite.getBoundsMaxWidth()){return;}
      if(container.min.z < container.z)
        {
        container.min.z--;
        boundsAdjusted = true;
        refreshGui();
        }
      else
        {
        container.max.z++;   
        boundsAdjusted = true;
        refreshGui();     
        }
      }
    };
  addGuiElement(b);
    
  addLayout();
  }

private void addLayout()
  {  
  int bits = (container.worksite.getBoundsMaxWidth()+2);
  int size = (240-56) / bits;
  
  int tlx = (256 - (size*bits))/2 + size;
  int tly = 36 + 8 + size;  
  
  
  BlockPosition p = new BlockPosition(container.x, container.y, container.z);
  BlockPosition p1 = container.min;
  BlockPosition p2 = container.max;
  
  BlockPosition o = new BlockPosition(p.x - p1.x, p.y - p1.y, p.z - p1.z);  
  
  int w = p2.x-p1.x;
  int l = p2.z-p1.z;
  
  Rectangle r;
  
  r = new Rectangle(tlx + o.x*size, tly + o.z*size, size, size, 0x0000ffff, 0x0000ffff);
  addGuiElement(r);
  
  for(int x = 0; x <= w; x++)
    {
    final int x1 = x;
    for(int y = 0; y <= l; y++)
      {
      final int y1 = y;      
      r = new ToggledRectangle(tlx + x*size, tly + y*size, size, size, 0x000000ff, 0x808080ff, 0xff0000ff, 0xff8080ff, isChecked(x, y))
        {
        @Override
        public void clicked(ActivationEvent evt)
          {
          if(!noTargetMode)
            {
            super.clicked(evt);
            setChecked(x1, y1, checked);  
            targetsAdjusted = true;
            }
          }
        };
      addGuiElement(r);
      }
    }    
  }

@Override
public void handlePacketData(NBTTagCompound data)
  {
  if(data.hasKey("checkedMap"))
    {
    checkedMap = data.getByteArray("checkedMap");
    refreshGui();
    }
  }

@Override
protected boolean onGuiCloseRequested()
  {
  NBTTagCompound tag = new NBTTagCompound();
  tag.setBoolean("guiClosed", true);
  if(boundsAdjusted)
    {    
    tag.setTag("min", container.min.writeToNBT(new NBTTagCompound()));
    tag.setTag("max", container.max.writeToNBT(new NBTTagCompound()));    
    }
  if(targetsAdjusted && container.worksite instanceof TileWorksiteUserBlocks)
    {
    if(!noTargetMode)
      {
      tag.setByteArray("checkedMap", checkedMap);
      }
    }
  sendDataToContainer(tag);
  return super.onGuiCloseRequested();
  }

private class ToggledRectangle extends Rectangle
{
boolean checked;
int checkedColor;
int hoverCheckedColor;
public ToggledRectangle(int topLeftX, int topLeftY, int width, int height, int color, int hoverColor, int checkColor, int hoverCheckColor, boolean checked)
  {
  super(topLeftX, topLeftY, width, height, color, hoverColor);
  this.checked = checked;
  this.checkedColor = checkColor;
  this.hoverCheckedColor = hoverCheckColor;  
  addNewListener(new Listener(Listener.MOUSE_DOWN)
    {
    @Override
    public boolean onEvent(GuiElement widget, ActivationEvent evt)
      {
      if(widget.isMouseOverElement(evt.mx, evt.my))
        {
        clicked(evt);        
        }
      return true;
      }
    });
  }

public void clicked(ActivationEvent evt)
  {
  checked = !checked;
  }

@Override
protected int getColor(int mouseX, int mouseY)  
  {
  if(checked)
    {
    return isMouseOverElement(mouseX, mouseY) ? hoverCheckedColor : checkedColor;
    }
  return super.getColor(mouseX, mouseY);
  }

}

}
