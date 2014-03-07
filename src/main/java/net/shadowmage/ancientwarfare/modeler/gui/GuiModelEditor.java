package net.shadowmage.ancientwarfare.modeler.gui;

import java.io.File;

import net.minecraft.client.Minecraft;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.config.Statics;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiFileSelect;
import net.shadowmage.ancientwarfare.core.gui.elements.Button;
import net.shadowmage.ancientwarfare.core.gui.elements.CompositeScrolled;
import net.shadowmage.ancientwarfare.core.gui.elements.ModelWidget;
import net.shadowmage.ancientwarfare.core.model.ModelBaseAW;
import net.shadowmage.ancientwarfare.core.model.ModelPiece;
import net.shadowmage.ancientwarfare.core.model.Primitive;
import net.shadowmage.ancientwarfare.core.model.PrimitiveBox;
import net.shadowmage.ancientwarfare.core.model.PrimitiveQuad;
import net.shadowmage.ancientwarfare.core.model.PrimitiveTriangle;

public class GuiModelEditor extends GuiContainerBase
{

static ModelBaseAW model;

ModelWidget modelWidget;

CompositeScrolled pieceControlArea;
CompositeScrolled primitiveControlArea;
CompositeScrolled fileControlArea;
CompositeScrolled partListArea;

public GuiModelEditor(ContainerBase par1Container)
  {
  super(par1Container, 256, 240, defaultBackground);
  }

@Override
public void initElements()
  {
  modelWidget = new ModelWidget(0, 0, 256, 240)
    {
    @Override
    protected void onSelection(ModelPiece piece, Primitive primitive)
      {
      handleSelection(piece, primitive);
      }
    };
  modelWidget.setSelectable(true);
  if(model!=null)//attempt to use existing client-side model, if it exists
    {
    modelWidget.setModel(model);
    }
  else
    {
    modelWidget.initModel(); 
    model = modelWidget.getModel();
    }
  this.addGuiElement(modelWidget);
  
  pieceControlArea = new CompositeScrolled(-guiLeft, -guiTop, ((width-xSize) / 2), height/2);
  addGuiElement(pieceControlArea);
  
  primitiveControlArea = new CompositeScrolled(-guiLeft, -guiTop + height/2, ((width-xSize) / 2), height/2);
  addGuiElement(primitiveControlArea);
  
  fileControlArea = new CompositeScrolled(xSize, -guiTop, ((width-xSize) / 2), height/2);
  addGuiElement(fileControlArea);
  
  partListArea = new CompositeScrolled(xSize, -guiTop + height/2, ((width-xSize) / 2), height/2);
  addGuiElement(partListArea);
  }

@Override
public void setupElements()
  {
  pieceControlArea.clearElements();
  primitiveControlArea.clearElements();
  fileControlArea.clearElements();
  partListArea.clearElements();
  
  pieceControlArea.setRenderPosition(-guiLeft, -guiTop);
  pieceControlArea.setSize(((width-xSize) / 2), height/2);
  
  primitiveControlArea.setRenderPosition(-guiLeft, -guiTop + height/2);
  primitiveControlArea.setSize(((width-xSize) / 2), height/2);
  
  fileControlArea.setRenderPosition(xSize, -guiTop);
  fileControlArea.setSize((width-xSize) / 2, height/2);
  
  partListArea.setRenderPosition(xSize, -guiTop + height/2);
  partListArea.setSize((width-xSize)/2, height/2);
  
  addFileControls();
  addPieceList();
    
  ModelPiece piece = getModelPiece();
  if(piece!=null)
    {
    addPieceElements();
    Primitive p = getPrimitive();   
    if(p!=null)
      {
      addPrimitiveElements(p);
      } 
    else
      {
      primitiveControlArea.setAreaSize(18);
      addNewPrimitiveButton(3);
      }
    } 
  else
    {
    addNewPieceButton(3);
    pieceControlArea.setAreaSize(18);
    primitiveControlArea.setAreaSize(18);
    }
  }

protected ModelPiece getModelPiece()
  {
  return modelWidget.getSelectedPiece();
  }

protected Primitive getPrimitive()
  {
  return modelWidget.getSelectedPrimitive();
  }

/**
 * save<br>
 * load<br>
 * import<br>
 * load texture<br>
 * uv-map<br>
 */
private void addFileControls()
  {
  int totalHeight = 3;
  int w = ((width - xSize)/2)-17;
  int h = 12;
  
  Button b = new Button(3, totalHeight, w, h, "Load Model")
    {
    @Override
    protected void onPressed()
      {
      GuiFileSelect gui = new GuiFileSelect(GuiModelEditor.this, Statics.configPath, false)
        {
        @Override
        public void onFileSelected(File file)
          {
          modelWidget.loadModel(file);
          }
        };
      Minecraft.getMinecraft().displayGuiScreen(gui);      
      }
    };
  totalHeight+=12;
  fileControlArea.addGuiElement(b);
  
  b = new Button(3, totalHeight, w, h, "Save Model")
    {
    @Override
    protected void onPressed()
      {
      GuiFileSelect gui = new GuiFileSelect(GuiModelEditor.this, Statics.configPath, false)
        {
        @Override
        public void onFileSelected(File file)
          {
          modelWidget.saveModel(file);
          }
        };
      Minecraft.getMinecraft().displayGuiScreen(gui); 
      }
    };
  totalHeight+=12;
  fileControlArea.addGuiElement(b);
  
  b = new Button(3, totalHeight, w, h, "Import Pieces")
    {
    @Override
    protected void onPressed()
      {
      GuiFileSelect gui = new GuiFileSelect(GuiModelEditor.this, Statics.configPath, false)
        {
        @Override
        public void onFileSelected(File file)
          {
          modelWidget.importPieces(file);
          }
        };
      Minecraft.getMinecraft().displayGuiScreen(gui);  
      }
    };
  totalHeight+=12;
  fileControlArea.addGuiElement(b);
  
  b = new Button(3, totalHeight, w, h, "Load Texture")
    {
    @Override
    protected void onPressed()
      {
      GuiFileSelect gui = new GuiFileSelect(GuiModelEditor.this, Statics.configPath, false)
        {
        @Override
        public void onFileSelected(File file)
          {
          modelWidget.loadTexture(file);
          }
        };
      Minecraft.getMinecraft().displayGuiScreen(gui);      
      }
    };
  totalHeight+=12;
  fileControlArea.addGuiElement(b);
  
  b = new Button(3, totalHeight, w, h, "U/V Map")
    {
    @Override
    protected void onPressed()
      {
      /**
       * TODO open UV Map editor GUI
       */
      }
    };
  totalHeight+=12;
  fileControlArea.addGuiElement(b);
  
  fileControlArea.setAreaSize(totalHeight);
  }

/**
 * per-piece controls<br>
 * delete piece<br>
 * copy piece<br>
 * swap parent<br>
 * rename piece<br>
 * piece origin<br>
 * piece rotation<br>
 */
private void addPieceElements()
  {
  int totalHeight = 3;
  int w = ((width - xSize)/2)-17;
  int h = 12;
  totalHeight = addNewPieceButton(totalHeight);//"New Piece"
  
  Button b = new Button(3, totalHeight, w, h, "Delete Piece")
    {
    @Override
    protected void onPressed()
      {
      modelWidget.deleteSelectedPiece();
      }
    };
  totalHeight+=12;
  pieceControlArea.addGuiElement(b);
  
  b = new Button(3, totalHeight, w, h, "Copy Piece")
    {
    @Override
    protected void onPressed()
      {
      modelWidget.copyPiece();
      }
    };
  totalHeight+=12;
  pieceControlArea.addGuiElement(b);
  
  b = new Button(3, totalHeight, w, h, "Swap Parent")
    {
    @Override
    protected void onPressed()
      {
      GuiPieceSelection gui = new GuiPieceSelection(GuiModelEditor.this)
        {
        @Override
        protected void onPieceSelected(ModelPiece piece)
          {
          modelWidget.swapPieceParent(piece);
          }
        };
      Minecraft.getMinecraft().displayGuiScreen(gui);
      }
    };
  totalHeight+=12;
  pieceControlArea.addGuiElement(b);
  
  b = new Button(3, totalHeight, w, h, "Rename Piece")
    {
    @Override
    protected void onPressed()
      {
      GuiPieceNameInput gui = new GuiPieceNameInput(GuiModelEditor.this)
        {
        @Override
        protected void onNameSelected(String name)
          {
          modelWidget.renameCurrentPiece(name);
          }
        };
      Minecraft.getMinecraft().displayGuiScreen(gui);
      }
    };
  totalHeight+=12;
  pieceControlArea.addGuiElement(b);
  
 /**
  * TODO add controls for piece origin -- x, y, z
  * TODO add controls for piece rotation -- x, y, z
  */
  pieceControlArea.setAreaSize(totalHeight);
  }

private int addNewPieceButton(int height)
  {
  int w = ((width - xSize)/2)-17;
  Button b = new Button(3, height, w, 12, "New Piece")
    {
    @Override
    protected void onPressed()
      {
      GuiPieceNameInput gui = new GuiPieceNameInput(GuiModelEditor.this)
        {
        @Override
        protected void onNameSelected(String name)
          {
          modelWidget.addNewPiece(name);
          }
        };
      Minecraft.getMinecraft().displayGuiScreen(gui);
      }
    };
  pieceControlArea.addGuiElement(b);
  return height + 12;
  }

private int addNewPrimitiveButton(int height)
  {
  int w = ((width - xSize)/2)-17;
  Button b = new Button(3, height, w, 12, "New Primitive")
    {
    @Override
    protected void onPressed()
      {
      GuiPrimitiveSelection gui = new GuiPrimitiveSelection(GuiModelEditor.this)
        {
        @Override
        protected void onPrimitiveCreated(Primitive p)
          {
          modelWidget.addNewPrimitive(p);
          }
        };
      Minecraft.getMinecraft().displayGuiScreen(gui);
      }
    };
  primitiveControlArea.addGuiElement(b);
  return height + 12;
  }

/**
 * per primitive controls<br>
 * delete primitive<br>
 * copy primitive<br>
 * swap primitive parent<br>
 * primitive origin<br>
 * primitive rotation<br>
 * primitive location (offset from rotation point)<br>
 */
private void addPrimitiveElements(Primitive prim)
  {  
  int totalHeight = 3;
  int w = ((width - xSize)/2)-17;
  int h = 12;
  totalHeight = addNewPrimitiveButton(totalHeight);

  Button b = new Button(3, totalHeight, w, h, "Delete Primitive")
    {
    @Override
    protected void onPressed()
      {
      modelWidget.deleteSelectedPrimitive();
      }
    };
  totalHeight+=12;
  primitiveControlArea.addGuiElement(b);
  
  b = new Button(3, totalHeight, w, h, "Copy Primitive")
    {
    @Override
    protected void onPressed()
      {
      modelWidget.copyPrimitive();
      }
    };
  totalHeight+=12;
  primitiveControlArea.addGuiElement(b);
  
  b = new Button(3, totalHeight, w, h, "Swap Parent")
    {
    @Override
    protected void onPressed()
      {
      GuiPieceSelection gui = new GuiPieceSelection(GuiModelEditor.this)
        {
        @Override
        protected void onPieceSelected(ModelPiece piece)
          {
          modelWidget.swapPrimitiveParent(piece);
          }
        };
      Minecraft.getMinecraft().displayGuiScreen(gui);
      }
    };
  totalHeight+=12;
  primitiveControlArea.addGuiElement(b);
   
  if(prim.getClass()==PrimitiveBox.class)
    {
    /**
     * TODO add x,y,z,w,l,h,rx,ry,rz controls
     */
    }
  else if(prim.getClass()==PrimitiveTriangle.class)
    {
    /**
     * TODO add x1,y1,z1,x2,y2,z2,x3,y3,z3,flip controls
     */
    }
  else if(prim.getClass()==PrimitiveQuad.class)
    {
    /**
     * TODO add x,y,z,w,h,rx,ry,rz,flip controls
     */
    }
  
  primitiveControlArea.setAreaSize(totalHeight);
  }

private void addPieceList()
  {
  
  }


private void handleSelection(ModelPiece piece, Primitive primitive)
  {
  this.refreshGui();
  }

}
