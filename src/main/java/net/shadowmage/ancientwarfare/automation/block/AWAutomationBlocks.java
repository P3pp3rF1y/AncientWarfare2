package net.shadowmage.ancientwarfare.automation.block;

import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.shadowmage.ancientwarfare.automation.AncientWarfareAutomation;

@ObjectHolder(AncientWarfareAutomation.modID)
public class AWAutomationBlocks {
	@ObjectHolder("civic_quarry")
	public static BlockWorksiteBase worksiteQuarry;
	@ObjectHolder("civic_forestry")
	public static BlockWorksiteBase worksiteForestry;
	@ObjectHolder("civic_crop_farm")
	public static BlockWorksiteBase worksiteCropFarm;
	@ObjectHolder("civic_mushroom_farm")
	public static BlockWorksiteBase worksiteMushroomFarm;
	@ObjectHolder("civic_animal_farm")
	public static BlockWorksiteBase worksiteAnimalFarm;
	@ObjectHolder("civic_fish_farm")
	public static BlockWorksiteBase worksiteFishFarm;
	@ObjectHolder("civic_reed_farm")
	public static BlockWorksiteBase worksiteReedFarm;
	@ObjectHolder("auto_crafting")
	public static BlockAutoCrafting worksiteAutoCrafting;
	@ObjectHolder("civic_warehouse")
	public static BlockWorksiteBase worksiteWarehouse;
	@ObjectHolder("warehouse_storage")
	public static BlockWarehouseStorage warehouseStorageBlock;
	@ObjectHolder("warehouse_interface")
	public static BlockWarehouseInterface warehouseInterface;
	@ObjectHolder("warehouse_crafting_station")
	public static BlockWarehouseCraftingStation warehouseCrafting;
	@ObjectHolder("warehouse_stock_viewer")
	public static BlockWarehouseStockViewer warehouseStockViewer;
	@ObjectHolder("mailbox")
	public static BlockMailbox mailbox;
	/*
		 * POWER NETWORK BLOCKS
		 */
	@ObjectHolder("flywheel_controller")
	public static BlockFlywheel flywheel;
	@ObjectHolder("flywheel_storage")
	public static BlockFlywheelStorage flywheelStorage;
	@ObjectHolder("torque_conduit")
	public static BlockTorqueTransportConduit torqueConduit;
	@ObjectHolder("torque_shaft")
	public static BlockTorqueTransportShaft torqueShaft;
	@ObjectHolder("torque_distributor")
	public static BlockTorqueDistributor torqueDistributor;
	@ObjectHolder("hand_cranked_engine")
	public static BlockHandCrankedEngine handCrankedEngine;
	@ObjectHolder("torque_generator_sterling")
	public static BlockTorqueGenerator torqueGeneratorSterling;
	@ObjectHolder("torque_generator_waterwheel")
	public static BlockTorqueGenerator torqueGeneratorWaterwheel;
	@ObjectHolder("windmill_blade")
	public static BlockWindmillBlade windmillBlade;
	@ObjectHolder("windmill_control")
	public static BlockWindmillControll windmillControl;
	@ObjectHolder("chunk_loader_simple")
	public static BlockChunkLoaderSimple chunkLoaderSimple;
	@ObjectHolder("chunk_loader_deluxe")
	public static BlockChunkLoaderDeluxe chunkLoaderDeluxe;
}
