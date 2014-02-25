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

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.imageio.ImageIO;

import shadowmage.ancient_framework.client.gui.GuiContainerAdvanced;
import shadowmage.ancient_framework.client.gui.elements.GuiButtonSimple;
import shadowmage.ancient_framework.client.gui.elements.GuiNumberInputLine;
import shadowmage.ancient_framework.client.gui.elements.GuiScrollableArea;
import shadowmage.ancient_framework.client.gui.elements.GuiString;
import shadowmage.ancient_framework.client.gui.elements.IGuiElement;
import shadowmage.ancient_framework.client.model.ModelBaseAW;
import shadowmage.ancient_framework.client.model.ModelPiece;
import shadowmage.ancient_framework.client.model.Primitive;
import shadowmage.ancient_framework.client.model.PrimitiveBox;
import shadowmage.ancient_framework.client.model.PrimitiveQuad;
import shadowmage.ancient_framework.client.model.PrimitiveTriangle;
import shadowmage.ancient_framework.common.config.AWLog;
import shadowmage.ancient_framework.common.config.Statics;
import shadowmage.ancient_framework.common.container.ContainerBase;

public class GuiUVMap extends GuiContainerAdvanced
{

GuiButtonSimple textureXSizePlus;
GuiButtonSimple textureXSizeMinus;
GuiButtonSimple textureYSizePlus;
GuiButtonSimple textureYSizeMinus;
GuiNumberInputLine textureXSizeInput;
GuiNumberInputLine textureYSizeInput;

GuiButtonSimple exportUVMap;

GuiScrollableArea textureControlArea;
GuiScrollableArea primitiveControlArea;
GuiScrollableArea primitiveSelectionArea;

GuiTextureElement texture;

PrimitiveUVSetup primitiveSetup;

ModelPiece selectedPiece;
Primitive selectedPrimitive;

BufferedImage image;

public GuiUVMap(ContainerBase container)
  {
  super(container);
  this.shouldCloseOnVanillaKeys = true;
  this.initImage();
  }

private final void initImage()
  {
  ModelBaseAW model = GuiModelEditor.model;
  image = new BufferedImage(model.textureWidth(), model.textureHeight(), BufferedImage.TYPE_INT_ARGB);
  
  this.updateImage();
  }

/**
 * updates the image from the current model/pieces
 * updates the openGL texture
 * should be called whenever the image-contents need to be recalculated -- normally because a piece texture-mapping has changed
 */
public final void updateImage()
  {
  for(int x = 0; x < image.getWidth(); x++)
    {
    for(int y = 0; y < image.getHeight(); y++)
      {      
      image.setRGB(x, y, 0xff222222);
      if((x==0 || x==image.getWidth()-1) || (y==0 || y==image.getHeight()-1))
        {
        image.setRGB(x, y, 0xff00ff00);
        }
      }
    }  
  List<ModelPiece> pieces = new ArrayList<ModelPiece>();
  GuiModelEditor.model.getPieces(pieces);
  for(ModelPiece p : pieces)
    {
    for(Primitive pr : p.getPrimitives())
      {
      addPrimitiveToTexture(pr);
      }
    }
  
//  int pixel;
//  pixel = 0xffffffff;
//  int a = 0xff;
//  int r = 0x00;
//  int g;
//  int b;
//  for(int x = 0; x < image.getWidth(); x++)
//    {
//    for(int y = 0; y < image.getHeight(); y++)
//      {
//      g = x%256;
//      b = y%256;
//      pixel = (a<<24) | (r<<16) | (g<<8) | (b<<0);
//      image.setRGB(x, y, pixel);
//      }
//    }
  if(this.texture!=null)
    {
    this.texture.updateImage(image);    
    }
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
  return 256;
  }

@Override
public void renderExtraBackGround(int mouseX, int mouseY, float partialTime)
  {

  }

@Override
public void updateScreenContents()
  {

  }

@Override
public void setupControls()
  {
  textureControlArea = new GuiScrollableArea(0, this, -guiLeft, -guiTop, 80, 30, 120);
  this.addElement(textureControlArea);
  
  primitiveControlArea = new GuiScrollableArea(1, this, -guiLeft, -guiTop+30, 80, 120, 120);
  this.addElement(primitiveControlArea);
  
  primitiveSelectionArea = new GuiScrollableArea(2, this, -guiLeft+width-80, -guiTop, 80, height, height);
  this.addElement(primitiveSelectionArea);  
  
  texture = new GuiTextureElement(3, this, 256, 256, image);
  texture.updateRenderPos(0, 0);
  this.addElement(texture);
  
  int col1 = 0;
  int col2 = 25;
  int col3 = 25+12+2;
  int col4 = 25+12+2+20+2;
  int totalHeight = 0;
   
  
  textureXSizeMinus = new GuiButtonSimple(0, textureControlArea, 12, 12, "-")
    {
    @Override
    public void onElementActivated()
      {
      ModelBaseAW model = GuiModelEditor.model;
      model.setTextureSize(model.textureWidth()-16, model.textureHeight());
      textureXSizeInput.setIntegerValue(model.textureWidth());
      updateImage();
      }
    };  
  textureXSizeMinus.updateRenderPos(col2, totalHeight);
  textureControlArea.addGuiElement(textureXSizeMinus);
  
  textureXSizePlus = new GuiButtonSimple(0, textureControlArea, 12, 12, "+")
    {
    @Override
    public void onElementActivated()
      {
      ModelBaseAW model = GuiModelEditor.model;
      model.setTextureSize(model.textureWidth()+16, model.textureHeight());
      textureXSizeInput.setIntegerValue(model.textureWidth());
      updateImage();
      }
    };  
  textureXSizePlus.updateRenderPos(col4, totalHeight);
  textureControlArea.addGuiElement(textureXSizePlus);
  
  textureXSizeInput = new GuiNumberInputLine(0, textureControlArea, 20, 12, 10, String.valueOf(GuiModelEditor.model.textureWidth()))
    {
    @Override
    public void onElementActivated()
      {
      ModelBaseAW model = GuiModelEditor.model;
      int val = getIntVal();
      int m = val%16;
      int mod = val/16;
      if(m==1)
        {
        mod++;
        }     
      val = mod*16;
      setIntegerValue(val);
      model.setTextureSize(val, model.textureHeight());
      updateImage();
      }
    };
  textureXSizeInput.updateRenderPos(col3, totalHeight);
  textureControlArea.addGuiElement(textureXSizeInput);
  textureXSizeInput.setAsIntegerValue();  
    
  GuiString label = new GuiString(0, textureControlArea, 25, 12, "T:XS");
  label.updateRenderPos(col1, totalHeight);
  textureControlArea.addGuiElement(label);
  totalHeight+=12;
  
  
  textureYSizeMinus = new GuiButtonSimple(0, textureControlArea, 12, 12, "-")
    {
    @Override
    public void onElementActivated()
      {
      ModelBaseAW model = GuiModelEditor.model;
      model.setTextureSize(model.textureWidth(), model.textureHeight()-16);
      textureYSizeInput.setIntegerValue(model.textureHeight());
      updateImage();
      }
    };  
  textureYSizeMinus.updateRenderPos(col2, totalHeight);
  textureControlArea.addGuiElement(textureYSizeMinus);
  
  textureYSizePlus = new GuiButtonSimple(0, textureControlArea, 12, 12, "+")
    {
    @Override
    public void onElementActivated()
      {
      ModelBaseAW model = GuiModelEditor.model;
      model.setTextureSize(model.textureWidth(), model.textureHeight()+16);
      textureYSizeInput.setIntegerValue(model.textureHeight());
      updateImage();
      }
    };  
  textureYSizePlus.updateRenderPos(col4, totalHeight);
  textureControlArea.addGuiElement(textureYSizePlus);
  
  textureYSizeInput = new GuiNumberInputLine(0, textureControlArea, 20, 12, 10, String.valueOf(GuiModelEditor.model.textureWidth()))
    {
    @Override
    public void onElementActivated()
      {
      ModelBaseAW model = GuiModelEditor.model; 
      int val = getIntVal();   
      int m = val%16;
      int mod = val/16;
      if(m==1)
        {
        mod++;
        }     
      val = mod*16;
      model.setTextureSize(model.textureWidth(), val);
      setIntegerValue(val);
      updateImage();
      }
    };
  textureYSizeInput.updateRenderPos(col3, totalHeight);
  textureControlArea.addGuiElement(textureYSizeInput);
  textureYSizeInput.setAsIntegerValue();  
    
  label = new GuiString(0, textureControlArea, 25, 12, "T:YS");
  label.updateRenderPos(col1, totalHeight);
  textureControlArea.addGuiElement(label);
  totalHeight+=12;
  
  
  exportUVMap = new GuiButtonSimple(0, textureControlArea, 45, 12, "Export")
    {
    @Override
    public void onElementActivated()
      {
      try
        {
        File file = new File(Statics.CONFIG_PATH, "UVExport.png");
        AWLog.logDebug("filePath:"+file.getAbsolutePath());
        ImageIO.write(image, "png", new File(Statics.CONFIG_PATH, "UVExport.png"));
        } 
      catch (IOException e)
        {
        e.printStackTrace();
        }
      }
    };
  exportUVMap.updateRenderPos(0, totalHeight);
  textureControlArea.addGuiElement(exportUVMap);
  totalHeight+=12;
  
  textureControlArea.updateTotalHeight(totalHeight);
  
  
  
  /**
   * TODO after controls are setup, init texture from parent model pieces
   */
  }

@Override
public void updateControls()
  {  
  textureControlArea.updateRenderPos(-guiLeft, -guiTop);
  textureControlArea.setHeight(40);
  
  primitiveControlArea.updateRenderPos(-guiLeft, -guiTop+40);
  primitiveControlArea.setHeight(height-40);
  this.addPrimitiveControls();
  
  primitiveSelectionArea.updateRenderPos(-guiLeft+width-80, -guiTop);
  primitiveSelectionArea.setHeight(height);
  this.addSelectionControls();
  }

protected void addPrimitiveControls()
  {
  this.primitiveSetup = null;
  primitiveControlArea.elements.clear();  
  if(this.selectedPrimitive==null)
    {
    this.primitiveSetup = new PrimitiveDummyUVSetup(this);
    }
  else if(this.selectedPrimitive instanceof PrimitiveBox)
    {
    this.primitiveSetup = new PrimitiveBoxUVSetup(this);
    }
  else if(this.selectedPrimitive instanceof PrimitiveQuad)
    {
    this.primitiveSetup = new PrimitiveQuadUVSetup(this);
    }
  else if(this.selectedPrimitive instanceof PrimitiveTriangle)
    {
    this.primitiveSetup = new PrimitiveTriangleUVSetup(this);
    }
  this.primitiveSetup.addControls(primitiveControlArea);
  }

protected void addSelectionControls()
  {
  primitiveSelectionArea.elements.clear();
  pieceLabelMap.clear();
  primitiveLabelMap.clear();
  List<ModelPiece> pieces = new ArrayList<ModelPiece>();
  GuiModelEditor.model.getPieces(pieces);
  
  int totalHeight = 0;
  
  GuiString label;
  
  label = new GuiString(0, primitiveSelectionArea, 80, 12, "Pieces:");
  label.updateRenderPos(0, totalHeight);
  primitiveSelectionArea.addGuiElement(label);
  totalHeight+=12;
  
  
  for(ModelPiece piece : pieces)
    {
    label = new GuiString(0, primitiveSelectionArea, 80, 12, piece.getName())
      {
      @Override
      public void onElementActivated()
        {
        setSelection(pieceLabelMap.get(this), null);
        }
      };
    label.updateRenderPos(0, totalHeight);
    label.clickable = true;
    pieceLabelMap.put(label, piece);
    primitiveSelectionArea.addGuiElement(label);
    totalHeight+=12;
    }
  
  if(this.selectedPiece!=null)
    {
    label = new GuiString(0, primitiveSelectionArea, 80, 12, "Primitives:");
    label.updateRenderPos(0, totalHeight);
    primitiveSelectionArea.addGuiElement(label);
    totalHeight+=12;
    int num = 1;
    for(Primitive p : this.selectedPiece.getPrimitives())
      {
      label = new GuiString(0, primitiveSelectionArea, 80, 12, "BOX:"+num)
        {
        @Override
        public void onElementActivated()
          {
          setSelection(selectedPiece, primitiveLabelMap.get(this));
          }
        };
      label.updateRenderPos(0, totalHeight);
      label.clickable = true;
      primitiveLabelMap.put(label, p);
      primitiveSelectionArea.addGuiElement(label);
      totalHeight+=12;
      }
    }
  
  primitiveSelectionArea.updateTotalHeight(totalHeight);
  }

private HashMap<GuiString, ModelPiece> pieceLabelMap = new HashMap<GuiString, ModelPiece>();
private HashMap<GuiString, Primitive> primitiveLabelMap = new HashMap<GuiString, Primitive>();

public void setSelection(ModelPiece piece, Primitive primitve)
  {
  this.selectedPiece = piece;
  this.selectedPrimitive = primitve;
  this.refreshGui();
  }

public void addPrimitiveToTexture(Primitive p)
  {
  p.addUVMapToImage(image);
  }

}
