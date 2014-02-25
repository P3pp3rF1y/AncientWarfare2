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

import net.minecraft.util.MathHelper;

import org.lwjgl.input.Keyboard;

import shadowmage.ancient_framework.client.gui.elements.GuiButtonSimple;
import shadowmage.ancient_framework.client.gui.elements.GuiNumberInputLine;
import shadowmage.ancient_framework.client.gui.elements.GuiScrollableArea;
import shadowmage.ancient_framework.client.gui.elements.GuiString;
import shadowmage.ancient_framework.client.model.PrimitiveTriangle;
import shadowmage.ancient_framework.common.config.AWLog;
import shadowmage.ancient_framework.common.utils.Trig;

public class PrimitiveTriangleUVSetup extends PrimitiveUVSetup
{

GuiButtonSimple xMinus;
GuiButtonSimple xPlus;

GuiButtonSimple yMinus;
GuiButtonSimple yPlus;

GuiNumberInputLine xInput;
GuiNumberInputLine yInput;

GuiButtonSimple u1Minus;
GuiButtonSimple u1Plus;
GuiNumberInputLine u1Input;
GuiButtonSimple v1Minus;
GuiButtonSimple v1Plus;
GuiNumberInputLine v1Input;

GuiButtonSimple u2Minus;
GuiButtonSimple u2Plus;
GuiNumberInputLine u2Input;
GuiButtonSimple v2Minus;
GuiButtonSimple v2Plus;
GuiNumberInputLine v2Input;

GuiButtonSimple u3Minus;
GuiButtonSimple u3Plus;
GuiNumberInputLine u3Input;
GuiButtonSimple v3Minus;
GuiButtonSimple v3Plus;
GuiNumberInputLine v3Input;

public PrimitiveTriangleUVSetup(GuiUVMap gui)
  {
  super(gui);
  }

@Override
public void addControls(GuiScrollableArea area)
  { 
  super.addControls(area);
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
      PrimitiveTriangle box = (PrimitiveTriangle) gui.selectedPrimitive;
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
      PrimitiveTriangle box = (PrimitiveTriangle) gui.selectedPrimitive;
      box.setTx(box.tx() + 1);
      xInput.setIntegerValue((int) box.tx());
      box.setCompiled(false);
      gui.updateImage();
      }
    };
  xPlus.updateRenderPos(col4, totalHeight);
  area.addGuiElement(xPlus);
  
  xInput = new GuiNumberInputLine(0, area, 25, 12, 10, "0")
    {
    @Override
    public void onElementActivated()
      {
      PrimitiveTriangle box = (PrimitiveTriangle) gui.selectedPrimitive;
      box.setTx(getIntVal());
      gui.updateImage();
      }
    };
  xInput.updateRenderPos(col2, totalHeight);
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
      PrimitiveTriangle box = (PrimitiveTriangle) gui.selectedPrimitive;
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
      PrimitiveTriangle box = (PrimitiveTriangle) gui.selectedPrimitive;
      box.setTy(box.ty() + 1);
      yInput.setIntegerValue((int) box.ty());
      box.setCompiled(false);
      gui.updateImage();
      }
    };
  yPlus.updateRenderPos(col4, totalHeight);
  area.addGuiElement(yPlus);
  
  yInput = new GuiNumberInputLine(0, area, 25, 12, 10, "0")
    {
    @Override
    public void onElementActivated()
      {
      PrimitiveTriangle box = (PrimitiveTriangle) gui.selectedPrimitive;
      box.setTy(getIntVal());
      gui.updateImage();
      }
    };
  yInput.updateRenderPos(col2, totalHeight);
  yInput.setAsIntegerValue();
  area.addGuiElement(yInput);
  
  totalHeight+=12;
  
  
  label = new GuiString(0, area, 25, 12, "T:U1");
  label.updateRenderPos(col1, totalHeight);
  area.addGuiElement(label);
  
  u1Minus = new GuiButtonSimple(0, area, 12, 12, "-")
    {
    @Override
    public void onElementActivated()
      {
      PrimitiveTriangle box = (PrimitiveTriangle) gui.selectedPrimitive;
      box.setUV(box.u1()-1, box.v1(), box.u2(), box.v2(), box.u3(), box.v3());
      u1Input.setValue(box.u1());
      box.setCompiled(false);
      gui.updateImage();
      }
    };
  u1Minus.updateRenderPos(col2, totalHeight);
  area.addGuiElement(u1Minus);
  
  u1Plus = new GuiButtonSimple(0, area, 12, 12, "+")
    {
    @Override
    public void onElementActivated()
      {
      PrimitiveTriangle box = (PrimitiveTriangle) gui.selectedPrimitive;
      box.setUV(box.u1()+1, box.v1(), box.u2(), box.v2(), box.u3(), box.v3());
      u1Input.setValue(box.u1());
      box.setCompiled(false);
      gui.updateImage();
      }
    };
  u1Plus.updateRenderPos(col4, totalHeight);
  area.addGuiElement(u1Plus);
  
  u1Input = new GuiNumberInputLine(0, area, 25, 12, 5, "0")
    {
    @Override
    public void onElementActivated()
      {
      PrimitiveTriangle box = (PrimitiveTriangle) gui.selectedPrimitive;
      box.setUV(getFloatVal(), box.v1(), box.u2(), box.v2(), box.u3(), box.v3());
      box.setCompiled(false);
      gui.updateImage();
      }
    };
  u1Input.setValue(((PrimitiveTriangle) gui.selectedPrimitive).u1());
  u1Input.updateRenderPos(col3, totalHeight);
  area.addGuiElement(u1Input);
  
  totalHeight+=12;
  
  
  label = new GuiString(0, area, 25, 12, "T:V1");
  label.updateRenderPos(col1, totalHeight);
  area.addGuiElement(label);
  
  v1Minus = new GuiButtonSimple(0, area, 12, 12, "-")
    {
    @Override
    public void onElementActivated()
      {
      PrimitiveTriangle box = (PrimitiveTriangle) gui.selectedPrimitive;
      box.setUV(box.v1()-1, box.v1(), box.u2(), box.v2(), box.u3(), box.v3());
      v1Input.setValue(box.v1());
      box.setCompiled(false);
      gui.updateImage();
      }
    };
  v1Minus.updateRenderPos(col2, totalHeight);
  area.addGuiElement(v1Minus);
  
  v1Plus = new GuiButtonSimple(0, area, 12, 12, "+")
    {
    @Override
    public void onElementActivated()
      {
      PrimitiveTriangle box = (PrimitiveTriangle) gui.selectedPrimitive;
      box.setUV(box.v1()+1, box.v1(), box.u2(), box.v2(), box.u3(), box.v3());
      v1Input.setValue(box.v1());
      box.setCompiled(false);
      gui.updateImage();
      }
    };
  v1Plus.updateRenderPos(col4, totalHeight);
  area.addGuiElement(v1Plus);
  
  v1Input = new GuiNumberInputLine(0, area, 25, 12, 5, "0")
    {
    @Override
    public void onElementActivated()
      {
      PrimitiveTriangle box = (PrimitiveTriangle) gui.selectedPrimitive;
      box.setUV(box.u1(), getFloatVal(), box.u2(), box.v2(), box.u3(), box.v3());
      box.setCompiled(false);
      gui.updateImage();
      }
    };
  v1Input.setValue(((PrimitiveTriangle) gui.selectedPrimitive).v1());
  v1Input.updateRenderPos(col3, totalHeight);
  area.addGuiElement(v1Input);
  
  totalHeight+=12;
  
  
  label = new GuiString(0, area, 25, 12, "T:U2");
  label.updateRenderPos(col1, totalHeight);
  area.addGuiElement(label);
  
  u2Minus = new GuiButtonSimple(0, area, 12, 12, "-")
    {
    @Override
    public void onElementActivated()
      {
      PrimitiveTriangle box = (PrimitiveTriangle) gui.selectedPrimitive;
      box.setUV(box.u1(), box.v1(), box.u2()-1, box.v2(), box.u3(), box.v3());
      u2Input.setValue(box.u2());
      box.setCompiled(false);
      gui.updateImage();
      }
    };
  u2Minus.updateRenderPos(col2, totalHeight);
  area.addGuiElement(u2Minus);
  
  u2Plus = new GuiButtonSimple(0, area, 12, 12, "+")
    {
    @Override
    public void onElementActivated()
      {
      PrimitiveTriangle box = (PrimitiveTriangle) gui.selectedPrimitive;
      box.setUV(box.u1(), box.v1(), box.u2()+1, box.v2(), box.u3(), box.v3());
      u2Input.setValue(box.u2());
      box.setCompiled(false);
      gui.updateImage();
      }
    };
  u2Plus.updateRenderPos(col4, totalHeight);
  area.addGuiElement(u2Plus);
  
  u2Input = new GuiNumberInputLine(0, area, 25, 12, 5, "0")
    {
    @Override
    public void onElementActivated()
      {
      PrimitiveTriangle box = (PrimitiveTriangle) gui.selectedPrimitive;
      box.setUV(box.u1(), box.v1(), getFloatVal(), box.v2(), box.u3(), box.v3());
      box.setCompiled(false);
      gui.updateImage();
      }
    };
  u2Input.setValue(((PrimitiveTriangle) gui.selectedPrimitive).u2());
  u2Input.updateRenderPos(col3, totalHeight);
  area.addGuiElement(u2Input);
  
  totalHeight+=12;
  
  
  label = new GuiString(0, area, 25, 12, "T:v2");
  label.updateRenderPos(col1, totalHeight);
  area.addGuiElement(label);
  
  v2Minus = new GuiButtonSimple(0, area, 12, 12, "-")
    {
    @Override
    public void onElementActivated()
      {
      PrimitiveTriangle box = (PrimitiveTriangle) gui.selectedPrimitive;
      box.setUV(box.u1(), box.v1(), box.v2(), box.v2()-1, box.u3(), box.v3());
      v2Input.setValue(box.v2());
      box.setCompiled(false);
      gui.updateImage();
      }
    };
  v2Minus.updateRenderPos(col2, totalHeight);
  area.addGuiElement(v2Minus);
  
  v2Plus = new GuiButtonSimple(0, area, 12, 12, "+")
    {
    @Override
    public void onElementActivated()
      {
      PrimitiveTriangle box = (PrimitiveTriangle) gui.selectedPrimitive;
      box.setUV(box.u1(), box.v1(), box.v2(), box.v2()+1, box.u3(), box.v3());
      v2Input.setValue(box.v2());
      box.setCompiled(false);
      gui.updateImage();
      }
    };
  v2Plus.updateRenderPos(col4, totalHeight);
  area.addGuiElement(v2Plus);
  
  v2Input = new GuiNumberInputLine(0, area, 25, 12, 5, "0")
    {
    @Override
    public void onElementActivated()
      {
      PrimitiveTriangle box = (PrimitiveTriangle) gui.selectedPrimitive;
      box.setUV(box.u1(), box.v1(), box.u2(), getFloatVal(), box.u3(), box.v3());
      box.setCompiled(false);
      gui.updateImage();
      }
    };
  v2Input.setValue(((PrimitiveTriangle) gui.selectedPrimitive).v2());
  v2Input.updateRenderPos(col3, totalHeight);
  area.addGuiElement(v2Input);
  
  totalHeight+=12;
  
  
  label = new GuiString(0, area, 25, 12, "T:U3");
  label.updateRenderPos(col1, totalHeight);
  area.addGuiElement(label);
  
  u3Minus = new GuiButtonSimple(0, area, 12, 12, "-")
    {
    @Override
    public void onElementActivated()
      {
      PrimitiveTriangle box = (PrimitiveTriangle) gui.selectedPrimitive;
      box.setUV(box.u1(), box.v1(), box.u2(), box.v2(), box.u3()-1, box.v3());
      u3Input.setValue(box.u3());
      box.setCompiled(false);
      gui.updateImage();
      }
    };
  u3Minus.updateRenderPos(col2, totalHeight);
  area.addGuiElement(u3Minus);
  
  u3Plus = new GuiButtonSimple(0, area, 12, 12, "+")
    {
    @Override
    public void onElementActivated()
      {
      PrimitiveTriangle box = (PrimitiveTriangle) gui.selectedPrimitive;
      box.setUV(box.u1(), box.v1(), box.u2(), box.v2(), box.u3()+1, box.v3());
      u3Input.setValue(box.u3());
      box.setCompiled(false);
      gui.updateImage();
      }
    };
  u3Plus.updateRenderPos(col4, totalHeight);
  area.addGuiElement(u3Plus);
  
  u3Input = new GuiNumberInputLine(0, area, 25, 12, 5, "0")
    {
    @Override
    public void onElementActivated()
      {
      PrimitiveTriangle box = (PrimitiveTriangle) gui.selectedPrimitive;
      box.setUV(box.u1(), box.v1(), box.u2(), box.v2(), getFloatVal(), box.v3());
      box.setCompiled(false);
      gui.updateImage();
      }
    };
  u3Input.setValue(((PrimitiveTriangle) gui.selectedPrimitive).u3());
  u3Input.updateRenderPos(col3, totalHeight);
  area.addGuiElement(u3Input);
  
  totalHeight+=12;
  
  
  label = new GuiString(0, area, 25, 12, "T:V3");
  label.updateRenderPos(col1, totalHeight);
  area.addGuiElement(label);
  
  v3Minus = new GuiButtonSimple(0, area, 12, 12, "-")
    {
    @Override
    public void onElementActivated()
      {
      PrimitiveTriangle box = (PrimitiveTriangle) gui.selectedPrimitive;
      box.setUV(box.u1(), box.v1(), box.u2(), box.v2(), box.v3(), box.v3()-1);
      v3Input.setValue(box.v3());
      box.setCompiled(false);
      gui.updateImage();
      }
    };
  v3Minus.updateRenderPos(col2, totalHeight);
  area.addGuiElement(v3Minus);
  
  v3Plus = new GuiButtonSimple(0, area, 12, 12, "+")
    {
    @Override
    public void onElementActivated()
      {
      PrimitiveTriangle box = (PrimitiveTriangle) gui.selectedPrimitive;
      box.setUV(box.u1(), box.v1(), box.u2(), box.v2(), box.v3(), box.v3()+1);
      v3Input.setValue(box.v3());
      box.setCompiled(false);
      gui.updateImage();
      }
    };
  v3Plus.updateRenderPos(col4, totalHeight);
  area.addGuiElement(v3Plus);
  
  v3Input = new GuiNumberInputLine(0, area, 25, 12, 5, "0")
    {
    @Override
    public void onElementActivated()
      {
      PrimitiveTriangle box = (PrimitiveTriangle) gui.selectedPrimitive;
      box.setUV(box.u1(), box.v1(), box.u2(), box.v2(), box.u3(), getFloatVal());
      box.setCompiled(false);
      gui.updateImage();
      }
    };
  v3Input.setValue(((PrimitiveTriangle) gui.selectedPrimitive).v3());
  v3Input.updateRenderPos(col3, totalHeight);
  area.addGuiElement(v3Input);
  
  totalHeight+=12;
  
  
  GuiButtonSimple rp = new GuiButtonSimple(0, area, 24, 12, "R-")
    {
    @Override
    public void onElementActivated()
      {      
      rotateTriangleUV((PrimitiveTriangle)gui.selectedPrimitive, Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) ? -5 : -1);
      }
    };
  rp.updateRenderPos(0, totalHeight);
  area.addGuiElement(rp);
  
  rp = new GuiButtonSimple(0, area, 24, 12, "R+")
    {
    @Override
    public void onElementActivated()
      {      
      rotateTriangleUV((PrimitiveTriangle)gui.selectedPrimitive, Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) ? 5 : 1);
      }
    };
  rp.updateRenderPos(26, totalHeight);
  area.addGuiElement(rp);
  
  totalHeight+=12;
  
  
  rp = new GuiButtonSimple(0, area, 24, 12, "RC")
    {
    @Override
    public void onElementActivated()
      {      
      PrimitiveTriangle t = (PrimitiveTriangle)gui.selectedPrimitive;
      t.recalcUV();
      t.setCompiled(false);
      gui.updateImage();
      u1Input.setValue(t.u1());
      v1Input.setValue(t.v1());
      u2Input.setValue(t.u2());
      v2Input.setValue(t.v2());
      u3Input.setValue(t.u3());
      v3Input.setValue(t.v3());
      }
    };
  rp.updateRenderPos(0, totalHeight);
  area.addGuiElement(rp);
  
  totalHeight+=12;
  
  area.updateTotalHeight(totalHeight); 
  }

private void rotateTriangleUV(PrimitiveTriangle t, float degrees)
  {
  t.rotateTriangleUV(degrees);
  gui.updateImage();
  u1Input.setValue(t.u1());
  v1Input.setValue(t.v1());
  u2Input.setValue(t.u2());
  v2Input.setValue(t.v2());
  u3Input.setValue(t.u3());
  v3Input.setValue(t.v3());
  }

}
