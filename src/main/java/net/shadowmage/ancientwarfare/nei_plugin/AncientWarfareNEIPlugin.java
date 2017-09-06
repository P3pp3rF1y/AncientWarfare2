//TODO JEI integration
package net.shadowmage.ancientwarfare.nei_plugin;

import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
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
        if (Loader.isModLoaded("NotEnoughItems")) {
            if (evt.getSide().isClient()) {
                AWLog.log("NEI Detected, attempting load of NEI Plugin");
                try {
                    Class.forName("net.shadowmage.ancientwarfare.nei_plugin.AWNeiRecipeHandler").newInstance();
                    AWLog.log("NEI Plugin loaded successfully");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else
            AWLog.log("Skipping loading of NEI plugin, NEI not found!");
    }
}
