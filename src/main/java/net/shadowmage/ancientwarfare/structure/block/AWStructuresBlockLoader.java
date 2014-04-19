package net.shadowmage.ancientwarfare.structure.block;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.shadowmage.ancientwarfare.core.api.AWBlocks;
import net.shadowmage.ancientwarfare.structure.item.AWStructuresItemLoader;
import net.shadowmage.ancientwarfare.structure.item.ItemBlockAdvancedSpawner;
import net.shadowmage.ancientwarfare.structure.tile.TEGateProxy;
import net.shadowmage.ancientwarfare.structure.tile.TileAdvancedSpawner;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

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
  }

}
