package net.shadowmage.ancientwarfare.core.interop;

import cpw.mods.fml.common.Loader;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;

public class ModAccessors {
    public static InteropFtbuInterface FTBU;
    
    public static void init() {
        try {
            if (Loader.isModLoaded("FTBU_AW2")) {
                AncientWarfareCore.log.info("FTBU_AW2 found!");
                FTBU = Class.forName("net.shadowmage.ancientwarfare.core.interop.InteropFtbu").asSubclass(InteropFtbuInterface.class).newInstance();
            } else {
                AncientWarfareCore.log.info("FTBU_AW2 not found.");
                FTBU = Class.forName("net.shadowmage.ancientwarfare.core.interop.InteropFtbuDummy").asSubclass(InteropFtbuInterface.class).newInstance();
            }
        } catch (Exception e) {
            // shouldn't happen
        }
    }
}
