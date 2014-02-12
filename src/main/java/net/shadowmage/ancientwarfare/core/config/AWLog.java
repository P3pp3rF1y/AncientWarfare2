package net.shadowmage.ancientwarfare.core.config;

import org.apache.logging.log4j.Level;

import net.shadowmage.ancientwarfare.core.AncientWarfareCore;

public class AWLog
{

public static void log(String message)
  {
  AncientWarfareCore.log.log(Level.INFO, message);
  }

public static void logError(String message)
  {
  AncientWarfareCore.log.log(Level.ERROR, message);
  }

public static void logDebug(String message)
  {
  AncientWarfareCore.log.log(Level.ERROR, "[DEBUG] 0"+message);
  }

}
