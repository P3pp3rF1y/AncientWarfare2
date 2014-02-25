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
import shadowmage.ancient_framework.client.gui.GuiContainerAdvanced;
import shadowmage.ancient_framework.client.gui.elements.GuiButtonSimple;
import shadowmage.ancient_framework.client.gui.elements.IGuiElement;
import shadowmage.ancient_framework.client.model.PrimitiveBox;
import shadowmage.ancient_framework.client.model.PrimitiveQuad;
import shadowmage.ancient_framework.client.model.PrimitiveTriangle;
import shadowmage.ancient_framework.common.container.ContainerBase;

public class GuiNewPrimitive extends GuiContainerAdvanced
{

private GuiModelEditor parentGui;

public GuiNewPrimitive(ContainerBase container, GuiModelEditor parent)
  {
  super(container);
  this.parentGui = parent;
  }

@Override
public void onElementActivated(IGuiElement element)
  {
  
  }

@Override
public int getXSize()
  {
  return 116;
  }

@Override
public int getYSize()
  {
  return 40;
  }

@Override
public void renderExtraBackGround(int mouseX, int mouseY, float partialTime)
  {
  // TODO Auto-generated method stub

  }

@Override
public void updateScreenContents()
  {
  // TODO Auto-generated method stub

  }

@Override
public void setupControls()
  {  
  GuiButtonSimple button = new GuiButtonSimple(1, this, 50, 12, "Cancel")
    {
    @Override
    public void onElementActivated()
      {
      Minecraft.getMinecraft().displayGuiScreen(parentGui);
      parentGui.refreshGui();
      }
    };
  button.updateRenderPos(116-8-50, 8);
  this.addElement(button);
    
  button = new GuiButtonSimple(2, this, 35, 12, "Box")
    {
    @Override
    public void onElementActivated()
      {
      if(parentGui.getSelectedPiece()!=null)
        {
        PrimitiveBox b = new PrimitiveBox(parentGui.getSelectedPiece());
        b.setBounds(-0.5f, -0.5f, -0.5f, 1, 1, 1);
        b.setOrigin(0, 0, 0);
        b.setRotation(0, 0, 0);
        parentGui.getSelectedPiece().addPrimitive(b);        
        parentGui.setSelectedPrimitive(b);        
        Minecraft.getMinecraft().displayGuiScreen(parentGui);
        parentGui.refreshGui();
        }
      }
    };
  button.updateRenderPos(8, 8);
  this.addElement(button);  
  
  button = new GuiButtonSimple(3, this, 35, 12, "Quad")
    {
    @Override
    public void onElementActivated()
      {
      if(parentGui.getSelectedPiece()!=null)
        {
        PrimitiveQuad b = new PrimitiveQuad(parentGui.getSelectedPiece());        
        b.setBounds(-0.5f, -0.5f, 1, 1);
        b.setOrigin(0, 0, 0);
        b.setRotation(0, 0, 0);
        parentGui.getSelectedPiece().addPrimitive(b); 
        parentGui.setSelectedPrimitive(b);      
        Minecraft.getMinecraft().displayGuiScreen(parentGui);
        parentGui.refreshGui();
        }
      }
    };
  button.updateRenderPos(8, 8+12+4);
  this.addElement(button); 
  
  button = new GuiButtonSimple(4, this, 60, 12, "Triangle")
    {
    @Override
    public void onElementActivated()
      {
      if(parentGui.getSelectedPiece()!=null)
        {
        PrimitiveTriangle b = new PrimitiveTriangle(parentGui.getSelectedPiece());
        b.setBounds(-0.5f, 0.f, 0.f, 0.5f, 0.f, 0.f, 0, -.5f, 0.f);
        b.setOrigin(0, 0, 0);
        b.setRotation(0, 0, 0);
        parentGui.getSelectedPiece().addPrimitive(b);        
        parentGui.setSelectedPrimitive(b);        
        Minecraft.getMinecraft().displayGuiScreen(parentGui);
        parentGui.refreshGui();
        }
      }
    };
  button.updateRenderPos(8+35+4, 8+12+4);
  this.addElement(button); 
  }


@Override
public void updateControls()
  {
  // TODO Auto-generated method stub

  }

}
