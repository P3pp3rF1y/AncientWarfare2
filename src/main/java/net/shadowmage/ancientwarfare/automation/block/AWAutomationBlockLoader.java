package net.shadowmage.ancientwarfare.automation.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.automation.item.ItemWorksitePlacer;
import net.shadowmage.ancientwarfare.automation.tile.TileWorkerTest;
import net.shadowmage.ancientwarfare.automation.tile.WorkSiteQuarry;
import net.shadowmage.ancientwarfare.automation.tile.WorkSiteTreeFarm;
import net.shadowmage.ancientwarfare.core.block.RelativeSide;
import cpw.mods.fml.common.registry.GameRegistry;

public class AWAutomationBlockLoader
{

public static final Block workerTest = new BlockWorkerTest("block.testWorker");

public static final BlockWorksiteBase worksiteTest = new BlockWorksiteBase(Material.rock,"block.testWorksite")
  {
  public TileEntity createTileEntity(World world, int metadata) 
    {
    return new WorkSiteQuarry();
    };
  };

public static final BlockWorksiteBase worksiteTest2 = new BlockWorksiteBase(Material.rock, "block.testForestry")
  {
  public TileEntity createTileEntity(World world, int metadata) 
    {
    return new WorkSiteTreeFarm();
    };
  };

public static void load()
  {  
  GameRegistry.registerBlock(workerTest, "block.testWorker");
  GameRegistry.registerTileEntity(TileWorkerTest.class, "tile.testWorker");
    
  GameRegistry.registerBlock(worksiteTest, ItemWorksitePlacer.class, "block.testWorksite");
  GameRegistry.registerTileEntity(WorkSiteQuarry.class, "tile.worksiteQuarry");
  worksiteTest.setIcon(RelativeSide.TOP, "ancientwarfare:civic/civicMineQuarryTop");
  worksiteTest.setIcon(RelativeSide.BOTTOM, "ancientwarfare:civic/civicFarmChickenSides");
  worksiteTest.setIcon(RelativeSide.FRONT, "ancientwarfare:civic/civicMineQuarrySides");
  worksiteTest.setIcon(RelativeSide.REAR, "ancientwarfare:civic/civicFarmCocoaSides");
  worksiteTest.setIcon(RelativeSide.LEFT, "ancientwarfare:civic/civicFarmNetherSides");
  worksiteTest.setIcon(RelativeSide.RIGHT, "ancientwarfare:civic/civicFarmOakSides");  
  
  GameRegistry.registerBlock(worksiteTest2, ItemWorksitePlacer.class, "block.testForestry");
  GameRegistry.registerTileEntity(WorkSiteTreeFarm.class, "tile.worksiteForestry");
  worksiteTest2.setIcon(RelativeSide.TOP, "ancientwarfare:civic/civicMineQuarryTop");
  worksiteTest2.setIcon(RelativeSide.BOTTOM, "ancientwarfare:civic/civicFarmChickenSides");
  worksiteTest2.setIcon(RelativeSide.FRONT, "ancientwarfare:civic/civicFarmOakSides");
  worksiteTest2.setIcon(RelativeSide.REAR, "ancientwarfare:civic/civicFarmJungleSides");
  worksiteTest2.setIcon(RelativeSide.LEFT, "ancientwarfare:civic/civicFarmSpruceSides");
  worksiteTest2.setIcon(RelativeSide.RIGHT, "ancientwarfare:civic/civicFarmBirchSides");  
  }

}
