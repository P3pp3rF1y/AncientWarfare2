package net.shadowmage.ancientwarfare.automation.block;

import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.shadowmage.ancientwarfare.automation.AncientWarfareAutomation;

@ObjectHolder(AncientWarfareAutomation.MOD_ID)
@SuppressWarnings("squid:S1444")
public class AWAutomationBlocks {
	private AWAutomationBlocks() {}

	@ObjectHolder("quarry")
	public static BlockWorksiteBase worksiteQuarry;
	@ObjectHolder("tree_farm")
	public static BlockWorksiteBase worksiteForestry;
	@ObjectHolder("crop_farm")
	public static BlockWorksiteBase worksiteCropFarm;
	@ObjectHolder("fruit_farm")
	public static BlockWorksiteBase worksiteFruitFarm;
	@ObjectHolder("tree_farm")
	public static BlockWorksiteBase worksiteTreeFarm;
	@ObjectHolder("animal_farm")
	public static BlockWorksiteBase worksiteAnimalFarm;
	@ObjectHolder("fish_farm")
	public static BlockWorksiteBase worksiteFishFarm;
	@ObjectHolder("auto_crafting")
	public static BlockAutoCrafting worksiteAutoCrafting;
	@ObjectHolder("warehouse_control")
	public static BlockWorksiteBase worksiteWarehouse;
	@ObjectHolder("warehouse_storage")
	public static BlockWarehouseStorage warehouseStorageBlock;
	@ObjectHolder("warehouse_interface")
	public static BlockWarehouseInterface warehouseInterface;
	@ObjectHolder("warehouse_crafting")
	public static BlockWarehouseCraftingStation warehouseCrafting;
	@ObjectHolder("warehouse_stock_viewer")
	public static BlockWarehouseStockViewer warehouseStockViewer;
	@ObjectHolder("mailbox")
	public static BlockMailbox mailbox;
	/*
		 * POWER NETWORK BLOCKS
		 */
	@ObjectHolder("flywheel_controller")
	public static BlockFlywheelController flywheel;
	@ObjectHolder("flywheel_storage")
	public static BlockFlywheelStorage flywheelStorage;
	@ObjectHolder("torque_junction")
	public static BlockTorqueTransport torqueJunction;
	@ObjectHolder("torque_shaft")
	public static BlockTorqueTransportShaft torqueShaft;
	@ObjectHolder("torque_distributor")
	public static BlockTorqueDistributor torqueDistributor;
	@ObjectHolder("hand_cranked_generator")
	public static BlockHandCrankedGenerator handCrankedGenerator;
	@ObjectHolder("stirling_generator")
	public static BlockTorqueGenerator stirlingGenerator;
	@ObjectHolder("waterwheel_generator")
	public static BlockTorqueGenerator torqueGeneratorWaterwheel;
	@ObjectHolder("windmill_blade")
	public static BlockWindmillBlade windmillBlade;
	@ObjectHolder("windmill_generator")
	public static BlockWindmillGenerator windmillControl;
	@ObjectHolder("chunk_loader_simple")
	public static BlockChunkLoaderSimple chunkLoaderSimple;
	@ObjectHolder("chunk_loader_deluxe")
	public static BlockChunkLoaderDeluxe chunkLoaderDeluxe;
}
