package net.shadowmage.ancientwarfare.modeler;

import net.minecraft.item.Item;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.core.api.ModuleStatus;
import net.shadowmage.ancientwarfare.core.config.AWCoreStatics;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.modeler.item.ItemModelEditor;
import net.shadowmage.ancientwarfare.modeler.proxy.CommonProxyModeler;

@Mod
        (
                name = "Ancient Warfare Model Editor",
                modid = AncientWarfareModeler.modID,
                version = "@VERSION@",
                dependencies = "required-after:ancientwarfare"
        )
@Mod.EventBusSubscriber(modid = AncientWarfareModeler.modID)
public class AncientWarfareModeler {

    @Instance(value = "AncientWarfareModeler")
    public static AncientWarfareModeler instance;

    public static final String modID = "ancientwarfaremodeler";

    @SidedProxy
            (
                    clientSide = "net.shadowmage.ancientwarfare.modeler.proxy.ClientProxyModeler",
                    serverSide = "net.shadowmage.ancientwarfare.modeler.proxy.CommonProxyModeler"
            )
    public static CommonProxyModeler proxy;

    public static Configuration config;

    public static org.apache.logging.log4j.Logger log;

    @EventHandler
    public void preInit(FMLPreInitializationEvent evt) {
        ModuleStatus.modelerLoaded = true;
        log = AncientWarfareCore.log;
        config = AWCoreStatics.getConfigFor("AncientWarfareModeler");

        /*
         * internal registry
         */
        if (config.hasChanged())
            config.save();
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        IForgeRegistry<Item> registry = event.getRegistry();

        registry.register(new ItemModelEditor("editor_opener"));
    }


    @EventHandler
    public void postInit(FMLPostInitializationEvent evt) {
        AWLog.log("Ancient Warfare Modeler Post-Init completed.  Successfully completed all loading stages.");
    }

}
