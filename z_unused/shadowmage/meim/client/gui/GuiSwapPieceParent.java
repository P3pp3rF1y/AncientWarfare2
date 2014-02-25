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

import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.client.Minecraft;
import shadowmage.ancient_framework.client.gui.GuiContainerAdvanced;
import shadowmage.ancient_framework.client.gui.elements.GuiButtonSimple;
import shadowmage.ancient_framework.client.gui.elements.GuiScrollableArea;
import shadowmage.ancient_framework.client.gui.elements.IGuiElement;
import shadowmage.ancient_framework.client.model.ModelPiece;
import shadowmage.ancient_framework.common.container.ContainerBase;

public class GuiSwapPieceParent extends GuiContainerAdvanced
{

GuiModelEditor parentGui;

public GuiSwapPieceParent(ContainerBase container, GuiModelEditor parent)
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
  return 256;
  }

@Override
public int getYSize()
  {
  return 240;
  }

@Override
public void renderExtraBackGround(int mouseX, int mouseY, float partialTime)
  {

  }

@Override
public void updateScreenContents()
  {

  }

GuiScrollableArea area;

@Override
public void setupControls()
  {
  area = new GuiScrollableArea(0, this, 8, 8, 256-16, 240-16-12-4, 240-16-12-4);
  this.addElement(area);
  ArrayList<ModelPiece> pieces = new ArrayList<ModelPiece>();
  GuiModelEditor.model.getPieces(pieces);
  
  int totalHeight = 0;
  
  GuiButtonSimple button;
  
  
  button = new GuiButtonSimple(0, area, 160, 12, "None")
    {
    @Override
    public void onElementActivated()
      {
      if(parentGui.getSelectedPiece().getParent()!=null)
        {
        parentGui.getSelectedPiece().getParent().removeChild(parentGui.getSelectedPiece()); 
        GuiModelEditor.model.addPiece(parentGui.getSelectedPiece());        
        }
      Minecraft.getMinecraft().displayGuiScreen(parentGui);
      }    
    };
  button.updateRenderPos(0, totalHeight);
  totalHeight+=12;
  area.addGuiElement(button);
  
  for(ModelPiece piece : pieces)
    {
    if(piece==parentGui.getSelectedPiece())
      {
      continue;
      }
    ModelPiece parent = piece.getParent();
    if(parent==parentGui.getSelectedPiece())
      {
      continue;
      }
    else if(parent!=null)
      {
      parent = parent.getParent();
      boolean skip = false;
      while(parent!=null)
        {
        if(parent==parentGui.getSelectedPiece())
          {
          skip = true;
          continue;
          }
        parent = parent.getParent();
        }      
      if(skip)
        {
        continue;
        }
      }
    button = new GuiButtonSimple(0, area, 160, 12, piece.getName())
      {
      public void onElementActivated()
        {
        ModelPiece piece2 = pieceMap.get(this);
        ModelPiece selPiece = parentGui.getSelectedPiece();
        GuiModelEditor.model.removePiece(selPiece);
        piece2.addChild(selPiece);
        GuiModelEditor.model.addPiece(selPiece);
        Minecraft.getMinecraft().displayGuiScreen(parentGui);
        };
      };
    button.updateRenderPos(0, totalHeight);
    totalHeight+=12;
    area.addGuiElement(button);
    pieceMap.put(button, piece);
    }
    
  area.updateTotalHeight(totalHeight);
  }

@Override
public void updateControls()
  {
  // TODO Auto-generated method stub

  }

private HashMap<GuiButtonSimple, ModelPiece> pieceMap = new HashMap<GuiButtonSimple, ModelPiece>();

}
