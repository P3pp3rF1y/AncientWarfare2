package net.shadowmage.ancientwarfare.automation.gui;

import net.shadowmage.ancientwarfare.automation.tile.WorkSiteAnimalFarm;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;

public class GuiWorksiteAnimalControl extends GuiContainerBase
{

WorkSiteAnimalFarm worksite;

public GuiWorksiteAnimalControl(ContainerBase par1Container)
  {
  super(par1Container, 256, 240, defaultBackground);
  }

@Override
public void initElements()
  {

  }

@Override
public void setupElements()
  {

  }

@Override
protected boolean onGuiCloseRequested()
  {
  NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_WORKSITE_INVENTORY, worksite.xCoord, worksite.yCoord, worksite.zCoord);
  return false;
  }

}
