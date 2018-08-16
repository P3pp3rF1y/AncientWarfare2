package net.shadowmage.ancientwarfare.automation.block;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
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
import net.shadowmage.ancientwarfare.automation.tile.torque.TileFlywheelControllerHeavy;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileFlywheelControllerLight;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileFlywheelControllerMedium;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileHandCrankedGenerator;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileStirlingGenerator;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileTorqueShaftHeavy;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileTorqueShaftLight;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileTorqueShaftMedium;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileWaterwheelGenerator;
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
import net.shadowmage.ancientwarfare.automation.tile.worksite.WorkSiteFishFarm;
import net.shadowmage.ancientwarfare.automation.tile.worksite.WorkSiteQuarry;
import net.shadowmage.ancientwarfare.automation.tile.worksite.cropfarm.WorkSiteCropFarm;
import net.shadowmage.ancientwarfare.automation.tile.worksite.fruitfarm.WorkSiteFruitFarm;
import net.shadowmage.ancientwarfare.automation.tile.worksite.treefarm.WorkSiteTreeFarm;
import net.shadowmage.ancientwarfare.core.item.ItemBlockMeta;
import net.shadowmage.ancientwarfare.core.item.ItemBlockOwnedRotatable;
import net.shadowmage.ancientwarfare.core.item.ItemBlockRotatableMetaTile;

@Mod.EventBusSubscriber(modid = AncientWarfareAutomation.MOD_ID)
public class AWAutomationBlockLoader {
	private AWAutomationBlockLoader() {
	}

	@SuppressWarnings("ConstantConditions")
	@SubscribeEvent
	public static void registerItemBlocks(RegistryEvent.Register<Item> event) {
		IForgeRegistry<Item> registry = event.getRegistry();

		registry.register(new ItemBlockWorksiteStatic(AWAutomationBlocks.worksiteQuarry));
		registry.register(new ItemBlockWorksiteStatic(AWAutomationBlocks.worksiteForestry));
		registry.register(new ItemBlockWorksiteStatic(AWAutomationBlocks.worksiteCropFarm));
		registry.register(new ItemBlockWorksiteStatic(AWAutomationBlocks.worksiteFruitFarm));
		registry.register(new ItemBlockWorksiteStatic(AWAutomationBlocks.worksiteAnimalFarm));
		registry.register(new ItemBlockWorksiteStatic(AWAutomationBlocks.worksiteFishFarm));
		registry.register(new ItemBlockWorksiteStatic(AWAutomationBlocks.worksiteWarehouse));
		registry.register(new ItemBlockWarehouseStorage(AWAutomationBlocks.warehouseStorageBlock));
		registry.register(new ItemBlock(AWAutomationBlocks.warehouseInterface).setRegistryName(AWAutomationBlocks.warehouseInterface.getRegistryName()));
		registry.register(new ItemBlock(AWAutomationBlocks.warehouseCrafting).setRegistryName(AWAutomationBlocks.warehouseCrafting.getRegistryName()));
		registry.register(new ItemBlockOwnedRotatable(AWAutomationBlocks.warehouseStockViewer));
		registry.register(new ItemBlockRotatableMetaTile(AWAutomationBlocks.worksiteAutoCrafting));
		registry.register(new ItemBlockOwnedRotatable(AWAutomationBlocks.mailbox));

		registry.register(new ItemBlockTorqueTile(AWAutomationBlocks.flywheel));
		registry.register(new ItemBlockMeta(AWAutomationBlocks.flywheelStorage));
		registry.register(new ItemBlockTorqueTile(AWAutomationBlocks.torqueJunction));
		registry.register(new ItemBlockTorqueTile(AWAutomationBlocks.torqueShaft));
		registry.register(new ItemBlockTorqueTile(AWAutomationBlocks.torqueDistributor));
		registry.register(new ItemBlockTorqueTile(AWAutomationBlocks.stirlingGenerator));
		registry.register(new ItemBlockTorqueTile(AWAutomationBlocks.torqueGeneratorWaterwheel));
		registry.register(new ItemBlockTorqueTile(AWAutomationBlocks.handCrankedGenerator));
		registry.register(new ItemBlock(AWAutomationBlocks.windmillBlade).setRegistryName(AWAutomationBlocks.windmillBlade.getRegistryName()));
		registry.register(new ItemBlockTorqueTile(AWAutomationBlocks.windmillControl));

		registry.register(new ItemBlock(AWAutomationBlocks.chunkLoaderSimple).setRegistryName(AWAutomationBlocks.chunkLoaderSimple.getRegistryName()));
		registry.register(new ItemBlock(AWAutomationBlocks.chunkLoaderDeluxe).setRegistryName(AWAutomationBlocks.chunkLoaderDeluxe.getRegistryName()));
	}

	@SubscribeEvent
	public static void register(RegistryEvent.Register<Block> event) {
		IForgeRegistry<Block> registry = event.getRegistry();

		registry.register(new BlockWorksiteBase("quarry").setTileFactory(WorkSiteQuarry::new));
		registerTile(WorkSiteQuarry.class, "quarry_tile");

		registry.register(new BlockWorksiteBase("tree_farm").setTileFactory(WorkSiteTreeFarm::new));
		registerTile(WorkSiteTreeFarm.class, "forestry_tile");

		registry.register(new BlockWorksiteBase("crop_farm").setTileFactory(WorkSiteCropFarm::new));
		registerTile(WorkSiteCropFarm.class, "crop_farm_tile");

		registry.register(new BlockWorksiteBase("fruit_farm").setTileFactory(WorkSiteFruitFarm::new));
		registerTile(WorkSiteFruitFarm.class, "fruit_farm_tile");

		registry.register(new BlockWorksiteBase("animal_farm").setTileFactory(WorkSiteAnimalFarm::new));
		registerTile(WorkSiteAnimalFarm.class, "animal_farm_tile");

		registry.register(new BlockWorksiteBase("fish_farm").setTileFactory(WorkSiteFishFarm::new));
		registerTile(WorkSiteFishFarm.class, "fish_farm_tile");

		registry.register(new BlockWorksiteBase("warehouse_control").setTileFactory(TileWarehouse::new));
		registerTile(TileWarehouse.class, "warehouse_control_tile");

		registry.register(new BlockWarehouseStorage("warehouse_storage"));
		registerTile(TileWarehouseStorageMedium.class, "warehouse_storage_medium_tile");
		registerTile(TileWarehouseStorage.class, "warehouse_storage_small_tile");
		registerTile(TileWarehouseStorageLarge.class, "warehouse_storage_large_tile");

		registry.register(new BlockWarehouseInterface("warehouse_interface"));
		registerTile(TileWarehouseInterface.class, "warehouse_interface_tile");

		registry.register(new BlockWarehouseCraftingStation("warehouse_crafting"));
		registerTile(TileWarehouseCraftingStation.class, "warehouse_crafting_tile");

		registry.register(new BlockWarehouseStockViewer("warehouse_stock_viewer"));
		registerTile(TileWarehouseStockViewer.class, "warehouse_stock_viewer_tile");

		registry.register(new BlockAutoCrafting("auto_crafting"));
		registerTile(TileAutoCrafting.class, "auto_crafting_tile");

		registry.register(new BlockMailbox("mailbox"));
		registerTile(TileMailbox.class, "mailbox_tile");

		registry.register(new BlockFlywheelController("flywheel_controller"));
		registerTile(TileFlywheelControllerLight.class, "flywheel_light_tile");
		registerTile(TileFlywheelControllerMedium.class, "flywheel_medium_tile");
		registerTile(TileFlywheelControllerHeavy.class, "flywheel_large_tile");

		registry.register(new BlockFlywheelStorage("flywheel_storage"));
		registerTile(TileFlywheelStorage.class, "flywheel_storage_tile");

		registry.register(new BlockTorqueJunction("torque_junction"));
		registerTile(TileConduitLight.class, "torque_junction_light_tile");
		registerTile(TileConduitMedium.class, "torque_junction_medium_tile");
		registerTile(TileConduitHeavy.class, "torque_junction_heavy_tile");

		registry.register(new BlockTorqueTransportShaft("torque_shaft"));
		registerTile(TileTorqueShaftLight.class, "torque_shaft_light_tile");
		registerTile(TileTorqueShaftMedium.class, "torque_shaft_medium_tile");
		registerTile(TileTorqueShaftHeavy.class, "torque_shaft_heavy_tile");

		registry.register(new BlockTorqueDistributor("torque_distributor"));
		registerTile(TileDistributorLight.class, "torque_distributor_light_tile");
		registerTile(TileDistributorMedium.class, "torque_distributor_medium_tile");
		registerTile(TileDistributorHeavy.class, "torque_distributor_heavy_tile");

		registry.register(new BlockStirlingGenerator("stirling_generator"));
		registerTile(TileStirlingGenerator.class, "stirling_generator_tile");

		registry.register(new BlockWaterwheelGenerator("waterwheel_generator"));
		registerTile(TileWaterwheelGenerator.class, "waterwheel_generator_tile");

		registry.register(new BlockHandCrankedGenerator("hand_cranked_generator"));
		registerTile(TileHandCrankedGenerator.class, "hand_cranked_generator_tile");

		registry.register(new BlockWindmillBlade("windmill_blade"));
		registerTile(TileWindmillBlade.class, "windmill_blade_tile");

		registry.register(new BlockWindmillGenerator("windmill_generator"));
		registerTile(TileWindmillController.class, "windmill_generator_tile");

		registry.register(new BlockChunkLoaderSimple("chunk_loader_simple"));
		registerTile(TileChunkLoaderSimple.class, "chunk_loader_simple_tile");

		registry.register(new BlockChunkLoaderDeluxe("chunk_loader_deluxe"));
		registerTile(TileChunkLoaderDeluxe.class, "chunk_loader_deluxe_tile");
	}

	private static void registerTile(Class<? extends TileEntity> teClass, String teId) {
		GameRegistry.registerTileEntity(teClass, new ResourceLocation(AncientWarfareAutomation.MOD_ID, teId));
	}
}
