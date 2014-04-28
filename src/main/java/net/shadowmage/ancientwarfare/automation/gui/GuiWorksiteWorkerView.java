package net.shadowmage.ancientwarfare.automation.gui;

import net.minecraft.util.StatCollector;
import net.shadowmage.ancientwarfare.automation.container.ContainerWorksiteWorkerView;
import net.shadowmage.ancientwarfare.automation.tile.TileWorksiteBase;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.Button;
import net.shadowmage.ancientwarfare.core.gui.elements.CompositeScrolled;
import net.shadowmage.ancientwarfare.core.gui.elements.Label;

public class GuiWorksiteWorkerView extends GuiContainerBase
{

TileWorksiteBase worksite;
CompositeScrolled workerViewArea;
ContainerWorksiteWorkerView container;

public GuiWorksiteWorkerView(ContainerBase par1Container)
  {
  super(par1Container, 256, 140, defaultBackground);
  container = (ContainerWorksiteWorkerView)par1Container;
  }

@Override
public void initElements()
  {
  workerViewArea = new CompositeScrolled(0, 0, 256, 140);
  addGuiElement(workerViewArea);
  }

@Override
public void setupElements()
  {
  workerViewArea.clearElements();
  
  Label label;
  Button button;
  int totalHeight = 8;
  for(String key : container.workerMap.keySet())
    {
    label = new Label(8, totalHeight, StatCollector.translateToLocal(key));
    workerViewArea.addGuiElement(label);
    totalHeight +=12;
    label = new Label(8, totalHeight, container.workerMap.get(key).toString());
    workerViewArea.addGuiElement(label);
    button = new Button(256-55-8-16, totalHeight, 55, 12, StatCollector.translateToLocal("guistrings.automation.remove_worker"))
      {
      @Override
      protected void onPressed()
        {
        //TODO make custom button class with ref to string name for worker
        }
      };
    workerViewArea.addGuiElement(button);
    totalHeight +=12;
    }
  workerViewArea.setAreaSize(totalHeight);
  }

}
