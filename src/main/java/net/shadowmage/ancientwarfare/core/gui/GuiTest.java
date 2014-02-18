package net.shadowmage.ancientwarfare.core.gui;

import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.Button;
import net.shadowmage.ancientwarfare.core.gui.elements.Checkbox;
import net.shadowmage.ancientwarfare.core.gui.elements.CompositeScrolled;
import net.shadowmage.ancientwarfare.core.gui.elements.CompositeTabbed;
import net.shadowmage.ancientwarfare.core.gui.elements.Scrollbar;
import net.shadowmage.ancientwarfare.core.gui.elements.Tab;

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
  
  CompositeScrolled com = new CompositeScrolled(50, 50, 50, 50);  
  this.addGuiElement(com);
  com.setAreaSize(200);  
  com.addGuiElement(new Button(5, 5, 100, 20, "TestButton2"));
  
  CompositeTabbed tab = new CompositeTabbed(50, 100, 100, 100);  
  tab.addTab("Test1", true);
  tab.addTab("Test2", true);
  tab.addTab("Test3", false);
  tab.addTab("Test4", false);
  this.addGuiElement(tab);
  
  tab.addGuiElement("Test1", new Button(10, 10, 24, 16, "test"));
  
  Checkbox box = new Checkbox(10, 10, 16, 16, "TestBox");
  tab.addGuiElement("Test2", box);
//  this.addGuiElement(box);
  }

}
