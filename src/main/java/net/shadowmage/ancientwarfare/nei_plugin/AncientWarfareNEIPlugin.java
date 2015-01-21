package net.shadowmage.ancientwarfare.nei_plugin;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.shadowmage.ancientwarfare.core.config.AWLog;

@Mod
        (
                name = "Ancient Warfare NEI Plugin",
                modid = "AncientWarfareNEIPlugin",
                version = "@VERSION@",
                dependencies = "required-after:AncientWarfare;after:NotEnoughItems"
        )
public class AncientWarfareNEIPlugin {

    @EventHandler
    public void init(FMLInitializationEvent evt) {
        if (Loader.isModLoaded("NotEnoughItems"))
            MinecraftForge.EVENT_BUS.register(this);
    }

    boolean loaded = false;

    @SubscribeEvent
    public void worldLoaded(WorldEvent.Load evt) {
        if (evt.world.isRemote && !loaded) {
            loaded = true;
            try {
                Class clz = Class.forName("codechicken.nei.api.API");
                if (clz != null) {
                    AWLog.log("NEI Detected, attempting load of NEI Plugin");
                    Class clz2 = Class.forName("net.shadowmage.ancientwarfare.nei_plugin.AWNeiProxyActual");
                    try {
                        Object proxy = clz2.newInstance();
                        AWLog.log("NEI Plugin loaded successfully");
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                AWLog.log("Skipping loading of NEI plugin, NEI not found!");
            }
        }
        MinecraftForge.EVENT_BUS.unregister(this);
    }
}
