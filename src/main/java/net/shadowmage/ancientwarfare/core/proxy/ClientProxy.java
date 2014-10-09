package net.shadowmage.ancientwarfare.core.proxy;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.Item;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.core.api.AWBlocks;
import net.shadowmage.ancientwarfare.core.config.AWCoreStatics;
import net.shadowmage.ancientwarfare.core.config.ConfigManager;
import net.shadowmage.ancientwarfare.core.gui.GuiBackpack;
import net.shadowmage.ancientwarfare.core.gui.GuiResearchBook;
import net.shadowmage.ancientwarfare.core.gui.crafting.GuiEngineeringStation;
import net.shadowmage.ancientwarfare.core.gui.research.GuiResearchStation;
import net.shadowmage.ancientwarfare.core.input.InputHandler;
import net.shadowmage.ancientwarfare.core.model.crafting_table.ModelEngineeringStation;
import net.shadowmage.ancientwarfare.core.model.crafting_table.ModelResearchStation;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.network.PacketHandlerClient;
import net.shadowmage.ancientwarfare.core.render.TileCraftingTableRender;
import net.shadowmage.ancientwarfare.core.tile.TileEngineeringStation;
import net.shadowmage.ancientwarfare.core.tile.TileResearchStation;
import cpw.mods.fml.client.config.DummyConfigElement.DummyCategoryElement;
import cpw.mods.fml.client.config.GuiConfig;
import cpw.mods.fml.client.config.GuiConfigEntries;
import cpw.mods.fml.client.config.GuiConfigEntries.CategoryEntry;
import cpw.mods.fml.client.config.IConfigElement;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.FMLCommonHandler;

/**
 * client-proxy for AW-Core
 * @author Shadowmage
 *
 */
public class ClientProxy extends ClientProxyBase
{

@Override
public void registerClient()
  {
  NetworkHandler.registerClientHandler(new PacketHandlerClient());
  FMLCommonHandler.instance().bus().register(InputHandler.instance());
  NetworkHandler.registerGui(NetworkHandler.GUI_CRAFTING, GuiEngineeringStation.class);
  NetworkHandler.registerGui(NetworkHandler.GUI_RESEARCH_STATION, GuiResearchStation.class);
  NetworkHandler.registerGui(NetworkHandler.GUI_BACKPACK, GuiBackpack.class);
  NetworkHandler.registerGui(NetworkHandler.GUI_RESEARCH_BOOK, GuiResearchBook.class);
  InputHandler.instance().loadConfig(AncientWarfareCore.config);
  
  TileCraftingTableRender render = new TileCraftingTableRender(new ModelEngineeringStation(), "textures/model/core/tile_engineering_station.png");
  ClientRegistry.bindTileEntitySpecialRenderer(TileEngineeringStation.class, render);
  MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(AWBlocks.engineeringStation), render);
  
  render = new TileCraftingTableRender(new ModelResearchStation(), "textures/model/core/tile_research_station.png");
  ClientRegistry.bindTileEntitySpecialRenderer(TileResearchStation.class, render);
  MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(AWBlocks.researchStation), render);
  
  ConfigManager.registerConfigCategory(new DummyCategoryElement("Core Keybinds", "tooltip.translation.key.goes.here", KeybindCategoryEntry.class));
  }

public void onConfigChanged()
  {
  InputHandler.instance().updateFromConfig();
  }

public static final class KeybindCategoryEntry extends CategoryEntry
{

public KeybindCategoryEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement configElement)
  {
  super(owningScreen, owningEntryList, configElement);
  }

@Override
protected GuiScreen buildChildScreen()
  {
  return new GuiConfig(this.owningScreen, getKeybindElements(), this.owningScreen.modID,
      owningScreen.allRequireWorldRestart || this.configElement.requiresWorldRestart(),
      owningScreen.allRequireMcRestart || this.configElement.requiresMcRestart(), this.owningScreen.title,
      ((this.owningScreen.titleLine2 == null ? "" : this.owningScreen.titleLine2) + " > " + this.name));
  }

private static List<IConfigElement> getKeybindElements()
  {
  List<IConfigElement> list = new ArrayList<IConfigElement>();
  Configuration config = AncientWarfareCore.config;
  list.add(new ConfigElement(config.get(AWCoreStatics.keybinds, "keybind.alt_item_use_1", 44)));
  list.add(new ConfigElement(config.get(AWCoreStatics.keybinds, "keybind.alt_item_use_2", 45)));
  list.add(new ConfigElement(config.get(AWCoreStatics.keybinds, "keybind.alt_item_use_3", 46)));
  list.add(new ConfigElement(config.get(AWCoreStatics.keybinds, "keybind.alt_item_use_4", 47)));
  list.add(new ConfigElement(config.get(AWCoreStatics.keybinds, "keybind.alt_item_use_5", 48)));
  return list;
  }

}

}
