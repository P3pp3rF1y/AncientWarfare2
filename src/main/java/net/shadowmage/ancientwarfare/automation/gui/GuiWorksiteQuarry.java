package net.shadowmage.ancientwarfare.automation.gui;

import net.shadowmage.ancientwarfare.core.container.ContainerBase;

public class GuiWorksiteQuarry extends GuiWorksiteBase
{

public GuiWorksiteQuarry(ContainerBase par1Container)
  {
  super(par1Container);
  }

@Override
public void initElements()
  {
  addLabels();
  addSideSelectButton();
  
//  Button button = new Button(xSize/2, ySize-8-12, xSize/2-8, 12, StatCollector.translateToLocal("guistrings.automation.quarry_pattern"))    
//    {
//    @Override
//    protected void onPressed()
//      {
//      //TODO open quarry-pattern-select GUI
//      }
//    };
//  addGuiElement(button);
  }

@Override
public void setupElements()
  {
  
  }


}
