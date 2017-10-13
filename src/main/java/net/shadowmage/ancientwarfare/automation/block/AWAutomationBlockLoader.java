package net.shadowmage.ancientwarfare.automation.block;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;
import net.shadowmage.ancientwarfare.automation.AncientWarfareAutomation;
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
import net.shadowmage.ancientwarfare.core.item.ItemBlockMeta;
import net.shadowmage.ancientwarfare.core.item.ItemBlockOwnedRotatable;
import net.shadowmage.ancientwarfare.core.item.ItemBlockRotatableMetaTile;

@Mod.EventBusSubscriber(modid = AncientWarfareAutomation.modID)
public class AWAutomationBlockLoader {

    @SuppressWarnings("ConstantConditions")
    @SubscribeEvent
    public static void registerItemBlocks(RegistryEvent.Register<Item> event) {
        IForgeRegistry<Item> registry = event.getRegistry();

        registry.register(new ItemBlockWorksiteStatic(AWAutomationBlocks.worksiteQuarry));
        registry.register(new ItemBlockWorksiteStatic(AWAutomationBlocks.worksiteForestry));
        registry.register(new ItemBlockWorksiteStatic(AWAutomationBlocks.worksiteCropFarm));
        registry.register(new ItemBlockWorksiteStatic(AWAutomationBlocks.worksiteMushroomFarm));
        registry.register(new ItemBlockWorksiteStatic(AWAutomationBlocks.worksiteAnimalFarm));
        registry.register(new ItemBlockWorksiteStatic(AWAutomationBlocks.worksiteFishFarm));
        registry.register(new ItemBlockWorksiteStatic(AWAutomationBlocks.worksiteReedFarm));
        registry.register(new ItemBlockWorksiteStatic(AWAutomationBlocks.worksiteWarehouse));
        registry.register(new ItemBlockWarehouseStorage(AWAutomationBlocks.warehouseStorageBlock));
        registry.register(new ItemBlock(AWAutomationBlocks.warehouseInterface).setRegistryName(AWAutomationBlocks.warehouseInterface.getRegistryName()));
        registry.register(new ItemBlock(AWAutomationBlocks.warehouseCrafting).setRegistryName(AWAutomationBlocks.warehouseCrafting.getRegistryName()));
        registry.register(new ItemBlockOwnedRotatable(AWAutomationBlocks.warehouseStockViewer));
        registry.register(new ItemBlockRotatableMetaTile(AWAutomationBlocks.worksiteAutoCrafting));
        registry.register(new ItemBlockOwnedRotatable(AWAutomationBlocks.mailbox));

        registry.register(new ItemBlockTorqueTile(AWAutomationBlocks.flywheel));
        registry.register(new ItemBlockMeta(AWAutomationBlocks.flywheelStorage));
        registry.register(new ItemBlockTorqueTile(AWAutomationBlocks.torqueConduit));
        registry.register(new ItemBlockTorqueTile(AWAutomationBlocks.torqueShaft));
        registry.register(new ItemBlockTorqueTile(AWAutomationBlocks.torqueDistributor));
        registry.register(new ItemBlockTorqueTile(AWAutomationBlocks.torqueGeneratorSterling));
        registry.register(new ItemBlockTorqueTile(AWAutomationBlocks.torqueGeneratorWaterwheel));
        registry.register(new ItemBlockTorqueTile(AWAutomationBlocks.handCrankedEngine));
        registry.register(new ItemBlock(AWAutomationBlocks.windmillBlade).setRegistryName(AWAutomationBlocks.windmillBlade.getRegistryName()));
        registry.register(new ItemBlockTorqueTile(AWAutomationBlocks.windmillControl));

        registry.register(new ItemBlock(AWAutomationBlocks.chunkLoaderSimple).setRegistryName(AWAutomationBlocks.chunkLoaderSimple.getRegistryName()));
        registry.register(new ItemBlock(AWAutomationBlocks.chunkLoaderDeluxe).setRegistryName(AWAutomationBlocks.chunkLoaderDeluxe.getRegistryName()));
    }

    @SubscribeEvent
    public static void register(RegistryEvent.Register<Block> event) {
        IForgeRegistry<Block> registry = event.getRegistry();

        registry.register(new BlockWorksiteBase("quarry").setTileEntity(WorkSiteQuarry.class).setWorkSize(64));
        GameRegistry.registerTileEntity(WorkSiteQuarry.class, "quarry_tile");

        registry.register(new BlockWorksiteBase("tree_farm").setTileEntity(WorkSiteTreeFarm.class));
        GameRegistry.registerTileEntity(WorkSiteTreeFarm.class, "forestry_tile");

        registry.register(new BlockWorksiteBase("crop_farm").setTileEntity(WorkSiteCropFarm.class));
        GameRegistry.registerTileEntity(WorkSiteCropFarm.class, "crop_farm_tile");

        registry.register(new BlockWorksiteBase("mushroom_farm").setTileEntity(WorkSiteMushroomFarm.class));
        GameRegistry.registerTileEntity(WorkSiteMushroomFarm.class, "mushroom_farm_tile");

        registry.register(new BlockWorksiteBase("animal_farm").setTileEntity(WorkSiteAnimalFarm.class));
        GameRegistry.registerTileEntity(WorkSiteAnimalFarm.class, "animal_farm_tile");

        registry.register(new BlockWorksiteBase("fish_farm").setTileEntity(WorkSiteFishFarm.class));
        GameRegistry.registerTileEntity(WorkSiteFishFarm.class, "fish_farm_tile");

        registry.register(new BlockWorksiteBase("reed_farm").setTileEntity(WorkSiteReedFarm.class));
        GameRegistry.registerTileEntity(WorkSiteReedFarm.class, "reed_farm_tile");

        registry.register(new BlockWorksiteBase("warehouse_control").setTileEntity(TileWarehouse.class).setWorkVerticalSize(4));
        GameRegistry.registerTileEntity(TileWarehouse.class, "warehouse_control_tile");

        registry.register(new BlockWarehouseStorage("warehouse_storage"));
        GameRegistry.registerTileEntity(TileWarehouseStorageMedium.class, "warehouse_storage_medium_tile");
        GameRegistry.registerTileEntity(TileWarehouseStorage.class, "warehouse_storage_small_tile");
        GameRegistry.registerTileEntity(TileWarehouseStorageLarge.class, "warehouse_storage_large_tile");

        registry.register(new BlockWarehouseInterface("warehouse_interface"));
        GameRegistry.registerTileEntity(TileWarehouseInterface.class, "warehouse_interface_tile");

        registry.register(new BlockWarehouseCraftingStation("warehouse_crafting"));
        GameRegistry.registerTileEntity(TileWarehouseCraftingStation.class, "warehouse_crafting_tile");

        registry.register(new BlockWarehouseStockViewer("warehouse_stock_viewer"));
        GameRegistry.registerTileEntity(TileWarehouseStockViewer.class, "warehouse_stock_viewer_tile");

        registry.register(new BlockAutoCrafting("auto_crafting"));
        GameRegistry.registerTileEntity(TileAutoCrafting.class, "auto_crafting_tile");

        registry.register(new BlockMailbox("mailbox"));
        GameRegistry.registerTileEntity(TileMailbox.class, "mailbox_tile");

        registry.register(new BlockFlywheel("flywheel_controller"));
        GameRegistry.registerTileEntity(TileFlywheelControlLight.class, "flywheel_tile");
        GameRegistry.registerTileEntity(TileFlywheelControlMedium.class, "flywheel_medium_tile");
        GameRegistry.registerTileEntity(TileFlywheelControlLarge.class, "flywheel_large_tile");

        registry.register(new BlockFlywheelStorage("flywheel_storage"));
        GameRegistry.registerTileEntity(TileFlywheelStorage.class, "flywheel_storage_tile");

        registry.register(new BlockTorqueTransportConduit("torque_conduit"));
        GameRegistry.registerTileEntity(TileConduitLight.class, "torque_conduit_tile");
        GameRegistry.registerTileEntity(TileConduitMedium.class, "torque_conduit_medium_tile");
        GameRegistry.registerTileEntity(TileConduitHeavy.class, "torque_conduit_heavy_tile");

        registry.register(new BlockTorqueTransportShaft("torque_shaft"));
        GameRegistry.registerTileEntity(TileTorqueShaftLight.class, "torque_driveline_tile");
        GameRegistry.registerTileEntity(TileTorqueShaftMedium.class, "torque_driveline_medium_tile");
        GameRegistry.registerTileEntity(TileTorqueShaftHeavy.class, "torque_driveline_heavy_tile");

        registry.register(new BlockTorqueDistributor("torque_distributor"));
        GameRegistry.registerTileEntity(TileDistributorLight.class, "torque_distributor_tile");
        GameRegistry.registerTileEntity(TileDistributorMedium.class, "torque_distributor_medium_tile");
        GameRegistry.registerTileEntity(TileDistributorHeavy.class, "torque_distributor_heavy_tile");

        registry.register(new BlockTorqueGeneratorSterling("torque_generator_sterling"));
        GameRegistry.registerTileEntity(TileSterlingEngine.class, "torque_generator_sterling_tile");

        registry.register(new BlockTorqueGeneratorWaterwheel("torque_generator_waterwheel"));
        GameRegistry.registerTileEntity(TileWaterwheel.class, "torque_generator_waterwheel_tile");

        registry.register(new BlockHandCrankedEngine("hand_cranked_engine"));
        GameRegistry.registerTileEntity(TileHandGenerator.class, "hand_cranked_engine_tile");

        registry.register(new BlockWindmillBlade("windmill_blade"));
        GameRegistry.registerTileEntity(TileWindmillBlade.class, "windmill_blade_tile");

        registry.register(new BlockWindmillControll("windmill_control"));
        GameRegistry.registerTileEntity(TileWindmillController.class, "windmill_control_tile");

        registry.register(new BlockChunkLoaderSimple("chunk_loader_simple"));
        GameRegistry.registerTileEntity(TileChunkLoaderSimple.class, "chunk_loader_simple_tile");

        registry.register(new BlockChunkLoaderDeluxe("chunk_loader_deluxe"));
        GameRegistry.registerTileEntity(TileChunkLoaderDeluxe.class, "chunk_loader_deluxe_tile");

//        warehouseInterface.setIcon(0, 0, "ancientwarfare:automation/warehouse_interface_bottom");
//        warehouseInterface.setIcon(0, 1, "ancientwarfare:automation/warehouse_interface_top");
//        warehouseInterface.setIcon(0, 2, "ancientwarfare:automation/warehouse_interface_side");
//        warehouseInterface.setIcon(0, 3, "ancientwarfare:automation/warehouse_interface_side");
//        warehouseInterface.setIcon(0, 4, "ancientwarfare:automation/warehouse_interface_side");
//        warehouseInterface.setIcon(0, 5, "ancientwarfare:automation/warehouse_interface_side");

//        warehouseCrafting.setIcon(0, 0, "ancientwarfare:automation/warehouse_crafting_bottom");
//        warehouseCrafting.setIcon(0, 1, "ancientwarfare:automation/warehouse_crafting_top");
//        warehouseCrafting.setIcon(0, 2, "ancientwarfare:automation/warehouse_crafting_front");
//        warehouseCrafting.setIcon(0, 3, "ancientwarfare:automation/warehouse_crafting_front");
//        warehouseCrafting.setIcon(0, 4, "ancientwarfare:automation/warehouse_crafting_side");
//        warehouseCrafting.setIcon(0, 5, "ancientwarfare:automation/warehouse_crafting_side");


    }

}
