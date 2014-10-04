package net.shadowmage.ancientwarfare.automation.block;

import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.automation.item.ItemBlockTorqueTile;
import net.shadowmage.ancientwarfare.automation.item.ItemBlockWarehouseStorage;
import net.shadowmage.ancientwarfare.automation.item.ItemBlockWorksiteStatic;
import net.shadowmage.ancientwarfare.automation.tile.TileChunkLoaderDeluxe;
import net.shadowmage.ancientwarfare.automation.tile.TileChunkLoaderSimple;
import net.shadowmage.ancientwarfare.automation.tile.TileMailbox;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileConduitHeavy;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileConduitLight;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileConduitMedium;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileDistributorHeavy;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileDistributorLight;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileDistributorMedium;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileFlywheelControlLarge;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileFlywheelControlLight;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileFlywheelControlMedium;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileHandGenerator;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileSterlingEngine;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileTorqueShaftHeavy;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileTorqueShaftLight;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileTorqueShaftMedium;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileWaterwheel;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileWindmillController;
import net.shadowmage.ancientwarfare.automation.tile.torque.multiblock.TileFlywheelStorage;
import net.shadowmage.ancientwarfare.automation.tile.torque.multiblock.TileWindmillBlade;
import net.shadowmage.ancientwarfare.automation.tile.warehouse2.TileWarehouse;
import net.shadowmage.ancientwarfare.automation.tile.warehouse2.TileWarehouseCraftingStation;
import net.shadowmage.ancientwarfare.automation.tile.warehouse2.TileWarehouseInterface;
import net.shadowmage.ancientwarfare.automation.tile.warehouse2.TileWarehouseStockViewer;
import net.shadowmage.ancientwarfare.automation.tile.warehouse2.TileWarehouseStorage;
import net.shadowmage.ancientwarfare.automation.tile.warehouse2.TileWarehouseStorageLarge;
import net.shadowmage.ancientwarfare.automation.tile.warehouse2.TileWarehouseStorageMedium;
import net.shadowmage.ancientwarfare.automation.tile.worksite.TileAutoCrafting;
import net.shadowmage.ancientwarfare.automation.tile.worksite.WorkSiteAnimalFarm;
import net.shadowmage.ancientwarfare.automation.tile.worksite.WorkSiteCropFarm;
import net.shadowmage.ancientwarfare.automation.tile.worksite.WorkSiteFishFarm;
import net.shadowmage.ancientwarfare.automation.tile.worksite.WorkSiteMushroomFarm;
import net.shadowmage.ancientwarfare.automation.tile.worksite.WorkSiteQuarry;
import net.shadowmage.ancientwarfare.automation.tile.worksite.WorkSiteReedFarm;
import net.shadowmage.ancientwarfare.automation.tile.worksite.WorkSiteTreeFarm;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.RelativeSide;
import net.shadowmage.ancientwarfare.core.item.ItemBlockMeta;
import net.shadowmage.ancientwarfare.core.item.ItemBlockOwnedRotatable;
import net.shadowmage.ancientwarfare.core.item.ItemBlockRotatableMetaTile;
import cpw.mods.fml.common.registry.GameRegistry;

public class AWAutomationBlockLoader
{

public static final BlockWorksiteBase worksiteQuarry = new BlockWorksiteBase(Material.rock, "civic_quarry")
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
  
public static final BlockAutoCrafting worksiteAutoCrafting = new BlockAutoCrafting(Material.rock, "civic_auto_crafting");
    
public static final BlockWorksiteBase worksiteWarehouse = new BlockWorksiteBase(Material.rock, "civic_warehouse")
  {    
  @Override
  public TileEntity createTileEntity(World world, int metadata)
    {   
    return new TileWarehouse();
    }
  };
  
public static final BlockWarehouseStorage warehouseStorageBlock = new BlockWarehouseStorage("warehouse_storage");

public static final BlockWarehouseInterface warehouseInterface = new BlockWarehouseInterface("warehouse_interface");

public static final BlockWarehouseCraftingStation warehouseCrafting = new BlockWarehouseCraftingStation("warehouse_crafting_station");

public static final BlockWarehouseStockViewer warehouseStockViewer = new BlockWarehouseStockViewer("warehouse_stock_viewer");

public static final BlockMailbox mailbox = new BlockMailbox("mailbox");

/**
 * POWER NETWORK BLOCKS
 */
public static final BlockFlywheel flywheel = new BlockFlywheel("flywheel_controller");
public static final BlockFlywheelStorage flywheelStorage = new BlockFlywheelStorage("flywheel_storage");
public static final BlockTorqueTransportConduit torqueConduit = new BlockTorqueTransportConduit("torque_conduit");
public static final BlockTorqueTransportShaft torqueShaft = new BlockTorqueTransportShaft("torque_shaft");
public static final BlockTorqueDistributor torqueDistributor = new BlockTorqueDistributor("torque_distributor");
public static final BlockHandCrankedEngine handCrankedEngine = new BlockHandCrankedEngine("hand_cranked_engine");
public static final BlockTorqueGenerator torqueGeneratorSterling = new BlockTorqueGeneratorSterling("torque_generator_sterling");
public static final BlockTorqueGenerator torqueGeneratorWaterwheel = new BlockTorqueGeneratorWaterwheel("torque_generator_waterwheel");
public static final BlockWindmillBlade windmillBlade = new BlockWindmillBlade("windmill_blade");
public static final BlockWindmillControll windmillControl = new BlockWindmillControll("windmill_controll");  

public static final BlockChunkLoaderSimple chunkLoaderSimple = new BlockChunkLoaderSimple("chunk_loader_simple");
public static final BlockChunkLoaderDeluxe chunkLoaderDeluxe = new BlockChunkLoaderDeluxe("chunk_loader_deluxe");

public static void load()
  {      
  GameRegistry.registerBlock(worksiteQuarry, ItemBlockWorksiteStatic.class, "civic_quarry");
  GameRegistry.registerTileEntity(WorkSiteQuarry.class, "civic_quarry_tile");
  worksiteQuarry.setIcon(RelativeSide.TOP, "ancientwarfare:automation/quarry_top");
  worksiteQuarry.setIcon(RelativeSide.BOTTOM, "ancientwarfare:automation/quarry_bottom");
  worksiteQuarry.setIcon(RelativeSide.FRONT, "ancientwarfare:automation/quarry_front");
  worksiteQuarry.setIcon(RelativeSide.REAR, "ancientwarfare:automation/quarry_rear");
  worksiteQuarry.setIcon(RelativeSide.LEFT, "ancientwarfare:automation/quarry_left");
  worksiteQuarry.setIcon(RelativeSide.RIGHT, "ancientwarfare:automation/quarry_right");  
  worksiteQuarry.setWorkSize(64);
  
  GameRegistry.registerBlock(worksiteForestry, ItemBlockWorksiteStatic.class, "civic_forestry");
  GameRegistry.registerTileEntity(WorkSiteTreeFarm.class, "civic_forestry_tile");
  worksiteForestry.setIcon(RelativeSide.TOP, "ancientwarfare:automation/tree_farm_top");
  worksiteForestry.setIcon(RelativeSide.BOTTOM, "ancientwarfare:automation/tree_farm_bottom");
  worksiteForestry.setIcon(RelativeSide.FRONT, "ancientwarfare:automation/tree_farm_front");
  worksiteForestry.setIcon(RelativeSide.REAR, "ancientwarfare:automation/tree_farm_rear");
  worksiteForestry.setIcon(RelativeSide.LEFT, "ancientwarfare:automation/tree_farm_left");
  worksiteForestry.setIcon(RelativeSide.RIGHT, "ancientwarfare:automation/tree_farm_right");  
  
  GameRegistry.registerBlock(worksiteCropFarm, ItemBlockWorksiteStatic.class, "civic_crop_farm");
  GameRegistry.registerTileEntity(WorkSiteCropFarm.class, "civic_crop_farm_tile");
  worksiteCropFarm.setIcon(RelativeSide.TOP, "ancientwarfare:automation/crop_farm_top");
  worksiteCropFarm.setIcon(RelativeSide.BOTTOM, "ancientwarfare:automation/crop_farm_bottom");
  worksiteCropFarm.setIcon(RelativeSide.FRONT, "ancientwarfare:automation/crop_farm_front");
  worksiteCropFarm.setIcon(RelativeSide.REAR, "ancientwarfare:automation/crop_farm_rear");
  worksiteCropFarm.setIcon(RelativeSide.LEFT, "ancientwarfare:automation/crop_farm_left");
  worksiteCropFarm.setIcon(RelativeSide.RIGHT, "ancientwarfare:automation/crop_farm_right");
  
  GameRegistry.registerBlock(worksiteMushroomFarm, ItemBlockWorksiteStatic.class, "civic_mushroom_farm");
  GameRegistry.registerTileEntity(WorkSiteMushroomFarm.class, "civic_mushroom_farm_tile");
  worksiteMushroomFarm.setIcon(RelativeSide.TOP, "ancientwarfare:automation/mushroom_farm_top");
  worksiteMushroomFarm.setIcon(RelativeSide.BOTTOM, "ancientwarfare:automation/mushroom_farm_bottom");
  worksiteMushroomFarm.setIcon(RelativeSide.FRONT, "ancientwarfare:automation/mushroom_farm_front");
  worksiteMushroomFarm.setIcon(RelativeSide.REAR, "ancientwarfare:automation/mushroom_farm_rear");
  worksiteMushroomFarm.setIcon(RelativeSide.LEFT, "ancientwarfare:automation/mushroom_farm_left");
  worksiteMushroomFarm.setIcon(RelativeSide.RIGHT, "ancientwarfare:automation/mushroom_farm_right");
  
  GameRegistry.registerBlock(worksiteAnimalFarm, ItemBlockWorksiteStatic.class, "civic_animal_farm");
  GameRegistry.registerTileEntity(WorkSiteAnimalFarm.class, "civic_animal_farm_tile");
  worksiteAnimalFarm.setIcon(RelativeSide.TOP, "ancientwarfare:automation/animal_farm_top");
  worksiteAnimalFarm.setIcon(RelativeSide.BOTTOM, "ancientwarfare:automation/animal_farm_bottom");
  worksiteAnimalFarm.setIcon(RelativeSide.FRONT, "ancientwarfare:automation/animal_farm_front");
  worksiteAnimalFarm.setIcon(RelativeSide.REAR, "ancientwarfare:automation/animal_farm_rear");
  worksiteAnimalFarm.setIcon(RelativeSide.LEFT, "ancientwarfare:automation/animal_farm_left");
  worksiteAnimalFarm.setIcon(RelativeSide.RIGHT, "ancientwarfare:automation/animal_farm_right");
    
  GameRegistry.registerBlock(worksiteFishFarm, ItemBlockWorksiteStatic.class, "civic_fish_farm");
  GameRegistry.registerTileEntity(WorkSiteFishFarm.class, "civic_fish_farm_tile");
  worksiteFishFarm.setIcon(RelativeSide.TOP, "ancientwarfare:automation/fish_farm_top");
  worksiteFishFarm.setIcon(RelativeSide.BOTTOM, "ancientwarfare:automation/fish_farm_bottom");
  worksiteFishFarm.setIcon(RelativeSide.FRONT, "ancientwarfare:automation/fish_farm_front");
  worksiteFishFarm.setIcon(RelativeSide.REAR, "ancientwarfare:automation/fish_farm_rear");
  worksiteFishFarm.setIcon(RelativeSide.LEFT, "ancientwarfare:automation/fish_farm_left");
  worksiteFishFarm.setIcon(RelativeSide.RIGHT, "ancientwarfare:automation/fish_farm_right");
  
  GameRegistry.registerBlock(worksiteReedFarm, ItemBlockWorksiteStatic.class, "civic_reed_farm");
  GameRegistry.registerTileEntity(WorkSiteReedFarm.class, "civic_reed_farm_tile");
  worksiteReedFarm.setIcon(RelativeSide.TOP, "ancientwarfare:automation/reed_farm_top");
  worksiteReedFarm.setIcon(RelativeSide.BOTTOM, "ancientwarfare:automation/reed_farm_bottom");
  worksiteReedFarm.setIcon(RelativeSide.FRONT, "ancientwarfare:automation/reed_farm_front");
  worksiteReedFarm.setIcon(RelativeSide.REAR, "ancientwarfare:automation/reed_farm_rear");
  worksiteReedFarm.setIcon(RelativeSide.LEFT, "ancientwarfare:automation/reed_farm_left");
  worksiteReedFarm.setIcon(RelativeSide.RIGHT, "ancientwarfare:automation/reed_farm_right");
  
  GameRegistry.registerBlock(worksiteWarehouse, ItemBlockWorksiteStatic.class, "civic_warehouse");
  GameRegistry.registerTileEntity(TileWarehouse.class, "civic_warehouse_tile");
  worksiteWarehouse.setIcon(RelativeSide.TOP, "ancientwarfare:automation/warehouse_control_top");
  worksiteWarehouse.setIcon(RelativeSide.BOTTOM, "ancientwarfare:automation/warehouse_control_bottom");
  worksiteWarehouse.setIcon(RelativeSide.FRONT, "ancientwarfare:automation/warehouse_control_front");
  worksiteWarehouse.setIcon(RelativeSide.REAR, "ancientwarfare:automation/warehouse_control_rear");
  worksiteWarehouse.setIcon(RelativeSide.LEFT, "ancientwarfare:automation/warehouse_control_left");
  worksiteWarehouse.setIcon(RelativeSide.RIGHT, "ancientwarfare:automation/warehouse_control_right");
  worksiteWarehouse.setWorkVerticalSize(4);
  
  GameRegistry.registerBlock(warehouseStorageBlock, ItemBlockWarehouseStorage.class, "warehouse_storage");
  GameRegistry.registerTileEntity(TileWarehouseStorage.class, "warehouse_storage_small_tile");
  warehouseStorageBlock.setIcon(0, 0, "ancientwarfare:automation/warehouse_storage_small_bottom");
  warehouseStorageBlock.setIcon(0, 1, "ancientwarfare:automation/warehouse_storage_small_top");
  warehouseStorageBlock.setIcon(0, 2, "ancientwarfare:automation/warehouse_storage_small_side");
  warehouseStorageBlock.setIcon(0, 3, "ancientwarfare:automation/warehouse_storage_small_side");
  warehouseStorageBlock.setIcon(0, 4, "ancientwarfare:automation/warehouse_storage_small_side");
  warehouseStorageBlock.setIcon(0, 5, "ancientwarfare:automation/warehouse_storage_small_side");
  GameRegistry.registerTileEntity(TileWarehouseStorageMedium.class, "warehouse_storage_medium_tile");
  warehouseStorageBlock.setIcon(1, 0, "ancientwarfare:automation/warehouse_storage_medium_bottom");
  warehouseStorageBlock.setIcon(1, 1, "ancientwarfare:automation/warehouse_storage_medium_top");
  warehouseStorageBlock.setIcon(1, 2, "ancientwarfare:automation/warehouse_storage_medium_side");
  warehouseStorageBlock.setIcon(1, 3, "ancientwarfare:automation/warehouse_storage_medium_side");
  warehouseStorageBlock.setIcon(1, 4, "ancientwarfare:automation/warehouse_storage_medium_side");
  warehouseStorageBlock.setIcon(1, 5, "ancientwarfare:automation/warehouse_storage_medium_side");
  GameRegistry.registerTileEntity(TileWarehouseStorageLarge.class, "warehouse_storage_large_tile");
  warehouseStorageBlock.setIcon(2, 0, "ancientwarfare:automation/warehouse_storage_large_bottom");
  warehouseStorageBlock.setIcon(2, 1, "ancientwarfare:automation/warehouse_storage_large_top");
  warehouseStorageBlock.setIcon(2, 2, "ancientwarfare:automation/warehouse_storage_large_side");
  warehouseStorageBlock.setIcon(2, 3, "ancientwarfare:automation/warehouse_storage_large_side");
  warehouseStorageBlock.setIcon(2, 4, "ancientwarfare:automation/warehouse_storage_large_side");
  warehouseStorageBlock.setIcon(2, 5, "ancientwarfare:automation/warehouse_storage_large_side");
    
  GameRegistry.registerBlock(warehouseInterface, "warehouse_interface");
  GameRegistry.registerTileEntity(TileWarehouseInterface.class, "warehouse_interface_tile");
  warehouseInterface.setIcon(0, 0, "ancientwarfare:automation/warehouse_interface_bottom");
  warehouseInterface.setIcon(0, 1, "ancientwarfare:automation/warehouse_interface_top");
  warehouseInterface.setIcon(0, 2, "ancientwarfare:automation/warehouse_interface_side");
  warehouseInterface.setIcon(0, 3, "ancientwarfare:automation/warehouse_interface_side");
  warehouseInterface.setIcon(0, 4, "ancientwarfare:automation/warehouse_interface_side");
  warehouseInterface.setIcon(0, 5, "ancientwarfare:automation/warehouse_interface_side");
  
  GameRegistry.registerBlock(warehouseCrafting, "warehouse_crafting_station");
  GameRegistry.registerTileEntity(TileWarehouseCraftingStation.class, "warehouse_crafting_station_tile");
  warehouseCrafting.setIcon(0, 0, "ancientwarfare:automation/warehouse_crafting_bottom");
  warehouseCrafting.setIcon(0, 1, "ancientwarfare:automation/warehouse_crafting_top");
  warehouseCrafting.setIcon(0, 2, "ancientwarfare:automation/warehouse_crafting_front");  
  warehouseCrafting.setIcon(0, 3, "ancientwarfare:automation/warehouse_crafting_front");
  warehouseCrafting.setIcon(0, 4, "ancientwarfare:automation/warehouse_crafting_side");
  warehouseCrafting.setIcon(0, 5, "ancientwarfare:automation/warehouse_crafting_side");  
  
  GameRegistry.registerBlock(warehouseStockViewer, ItemBlockOwnedRotatable.class, "warehouse_stock_viewer");
  GameRegistry.registerTileEntity(TileWarehouseStockViewer.class, "warehouse_stock_viewer_tile");
  
  GameRegistry.registerBlock(worksiteAutoCrafting, ItemBlockRotatableMetaTile.class, "civic_auto_crafting");
  GameRegistry.registerTileEntity(TileAutoCrafting.class, "civic_auto_crafting_tile");
  
  GameRegistry.registerBlock(mailbox, ItemBlockOwnedRotatable.class, "mailbox");
  GameRegistry.registerTileEntity(TileMailbox.class, "mailbox_tile");
  mailbox.setIcon(RelativeSide.TOP, "ancientwarfare:automation/mailbox_top");
  mailbox.setIcon(RelativeSide.FRONT, "ancientwarfare:automation/mailbox_front");
  mailbox.setIcon(RelativeSide.REAR, "ancientwarfare:automation/mailbox_rear");  
  mailbox.setIcon(RelativeSide.BOTTOM, "ancientwarfare:automation/mailbox_bottom");
  mailbox.setIcon(RelativeSide.LEFT, "ancientwarfare:automation/mailbox_left");
  mailbox.setIcon(RelativeSide.RIGHT, "ancientwarfare:automation/mailbox_right");  

  GameRegistry.registerBlock(flywheel, ItemBlockTorqueTile.class, "flywheel");
  GameRegistry.registerTileEntity(TileFlywheelControlLight.class, "flywheel_tile");  
  GameRegistry.registerTileEntity(TileFlywheelControlMedium.class, "flywheel_medium_tile");  
  GameRegistry.registerTileEntity(TileFlywheelControlLarge.class, "flywheel_large_tile");
  
  GameRegistry.registerBlock(flywheelStorage, ItemBlockMeta.class, "flywheel_storage");
  GameRegistry.registerTileEntity(TileFlywheelStorage.class, "flywheel_storage_tile");
  
  GameRegistry.registerBlock(torqueConduit, ItemBlockTorqueTile.class, "torque_conduit");
  GameRegistry.registerTileEntity(TileConduitLight.class, "torque_conduit_tile");  
  GameRegistry.registerTileEntity(TileConduitMedium.class, "torque_conduit_medium_tile");  
  GameRegistry.registerTileEntity(TileConduitHeavy.class, "torque_conduit_heavy_tile");

  GameRegistry.registerBlock(torqueShaft, ItemBlockTorqueTile.class, "torque_shaft");
  GameRegistry.registerTileEntity(TileTorqueShaftLight.class, "torque_driveline_tile");
  GameRegistry.registerTileEntity(TileTorqueShaftMedium.class, "torque_driveline_medium_tile");
  GameRegistry.registerTileEntity(TileTorqueShaftHeavy.class, "torque_driveline_heavy_tile");
  
  GameRegistry.registerBlock(torqueDistributor, ItemBlockTorqueTile.class, "torque_distributor");
  GameRegistry.registerTileEntity(TileDistributorLight.class, "torque_distributor_tile");
  GameRegistry.registerTileEntity(TileDistributorMedium.class, "torque_distributor_medium_tile");
  GameRegistry.registerTileEntity(TileDistributorHeavy.class, "torque_distributor_heavy_tile");
    
  GameRegistry.registerBlock(torqueGeneratorSterling, ItemBlockTorqueTile.class, "torque_generator_sterling");
  GameRegistry.registerTileEntity(TileSterlingEngine.class, "torque_generator_sterling_tile");
  
  GameRegistry.registerBlock(torqueGeneratorWaterwheel, ItemBlockTorqueTile.class, "torque_generator_waterwheel");
  GameRegistry.registerTileEntity(TileWaterwheel.class, "torque_generator_waterwheel_tile");
  
  GameRegistry.registerBlock(handCrankedEngine, ItemBlockTorqueTile.class, "hand_cranked_engine");
  GameRegistry.registerTileEntity(TileHandGenerator.class, "hand_cranked_engine_tile");
  
  GameRegistry.registerBlock(windmillBlade, "windmill_blade");
  GameRegistry.registerTileEntity(TileWindmillBlade.class, "windmill_blade_tile");
  
  GameRegistry.registerBlock(windmillControl, "windmill_control");
  GameRegistry.registerTileEntity(TileWindmillController.class, "windmill_control_tile");
  
  GameRegistry.registerBlock(chunkLoaderSimple, "chunk_loader_simple");
  GameRegistry.registerTileEntity(TileChunkLoaderSimple.class, "chunk_loader_simple_tile");
  chunkLoaderSimple.iconMap.setIconTexture(0, 0, "ancientwarfare:automation/chunk_loader_simple_bottom");
  chunkLoaderSimple.iconMap.setIconTexture(1, 0, "ancientwarfare:automation/chunk_loader_simple_bottom");
  chunkLoaderSimple.iconMap.setIconTexture(2, 0, "ancientwarfare:automation/chunk_loader_simple_side");
  chunkLoaderSimple.iconMap.setIconTexture(3, 0, "ancientwarfare:automation/chunk_loader_simple_side");
  chunkLoaderSimple.iconMap.setIconTexture(4, 0, "ancientwarfare:automation/chunk_loader_simple_side");
  chunkLoaderSimple.iconMap.setIconTexture(5, 0, "ancientwarfare:automation/chunk_loader_simple_side");
  
  GameRegistry.registerBlock(chunkLoaderDeluxe, "chunk_loader_deluxe");
  GameRegistry.registerTileEntity(TileChunkLoaderDeluxe.class, "chunk_loader_deluxe_tile");
  chunkLoaderDeluxe.iconMap.setIconTexture(0, 0, "ancientwarfare:automation/chunk_loader_deluxe_bottom");
  chunkLoaderDeluxe.iconMap.setIconTexture(1, 0, "ancientwarfare:automation/chunk_loader_deluxe_bottom");
  chunkLoaderDeluxe.iconMap.setIconTexture(2, 0, "ancientwarfare:automation/chunk_loader_deluxe_side");
  chunkLoaderDeluxe.iconMap.setIconTexture(3, 0, "ancientwarfare:automation/chunk_loader_deluxe_side");
  chunkLoaderDeluxe.iconMap.setIconTexture(4, 0, "ancientwarfare:automation/chunk_loader_deluxe_side");
  chunkLoaderDeluxe.iconMap.setIconTexture(5, 0, "ancientwarfare:automation/chunk_loader_deluxe_side");
  }

}
