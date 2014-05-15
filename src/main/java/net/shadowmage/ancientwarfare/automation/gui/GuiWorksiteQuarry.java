package net.shadowmage.ancientwarfare.automation.gui;

import net.minecraft.util.StatCollector;
import net.shadowmage.ancientwarfare.automation.container.ContainerWorksiteQuarry;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.Button;
import net.shadowmage.ancientwarfare.core.gui.elements.Label;

public class GuiWorksiteQuarry extends GuiWorksiteBase
{
ContainerWorksiteQuarry container;
public GuiWorksiteQuarry(ContainerBase par1Container)
  {
  super(par1Container);
  this.container = (ContainerWorksiteQuarry)par1Container;
  this.ySize = container.guiHeight+12;
  }

@Override
public void initElements()
  {
  Label label = new Label(8, container.topLabel, StatCollector.translateToLocal("guistrings.inventory.side.top"));
  addGuiElement(label);
  
  label = new Label(8, container.playerLabel, StatCollector.translateToLocal("guistrings.inventory.player"));
  addGuiElement(label);
  
  Button button = new Button(8, ySize-8-12, xSize/2-8, 12, StatCollector.translateToLocal("guistrings.inventory.setsides"));
  addGuiElement(button);
  
//  button = new Button(xSize/2, ySize-8-12, xSize/2-8, 12, StatCollector.translateToLocal("guistrings.inventory.setsides"));
//  addGuiElement(button);
  }

@Override
public void setupElements()
  {
  
  }


}
