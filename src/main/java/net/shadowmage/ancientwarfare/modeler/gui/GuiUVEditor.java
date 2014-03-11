package net.shadowmage.ancientwarfare.modeler.gui;

import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.CompositeScrolled;
import net.shadowmage.ancientwarfare.core.gui.elements.TexturedRectangleLive;

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
CompositeScrolled infoViewArea;

public GuiUVEditor(GuiModelEditor parent)
  {
  super((ContainerBase) parent.inventorySlots, 256, 256, defaultBackground);
  this.parent = parent;
  }

@Override
public void initElements()
  {
  textureRect = new TexturedRectangleLive(0, 0, 256, 256, 256, 256, 0, 0, 256, 256, "editorTexture");
  this.addGuiElement(textureRect);
  }

@Override
public void setupElements()
  {
  
  }

}
