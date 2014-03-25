package net.shadowmage.ancientwarfare.automation.block;

import net.minecraft.block.Block;
import net.shadowmage.ancientwarfare.automation.item.ItemWorksitePlacer;
import net.shadowmage.ancientwarfare.automation.tile.TileWorkerTest;
import net.shadowmage.ancientwarfare.automation.tile.WorkSiteQuarry;
import cpw.mods.fml.common.registry.GameRegistry;

public class AWAutomationBlockLoader
{

public static final Block workerTest = new BlockWorkerTest("block.testWorker");
public static final Block worksiteTest = new BlockWorksiteTest("block.testWorksite");

public static void load()
  {  
  GameRegistry.registerBlock(workerTest, "block.testWorker");
  
  GameRegistry.registerBlock(worksiteTest, ItemWorksitePlacer.class, "block.testWorksite");
  
  GameRegistry.registerTileEntity(TileWorkerTest.class, "tile.testWorker");
  GameRegistry.registerTileEntity(WorkSiteQuarry.class, "tile.worksiteQuarry");
  }

}
