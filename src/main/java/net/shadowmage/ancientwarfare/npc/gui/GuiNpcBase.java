package net.shadowmage.ancientwarfare.npc.gui;

import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.npc.container.ContainerNpcBase;

public abstract class GuiNpcBase extends GuiContainerBase
{

ContainerNpcBase container;
public GuiNpcBase(ContainerBase container)
  {
  super(container);
  this.container = (ContainerNpcBase)container;
  }


}
