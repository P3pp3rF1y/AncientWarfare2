package net.shadowmage.ancientwarfare.core.config;

import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import org.apache.logging.log4j.Level;

public class AWLog {

    public static void log(String message) {
        AncientWarfareCore.log.log(Level.INFO, message);
    }

    public static void logError(String message) {
        AncientWarfareCore.log.log(Level.ERROR, message);
    }

    public static void logDebug(String message) {
        if (AWCoreStatics.DEBUG) {
            log(message);
        }
    }

}
