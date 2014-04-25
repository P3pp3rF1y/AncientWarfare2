package net.shadowmage.ancientwarfare.automation.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.automation.item.ItemWorksitePlacer;
import net.shadowmage.ancientwarfare.automation.tile.TileWorkerTest;
import net.shadowmage.ancientwarfare.automation.tile.WorkSiteAnimalFarm;
import net.shadowmage.ancientwarfare.automation.tile.WorkSiteCropFarm;
import net.shadowmage.ancientwarfare.automation.tile.WorkSiteMushroomFarm;
import net.shadowmage.ancientwarfare.automation.tile.WorkSiteQuarry;
import net.shadowmage.ancientwarfare.automation.tile.WorkSiteTreeFarm;
import net.shadowmage.ancientwarfare.automation.tile.WorksiteAutoCrafting;
import net.shadowmage.ancientwarfare.core.block.RelativeSide;
import cpw.mods.fml.common.registry.GameRegistry;

public class AWAutomationBlockLoader
{

public static final Block workerTest = new BlockWorkerTest("block.testWorker");

public static final BlockWorksiteBase worksiteQuarry = new BlockWorksiteBase(Material.rock,"civic_quarry")
  {
  public TileEntity createTileEntity(World world, int metadata) 
    {
    return new WorkSiteQuarry();
    };
  };

public static final BlockWorksiteBase worksiteForestry = new BlockWorksiteBase(Material.rock, "civic_forestry")
  {
  public TileEntity createTileEntity(World world, int metadata) 
    {
    return new WorkSiteTreeFarm();
    };
  };
  
public static final BlockWorksiteBase worksiteCropFarm = new BlockWorksiteBase(Material.rock, "civic_crop_farm")
    {
    public TileEntity createTileEntity(World world, int metadata) 
      {
      return new WorkSiteCropFarm();
      };
    };
    
public static final BlockWorksiteBase worksiteMushroomFarm = new BlockWorksiteBase(Material.rock, "civic_mushroom_farm")
  {
  public TileEntity createTileEntity(World world, int metadata) 
    {
    return new WorkSiteMushroomFarm();
    };
  };  
  
public static final BlockWorksiteBase worksiteAnimalFarm = new BlockWorksiteBase(Material.rock, "civic_animal_farm")
  {
  @Override
  public TileEntity createTileEntity(World world, int metadata)
    {
    return new WorkSiteAnimalFarm();
    }  
  };
  
public static final BlockWorksiteBase worksiteAutoCrafting = new BlockWorksiteBase(Material.rock, "civic_auto_craftin")
  {
  @Override
  public TileEntity createTileEntity(World world, int metadata)
    {
    return new WorksiteAutoCrafting();
    }
  };

public static void load()
  {  
  GameRegistry.registerBlock(workerTest, "block.testWorker");
  GameRegistry.registerTileEntity(TileWorkerTest.class, "tile.testWorker");
    
  GameRegistry.registerBlock(worksiteQuarry, ItemWorksitePlacer.class, "civic_quarry");
  GameRegistry.registerTileEntity(WorkSiteQuarry.class, "civic_quarry_tile");
  worksiteQuarry.setIcon(RelativeSide.TOP, "ancientwarfare:civic/civicMineQuarryTop");
  worksiteQuarry.setIcon(RelativeSide.BOTTOM, "ancientwarfare:civic/civicFarmChickenSides");
  worksiteQuarry.setIcon(RelativeSide.FRONT, "ancientwarfare:civic/civicMineQuarrySides");
  worksiteQuarry.setIcon(RelativeSide.REAR, "ancientwarfare:civic/civicFarmCocoaSides");
  worksiteQuarry.setIcon(RelativeSide.LEFT, "ancientwarfare:civic/civicFarmNetherSides");
  worksiteQuarry.setIcon(RelativeSide.RIGHT, "ancientwarfare:civic/civicFarmOakSides");  
  worksiteQuarry.setWorkSize(64);
  
  GameRegistry.registerBlock(worksiteForestry, ItemWorksitePlacer.class, "civic_forestry");
  GameRegistry.registerTileEntity(WorkSiteTreeFarm.class, "civic_forestry_tile");
  worksiteForestry.setIcon(RelativeSide.TOP, "ancientwarfare:civic/civicMineQuarryTop");
  worksiteForestry.setIcon(RelativeSide.BOTTOM, "ancientwarfare:civic/civicFarmChickenSides");
  worksiteForestry.setIcon(RelativeSide.FRONT, "ancientwarfare:civic/civicFarmOakSides");
  worksiteForestry.setIcon(RelativeSide.REAR, "ancientwarfare:civic/civicFarmJungleSides");
  worksiteForestry.setIcon(RelativeSide.LEFT, "ancientwarfare:civic/civicFarmSpruceSides");
  worksiteForestry.setIcon(RelativeSide.RIGHT, "ancientwarfare:civic/civicFarmBirchSides");  
  
  GameRegistry.registerBlock(worksiteCropFarm, ItemWorksitePlacer.class, "civic_crop_farm");
  GameRegistry.registerTileEntity(WorkSiteCropFarm.class, "civic_crop_farm_tile");
  worksiteCropFarm.setIcon(RelativeSide.TOP, "ancientwarfare:civic/civicMineQuarryTop");
  worksiteCropFarm.setIcon(RelativeSide.BOTTOM, "ancientwarfare:civic/civicFarmChickenSides");
  worksiteCropFarm.setIcon(RelativeSide.FRONT, "ancientwarfare:civic/civicFarmWheatSides");
  worksiteCropFarm.setIcon(RelativeSide.REAR, "ancientwarfare:civic/civicFarmMelonSides");
  worksiteCropFarm.setIcon(RelativeSide.LEFT, "ancientwarfare:civic/civicFarmCarrotSides");
  worksiteCropFarm.setIcon(RelativeSide.RIGHT, "ancientwarfare:civic/civicFarmPotatoSides");
  
  GameRegistry.registerBlock(worksiteMushroomFarm, ItemWorksitePlacer.class, "civic_mushroom_farm");
  GameRegistry.registerTileEntity(WorkSiteMushroomFarm.class, "civic_mushroom_farm_tile");
  worksiteMushroomFarm.setIcon(RelativeSide.TOP, "ancientwarfare:civic/civicMineQuarryTop");
  worksiteMushroomFarm.setIcon(RelativeSide.BOTTOM, "ancientwarfare:civic/civicFarmChickenSides");
  worksiteMushroomFarm.setIcon(RelativeSide.FRONT, "ancientwarfare:civic/civicFarmNetherSides");
  worksiteMushroomFarm.setIcon(RelativeSide.REAR, "ancientwarfare:civic/civicFarmMelonSides");
  worksiteMushroomFarm.setIcon(RelativeSide.LEFT, "ancientwarfare:civic/civicFarmMushroomRedSides");
  worksiteMushroomFarm.setIcon(RelativeSide.RIGHT, "ancientwarfare:civic/civicFarmMushroomBrownSides");
  
  GameRegistry.registerBlock(worksiteAnimalFarm, ItemWorksitePlacer.class, "civic_animal_farm");
  GameRegistry.registerTileEntity(WorkSiteAnimalFarm.class, "civic_animal_farm_tile");
  worksiteAnimalFarm.setIcon(RelativeSide.TOP, "ancientwarfare:civic/civicMineQuarryTop");
  worksiteAnimalFarm.setIcon(RelativeSide.BOTTOM, "ancientwarfare:civic/civicFarmChickenSides");
  worksiteAnimalFarm.setIcon(RelativeSide.FRONT, "ancientwarfare:civic/civicFarmNetherSides");
  worksiteAnimalFarm.setIcon(RelativeSide.REAR, "ancientwarfare:civic/civicFarmMelonSides");
  worksiteAnimalFarm.setIcon(RelativeSide.LEFT, "ancientwarfare:civic/civicFarmMushroomRedSides");
  worksiteAnimalFarm.setIcon(RelativeSide.RIGHT, "ancientwarfare:civic/civicFarmMushroomBrownSides");
  }

}
