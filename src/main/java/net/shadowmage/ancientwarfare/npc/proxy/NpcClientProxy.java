package net.shadowmage.ancientwarfare.npc.proxy;

import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;
import net.shadowmage.ancientwarfare.core.config.ClientOptions;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.npc.AncientWarfareNPC;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;
import net.shadowmage.ancientwarfare.npc.gui.GuiBard;
import net.shadowmage.ancientwarfare.npc.gui.GuiCombatOrder;
import net.shadowmage.ancientwarfare.npc.gui.GuiNpcCreativeControls;
import net.shadowmage.ancientwarfare.npc.gui.GuiNpcInventory;
import net.shadowmage.ancientwarfare.npc.gui.GuiNpcTrade;
import net.shadowmage.ancientwarfare.npc.gui.GuiRecruitingStation;
import net.shadowmage.ancientwarfare.npc.gui.GuiRoutingOrder;
import net.shadowmage.ancientwarfare.npc.gui.GuiTownHallInventory;
import net.shadowmage.ancientwarfare.npc.gui.GuiUpkeepOrder;
import net.shadowmage.ancientwarfare.npc.gui.GuiWorkOrder;
import net.shadowmage.ancientwarfare.npc.item.AWNpcItemLoader;
import net.shadowmage.ancientwarfare.npc.render.RenderCommandOverlay;
import net.shadowmage.ancientwarfare.npc.render.RenderNpcBase;
import net.shadowmage.ancientwarfare.npc.render.RenderShield;
import net.shadowmage.ancientwarfare.npc.render.RenderWorkLines;
import net.shadowmage.ancientwarfare.npc.skin.NpcSkinManager;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;

public class NpcClientProxy extends NpcCommonProxy
{

@Override
public void registerClient()
  {
  NetworkHandler.registerGui(NetworkHandler.GUI_NPC_INVENTORY, GuiNpcInventory.class);
  NetworkHandler.registerGui(NetworkHandler.GUI_NPC_TRADE, GuiNpcTrade.class);
  NetworkHandler.registerGui(NetworkHandler.GUI_NPC_WORK_ORDER, GuiWorkOrder.class);
  NetworkHandler.registerGui(NetworkHandler.GUI_NPC_UPKEEP_ORDER, GuiUpkeepOrder.class);
  NetworkHandler.registerGui(NetworkHandler.GUI_NPC_COMBAT_ORDER, GuiCombatOrder.class);
  NetworkHandler.registerGui(NetworkHandler.GUI_NPC_ROUTING_ORDER, GuiRoutingOrder.class);
  NetworkHandler.registerGui(NetworkHandler.GUI_NPC_TOWN_HALL, GuiTownHallInventory.class);  
  NetworkHandler.registerGui(NetworkHandler.GUI_NPC_RECRUITING_STATION, GuiRecruitingStation.class);
  NetworkHandler.registerGui(NetworkHandler.GUI_NPC_BARD, GuiBard.class);
  NetworkHandler.registerGui(NetworkHandler.GUI_NPC_CREATIVE, GuiNpcCreativeControls.class);
  
  RenderingRegistry.registerEntityRenderingHandler(NpcBase.class, new RenderNpcBase());
  
  MinecraftForge.EVENT_BUS.register(RenderWorkLines.INSTANCE);//register render for orders items routes/block highlights
  FMLCommonHandler.instance().bus().register(RenderCommandOverlay.INSTANCE);//register overlay renderer
  MinecraftForge.EVENT_BUS.register(RenderCommandOverlay.INSTANCE);//register block/entity highlight renderer
  
  RenderShield shieldRender = new RenderShield();
  MinecraftForgeClient.registerItemRenderer(AWNpcItemLoader.woodenShield, shieldRender);
  MinecraftForgeClient.registerItemRenderer(AWNpcItemLoader.stoneShield, shieldRender);
  MinecraftForgeClient.registerItemRenderer(AWNpcItemLoader.ironShield, shieldRender);
  MinecraftForgeClient.registerItemRenderer(AWNpcItemLoader.goldShield, shieldRender);
  MinecraftForgeClient.registerItemRenderer(AWNpcItemLoader.diamondShield, shieldRender);
  
  registerClientOptions();
  }

private void registerClientOptions()
  {
  ClientOptions.INSTANCE.registerClientOption(ClientOptions.OPTION_RENDER_NPC_ADDITIONAL_INFO, "Main control for additional npc-related rendering", true, AncientWarfareNPC.config);
  ClientOptions.INSTANCE.registerClientOption(ClientOptions.OPTION_RENDER_NPC_AI, "Render NPC AI Tasks", true, AncientWarfareNPC.config);
  ClientOptions.INSTANCE.registerClientOption(ClientOptions.OPTION_RENDER_NPC_FRIENDLY_NAMES, "Render friendly/neutral NPC nameplates", true, AncientWarfareNPC.config);
  ClientOptions.INSTANCE.registerClientOption(ClientOptions.OPTION_RENDER_NPC_HOSTILE_NAMES, "Render hostile NPC nameplates", true, AncientWarfareNPC.config);
  ClientOptions.INSTANCE.registerClientOption(ClientOptions.OPTION_RENDER_HOSTILE_HEALTH, "Render health values on hostile npc nameplates", true, AncientWarfareNPC.config);
  ClientOptions.INSTANCE.registerClientOption(ClientOptions.OPTION_RENDER_FRIENDLY_HEALTH, "Render health values on friendly npc nameplates", true, AncientWarfareNPC.config);
  ClientOptions.INSTANCE.registerClientOption(ClientOptions.OPTION_RENDER_TEAM_COLORS, "Render team colors for nameplate names", true, AncientWarfareNPC.config);
  }

@Override
public void loadSkins()
  {
  NpcSkinManager.INSTANCE.loadSkinPacks();
  }


}
