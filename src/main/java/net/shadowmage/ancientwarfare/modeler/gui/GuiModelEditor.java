package net.shadowmage.ancientwarfare.modeler.gui;

import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.ModelWidget;

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
  modelWidget = new ModelWidget(0, 0, 256, 240);
  this.addGuiElement(modelWidget);
  }

@Override
public void setupElements()
  {
  
  }

}
