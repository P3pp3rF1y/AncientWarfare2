package net.shadowmage.ancientwarfare.npc.proxy;

import net.shadowmage.ancientwarfare.core.config.ClientOptions;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.proxy.ClientProxyBase;
import net.shadowmage.ancientwarfare.npc.AncientWarfareNPC;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;
import net.shadowmage.ancientwarfare.npc.gui.GuiNpcInventory;
import net.shadowmage.ancientwarfare.npc.gui.GuiUpkeepOrder;
import net.shadowmage.ancientwarfare.npc.gui.GuiWorkOrder;
import net.shadowmage.ancientwarfare.npc.render.RenderNpcBase;
import net.shadowmage.ancientwarfare.npc.skin.NpcSkinManager;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class NpcClientProxy extends NpcCommonProxy
{

@Override
public void registerClient()
  {
  NetworkHandler.registerGui(NetworkHandler.GUI_NPC_INVENTORY, GuiNpcInventory.class);
  NetworkHandler.registerGui(NetworkHandler.GUI_NPC_WORK_ORDER, GuiWorkOrder.class);
  NetworkHandler.registerGui(NetworkHandler.GUI_NPC_UPKEEP_ORDER, GuiUpkeepOrder.class);
  RenderingRegistry.registerEntityRenderingHandler(NpcBase.class, new RenderNpcBase());
  
  registerClientOptions();
  }

public void registerClientOptions()
  {
  ClientOptions.INSTANCE.registerClientOption(ClientOptions.OPTION_RENDER_NPC_AI, "Render NPC AI Tasks", true, AncientWarfareNPC.config);
  ClientOptions.INSTANCE.registerClientOption(ClientOptions.OPTION_RENDER_NPC_FRIENDLY_NAMES, "Render friendly/neutral NPC nameplates", true, AncientWarfareNPC.config);
  ClientOptions.INSTANCE.registerClientOption(ClientOptions.OPTION_RENDER_NPC_HOSTILE_NAMES, "Render hostile NPC nameplates", true, AncientWarfareNPC.config);
  }

@Override
public void loadSkins()
  {
  NpcSkinManager.INSTANCE.loadSkinPacks();
  }
}
