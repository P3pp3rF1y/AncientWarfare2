package net.shadowmage.ancientwarfare.npc.gui;

import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.npc.container.ContainerNpcInventory;

public class GuiNpcInventory extends GuiNpcBase
{

public GuiNpcInventory(ContainerBase container)
  {
  super(container);
  this.xSize = 178;
  this.ySize = ((ContainerNpcInventory)container).guiHeight;
  }

@Override
public void initElements()
  {

  }

@Override
public void setupElements()
  {

  }

}
