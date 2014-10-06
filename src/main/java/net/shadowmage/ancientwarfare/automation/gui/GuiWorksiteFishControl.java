package net.shadowmage.ancientwarfare.automation.gui;

import net.shadowmage.ancientwarfare.automation.container.ContainerWorksiteFishControl;
import net.shadowmage.ancientwarfare.automation.tile.worksite.WorkSiteFishFarm;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.Checkbox;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;

public class GuiWorksiteFishControl extends GuiContainerBase
{

ContainerWorksiteFishControl container;
WorkSiteFishFarm worksite;

Checkbox fishBox;
Checkbox inkBox;

public GuiWorksiteFishControl(ContainerBase par1Container)
  {
  super(par1Container, 168, 32+16, defaultBackground);
  container = (ContainerWorksiteFishControl)par1Container;
  this.worksite = container.worksite;
  }

@Override
public void initElements()
  {
  fishBox = new Checkbox(8, 8, 16, 16, "guistrings.automation.harvest_fish")
    {
    @Override
    public void onToggled()
      {
      container.harvestFish = checked();
      container.sendSettingsToServer();
      }
    };
  addGuiElement(fishBox);
  
  inkBox = new Checkbox(8, 8+16, 16, 16, "guistrings.automation.harvest_ink")
    {
    @Override
    public void onToggled()
      {
      container.harvestInk = checked();
      container.sendSettingsToServer();
      }
    };
  addGuiElement(inkBox);
  }

@Override
public void setupElements()
  {
  fishBox.setChecked(container.harvestFish);
  inkBox.setChecked(container.harvestInk);
  }

@Override
protected boolean onGuiCloseRequested()
  {
  container.sendSettingsToServer();
  NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_WORKSITE_FISH_FARM, worksite.xCoord, worksite.yCoord, worksite.zCoord);
  return false;
  }

}
