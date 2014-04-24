package net.shadowmage.ancientwarfare.modeler.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.modeler.gui.GuiModelEditor;

public class ClientProxyModeler extends CommonProxyModeler
{

public ClientProxyModeler()
  {
  
  }

@Override
public void openGui(EntityPlayer player)
  {
  Minecraft.getMinecraft().displayGuiScreen(new GuiModelEditor(new ContainerBase(player, 0, 0, 0)));
  }

}
