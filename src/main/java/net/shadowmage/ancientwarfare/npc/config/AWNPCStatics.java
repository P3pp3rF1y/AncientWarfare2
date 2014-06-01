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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.shadowmage.ancientwarfare.core.config.ModConfiguration;
import net.shadowmage.ancientwarfare.npc.faction.FactionTracker;
import net.shadowmage.ancientwarfare.npc.trade.NpcTrade;
import net.shadowmage.ancientwarfare.npc.trade.NpcTradeManager;
import net.shadowmage.ancientwarfare.npc.trade.TradeParser;

public class AWNPCStatics extends ModConfiguration
{

/**
 * shared settings:
 * NONE?
 */
public static final String sharedSettings = "01_shared_settings";

/**
 * server settings:
 * npc worker tick rate / ticks per work unit
 */
public static final String serverSettings = "02_server_settings";
public static int maxNpcLevel = 10;
public static int npcXpFromWork = 1;
public static int npcXpFromTrade = 1;
public static int npcXpFromAttack = 1;
public static int npcXpFromKill = 5;
public static int npcXpFromMoveItem = 1;//TODO add to config
public static int npcWorkTicks = 50;
/**
 * TODO add these to config
 */
public static int npcAttackDamage = 1;//base attack damage for npcs--further multiplied by their equipped weapon
public static float npcLevelDamageMultiplier = 0.05f;//damage bonus per npc level.  @ level 10 they do 2x the damage as at lvl 0
public static int npcArcherAttackDamage = 3;//damage for npc archers...can be increased via enchanted weapons

/**
 * client settings:
 * --SET VIA PROXY / ClientOptions.INSTANCE
 */
public static final String clientSettings = "03_client_settings";

/**
 * what food items are edible, and the amount of food value an NPC will get from eating them
 */
public static final String foodSettings = "04_food_settings";
private HashMap<String, Integer> foodValues = new HashMap<String, Integer>();

/**
 * base aggro / target settings for combat NPCs.  Can be further
 * customized on a per-npc basis via config GUI.
 */
public static final String targetSettings = "05_target_settings";
private HashMap<String, List<String>> entityTargetSettings = new HashMap<String, List<String>>();
private List<String> entitiesToTargetNpcs = new ArrayList<String>();

/**
 * enable/disable specific recipes
 * enable/disable research for specific recipes
 */
public static final String recipeSettings = "06_recipe_settings";

public static final String factionSettings = "07_faction_settings";
public static int factionLossOnDeath = 10;//how much faction standing is lost when you (or one of your npcs) kills an enemy faction-based npc
public static int factionGainOnTrade = 2;//how much faction standing is gained when you complete a trade with a faction-based trader-npc
private HashMap<String, Integer> defaultFactionStandings = new HashMap<String, Integer>();

public AWNPCStatics(Configuration config)
  {
  super(config);
  }

@Override
public void initializeCategories()
  {
  config.addCustomCategoryComment(sharedSettings, "General Options\n" +
      "Affect both client and server.  These configs must match for client and server, or\n" +
      "strange and probably BAD things WILL happen.");
  
  config.addCustomCategoryComment(serverSettings, "Server Options\n" +
      "Affect only server-side operations.  Will need to be set for dedicated servers, and single\n" +
      "player (or LAN worlds).  Clients playing on remote servers can ignore these settings.");
  
  config.addCustomCategoryComment(clientSettings, "Client Options\n" +
      "Affect only client-side operations.  Many of these options can be set from the in-game Options GUI.\n" +
      "Server admins can ignore these settings.");
  
  config.addCustomCategoryComment(foodSettings, "Food Value Options\n" +
      "Affect only server-side operations.  Will need to be set for dedicated servers, and single\n" +
      "player (or LAN worlds).  Clients playing on remote servers can ignore these settings.");
  
  config.addCustomCategoryComment(targetSettings, "Custom NPC Targeting Options\n" +
  		"Add / remove vanilla / mod-added entities from the NPC targeting lists.\n" +
      "Affect only server-side operations.  Will need to be set for dedicated servers, and single\n" +
      "player (or LAN worlds).  Clients playing on remote servers can ignore these settings.");
  
  config.addCustomCategoryComment(recipeSettings, "Recipe Options\n" +
  		"Enable / Disable specific recipes, or remove the research requirements from specific recipes.\n" +
      "Affect only server-side operations.  Will need to be set for dedicated servers, and single\n" +
      "player (or LAN worlds).  Clients playing on remote servers can ignore these settings.");
  
  config.addCustomCategoryComment(factionSettings, "Faction Options\n" +
  		"Set starting faction values, and alter the amount of standing gained/lost from player actions.\n" +
      "Affect only server-side operations.  Will need to be set for dedicated servers, and single\n" +
      "player (or LAN worlds).  Clients playing on remote servers can ignore these settings.");
  
  }

@Override
public void initializeValues()
  { 
  loadFoodValues();
  loadTargetValues();
  loadDefaultFactionStandings();
  
  maxNpcLevel = config.get(serverSettings, "npc_max_level", maxNpcLevel, "Max NPC Level : Default="+maxNpcLevel+"\n" +
  		"How high can NPCs level up?  Npcs gain more health, attack damage, and overall\n" +
  		"improved stats with each level.  Levels can go very high, but higher values may\n" +
  		"result in overpowered NPCs once leveled up.").getInt(maxNpcLevel);
  
  npcXpFromAttack = config.get(serverSettings, "npc_xp_per_attack", npcXpFromAttack, "XP Per Attack : Default="+npcXpFromAttack+"\n" +
  		"How much xp should an NPC gain each time they damage but do not kill an enemy?\n" +
  		"Higher values will result in faster npc leveling.\n" +
  		"Applies to both player-owned and faction-based NPCs.").getInt(npcXpFromAttack);
  
  npcXpFromKill = config.get(serverSettings, "npc_xp_per_kill", npcXpFromKill, "XP Per Kill : Default="+npcXpFromKill+"\n" +
      "How much xp should an NPC gain each time they kill an enemy?\n" +
      "Higher values will result in faster npc leveling.\n" +
      "Applies to both player-owned and faction-based NPCs.").getInt(npcXpFromKill);
  
  npcXpFromTrade = config.get(serverSettings, "npc_xp_per_trade", npcXpFromTrade, "XP Per Trade : Default="+npcXpFromTrade+"\n" +
      "How much xp should an NPC gain each time are sucessfully traded with?\n" +
      "Higher values will result in faster npc leveling and unlock more trade recipes.\n" +
      "Applies to both player-owned and faction-based NPCs.").getInt(npcXpFromTrade);
  
  npcXpFromWork = config.get(serverSettings, "npc_xp_per_work", npcXpFromWork, "XP Per Work: Default="+npcXpFromWork+"\n" +
      "How much xp should an NPC gain each time do work at a worksite?\n" +
      "Higher values will result in faster npc leveling.\n" +
      "Applies to player-owned NPCs only.").getInt(npcXpFromWork);
  
  npcWorkTicks = config.get(serverSettings, "npc_work_ticks", npcWorkTicks, "Time Between Work Ticks: Default="+npcWorkTicks+"\n" +
  		"How many game ticks should pass between workers' processing work at a work-site.\n" +
  		"Lower values result in more work output, higher values result in less work output.").getInt(npcWorkTicks);
  
  factionLossOnDeath = config.get(factionSettings, "faction_loss_on_kill", 10, "Faction Loss On Kill : Default=10\n" +
  		"How much faction standing should be lost if you or one of your minions kills a faction\n" +
  		"based NPC.").getInt(10);
  
  factionGainOnTrade = config.get(factionSettings, "faction_gain_on_trade", 2, "Faction Gain On Trade : Default=2\n" +
  		"How much faction standing should be gained when you trade with a faction based trader.").getInt(2);
  //TODO add all the other normal config options -- faction gain/loss, follow range, work-ticks
  this.config.save();
  }

private void loadTargetValues()
  {
  String[] defaultTargets = new String[]{"Zombie","Skeleton","Slime"};
  String[] targets;
  
  targets = config.get(targetSettings, "combat.targets", defaultTargets, "Default targets for: unassigned combat npc").getStringList();
  addTargetMapping("combat", "", targets);
  
  targets = config.get(targetSettings, "combat.archer.targets", defaultTargets, "Default targets for: player-owned archer").getStringList();
  addTargetMapping("combat", "archer", targets);
  
  targets = config.get(targetSettings, "combat.soldier.targets", defaultTargets, "Default targets for: player-owned soldier").getStringList();
  addTargetMapping("combat", "soldier", targets);
  
  targets = config.get(targetSettings, "combat.leader.targets", defaultTargets, "Default targets for: player-owned leader npc").getStringList();
  addTargetMapping("combat", "leader", targets);
  
  targets = config.get(targetSettings, "combat.medic.targets", defaultTargets, "Default targets for: player-owned medic npc").getStringList();
  addTargetMapping("combat", "medic", targets);
  
  targets = config.get(targetSettings, "combat.engineer.targets", defaultTargets, "Default targets for: player-owned engineer npc").getStringList();
  addTargetMapping("combat", "engineer", targets);
  
  for(String name : FactionTracker.factionNames)
    {
    targets = config.get(targetSettings, name+".archer.targets", defaultTargets, "Default targets for: "+name+" archers").getStringList();
    addTargetMapping(name, "archer", targets);
    
    targets = config.get(targetSettings, name+".soldier.targets", defaultTargets, "Default targets for: "+name+" soldiers").getStringList();
    addTargetMapping(name, "soldier", targets);
    
    targets = config.get(targetSettings, name+".leader.targets", defaultTargets, "Default targets for: "+name+" leaders").getStringList();
    addTargetMapping(name, "leader", targets);  
    }  
  
  targets = config.get(targetSettings, "enemies_to_target_npcs", defaultTargets, "What mob types should have AI inserted to enable them to target NPCs?\n" +
  		"Should work with any new-ai enabled mob type; vanilla or mod-added.").getStringList();
  
  for(String target : targets)
    {
    entitiesToTargetNpcs.add(target);
    }
  }

private void addTargetMapping(String npcType, String npcSubtype, String[] targets)
  {
  String type = npcType + (npcSubtype.isEmpty()? "" : "."+npcSubtype);
  if(!entityTargetSettings.containsKey(type)){entityTargetSettings.put(type, new ArrayList<String>());}
  List<String> t = entityTargetSettings.get(type);
  for(String target : targets)
    {
    t.add(target);
    }
  } 

public boolean shouldEntityTargetNpcs(String entityName)
  {
  return entitiesToTargetNpcs.contains(entityName);
  }

public List<String> getValidTargetsFor(String npcType, String npcSubtype)
  {
  String type = npcType + (npcSubtype.isEmpty()? "" : "."+npcSubtype);
  if(entityTargetSettings.containsKey(type)){return entityTargetSettings.get(type);}
  return Collections.emptyList();
  }

private void loadFoodValues()
  {  
  config.get(foodSettings, Item.itemRegistry.getNameForObject(Items.apple), 3000);
  config.get(foodSettings, Item.itemRegistry.getNameForObject(Items.mushroom_stew), 4500);
  config.get(foodSettings, Item.itemRegistry.getNameForObject(Items.bread), 3750);
  config.get(foodSettings, Item.itemRegistry.getNameForObject(Items.carrot), 3000);
  config.get(foodSettings, Item.itemRegistry.getNameForObject(Items.potato), 1750);
  config.get(foodSettings, Item.itemRegistry.getNameForObject(Items.baked_potato), 4500);
  config.get(foodSettings, Item.itemRegistry.getNameForObject(Items.beef), 2250);
  config.get(foodSettings, Item.itemRegistry.getNameForObject(Items.cooked_beef), 6000);
  config.get(foodSettings, Item.itemRegistry.getNameForObject(Items.chicken), 1500);
  config.get(foodSettings, Item.itemRegistry.getNameForObject(Items.cooked_chicken), 4500);
//  config.get(foodSettings, Item.itemRegistry.getNameForObject(Items.fish), 6);//TODO what to do about fish / item subtypes?
//  config.get(foodSettings, Item.itemRegistry.getNameForObject(Items.cooked_fished), 6);
  config.get(foodSettings, Item.itemRegistry.getNameForObject(Items.porkchop), 2250);
  config.get(foodSettings, Item.itemRegistry.getNameForObject(Items.cooked_porkchop), 6000);
  config.get(foodSettings, Item.itemRegistry.getNameForObject(Items.cookie), 1500);
  config.get(foodSettings, Item.itemRegistry.getNameForObject(Items.pumpkin_pie), 6000);
  
  
  ConfigCategory category = config.getCategory(foodSettings);
  
  String name;
  int value;
  for(Entry<String, Property> entry : category.entrySet())
    {
    name = entry.getKey();
    value = entry.getValue().getInt(0);
    foodValues.put(name, value);
    }
  }

private void loadDefaultFactionStandings()
  {
  for(String name : FactionTracker.factionNames)
    {
    this.defaultFactionStandings.put(name, config.get(factionSettings, name+".starting_faction_standing", 0, "Default faction standing for: ["+name+"s] for new players joining a game.").getInt(0));
    }
  }

/**
 * called during post-init, to ensure all items are loaded
 */
public void loadDefaultTrades()
  {
  File file = new File("config/AWConfig/npc/trades");
  file.mkdirs();
  file = new File(file, "trades.cfg");
  if(!file.exists())
    {
    writeOutDefaultTrades(file);
    }
  List<NpcTrade> trades = TradeParser.parseTrades(file);
  for(NpcTrade trade : trades)
    {
    NpcTradeManager.INSTANCE.addNpcTrade(trade);
    }
  }

private void writeOutDefaultTrades(File file)
  {
  InputStream is = getClass().getResourceAsStream("/assets/ancientwarfare/trades/trades.cfg");
  BufferedReader reader = new BufferedReader(new InputStreamReader(is));    
  try
    {
    BufferedWriter writer = new BufferedWriter(new FileWriter(file));
    String line;
    while((line=reader.readLine())!=null)
      {
      writer.write(line);
      writer.newLine();
      }
    writer.close();
    reader.close();
    is.close();
    } 
  catch (IOException e)
    {
    e.printStackTrace();
    }  
  }

/**
 * returns the food value for a single size stack of the input item stack
 * @param stack
 * @return
 */
public int getFoodValue(ItemStack stack)
  {
  if(stack==null || stack.getItem()==null){return 0;}
  int food = 0;
  String name = Item.itemRegistry.getNameForObject(stack.getItem());
  if(foodValues.containsKey(name))
    {
    food = foodValues.get(name);    
    }  
  return food;
  }

public int getDefaultFaction(String factionName)
  {
  if(defaultFactionStandings.containsKey(factionName))
    {
    return defaultFactionStandings.get(factionName);
    }
  return 0;
  }

}
