package net.shadowmage.ancientwarfare.automation.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.shadowmage.ancientwarfare.automation.item.ItemWorksitePlacer;
import net.shadowmage.ancientwarfare.automation.tile.TileWorkerTest;
import net.shadowmage.ancientwarfare.automation.tile.WorkSiteQuarry;
import net.shadowmage.ancientwarfare.automation.tile.WorkSiteTreeFarm;
import cpw.mods.fml.common.registry.GameRegistry;

public class AWAutomationBlockLoader
{

public static final Block workerTest = new BlockWorkerTest("block.testWorker");
public static final Block worksiteTest = new BlockWorksiteTest("block.testWorksite");
public static final Block worksiteTest2 = new BlockWorksiteBase(Material.rock, "block.testForestry")
  {
  public net.minecraft.tileentity.TileEntity createTileEntity(net.minecraft.world.World world, int metadata) 
    {
    return new WorkSiteTreeFarm();
    };
  };

public static void load()
  {  
  GameRegistry.registerBlock(workerTest, "block.testWorker");
  
  GameRegistry.registerBlock(worksiteTest, ItemWorksitePlacer.class, "block.testWorksite");
  GameRegistry.registerBlock(worksiteTest2, ItemWorksitePlacer.class, "block.testForestry");
  
  GameRegistry.registerTileEntity(TileWorkerTest.class, "tile.testWorker");
  GameRegistry.registerTileEntity(WorkSiteQuarry.class, "tile.worksiteQuarry");
  GameRegistry.registerTileEntity(WorkSiteTreeFarm.class, "tile.worksiteForestry");
  }

}
