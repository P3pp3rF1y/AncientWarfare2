package net.shadowmage.ancientwarfare.automation.gui;

import net.shadowmage.ancientwarfare.automation.container.ContainerWorksiteBase;
import net.shadowmage.ancientwarfare.automation.tile.TileWorksiteBase;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;

public abstract class GuiWorksiteBase extends GuiContainerBase
{

TileWorksiteBase worksite;

public GuiWorksiteBase(ContainerBase par1Container)
  {
  super(par1Container, 178, 240, defaultBackground);
  worksite = ((ContainerWorksiteBase)inventorySlots).worksite;
  }

}
