package net.shadowmage.ancientwarfare.modeler;

import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.core.api.ModuleStatus;
import net.shadowmage.ancientwarfare.core.config.AWCoreStatics;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.modeler.item.ItemModelEditor;
import net.shadowmage.ancientwarfare.modeler.proxy.CommonProxyModeler;

@Mod
        (
                name = "Ancient Warfare Model Editor",
                modid = "AncientWarfareModeler",
                version = "@VERSION@",
                dependencies = "required-after:AncientWarfare"
        )

public class AncientWarfareModeler {

    @Instance(value = "AncientWarfareModeler")
    public static AncientWarfareModeler instance;

    @SidedProxy
            (
                    clientSide = "net.shadowmage.ancientwarfare.modeler.proxy.ClientProxyModeler",
                    serverSide = "net.shadowmage.ancientwarfare.modeler.proxy.CommonProxyModeler"
            )
    public static CommonProxyModeler proxy;

    public static Configuration config;

    public static org.apache.logging.log4j.Logger log;

    public static ItemModelEditor editorOpener;

    @EventHandler
    public void preInit(FMLPreInitializationEvent evt) {
        ModuleStatus.modelerLoaded = true;
        log = AncientWarfareCore.log;
        config = AWCoreStatics.getConfigFor("AncientWarfareModeler");

        /**
         * internal registry
         */
        editorOpener = (ItemModelEditor) new ItemModelEditor("editor_opener").setTextureName("ancientwarfare:modeler/editor_opener");
        GameRegistry.registerItem(editorOpener, "editor_opener");
        if (config.hasChanged())
            config.save();
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent evt) {
        AWLog.log("Ancient Warfare Modeler Post-Init completed.  Successfully completed all loading stages.");
    }

}
