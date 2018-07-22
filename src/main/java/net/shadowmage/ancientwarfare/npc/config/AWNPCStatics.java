/*
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

import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.shadowmage.ancientwarfare.core.config.ModConfiguration;
import net.shadowmage.ancientwarfare.npc.registry.FactionRegistry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

public class AWNPCStatics extends ModConfiguration {

	/* ********************************************SHARED SETTINGS************************************************ */
	public static int npcActionRange = 3;
	public static boolean repackCreativeOnly = false;
	public static boolean persistOrdersOnDeath = true;

	/* ********************************************SERVER SETTINGS************************************************ */
	public static int maxNpcLevel = 10;
	public static int npcXpFromWork = 1;
	public static int npcXpFromTrade = 1;
	public static int npcXpFromAttack = 1;
	public static int npcXpFromKill = 5;
	public static int npcXpFromMoveItem = 1;
	public static int npcWorkTicks = 50;
	public static int npcDefaultUpkeepWithdraw = 6000;//5 minutes
	public static boolean npcAllowUpkeepAnyInventory = true;
	public static int townMaxRange = 100;
	public static int townUpdateFreq = 100; //5 second broadcast frequency
	public static boolean npcAIDebugMode = false;
	public static double archerRange = 16.0;

	/*
	 * TODO add these to config
	 */
	public static double npcLevelDamageMultiplier = 0.05;//damage bonus per npc level.  @ level 10 they do 2x the damage as at lvl 0
	/* ********************************************CLIENT SETTINGS************************************************ */
	public static boolean loadDefaultSkinPack = true;

	public static Property renderAI, renderWorkPoints, renderFriendlyNames, renderHostileNames, renderFriendlyHealth, renderHostileHealth, renderTeamColors;

	/* ********************************************FOOD SETTINGS************************************************ */
	private Configuration foodConfig;
	private static final String foodSettings = "01_food_settings";
	private HashMap<String, Integer> foodValues;
	private int foodMultiplier = 750;

	/* ********************************************TARGET SETTINGS************************************************ */
	private Configuration targetConfig;
	private static final String targetSettings = "00_new_targeting_settings";
	private static final String targetSettingsLegacy = "01_target_settings";
	private HashMap<String, List<String>> entityTargetSettings;
	private List<String> entitiesToTargetNpcs;
	public static boolean autoTargetting = true;
	public static boolean autoTargettingConfigLos = true;
	private ArrayList<String> autoTargettingMobExclude;

	/* ********************************************FACTION STARTING VALUE SETTINGS************************************************ */
	private Configuration factionConfig;
	private static final String factionSettings = "01_faction_settings";
	public static int factionLossOnDeath = 10;//how much faction standing is lost when you (or one of your npcs) kills an enemy faction-based npc
	public static int factionGainOnTrade = 2;//how much faction standing is gained when you complete a trade with a faction-based trader-npc

	public AWNPCStatics(String mod) {
		super(mod);
	}

	@Override
	public void initializeCategories() {
		config.addCustomCategoryComment(generalOptions, "General Options\n" + "Affect both client and server.  These configs must match for client and server, or\n" + "strange and probably BAD things WILL happen.");

		config.addCustomCategoryComment(serverOptions, "Server Options\n" + "Affect only server-side operations.  Will need to be set for dedicated servers, and single\n" + "player (or LAN worlds).  Clients playing on remote servers can ignore these settings.");

		config.addCustomCategoryComment(clientOptions, "Client Options\n" + "Affect only client-side operations.  Many of these options can be set from the in-game Options GUI.\n" + "Server admins can ignore these settings.");

		foodConfig = getConfigFor("AncientWarfareNpcFood");
		foodConfig.addCustomCategoryComment(foodSettings, "Food Value Options\n" + "The value specified is the number of ticks that the item will feed the NPC for.\n" + "Add a new line for each item. The item type is not checked, and the default multiplier is not applied.\n" + "0 or under will make the item unusable as a food.\n" + "Affect only server-side operations.  Will need to be set for dedicated servers, and single\n" + "player (or LAN worlds).  Clients playing on remote servers can ignore these settings.");

		targetConfig = getConfigFor("AncientWarfareNpcTargeting");
		targetConfig.addCustomCategoryComment(targetSettings, "NPC Targeting/AI Settings\n" + "Define the logic for mob and NPC target AI (i.e. attacking and fleeing) here.\n" + "Affects only server-side operations.  Will need to be set for dedicated servers, and single\n" + "player (or LAN worlds).  Clients playing on remote servers can ignore these settings.");

		targetConfig.addCustomCategoryComment(targetSettingsLegacy, "Legacy NPC Targeting Lists\n" + "Add / remove entities from the NPC targeting lists. All settings here have no effect unless 'auto_targetting' is\n" + "set to false in the section above. \n" + "Affect only server-side operations.  Will need to be set for dedicated servers, and single\n" + "player (or LAN worlds).  Clients playing on remote servers can ignore these settings.");

		factionConfig = getConfigFor("AncientWarfareNpcFactionStandings");
		factionConfig.addCustomCategoryComment(factionSettings, "Faction Options\n" + "Set starting faction values, and alter the amount of standing gained/lost from player actions.\n" + "Affect only server-side operations.  Will need to be set for dedicated servers, and single\n" + "player (or LAN worlds).  Clients playing on remote servers can ignore these settings.");
	}

	@Override
	public void initializeValues() {
		loadFoodValues();
		loadTargetValues();

		maxNpcLevel = config.get(serverOptions, "npc_max_level", maxNpcLevel, "Max NPC Level\nDefault=" + maxNpcLevel + "\n" + "How high can NPCs level up?  Npcs gain more health, attack damage, and overall\n" + "improved stats with each level.  Levels can go very high, but higher values may\n" + "result in overpowered NPCs once leveled up.").getInt();

		npcXpFromAttack = config.get(serverOptions, "npc_xp_per_attack", npcXpFromAttack, "XP Per Attack\nDefault=" + npcXpFromAttack + "\n" + "How much xp should an NPC gain each time they damage but do not kill an enemy?\n" + "Higher values will result in faster npc leveling.\n" + "Applies to both player-owned and faction-based NPCs.").getInt();

		npcXpFromKill = config.get(serverOptions, "npc_xp_per_kill", npcXpFromKill, "XP Per Killnefault=" + npcXpFromKill + "\n" + "How much xp should an NPC gain each time they kill an enemy?\n" + "Higher values will result in faster npc leveling.\n" + "Applies to both player-owned and faction-based NPCs.").getInt();

		npcXpFromTrade = config.get(serverOptions, "npc_xp_per_trade", npcXpFromTrade, "XP Per Trade\nDefault=" + npcXpFromTrade + "\n" + "How much xp should an NPC gain each time successfully traded with?\n" + "Higher values will result in faster npc leveling and unlock more trade recipes.\n" + "Applies to both player-owned and faction-based NPCs.").getInt();

		npcXpFromWork = config.get(serverOptions, "npc_xp_per_work", npcXpFromWork, "XP Per Work\nDefault=" + npcXpFromWork + "\n" + "How much xp should an NPC gain each time it works at a worksite?\n" + "Higher values will result in faster npc leveling.\n" + "Applies to player-owned NPCs only.").getInt();

		npcXpFromMoveItem = config.get(serverOptions, "npc_xp_per_item_moved", npcXpFromMoveItem, "XP Per Courier\nDefault=" + npcXpFromMoveItem + "\n" + "How much xp should an NPC gain each time it moves an item?\n" + "Higher values will result in faster npc leveling.\n" + "Applies to player-owned NPCs only.").getInt();

		npcWorkTicks = config.get(serverOptions, "npc_work_ticks", npcWorkTicks, "Time Between Work Ticks\nDefault=" + npcWorkTicks + "\n" + "How many game ticks should pass between workers' processing work at a work-site.\n" + "Lower values result in more work output, higher values result in less work output.").getInt();

		npcAllowUpkeepAnyInventory = config.get(serverOptions, "allow_upkeep_any_inventory", npcAllowUpkeepAnyInventory, "Allow NPC upkeep location at any inventory\nDefault=" + npcAllowUpkeepAnyInventory + "\n" + "By default, the Upkeep Order slip can be used to assign upkeep locations to any valid inventory block.\n" + "If set to false, only Town Hall blocks will be allowed as valid upkeep locations.").getBoolean();

		townMaxRange = config.get(serverOptions, "town_hall_max_range", townMaxRange, "Town Hall Max Activation Range\nDefault=" + townMaxRange + "\n" + "How many blocks can a Town Hall be away from an NPC, while still detecting their death for possible resurrection.\n" + "This is a maximum, for server efficiency sake. Lower individual values can be setup from each block interaction GUI.").getInt();

		townUpdateFreq = config.get(serverOptions, "town_hall_ticks", townUpdateFreq, "Default=" + townUpdateFreq + "\n" + "How many game ticks should pass between Town Hall updates." + "This affect how an NPC can change its selected Town Hall by moving to different places.\n" + "Lower values will make an NPC change its Town Hall faster, but is more costly for a server.\n").getInt();

		factionLossOnDeath = factionConfig.get(factionSettings, "faction_loss_on_kill", factionLossOnDeath, "Faction Loss On Kill\nDefault=10\n" + "How much faction standing should be lost if you or one of your minions kills a faction based NPC.").getInt();

		factionGainOnTrade = factionConfig.get(factionSettings, "faction_gain_on_trade", factionGainOnTrade, "Faction Gain On Trade\nDefault=2\n" + "How much faction standing should be gained when you trade with a faction based trader.").getInt();

		loadDefaultSkinPack = config.get(clientOptions, "load_default_skin_pack", loadDefaultSkinPack, "Load Default Skin Pack\nDefault=true\n" + "If true, default skin pack will be loaded.\n" + "If false, default skin pack will NOT be loaded -- you will need to supply your own\n" + "skin packs or all npcs will use the default skin.").getBoolean();

		archerRange = config.get(serverOptions, "archer_attack_range", archerRange, "Archer attack range\nDefault=" + archerRange + "\n" + "Attack range of all archers, except mounted archers who are half of this value.").getDouble();

		renderAI = config.get(clientOptions, "render_npc_ai", true);
		renderWorkPoints = config.get(clientOptions, "render_work_points", true);
		renderFriendlyNames = config.get(clientOptions, "render_friendly_nameplates", true);
		renderHostileNames = config.get(clientOptions, "render_hostile_nameplates", true);
		renderFriendlyHealth = config.get(clientOptions, "render_friendly_health", true);
		renderHostileHealth = config.get(clientOptions, "render_hostile_health", true);
		renderTeamColors = config.get(clientOptions, "render_team_colors", true);

		npcActionRange = config.get(generalOptions, "npc_action_range", npcActionRange, "Action Range\nDefault=" + npcActionRange + "\n" + "The range in blocks that an NPC can perform an action on something. The player has an action\n" + "range of 5. Only affects workers, no effect on the attack range of combat units nor medics.\n" + "Minimum value of 3 unless you want NPC's to bug-out and get stuck at random.").getInt();

		repackCreativeOnly = config.get(generalOptions, "npc_repack_creative_only", repackCreativeOnly, "Repack only available for Creative players?\nDefault=" + repackCreativeOnly + "\n" + "If true, the 'Repack' option for NPC's will be unavailable outside of Creative mode.").getBoolean();

		persistOrdersOnDeath = config.get(generalOptions, "npc_death_keep_orders_items", persistOrdersOnDeath, "NPC's will keep orders items on death?\nDefault=" + persistOrdersOnDeath + "\n" + "If true, an NPC who dies and manages to notify a nearby town hall will keep their orders items on their body. So if/when a priest resurrects them, they will have the orders items on them still. If there is no Town Hall nearby to catch the death however, the will drop on the ground as normal.").getBoolean();
	}

	private void loadTargetValues() {
		String[] targets;

		autoTargetting = targetConfig.get(targetSettings, "auto_targetting", autoTargetting, "Use new automatic AI injection for hostile entities. If true, anything that uses the vanilla \n" + "'New AI' tasks and is hostile to the player will automagically be injected with hostility for all NPC's too.\n" + "Will not work for mods which use custom AI tasks, nor 'Old AI' mobs e.g. Blazes, Spiders, etc. \n" + "NOTE! Setting this to false will completely disable this section, and revert to the old behavior (see next section).").getBoolean();

		autoTargettingConfigLos = targetConfig.get(targetSettings, "auto_targetting.config.los", autoTargettingConfigLos, "Auto AI injection requires line-of-sight targetting?\n" + "With old method, mobs did not need line-of-sight before they decided to chase an NPC. \n" + "With the new AI they do need LOS by default. Disable it here if you want for some reason.").getBoolean();

		targets = targetConfig.get(targetSettings, "auto_targetting.exclude", new String[] {EntityRegistry.getEntry(EntityCreeper.class).getName()}, "Exclude these entities from auto-injection, i.e. 'force passive'. Any entities listed here\n" + "will *not* target NPC's, and NPC's in turn will not be alarmed by these entities.\n" +
				//"Note that the mob will only be excluded if it is also NOT listed on the include list below.\n" +
				"Check the AncientWarfareNpc.cfg for a setting to export entity names.").getStringList();
		autoTargettingMobExclude = new ArrayList<>();
		Collections.addAll(autoTargettingMobExclude, targets);

		// old stuff from here on
		if (!autoTargetting) {
			entityTargetSettings = new HashMap<>();
			String[] defaultTargets = new String[] {"Zombie", "Skeleton", "Slime"};

			targets = targetConfig.get(targetSettingsLegacy, "enemies_to_target_npcs", defaultTargets, "What mob types should have AI inserted to enable them to target NPCs?\n" + "Should work with any new-ai enabled mob type; vanilla or mod-added (but might not work with mod-added entities with custom AI).\n" + "NOTE! This is a LEGACY option! This option ONLY works if the 'auto_inject_mobs' option at the top is changed to false.").getStringList();
			entitiesToTargetNpcs = new ArrayList<>();
			Collections.addAll(entitiesToTargetNpcs, targets);
		}
	}

	public int getPlayerDefaultStanding(String factionName) {
		return factionConfig.get(factionSettings, factionName + ".starting_faction_standing", FactionRegistry.getFaction(factionName).getPlayerDefaultStanding(),
				"Default faction standing for: [" + factionName + "] for new players joining a game." +
						" Less than 0 will be hostile, greater than or equal to zero will be neutral/friendly." +
						" Players will need to trade with faction-owned traders to improve their standing to become friendly.").getInt();
	}

	/*
	 * Used for determining if AI injection should be done. To check if the entity
	 * already DOES have the desire to attack NPC's, use NpcAI.doesTargetNpcs instead.
	 */
	public boolean shouldEntityTargetNpcs(String entityName) {
		if (!autoTargetting) // old targetting in use
			return entitiesToTargetNpcs.contains(entityName);
		return (!autoTargettingMobExclude.contains(entityName));
	}

	private void loadFoodValues() {
		foodMultiplier = foodConfig.getInt("Food Multiplier", "Default", foodMultiplier, 0, Integer.MAX_VALUE / 10, "Food items which don't have a custom duration time set will have their nourishing amount multiplied by this number, to get the number of ticks feeding the npc.");
		foodConfig.get(foodSettings, "minecraft:apple", 3000, "Example of a food usual tick duration. Default food multiplier included.");
		foodConfig.get(foodSettings, "minecraft:mushroom_stew", 4500, "Example of a food usual tick duration. Default food multiplier included.");
		foodConfig.get(foodSettings, "minecraft:rotten_flesh", 0, "Rotten flesh is a rejected food by default.");
		foodConfig.get(foodSettings, "minecraft:poisonous_potato", 0, "Poisonous potato is a rejected food by default.");
		foodConfig.get(foodSettings, "minecraft:spider_eye", 0, "Spider eye is a rejected food by default.");

		ConfigCategory category = foodConfig.getCategory(foodSettings);
		foodValues = new HashMap<>();
		String name;
		int value;
		for (Entry<String, Property> entry : category.entrySet()) {
			name = entry.getKey();
			value = entry.getValue().getInt(0);
			foodValues.put(name, value);
		}
	}

	/*
	 * returns the food value for a single size stack of the input item stack
	 */
	public int getFoodValue(ItemStack stack) {
		if (stack.isEmpty()) {
			return 0;
		}
		String name = stack.getItem().getRegistryName().toString();
		if (foodValues.containsKey(name)) {
			return foodValues.get(name);
		} else if (stack.getItem() instanceof ItemFood) {
			return ((ItemFood) stack.getItem()).getHealAmount(stack) * foodMultiplier;
		}
		return 0;
	}

	@Override
	public void save() {
		super.save();
		targetConfig.save();
		foodConfig.save();
		factionConfig.save();
	}
}
