package net.shadowmage.ancientwarfare.automation.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.automation.item.ItemWorksitePlacer;
import net.shadowmage.ancientwarfare.automation.tile.TileWorkerTest;
import net.shadowmage.ancientwarfare.automation.tile.WorkSiteCropFarm;
import net.shadowmage.ancientwarfare.automation.tile.WorkSiteMushroomFarm;
import net.shadowmage.ancientwarfare.automation.tile.WorkSiteQuarry;
import net.shadowmage.ancientwarfare.automation.tile.WorkSiteTreeFarm;
import net.shadowmage.ancientwarfare.core.block.RelativeSide;
import cpw.mods.fml.common.registry.GameRegistry;

public class AWAutomationBlockLoader
{

public static final Block workerTest = new BlockWorkerTest("block.testWorker");

public static final BlockWorksiteBase worksiteTest = new BlockWorksiteBase(Material.rock,"civic_quarry")
  {
  public TileEntity createTileEntity(World world, int metadata) 
    {
    return new WorkSiteQuarry();
    };
  };

public static final BlockWorksiteBase worksiteTest2 = new BlockWorksiteBase(Material.rock, "civic_forestry")
  {
  public TileEntity createTileEntity(World world, int metadata) 
    {
    return new WorkSiteTreeFarm();
    };
  };
  
public static final BlockWorksiteBase worksiteTest3 = new BlockWorksiteBase(Material.rock, "civic_crop_farm")
    {
    public TileEntity createTileEntity(World world, int metadata) 
      {
      return new WorkSiteCropFarm();
      };
    };
    
public static final BlockWorksiteBase worksiteTest4 = new BlockWorksiteBase(Material.rock, "civic_mushroom_farm")
  {
  public TileEntity createTileEntity(World world, int metadata) 
    {
    return new WorkSiteMushroomFarm();
    };
  };  

public static void load()
  {  
  GameRegistry.registerBlock(workerTest, "block.testWorker");
  GameRegistry.registerTileEntity(TileWorkerTest.class, "tile.testWorker");
    
  GameRegistry.registerBlock(worksiteTest, ItemWorksitePlacer.class, "civic_quarry");
  GameRegistry.registerTileEntity(WorkSiteQuarry.class, "civic_quarry_tile");
  worksiteTest.setIcon(RelativeSide.TOP, "ancientwarfare:civic/civicMineQuarryTop");
  worksiteTest.setIcon(RelativeSide.BOTTOM, "ancientwarfare:civic/civicFarmChickenSides");
  worksiteTest.setIcon(RelativeSide.FRONT, "ancientwarfare:civic/civicMineQuarrySides");
  worksiteTest.setIcon(RelativeSide.REAR, "ancientwarfare:civic/civicFarmCocoaSides");
  worksiteTest.setIcon(RelativeSide.LEFT, "ancientwarfare:civic/civicFarmNetherSides");
  worksiteTest.setIcon(RelativeSide.RIGHT, "ancientwarfare:civic/civicFarmOakSides");  
  
  GameRegistry.registerBlock(worksiteTest2, ItemWorksitePlacer.class, "civic_forestry");
  GameRegistry.registerTileEntity(WorkSiteTreeFarm.class, "civic_forestry_tile");
  worksiteTest2.setIcon(RelativeSide.TOP, "ancientwarfare:civic/civicMineQuarryTop");
  worksiteTest2.setIcon(RelativeSide.BOTTOM, "ancientwarfare:civic/civicFarmChickenSides");
  worksiteTest2.setIcon(RelativeSide.FRONT, "ancientwarfare:civic/civicFarmOakSides");
  worksiteTest2.setIcon(RelativeSide.REAR, "ancientwarfare:civic/civicFarmJungleSides");
  worksiteTest2.setIcon(RelativeSide.LEFT, "ancientwarfare:civic/civicFarmSpruceSides");
  worksiteTest2.setIcon(RelativeSide.RIGHT, "ancientwarfare:civic/civicFarmBirchSides");  
  
  GameRegistry.registerBlock(worksiteTest3, ItemWorksitePlacer.class, "civic_crop_farm");
  GameRegistry.registerTileEntity(WorkSiteCropFarm.class, "civic_crop_farm_tile");
  worksiteTest3.setIcon(RelativeSide.TOP, "ancientwarfare:civic/civicMineQuarryTop");
  worksiteTest3.setIcon(RelativeSide.BOTTOM, "ancientwarfare:civic/civicFarmChickenSides");
  worksiteTest3.setIcon(RelativeSide.FRONT, "ancientwarfare:civic/civicFarmWheatSides");
  worksiteTest3.setIcon(RelativeSide.REAR, "ancientwarfare:civic/civicFarmMelonSides");
  worksiteTest3.setIcon(RelativeSide.LEFT, "ancientwarfare:civic/civicFarmCarrotSides");
  worksiteTest3.setIcon(RelativeSide.RIGHT, "ancientwarfare:civic/civicFarmPotatoSides");
  
  GameRegistry.registerBlock(worksiteTest4, ItemWorksitePlacer.class, "civic_mushroom_farm");
  GameRegistry.registerTileEntity(WorkSiteMushroomFarm.class, "civic_mushroom_farm_tile");
  worksiteTest4.setIcon(RelativeSide.TOP, "ancientwarfare:civic/civicMineQuarryTop");
  worksiteTest4.setIcon(RelativeSide.BOTTOM, "ancientwarfare:civic/civicFarmChickenSides");
  worksiteTest4.setIcon(RelativeSide.FRONT, "ancientwarfare:civic/civicFarmNetherSides");
  worksiteTest4.setIcon(RelativeSide.REAR, "ancientwarfare:civic/civicFarmMelonSides");
  worksiteTest4.setIcon(RelativeSide.LEFT, "ancientwarfare:civic/civicFarmMushroomRedSides");
  worksiteTest4.setIcon(RelativeSide.RIGHT, "ancientwarfare:civic/civicFarmMushroomBrownSides");
  }

}
