package net.shadowmage.ancientwarfare.npc.proxy;

import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.proxy.ClientProxyBase;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;
import net.shadowmage.ancientwarfare.npc.gui.GuiNpcBase;
import net.shadowmage.ancientwarfare.npc.gui.GuiNpcInventory;
import net.shadowmage.ancientwarfare.npc.render.RenderNpcBase;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class ClientProxyNPC extends ClientProxyBase
{

@Override
public void registerClient()
  {
  NetworkHandler.registerGui(NetworkHandler.GUI_NPC_INVENTORY, GuiNpcInventory.class);
  RenderingRegistry.registerEntityRenderingHandler(NpcBase.class, new RenderNpcBase());
  }


}
