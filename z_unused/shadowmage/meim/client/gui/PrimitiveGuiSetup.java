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

import net.minecraft.client.Minecraft;
import shadowmage.ancient_framework.client.gui.elements.GuiButtonSimple;
import shadowmage.ancient_framework.client.gui.elements.GuiNumberInputLine;
import shadowmage.ancient_framework.client.gui.elements.GuiScrollableArea;
import shadowmage.ancient_framework.client.gui.elements.GuiString;
import shadowmage.ancient_framework.common.container.ContainerBase;

public class PrimitiveGuiSetup
{

private GuiButtonSimple primitiveXMinus;
private GuiButtonSimple primitiveXPlus;
private GuiButtonSimple primitiveYMinus;
private GuiButtonSimple primitiveYPlus;
private GuiButtonSimple primitiveZMinus;
private GuiButtonSimple primitiveZPlus;

private GuiNumberInputLine primitiveXInput;
private GuiNumberInputLine primitiveYInput;
private GuiNumberInputLine primitiveZInput;

private GuiButtonSimple primitiveRXMinus;
private GuiButtonSimple primitiveRXPlus;
private GuiButtonSimple primitiveRYMinus;
private GuiButtonSimple primitiveRYPlus;
private GuiButtonSimple primitiveRZMinus;
private GuiButtonSimple primitiveRZPlus;

private GuiNumberInputLine primitiveRXInput;
private GuiNumberInputLine primitiveRYInput;
private GuiNumberInputLine primitiveRZInput;

private GuiButtonSimple changeBoxOwnerPiece;

private GuiButtonSimple copyBox;

private GuiButtonSimple addBox;
private GuiButtonSimple deleteBox;

protected GuiModelEditor gui;
protected GuiModelEditorSetup setup;

public PrimitiveGuiSetup(GuiModelEditor gui, GuiModelEditorSetup setup)
  {
  this.gui = gui;
  this.setup = setup;
  }

public void addElements(GuiScrollableArea area)
  {
  int totalHeight = 0;
  area.elements.clear();
  int col1 = 0;
  int col2 = 25;
  int col3 = 25+12+2;
  int col4 = 25+12+2+20+2;
  
  addBox = new GuiButtonSimple(0, area, 84, 12, "Add Primitive")
    {
    @Override
    public boolean handleMousePressed(int x, int y, int num)
      {
      if(super.handleMousePressed(x, y, num))
        {
        Minecraft.getMinecraft().displayGuiScreen(new GuiNewPrimitive((ContainerBase) gui.inventorySlots, gui));
        }
      return true;
      }
    };  
  addBox.updateRenderPos(0, totalHeight);
  totalHeight+=12;
  area.elements.add(addBox);
  
  deleteBox = new GuiButtonSimple(0, area, 84, 12, "Delete Prim")
    {
    @Override
    public boolean handleMousePressed(int x, int y, int num)
      {
      if(super.handleMousePressed(x, y, num) && GuiModelEditor.model!=null && gui.getSelectedPiece()!=null && gui.getSelectedPrimitive()!=null)
        {
        if(gui.getSelectedPiece()!=null)
          {
          gui.getSelectedPiece().removePrimitive(gui.getSelectedPrimitive());
          gui.setSelectedPrimitive(null);
          }
        }
      return true;
      }
    };  
  deleteBox.updateRenderPos(0, totalHeight);
  totalHeight+=12;
  area.elements.add(deleteBox);
  
  changeBoxOwnerPiece = new GuiButtonSimple(0, area, 84, 12, "Swap Prim Owner")
    {
    @Override
    public boolean handleMousePressed(int x, int y, int num)
      {
      if(super.handleMousePressed(x, y, num) && GuiModelEditor.model!=null && gui.getSelectedPiece()!=null && gui.getSelectedPrimitive()!=null)
        {
        Minecraft.getMinecraft().displayGuiScreen(new GuiSwapPrimitiveParent((ContainerBase) gui.inventorySlots, gui));
        }
      return true;
      }
    };  
  changeBoxOwnerPiece.updateRenderPos(0, totalHeight);
  totalHeight+=12;
  area.elements.add(changeBoxOwnerPiece);
  
  
  copyBox = new GuiButtonSimple(0, area, 84, 12, "Copy Primitive")
    {
    @Override
    public void onElementActivated()
      {
      gui.getSelectedPiece().addPrimitive(gui.getSelectedPrimitive().copy());
      setup.addPrimitiveControls();
      gui.refreshGui();
      }
    };  
  copyBox.updateRenderPos(0, totalHeight);
  totalHeight+=12;
  area.elements.add(copyBox);
   
  
  primitiveXMinus = new GuiButtonSimple(0,area, 12, 12, "-")
    {
    @Override
    public boolean handleMousePressed(int x, int y, int num)
      {
      if(super.handleMousePressed(x, y, num) && GuiModelEditor.model!=null && gui.getSelectedPiece()!=null && gui.getSelectedPrimitive()!=null)
        {   
        gui.getSelectedPrimitive().setOrigin(gui.getSelectedPrimitive().x()-1 * setup.scale, gui.getSelectedPrimitive().y(), gui.getSelectedPrimitive().z());          
        updateButtonValues();
        }
      return true;
      }
    };  
  primitiveXMinus.updateRenderPos(col2, totalHeight);
  area.addGuiElement(primitiveXMinus);
  
  primitiveXPlus = new GuiButtonSimple(0,area, 12, 12, "+")
    {
    @Override
    public boolean handleMousePressed(int x, int y, int num)
      {
      if(super.handleMousePressed(x, y, num) && GuiModelEditor.model!=null && gui.getSelectedPiece()!=null && gui.getSelectedPrimitive()!=null)
        {
        gui.getSelectedPrimitive().setOrigin(gui.getSelectedPrimitive().x()+1 * setup.scale, gui.getSelectedPrimitive().y(), gui.getSelectedPrimitive().z());  
        updateButtonValues();
        }
      return true;
      }
    };  
  primitiveXPlus.updateRenderPos(col4, totalHeight);
  area.addGuiElement(primitiveXPlus);
  
  primitiveXInput = new GuiNumberInputLine(0,area, 20, 12, 4, "0")
    {
    @Override
    public void onElementActivated()
      {
      if(GuiModelEditor.model!=null && gui.getSelectedPiece()!=null && gui.getSelectedPrimitive()!=null)
        {        
        gui.getSelectedPrimitive().setOrigin(getFloatVal() * setup.scale, gui.getSelectedPrimitive().y(), gui.getSelectedPrimitive().z());          
        updateButtonValues();
        }
      }
    };
  primitiveXInput.setValue(0.f);
  primitiveXInput.updateRenderPos(col3, totalHeight);
  area.addGuiElement(primitiveXInput);
  
  GuiString label = new GuiString(0,area, 25, 12, "B:OX");
  label.updateRenderPos(col1, totalHeight);
  area.addGuiElement(label);
  
  totalHeight+=12;
  
  
  primitiveYMinus = new GuiButtonSimple(0,area, 12, 12, "-")
    {
    @Override
    public boolean handleMousePressed(int x, int y, int num)
      {
      if(super.handleMousePressed(x, y, num) && GuiModelEditor.model!=null && gui.getSelectedPiece()!=null && gui.getSelectedPrimitive()!=null)
        {   
        gui.getSelectedPrimitive().setOrigin(gui.getSelectedPrimitive().x(), gui.getSelectedPrimitive().y()-1 * setup.scale, gui.getSelectedPrimitive().z());  
        updateButtonValues();
        }
      return true;
      }
    };  
  primitiveYMinus.updateRenderPos(col2, totalHeight);
  area.addGuiElement(primitiveYMinus);
  
  primitiveYPlus = new GuiButtonSimple(0,area, 12, 12, "+")
    {
    @Override
    public boolean handleMousePressed(int x, int y, int num)
      {
      if(super.handleMousePressed(x, y, num) && GuiModelEditor.model!=null && gui.getSelectedPiece()!=null && gui.getSelectedPrimitive()!=null)
        {
        gui.getSelectedPrimitive().setOrigin(gui.getSelectedPrimitive().x(), gui.getSelectedPrimitive().y()+1 * setup.scale, gui.getSelectedPrimitive().z());
        updateButtonValues();
        }
      return true;
      }
    };  
  primitiveYPlus.updateRenderPos(col4, totalHeight);
  area.addGuiElement(primitiveYPlus);
  
  primitiveYInput = new GuiNumberInputLine(0,area, 20, 12, 4, "0")
    {
    @Override
    public void onElementActivated()
      {
      if(GuiModelEditor.model!=null && gui.getSelectedPiece()!=null && gui.getSelectedPrimitive()!=null)
        {
        gui.getSelectedPrimitive().setOrigin(gui.getSelectedPrimitive().x(), getFloatVal() * setup.scale, gui.getSelectedPrimitive().z());  
        updateButtonValues();
        }
      }
    };
  primitiveYInput.setValue(0.f);
  primitiveYInput.updateRenderPos(col3, totalHeight);
  area.addGuiElement(primitiveYInput);
  
  label = new GuiString(0,area, 25, 12, "B:OY");
  label.updateRenderPos(col1, totalHeight);
  area.addGuiElement(label);
  
  totalHeight+=12;
  
  
  primitiveZMinus = new GuiButtonSimple(0,area, 12, 12, "-")
    {
    @Override
    public boolean handleMousePressed(int x, int y, int num)
      {
      if(super.handleMousePressed(x, y, num) && GuiModelEditor.model!=null && gui.getSelectedPiece()!=null && gui.getSelectedPrimitive()!=null)
        {   
        gui.getSelectedPrimitive().setOrigin(gui.getSelectedPrimitive().x(), gui.getSelectedPrimitive().y(), gui.getSelectedPrimitive().z()-1 * setup.scale);  
        updateButtonValues();
        }
      return true;
      }
    };  
  primitiveZMinus.updateRenderPos(col2, totalHeight);
  area.addGuiElement(primitiveZMinus);
  
  primitiveZPlus = new GuiButtonSimple(0,area, 12, 12, "+")
    {
    @Override
    public boolean handleMousePressed(int x, int y, int num)
      {
      if(super.handleMousePressed(x, y, num) && GuiModelEditor.model!=null && gui.getSelectedPiece()!=null && gui.getSelectedPrimitive()!=null)
        {
        gui.getSelectedPrimitive().setOrigin(gui.getSelectedPrimitive().x(), gui.getSelectedPrimitive().y(), gui.getSelectedPrimitive().z()+1 * setup.scale);  
        updateButtonValues();
        }
      return true;
      }
    };  
  primitiveZPlus.updateRenderPos(col4, totalHeight);
  area.addGuiElement(primitiveZPlus);
  
  primitiveZInput = new GuiNumberInputLine(0,area, 20, 12, 4, "0")
    {
    @Override
    public void onElementActivated()
      {
      if(GuiModelEditor.model!=null && gui.getSelectedPiece()!=null && gui.getSelectedPrimitive()!=null)
        {
        gui.getSelectedPrimitive().setOrigin(gui.getSelectedPrimitive().x(), gui.getSelectedPrimitive().y(), getFloatVal() * setup.scale);  
        updateButtonValues();
        }
      }    
    };
  primitiveZInput.setValue(0.f);
  primitiveZInput.updateRenderPos(col3, totalHeight);
  area.addGuiElement(primitiveZInput);
  
  label = new GuiString(0,area, 25, 12, "B:OZ");
  label.updateRenderPos(col1, totalHeight);
  area.addGuiElement(label);
  
  totalHeight+=12;
  
  primitiveRXMinus = new GuiButtonSimple(0,area, 12, 12, "-")
    {
    @Override
    public boolean handleMousePressed(int x, int y, int num)
      {
      if(super.handleMousePressed(x, y, num) && GuiModelEditor.model!=null && gui.getSelectedPiece()!=null && gui.getSelectedPrimitive()!=null)
        { 
        gui.getSelectedPrimitive().setRotation(gui.getSelectedPrimitive().rx()-1, gui.getSelectedPrimitive().ry(), gui.getSelectedPrimitive().rz());  
        updateButtonValues();
        }
      return true;
      }
    };  
  primitiveRXMinus.updateRenderPos(col2, totalHeight);
  area.addGuiElement(primitiveRXMinus);
  
  primitiveRXPlus = new GuiButtonSimple(0,area, 12, 12, "+")
    {
    @Override
    public boolean handleMousePressed(int x, int y, int num)
      {
      if(super.handleMousePressed(x, y, num) && GuiModelEditor.model!=null && gui.getSelectedPiece()!=null && gui.getSelectedPrimitive()!=null)
        {
        gui.getSelectedPrimitive().setRotation(gui.getSelectedPrimitive().rx()+1, gui.getSelectedPrimitive().ry(), gui.getSelectedPrimitive().rz());  
        updateButtonValues();
        }
      return true;
      }
    };  
  primitiveRXPlus.updateRenderPos(col4, totalHeight);
  area.addGuiElement(primitiveRXPlus);
  
  primitiveRXInput = new GuiNumberInputLine(0,area, 20, 12, 4, "0")
    {
    @Override
    public void onElementActivated()
      {
      if(GuiModelEditor.model!=null && gui.getSelectedPiece()!=null && gui.getSelectedPrimitive()!=null)
        {
        gui.getSelectedPrimitive().setRotation(getFloatVal(), gui.getSelectedPrimitive().ry(), gui.getSelectedPrimitive().rz());  
        updateButtonValues();
        }
      }
    };
  primitiveRXInput.setValue(0.f);
  primitiveRXInput.updateRenderPos(col3, totalHeight);
 area.addGuiElement(primitiveRXInput);
  
  label = new GuiString(0,area, 25, 12, "B:RX");
  label.updateRenderPos(col1, totalHeight);
 area.addGuiElement(label);
  
  totalHeight+=12;
  
  
  primitiveRYMinus = new GuiButtonSimple(0,area, 12, 12, "-")
    {
    @Override
    public boolean handleMousePressed(int x, int y, int num)
      {
      if(super.handleMousePressed(x, y, num) && GuiModelEditor.model!=null && gui.getSelectedPiece()!=null && gui.getSelectedPrimitive()!=null)
        {   
        gui.getSelectedPrimitive().setRotation(gui.getSelectedPrimitive().rx(), gui.getSelectedPrimitive().ry()-1, gui.getSelectedPrimitive().rz());  
        updateButtonValues();
        }
      return true;
      }
    };  
  primitiveRYMinus.updateRenderPos(col2, totalHeight);
 area.addGuiElement(primitiveRYMinus);
  
  primitiveRYPlus = new GuiButtonSimple(0,area, 12, 12, "+")
    {
    @Override
    public boolean handleMousePressed(int x, int y, int num)
      {
      if(super.handleMousePressed(x, y, num) && GuiModelEditor.model!=null && gui.getSelectedPiece()!=null && gui.getSelectedPrimitive()!=null)
        {
        gui.getSelectedPrimitive().setRotation(gui.getSelectedPrimitive().rx(), gui.getSelectedPrimitive().ry()+1, gui.getSelectedPrimitive().rz());  
        updateButtonValues();
        }
      return true;
      }
    };  
  primitiveRYPlus.updateRenderPos(col4, totalHeight);
 area.addGuiElement(primitiveRYPlus);
  
  primitiveRYInput = new GuiNumberInputLine(0,area, 20, 12, 4, "0")
    {
    @Override
    public void onElementActivated()
      {
      if(GuiModelEditor.model!=null && gui.getSelectedPiece()!=null && gui.getSelectedPrimitive()!=null)
        {
        gui.getSelectedPrimitive().setRotation(gui.getSelectedPrimitive().rx(), getFloatVal(), gui.getSelectedPrimitive().rz());
        updateButtonValues();
        }
      }
    };
  primitiveRYInput.setValue(0.f);
  primitiveRYInput.updateRenderPos(col3, totalHeight);
 area.addGuiElement(primitiveRYInput);
  
  label = new GuiString(0,area, 25, 12, "B:RY");
  label.updateRenderPos(col1, totalHeight);
 area.addGuiElement(label);
  
  totalHeight+=12;
  
  
  primitiveRZMinus = new GuiButtonSimple(0,area, 12, 12, "-")
    {
    @Override
    public boolean handleMousePressed(int x, int y, int num)
      {
      if(super.handleMousePressed(x, y, num) && GuiModelEditor.model!=null && gui.getSelectedPiece()!=null && gui.getSelectedPrimitive()!=null)
        {   
        gui.getSelectedPrimitive().setRotation(gui.getSelectedPrimitive().rx(), gui.getSelectedPrimitive().ry(), gui.getSelectedPrimitive().rz()-1);  
        updateButtonValues();
        }
      return true;
      }
    };  
  primitiveRZMinus.updateRenderPos(col2, totalHeight);
 area.addGuiElement(primitiveRZMinus);
  
  primitiveRZPlus = new GuiButtonSimple(0,area, 12, 12, "+")
    {
    @Override
    public boolean handleMousePressed(int x, int y, int num)
      {
      if(super.handleMousePressed(x, y, num) && GuiModelEditor.model!=null && gui.getSelectedPiece()!=null && gui.getSelectedPrimitive()!=null)
        {
        gui.getSelectedPrimitive().setRotation(gui.getSelectedPrimitive().rx(), gui.getSelectedPrimitive().ry(), gui.getSelectedPrimitive().rz()+1);
        updateButtonValues();
        }
      return true;
      }
    };  
  primitiveRZPlus.updateRenderPos(col4, totalHeight);
  area.addGuiElement(primitiveRZPlus);
  
  primitiveRZInput = new GuiNumberInputLine(0,area, 20, 12, 4, "0")
    {
    @Override
    public void onElementActivated()
      {
      if(GuiModelEditor.model!=null && gui.getSelectedPiece()!=null && gui.getSelectedPrimitive()!=null)
        {
        gui.getSelectedPrimitive().setRotation(gui.getSelectedPrimitive().rx(), gui.getSelectedPrimitive().ry(), getFloatVal());  
        updateButtonValues();
        }
      }
    };
  primitiveRZInput.setValue(0.f);
  primitiveRZInput.updateRenderPos(col3, totalHeight);
  area.addGuiElement(primitiveRZInput);
  
  label = new GuiString(0,area, 25, 12, "B:RZ");
  label.updateRenderPos(col1, totalHeight);
  area.addGuiElement(label);
  
  totalHeight+=12;
  area.updateTotalHeight(totalHeight);   
  }

public void updateButtonValues()
  {
  float scale = setup.scale;
  primitiveXInput.setValue(gui.getSelectedPrimitive()==null ? 0.f : gui.getSelectedPrimitive().x()/scale);
  primitiveYInput.setValue(gui.getSelectedPrimitive()==null ? 0.f : gui.getSelectedPrimitive().y()/scale);
  primitiveZInput.setValue(gui.getSelectedPrimitive()==null ? 0.f : gui.getSelectedPrimitive().z()/scale);
  
  primitiveRXInput.setValue(gui.getSelectedPrimitive()==null ? 0.f : gui.getSelectedPrimitive().rx());
  primitiveRYInput.setValue(gui.getSelectedPrimitive()==null ? 0.f : gui.getSelectedPrimitive().ry());
  primitiveRZInput.setValue(gui.getSelectedPrimitive()==null ? 0.f : gui.getSelectedPrimitive().rz()); 
  }
}
