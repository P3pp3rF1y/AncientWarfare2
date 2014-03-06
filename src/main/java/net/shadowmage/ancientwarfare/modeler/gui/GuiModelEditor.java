package net.shadowmage.ancientwarfare.modeler.gui;

import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.ModelWidget;
import net.shadowmage.ancientwarfare.core.model.ModelPiece;
import net.shadowmage.ancientwarfare.core.model.Primitive;
import net.shadowmage.ancientwarfare.core.model.PrimitiveBox;
import net.shadowmage.ancientwarfare.core.model.PrimitiveQuad;
import net.shadowmage.ancientwarfare.core.model.PrimitiveTriangle;

public class GuiModelEditor extends GuiContainerBase
{

ModelWidget modelWidget;

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
  this.addGuiElement(modelWidget);
  modelWidget.setSelectable(true);
  modelWidget.initModel();
  }

@Override
public void setupElements()
  {
  this.clearElements();
  this.addGuiElement(modelWidget);
  
  addDefaultElements();
  
  ModelPiece piece = modelWidget.getSelectedPiece();
  if(piece!=null)
    {
    addPieceElements();
    }
  
  Primitive p = modelWidget.getSelectedPrimitive();
  if(p!=null)
    {
    addPrimitiveElements(p);
    }
  
  }

/**
 * save<br>
 * load<br>
 * import<br>
 * uv-map<br>
 * new piece<br>
 */
private void addDefaultElements()
  {
  
  }

/**
 * per-piece controls<br>
 * piece origin<br>
 * piece rotation<br>
 * copy piece<br>
 * delete piece<br>
 * rename piece<br>
 * swap parent<br>
 * new primitive<br>
 */
private void addPieceElements()
  {
  
  }

/**
 * per primitive controls<br>
 * primitive origin<br>
 * primitive rotation<br>
 * primitive location (offset from rotation point)<br>
 * delete primitive<br>
 * copy primitive<br>
 */
private void addPrimitiveElements(Primitive prim)
  {
  if(prim.getClass()==PrimitiveBox.class)
    {
    
    }
  else if(prim.getClass()==PrimitiveTriangle.class)
    {
    
    }
  else if(prim.getClass()==PrimitiveQuad.class)
    {
    
    }
  }


private void handleSelection(ModelPiece piece, Primitive primitive)
  {
  this.refreshGui();
  }

}
