package net.shadowmage.ancientwarfare.automation.gui;

import net.minecraft.util.StatCollector;
import net.shadowmage.ancientwarfare.automation.container.ContainerWorksiteInventorySideSelection;
import net.shadowmage.ancientwarfare.core.block.RelativeSide;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.Button;
import net.shadowmage.ancientwarfare.core.gui.elements.Label;

public class GuiWorksiteInventorySideSelection extends GuiContainerBase
{

public GuiWorksiteInventorySideSelection(ContainerBase par1Container)
  {
  super(par1Container, 256, 240, defaultBackground);
  this.shouldCloseOnVanillaKeys = false;
  }

@Override
public void initElements()
  {

  }

@Override
public void setupElements()
  {
  this.clearElements();
  ContainerWorksiteInventorySideSelection container = (ContainerWorksiteInventorySideSelection)inventorySlots;
  int height = 8;
  Label label;
  Button button;
  RelativeSide accessed;
  AWLog.logDebug("adding side map elements from container: "+container);
  for(RelativeSide side : RelativeSide.values())
    {
    label = new Label(8, height, side.name());
    addGuiElement(label);

    accessed = container.sideMap.get(side);
    label = new Label(128, height, accessed==null? "None" : accessed.name());
    addGuiElement(label);
    height+=14;
    }  
  
  button = new Button(256-8-55, 8, 55, 12, StatCollector.translateToLocal("guistrings.done"))
    {
    @Override
    protected void onPressed()
      {
      ContainerWorksiteInventorySideSelection container = (ContainerWorksiteInventorySideSelection)inventorySlots;
      container.sendSettingsToServer();
      }
    };
  addGuiElement(button);
  }

@Override
public void refreshGui()
  {
  AWLog.logDebug("refreshing gui..");
  super.refreshGui();
  }

@Override
public void onGuiClosed()
  {
  
  super.onGuiClosed();
  }

}
