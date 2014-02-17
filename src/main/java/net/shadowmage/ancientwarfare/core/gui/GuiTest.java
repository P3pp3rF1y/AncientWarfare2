package net.shadowmage.ancientwarfare.core.gui;

import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.Button;

public class GuiTest extends GuiContainerBase
{

Button testButton;

public GuiTest(ContainerBase par1Container)
  {
  super(par1Container, 256, 240, "guiBackgroundLarge.png");
  }

@Override
public void setupElements()
  {
  // TODO Auto-generated method stub  
  }

@Override
public void initElements()
  {
  testButton = new Button(10, 10, 60, 20, "TestButton");
  this.addGuiElement(testButton);
  }

}
