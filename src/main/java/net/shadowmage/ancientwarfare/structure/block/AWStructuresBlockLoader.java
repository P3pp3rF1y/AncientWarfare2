package net.shadowmage.ancientwarfare.structure.block;

import net.shadowmage.ancientwarfare.core.api.AWBlocks;
import net.shadowmage.ancientwarfare.structure.item.ItemBlockAdvancedSpawner;
import net.shadowmage.ancientwarfare.structure.item.ItemBlockStructureBuilder;
import net.shadowmage.ancientwarfare.structure.tile.TEGateProxy;
import net.shadowmage.ancientwarfare.structure.tile.TileAdvancedSpawner;
import net.shadowmage.ancientwarfare.structure.tile.TileDraftingStation;
import net.shadowmage.ancientwarfare.structure.tile.TileStructureBuilder;
import cpw.mods.fml.common.registry.GameRegistry;

public class AWStructuresBlockLoader
{

public static void load()
  {
  AWBlocks.advancedSpawner = new BlockAdvancedSpawner("advanced_spawner");
  GameRegistry.registerBlock(AWBlocks.advancedSpawner, ItemBlockAdvancedSpawner.class, "advanced_spawner");
  GameRegistry.registerTileEntity(TileAdvancedSpawner.class, "advanced_spawner_tile");
  
  AWBlocks.gateProxy = new BlockGateProxy("gate_proxy");
  GameRegistry.registerBlock(AWBlocks.gateProxy, "gate_proxy");
  GameRegistry.registerTileEntity(TEGateProxy.class, "gate_proxy_tile");
  
  BlockDraftingStation draftingStation;
  AWBlocks.draftingStation = draftingStation = new BlockDraftingStation("drafting_station");
  GameRegistry.registerBlock(AWBlocks.draftingStation, "drafting_station");
  GameRegistry.registerTileEntity(TileDraftingStation.class, "drafting_station_tile");
  draftingStation.setIcon(0, 0, "ancientwarfare:structure/drafting_station_bottom");
  draftingStation.setIcon(1, 0, "ancientwarfare:structure/drafting_station_top");
  draftingStation.setIcon(2, 0, "ancientwarfare:structure/drafting_station_front");
  draftingStation.setIcon(3, 0, "ancientwarfare:structure/drafting_station_front");
  draftingStation.setIcon(4, 0, "ancientwarfare:structure/drafting_station_side");
  draftingStation.setIcon(5, 0, "ancientwarfare:structure/drafting_station_side");
  
  BlockStructureBuilder builder;
  AWBlocks.builderBlock = builder = new BlockStructureBuilder("structure_builder_ticked");
  GameRegistry.registerBlock(AWBlocks.builderBlock, ItemBlockStructureBuilder.class, "structure_builder_ticked");
  GameRegistry.registerTileEntity(TileStructureBuilder.class, "structure_builder_ticked_tile");
  builder.setIcon(0, 0, "ancientwarfare:structure/builder_bottom");
  builder.setIcon(1, 0, "ancientwarfare:structure/builder_top");
  builder.setIcon(2, 0, "ancientwarfare:structure/builder_side");
  builder.setIcon(3, 0, "ancientwarfare:structure/builder_side");
  builder.setIcon(4, 0, "ancientwarfare:structure/builder_side");
  builder.setIcon(5, 0, "ancientwarfare:structure/builder_side");
  
  
  }

}
