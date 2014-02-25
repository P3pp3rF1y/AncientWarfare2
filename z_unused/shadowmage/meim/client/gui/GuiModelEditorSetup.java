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
import java.util.List;

import net.minecraft.client.Minecraft;
import shadowmage.ancient_framework.client.gui.elements.GuiButtonSimple;
import shadowmage.ancient_framework.client.gui.elements.GuiElement;
import shadowmage.ancient_framework.client.gui.elements.GuiNumberInputLine;
import shadowmage.ancient_framework.client.gui.elements.GuiScrollableArea;
import shadowmage.ancient_framework.client.gui.elements.GuiString;
import shadowmage.ancient_framework.client.model.ModelPiece;
import shadowmage.ancient_framework.client.model.Primitive;
import shadowmage.ancient_framework.client.model.PrimitiveBox;
import shadowmage.ancient_framework.client.model.PrimitiveQuad;
import shadowmage.ancient_framework.client.model.PrimitiveTriangle;
import shadowmage.ancient_framework.common.config.AWLog;
import shadowmage.ancient_framework.common.container.ContainerBase;
import shadowmage.meim.common.config.MEIMConfig;

public class GuiModelEditorSetup
{

private GuiModelEditor gui;

private GuiButtonSimple load;
private GuiButtonSimple save;
private GuiButtonSimple importPiece;
private GuiButtonSimple uvMap;
private GuiButtonSimple clear;
private GuiButtonSimple loadTexture;

private GuiButtonSimple copyPiece;
private GuiButtonSimple newPiece;
//private GuiButtonSimple clearSelection;
private GuiButtonSimple deletePiece;
private GuiButtonSimple changePieceParent;
//private GuiButtonSimple clearPieceParent;

private GuiButtonSimple pieceXMinus;
private GuiButtonSimple pieceXPlus;
private GuiButtonSimple pieceYMinus;
private GuiButtonSimple pieceYPlus;
private GuiButtonSimple pieceZMinus;
private GuiButtonSimple pieceZPlus;

private GuiNumberInputLine pieceXInput;
private GuiNumberInputLine pieceYInput;
private GuiNumberInputLine pieceZInput;

private GuiButtonSimple pieceRXMinus;
private GuiButtonSimple pieceRXPlus;
private GuiButtonSimple pieceRYMinus;
private GuiButtonSimple pieceRYPlus;
private GuiButtonSimple pieceRZMinus;
private GuiButtonSimple pieceRZPlus;

private GuiNumberInputLine pieceRXInput;
private GuiNumberInputLine pieceRYInput;
private GuiNumberInputLine pieceRZInput;

private GuiScrollableArea leftPieceControlPanel;
private GuiScrollableArea leftPrimitiveControlPanel;
private GuiScrollableArea rightControlPanel;
private GuiScrollableArea rightPrimitivesPanel;

PrimitiveGuiSetup primitiveControls;

HashMap<GuiString, ModelPiece> pieceLabelMap = new HashMap<GuiString, ModelPiece>();
HashMap<GuiString, Primitive> primitiveLabelMap = new HashMap<GuiString, Primitive>();

float scale = 0.0625f;

public GuiModelEditorSetup(GuiModelEditor gui)
  {
  this.gui = gui;
  }

public void setupControls()
  {
  leftPieceControlPanel = new GuiScrollableArea(0, gui, 0, 0, 100, 120, 120);
  this.addElement(leftPieceControlPanel);
  leftPrimitiveControlPanel = new GuiScrollableArea(1, gui, 0, 120, 100, 120, 120);
  this.addElement(leftPrimitiveControlPanel);
  rightControlPanel = new GuiScrollableArea(2, gui, 0, 0, 100, 120, 120);
  this.addElement(rightControlPanel);
  rightPrimitivesPanel = new GuiScrollableArea(3, gui, 0, 120, 100, 120, 120);
  this.addElement(rightPrimitivesPanel); 
    
  addPieceControls();
  addPrimitiveControls();
  addFileControls();
  addRightLabels();
  }

void addPrimitiveControls()
  {
  this.primitiveControls = null;
  if(this.gui.getSelectedPrimitive()==null)
    {
    this.primitiveControls = new PrimitiveDummySetup(gui, this);    
    }
  else if(this.gui.getSelectedPrimitive() instanceof PrimitiveBox)
    {
    this.primitiveControls = new PrimitiveBoxSetup(gui, this);    
    }
  else if(this.gui.getSelectedPrimitive() instanceof PrimitiveQuad)
    {
    this.primitiveControls = new PrimitiveQuadSetup(gui, this);
    }
  else if(this.gui.getSelectedPrimitive() instanceof PrimitiveTriangle)
    {
    this.primitiveControls = new PrimitiveTriangleSetup(gui, this);
    }
  if(this.primitiveControls!=null)
    {
    this.primitiveControls.addElements(leftPrimitiveControlPanel);
    }
  this.updateButtonValues();
  }

private void addPieceControls()
  {
  int totalHeight = 0;
   
  newPiece = new GuiButtonSimple(0, leftPieceControlPanel, 84, 12, "New Piece")
    {
    @Override
    public boolean handleMousePressed(int x, int y, int num)
      {
      if(super.handleMousePressed(x, y, num))
        {
        Minecraft.getMinecraft().displayGuiScreen(new GuiNewPiece((ContainerBase) gui.inventorySlots, gui));
        }
      return true;
      }
    };  
  newPiece.updateRenderPos(0, totalHeight);
  totalHeight+=12;
  leftPieceControlPanel.elements.add(newPiece);
  
  copyPiece = new GuiButtonSimple(0, leftPieceControlPanel, 84, 12, "Copy Piece")
    {
    @Override
    public boolean handleMousePressed(int x, int y, int num)
      {
      if(super.handleMousePressed(x, y, num))
        {
        gui.copyPiece();
        }
      return true;
      }
    };  
  copyPiece.updateRenderPos(0, totalHeight);
  totalHeight+=12;
  leftPieceControlPanel.elements.add(copyPiece);
  
//  clearSelection = new GuiButtonSimple(0, leftPieceControlPanel, 84, 12, "Clear Selection")
//    {
//    @Override
//    public boolean handleMousePressed(int x, int y, int num)
//      {
//      if(super.handleMousePressed(x, y, num))
//        {
//        gui.clearSelection();
//        }
//      return true;
//      }
//    };  
//  clearSelection.updateRenderPos(0, totalHeight);
//  totalHeight+=12;
//  leftPieceControlPanel.elements.add(clearSelection);
  
  deletePiece = new GuiButtonSimple(0, leftPieceControlPanel, 84, 12, "Delete Piece")
    {
    @Override
    public boolean handleMousePressed(int x, int y, int num)
      {
      if(super.handleMousePressed(x, y, num))
        {
        gui.deletePiece();
        }
      return true;
      }
    };  
  deletePiece.updateRenderPos(0, totalHeight);
  totalHeight+=12;
  leftPieceControlPanel.elements.add(deletePiece);
    
  changePieceParent = new GuiButtonSimple(0, leftPieceControlPanel, 84, 12, "Change Parent")
    {
    @Override
    public boolean handleMousePressed(int x, int y, int num)
      {
      if(super.handleMousePressed(x, y, num))
        {
        Minecraft.getMinecraft().displayGuiScreen(new GuiSwapPieceParent((ContainerBase) gui.inventorySlots, gui));
        }
      return true;
      }
    };  
  changePieceParent.updateRenderPos(0, totalHeight);
  totalHeight+=12;
  leftPieceControlPanel.elements.add(changePieceParent);
  
//  clearPieceParent = new GuiButtonSimple(0, leftPieceControlPanel, 84, 12, "Clear Parent")
//    {
//    @Override
//    public boolean handleMousePressed(int x, int y, int num)
//      {
//      if(super.handleMousePressed(x, y, num))
//        {
//        if(gui.selectedPiece!=null && gui.selectedPiece.getParent()!=null)
//          {
//          gui.selectedPiece.getParent().removeChild(gui.selectedPiece);
//          }
//        }
//      return true;
//      }
//    };  
//  clearPieceParent.updateRenderPos(0, totalHeight);
//  totalHeight+=12;
//  leftPieceControlPanel.elements.add(clearPieceParent);
  
 
     
  totalHeight = addLeftPieceControls(totalHeight);    
  leftPieceControlPanel.updateTotalHeight(totalHeight);
  }

private int addLeftPieceControls(int totalHeight)
  {
  int col1 = 0;
  int col2 = 25;
  int col3 = 25+12+2;
  int col4 = 25+12+2+20+2;
  pieceXMinus = new GuiButtonSimple(0, leftPieceControlPanel, 12, 12, "-")
    {
    @Override
    public boolean handleMousePressed(int x, int y, int num)
      {
      if(super.handleMousePressed(x, y, num) && GuiModelEditor.model!=null && gui.getSelectedPiece()!=null)
        {
        gui.getSelectedPiece().setPosition(gui.getSelectedPiece().x() - 1 * scale, gui.getSelectedPiece().y(), gui.getSelectedPiece().z());  
        updateButtonValues();
        }
      return true;
      }
    };  
  pieceXMinus.updateRenderPos(col2, totalHeight);
  leftPieceControlPanel.addGuiElement(pieceXMinus);
  
  pieceXPlus = new GuiButtonSimple(0, leftPieceControlPanel, 12, 12, "+")
    {
    @Override
    public boolean handleMousePressed(int x, int y, int num)
      {
      if(super.handleMousePressed(x, y, num) && GuiModelEditor.model!=null && gui.getSelectedPiece()!=null)
        {
        gui.getSelectedPiece().setPosition(gui.getSelectedPiece().x()+1 * scale, gui.getSelectedPiece().y(), gui.getSelectedPiece().z());  
        updateButtonValues();
        }
      return true;
      }
    };  
  pieceXPlus.updateRenderPos(col4, totalHeight);
  leftPieceControlPanel.addGuiElement(pieceXPlus);
  
  pieceXInput = new GuiNumberInputLine(0, leftPieceControlPanel, 20, 12, 4, "0")
    {
    @Override
    public void onElementActivated()      
      {
      if(GuiModelEditor.model!=null && gui.getSelectedPiece()!=null)
        {
        gui.getSelectedPiece().setPosition(getFloatVal() * scale, gui.getSelectedPiece().y(), gui.getSelectedPiece().z());  
        updateButtonValues();
        }
      }     
    };
  pieceXInput.setValue(0.f);
  pieceXInput.updateRenderPos(col3, totalHeight);
  leftPieceControlPanel.addGuiElement(pieceXInput);
  
  GuiString label = new GuiString(0, leftPieceControlPanel, 25, 12, "P:X");
  label.updateRenderPos(col1, totalHeight);
  leftPieceControlPanel.addGuiElement(label);
  
  totalHeight+=12;
  
  
  pieceYMinus = new GuiButtonSimple(0, leftPieceControlPanel, 12, 12, "-")
    {
    @Override
    public boolean handleMousePressed(int x, int y, int num)
      {
      if(super.handleMousePressed(x, y, num) && GuiModelEditor.model!=null && gui.getSelectedPiece()!=null)
        {
        gui.getSelectedPiece().setPosition(gui.getSelectedPiece().x(), gui.getSelectedPiece().y()-1 * scale, gui.getSelectedPiece().z()); 
        updateButtonValues();
        }
      return true;
      }
    };  
  pieceYMinus.updateRenderPos(col2, totalHeight);
  leftPieceControlPanel.addGuiElement(pieceYMinus);
  
  pieceYPlus = new GuiButtonSimple(0, leftPieceControlPanel, 12, 12, "+")
    {
    @Override
    public boolean handleMousePressed(int x, int y, int num)
      {
      if(super.handleMousePressed(x, y, num) && GuiModelEditor.model!=null && gui.getSelectedPiece()!=null)
        {
        gui.getSelectedPiece().setPosition(gui.getSelectedPiece().x(), gui.getSelectedPiece().y()+1 * scale, gui.getSelectedPiece().z());  
        updateButtonValues();
        }
      return true;
      }
    };  
  pieceYPlus.updateRenderPos(col4, totalHeight);
  leftPieceControlPanel.addGuiElement(pieceYPlus);
  
  pieceYInput = new GuiNumberInputLine(0, leftPieceControlPanel, 20, 12, 4, "0")
    {
    @Override
    public void onElementActivated()      
      {
      if(GuiModelEditor.model!=null && gui.getSelectedPiece()!=null)
        {
        gui.getSelectedPiece().setPosition(gui.getSelectedPiece().x(), getFloatVal() * scale, gui.getSelectedPiece().z());      
        updateButtonValues();
        }
      }  
    };
  pieceYInput.setValue(0.f);
  pieceYInput.updateRenderPos(col3, totalHeight);
  leftPieceControlPanel.addGuiElement(pieceYInput);
  
  label = new GuiString(0, leftPieceControlPanel, 25, 12, "P:Y");
  label.updateRenderPos(col1, totalHeight);
  leftPieceControlPanel.addGuiElement(label);
  
  totalHeight+=12;
    
  
  pieceZMinus = new GuiButtonSimple(0, leftPieceControlPanel, 12, 12, "-")
    {
    @Override
    public boolean handleMousePressed(int x, int y, int num)
      {
      if(super.handleMousePressed(x, y, num) && GuiModelEditor.model!=null && gui.getSelectedPiece()!=null)
        {
        gui.getSelectedPiece().setPosition(gui.getSelectedPiece().x(), gui.getSelectedPiece().y(), gui.getSelectedPiece().z()-1 * scale);  
        updateButtonValues();
        }
      return true;
      }
    };  
  pieceZMinus.updateRenderPos(col2, totalHeight);
  leftPieceControlPanel.addGuiElement(pieceZMinus);
  
  pieceZPlus = new GuiButtonSimple(0, leftPieceControlPanel, 12, 12, "+")
    {
    @Override
    public boolean handleMousePressed(int x, int y, int num)
      {
      if(super.handleMousePressed(x, y, num) && GuiModelEditor.model!=null && gui.getSelectedPiece()!=null)
        {
        gui.getSelectedPiece().setPosition(gui.getSelectedPiece().x(), gui.getSelectedPiece().y(), gui.getSelectedPiece().z()+1 * scale);  
        updateButtonValues();
        }
      return true;
      }
    };  
  pieceZPlus.updateRenderPos(col4, totalHeight);
  leftPieceControlPanel.addGuiElement(pieceZPlus);
  
  pieceZInput = new GuiNumberInputLine(0, leftPieceControlPanel, 20, 12, 4, "0")
    {
    @Override
    public void onElementActivated()      
      {
      if(GuiModelEditor.model!=null && gui.getSelectedPiece()!=null)
        {
        gui.getSelectedPiece().setPosition(gui.getSelectedPiece().x(), gui.getSelectedPiece().y(), getFloatVal() * scale);   
        updateButtonValues();   
        }
      }       
    };
  pieceZInput.setValue(0.f);
  pieceZInput.updateRenderPos(col3, totalHeight);
  leftPieceControlPanel.addGuiElement(pieceZInput);
  
  label = new GuiString(0, leftPieceControlPanel, 25, 12, "P:Z");
  label.updateRenderPos(col1, totalHeight);
  leftPieceControlPanel.addGuiElement(label);
  
  totalHeight+=12;
  
  
  pieceRXMinus = new GuiButtonSimple(0, leftPieceControlPanel, 12, 12, "-")
    {
    @Override
    public boolean handleMousePressed(int x, int y, int num)
      {
      if(super.handleMousePressed(x, y, num) && GuiModelEditor.model!=null && gui.getSelectedPiece()!=null)
        {
        gui.getSelectedPiece().setRotation(gui.getSelectedPiece().rx()-1, gui.getSelectedPiece().ry(), gui.getSelectedPiece().rz());  
        updateButtonValues();
        }
      return true;
      }
    };  
  pieceRXMinus.updateRenderPos(col2, totalHeight);
  leftPieceControlPanel.addGuiElement(pieceRXMinus);
  
  pieceRXPlus = new GuiButtonSimple(0, leftPieceControlPanel, 12, 12, "+")
    {
    @Override
    public boolean handleMousePressed(int x, int y, int num)
      {
      if(super.handleMousePressed(x, y, num) && GuiModelEditor.model!=null && gui.getSelectedPiece()!=null)
        {
        gui.getSelectedPiece().setRotation(gui.getSelectedPiece().rx()+1, gui.getSelectedPiece().ry(), gui.getSelectedPiece().rz());  
        updateButtonValues();
        }
      return true;
      }
    };  
  pieceRXPlus.updateRenderPos(col4, totalHeight);
  leftPieceControlPanel.addGuiElement(pieceRXPlus);
  
  pieceRXInput = new GuiNumberInputLine(0, leftPieceControlPanel, 20, 12, 4, "0")
    {
    @Override
    public void onElementActivated()      
      {
      if(GuiModelEditor.model!=null && gui.getSelectedPiece()!=null)
        {
        gui.getSelectedPiece().setRotation(getFloatVal(), gui.getSelectedPiece().ry(), gui.getSelectedPiece().rz());  
        updateButtonValues();
        }
      }
    };
  pieceRXInput.setValue(0.f);
  pieceRXInput.updateRenderPos(col3, totalHeight);
  leftPieceControlPanel.addGuiElement(pieceRXInput);
  
  label = new GuiString(0, leftPieceControlPanel, 25, 12, "P:RX");
  label.updateRenderPos(col1, totalHeight);
  leftPieceControlPanel.addGuiElement(label);
  
  totalHeight+=12;
  
  
  pieceRYMinus = new GuiButtonSimple(0, leftPieceControlPanel, 12, 12, "-")
    {
    @Override
    public boolean handleMousePressed(int x, int y, int num)
      {
      if(super.handleMousePressed(x, y, num) && GuiModelEditor.model!=null && gui.getSelectedPiece()!=null)
        {
        gui.getSelectedPiece().setRotation(gui.getSelectedPiece().rx(), gui.getSelectedPiece().ry()-1, gui.getSelectedPiece().rz());  
        updateButtonValues();
        }
      return true;
      }
    };  
  pieceRYMinus.updateRenderPos(col2, totalHeight);
  leftPieceControlPanel.addGuiElement(pieceRYMinus);
  
  pieceRYPlus = new GuiButtonSimple(0, leftPieceControlPanel, 12, 12, "+")
    {
    @Override
    public boolean handleMousePressed(int x, int y, int num)
      {
      if(super.handleMousePressed(x, y, num) && GuiModelEditor.model!=null && gui.getSelectedPiece()!=null)
        {
        gui.getSelectedPiece().setRotation(gui.getSelectedPiece().rx(), gui.getSelectedPiece().ry()+1, gui.getSelectedPiece().rz());  
        updateButtonValues();
        }
      return true;
      }
    };  
  pieceRYPlus.updateRenderPos(col4, totalHeight);
  leftPieceControlPanel.addGuiElement(pieceRYPlus);
  
  pieceRYInput = new GuiNumberInputLine(0, leftPieceControlPanel, 20, 12, 4, "0")
    {
    @Override
    public void onElementActivated()      
      {
      if(GuiModelEditor.model!=null && gui.getSelectedPiece()!=null)
        {
        gui.getSelectedPiece().setRotation(gui.getSelectedPiece().rx(), getFloatVal(), gui.getSelectedPiece().rz());  
        updateButtonValues();
        }
      }   
    };
  pieceRYInput.setValue(0.f);
  pieceRYInput.updateRenderPos(col3, totalHeight);
  leftPieceControlPanel.addGuiElement(pieceRYInput);
  
  label = new GuiString(0, leftPieceControlPanel, 25, 12, "P:RY");
  label.updateRenderPos(col1, totalHeight);
  leftPieceControlPanel.addGuiElement(label);
  
  totalHeight+=12;
  
  
  pieceRZMinus = new GuiButtonSimple(0, leftPieceControlPanel, 12, 12, "-")
    {
    @Override
    public boolean handleMousePressed(int x, int y, int num)
      {
      if(super.handleMousePressed(x, y, num) && GuiModelEditor.model!=null && gui.getSelectedPiece()!=null)
        {
        gui.getSelectedPiece().setRotation(gui.getSelectedPiece().rx(), gui.getSelectedPiece().ry(), gui.getSelectedPiece().rz()-1);  
        updateButtonValues();
        }
      return true;
      }
    };  
  pieceRZMinus.updateRenderPos(col2, totalHeight);
  leftPieceControlPanel.addGuiElement(pieceRZMinus);
  
  pieceRZPlus = new GuiButtonSimple(0, leftPieceControlPanel, 12, 12, "+")
    {
    @Override
    public boolean handleMousePressed(int x, int y, int num)
      {
      if(super.handleMousePressed(x, y, num) && GuiModelEditor.model!=null && gui.getSelectedPiece()!=null)
        {
        gui.getSelectedPiece().setRotation(gui.getSelectedPiece().rx(), gui.getSelectedPiece().ry(), gui.getSelectedPiece().rz()+1);  
        updateButtonValues();
        }
      return true;
      }
    };  
  pieceRZPlus.updateRenderPos(col4, totalHeight);
  leftPieceControlPanel.addGuiElement(pieceRZPlus);
  
  pieceRZInput = new GuiNumberInputLine(0, leftPieceControlPanel, 20, 12, 4, "0")
    {
    @Override
    public void onElementActivated()      
      {
      if(GuiModelEditor.model!=null && gui.getSelectedPiece()!=null)
        {
        gui.getSelectedPiece().setRotation(gui.getSelectedPiece().rx(), gui.getSelectedPiece().ry(),  getFloatVal());  
        updateButtonValues();
        }
      }      
    };
  pieceRZInput.setValue(0.f);
  pieceRZInput.updateRenderPos(col3, totalHeight);
  leftPieceControlPanel.addGuiElement(pieceRZInput);
  
  label = new GuiString(0, leftPieceControlPanel, 25, 12, "P:RZ");
  label.updateRenderPos(col1, totalHeight);
  leftPieceControlPanel.addGuiElement(label);
  
  totalHeight+=12;
      
  return totalHeight;
  }

private void addFileControls()
  {
  int totalHeight = 0;
  
  load = new GuiButtonSimple(0, rightControlPanel, 84, 12, "Load")
    {
    @Override
    public boolean handleMousePressed(int x, int y, int num)
      {
      if(super.handleMousePressed(x, y, num))
        {
        gui.selectionMode = GuiModelEditor.SELECT_LOAD;
        Minecraft.getMinecraft().displayGuiScreen(new GuiFileSelect(gui, gui, MEIMConfig.getModelSaveDir(), false));
        }
      return true;
      }
    };
  load.updateRenderPos(0, totalHeight);
  totalHeight+=12;
  rightControlPanel.elements.add(load);
  
  save = new GuiButtonSimple(0, rightControlPanel, 84, 12, "Save")
    {
    @Override
    public boolean handleMousePressed(int x, int y, int num)
      {
      if(super.handleMousePressed(x, y, num) && GuiModelEditor.model!=null)
        {
        gui.selectionMode = GuiModelEditor.SELECT_SAVE;
        Minecraft.getMinecraft().displayGuiScreen(new GuiFileSelect(gui, gui, MEIMConfig.getModelSaveDir(), true));
        }
      return true;
      }
    };
  save.updateRenderPos(0, totalHeight);
  totalHeight+=12;
  rightControlPanel.elements.add(save);
  
  importPiece = new GuiButtonSimple(0, rightControlPanel, 84, 12, "Import")
    {
    @Override
    public boolean handleMousePressed(int x, int y, int num)
      {
      if(super.handleMousePressed(x, y, num) && GuiModelEditor.model!=null)
        {
        gui.selectionMode = GuiModelEditor.SELECT_IMPORT_PIECE;
        Minecraft.getMinecraft().displayGuiScreen(new GuiFileSelect(gui, gui, MEIMConfig.getModelSaveDir(), false));
        }
      return true;
      }
    };
  importPiece.updateRenderPos(0, totalHeight);
  totalHeight+=12;
  rightControlPanel.elements.add(importPiece);
  
  clear = new GuiButtonSimple(0, rightControlPanel, 84, 12, "Clear")
    {
    @Override
    public boolean handleMousePressed(int x, int y, int num)
      {
      if(super.handleMousePressed(x, y, num))
        {
        gui.setSelectedPiece(null);
        gui.setSelectedPrimitive(null);
        GuiModelEditor.model = null;
        gui.initModel();
        addPrimitiveControls();
        updateButtonValues();
        gui.refreshGui();
        }
      return true;
      }
    };
  clear.updateRenderPos(0, totalHeight);
  totalHeight+=12;
  rightControlPanel.elements.add(clear);
  
  uvMap = new GuiButtonSimple(0, rightControlPanel, 84, 12, "UVMap")
    {
    @Override
    public boolean handleMousePressed(int x, int y, int num)
      {
      if(super.handleMousePressed(x, y, num))
        {
        Minecraft.getMinecraft().displayGuiScreen(new GuiUVMap((ContainerBase) gui.inventorySlots));
        }
      return true;
      }
    };
  uvMap.updateRenderPos(0, totalHeight);
  totalHeight+=12;
  rightControlPanel.elements.add(uvMap);
    
  loadTexture = new GuiButtonSimple(0, rightControlPanel, 84, 12, "Load Texture")
    {
    @Override
    public boolean handleMousePressed(int x, int y, int num)
      {
      if(super.handleMousePressed(x, y, num))
        {
        gui.selectionMode = GuiModelEditor.SELECT_LOAD_TEXTURE;
        Minecraft.getMinecraft().displayGuiScreen(new GuiFileSelect(gui, gui, MEIMConfig.getTexLoadDir(), false));
        }
      return true;
      }
    };
  loadTexture.updateRenderPos(0, totalHeight);
  totalHeight+=12;
  rightControlPanel.elements.add(loadTexture);
  
  
  GuiString label = new GuiString(0, rightControlPanel, 40, 12, "Scale");
  label.updateRenderPos(0, totalHeight);
  rightControlPanel.addGuiElement(label);
  
  GuiNumberInputLine scaleInput = new GuiNumberInputLine(0, rightControlPanel, 40, 10, 10, "0.0625")
    {
    @Override
    public void onElementActivated()
      {
      scale = getFloatVal();
      updateButtonValues();
      }
    };
  scaleInput.setValue(scale);
  scaleInput.updateRenderPos(40, totalHeight+1);
  rightControlPanel.addGuiElement(scaleInput);
  totalHeight+=12;
    
  rightControlPanel.updateTotalHeight(totalHeight);
  }

public void addRightLabels()
  {
  int totalHeight = 0;
  pieceLabelMap.clear();
  if(GuiModelEditor.model!=null)
    {
    List<ModelPiece> pieces = new ArrayList<ModelPiece>();
    GuiModelEditor.model.getPieces(pieces);
    
    GuiString label = new GuiString(0, rightPrimitivesPanel, 80, 12, "Pieces:");
    label.updateRenderPos(0, totalHeight);
    rightPrimitivesPanel.addGuiElement(label);
    totalHeight+=12;
    
    for(ModelPiece piece : pieces)
      {
      label = new GuiString(0, rightPrimitivesPanel, 80, 12, piece.getName())
        {
        @Override
        public void onElementActivated()
          {
          ModelPiece p = pieceLabelMap.get(this);
          if(p!=null)
            {
            gui.setSelectedPiece(p);
            gui.setSelectedPrimitive(null);
            gui.refreshGui();
            updateButtonValues();
            AWLog.logDebug("selected piece: "+gui.getSelectedPiece() + " prims: " + (gui.getSelectedPiece()!=null ? gui.getSelectedPiece().getPrimitives().size() : "null"));
            }
          }
        };
      label.clickable = true;
      label.updateRenderPos(0, totalHeight);
      pieceLabelMap.put(label, piece);
      rightPrimitivesPanel.addGuiElement(label);
      totalHeight+=12;
      }    
    }  
  
  
  primitiveLabelMap.clear();
  
  if(gui.getSelectedPiece()!=null)
    {
    GuiString label = new GuiString(0, rightPrimitivesPanel, 80, 12, "Primitives:");
    label.updateRenderPos(0, totalHeight);
    rightPrimitivesPanel.addGuiElement(label);    
    totalHeight+=12;
    
    int num = 1;    
    for(Primitive p : gui.getSelectedPiece().getPrimitives())
      {
      label = new GuiString(0, rightPrimitivesPanel, 80, 12, "Prim:"+num)
        {
        @Override
        public void onElementActivated()
          {          
          gui.setSelectedPrimitive(primitiveLabelMap.get(this));          
          }
        };
      label.clickable = true;
      label.updateRenderPos(0, totalHeight);      
      totalHeight+=12;     
      num++;     
      
      primitiveLabelMap.put(label, p);
      rightPrimitivesPanel.addGuiElement(label);
      }
    }
  rightPrimitivesPanel.updateTotalHeight(totalHeight);
  }

private void addElement(GuiElement element)
  {
  gui.addElement(element);
  }

public void updateControls(int guiLeft, int guiTop, int width, int height)
  {
  leftPieceControlPanel.updateRenderPos(-guiLeft, -guiTop);
  leftPieceControlPanel.setHeight(height/2);
  leftPieceControlPanel.updateTotalHeight(leftPieceControlPanel.totalHeight);
  leftPrimitiveControlPanel.updateRenderPos(-guiLeft, -guiTop + height/2);
  leftPrimitiveControlPanel.setHeight(height/2);
  leftPrimitiveControlPanel.updateTotalHeight(leftPrimitiveControlPanel.totalHeight);
  rightControlPanel.updateRenderPos(-guiLeft + width - 100, -guiTop);
  rightControlPanel.setHeight(height/2);
  rightControlPanel.updateTotalHeight(rightControlPanel.totalHeight);
  rightPrimitivesPanel.updateRenderPos(-guiLeft + width - 100, -guiTop + height/2);
  rightPrimitivesPanel.setHeight(height/2); 
  rightPrimitivesPanel.updateTotalHeight(rightPrimitivesPanel.totalHeight);

  rightPrimitivesPanel.elements.clear();
  addRightLabels();
  
  this.addPrimitiveControls();
  
  this.updateButtonValues();
  }

public void updateButtonValues()
  {
  pieceXInput.setValue(gui.getSelectedPiece()==null ? 0.f : gui.getSelectedPiece().x()/scale);
  pieceYInput.setValue(gui.getSelectedPiece()==null ? 0.f : gui.getSelectedPiece().y()/scale);
  pieceZInput.setValue(gui.getSelectedPiece()==null ? 0.f : gui.getSelectedPiece().z()/scale);
  
  pieceRXInput.setValue(gui.getSelectedPiece()==null ? 0.f : gui.getSelectedPiece().rx());
  pieceRYInput.setValue(gui.getSelectedPiece()==null ? 0.f : gui.getSelectedPiece().ry());
  pieceRZInput.setValue(gui.getSelectedPiece()==null ? 0.f : gui.getSelectedPiece().rz()); 
  if(this.primitiveControls!=null)
    {
    this.primitiveControls.updateButtonValues();
    }
 
  }

}
