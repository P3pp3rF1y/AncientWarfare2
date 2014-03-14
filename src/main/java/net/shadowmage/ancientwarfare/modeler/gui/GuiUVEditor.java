package net.shadowmage.ancientwarfare.modeler.gui;

import java.util.ArrayList;
import java.util.HashMap;

import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.Listener;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase.ActivationEvent;
import net.shadowmage.ancientwarfare.core.gui.elements.CompositeScrolled;
import net.shadowmage.ancientwarfare.core.gui.elements.GuiElement;
import net.shadowmage.ancientwarfare.core.gui.elements.Label;
import net.shadowmage.ancientwarfare.core.gui.elements.TexturedRectangleLive;
import net.shadowmage.ancientwarfare.core.model.ModelPiece;
import net.shadowmage.ancientwarfare.core.model.Primitive;
import net.shadowmage.ancientwarfare.core.model.PrimitiveBox;
import net.shadowmage.ancientwarfare.core.model.PrimitiveQuad;
import net.shadowmage.ancientwarfare.core.util.AWTextureManager;
import net.shadowmage.ancientwarfare.core.util.AWTextureManager.Texture;

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

static int textureXSize = 256;
static int textureYSize = 256;

//map of label-element combos, to select pieces through clicking on/in the piece list area
private HashMap<Label, ModelPiece> pieceMap = new HashMap<Label, ModelPiece>();
private HashMap<Label, Primitive> primitiveMap = new HashMap<Label, Primitive>();

public GuiUVEditor(GuiModelEditor parent)
  {
  super((ContainerBase) parent.inventorySlots, 256, 256, defaultBackground);
  this.parent = parent;
  }

@Override
public void initElements()
  {
  textureRect = new TexturedRectangleLive(0, 0, xSize, ySize, textureXSize, textureYSize, 0, 0, textureXSize, textureYSize, "editorTexture");
  this.addGuiElement(textureRect);
  
  textureControlArea = new CompositeScrolled(-guiLeft, -guiTop, (width - xSize)/2, height/2);
  addGuiElement(textureControlArea);
  
  primitiveControlArea = new CompositeScrolled(-guiLeft, -guiTop + height/2, (width - xSize)/2, height/2);
  addGuiElement(primitiveControlArea);
  
  fileControlArea = new CompositeScrolled(width, -guiTop, (width - xSize)/2, height/2);
  addGuiElement(fileControlArea);
  
  pieceListArea = new CompositeScrolled(width, -guiTop + height/2, (width - xSize)/2, height/2);
  addGuiElement(pieceListArea);
  }

@Override
public void setupElements()
  {
  textureControlArea.setRenderPosition(-guiLeft, -guiTop);//top-left
  primitiveControlArea.setRenderPosition(-guiLeft, -guiTop + height/2);//bottom-left
  fileControlArea.setRenderPosition(xSize, -guiTop);//top-right
  pieceListArea.setRenderPosition(xSize, -guiTop + height/2);//bottom-right
  
  primitiveControlArea.clearElements();
  pieceListArea.clearElements();
//textureControlArea.clearElements();
//fileControlArea.clearElements();
  
  primitiveMap.clear();
  pieceMap.clear();
  
  addPieceList();
  
  
//  Texture t = AWTextureManager.instance().getTexture("editorTexture");
  
  }

private void addPieceList()
  {
  ArrayList<ModelPiece> pieces = new ArrayList<ModelPiece>();
  parent.model.getPieces(pieces);
    
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
          }
        else if(primitiveMap.containsKey(l))
          {
          Primitive p = primitiveMap.get(l);
          parent.modelWidget.setSelection(p.parent, p);
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
