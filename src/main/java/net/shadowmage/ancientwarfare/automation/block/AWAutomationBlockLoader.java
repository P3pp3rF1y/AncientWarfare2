package net.shadowmage.ancientwarfare.automation.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.automation.item.ItemBlockWarehouseStorage;
import net.shadowmage.ancientwarfare.automation.item.ItemBlockWorksite;
import net.shadowmage.ancientwarfare.automation.tile.TileMailbox;
import net.shadowmage.ancientwarfare.automation.tile.TileMechanicalWorker;
import net.shadowmage.ancientwarfare.automation.tile.TileWarehouseCraftingStation;
import net.shadowmage.ancientwarfare.automation.tile.TileWarehouseInput;
import net.shadowmage.ancientwarfare.automation.tile.TileWarehouseOutput;
import net.shadowmage.ancientwarfare.automation.tile.TileWarehouseStorageSmall;
import net.shadowmage.ancientwarfare.automation.tile.WorkSiteAnimalFarm;
import net.shadowmage.ancientwarfare.automation.tile.WorkSiteCropFarm;
import net.shadowmage.ancientwarfare.automation.tile.WorkSiteFishFarm;
import net.shadowmage.ancientwarfare.automation.tile.WorkSiteMushroomFarm;
import net.shadowmage.ancientwarfare.automation.tile.WorkSiteQuarry;
import net.shadowmage.ancientwarfare.automation.tile.WorkSiteReedFarm;
import net.shadowmage.ancientwarfare.automation.tile.WorkSiteTreeFarm;
import net.shadowmage.ancientwarfare.automation.tile.WorkSiteWarehouse;
import net.shadowmage.ancientwarfare.automation.tile.WorksiteAutoCrafting;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.RelativeSide;
import net.shadowmage.ancientwarfare.core.item.ItemBlockOwnedRotatable;
import cpw.mods.fml.common.registry.GameRegistry;

public class AWAutomationBlockLoader
{

public static final Block workerTest = new BlockMechanicalWorker("mechanical_worker");

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
  
public static final BlockWorksiteBase worksiteAutoCrafting = new BlockWorksiteBase(Material.rock, "civic_auto_crafting")
  {
  @Override
  public TileEntity createTileEntity(World world, int metadata)
    {
    return new WorksiteAutoCrafting();
    }
  };
  
public static final BlockWorksiteBase worksiteFishFarm = new BlockWorksiteBase(Material.rock, "civic_fish_farm")
  {
  @Override
  public TileEntity createTileEntity(World world, int metadata)
    {
    return new WorkSiteFishFarm();
    }  
  };
  
public static final BlockWorksiteBase worksiteReedFarm = new BlockWorksiteBase(Material.rock, "civic_reed_farm")
  {
  @Override
  public TileEntity createTileEntity(World world, int metadata)
    {
    return new WorkSiteReedFarm();
    }
  };
  
public static final BlockWorksiteBase worksiteWarehouse = new BlockWorksiteBase(Material.rock, "civic_warehouse")
  {    
  @Override
  public TileEntity createTileEntity(World world, int metadata)
    {   
    return new WorkSiteWarehouse();
    }
  };
  
public static final BlockMailbox mailbox = new BlockMailbox("mailbox");

public static final BlockWarehouseStorage warehouseStorageBlock = new BlockWarehouseStorage("warehouse_storage");

public static final BlockWarehouseInput warehouseInput = new BlockWarehouseInput("warehouse_input");

public static final BlockWarehouseOutput warehouseOutput = new BlockWarehouseOutput("warehouse_output");

public static final BlockWarehouseCraftingStation warehouseCrafting = new BlockWarehouseCraftingStation("warehouse_crafting_station");

public static void load()
  {  
  GameRegistry.registerBlock(workerTest, ItemBlockOwnedRotatable.class, "mechanical_worker");
  GameRegistry.registerTileEntity(TileMechanicalWorker.class, "mechanical_worker_tile");
    
  GameRegistry.registerBlock(worksiteQuarry, ItemBlockWorksite.class, "civic_quarry");
  GameRegistry.registerTileEntity(WorkSiteQuarry.class, "civic_quarry_tile");
  worksiteQuarry.setIcon(RelativeSide.TOP, "ancientwarfare:civic/civicMineQuarryTop");
  worksiteQuarry.setIcon(RelativeSide.BOTTOM, "ancientwarfare:civic/civicFarmChickenSides");
  worksiteQuarry.setIcon(RelativeSide.FRONT, "ancientwarfare:civic/civicMineQuarrySides");
  worksiteQuarry.setIcon(RelativeSide.REAR, "ancientwarfare:civic/civicFarmCocoaSides");
  worksiteQuarry.setIcon(RelativeSide.LEFT, "ancientwarfare:civic/civicFarmNetherSides");
  worksiteQuarry.setIcon(RelativeSide.RIGHT, "ancientwarfare:civic/civicFarmOakSides");  
  worksiteQuarry.setWorkSize(64);
  
  GameRegistry.registerBlock(worksiteForestry, ItemBlockWorksite.class, "civic_forestry");
  GameRegistry.registerTileEntity(WorkSiteTreeFarm.class, "civic_forestry_tile");
  worksiteForestry.setIcon(RelativeSide.TOP, "ancientwarfare:civic/civicMineQuarryTop");
  worksiteForestry.setIcon(RelativeSide.BOTTOM, "ancientwarfare:civic/civicFarmChickenSides");
  worksiteForestry.setIcon(RelativeSide.FRONT, "ancientwarfare:civic/civicFarmOakSides");
  worksiteForestry.setIcon(RelativeSide.REAR, "ancientwarfare:civic/civicFarmJungleSides");
  worksiteForestry.setIcon(RelativeSide.LEFT, "ancientwarfare:civic/civicFarmSpruceSides");
  worksiteForestry.setIcon(RelativeSide.RIGHT, "ancientwarfare:civic/civicFarmBirchSides");  
  
  GameRegistry.registerBlock(worksiteCropFarm, ItemBlockWorksite.class, "civic_crop_farm");
  GameRegistry.registerTileEntity(WorkSiteCropFarm.class, "civic_crop_farm_tile");
  worksiteCropFarm.setIcon(RelativeSide.TOP, "ancientwarfare:civic/civicMineQuarryTop");
  worksiteCropFarm.setIcon(RelativeSide.BOTTOM, "ancientwarfare:civic/civicFarmChickenSides");
  worksiteCropFarm.setIcon(RelativeSide.FRONT, "ancientwarfare:civic/civicFarmWheatSides");
  worksiteCropFarm.setIcon(RelativeSide.REAR, "ancientwarfare:civic/civicFarmMelonSides");
  worksiteCropFarm.setIcon(RelativeSide.LEFT, "ancientwarfare:civic/civicFarmCarrotSides");
  worksiteCropFarm.setIcon(RelativeSide.RIGHT, "ancientwarfare:civic/civicFarmPotatoSides");
  
  GameRegistry.registerBlock(worksiteMushroomFarm, ItemBlockWorksite.class, "civic_mushroom_farm");
  GameRegistry.registerTileEntity(WorkSiteMushroomFarm.class, "civic_mushroom_farm_tile");
  worksiteMushroomFarm.setIcon(RelativeSide.TOP, "ancientwarfare:civic/civicMineQuarryTop");
  worksiteMushroomFarm.setIcon(RelativeSide.BOTTOM, "ancientwarfare:civic/civicFarmChickenSides");
  worksiteMushroomFarm.setIcon(RelativeSide.FRONT, "ancientwarfare:civic/civicFarmNetherSides");
  worksiteMushroomFarm.setIcon(RelativeSide.REAR, "ancientwarfare:civic/civicFarmMelonSides");
  worksiteMushroomFarm.setIcon(RelativeSide.LEFT, "ancientwarfare:civic/civicFarmMushroomRedSides");
  worksiteMushroomFarm.setIcon(RelativeSide.RIGHT, "ancientwarfare:civic/civicFarmMushroomBrownSides");
  
  GameRegistry.registerBlock(worksiteAnimalFarm, ItemBlockWorksite.class, "civic_animal_farm");
  GameRegistry.registerTileEntity(WorkSiteAnimalFarm.class, "civic_animal_farm_tile");
  worksiteAnimalFarm.setIcon(RelativeSide.TOP, "ancientwarfare:civic/civicMineQuarryTop");
  worksiteAnimalFarm.setIcon(RelativeSide.BOTTOM, "ancientwarfare:civic/civicFarmChickenSides");
  worksiteAnimalFarm.setIcon(RelativeSide.FRONT, "ancientwarfare:civic/civicFarmNetherSides");
  worksiteAnimalFarm.setIcon(RelativeSide.REAR, "ancientwarfare:civic/civicFarmMelonSides");
  worksiteAnimalFarm.setIcon(RelativeSide.LEFT, "ancientwarfare:civic/civicFarmMushroomRedSides");
  worksiteAnimalFarm.setIcon(RelativeSide.RIGHT, "ancientwarfare:civic/civicFarmMushroomBrownSides");
  
  GameRegistry.registerBlock(worksiteAutoCrafting, ItemBlockOwnedRotatable.class, "civic_auto_craftin");
  GameRegistry.registerTileEntity(WorksiteAutoCrafting.class, "civic_auto_crafting_tile");
  worksiteAutoCrafting.setIcon(RelativeSide.TOP, "ancientwarfare:civic/civicMineQuarryTop");
  
  GameRegistry.registerBlock(worksiteFishFarm, ItemBlockWorksite.class, "civic_fish_farm");
  GameRegistry.registerTileEntity(WorkSiteFishFarm.class, "civic_fish_farm_tile");
  
  GameRegistry.registerBlock(worksiteReedFarm, ItemBlockWorksite.class, "civic_reed_farm");
  GameRegistry.registerTileEntity(WorkSiteReedFarm.class, "civic_reed_farm_tile");
  
  GameRegistry.registerBlock(mailbox, ItemBlockOwnedRotatable.class, "mailbox");
  GameRegistry.registerTileEntity(TileMailbox.class, "mailbox_tile");
  mailbox.setIcon(RelativeSide.TOP, "ancientwarfare:civic/civicMineQuarryTop");
  mailbox.setIcon(RelativeSide.FRONT, "ancientwarfare:civic/civicFarmMelonSides");
  mailbox.setIcon(RelativeSide.REAR, "ancientwarfare:civic/civicFarmNetherSides");  
  
  GameRegistry.registerBlock(worksiteWarehouse, ItemBlockWorksite.class, "civic_warehouse");
  GameRegistry.registerTileEntity(WorkSiteWarehouse.class, "civic_warehouse_tile");
  worksiteWarehouse.setWorkVerticalSize(4);
  
  GameRegistry.registerBlock(warehouseStorageBlock, ItemBlockWarehouseStorage.class, "warehouse_storage");
  GameRegistry.registerTileEntity(TileWarehouseStorageSmall.class, "warehouse_storage_small_tile");
  warehouseStorageBlock.setIcon(0, 0, "ancientwarfare:automation/warehouse_storage_small_bottom");
  warehouseStorageBlock.setIcon(0, 1, "ancientwarfare:automation/warehouse_storage_small_top");
  warehouseStorageBlock.setIcon(0, 2, "ancientwarfare:automation/warehouse_storage_small_side");
  warehouseStorageBlock.setIcon(0, 3, "ancientwarfare:automation/warehouse_storage_small_side");
  warehouseStorageBlock.setIcon(0, 4, "ancientwarfare:automation/warehouse_storage_small_side");
  warehouseStorageBlock.setIcon(0, 5, "ancientwarfare:automation/warehouse_storage_small_side");
  
  GameRegistry.registerBlock(warehouseInput, "warehouse_input");
  GameRegistry.registerTileEntity(TileWarehouseInput.class, "warehouse_input_tile");
  
  GameRegistry.registerBlock(warehouseOutput, "warehouse_output");
  GameRegistry.registerTileEntity(TileWarehouseOutput.class, "warehouse_output_tile");
  
  GameRegistry.registerBlock(warehouseCrafting, "warehouse_crafting_station");
  GameRegistry.registerTileEntity(TileWarehouseCraftingStation.class, "warehouse_crafting_station_tile");
  }

}
