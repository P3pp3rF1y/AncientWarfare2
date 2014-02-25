/**
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
import shadowmage.ancient_framework.client.model.PrimitiveQuad;

public class PrimitiveQuadSetup extends PrimitiveGuiSetup
{

private GuiButtonSimple primitiveX1Minus;
private GuiButtonSimple primitiveX1Plus;
private GuiButtonSimple primitiveY1Minus;
private GuiButtonSimple primitiveY1Plus;

private GuiNumberInputLine primitiveX1Input;
private GuiNumberInputLine primitiveY1Input;

private GuiButtonSimple primitiveX2Minus;
private GuiButtonSimple primitiveX2Plus;
private GuiButtonSimple primitiveY2Minus;
private GuiButtonSimple primitiveY2Plus;

private GuiNumberInputLine primitiveX2Input;
private GuiNumberInputLine primitiveY2Input;


public PrimitiveQuadSetup(GuiModelEditor gui, GuiModelEditorSetup setup)
  {
  super(gui, setup);
  }

@Override
public void addElements(GuiScrollableArea area)
  {
  super.addElements(area);
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
        PrimitiveQuad box = (PrimitiveQuad)gui.getSelectedPrimitive();
        box.setBounds(box.x1()-1 * setup.scale, box.y1(), box.width(), box.height());  
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
        PrimitiveQuad box = (PrimitiveQuad)gui.getSelectedPrimitive();
        box.setBounds(box.x1()+1 * setup.scale, box.y1(), box.width(), box.height());
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
        PrimitiveQuad box = (PrimitiveQuad)gui.getSelectedPrimitive();
        box.setBounds(getFloatVal() * setup.scale, box.y1(), box.width(), box.height()); 
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
        PrimitiveQuad box = (PrimitiveQuad)gui.getSelectedPrimitive();
        box.setBounds(box.x1(), box.y1()-1 * setup.scale, box.width(), box.height());
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
        PrimitiveQuad box = (PrimitiveQuad)gui.getSelectedPrimitive();
        box.setBounds(box.x1(), box.y1()+1 * setup.scale, box.width(), box.height());
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
        PrimitiveQuad box = (PrimitiveQuad)gui.getSelectedPrimitive();
        box.setBounds(box.x1(), getFloatVal() * setup.scale, box.width(), box.height());  
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
  
  primitiveX2Minus = new GuiButtonSimple(0,area, 12, 12, "-")
    {
    @Override
    public boolean handleMousePressed(int x, int y, int num)
      {
      if(super.handleMousePressed(x, y, num) && GuiModelEditor.model!=null && gui.getSelectedPiece()!=null && gui.getSelectedPrimitive()!=null)
        {   
        PrimitiveQuad box = (PrimitiveQuad)gui.getSelectedPrimitive();
        box.setBounds(box.x1(), box.y1(), box.width()-1 * setup.scale, box.height());
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
        PrimitiveQuad box = (PrimitiveQuad)gui.getSelectedPrimitive();
        box.setBounds(box.x1(), box.y1(), box.width()+1 * setup.scale, box.height());
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
        PrimitiveQuad box = (PrimitiveQuad)gui.getSelectedPrimitive();
        box.setBounds(box.x1(), box.y1(), getFloatVal() * setup.scale, box.height());   
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
        PrimitiveQuad box = (PrimitiveQuad)gui.getSelectedPrimitive();
        box.setBounds(box.x1(), box.y1(), box.width(), box.height()-1 * setup.scale);
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
        PrimitiveQuad box = (PrimitiveQuad)gui.getSelectedPrimitive();
        box.setBounds(box.x1(), box.y1(), box.width(), box.height()+1 * setup.scale);
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
        PrimitiveQuad box = (PrimitiveQuad)gui.getSelectedPrimitive();
        box.setBounds(box.x1(), box.y1(), box.width(), getFloatVal() * setup.scale); 
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
  
  area.updateTotalHeight(totalHeight);
  }

@Override
public void updateButtonValues()
  {
  super.updateButtonValues();
  float scale = setup.scale;
  PrimitiveQuad box = (PrimitiveQuad)gui.getSelectedPrimitive();
  primitiveX1Input.setValue(gui.getSelectedPrimitive()==null ? 0.f : box.x1()/scale);
  primitiveY1Input.setValue(gui.getSelectedPrimitive()==null ? 0.f : box.y1()/scale);
  
  primitiveX2Input.setValue(gui.getSelectedPrimitive()==null ? 0.f : box.width()/scale);
  primitiveY2Input.setValue(gui.getSelectedPrimitive()==null ? 0.f : box.height()/scale);
  }

}
