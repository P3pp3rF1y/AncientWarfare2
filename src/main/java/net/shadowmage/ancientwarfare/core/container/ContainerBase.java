package net.shadowmage.ancientwarfare.core.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.shadowmage.ancientwarfare.core.interfaces.IContainerGuiCallback;

public class ContainerBase extends Container
{

EntityPlayer player;
IContainerGuiCallback gui;

public ContainerBase(EntityPlayer player, int x, int y, int z)
  {
  this.player = player;
  }

public final void setGui(IContainerGuiCallback gui)
  {
  this.gui = gui;
  }

protected final void sendDataToGui(Object data)
  {
  if(gui!=null)
    {
    gui.handlePacketData(data);
    }
  }

@Override
public boolean canInteractWith(EntityPlayer var1)
  {
  return true;
  }

}
