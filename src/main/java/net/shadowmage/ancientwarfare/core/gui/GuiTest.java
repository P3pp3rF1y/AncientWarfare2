package net.shadowmage.ancientwarfare.core.gui;

import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.Button;
import net.shadowmage.ancientwarfare.core.gui.elements.Scrollbar;

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
  
  Scrollbar bar = new Scrollbar(30, 30, 20, 100, null);
  this.addGuiElement(bar);
  bar.setAreaSize(200);
  }

}
