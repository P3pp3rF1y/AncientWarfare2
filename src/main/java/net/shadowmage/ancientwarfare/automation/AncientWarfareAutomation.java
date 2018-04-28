package net.shadowmage.ancientwarfare.automation;

import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.shadowmage.ancientwarfare.automation.chunkloader.AWChunkLoader;
import net.shadowmage.ancientwarfare.automation.compat.CompatLoader;
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
import net.shadowmage.ancientwarfare.automation.container.ContainerWorksiteInventorySideSelection;
import net.shadowmage.ancientwarfare.automation.container.ContainerWorksiteMushroomFarm;
import net.shadowmage.ancientwarfare.automation.container.ContainerWorksiteQuarry;
import net.shadowmage.ancientwarfare.automation.container.ContainerWorksiteReedFarm;
import net.shadowmage.ancientwarfare.automation.container.ContainerWorksiteTreeFarm;
import net.shadowmage.ancientwarfare.automation.proxy.RFProxy;
import net.shadowmage.ancientwarfare.automation.registry.CropFarmRegistry;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.core.api.ModuleStatus;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.proxy.CommonProxyBase;
import net.shadowmage.ancientwarfare.core.registry.RegistryLoader;

@Mod(name = "Ancient Warfare Automation", modid = AncientWarfareAutomation.modID, version = "@VERSION@", dependencies = "required-after:ancientwarfare;after:redstoneflux;after:buildcraftcore")
public class AncientWarfareAutomation {
	public static final String modID = "ancientwarfareautomation";

	@Instance(value = modID)
	public static AncientWarfareAutomation instance;

	@SidedProxy(clientSide = "net.shadowmage.ancientwarfare.automation.proxy.ClientProxyAutomation", serverSide = "net.shadowmage.ancientwarfare.core.proxy.CommonProxy")
	public static CommonProxyBase proxy;

	public static AWAutomationStatics statics;

	@EventHandler
	public void preInit(FMLPreInitializationEvent evt) {
		ModuleStatus.automationLoaded = true;
		if (Loader.isModLoaded("buildcraftcore")) {
			ModuleStatus.buildCraftLoaded = true;
			AWLog.log("Detecting BuildCraft Core is loaded, enabling BC Compatibility");
		}
		if (Loader.isModLoaded("redstoneflux")) {
			ModuleStatus.redstoneFluxEnabled = true;
			AWLog.log("Detecting Redstone Flux is loaded, enabling RF Compatibility");
		}
		RFProxy.loadInstance();

        /*
		 * setup module-owned config file and config-access class
         */
		statics = new AWAutomationStatics("AncientWarfareAutomation");

        /*
		 * must be loaded after items/blocks, as it needs them registered
         */
		proxy.preInit();

        /*
		 * register containers
         */
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
		NetworkHandler.registerContainer(NetworkHandler.GUI_WORKSITE_TREE_FARM, ContainerWorksiteTreeFarm.class);
		NetworkHandler.registerContainer(NetworkHandler.GUI_WORKSITE_CROP_FARM, ContainerWorksiteCropFarm.class);
		NetworkHandler.registerContainer(NetworkHandler.GUI_WORKSITE_MUSHROOM_FARM, ContainerWorksiteMushroomFarm.class);
		NetworkHandler.registerContainer(NetworkHandler.GUI_WORKSITE_ANIMAL_FARM, ContainerWorksiteAnimalFarm.class);
		NetworkHandler.registerContainer(NetworkHandler.GUI_WORKSITE_REED_FARM, ContainerWorksiteReedFarm.class);
		NetworkHandler.registerContainer(NetworkHandler.GUI_WORKSITE_FISH_FARM, ContainerWorksiteFishFarm.class);
		NetworkHandler.registerContainer(NetworkHandler.GUI_STIRLING_GENERATOR, ContainerStirlingGenerator.class);
		NetworkHandler.registerContainer(NetworkHandler.GUI_CHUNK_LOADER_DELUXE, ContainerChunkLoaderDeluxe.class);
		NetworkHandler.registerContainer(NetworkHandler.GUI_WAREHOUSE_STOCK, ContainerWarehouseStockViewer.class);
		NetworkHandler.registerContainer(NetworkHandler.GUI_WORKSITE_BOUNDS, ContainerWorksiteBoundsAdjust.class);

        /*
		 * register tick-handlers
         */
		MinecraftForge.EVENT_BUS.register(this);

		ForgeChunkManager.setForcedChunkLoadingCallback(this, AWChunkLoader.INSTANCE);

		RegistryLoader.registerParser(new CropFarmRegistry.Parser());
	}

	@EventHandler
	public void init(FMLInitializationEvent evt) {
		/*
		 * construct recipes, load plugins
         */
		proxy.init();

		CompatLoader.init();

		statics.save();
	}

	@SubscribeEvent
	public void onConfigChanged(OnConfigChangedEvent evt) {
		if (AncientWarfareCore.modID.equals(evt.getModID())) {
			statics.save();
		}
	}
}
