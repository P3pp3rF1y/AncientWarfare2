package net.shadowmage.ancientwarfare.automation;

import cpw.mods.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.config.Configuration;
import net.shadowmage.ancientwarfare.automation.block.AWAutomationBlockLoader;
import net.shadowmage.ancientwarfare.automation.chunkloader.AWChunkLoader;
import net.shadowmage.ancientwarfare.automation.config.AWAutomationStatics;
import net.shadowmage.ancientwarfare.automation.container.*;
import net.shadowmage.ancientwarfare.automation.crafting.AWAutomationCrafting;
import net.shadowmage.ancientwarfare.automation.gamedata.MailboxData;
import net.shadowmage.ancientwarfare.automation.gamedata.MailboxTicker;
import net.shadowmage.ancientwarfare.automation.item.AWAutomationItemLoader;
import net.shadowmage.ancientwarfare.automation.proxy.RFProxy;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.core.api.ModuleStatus;
import net.shadowmage.ancientwarfare.core.config.AWCoreStatics;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.gamedata.AWGameData;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.proxy.CommonProxyBase;

@Mod
        (
                name = "Ancient Warfare Automation",
                modid = "AncientWarfareAutomation",
                version = "@VERSION@",
                dependencies = "required-after:AncientWarfare;after:CoFHCore;after:BuildCraft|Core"
        )

public class AncientWarfareAutomation {

    @Instance(value = "AncientWarfareAutomation")
    public static AncientWarfareAutomation instance;

    @SidedProxy
            (
                    clientSide = "net.shadowmage.ancientwarfare.automation.proxy.ClientProxyAutomation",
                    serverSide = "net.shadowmage.ancientwarfare.core.proxy.CommonProxy"
            )
    public static CommonProxyBase proxy;

    public static Configuration config;


    public static AWAutomationStatics statics;


    @EventHandler
    public void preInit(FMLPreInitializationEvent evt) {
        ModuleStatus.automationLoaded = true;
        if (Loader.isModLoaded("BuildCraft|Core")) {
            ModuleStatus.buildCraftLoaded = true;
            AWLog.log("Detecting BuildCraft|Core is loaded, enabling BC Compatibility");
        }
        if (Loader.isModLoaded("CoFHCore")) {
            ModuleStatus.redstoneFluxEnabled = true;
            AWLog.log("Detecting CoFHCore is loaded, enabling RF Compatibility");
        }
        RFProxy.loadInstance();

        /**
         * setup module-owned config file and config-access class
         */
        config = AWCoreStatics.getConfigFor("AncientWarfareAutomation");
        statics = new AWAutomationStatics(config);

        /**
         * load pre-init
         */
        statics.load();//load config settings

        /**
         * load items and blocks
         */
        AWAutomationBlockLoader.load();
        AWAutomationItemLoader.load();

        /**
         * must be loaded after items/blocks, as it needs them registered
         */
        proxy.registerClient();

        /**
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
        NetworkHandler.registerContainer(NetworkHandler.GUI_TORQUE_GENERATOR_STERLING, ContainerTorqueGeneratorSterling.class);
        NetworkHandler.registerContainer(NetworkHandler.GUI_CHUNK_LOADER_DELUXE, ContainerChunkLoaderDeluxe.class);
        NetworkHandler.registerContainer(NetworkHandler.GUI_WAREHOUSE_STOCK, ContainerWarehouseStockViewer.class);
        NetworkHandler.registerContainer(NetworkHandler.GUI_WORKSITE_BOUNDS, ContainerWorksiteBoundsAdjust.class);

        /**
         * register tick-handlers
         */
        FMLCommonHandler.instance().bus().register(MailboxTicker.INSTANCE);
        FMLCommonHandler.instance().bus().register(this);

        ForgeChunkManager.setForcedChunkLoadingCallback(this, AWChunkLoader.INSTANCE);
    }

    @EventHandler
    public void init(FMLInitializationEvent evt) {
        /**
         * construct recipes, load plugins
         */
        AWAutomationCrafting.loadRecipes();
        if (config.hasChanged())
            config.save();
    }

    @SubscribeEvent
    public void onConfigChanged(OnConfigChangedEvent evt) {
        if (AncientWarfareCore.modID.equals(evt.modID)) {
            proxy.onConfigChanged();
        }
    }
}
