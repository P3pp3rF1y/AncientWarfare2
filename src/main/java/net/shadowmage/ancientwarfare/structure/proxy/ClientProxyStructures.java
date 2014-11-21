package net.shadowmage.ancientwarfare.structure.proxy;

import net.minecraftforge.common.MinecraftForge;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.proxy.ClientProxyBase;
import net.shadowmage.ancientwarfare.structure.container.ContainerSoundBlock;
import net.shadowmage.ancientwarfare.structure.entity.EntityGate;
import net.shadowmage.ancientwarfare.structure.event.StructureBoundingBoxRenderer;
import net.shadowmage.ancientwarfare.structure.gui.GuiDraftingStation;
import net.shadowmage.ancientwarfare.structure.gui.GuiGateControl;
import net.shadowmage.ancientwarfare.structure.gui.GuiSoundBlock;
import net.shadowmage.ancientwarfare.structure.gui.GuiSpawnerAdvanced;
import net.shadowmage.ancientwarfare.structure.gui.GuiSpawnerAdvancedInventory;
import net.shadowmage.ancientwarfare.structure.gui.GuiSpawnerPlacer;
import net.shadowmage.ancientwarfare.structure.gui.GuiStructureScanner;
import net.shadowmage.ancientwarfare.structure.gui.GuiStructureSelection;
import net.shadowmage.ancientwarfare.structure.render.RenderGateHelper;
import net.shadowmage.ancientwarfare.structure.render.RenderStructureBuilder;
import net.shadowmage.ancientwarfare.structure.tile.TileStructureBuilder;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class ClientProxyStructures extends ClientProxyBase
{

@Override
public void registerClient()
  {
  NetworkHandler.registerGui(NetworkHandler.GUI_SCANNER, GuiStructureScanner.class);
  NetworkHandler.registerGui(NetworkHandler.GUI_BUILDER, GuiStructureSelection.class);
  NetworkHandler.registerGui(NetworkHandler.GUI_SPAWNER, GuiSpawnerPlacer.class);
  NetworkHandler.registerGui(NetworkHandler.GUI_SPAWNER_ADVANCED, GuiSpawnerAdvanced.class);
  NetworkHandler.registerGui(NetworkHandler.GUI_SPAWNER_ADVANCED_BLOCK, GuiSpawnerAdvanced.class);
  NetworkHandler.registerGui(NetworkHandler.GUI_SPAWNER_ADVANCED_INVENTORY, GuiSpawnerAdvancedInventory.class);
  NetworkHandler.registerGui(NetworkHandler.GUI_SPAWNER_ADVANCED_BLOCK_INVENTORY, GuiSpawnerAdvancedInventory.class);
  NetworkHandler.registerGui(NetworkHandler.GUI_GATE_CONTROL, GuiGateControl.class);
  NetworkHandler.registerGui(NetworkHandler.GUI_DRAFTING_STATION, GuiDraftingStation.class);
  NetworkHandler.registerGui(NetworkHandler.GUI_SOUND_BLOCK, GuiSoundBlock.class);
  MinecraftForge.EVENT_BUS.register(StructureBoundingBoxRenderer.instance());
  
  RenderingRegistry.registerEntityRenderingHandler(EntityGate.class, new RenderGateHelper());  
  ClientRegistry.bindTileEntitySpecialRenderer(TileStructureBuilder.class, new RenderStructureBuilder());
  }


}
