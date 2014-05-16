package net.shadowmage.ancientwarfare.automation.gui;

import net.minecraft.util.StatCollector;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.Button;

public class GuiWorksiteFishFarm extends GuiWorksiteBase
{

public GuiWorksiteFishFarm(ContainerBase par1Container)
  {
  super(par1Container);
  }

@Override
public void initElements()
  {
  this.addLabels();
  this.addSideSelectButton();
  
  Button button = new Button(xSize/2, ySize-8-12, xSize/2-8, 12, StatCollector.translateToLocal("guistrings.automation.fish_control"))    
    {
    @Override
    protected void onPressed()
      {
      container.worksite.openAltGui(player);
      }
    };
  addGuiElement(button);
  }

@Override
public void setupElements()
  {

  }

}
