package net.shadowmage.ancientwarfare.modeler.gui;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.Listener;
import net.shadowmage.ancientwarfare.core.gui.elements.Button;
import net.shadowmage.ancientwarfare.core.gui.elements.CompositeScrolled;
import net.shadowmage.ancientwarfare.core.gui.elements.GuiElement;
import net.shadowmage.ancientwarfare.core.gui.elements.Label;
import net.shadowmage.ancientwarfare.core.gui.elements.NumberInput;
import net.shadowmage.ancientwarfare.core.gui.elements.TexturedRectangleLive;
import net.shadowmage.ancientwarfare.core.model.ModelPiece;
import net.shadowmage.ancientwarfare.core.model.Primitive;
import net.shadowmage.ancientwarfare.core.model.PrimitiveBox;
import net.shadowmage.ancientwarfare.core.model.PrimitiveQuad;
import net.shadowmage.ancientwarfare.core.model.PrimitiveTriangle;
import net.shadowmage.ancientwarfare.core.util.AWTextureManager;

/**
 * UV editor for MEIM
 * @author Shadowmage
 *
 */
public class GuiUVEditor extends GuiContainerBase
{

GuiModelEditor parent;
TexturedRectangleLive textureRect;

CompositeScrolled primitiveControlArea;
CompositeScrolled pieceListArea;
CompositeScrolled fileControlArea;
CompositeScrolled textureControlArea;//set texture x/y size
Label pieceNameLabel;
Label primitiveNameLabel;

ResourceLocation loc;

//map of label-element combos, to select pieces through clicking on/in the piece list area
private HashMap<Label, ModelPiece> pieceMap = new HashMap<Label, ModelPiece>();
private HashMap<Label, Primitive> primitiveMap = new HashMap<Label, Primitive>();
private HashMap<String, GuiElement> widgetMap = new HashMap<String, GuiElement>();

public GuiUVEditor(GuiModelEditor parent)
  {
  super((ContainerBase) parent.inventorySlots, 256, 256, defaultBackground);
  this.parent = parent;
  loc = new ResourceLocation(GuiModelEditor.uvMapTextureName);
  }

@Override
protected boolean onGuiCloseRequested()
  {
  Minecraft.getMinecraft().displayGuiScreen(parent);
  return false;
  }

@Override
public void initElements()
  {
  textureRect = new TexturedRectangleLive(0, 0, xSize, ySize, parent.model.textureWidth(), parent.model.textureHeight(), 0, 0, parent.model.textureWidth(), parent.model.textureHeight(), loc);
  addGuiElement(textureRect);
  
  textureControlArea = new CompositeScrolled(-guiLeft, -guiTop, (width - xSize)/2, height/2);
  addGuiElement(textureControlArea);
  
  primitiveControlArea = new CompositeScrolled(-guiLeft, -guiTop + height/2, (width - xSize)/2, height/2);
  addGuiElement(primitiveControlArea);
  
  fileControlArea = new CompositeScrolled(width, -guiTop, (width - xSize)/2, height/2);
  addGuiElement(fileControlArea);
  
  pieceListArea = new CompositeScrolled(width, -guiTop + height/2, (width - xSize)/2, height/2);
  addGuiElement(pieceListArea);
  
  pieceNameLabel = new Label(8, -guiTop, "Piece: No Selection");
  addGuiElement(pieceNameLabel);
  
  primitiveNameLabel = new Label(8, -guiTop + 10, "Primitive: No Selection");
  addGuiElement(primitiveNameLabel);
  
  updateTexture();
  }

@Override
public void setupElements()
  {
  textureControlArea.setRenderPosition(-guiLeft, -guiTop);//top-left
  primitiveControlArea.setRenderPosition(-guiLeft, -guiTop + height/2);//bottom-left
  fileControlArea.setRenderPosition(xSize, -guiTop);//top-right
  pieceListArea.setRenderPosition(xSize, -guiTop + height/2);//bottom-right
  
  pieceNameLabel.setRenderPosition(8, -guiTop);
  primitiveNameLabel.setRenderPosition(8, -guiTop+10);
  
  pieceNameLabel.setText(parent.getModelPiece()==null? "Piece: No Selection" : "Piece: "+parent.getModelPiece().getName());
  primitiveNameLabel.setText(parent.getPrimitive()==null? "Primitive: No Selection" : "Primitive: "+parent.getPrimitive().toString());
  
  primitiveControlArea.clearElements();
  pieceListArea.clearElements();
  textureControlArea.clearElements();
//fileControlArea.clearElements();
  
  primitiveMap.clear();
  pieceMap.clear();
  widgetMap.clear();
  
  this.removeGuiElement(textureRect);
  textureRect = new TexturedRectangleLive(0, 0, xSize, ySize, parent.model.textureWidth(), parent.model.textureHeight(), 0, 0, parent.model.textureWidth(), parent.model.textureHeight(), loc);
  this.addGuiElement(textureRect);
  
  addPieceList();
  
  addTextureControls();
  
  addPrimitiveControls();
  
  addFileControls();
  
  }

private void setTextureXSize(int size)
  {  
  GuiModelEditor.model.setTextureSize(size, parent.model.textureHeight());
  updateTextureSize();
  }

private void setTextureYSize(int size)
  {
  GuiModelEditor.model.setTextureSize(parent.model.textureWidth(), size);
  updateTextureSize();
  }

/**
 * builds a new buffered image and texture-rendering widget with the currently set texture size
 * subsequently calls updateTexture() to upload the new image to gfx texture
 */
private void updateTextureSize()
  {  
  GuiModelEditor.image = new BufferedImage(parent.model.textureWidth(), parent.model.textureHeight(), BufferedImage.TYPE_INT_ARGB);
  updateTexture();
  refreshGui();
  }

/**
 * should be called whenever a piece moves and the texture needs updating;
 */
private void updateTexture()
  {
  for(int x = 0; x< GuiModelEditor.image.getWidth(); x++)
    {
    for(int y = 0; y< GuiModelEditor.image.getHeight(); y++)
      {
      GuiModelEditor.image.setRGB(x, y, 0xff000000);//clear image to default  black 0% alpha (opaque)
      }
    }
  ArrayList<ModelPiece> pieces = new ArrayList<ModelPiece>();
  GuiModelEditor.model.getPieces(pieces);
  for(ModelPiece piece : pieces)
    {
    for(Primitive primitive : piece.getPrimitives())
      {
      primitive.addUVMapToImage(GuiModelEditor.image);
      }
    }
  AWTextureManager.instance().updateImageBasedTexture(GuiModelEditor.uvMapTextureName, GuiModelEditor.image);
  }

private void addTextureControls()
  {
  int w = ((width - xSize)/2)-17;
  int c0 = 5;//label
  int c1 = c0+17;//-
  int c2 = c1+12;//20+12 --input
  int c3 = 2 + w - 12;//+      
  int w2 = w - 24 - 20;//width of the input bar
  int totalHeight = 3;  
  
  Label label;
  NumberInput input;
  Button button;
  
  label = new Label(c0, totalHeight, StatCollector.translateToLocal("guistrings.texture_size")+":");
  textureControlArea.addGuiElement(label);
  totalHeight+=12;
  
  /***************************************** X SIZE **********************************************/
  label = new Label(c0, totalHeight, "X:");
  textureControlArea.addGuiElement(label);
  
  input = new NumberInput(c2, totalHeight, w2, parent.model.textureWidth(), this)
    {
    @Override
    public void onValueUpdated(float value)
      {
      setTextureXSize((int)value);
      }
    };
  input.setIntegerValue();
  textureControlArea.addGuiElement(input);
  
  button = new Button(c1, totalHeight, 12, 12, "-")
    {
    @Override
    protected void onPressed()
      {
      if(parent.model.textureWidth()>1)
        {
        setTextureXSize(parent.model.textureWidth()-1);
        }
      }
    };
  textureControlArea.addGuiElement(button);
  
  button = new Button(c3, totalHeight, 12, 12, "+")
    {
    @Override
    protected void onPressed()
      {
      if(parent.model.textureWidth()<1024)
        {
        setTextureXSize(parent.model.textureWidth()+1);
        }
      }
    };
  textureControlArea.addGuiElement(button);
  
  totalHeight+=12;
  
  /***************************************** Y SIZE **********************************************/
  label = new Label(c0, totalHeight, "Y:");
  textureControlArea.addGuiElement(label);
  
  input = new NumberInput(c2, totalHeight, w2, parent.model.textureHeight(), this)
    {
    @Override
    public void onValueUpdated(float value)
      {
      setTextureYSize((int)value);
      }
    };
  input.setIntegerValue();
  textureControlArea.addGuiElement(input);
  
  button = new Button(c1, totalHeight, 12, 12, "-")
    {
    @Override
    protected void onPressed()
      {
      if(parent.model.textureHeight()>1)
        {
        setTextureYSize(parent.model.textureHeight()-1);
        }
      }
    };
  textureControlArea.addGuiElement(button);
  
  button = new Button(c3, totalHeight, 12, 12, "+")
    {
    @Override
    protected void onPressed()
      {
      if(parent.model.textureHeight()<1024)
        {
        setTextureYSize(parent.model.textureHeight()+1);
        }
      }
    };
  textureControlArea.addGuiElement(button);
  
  totalHeight+=12;
  
  textureControlArea.setAreaSize(totalHeight);
  }

private void addFileControls()
  {
  Button button = new Button(8,8, 55, 12, "Export UV")
    {
    @Override
    protected void onPressed()
      {
      File path = new File("config/AWConfig/meim/uvmaps/"+System.currentTimeMillis()+".png");
      try
        {
        ImageIO.write(parent.image, "png", path);
        }
      catch (IOException e)
        {
        e.printStackTrace();
        }
      }
    };
  fileControlArea.addGuiElement(button);
  /**
   * TODO
   */
  }

/**
 * add the primitive controls to the primitive control area
 */
private void addPrimitiveControls()
  {
  Primitive p = parent.getPrimitive();
  if(p==null)
    {
    return;
    }
  if(p.getClass()==PrimitiveBox.class)
    {
    addBoxControls();
    }
  else if(p.getClass()==PrimitiveTriangle.class)
    {
    addTriangleControls();
    }
  else if(p.getClass()==PrimitiveQuad.class)
    {
    addQuadControls();
    }
  }

private void addBoxControls()
  {
  int w = ((width - xSize)/2)-17;
  int c0 = 5;//label
  int c1 = c0+17;//-
  int c2 = c1+12;//20+12 --input
  int c3 = 2 + w - 12;//+      
  int w2 = w - 24 - 20;//width of the input bar
  int totalHeight = 3;  
  
  Label label;
  NumberInput input;
  Button button;
  

  PrimitiveBox currentBox = (PrimitiveBox)parent.getPrimitive();
  

  /************************************* TX *********************************/
  label = new Label(c0, totalHeight, "TX");
  primitiveControlArea.addGuiElement(label);
  
  button = new Button(c1, totalHeight, 12, 12, "-")
    {
    @Override
    protected void onPressed()
      {      
      PrimitiveBox box = (PrimitiveBox)parent.getPrimitive();
      if(box.tx()>0)
        {
        box.setTx(box.tx()-1);
        NumberInput num = (NumberInput)widgetMap.get("TX");
        num.setValue(box.tx());
        updateTexture();
        refreshGui();
        }
      }
    };
  primitiveControlArea.addGuiElement(button);
  
  button = new Button(c3, totalHeight, 12, 12, "+")
    {
    @Override
    protected void onPressed()
      {      
      PrimitiveBox box = (PrimitiveBox)parent.getPrimitive();
      if(box.tx()<GuiModelEditor.image.getWidth()-1)
        {
        box.setTx(box.tx()+1);
        NumberInput num = (NumberInput)widgetMap.get("TX");
        num.setValue(box.tx());  
        updateTexture();
        refreshGui();      
        }
      }
    };
  primitiveControlArea.addGuiElement(button);
  
  input = new NumberInput(c2, totalHeight, w2, currentBox.tx(), this)
    {
    @Override
    public void onValueUpdated(float value)
      {
      PrimitiveBox box = (PrimitiveBox)parent.getPrimitive();
      box.setTx(value);
      updateTexture();
      refreshGui();
      }
    };
  primitiveControlArea.addGuiElement(input);
  widgetMap.put("TX", input);
  input.setAllowNegative();
  
  totalHeight+=12;
  
  
  /************************************* TY *********************************/
  label = new Label(c0, totalHeight, "TY");
  primitiveControlArea.addGuiElement(label);
  
  button = new Button(c1, totalHeight, 12, 12, "-")
    {
    @Override
    protected void onPressed()
      {      
      PrimitiveBox box = (PrimitiveBox)parent.getPrimitive();
      if(box.ty()>0)
        {
        box.setTx(box.ty()-1);
        NumberInput num = (NumberInput)widgetMap.get("TY");
        num.setValue(box.ty());
        updateTexture();
        refreshGui();
        }
      }
    };
  primitiveControlArea.addGuiElement(button);
  
  button = new Button(c3, totalHeight, 12, 12, "+")
    {
    @Override
    protected void onPressed()
      {      
      PrimitiveBox box = (PrimitiveBox)parent.getPrimitive();
      if(box.ty()<GuiModelEditor.image.getHeight()-1)
        {
        box.setTx(box.ty()+1);
        NumberInput num = (NumberInput)widgetMap.get("TY");
        num.setValue(box.ty());  
        updateTexture();
        refreshGui();      
        }
      }
    };
  primitiveControlArea.addGuiElement(button);
  
  input = new NumberInput(c2, totalHeight, w2, currentBox.ty(), this)
    {
    @Override
    public void onValueUpdated(float value)
      {
      PrimitiveBox box = (PrimitiveBox)parent.getPrimitive();
      box.setTy(value);
      updateTexture();
      refreshGui();
      }
    };
  primitiveControlArea.addGuiElement(input);
  widgetMap.put("TY", input);
  input.setAllowNegative();
  
  totalHeight+=12;  
  }

private void addTriangleControls()
  {
  int w = ((width - xSize)/2)-17;
  int c0 = 5;//label
  int c1 = c0+17;//-
  int c2 = c1+12;//20+12 --input
  int c3 = 2 + w - 12;//+      
  int w2 = w - 24 - 20;//width of the input bar
  int totalHeight = 3;  
  
  Label label;
  NumberInput input;
  Button button;
  

  PrimitiveTriangle currentBox = (PrimitiveTriangle)parent.getPrimitive();
  
  /************************************* MOVE-ALL *********************************/
  label = new Label(c0, totalHeight, "U");
  primitiveControlArea.addGuiElement(label);
  
  button = new Button(c1, totalHeight, 12, 12, "-")
    {
    @Override
    protected void onPressed()
      {      
      PrimitiveTriangle box = (PrimitiveTriangle)parent.getPrimitive();
      if(box.u1()>0)
        {
        box.setUV(box.u1()-1, box.v1(), box.u2()-1, box.v2(), box.u3()-1, box.v3());
        updateTexture();
        refreshGui();
        }
      }
    };
  primitiveControlArea.addGuiElement(button);
  
  button = new Button(c3, totalHeight, 12, 12, "+")
    {
    @Override
    protected void onPressed()
      {      
      PrimitiveTriangle box = (PrimitiveTriangle)parent.getPrimitive();
      if(box.u1()<GuiModelEditor.image.getWidth()-1)
        {
        box.setUV(box.u1()+1, box.v1(), box.u2()+1, box.v2(), box.u3()+1, box.v3());
        updateTexture();
        refreshGui();      
        }
      }
    };
  primitiveControlArea.addGuiElement(button);
  
  totalHeight+=12;
  
  label = new Label(c0, totalHeight, "V");
  primitiveControlArea.addGuiElement(label);
  
  button = new Button(c1, totalHeight, 12, 12, "-")
    {
    @Override
    protected void onPressed()
      {      
      PrimitiveTriangle box = (PrimitiveTriangle)parent.getPrimitive();
      if(box.u1()>0)
        {
        box.setUV(box.u1(), box.v1()-1, box.u2(), box.v2()-1, box.u3(), box.v3()-1);       
        updateTexture();
        refreshGui();
        }
      }
    };
  primitiveControlArea.addGuiElement(button);
  
  button = new Button(c3, totalHeight, 12, 12, "+")
    {
    @Override
    protected void onPressed()
      {      
      PrimitiveTriangle box = (PrimitiveTriangle)parent.getPrimitive();
      if(box.u1()<GuiModelEditor.image.getWidth()-1)
        {
        box.setUV(box.u1(), box.v1()+1, box.u2(), box.v2()+1, box.u3(), box.v3()+1);
        updateTexture();
        refreshGui();      
        }
      }
    };
  primitiveControlArea.addGuiElement(button);
  
  totalHeight+=12;
  
  /************************************* U1 *********************************/
  label = new Label(c0, totalHeight, "U1");
  primitiveControlArea.addGuiElement(label);
  
  button = new Button(c1, totalHeight, 12, 12, "-")
    {
    @Override
    protected void onPressed()
      {      
      PrimitiveTriangle box = (PrimitiveTriangle)parent.getPrimitive();
      if(box.u1()>0)
        {
        box.setUV(box.u1()-1, box.v1(), box.u2(), box.v2(), box.u3(), box.v3());
        NumberInput num = (NumberInput)widgetMap.get("U1");
        num.setValue(box.u1());
        updateTexture();
        refreshGui();
        }
      }
    };
  primitiveControlArea.addGuiElement(button);
  
  button = new Button(c3, totalHeight, 12, 12, "+")
    {
    @Override
    protected void onPressed()
      {      
      PrimitiveTriangle box = (PrimitiveTriangle)parent.getPrimitive();
      if(box.u1()<GuiModelEditor.image.getWidth()-1)
        {
        box.setUV(box.u1()+1, box.v1(), box.u2(), box.v2(), box.u3(), box.v3());
        NumberInput num = (NumberInput)widgetMap.get("U1");
        num.setValue(box.u1());  
        updateTexture();
        refreshGui();      
        }
      }
    };
  primitiveControlArea.addGuiElement(button);
  
  input = new NumberInput(c2, totalHeight, w2, currentBox.u1(), this)
    {
    @Override
    public void onValueUpdated(float value)
      {
      PrimitiveTriangle box = (PrimitiveTriangle)parent.getPrimitive();
      box.setUV(value, box.v1(), box.u2(), box.v2(), box.u3(), box.v3());
      
      updateTexture();
      refreshGui();
      }
    };
  primitiveControlArea.addGuiElement(input);
  widgetMap.put("U1", input);
  input.setAllowNegative();
  
  totalHeight+=12;
  
  
  /************************************* V1 *********************************/
  label = new Label(c0, totalHeight, "V1");
  primitiveControlArea.addGuiElement(label);
  
  button = new Button(c1, totalHeight, 12, 12, "-")
    {
    @Override
    protected void onPressed()
      {      
      PrimitiveTriangle box = (PrimitiveTriangle)parent.getPrimitive();
      if(box.u1()>0)
        {
        box.setUV(box.u1(), box.v1()-1, box.u2(), box.v2(), box.u3(), box.v3());
        NumberInput num = (NumberInput)widgetMap.get("V1");
        num.setValue(box.v1());
        updateTexture();
        refreshGui();
        }
      }
    };
  primitiveControlArea.addGuiElement(button);
  
  button = new Button(c3, totalHeight, 12, 12, "+")
    {
    @Override
    protected void onPressed()
      {      
      PrimitiveTriangle box = (PrimitiveTriangle)parent.getPrimitive();
      if(box.u1()<GuiModelEditor.image.getWidth()-1)
        {
        box.setUV(box.u1(), box.v1()+1, box.u2(), box.v2(), box.u3(), box.v3());
        NumberInput num = (NumberInput)widgetMap.get("V1");
        num.setValue(box.v1());  
        updateTexture();
        refreshGui();      
        }
      }
    };
  primitiveControlArea.addGuiElement(button);
  
  input = new NumberInput(c2, totalHeight, w2, currentBox.v1(), this)
    {
    @Override
    public void onValueUpdated(float value)
      {
      PrimitiveTriangle box = (PrimitiveTriangle)parent.getPrimitive();
      box.setUV(box.u1(), value, box.u2(), box.v2(), box.u3(), box.v3());
      
      updateTexture();
      refreshGui();
      }
    };
  primitiveControlArea.addGuiElement(input);
  widgetMap.put("V1", input);
  input.setAllowNegative();
  
  totalHeight+=12;
  
  
  /************************************* U2 *********************************/
  label = new Label(c0, totalHeight, "U2");
  primitiveControlArea.addGuiElement(label);
  
  button = new Button(c1, totalHeight, 12, 12, "-")
    {
    @Override
    protected void onPressed()
      {      
      PrimitiveTriangle box = (PrimitiveTriangle)parent.getPrimitive();
      if(box.u1()>0)
        {
        box.setUV(box.u1(), box.v1(), box.u2()-1, box.v2(), box.u3(), box.v3());
        NumberInput num = (NumberInput)widgetMap.get("U2");
        num.setValue(box.u2());
        updateTexture();
        refreshGui();
        }
      }
    };
  primitiveControlArea.addGuiElement(button);
  
  button = new Button(c3, totalHeight, 12, 12, "+")
    {
    @Override
    protected void onPressed()
      {      
      PrimitiveTriangle box = (PrimitiveTriangle)parent.getPrimitive();
      if(box.u1()<GuiModelEditor.image.getWidth()-1)
        {
        box.setUV(box.u1(), box.v1(), box.u2()+1, box.v2(), box.u3(), box.v3());
        NumberInput num = (NumberInput)widgetMap.get("U2");
        num.setValue(box.u2());  
        updateTexture();
        refreshGui();      
        }
      }
    };
  primitiveControlArea.addGuiElement(button);
  
  input = new NumberInput(c2, totalHeight, w2, currentBox.u2(), this)
    {
    @Override
    public void onValueUpdated(float value)
      {
      PrimitiveTriangle box = (PrimitiveTriangle)parent.getPrimitive();
      box.setUV(box.u1(), box.v1(), value, box.v2(), box.u3(), box.v3());
      
      updateTexture();
      refreshGui();
      }
    };
  primitiveControlArea.addGuiElement(input);
  widgetMap.put("U2", input);
  input.setAllowNegative();
  
  totalHeight+=12;
  
  
  /************************************* V2 *********************************/
  label = new Label(c0, totalHeight, "V2");
  primitiveControlArea.addGuiElement(label);
  
  button = new Button(c1, totalHeight, 12, 12, "-")
    {
    @Override
    protected void onPressed()
      {      
      PrimitiveTriangle box = (PrimitiveTriangle)parent.getPrimitive();
      if(box.u1()>0)
        {
        box.setUV(box.u1(), box.v1(), box.u2(), box.v2()-1, box.u3(), box.v3());
        NumberInput num = (NumberInput)widgetMap.get("V2");
        num.setValue(box.v2());
        updateTexture();
        refreshGui();
        }
      }
    };
  primitiveControlArea.addGuiElement(button);
  
  button = new Button(c3, totalHeight, 12, 12, "+")
    {
    @Override
    protected void onPressed()
      {      
      PrimitiveTriangle box = (PrimitiveTriangle)parent.getPrimitive();
      if(box.u1()<GuiModelEditor.image.getWidth()-1)
        {
        box.setUV(box.u1(), box.v1(), box.u2(), box.v2()+1, box.u3(), box.v3());
        NumberInput num = (NumberInput)widgetMap.get("V2");
        num.setValue(box.v2());  
        updateTexture();
        refreshGui();      
        }
      }
    };
  primitiveControlArea.addGuiElement(button);
  
  input = new NumberInput(c2, totalHeight, w2, currentBox.v2(), this)
    {
    @Override
    public void onValueUpdated(float value)
      {
      PrimitiveTriangle box = (PrimitiveTriangle)parent.getPrimitive();
      box.setUV(box.u1(), box.v1(), box.u2(), value, box.u3(), box.v3());
      
      updateTexture();
      refreshGui();
      }
    };
  primitiveControlArea.addGuiElement(input);
  widgetMap.put("V2", input);
  input.setAllowNegative();
  
  totalHeight+=12;
  
  
  /************************************* U3 *********************************/
  label = new Label(c0, totalHeight, "U3");
  primitiveControlArea.addGuiElement(label);
  
  button = new Button(c1, totalHeight, 12, 12, "-")
    {
    @Override
    protected void onPressed()
      {      
      PrimitiveTriangle box = (PrimitiveTriangle)parent.getPrimitive();
      if(box.u1()>0)
        {
        box.setUV(box.u1(), box.v1(), box.u2(), box.v2(), box.u3()-1, box.v3());
        NumberInput num = (NumberInput)widgetMap.get("U3");
        num.setValue(box.u3());
        updateTexture();
        refreshGui();
        }
      }
    };
  primitiveControlArea.addGuiElement(button);
  
  button = new Button(c3, totalHeight, 12, 12, "+")
    {
    @Override
    protected void onPressed()
      {      
      PrimitiveTriangle box = (PrimitiveTriangle)parent.getPrimitive();
      if(box.u1()<GuiModelEditor.image.getWidth()-1)
        {
        box.setUV(box.u1(), box.v1(), box.u2(), box.v2(), box.u3()+1, box.v3());
        NumberInput num = (NumberInput)widgetMap.get("U3");
        num.setValue(box.u3());  
        updateTexture();
        refreshGui();      
        }
      }
    };
  primitiveControlArea.addGuiElement(button);
  
  input = new NumberInput(c2, totalHeight, w2, currentBox.u3(), this)
    {
    @Override
    public void onValueUpdated(float value)
      {
      PrimitiveTriangle box = (PrimitiveTriangle)parent.getPrimitive();
      box.setUV(box.u1(), box.v1(), box.u2(), box.v2(), value, box.v3());
      
      updateTexture();
      refreshGui();
      }
    };
  primitiveControlArea.addGuiElement(input);
  widgetMap.put("U3", input);
  input.setAllowNegative();
  
  totalHeight+=12;
  
  
  /************************************* V3 *********************************/
  label = new Label(c0, totalHeight, "V3");
  primitiveControlArea.addGuiElement(label);
  
  button = new Button(c1, totalHeight, 12, 12, "-")
    {
    @Override
    protected void onPressed()
      {      
      PrimitiveTriangle box = (PrimitiveTriangle)parent.getPrimitive();
      if(box.u1()>0)
        {
        box.setUV(box.u1(), box.v1(), box.u2(), box.v2(), box.u3(), box.v3()-1);
        NumberInput num = (NumberInput)widgetMap.get("V3");
        num.setValue(box.v3());
        updateTexture();
        refreshGui();
        }
      }
    };
  primitiveControlArea.addGuiElement(button);
  
  button = new Button(c3, totalHeight, 12, 12, "+")
    {
    @Override
    protected void onPressed()
      {      
      PrimitiveTriangle box = (PrimitiveTriangle)parent.getPrimitive();
      if(box.u1()<GuiModelEditor.image.getWidth()-1)
        {
        box.setUV(box.u1(), box.v1(), box.u2(), box.v2(), box.u3(), box.v3()+1);
        NumberInput num = (NumberInput)widgetMap.get("V3");
        num.setValue(box.v3());  
        updateTexture();
        refreshGui();      
        }
      }
    };
  primitiveControlArea.addGuiElement(button);
  
  input = new NumberInput(c2, totalHeight, w2, currentBox.v3(), this)
    {
    @Override
    public void onValueUpdated(float value)
      {
      PrimitiveTriangle box = (PrimitiveTriangle)parent.getPrimitive();
      box.setUV(box.u1(), box.v1(), box.u2(), box.v2(), box.u3(), value);
      
      updateTexture();
      refreshGui();
      }
    };
  primitiveControlArea.addGuiElement(input);
  widgetMap.put("V3", input);
  input.setAllowNegative();
  
  totalHeight+=12;
  
  
  /************************************* ROTATE *********************************/
  label = new Label(c0, totalHeight, "ROT");
  primitiveControlArea.addGuiElement(label);
  
  button = new Button(c1, totalHeight, 12, 12, "-")
    {
    @Override
    protected void onPressed()
      {      
      PrimitiveTriangle box = (PrimitiveTriangle)parent.getPrimitive();
      box.rotateTriangleUV(-1);
      updateTexture();
      refreshGui();
      }
    };
  primitiveControlArea.addGuiElement(button);
  
  button = new Button(c3, totalHeight, 12, 12, "+")
    {
    @Override
    protected void onPressed()
      {      
      PrimitiveTriangle box = (PrimitiveTriangle)parent.getPrimitive();
      box.rotateTriangleUV(1);
      updateTexture();
      refreshGui();
      }
    };
  primitiveControlArea.addGuiElement(button);
  
  totalHeight+=12;
  
  /************************************* FLIP *********************************/
  
  }

private void addQuadControls()
  {
  int w = ((width - xSize)/2)-17;
  int c0 = 5;//label
  int c1 = c0+17;//-
  int c2 = c1+12;//20+12 --input
  int c3 = 2 + w - 12;//+      
  int w2 = w - 24 - 20;//width of the input bar
  int totalHeight = 3;  
  
  Label label;
  NumberInput input;
  Button button;
  

  PrimitiveQuad currentBox = (PrimitiveQuad)parent.getPrimitive();
  

  /************************************* TX *********************************/
  label = new Label(c0, totalHeight, "TX");
  primitiveControlArea.addGuiElement(label);
  
  button = new Button(c1, totalHeight, 12, 12, "-")
    {
    @Override
    protected void onPressed()
      {      
      PrimitiveQuad box = (PrimitiveQuad)parent.getPrimitive();
      if(box.tx()>0)
        {
        box.setTx(box.tx()-1);
        NumberInput num = (NumberInput)widgetMap.get("TX");
        num.setValue(box.tx());
        updateTexture();
        refreshGui();
        }
      }
    };
  primitiveControlArea.addGuiElement(button);
  
  button = new Button(c3, totalHeight, 12, 12, "+")
    {
    @Override
    protected void onPressed()
      {      
      PrimitiveQuad box = (PrimitiveQuad)parent.getPrimitive();
      if(box.tx()<GuiModelEditor.image.getWidth()-1)
        {
        box.setTx(box.tx()+1);
        NumberInput num = (NumberInput)widgetMap.get("TX");
        num.setValue(box.tx());  
        updateTexture();
        refreshGui();      
        }
      }
    };
  primitiveControlArea.addGuiElement(button);
  
  input = new NumberInput(c2, totalHeight, w2, currentBox.tx(), this)
    {
    @Override
    public void onValueUpdated(float value)
      {
      PrimitiveQuad box = (PrimitiveQuad)parent.getPrimitive();
      box.setTx(value);
      updateTexture();
      refreshGui();
      }
    };
  primitiveControlArea.addGuiElement(input);
  widgetMap.put("TX", input);
  input.setAllowNegative();
  
  totalHeight+=12;
  
  
  /************************************* TY *********************************/
  label = new Label(c0, totalHeight, "TY");
  primitiveControlArea.addGuiElement(label);
  
  button = new Button(c1, totalHeight, 12, 12, "-")
    {
    @Override
    protected void onPressed()
      {      
      PrimitiveQuad box = (PrimitiveQuad)parent.getPrimitive();
      if(box.ty()>0)
        {
        box.setTx(box.ty()-1);
        NumberInput num = (NumberInput)widgetMap.get("TY");
        num.setValue(box.ty());
        updateTexture();
        refreshGui();
        }
      }
    };
  primitiveControlArea.addGuiElement(button);
  
  button = new Button(c3, totalHeight, 12, 12, "+")
    {
    @Override
    protected void onPressed()
      {      
      PrimitiveQuad box = (PrimitiveQuad)parent.getPrimitive();
      if(box.ty()<GuiModelEditor.image.getHeight()-1)
        {
        box.setTx(box.ty()+1);
        NumberInput num = (NumberInput)widgetMap.get("TY");
        num.setValue(box.ty());  
        updateTexture();
        refreshGui();      
        }
      }
    };
  primitiveControlArea.addGuiElement(button);
  
  input = new NumberInput(c2, totalHeight, w2, currentBox.ty(), this)
    {
    @Override
    public void onValueUpdated(float value)
      {
      PrimitiveQuad box = (PrimitiveQuad)parent.getPrimitive();
      box.setTy(value);
      updateTexture();
      refreshGui();
      }
    };
  primitiveControlArea.addGuiElement(input);
  widgetMap.put("TY", input);
  input.setAllowNegative();
  
  totalHeight+=12; 
  }

/**
 * add the selectable piece list to the piece-list control area
 */
private void addPieceList()
  {
  ArrayList<ModelPiece> pieces = new ArrayList<ModelPiece>();
  GuiModelEditor.model.getPieces(pieces);
    
  int totalHeight = 3;
  Label label = null;
  
  Listener listener = new Listener(Listener.MOUSE_UP)
    {
    @Override
    public boolean onEvent(GuiElement widget, ActivationEvent evt)
      {
      if(widget.isMouseOverElement(evt.mx, evt.my))
        {
        Label l = (Label)widget;
        if(pieceMap.containsKey(l))
          {
          ModelPiece piece = pieceMap.get(l);
          parent.modelWidget.setSelection(piece, null);  
          refreshGui();        
          }
        else if(primitiveMap.containsKey(l))
          {
          Primitive p = primitiveMap.get(l);
          parent.modelWidget.setSelection(p.parent, p);
          refreshGui();
          }
        }
      return true;
      }
    };
  
  String prefix;
  int partNum = 1;
  for(ModelPiece piece : pieces)
    {
    partNum = 1;
    label = new Label(3, totalHeight, piece.getName());
    label.addNewListener(listener);
    pieceListArea.addGuiElement(label);
    pieceMap.put(label, piece);
    totalHeight+=12;
    for(Primitive primitive : piece.getPrimitives())
      {
      prefix = primitive.getClass()==PrimitiveBox.class ? "BOX" : primitive.getClass()==PrimitiveQuad.class? "QUAD" : "TRI";
      label = new Label(3, totalHeight, "  "+prefix+":"+partNum);
      label.addNewListener(listener);
      pieceListArea.addGuiElement(label);    
      primitiveMap.put(label, primitive);
      totalHeight+=12;
      partNum++;
      }
    }
  pieceListArea.setAreaSize(totalHeight);
  }

}
