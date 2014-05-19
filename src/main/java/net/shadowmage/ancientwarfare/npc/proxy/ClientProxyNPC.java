package net.shadowmage.ancientwarfare.npc.proxy;

import net.shadowmage.ancientwarfare.core.proxy.ClientProxyBase;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;
import net.shadowmage.ancientwarfare.npc.render.RenderNpcBase;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class ClientProxyNPC extends ClientProxyBase
{

@Override
public void registerClient()
  {
  RenderingRegistry.registerEntityRenderingHandler(NpcBase.class, new RenderNpcBase());
  }


}
