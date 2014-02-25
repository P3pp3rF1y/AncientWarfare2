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

public class PrimitiveQuadUVSetup extends PrimitiveUVSetup
{
GuiButtonSimple xMinus;
GuiButtonSimple xPlus;

GuiButtonSimple yMinus;
GuiButtonSimple yPlus;

GuiNumberInputLine xInput;
GuiNumberInputLine yInput;


public PrimitiveQuadUVSetup(GuiUVMap gui)
  {
  super(gui);
  }

@Override
public void addControls(GuiScrollableArea area)
  {
  int col1 = 0;
  int col2 = 25;
  int col3 = 25+12+2;
  int col4 = 25+12+2+20+2;  
  int totalHeight = 0;
  
  GuiString label;
  
  label = new GuiString(0, area, 25, 12, "T:X");
  label.updateRenderPos(col1, totalHeight);
  area.addGuiElement(label);
  
  xMinus = new GuiButtonSimple(0, area, 12, 12, "-")
    {
    @Override
    public void onElementActivated()
      {
      PrimitiveQuad box = (PrimitiveQuad) gui.selectedPrimitive;
      box.setTx(box.tx() - 1);
      xInput.setIntegerValue((int) box.tx());
      box.setCompiled(false);
      gui.updateImage();
      }
    };
  xMinus.updateRenderPos(col2, totalHeight);
  area.addGuiElement(xMinus);
  
  xPlus = new GuiButtonSimple(0, area, 12, 12, "+")
    {
    @Override
    public void onElementActivated()
      {
      PrimitiveQuad box = (PrimitiveQuad) gui.selectedPrimitive;
      box.setTx(box.tx() + 1);
      xInput.setIntegerValue((int) box.tx());
      box.setCompiled(false);
      gui.updateImage();
      }
    };
  xPlus.updateRenderPos(col4, totalHeight);
  area.addGuiElement(xPlus);
  
  xInput = new GuiNumberInputLine(0, area, 25, 12, 5, "0")
    {
    @Override
    public void onElementActivated()
      {
      PrimitiveQuad box = (PrimitiveQuad) gui.selectedPrimitive;
      box.setTx(getIntVal());
      gui.updateImage();
      }
    };
  xInput.updateRenderPos(col3, totalHeight);
  xInput.setAsIntegerValue();
  area.addGuiElement(xInput);
  
  totalHeight+=12;
  
  
  label = new GuiString(0, area, 25, 12, "T:Y");
  label.updateRenderPos(col1, totalHeight);
  area.addGuiElement(label);
  
  yMinus = new GuiButtonSimple(0, area, 12, 12, "-")
    {
    @Override
    public void onElementActivated()
      {
      PrimitiveQuad box = (PrimitiveQuad) gui.selectedPrimitive;
      box.setTx(box.tx() - 1);
      xInput.setIntegerValue((int) box.tx());
      box.setCompiled(false);
      gui.updateImage();
      }
    };
  yMinus.updateRenderPos(col2, totalHeight);
  area.addGuiElement(yMinus);
  
  yPlus = new GuiButtonSimple(0, area, 12, 12, "+")
    {
    @Override
    public void onElementActivated()
      {
      PrimitiveQuad box = (PrimitiveQuad) gui.selectedPrimitive;
      box.setTy(box.ty() + 1);
      yInput.setIntegerValue((int) box.ty());
      box.setCompiled(false);
      gui.updateImage();
      }
    };
  yPlus.updateRenderPos(col4, totalHeight);
  area.addGuiElement(yPlus);
  
  yInput = new GuiNumberInputLine(0, area, 25, 12, 5, "0")
    {
    @Override
    public void onElementActivated()
      {
      PrimitiveQuad box = (PrimitiveQuad) gui.selectedPrimitive;
      box.setTy(getIntVal());
      gui.updateImage();
      }
    };
  yInput.updateRenderPos(col3, totalHeight);
  yInput.setAsIntegerValue();
  area.addGuiElement(yInput);
  
  totalHeight+=12;
  
  
  area.updateTotalHeight(totalHeight);
  }

}
