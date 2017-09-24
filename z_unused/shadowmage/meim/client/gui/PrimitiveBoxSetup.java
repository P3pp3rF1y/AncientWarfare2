/*
   Copyright 2012-2013 John Cummens (aka Shadowmage, Shadowmage4513)
   This software is distributed under the terms of the GNU General Public License.
   Please see COPYING for precise license information.

   This file is part of Ancient Warfare.

   Ancient Warfare is free software: you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation, either version 3 of the License, or
   (at your option) any later version.

   Ancient Warfare is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with Ancient Warfare.  If not, see <http://www.gnu.org/licenses/>.
 */
package shadowmage.meim.client.gui;

import shadowmage.ancient_framework.client.gui.elements.GuiButtonSimple;
import shadowmage.ancient_framework.client.gui.elements.GuiNumberInputLine;
import shadowmage.ancient_framework.client.gui.elements.GuiScrollableArea;
import shadowmage.ancient_framework.client.gui.elements.GuiString;
import shadowmage.ancient_framework.client.model.PrimitiveBox;

public class PrimitiveBoxSetup extends PrimitiveGuiSetup
{

private GuiButtonSimple primitiveX1Minus;
private GuiButtonSimple primitiveX1Plus;
private GuiButtonSimple primitiveY1Minus;
private GuiButtonSimple primitiveY1Plus;
private GuiButtonSimple primitiveZ1Minus;
private GuiButtonSimple primitiveZ1Plus;

private GuiNumberInputLine primitiveX1Input;
private GuiNumberInputLine primitiveY1Input;
private GuiNumberInputLine primitiveZ1Input;

private GuiButtonSimple primitiveX2Minus;
private GuiButtonSimple primitiveX2Plus;
private GuiButtonSimple primitiveY2Minus;
private GuiButtonSimple primitiveY2Plus;
private GuiButtonSimple primitiveZ2Minus;
private GuiButtonSimple primitiveZ2Plus;

private GuiNumberInputLine primitiveX2Input;
private GuiNumberInputLine primitiveY2Input;
private GuiNumberInputLine primitiveZ2Input;

public PrimitiveBoxSetup(GuiModelEditor gui, GuiModelEditorSetup setup)
  {
  super(gui, setup);
  }

@Override
public void addElements(GuiScrollableArea area)
  {
  super.addElements(area);//add base elements...ox, oy, oz, rx, ry, rz
  int totalHeight = area.totalHeight;
  int col1 = 0;
  int col2 = 25;
  int col3 = 25+12+2;
  int col4 = 25+12+2+20+2;
  
  primitiveX1Minus = new GuiButtonSimple(0,area, 12, 12, "-")
    {
    @Override
    public boolean handleMousePressed(int x, int y, int num)
      {
      if(super.handleMousePressed(x, y, num) && GuiModelEditor.model!=null && gui.getSelectedPiece()!=null && gui.getSelectedPrimitive()!=null)
        {  
        PrimitiveBox box = (PrimitiveBox)gui.getSelectedPrimitive();
        box.setBounds(box.x1()-1 * setup.scale, box.y1(), box.z1(), box.width(), box.height(), box.length());  
        updateButtonValues();
        }
      return true;
      }
    };  
  primitiveX1Minus.updateRenderPos(col2, totalHeight);
  area.addGuiElement(primitiveX1Minus);
  
  primitiveX1Plus = new GuiButtonSimple(0,area, 12, 12, "+")
    {
    @Override
    public boolean handleMousePressed(int x, int y, int num)
      {
      if(super.handleMousePressed(x, y, num) && GuiModelEditor.model!=null && gui.getSelectedPiece()!=null && gui.getSelectedPrimitive()!=null)
        {
        PrimitiveBox box = (PrimitiveBox)gui.getSelectedPrimitive();
        box.setBounds(box.x1()+1 * setup.scale, box.y1(), box.z1(), box.width(), box.height(), box.length());
        updateButtonValues();
        }
      return true;
      }
    };  
  primitiveX1Plus.updateRenderPos(col4, totalHeight);
 area.addGuiElement(primitiveX1Plus);
  
  primitiveX1Input = new GuiNumberInputLine(0,area, 20, 12, 4, "0")
    {
    @Override
    public void onElementActivated()
      {
      if(GuiModelEditor.model!=null && gui.getSelectedPiece()!=null && gui.getSelectedPrimitive()!=null)
        {
        PrimitiveBox box = (PrimitiveBox)gui.getSelectedPrimitive();
        box.setBounds(getFloatVal() * setup.scale, box.y1(), box.z1(), box.width(), box.height(), box.length()); 
        updateButtonValues();
        }
      }
    };
  primitiveX1Input.setValue(0.f);
  primitiveX1Input.updateRenderPos(col3, totalHeight);
  area.addGuiElement(primitiveX1Input);
  
  GuiString label = new GuiString(0,area, 25, 12, "B:X1");
  label.updateRenderPos(col1, totalHeight);
  area.addGuiElement(label);
  
  totalHeight+=12;
  
  
  primitiveY1Minus = new GuiButtonSimple(0,area, 12, 12, "-")
    {
    @Override
    public boolean handleMousePressed(int x, int y, int num)
      {
      if(super.handleMousePressed(x, y, num) && GuiModelEditor.model!=null && gui.getSelectedPiece()!=null && gui.getSelectedPrimitive()!=null)
        {   
        PrimitiveBox box = (PrimitiveBox)gui.getSelectedPrimitive();
        box.setBounds(box.x1(), box.y1()-1 * setup.scale, box.z1(), box.width(), box.height(), box.length());
        updateButtonValues();
        }
      return true;
      }
    };  
  primitiveY1Minus.updateRenderPos(col2, totalHeight);
  area.addGuiElement(primitiveY1Minus);
  
  primitiveY1Plus = new GuiButtonSimple(0,area, 12, 12, "+")
    {
    @Override
    public boolean handleMousePressed(int x, int y, int num)
      {
      if(super.handleMousePressed(x, y, num) && GuiModelEditor.model!=null && gui.getSelectedPiece()!=null && gui.getSelectedPrimitive()!=null)
        {
        PrimitiveBox box = (PrimitiveBox)gui.getSelectedPrimitive();
        box.setBounds(box.x1(), box.y1()+1 * setup.scale, box.z1(), box.width(), box.height(), box.length());
        updateButtonValues();
        }
      return true;
      }
    };  
  primitiveY1Plus.updateRenderPos(col4, totalHeight);
 area.addGuiElement(primitiveY1Plus);
  
  primitiveY1Input = new GuiNumberInputLine(0,area, 20, 12, 4, "0")
    {
    @Override
    public void onElementActivated()
      {
      if(GuiModelEditor.model!=null && gui.getSelectedPiece()!=null && gui.getSelectedPrimitive()!=null)
        {
        PrimitiveBox box = (PrimitiveBox)gui.getSelectedPrimitive();
        box.setBounds(box.x1(), getFloatVal() * setup.scale, box.z1(), box.width(), box.height(), box.length());  
        updateButtonValues();
        }
      }
    };
  primitiveY1Input.setValue(0.f);
  primitiveY1Input.updateRenderPos(col3, totalHeight);
 area.addGuiElement(primitiveY1Input);
  
  label = new GuiString(0,area, 25, 12, "B:Y1");
  label.updateRenderPos(col1, totalHeight);
 area.addGuiElement(label);
  
  totalHeight+=12;
  
  
  primitiveZ1Minus = new GuiButtonSimple(0,area, 12, 12, "-")
    {
    @Override
    public boolean handleMousePressed(int x, int y, int num)
      {
      if(super.handleMousePressed(x, y, num) && GuiModelEditor.model!=null && gui.getSelectedPiece()!=null && gui.getSelectedPrimitive()!=null)
        {   
        PrimitiveBox box = (PrimitiveBox)gui.getSelectedPrimitive();
        box.setBounds(box.x1(), box.y1(), box.z1()-1 * setup.scale, box.width(), box.height(), box.length());
        updateButtonValues();
        }
      return true;
      }
    };  
  primitiveZ1Minus.updateRenderPos(col2, totalHeight);
 area.addGuiElement(primitiveZ1Minus);
  
  primitiveZ1Plus = new GuiButtonSimple(0,area, 12, 12, "+")
    {
    @Override
    public boolean handleMousePressed(int x, int y, int num)
      {
      if(super.handleMousePressed(x, y, num) && GuiModelEditor.model!=null && gui.getSelectedPiece()!=null && gui.getSelectedPrimitive()!=null)
        {
        PrimitiveBox box = (PrimitiveBox)gui.getSelectedPrimitive();
        box.setBounds(box.x1(), box.y1(), box.z1()+1 * setup.scale, box.width(), box.height(), box.length());
        updateButtonValues();
        }
      return true;
      }
    };  
  primitiveZ1Plus.updateRenderPos(col4, totalHeight);
 area.addGuiElement(primitiveZ1Plus);
  
  primitiveZ1Input = new GuiNumberInputLine(0,area, 20, 12, 4, "0")
    {
    @Override
    public void onElementActivated()
      {
      if(GuiModelEditor.model!=null && gui.getSelectedPiece()!=null && gui.getSelectedPrimitive()!=null)
        {
        PrimitiveBox box = (PrimitiveBox)gui.getSelectedPrimitive();
        box.setBounds(box.x1(), box.y1(), getFloatVal() * setup.scale, box.width(), box.height(), box.length()); 
        updateButtonValues();
        }
      }
    };
  primitiveZ1Input.setValue(0.f);
  primitiveZ1Input.updateRenderPos(col3, totalHeight);
 area.addGuiElement(primitiveZ1Input);
  
  label = new GuiString(0,area, 25, 12, "B:Z1");
  label.updateRenderPos(col1, totalHeight);
 area.addGuiElement(label);
  
  totalHeight+=12;
  
  
  primitiveX2Minus = new GuiButtonSimple(0,area, 12, 12, "-")
    {
    @Override
    public boolean handleMousePressed(int x, int y, int num)
      {
      if(super.handleMousePressed(x, y, num) && GuiModelEditor.model!=null && gui.getSelectedPiece()!=null && gui.getSelectedPrimitive()!=null)
        {   
        PrimitiveBox box = (PrimitiveBox)gui.getSelectedPrimitive();
        box.setBounds(box.x1(), box.y1(), box.z1(), box.width()-1 * setup.scale, box.height(), box.length());
        updateButtonValues(); 
        }
      return true;
      }
    };  
  primitiveX2Minus.updateRenderPos(col2, totalHeight);
 area.addGuiElement(primitiveX2Minus);
  
  primitiveX2Plus = new GuiButtonSimple(0,area, 12, 12, "+")
    {
    @Override
    public boolean handleMousePressed(int x, int y, int num)
      {
      if(super.handleMousePressed(x, y, num) && GuiModelEditor.model!=null && gui.getSelectedPiece()!=null && gui.getSelectedPrimitive()!=null)
        {
        PrimitiveBox box = (PrimitiveBox)gui.getSelectedPrimitive();
        box.setBounds(box.x1(), box.y1(), box.z1(), box.width()+1 * setup.scale, box.height(), box.length());
        updateButtonValues();
        }
      return true;
      }
    };  
  primitiveX2Plus.updateRenderPos(col4, totalHeight);
 area.addGuiElement(primitiveX2Plus);
  
  primitiveX2Input = new GuiNumberInputLine(0,area, 20, 12, 4, "0")
    {
    @Override
    public void onElementActivated()
      {
      if(GuiModelEditor.model!=null && gui.getSelectedPiece()!=null && gui.getSelectedPrimitive()!=null)
        {
        PrimitiveBox box = (PrimitiveBox)gui.getSelectedPrimitive();
        box.setBounds(box.x1(), box.y1(), box.z1(), getFloatVal() * setup.scale, box.height(), box.length());   
        updateButtonValues();
        }
      }
    };
  primitiveX2Input.setValue(0.f);
  primitiveX2Input.updateRenderPos(col3, totalHeight);
 area.addGuiElement(primitiveX2Input);
  
  label = new GuiString(0,area, 25, 12, "B:X2");
  label.updateRenderPos(col1, totalHeight);
 area.addGuiElement(label);
  
  totalHeight+=12;
  
  
  primitiveY2Minus = new GuiButtonSimple(0,area, 12, 12, "-")
    {
    @Override
    public boolean handleMousePressed(int x, int y, int num)
      {
      if(super.handleMousePressed(x, y, num) && GuiModelEditor.model!=null && gui.getSelectedPiece()!=null && gui.getSelectedPrimitive()!=null)
        {   
        PrimitiveBox box = (PrimitiveBox)gui.getSelectedPrimitive();
        box.setBounds(box.x1(), box.y1(), box.z1(), box.width(), box.height()-1 * setup.scale, box.length());
        updateButtonValues();
        }
      return true;
      }
    };  
  primitiveY2Minus.updateRenderPos(col2, totalHeight);
 area.addGuiElement(primitiveY2Minus);
  
  primitiveY2Plus = new GuiButtonSimple(0,area, 12, 12, "+")
    {
    @Override
    public boolean handleMousePressed(int x, int y, int num)
      {
      if(super.handleMousePressed(x, y, num) && GuiModelEditor.model!=null && gui.getSelectedPiece()!=null && gui.getSelectedPrimitive()!=null)
        {
        PrimitiveBox box = (PrimitiveBox)gui.getSelectedPrimitive();
        box.setBounds(box.x1(), box.y1(), box.z1(), box.width(), box.height()+1 * setup.scale, box.length());
        updateButtonValues();
        }
      return true;
      }
    };  
  primitiveY2Plus.updateRenderPos(col4, totalHeight);
 area.addGuiElement(primitiveY2Plus);
  
  primitiveY2Input = new GuiNumberInputLine(0,area, 20, 12, 4, "0")
    {
    @Override
    public void onElementActivated()
      {
      if(GuiModelEditor.model!=null && gui.getSelectedPiece()!=null && gui.getSelectedPrimitive()!=null)
        {
        PrimitiveBox box = (PrimitiveBox)gui.getSelectedPrimitive();
        box.setBounds(box.x1(), box.y1(), box.z1(), box.width(), getFloatVal() * setup.scale, box.length()); 
        updateButtonValues();
        }
      }
    };
  primitiveY2Input.setValue(0.f);
  primitiveY2Input.updateRenderPos(col3, totalHeight);
 area.addGuiElement(primitiveY2Input);
  
  label = new GuiString(0,area, 25, 12, "B:Y2");
  label.updateRenderPos(col1, totalHeight);
 area.addGuiElement(label);
  
  totalHeight+=12;
  
  
  primitiveZ2Minus = new GuiButtonSimple(0,area, 12, 12, "-")
    {
    @Override
    public boolean handleMousePressed(int x, int y, int num)
      {
      if(super.handleMousePressed(x, y, num) && GuiModelEditor.model!=null && gui.getSelectedPiece()!=null && gui.getSelectedPrimitive()!=null)
        {   
        PrimitiveBox box = (PrimitiveBox)gui.getSelectedPrimitive();
        box.setBounds(box.x1(), box.y1(), box.z1(), box.width(), box.height(), box.length()-1 * setup.scale);
        updateButtonValues();
        }
      return true;
      }
    };  
  primitiveZ2Minus.updateRenderPos(col2, totalHeight);
 area.addGuiElement(primitiveZ2Minus);
  
  primitiveZ2Plus = new GuiButtonSimple(0,area, 12, 12, "+")
    {
    @Override
    public boolean handleMousePressed(int x, int y, int num)
      {
      if(super.handleMousePressed(x, y, num) && GuiModelEditor.model!=null && gui.getSelectedPiece()!=null && gui.getSelectedPrimitive()!=null)
        {
        PrimitiveBox box = (PrimitiveBox)gui.getSelectedPrimitive();
        box.setBounds(box.x1(), box.y1(), box.z1(), box.width(), box.height(), box.length()+1 * setup.scale);
        updateButtonValues();
        }
      return true;
      }
    };  
  primitiveZ2Plus.updateRenderPos(col4, totalHeight);
  area.addGuiElement(primitiveZ2Plus);
  
  primitiveZ2Input = new GuiNumberInputLine(0,area, 20, 12, 4, "0")
    {
    @Override
    public void onElementActivated()
      {
      if(GuiModelEditor.model!=null && gui.getSelectedPiece()!=null && gui.getSelectedPrimitive()!=null)
        {
        PrimitiveBox box = (PrimitiveBox)gui.getSelectedPrimitive();
        box.setBounds(box.x1(), box.y1(), box.z1(), box.width(), box.height(), getFloatVal() * setup.scale);   
        updateButtonValues();
        }
      }
    };
  primitiveZ2Input.setValue(0.f);
  primitiveZ2Input.updateRenderPos(col3, totalHeight);
  area.addGuiElement(primitiveZ2Input);
  
  label = new GuiString(0,area, 25, 12, "B:Z2");
  label.updateRenderPos(col1, totalHeight);
  area.addGuiElement(label);
  
  totalHeight+=12;
  
  area.updateTotalHeight(totalHeight);
  }

@Override
public void updateButtonValues()
  {
  super.updateButtonValues();
  float scale = setup.scale;
  PrimitiveBox box = (PrimitiveBox)gui.getSelectedPrimitive();
  primitiveX1Input.setValue(gui.getSelectedPrimitive()==null ? 0.f : box.x1()/scale);
  primitiveY1Input.setValue(gui.getSelectedPrimitive()==null ? 0.f : box.y1()/scale);
  primitiveZ1Input.setValue(gui.getSelectedPrimitive()==null ? 0.f : box.z1()/scale);
  
  primitiveX2Input.setValue(gui.getSelectedPrimitive()==null ? 0.f : box.width()/scale);
  primitiveY2Input.setValue(gui.getSelectedPrimitive()==null ? 0.f : box.height()/scale);
  primitiveZ2Input.setValue(gui.getSelectedPrimitive()==null ? 0.f : box.length()/scale); 
  }


}
