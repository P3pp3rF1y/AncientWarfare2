package net.shadowmage.ancientwarfare.npc.gui;

import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.npc.container.ContainerNpcBard;

public class GuiNpcBard extends GuiContainerBase
{

ContainerNpcBard container;
public GuiNpcBard(ContainerBase container)
  {
  super(container);
  this.container = (ContainerNpcBard)container;
  this.xSize = 128+80;
  this.ySize = 4*12 + 16;
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
  NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_NPC_INVENTORY, container.npc.getEntityId(), 0, 0);
  return false;
  }

}
