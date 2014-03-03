package net.shadowmage.ancientwarfare.structure.gui;

import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;

public class GuiStructureValidationSettings extends GuiContainerBase
{

GuiStructureScanner parent;

public GuiStructureValidationSettings(GuiStructureScanner parent)
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
