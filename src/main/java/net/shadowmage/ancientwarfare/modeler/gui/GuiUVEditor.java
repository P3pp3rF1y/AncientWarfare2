package net.shadowmage.ancientwarfare.modeler.gui;

import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;

/**
 * UV editor for MEIM
 * @author Shadowmage
 *
 */
public class GuiUVEditor extends GuiContainerBase
{

GuiModelEditor parent;

public GuiUVEditor(GuiModelEditor parent)
  {
  super((ContainerBase) parent.inventorySlots, 256, 240, defaultBackground);
  this.parent = parent;
  }

@Override
public void initElements()
  {
 
  }

@Override
public void setupElements()
  {
  
  }

}
