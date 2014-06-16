package net.shadowmage.ancientwarfare.automation.block;

import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.automation.item.ItemBlockTorqueTile;
import net.shadowmage.ancientwarfare.automation.item.ItemBlockWarehouseStorage;
import net.shadowmage.ancientwarfare.automation.item.ItemBlockWorksite;
import net.shadowmage.ancientwarfare.automation.render.RenderSterlingEngine;
import net.shadowmage.ancientwarfare.automation.tile.TileChunkLoaderDeluxe;
import net.shadowmage.ancientwarfare.automation.tile.TileChunkLoaderSimple;
import net.shadowmage.ancientwarfare.automation.tile.TileMailbox;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileTorqueGeneratorHandCranked;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileTorqueGeneratorSterling;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileTorqueGeneratorWaterwheel;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileTorqueStorageFlywheel;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileTorqueStorageFlywheelLarge;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileTorqueStorageFlywheelMedium;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileTorqueTransportConduit;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileTorqueTransportConduitHeavy;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileTorqueTransportConduitMedium;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileTorqueTransportDistributor;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileTorqueTransportDistributorHeavy;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileTorqueTransportDistributorMedium;
import net.shadowmage.ancientwarfare.automation.tile.warehouse2.TileWarehouse;
import net.shadowmage.ancientwarfare.automation.tile.warehouse2.TileWarehouseCraftingStation;
import net.shadowmage.ancientwarfare.automation.tile.warehouse2.TileWarehouseInterface;
import net.shadowmage.ancientwarfare.automation.tile.warehouse2.TileWarehouseStockViewer;
import net.shadowmage.ancientwarfare.automation.tile.warehouse2.TileWarehouseStorage;
import net.shadowmage.ancientwarfare.automation.tile.warehouse2.TileWarehouseStorageLarge;
import net.shadowmage.ancientwarfare.automation.tile.warehouse2.TileWarehouseStorageMedium;
import net.shadowmage.ancientwarfare.automation.tile.worksite.WorkSiteAnimalFarm;
import net.shadowmage.ancientwarfare.automation.tile.worksite.WorkSiteCropFarm;
import net.shadowmage.ancientwarfare.automation.tile.worksite.WorkSiteFishFarm;
import net.shadowmage.ancientwarfare.automation.tile.worksite.WorkSiteMushroomFarm;
import net.shadowmage.ancientwarfare.automation.tile.worksite.WorkSiteQuarry;
import net.shadowmage.ancientwarfare.automation.tile.worksite.WorkSiteReedFarm;
import net.shadowmage.ancientwarfare.automation.tile.worksite.WorkSiteTreeFarm;
import net.shadowmage.ancientwarfare.automation.tile.worksite.WorksiteAutoCrafting;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.RelativeSide;
import net.shadowmage.ancientwarfare.core.item.ItemBlockOwnedRotatable;
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
  
public static final BlockWorksiteBase worksiteAutoCrafting = new BlockWorksiteBase(Material.rock, "civic_auto_crafting")
  {
  @Override
  public TileEntity createTileEntity(World world, int metadata)
    {
    return new WorksiteAutoCrafting();
    }
  };
    
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
public static final BlockFlywheel flywheel = new BlockFlywheel("flywheel");
public static final BlockTorqueConduit torqueConduit = new BlockTorqueConduit("torque_conduit");
public static final BlockTorqueDistributor torqueDistributor = new BlockTorqueDistributor("torque_distributor");
public static final BlockHandCrankedEngine handCrankedEngine = new BlockHandCrankedEngine("hand_cranked_engine");
public static final BlockTorqueGenerator torqueGeneratorSterling = new BlockTorqueGenerator("torque_generator_sterling")
  { 
  @Override
  public TileEntity createTileEntity(World world, int metadata)
    {
    return new TileTorqueGeneratorSterling();
    }
  public boolean shouldSideBeRendered(net.minecraft.world.IBlockAccess p_149646_1_, int p_149646_2_, int p_149646_3_, int p_149646_4_, int p_149646_5_) {return false;};
  public boolean isOpaqueCube() {return false;};
  public boolean isNormalCube() {return false;};
  public int getRenderType() {return RenderSterlingEngine.renderID;};
  };
public static final BlockTorqueGenerator torqueGeneratorWaterwheel = new BlockTorqueGenerator("torque_generator_waterwheel")
  { 
  @Override
  public TileEntity createTileEntity(World world, int metadata)
    {
    return new TileTorqueGeneratorWaterwheel();
    }
  @Override
  public boolean invertFacing()
    {
    return true;
    }
  };
  
public static final BlockChunkLoaderSimple chunkLoaderSimple = new BlockChunkLoaderSimple("chunk_loader_simple");
public static final BlockChunkLoaderDeluxe chunkLoaderDeluxe = new BlockChunkLoaderDeluxe("chunk_loader_deluxe");

public static void load()
  {      
  GameRegistry.registerBlock(worksiteQuarry, ItemBlockWorksite.class, "civic_quarry");
  GameRegistry.registerTileEntity(WorkSiteQuarry.class, "civic_quarry_tile");
  worksiteQuarry.setIcon(RelativeSide.TOP, "ancientwarfare:automation/quarry_top");
  worksiteQuarry.setIcon(RelativeSide.BOTTOM, "ancientwarfare:automation/quarry_bottom");
  worksiteQuarry.setIcon(RelativeSide.FRONT, "ancientwarfare:automation/quarry_front");
  worksiteQuarry.setIcon(RelativeSide.REAR, "ancientwarfare:automation/quarry_rear");
  worksiteQuarry.setIcon(RelativeSide.LEFT, "ancientwarfare:automation/quarry_left");
  worksiteQuarry.setIcon(RelativeSide.RIGHT, "ancientwarfare:automation/quarry_right");  
  worksiteQuarry.setWorkSize(64);
  
  GameRegistry.registerBlock(worksiteForestry, ItemBlockWorksite.class, "civic_forestry");
  GameRegistry.registerTileEntity(WorkSiteTreeFarm.class, "civic_forestry_tile");
  worksiteForestry.setIcon(RelativeSide.TOP, "ancientwarfare:automation/tree_farm_top");
  worksiteForestry.setIcon(RelativeSide.BOTTOM, "ancientwarfare:automation/tree_farm_bottom");
  worksiteForestry.setIcon(RelativeSide.FRONT, "ancientwarfare:automation/tree_farm_front");
  worksiteForestry.setIcon(RelativeSide.REAR, "ancientwarfare:automation/tree_farm_rear");
  worksiteForestry.setIcon(RelativeSide.LEFT, "ancientwarfare:automation/tree_farm_left");
  worksiteForestry.setIcon(RelativeSide.RIGHT, "ancientwarfare:automation/tree_farm_right");  
  
  GameRegistry.registerBlock(worksiteCropFarm, ItemBlockWorksite.class, "civic_crop_farm");
  GameRegistry.registerTileEntity(WorkSiteCropFarm.class, "civic_crop_farm_tile");
  worksiteCropFarm.setIcon(RelativeSide.TOP, "ancientwarfare:automation/crop_farm_top");
  worksiteCropFarm.setIcon(RelativeSide.BOTTOM, "ancientwarfare:automation/crop_farm_bottom");
  worksiteCropFarm.setIcon(RelativeSide.FRONT, "ancientwarfare:automation/crop_farm_front");
  worksiteCropFarm.setIcon(RelativeSide.REAR, "ancientwarfare:automation/crop_farm_rear");
  worksiteCropFarm.setIcon(RelativeSide.LEFT, "ancientwarfare:automation/crop_farm_left");
  worksiteCropFarm.setIcon(RelativeSide.RIGHT, "ancientwarfare:automation/crop_farm_right");
  
  GameRegistry.registerBlock(worksiteMushroomFarm, ItemBlockWorksite.class, "civic_mushroom_farm");
  GameRegistry.registerTileEntity(WorkSiteMushroomFarm.class, "civic_mushroom_farm_tile");
  worksiteMushroomFarm.setIcon(RelativeSide.TOP, "ancientwarfare:automation/mushroom_farm_top");
  worksiteMushroomFarm.setIcon(RelativeSide.BOTTOM, "ancientwarfare:automation/mushroom_farm_bottom");
  worksiteMushroomFarm.setIcon(RelativeSide.FRONT, "ancientwarfare:automation/mushroom_farm_front");
  worksiteMushroomFarm.setIcon(RelativeSide.REAR, "ancientwarfare:automation/mushroom_farm_rear");
  worksiteMushroomFarm.setIcon(RelativeSide.LEFT, "ancientwarfare:automation/mushroom_farm_left");
  worksiteMushroomFarm.setIcon(RelativeSide.RIGHT, "ancientwarfare:automation/mushroom_farm_right");
  
  GameRegistry.registerBlock(worksiteAnimalFarm, ItemBlockWorksite.class, "civic_animal_farm");
  GameRegistry.registerTileEntity(WorkSiteAnimalFarm.class, "civic_animal_farm_tile");
  worksiteAnimalFarm.setIcon(RelativeSide.TOP, "ancientwarfare:automation/animal_farm_top");
  worksiteAnimalFarm.setIcon(RelativeSide.BOTTOM, "ancientwarfare:automation/animal_farm_bottom");
  worksiteAnimalFarm.setIcon(RelativeSide.FRONT, "ancientwarfare:automation/animal_farm_front");
  worksiteAnimalFarm.setIcon(RelativeSide.REAR, "ancientwarfare:automation/animal_farm_rear");
  worksiteAnimalFarm.setIcon(RelativeSide.LEFT, "ancientwarfare:automation/animal_farm_left");
  worksiteAnimalFarm.setIcon(RelativeSide.RIGHT, "ancientwarfare:automation/animal_farm_right");
    
  GameRegistry.registerBlock(worksiteFishFarm, ItemBlockWorksite.class, "civic_fish_farm");
  GameRegistry.registerTileEntity(WorkSiteFishFarm.class, "civic_fish_farm_tile");
  worksiteFishFarm.setIcon(RelativeSide.TOP, "ancientwarfare:automation/fish_farm_top");
  worksiteFishFarm.setIcon(RelativeSide.BOTTOM, "ancientwarfare:automation/fish_farm_bottom");
  worksiteFishFarm.setIcon(RelativeSide.FRONT, "ancientwarfare:automation/fish_farm_front");
  worksiteFishFarm.setIcon(RelativeSide.REAR, "ancientwarfare:automation/fish_farm_rear");
  worksiteFishFarm.setIcon(RelativeSide.LEFT, "ancientwarfare:automation/fish_farm_left");
  worksiteFishFarm.setIcon(RelativeSide.RIGHT, "ancientwarfare:automation/fish_farm_right");
  
  GameRegistry.registerBlock(worksiteReedFarm, ItemBlockWorksite.class, "civic_reed_farm");
  GameRegistry.registerTileEntity(WorkSiteReedFarm.class, "civic_reed_farm_tile");
  worksiteReedFarm.setIcon(RelativeSide.TOP, "ancientwarfare:automation/reed_farm_top");
  worksiteReedFarm.setIcon(RelativeSide.BOTTOM, "ancientwarfare:automation/reed_farm_bottom");
  worksiteReedFarm.setIcon(RelativeSide.FRONT, "ancientwarfare:automation/reed_farm_front");
  worksiteReedFarm.setIcon(RelativeSide.REAR, "ancientwarfare:automation/reed_farm_rear");
  worksiteReedFarm.setIcon(RelativeSide.LEFT, "ancientwarfare:automation/reed_farm_left");
  worksiteReedFarm.setIcon(RelativeSide.RIGHT, "ancientwarfare:automation/reed_farm_right");
  
  GameRegistry.registerBlock(worksiteWarehouse, ItemBlockWorksite.class, "civic_warehouse");
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
  
  GameRegistry.registerBlock(worksiteAutoCrafting, ItemBlockOwnedRotatable.class, "civic_auto_crafting");
  GameRegistry.registerTileEntity(WorksiteAutoCrafting.class, "civic_auto_crafting_tile");
  worksiteAutoCrafting.setIcon(RelativeSide.TOP, "ancientwarfare:automation/auto_crafting_top");
  worksiteAutoCrafting.setIcon(RelativeSide.FRONT, "ancientwarfare:automation/auto_crafting_front");
  worksiteAutoCrafting.setIcon(RelativeSide.REAR, "ancientwarfare:automation/auto_crafting_side");  
  worksiteAutoCrafting.setIcon(RelativeSide.BOTTOM, "ancientwarfare:automation/auto_crafting_side");
  worksiteAutoCrafting.setIcon(RelativeSide.LEFT, "ancientwarfare:automation/auto_crafting_side");
  worksiteAutoCrafting.setIcon(RelativeSide.RIGHT, "ancientwarfare:automation/auto_crafting_side");  
  
  GameRegistry.registerBlock(mailbox, ItemBlockOwnedRotatable.class, "mailbox");
  GameRegistry.registerTileEntity(TileMailbox.class, "mailbox_tile");
  mailbox.setIcon(RelativeSide.TOP, "ancientwarfare:automation/mailbox_top");
  mailbox.setIcon(RelativeSide.FRONT, "ancientwarfare:automation/mailbox_front");
  mailbox.setIcon(RelativeSide.REAR, "ancientwarfare:automation/mailbox_rear");  
  mailbox.setIcon(RelativeSide.BOTTOM, "ancientwarfare:automation/mailbox_bottom");
  mailbox.setIcon(RelativeSide.LEFT, "ancientwarfare:automation/mailbox_left");
  mailbox.setIcon(RelativeSide.RIGHT, "ancientwarfare:automation/mailbox_right");  

  GameRegistry.registerBlock(flywheel, ItemBlockTorqueTile.class, "flywheel");
  GameRegistry.registerTileEntity(TileTorqueStorageFlywheel.class, "flywheel_tile");
  flywheel.setIcon(0, RelativeSide.TOP, "ancientwarfare:automation/flywheel_top");
  flywheel.setIcon(0, RelativeSide.FRONT, "ancientwarfare:automation/flywheel_front");
  flywheel.setIcon(0, RelativeSide.REAR, "ancientwarfare:automation/flywheel_rear");  
  flywheel.setIcon(0, RelativeSide.BOTTOM, "ancientwarfare:automation/flywheel_bottom");
  flywheel.setIcon(0, RelativeSide.LEFT, "ancientwarfare:automation/flywheel_left");
  flywheel.setIcon(0, RelativeSide.RIGHT, "ancientwarfare:automation/flywheel_right");  
  
  GameRegistry.registerTileEntity(TileTorqueStorageFlywheelMedium.class, "flywheel_medium_tile");
  flywheel.setIcon(1, RelativeSide.TOP, "ancientwarfare:automation/flywheel_medium_top");
  flywheel.setIcon(1, RelativeSide.FRONT, "ancientwarfare:automation/flywheel_medium_front");
  flywheel.setIcon(1, RelativeSide.REAR, "ancientwarfare:automation/flywheel_medium_rear");  
  flywheel.setIcon(1, RelativeSide.BOTTOM, "ancientwarfare:automation/flywheel_medium_bottom");
  flywheel.setIcon(1, RelativeSide.LEFT, "ancientwarfare:automation/flywheel_medium_left");
  flywheel.setIcon(1, RelativeSide.RIGHT, "ancientwarfare:automation/flywheel_medium_right"); 
  
  GameRegistry.registerTileEntity(TileTorqueStorageFlywheelLarge.class, "flywheel_large_tile");
  flywheel.setIcon(2, RelativeSide.TOP, "ancientwarfare:automation/flywheel_large_top");
  flywheel.setIcon(2, RelativeSide.FRONT, "ancientwarfare:automation/flywheel_large_front");
  flywheel.setIcon(2, RelativeSide.REAR, "ancientwarfare:automation/flywheel_large_rear");  
  flywheel.setIcon(2, RelativeSide.BOTTOM, "ancientwarfare:automation/flywheel_large_bottom");
  flywheel.setIcon(2, RelativeSide.LEFT, "ancientwarfare:automation/flywheel_large_left");
  flywheel.setIcon(2, RelativeSide.RIGHT, "ancientwarfare:automation/flywheel_large_right"); 
  
  GameRegistry.registerBlock(torqueConduit, ItemBlockTorqueTile.class, "torque_conduit");
  GameRegistry.registerTileEntity(TileTorqueTransportConduit.class, "torque_conduit_tile");
  torqueConduit.setIcon(0, RelativeSide.TOP, "ancientwarfare:automation/torque_conduit_top");
  torqueConduit.setIcon(0, RelativeSide.BOTTOM, "ancientwarfare:automation/torque_conduit_side");
  torqueConduit.setIcon(0, RelativeSide.ANY_SIDE, "ancientwarfare:automation/torque_conduit_side");
  
  GameRegistry.registerTileEntity(TileTorqueTransportConduitMedium.class, "torque_conduit_medium_tile");
  torqueConduit.setIcon(1, RelativeSide.TOP, "ancientwarfare:automation/torque_conduit_medium_top");
  torqueConduit.setIcon(1, RelativeSide.BOTTOM, "ancientwarfare:automation/torque_conduit_medium_side");
  torqueConduit.setIcon(1, RelativeSide.ANY_SIDE, "ancientwarfare:automation/torque_conduit_medium_side");
  
  GameRegistry.registerTileEntity(TileTorqueTransportConduitHeavy.class, "torque_conduit_heavy_tile");
  torqueConduit.setIcon(2, RelativeSide.TOP, "ancientwarfare:automation/torque_conduit_heavy_top");
  torqueConduit.setIcon(2, RelativeSide.BOTTOM, "ancientwarfare:automation/torque_conduit_heavy_side");
  torqueConduit.setIcon(2, RelativeSide.ANY_SIDE, "ancientwarfare:automation/torque_conduit_heavy_side");
  
  GameRegistry.registerBlock(torqueDistributor, ItemBlockTorqueTile.class, "torque_distributor");
  GameRegistry.registerTileEntity(TileTorqueTransportDistributor.class, "torque_distributor_tile");
  torqueDistributor.setIcon(0, RelativeSide.TOP, "ancientwarfare:automation/torque_distributor_side");
  torqueDistributor.setIcon(0, RelativeSide.BOTTOM, "ancientwarfare:automation/torque_distributor_top");
  torqueDistributor.setIcon(0, RelativeSide.ANY_SIDE, "ancientwarfare:automation/torque_distributor_side");  

  GameRegistry.registerTileEntity(TileTorqueTransportDistributorMedium.class, "torque_distributor_medium_tile");
  torqueDistributor.setIcon(1, RelativeSide.TOP, "ancientwarfare:automation/torque_distributor_medium_side");
  torqueDistributor.setIcon(1, RelativeSide.BOTTOM, "ancientwarfare:automation/torque_distributor_medium_top");
  torqueDistributor.setIcon(1, RelativeSide.ANY_SIDE, "ancientwarfare:automation/torque_distributor_medium_side");  

  GameRegistry.registerTileEntity(TileTorqueTransportDistributorHeavy.class, "torque_distributor_heavy_tile");
  torqueDistributor.setIcon(2, RelativeSide.TOP, "ancientwarfare:automation/torque_distributor_heavy_side");
  torqueDistributor.setIcon(2, RelativeSide.BOTTOM, "ancientwarfare:automation/torque_distributor_heavy_top");
  torqueDistributor.setIcon(2, RelativeSide.ANY_SIDE, "ancientwarfare:automation/torque_distributor_heavy_side");
    
  GameRegistry.registerBlock(torqueGeneratorSterling, ItemBlockTorqueTile.class, "torque_generator_sterling");
  GameRegistry.registerTileEntity(TileTorqueGeneratorSterling.class, "torque_generator_sterling_tile");
  torqueGeneratorSterling.setIcon(0, RelativeSide.FRONT, "ancientwarfare:automation/torque_generator_sterling_top");
  torqueGeneratorSterling.setIcon(0, RelativeSide.REAR, "ancientwarfare:automation/torque_generator_sterling_bottom");
  torqueGeneratorSterling.setIcon(0, RelativeSide.LEFT, "ancientwarfare:automation/torque_generator_sterling_bottom");
  torqueGeneratorSterling.setIcon(0, RelativeSide.RIGHT, "ancientwarfare:automation/torque_generator_sterling_bottom");
  torqueGeneratorSterling.setIcon(0, RelativeSide.TOP, "ancientwarfare:automation/torque_generator_sterling_bottom");
  torqueGeneratorSterling.setIcon(0, RelativeSide.BOTTOM, "ancientwarfare:automation/torque_generator_sterling_bottom");
  
  GameRegistry.registerBlock(torqueGeneratorWaterwheel, ItemBlockTorqueTile.class, "torque_generator_waterwheel");
  GameRegistry.registerTileEntity(TileTorqueGeneratorWaterwheel.class, "torque_generator_waterwheel_tile");
  torqueGeneratorWaterwheel.setIcon(0, RelativeSide.FRONT, "ancientwarfare:automation/waterwheel_front");
  torqueGeneratorWaterwheel.setIcon(0, RelativeSide.REAR, "ancientwarfare:automation/waterwheel_rear");
  torqueGeneratorWaterwheel.setIcon(0, RelativeSide.LEFT, "ancientwarfare:automation/waterwheel_left");
  torqueGeneratorWaterwheel.setIcon(0, RelativeSide.RIGHT, "ancientwarfare:automation/waterwheel_right");
  torqueGeneratorWaterwheel.setIcon(0, RelativeSide.TOP, "ancientwarfare:automation/waterwheel_top");
  torqueGeneratorWaterwheel.setIcon(0, RelativeSide.BOTTOM, "ancientwarfare:automation/waterwheel_bottom");
  
  GameRegistry.registerBlock(handCrankedEngine, ItemBlockTorqueTile.class, "hand_cranked_engine");
  GameRegistry.registerTileEntity(TileTorqueGeneratorHandCranked.class, "hand_cranked_engine_tile");
  handCrankedEngine.setIcon(0, RelativeSide.TOP, "ancientwarfare:automation/hand_cranked_front");
  handCrankedEngine.setIcon(0, RelativeSide.BOTTOM, "ancientwarfare:automation/hand_cranked_top");
  handCrankedEngine.setIcon(0, RelativeSide.ANY_SIDE, "ancientwarfare:automation/hand_cranked_top");
  
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
