package net.shadowmage.ancientwarfare.core.config;

import net.minecraftforge.common.config.Configuration;

public class Statics
{

public static final boolean DEBUG = true;
public static String configPath;//the base AW config folder
public static final String coreModID = "ancientwarfare";
public static final String resourcePath = "/assets/ancientwarfare/resources/";

/**
 * category names
 */
private static final String generalOptions = "a_general_options";
private static final String serverOptions = "b_server_options";
private static final String clientOptions = "c_client_options";
private static final String worldGenSettings = "d_world_gen_settings";
private static final String keybinds = "e_keybinds";

/**
 * general options
 */
public static boolean useResearchSystem = true;

/**
 * server options
 */
public static boolean fireBlockBreakEvents = true;
public static boolean includeResearchInChests = true;//TODO add to config

/**
 * client options
 */
public static void loadConfig(Configuration config)
  {
  setCategoryNames(config);
  /**
   * general options
   */
  useResearchSystem = config.get(generalOptions, "use_research_system", useResearchSystem, "Default = true\n" +
  		"If set to false, research system will be disabled and\n" +
  		"all recipes will be available in normal crafting station.").getBoolean(useResearchSystem);
  
  /**
   * server options
   */
  fireBlockBreakEvents = config.get(serverOptions, "fire_block_break_events", fireBlockBreakEvents, "Default = true\n" +
  		"If set to false, block-break-events will not be posted for _any_ operations\n" +
  		"effectively negating any block-protection mods/mechanims in place on the server.\n" +
  		"If left at true, block-break events will be posted for any automation or vehicles\n" +
  		"which are changing blocks in the world.  Most will use a reference to their owners-name\n" +
  		"for permissions systems.").getBoolean(fireBlockBreakEvents);
  
  /**
   * client options
   */
  
  /**
   * world-gen options
   */
  
  config.save();
  }

private static void setCategoryNames(Configuration config)
  {
  config.addCustomCategoryComment(generalOptions, "General Options\n" +
  		"Affect both client and server.  These configs must match for client and server, or\n" +
  		"strange things WILL happen.");
  
  config.addCustomCategoryComment(serverOptions, "Server Options\n" +
  		"Affect only server-side operations.  Will need to be set for dedicated servers, and single\n" +
  		"player (or LAN worlds).  Clients playing on remote servers can ignore these settings.");
  
  config.addCustomCategoryComment(clientOptions, "Client Options\n" +
  		"Affect only client-side operations.  Many of these options can be set from the in-game Options GUI.\n" +
  		"Server admins can ignore these settings.");
  
  config.addCustomCategoryComment(worldGenSettings, "AW Core World Generation Settings\n" +
  		"Server-side only settings.  These settings affect world generation settings for AWCore.");
  
  config.addCustomCategoryComment(keybinds, "Custom Keybinds Selection\n" +
  		"Client-side only.  These are keybinds used by Ancient Warfare only.\n" +
  		"These keybinds need-not be unique -- you may bind the same key to multiple\n" +
  		"functions, or to keys used by other mods or vanilla functions.  Resolution of\n" +
  		"key conflicts is left up to the user.");
  }

}
