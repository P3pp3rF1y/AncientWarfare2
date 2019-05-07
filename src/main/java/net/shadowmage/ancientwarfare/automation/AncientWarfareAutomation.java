package net.shadowmage.ancientwarfare.automation;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.shadowmage.ancientwarfare.automation.chunkloader.AWChunkLoader;
import net.shadowmage.ancientwarfare.automation.command.CommandWarehouse;
import net.shadowmage.ancientwarfare.automation.compat.agricraft.AgricraftCompat;
import net.shadowmage.ancientwarfare.automation.config.AWAutomationStatics;
import net.shadowmage.ancientwarfare.automation.container.ContainerChunkLoaderDeluxe;
import net.shadowmage.ancientwarfare.automation.container.ContainerMailbox;
import net.shadowmage.ancientwarfare.automation.container.ContainerStirlingGenerator;
import net.shadowmage.ancientwarfare.automation.container.ContainerWarehouseControl;
import net.shadowmage.ancientwarfare.automation.container.ContainerWarehouseCraftingStation;
import net.shadowmage.ancientwarfare.automation.container.ContainerWarehouseInterface;
import net.shadowmage.ancientwarfare.automation.container.ContainerWarehouseStockViewer;
import net.shadowmage.ancientwarfare.automation.container.ContainerWarehouseStorage;
import net.shadowmage.ancientwarfare.automation.container.ContainerWorksiteAnimalControl;
import net.shadowmage.ancientwarfare.automation.container.ContainerWorksiteAnimalFarm;
import net.shadowmage.ancientwarfare.automation.container.ContainerWorksiteAutoCrafting;
import net.shadowmage.ancientwarfare.automation.container.ContainerWorksiteBoundsAdjust;
import net.shadowmage.ancientwarfare.automation.container.ContainerWorksiteCropFarm;
import net.shadowmage.ancientwarfare.automation.container.ContainerWorksiteFishControl;
import net.shadowmage.ancientwarfare.automation.container.ContainerWorksiteFishFarm;
import net.shadowmage.ancientwarfare.automation.container.ContainerWorksiteFruitFarm;
import net.shadowmage.ancientwarfare.automation.container.ContainerWorksiteInventorySideSelection;
import net.shadowmage.ancientwarfare.automation.container.ContainerWorksiteQuarry;
import net.shadowmage.ancientwarfare.automation.container.ContainerWorksiteQuarryBounds;
import net.shadowmage.ancientwarfare.automation.container.ContainerWorksiteTreeFarm;
import net.shadowmage.ancientwarfare.automation.proxy.RFProxy;
import net.shadowmage.ancientwarfare.automation.registry.CropFarmRegistry;
import net.shadowmage.ancientwarfare.automation.registry.FruitFarmRegistry;
import net.shadowmage.ancientwarfare.automation.registry.TreeFarmRegistry;
import net.shadowmage.ancientwarfare.automation.tile.warehouse2.WarehouseDebugger;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.core.compat.CompatLoader;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.proxy.CommonProxyBase;
import net.shadowmage.ancientwarfare.core.registry.RegistryLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(name = "Ancient Warfare Automation", modid = AncientWarfareAutomation.MOD_ID, version = "@VERSION@", dependencies = "required-after:ancientwarfare;after:redstoneflux;after:buildcraftcore")
public class AncientWarfareAutomation {
	public static final String MOD_ID = "ancientwarfareautomation";

	@Instance(value = MOD_ID)
	public static AncientWarfareAutomation instance;

	@SidedProxy(clientSide = "net.shadowmage.ancientwarfare.automation.proxy.ClientProxyAutomation", serverSide = "net.shadowmage.ancientwarfare.core.proxy.CommonProxy")
	public static CommonProxyBase proxy;

	public static final CreativeTabs TAB = new AWAutomationTab();

	public static final Logger LOG = LogManager.getLogger(MOD_ID);

	public static AWAutomationStatics statics;

	@EventHandler
	public void preInit(FMLPreInitializationEvent evt) {
		RFProxy.loadInstance();

		statics = new AWAutomationStatics("AncientWarfareAutomation");

		proxy.preInit();

		NetworkHandler.registerContainer(NetworkHandler.GUI_WORKSITE_INVENTORY_SIDE_ADJUST, ContainerWorksiteInventorySideSelection.class);
		NetworkHandler.registerContainer(NetworkHandler.GUI_WORKSITE_ANIMAL_CONTROL, ContainerWorksiteAnimalControl.class);
		NetworkHandler.registerContainer(NetworkHandler.GUI_WORKSITE_AUTO_CRAFT, ContainerWorksiteAutoCrafting.class);
		NetworkHandler.registerContainer(NetworkHandler.GUI_WORKSITE_FISH_CONTROL, ContainerWorksiteFishControl.class);
		NetworkHandler.registerContainer(NetworkHandler.GUI_MAILBOX_INVENTORY, ContainerMailbox.class);
		NetworkHandler.registerContainer(NetworkHandler.GUI_WAREHOUSE_CONTROL, ContainerWarehouseControl.class);
		NetworkHandler.registerContainer(NetworkHandler.GUI_WAREHOUSE_STORAGE, ContainerWarehouseStorage.class);
		NetworkHandler.registerContainer(NetworkHandler.GUI_WAREHOUSE_OUTPUT, ContainerWarehouseInterface.class);
		NetworkHandler.registerContainer(NetworkHandler.GUI_WAREHOUSE_CRAFTING, ContainerWarehouseCraftingStation.class);
		NetworkHandler.registerContainer(NetworkHandler.GUI_WORKSITE_QUARRY, ContainerWorksiteQuarry.class);
		NetworkHandler.registerContainer(NetworkHandler.GUI_WORKSITE_QUARRY_BOUNDS, ContainerWorksiteQuarryBounds.class);
		NetworkHandler.registerContainer(NetworkHandler.GUI_WORKSITE_TREE_FARM, ContainerWorksiteTreeFarm.class);
		NetworkHandler.registerContainer(NetworkHandler.GUI_WORKSITE_CROP_FARM, ContainerWorksiteCropFarm.class);
		NetworkHandler.registerContainer(NetworkHandler.GUI_WORKSITE_FRUIT_FARM, ContainerWorksiteFruitFarm.class);
		NetworkHandler.registerContainer(NetworkHandler.GUI_WORKSITE_ANIMAL_FARM, ContainerWorksiteAnimalFarm.class);
		NetworkHandler.registerContainer(NetworkHandler.GUI_WORKSITE_FISH_FARM, ContainerWorksiteFishFarm.class);
		NetworkHandler.registerContainer(NetworkHandler.GUI_STIRLING_GENERATOR, ContainerStirlingGenerator.class);
		NetworkHandler.registerContainer(NetworkHandler.GUI_CHUNK_LOADER_DELUXE, ContainerChunkLoaderDeluxe.class);
		NetworkHandler.registerContainer(NetworkHandler.GUI_WAREHOUSE_STOCK, ContainerWarehouseStockViewer.class);
		NetworkHandler.registerContainer(NetworkHandler.GUI_WORKSITE_BOUNDS, ContainerWorksiteBoundsAdjust.class);

		MinecraftForge.EVENT_BUS.register(this);
		MinecraftForge.EVENT_BUS.register(new WarehouseDebugger());

		ForgeChunkManager.setForcedChunkLoadingCallback(this, AWChunkLoader.INSTANCE);

		RegistryLoader.registerParser(new CropFarmRegistry.TillableParser());
		RegistryLoader.registerParser(new CropFarmRegistry.CropParser());
		RegistryLoader.registerParser(new CropFarmRegistry.SoilParser());
		RegistryLoader.registerParser(new FruitFarmRegistry.FruitParser());
		RegistryLoader.registerParser(new TreeFarmRegistry.PlantableParser());
		RegistryLoader.registerParser(new TreeFarmRegistry.SoilParser());
		RegistryLoader.registerParser(new TreeFarmRegistry.TreeScannerParser());

		CompatLoader.registerCompat(new AgricraftCompat());
	}

	@EventHandler
	public void init(FMLInitializationEvent evt) {
		proxy.init();

		statics.save();
	}

	@EventHandler
	public void serverStart(FMLServerStartingEvent evt) {
		evt.registerServerCommand(new CommandWarehouse());
	}

	@SubscribeEvent
	public void onConfigChanged(OnConfigChangedEvent evt) {
		if (AncientWarfareCore.MOD_ID.equals(evt.getModID())) {
			statics.save();
		}
	}
}
