package net.shadowmage.ancientwarfare.core.config;

import net.minecraft.item.Item;
import net.minecraftforge.common.config.Configuration;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;

import java.io.File;

public class AWCoreStatics {

    public static boolean DEBUG = true;
    public static String configPathForFiles = "config/ancientwarfare/";
    public static final String resourcePath = "/assets/ancientwarfare/resources/";
    private static Configuration config;

    /**
     * category names
     */
    public static final String generalOptions = "01_shared_settings";
    public static final String serverOptions = "02_server_settings";
    public static final String clientOptions = "03_client_settings";
    public static final String worldGenSettings = "04_world_gen_settings";
    public static final String keybinds = "05_keybinds";
    public static final String researchSettings = "06_research";
    public static final String researchDetailSettings = "07_research_details";
    public static final String recipeDetailSettings = "08_recipe_details";
    public static final String recipeResearchDetails = "09_recipe_research_details";

    /**
     * research options
     */
    public static boolean useResearchSystem = true;
    public static boolean enableResearchResourceUse = true;
    public static double energyPerResearchUnit = 1;
    public static double researchPerTick = 1;//TODO add to config

    /**
     * server options
     */
    public static boolean fireBlockBreakEvents = true;
    public static boolean includeResearchInChests = true;
    public static double energyPerWorkUnit = 50;

    /**
     * client options
     */
    public static void loadConfig(Configuration config) {
        setCategoryNames(config);
        AWCoreStatics.config = config;
        /**
         * general options
         */
        DEBUG = config.get(generalOptions, "debug_ouput", false, "Enable extra debug console output and runtime checks.\n" +
                "Can degrade performance if left on and lead to large log files.").getBoolean(false);

        /**
         * server options
         */
        fireBlockBreakEvents = config.get(serverOptions, "fire_block_break_events", fireBlockBreakEvents, "Fire Block Break Events\nDefault = true\n" +
                "If set to false, block-break-events will not be posted for _any_ operations\n" +
                "effectively negating any block-protection mods/mechanims in place on the server.\n" +
                "If left at true, block-break events will be posted for any automation or vehicles\n" +
                "which are changing blocks in the world.  Most will use a reference to their owners-name\n" +
                "for permissions systems.").getBoolean(fireBlockBreakEvents);
        includeResearchInChests = config.get(serverOptions, "include_research_in_chests", includeResearchInChests, "Include Research In Dungeon Loot Chests\nDefault=true\n" +
                "If set to true, Research Note items will be added to dungeon-chest loot tables.\n" +
                "If set to false, no research will be added.\n" +
                "This is the global setting.  Individual research may be toggled in the Research\n" +
                "section of the config file.").getBoolean(includeResearchInChests);
        energyPerWorkUnit = config.get(serverOptions, "energy_per_work_unit", energyPerWorkUnit, "Energy Per Work Unit\nDefault = 50\n" +
                "How much Torque energy is generated per worker work tick.\n" +
                "This is the base number and is further adjusted per worker by worker effectiveness.\n" +
                "Setting to 0 or below effectively disables  workers.").getDouble(energyPerWorkUnit);

        /**
         * client options
         */

        /**
         * core module world-gen options
         */

        /**
         * research settings
         */
        energyPerResearchUnit = config.get(researchSettings, "energy_used per_research_tick", energyPerResearchUnit, "Energy Per Research Unit\nDefault = 1\n" +
                "How much energy is consumed per research tick.\n" +
                "Research generally ticks every game-tick if being worked at.\n" +
                "Setting to 0 will eliminate the energy/worker requirements for research.\n" +
                "Setting to higher than 1 will increase the amount of energy needed for research,\n" +
                "increasing the amount of time/resources required for all research.").getDouble(energyPerResearchUnit);

        useResearchSystem = config.get(researchSettings, "use_research_system", useResearchSystem, "Default = true\n" +
                "If set to false, research system will be disabled and\n" +
                "all recipes will be available in normal crafting station.").getBoolean(useResearchSystem);

        enableResearchResourceUse = config.get(researchSettings, "use_research_resources", useResearchSystem, "Default = true\n" +
                "If set to false, research system will not use resources for research.").getBoolean(useResearchSystem);
        config.save();
    }

    private static void setCategoryNames(Configuration config) {
        config.addCustomCategoryComment(generalOptions, "General Options\n" +
                "Affect both client and server.  These configs must match for client and server, or\n" +
                "strange and probably BAD things WILL happen.");

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

        config.addCustomCategoryComment(researchSettings, "Research Settings Section\n" +
                "Affect both client and server.  These configs must match for client and server, or\n" +
                "strange and probably BAD things WILL happen.");

        config.addCustomCategoryComment(researchDetailSettings, "Research Detail Settings Section\n" +
                "Configure research times per research goal.\n" +
                "Affect both client and server.  These configs must match for client and server, or\n" +
                "strange and probably BAD things WILL happen.");

        config.addCustomCategoryComment(recipeDetailSettings, "Recipe Detail Settings Section\n" +
                "Configure recipe enable/disable per item.\n" +
                "Disabling the recipe effectively disables that item.\n" +
                "Affect both client and server.  These configs must match for client and server, or\n" +
                "strange and probably BAD things WILL happen.");

        config.addCustomCategoryComment(recipeResearchDetails, "Recipe Research Detail Settings Section\n" +
                "Configure enable/disable research for specific recipes.\n" +
                "Disabling the research removes all research requirements for that item.\n" +
                "Affect both client and server.  These configs must match for client and server, or\n" +
                "strange and probably BAD things WILL happen.");
    }

    public static int getResearchTimeFor(String goalName, int defaultTime) {
        Configuration config = AncientWarfareCore.config;
        return config.get(researchDetailSettings, goalName, defaultTime).getInt(defaultTime);
    }

    public static boolean isItemCraftable(Item item) {
        String name = Item.itemRegistry.getNameForObject(item);
        return config.getBoolean(name, recipeDetailSettings, true, "");
    }

    public static boolean isItemResearched(Item item) {
        String name = Item.itemRegistry.getNameForObject(item);
        return config.getBoolean(name, recipeResearchDetails, true, "");
    }

    public static Configuration getConfigFor(String modID) {
        return new Configuration(new File(configPathForFiles, modID + ".cfg"));
    }

}
