/**
 Copyright 2012-2013 John Cummens (aka Shadowmage, Shadowmage4513)
 This software is distributed under the terms of the GNU General Public License.
 Please see COPYING for precise license information.

 This file is part of Ancient Warfare.

 Ancient Warfare is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 Ancient Warfare is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with Ancient Warfare.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.shadowmage.ancientwarfare.npc.config;

import net.minecraft.entity.EntityList;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.shadowmage.ancientwarfare.core.config.AWCoreStatics;
import net.shadowmage.ancientwarfare.core.config.ModConfiguration;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

public class AWNPCStatics extends ModConfiguration {

/**
 * shared settings:
 * NONE?
 */
    /** ********************************************shared SETTINGS************************************************ */
    public static final String sharedSettings = "01_shared_settings";

/**
 * server settings:
 * npc worker tick rate / ticks per work unit
 */
    /** ********************************************SERVER SETTINGS************************************************ */
    public static final String serverSettings = "02_server_settings";
    public static int maxNpcLevel = 10;
    public static int npcXpFromWork = 1;
    public static int npcXpFromTrade = 1;
    public static int npcXpFromAttack = 1;
    public static int npcXpFromKill = 5;
    public static int npcXpFromMoveItem = 1;//TODO add to config
    public static int npcWorkTicks = 50;
    public static int npcDefaultUpkeepWithdraw = 6000;//5 minutes
    public static boolean npcAllowUpkeepAnyInventory = true;
    public static int townMaxRange = 100;
    public static int townUpdateFreq = 100; //5 second broadcast frequency
    public static boolean exportEntityNames = false;
    public static boolean npcAIDebugMode = false;

    /**
     * TODO add these to config
     */
    public static double npcLevelDamageMultiplier = 0.05;//damage bonus per npc level.  @ level 10 they do 2x the damage as at lvl 0
    public static int npcArcherAttackDamage = 3;//damage for npc archers...can be increased via enchanted weapons
    /** ********************************************CLIENT SETTINGS************************************************ */
    public static final String clientSettings = "03_client_settings";
    public static boolean loadDefaultSkinPack = true;

    /** ********************************************RECIPE SETTINGS************************************************ */
    public static final String recipeSettings = "04_recipe_settings";

/************************************************FOOD CONFIG*************************************************/
    /** ********************************************FOOD SETTINGS************************************************ */
    public static final String foodSettings = "01_food_settings";
    private HashMap<String, Integer> foodValues = new HashMap<String, Integer>();
    private int foodMultiplier = 750;

/************************************************TARGET CONFIG*************************************************/
    /** ********************************************TARGET SETTINGS************************************************ */
    public static final String targetSettings = "01_target_settings";
    private HashMap<String, List<String>> entityTargetSettings = new HashMap<String, List<String>>();
    private List<String> entitiesToTargetNpcs = new ArrayList<String>();


/************************************************FACTIONS CONFIG*************************************************/
    /** ********************************************FACTION STARTING VALUE SETTINGS************************************************ */
    public static final String factionSettings = "01_faction_settings";
    public static int factionLossOnDeath = 10;//how much faction standing is lost when you (or one of your npcs) kills an enemy faction-based npc
    public static int factionGainOnTrade = 2;//how much faction standing is gained when you complete a trade with a faction-based trader-npc
    private HashMap<String, Integer> defaultFactionStandings = new HashMap<String, Integer>();
    private HashMap<String, HashMap<String, Boolean>> factionVsFactionStandings = new HashMap<String, HashMap<String, Boolean>>();


/************************************************FACTION NAMES*************************************************/

    public static final String[] factionNames = new String[]{"bandit", "viking", "pirate", "desert", "native", "custom_1", "custom_2", "custom_3"};
    public static final String[] factionNpcSubtypes = new String[]{"soldier", "soldier.elite", "cavalry", "archer", "archer.elite", "mounted_archer", "leader", "leader.elite", "priest", "trader", "civilian.male", "civilian.female", "bard"};


/************************************************NPC CONFIG*************************************************/
    /** ********************************************NPC VALUES SETTINGS************************************************ */
    private HashMap<String, Attribute> attributes = new HashMap<String, Attribute>();

    /** ********************************************NPC PATH SETTINGS************************************************ */
    private HashMap<String, Path> pathValues = new HashMap<String, Path>();

/************************************************EQUIPMENT CONFIG*************************************************/
    /** ********************************************NPC WEAPON SETTINGS************************************************ */

    private static final String npcDefaultWeapons = "01_npc_weapons";
    private static final String npcOffhandItems = "02_npc_offhand";
    private static final String npcArmorHead = "03_npc_helmet";
    private static final String npcArmorChest = "04_npc_chest";
    private static final String npcArmorLegs = "05_npc_legs";
    private static final String npcArmorBoots = "06_npc_boots";
    private static final String npcWorkItem = "07_npc_work_slot";
    private static final String npcUpkeepItem = "08_npc_upkeep_slot";

    private final Configuration equipmentConfig;
    private final Configuration targetConfig;
    private final Configuration valuesConfig;
    private final Configuration foodConfig;
    private final Configuration factionConfig;
    private final Configuration pathConfig;

    public static Property renderAI, renderWorkPoints, renderFriendlyNames, renderHostileNames, renderFriendlyHealth, renderHostileHealth, renderTeamColors;

    public AWNPCStatics(Configuration config) {
        super(config);
        equipmentConfig = AWCoreStatics.getConfigFor("AncientWarfareNpcEquipment");
        targetConfig = AWCoreStatics.getConfigFor("AncientWarfareNpcTargeting");
        valuesConfig = AWCoreStatics.getConfigFor("AncientWarfareNpcValues");
        pathConfig = AWCoreStatics.getConfigFor("AncientWarfareNpcPath");
        foodConfig = AWCoreStatics.getConfigFor("AncientWarfareNpcFood");
        factionConfig = AWCoreStatics.getConfigFor("AncientWarfareNpcFactionStandings");
    }

    @Override
    public void initializeCategories() {
        config.addCustomCategoryComment(sharedSettings, "General Options\n" +
                "Affect both client and server.  These configs must match for client and server, or\n" +
                "strange and probably BAD things WILL happen.");

        config.addCustomCategoryComment(serverSettings, "Server Options\n" +
                "Affect only server-side operations.  Will need to be set for dedicated servers, and single\n" +
                "player (or LAN worlds).  Clients playing on remote servers can ignore these settings.");

        config.addCustomCategoryComment(clientSettings, "Client Options\n" +
                "Affect only client-side operations.  Many of these options can be set from the in-game Options GUI.\n" +
                "Server admins can ignore these settings.");

        config.addCustomCategoryComment(recipeSettings, "Recipe Options\n" +
                "Enable / Disable specific recipes, or remove the research requirements from specific recipes.\n" +
                "Affect only server-side operations.  Will need to be set for dedicated servers, and single\n" +
                "player (or LAN worlds).  Clients playing on remote servers can ignore these settings.");

        foodConfig.addCustomCategoryComment(foodSettings, "Food Value Options\n" +
                "The value specified is the number of ticks that the item will feed the NPC for.\n" +
                "Add a new line for each item. The item type is not checked, and the default multiplier is not applied.\n" +
                "0 or under will make the item unusable as a food.\n" +
                "Affect only server-side operations.  Will need to be set for dedicated servers, and single\n" +
                "player (or LAN worlds).  Clients playing on remote servers can ignore these settings.");

        targetConfig.addCustomCategoryComment(targetSettings, "Custom NPC Targeting Options\n" +
                "Add / remove vanilla / mod-added entities from the NPC targeting lists.\n" +
                "Affect only server-side operations.  Will need to be set for dedicated servers, and single\n" +
                "player (or LAN worlds).  Clients playing on remote servers can ignore these settings.");

        factionConfig.addCustomCategoryComment(factionSettings, "Faction Options\n" +
                "Set starting faction values, and alter the amount of standing gained/lost from player actions.\n" +
                "Affect only server-side operations.  Will need to be set for dedicated servers, and single\n" +
                "player (or LAN worlds).  Clients playing on remote servers can ignore these settings.");

        equipmentConfig.addCustomCategoryComment(npcDefaultWeapons, "Default Equipped Weapons\n");//TODO comment
        equipmentConfig.addCustomCategoryComment(npcOffhandItems, "Default Equipped Offhand Items\n");//TODO comment
        equipmentConfig.addCustomCategoryComment(npcArmorHead, "Default Equipped Helmets\n");//TODO comment
        equipmentConfig.addCustomCategoryComment(npcArmorChest, "Default Equipped Chest Armor\n");//TODO comment
        equipmentConfig.addCustomCategoryComment(npcArmorLegs, "Default Equipped Leg Armor\n");//TODO comment
        equipmentConfig.addCustomCategoryComment(npcArmorBoots, "Default Equipped Foot Armor\n");//TODO comment
        equipmentConfig.addCustomCategoryComment(npcWorkItem, "Default Equipped Order Item (drop-on-death only)\n");//TODO comment
        equipmentConfig.addCustomCategoryComment(npcUpkeepItem, "Default Equipped Upkeep Item (drop-on-death only)\n");//TODO comment
    }

    @Override
    public void initializeValues() {
        loadFoodValues();
        loadTargetValues();
        loadDefaultFactionStandings();
        initializeCustomValues();
        initializeNpcEquipmentConfigs();

        maxNpcLevel = config.get(serverSettings, "npc_max_level", maxNpcLevel, "Max NPC Level\nDefault=" + maxNpcLevel + "\n" +
                "How high can NPCs level up?  Npcs gain more health, attack damage, and overall\n" +
                "improved stats with each level.  Levels can go very high, but higher values may\n" +
                "result in overpowered NPCs once leveled up.").getInt();

        npcXpFromAttack = config.get(serverSettings, "npc_xp_per_attack", npcXpFromAttack, "XP Per Attack\nDefault=" + npcXpFromAttack + "\n" +
                "How much xp should an NPC gain each time they damage but do not kill an enemy?\n" +
                "Higher values will result in faster npc leveling.\n" +
                "Applies to both player-owned and faction-based NPCs.").getInt();

        npcXpFromKill = config.get(serverSettings, "npc_xp_per_kill", npcXpFromKill, "XP Per Killnefault=" + npcXpFromKill + "\n" +
                "How much xp should an NPC gain each time they kill an enemy?\n" +
                "Higher values will result in faster npc leveling.\n" +
                "Applies to both player-owned and faction-based NPCs.").getInt();

        npcXpFromTrade = config.get(serverSettings, "npc_xp_per_trade", npcXpFromTrade, "XP Per Trade\nDefault=" + npcXpFromTrade + "\n" +
                "How much xp should an NPC gain each time successfully traded with?\n" +
                "Higher values will result in faster npc leveling and unlock more trade recipes.\n" +
                "Applies to both player-owned and faction-based NPCs.").getInt();

        npcXpFromWork = config.get(serverSettings, "npc_xp_per_work", npcXpFromWork, "XP Per Work\nDefault=" + npcXpFromWork + "\n" +
                "How much xp should an NPC gain each time it works at a worksite?\n" +
                "Higher values will result in faster npc leveling.\n" +
                "Applies to player-owned NPCs only.").getInt();

        npcXpFromMoveItem = config.get(serverSettings, "npc_xp_per_item_moved", npcXpFromMoveItem, "XP Per Courier\nDefault=" + npcXpFromMoveItem + "\n" +
                "How much xp should an NPC gain each time it moves an item?\n" +
                "Higher values will result in faster npc leveling.\n" +
                "Applies to player-owned NPCs only.").getInt();

        npcWorkTicks = config.get(serverSettings, "npc_work_ticks", npcWorkTicks, "Time Between Work Ticks\nDefault=" + npcWorkTicks + "\n" +
                "How many game ticks should pass between workers' processing work at a work-site.\n" +
                "Lower values result in more work output, higher values result in less work output.").getInt();

        npcAllowUpkeepAnyInventory = config.get(serverSettings, "allow_upkeep_any_inventory", npcAllowUpkeepAnyInventory, "Allow NPC upkeep location at any inventory\nDefault=" + npcAllowUpkeepAnyInventory + "\n" +
                "By default, the Upkeep Order slip can be used to assign upkeep locations to any valid inventory block.\n" +
                "If set to false, only Town Hall blocks will be allowed as valid upkeep locations.").getBoolean();
        
        townMaxRange = config.get(serverSettings, "town_hall_max_range", townMaxRange, "Town Hall Max Activation Range\nDefault=" + townMaxRange + "\n" +
                "How many blocks can a Town Hall be away from an NPC, while still detecting their death for possible resurrection.\n" +
                "This is a maximum, for server efficiency sake. Lower individual values can be setup from each block interaction GUI.").getInt();

        townUpdateFreq = config.get(serverSettings, "town_hall_ticks", townUpdateFreq, "Default=" + townUpdateFreq + "\n" +
                "How many game ticks should pass between Town Hall updates." +
                "This affect how an NPC can change its selected Town Hall by moving to different places.\n" +
                "Lower values will make an NPC change its Town Hall faster, but is more costly for a server.").getInt();

        factionLossOnDeath = factionConfig.get(factionSettings, "faction_loss_on_kill", factionLossOnDeath, "Faction Loss On Kill\nDefault=10\n" +
                "How much faction standing should be lost if you or one of your minions kills a faction based NPC.").getInt();

        factionGainOnTrade = factionConfig.get(factionSettings, "faction_gain_on_trade", factionGainOnTrade, "Faction Gain On Trade\nDefault=2\n" +
                "How much faction standing should be gained when you trade with a faction based trader.").getInt();

        loadDefaultSkinPack = config.get(clientSettings, "load_default_skin_pack", loadDefaultSkinPack, "Load Default Skin Pack\nDefault=true\n" +
                "If true, default skin pack will be loaded.\n" +
                "If false, default skin pack will NOT be loaded -- you will need to supply your own\n" +
                "skin packs or all npcs will use the default skin.").getBoolean();

        exportEntityNames = config.get(serverSettings, "export_entity_names", exportEntityNames, "Export entity name list\nDefault=" + exportEntityNames + "\n" +
                "If true, a text file will be created in the main ancientwarfare config directory containing a list of all registered in-game entity names.\n" +
                "These names may be used to populate the NPC target lists.").getBoolean();

        renderAI = config.get(clientSettings, "render_npc_ai", true);
        renderWorkPoints = config.get(clientSettings, "render_work_points", true);
        renderFriendlyNames = config.get(clientSettings, "render_friendly_nameplates", true);
        renderHostileNames = config.get(clientSettings, "render_hostile_nameplates", true);
        renderFriendlyHealth = config.get(clientSettings, "render_friendly_health", true);
        renderHostileHealth = config.get(clientSettings, "render_hostile_health", true);
        renderTeamColors = config.get(clientSettings, "render_team_colors", true);
        if(this.config.hasChanged())
            this.config.save();
    }

    public void postInitCallback() {
        if (exportEntityNames) {
            File file = new File("config/ancientwarfare");
            file.mkdirs();
            file = new File(file, "entity_names.txt");
            FileWriter wr = null;
            try {
                wr = new FileWriter(file);
                for (Object obj : EntityList.stringToClassMapping.keySet()) {
                    wr.write(String.valueOf(obj) + "\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (wr != null) {
                    try {
                        wr.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void loadTargetValues() {
        String[] defaultTargets = new String[]{"Zombie", "Skeleton", "Slime"};
        String[] targets;

        targets = targetConfig.get(targetSettings, "combat.targets", defaultTargets, "Default targets for: unassigned combat npc").getStringList();
        addTargetMapping("combat", "", targets);

        targets = targetConfig.get(targetSettings, "combat.archer.targets", defaultTargets, "Default targets for: player-owned archer").getStringList();
        addTargetMapping("combat", "archer", targets);

        targets = targetConfig.get(targetSettings, "combat.soldier.targets", defaultTargets, "Default targets for: player-owned soldier").getStringList();
        addTargetMapping("combat", "soldier", targets);

        targets = targetConfig.get(targetSettings, "combat.leader.targets", defaultTargets, "Default targets for: player-owned leader npc").getStringList();
        addTargetMapping("combat", "leader", targets);

        targets = targetConfig.get(targetSettings, "combat.medic.targets", defaultTargets, "Default targets for: player-owned medic npc").getStringList();
        addTargetMapping("combat", "medic", targets);

        targets = targetConfig.get(targetSettings, "combat.engineer.targets", defaultTargets, "Default targets for: player-owned engineer npc").getStringList();
        addTargetMapping("combat", "engineer", targets);

        for (String name : factionNames) {
            for(String sub : factionNpcSubtypes){
                targets = targetConfig.get(targetSettings, name + "." + sub + ".targets", defaultTargets, "Default targets for: " + name + " "+ asName(sub)).getStringList();
                addTargetMapping(name, sub, targets);
            }
        }

        targets = targetConfig.get(targetSettings, "enemies_to_target_npcs", defaultTargets, "What mob types should have AI inserted to enable them to target NPCs?\n" +
                "Should work with any new-ai enabled mob type; vanilla or mod-added (but might not work with mod-added entities with custom AI).").getStringList();

        Collections.addAll(entitiesToTargetNpcs, targets);
    }

    private String asName(String npcSubtype){
        String[] txt = npcSubtype.split("\\.");
        StringBuilder build = new StringBuilder();
        for(int i = txt.length - 1; i >= 0; i--){
            build.append(txt[i]).append(" ");
        }
        return build.replace(build.length()-1, build.length(), "s").toString().replace("_", " ");
    }

    private void addTargetMapping(String npcType, String npcSubtype, String[] targets) {
        String type = npcType + (npcSubtype.isEmpty() ? "" : "." + npcSubtype);
        List<String> collection;
        if (!entityTargetSettings.containsKey(type)) {
            collection = new ArrayList<String>();
        }else{
            collection = entityTargetSettings.get(type);
        }
        Collections.addAll(collection, targets);
        entityTargetSettings.put(type, collection);
    }

    public boolean shouldEntityTargetNpcs(String entityName) {
        return entitiesToTargetNpcs.contains(entityName);
    }

    public List<String> getValidTargetsFor(String npcType, String npcSubtype) {
        String type = npcType + (npcSubtype.isEmpty() ? "" : "." + npcSubtype);
        if (entityTargetSettings.containsKey(type)) {
            return entityTargetSettings.get(type);
        }
        return Collections.emptyList();
    }

    private void loadFoodValues() {
        foodMultiplier = foodConfig.getInt("Food Multiplier", "Default", foodMultiplier, 0, Integer.MAX_VALUE/10, "Food items which don't have a custom duration time set will have their nourishing amount multiplied by this number, to get the number of ticks feeding the npc.");
        foodConfig.get(foodSettings, "minecraft:apple", 3000, "Example of a food usual tick duration. Default food multiplier included.");
        foodConfig.get(foodSettings, "minecraft:mushroom_stew", 4500, "Example of a food usual tick duration. Default food multiplier included.");
        foodConfig.get(foodSettings, "minecraft:rotten_flesh", 0, "Rotten flesh is a rejected food by default.");
        foodConfig.get(foodSettings, "minecraft:poisonous_potato", 0, "Poisonous potato is a rejected food by default.");
        foodConfig.get(foodSettings, "minecraft:spider_eye", 0, "Spider eye is a rejected food by default.");

        ConfigCategory category = foodConfig.getCategory(foodSettings);

        String name;
        int value;
        for (Entry<String, Property> entry : category.entrySet()) {
            name = entry.getKey();
            value = entry.getValue().getInt(0);
            foodValues.put(name, value);
        }
    }

    private void loadDefaultFactionStandings() {
        String key;
        boolean val;
        for (String name : factionNames) {
            if (!this.factionVsFactionStandings.containsKey(name)) {
                this.factionVsFactionStandings.put(name, new HashMap<String, Boolean>());
            }
            this.defaultFactionStandings.put(name, factionConfig.get(factionSettings, name + ".starting_faction_standing", -50,
                    "Default faction standing for: [" + name + "] for new players joining a game." +
                            " Less than 0 will be hostile, greater than or equal to zero will be neutral/friendly." +
                            " Default value is -50 for all factions, starting all players with a minor hostile standing." +
                            " Players will need to trade with faction-owned traders to improve their standing to become friendly.").getInt());
            for (String name2 : factionNames) {
                if (name.equals(name2)) {
                    continue;
                }
                key = name + ":" + name2;
                val = factionConfig.get(factionSettings, key, false, "How does: " + name + " faction view: " + name2 + " faction?\n" +
                        "If true, " + name + "s will be hostile towards " + name2 + "s").getBoolean();
                this.factionVsFactionStandings.get(name).put(name2, val);
            }
        }
    }

    /**
     * returns the food value for a single size stack of the input item stack
     */
    public int getFoodValue(ItemStack stack) {
        if (stack == null || stack.getItem() == null) {
            return 0;
        }
        String name = Item.itemRegistry.getNameForObject(stack.getItem());
        if (foodValues.containsKey(name)) {
            return foodValues.get(name);
        }else if(stack.getItem() instanceof ItemFood){
            return ((ItemFood) stack.getItem()).func_150905_g(stack) * foodMultiplier;
        }
        return 0;
    }

    public int getDefaultFaction(String factionName) {
        if (defaultFactionStandings.containsKey(factionName)) {
            return defaultFactionStandings.get(factionName);
        }
        return 0;
    }

    private void initializeCustomValues() {
        String key;
        for (String name : factionNames) {
            for (String type : factionNpcSubtypes) {
                key = name + "." + type;
                attributes.put(key, getDefault(key));
                pathValues.put(key, getDefaultPath(key));
            }
        }
        attributes.put("combat", getDefault("combat"));
        attributes.put("worker", getDefault("worker"));
        attributes.put("courier", getDefault("courier"));
        attributes.put("trader", getDefault("trader"));
        attributes.put("priest", getDefault("priest"));
        attributes.put("bard", getDefault("bard"));
        pathValues.put("combat", getDefaultPath("combat"));
        pathValues.put("worker", getDefaultPath("worker"));
        pathValues.put("courier", getDefaultPath("courier"));
        pathValues.put("trader", getDefaultPath("trader"));
        pathValues.put("priest", getDefaultPath("priest"));
        pathValues.put("bard", getDefaultPath("bard"));
    }

    //TODO check what entity speed is needed / feels right. perhaps vary depending upon level or type
    private Attribute getDefault(String type) {
        return new Attribute(valuesConfig.get("01_npc_base_health", type, 20).getDouble(), valuesConfig.get("02_npc_base_attack", type, 1).getDouble(), valuesConfig.get("03_npc_base_speed", type, 0.325D).getDouble(), valuesConfig.get("05_npc_base_range", type, 60).getDouble(), valuesConfig.get("04_npc_exp_drop", type, 0).getInt());
    }

    public double getMaxHealthFor(String type) {
        return attributes.get(type).baseHealth();
    }

    public double getAttack(NpcBase npcBase) {
        String type = npcBase.getNpcType();
        Attribute attribute = attributes.get(type);
        if (attribute != null) {
            double dmg = attribute.baseAttack();
            int level = npcBase.getLevelingStats().getLevel();
            return dmg + dmg * level * npcLevelDamageMultiplier;
        }
        return 0;
    }

    private Path getDefaultPath(String key){
        return new Path(pathConfig.get("01_npc_path_avoidWater", key, false).getBoolean(), pathConfig.get("02_npc_path_breakDoors", key, true).getBoolean());
    }

    public void applyAttributes(NpcBase npc) {
        Attribute type = attributes.get(npc.getNpcType());
        if (type != null) {
            npc.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(type.baseHealth());
            npc.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(type.baseSpeed());
            npc.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(type.baseAttack());
            npc.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(type.baseRange());
            npc.setExperienceDrop(type.expDrop());
        }
    }

    public void applyPathConfig(NpcBase npc) {
        Path path = pathValues.get(npc.getNpcType());
        if (path != null) {
            path.applyTo(npc.getNavigator());
        }
    }

    public boolean shouldFactionBeHostileTowards(String faction1, String faction2) {
        if (factionVsFactionStandings.containsKey(faction1) && factionVsFactionStandings.get(faction1).containsKey(faction2)) {
            return factionVsFactionStandings.get(faction1).get(faction2);
        }
        return false;
    }

    private HashMap<String, String[]> eqmp = new HashMap<String, String[]>();

    public void initializeNpcEquipmentConfigs() {
        String fullType;
        for (String faction : factionNames) {
            for (String type : factionNpcSubtypes) {
                fullType = faction + "." + type;
                eqmp.put(fullType, new String[8]);//allocate empty string array for each npc type to hold item names for their equipment
            }
            eqmp.get(faction + ".soldier")[0] = "minecraft:iron_sword";
            eqmp.get(faction + ".soldier.elite")[0] = "minecraft:iron_sword";
            eqmp.get(faction + ".cavalry")[0] = "minecraft:iron_sword";
            eqmp.get(faction + ".archer")[0] = "minecraft:bow";
            eqmp.get(faction + ".archer.elite")[0] = "minecraft:bow";
            eqmp.get(faction + ".mounted_archer")[0] = "minecraft:bow";
            eqmp.get(faction + ".leader")[0] = "minecraft:diamond_sword";
            eqmp.get(faction + ".leader.elite")[0] = "minecraft:diamond_sword";
            eqmp.get(faction + ".trader")[0] = "minecraft:book";
            eqmp.get(faction + ".priest")[0] = "minecraft:book";
            eqmp.get(faction + ".bard")[0] = "AncientWarfareNpc:bard_instrument";
        }

        String[] array;
        String item;
        for (String key : eqmp.keySet()) {
            array = eqmp.get(key);
            item = array[0];
            item = item == null ? "null" : item;
            item = equipmentConfig.get(npcDefaultWeapons, key, item).getString();
            array[0] = item;

            item = array[4];
            item = item == null ? "null" : item;
            item = equipmentConfig.get(npcArmorHead, key, item).getString();
            array[4] = item;

            item = array[3];
            item = item == null ? "null" : item;
            item = equipmentConfig.get(npcArmorChest, key, item).getString();
            array[3] = item;

            item = array[2];
            item = item == null ? "null" : item;
            item = equipmentConfig.get(npcArmorLegs, key, item).getString();
            array[2] = item;

            item = array[1];
            item = item == null ? "null" : item;
            item = equipmentConfig.get(npcArmorBoots, key, item).getString();
            array[1] = item;

            item = array[5];
            item = item == null ? "null" : item;
            item = equipmentConfig.get(npcWorkItem, key, item).getString();
            array[5] = item;

            item = array[6];
            item = item == null ? "null" : item;
            item = equipmentConfig.get(npcUpkeepItem, key, item).getString();
            array[6] = item;

            item = array[7];
            item = item == null ? "null" : item;
            item = equipmentConfig.get(npcOffhandItems, key, item).getString();
            array[7] = item;
        }
    }

    public ItemStack getStartingEquipmentForSlot(String type, int slot) {
        String itemName = null;
        if (eqmp.containsKey(type)) {
            itemName = eqmp.get(type)[slot];
        }
        if (itemName != null && !itemName.isEmpty() && !itemName.equals("null")) {
            Item item = (Item) Item.itemRegistry.getObject(itemName);
            if (item != null) {
                return new ItemStack(item);
            }
        }
        return null;
    }

    public void save() {
        if(config.hasChanged())
            config.save();
        equipmentConfig.save();
        targetConfig.save();
        valuesConfig.save();
        pathConfig.save();
        foodConfig.save();
        factionConfig.save();
    }

    private static class Path {
        private final boolean avoidWater, breakDoor;

        private Path(boolean water, boolean door) {
            avoidWater = water;
            breakDoor = door;
        }

        public void applyTo(PathNavigate navigate) {
            navigate.setAvoidsWater(avoidWater);
            navigate.setBreakDoors(breakDoor);
        }
    }

    private static class Attribute {
        private final double health, attack, speed, range;
        private final int exp;

        private Attribute(double hp, double ap, double sp, double rg, int xp) {
            health = hp;
            attack = ap;
            speed = sp;
            range = rg;
            exp = xp;
        }

        public double baseHealth() {
            return health;
        }
        //base attack damage for npcs--further multiplied by their equipped weapon
        public double baseAttack() {
            return attack;
        }

        public double baseRange(){
            return range;
        }

        public double baseSpeed() {
            return speed;
        }

        public int expDrop(){
            return exp;
        }
    }

}
