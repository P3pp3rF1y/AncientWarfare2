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
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.shadowmage.ancientwarfare.core.config.ModConfiguration;
import net.shadowmage.ancientwarfare.npc.trade.NpcTrade;
import net.shadowmage.ancientwarfare.npc.trade.NpcTradeManager;
import net.shadowmage.ancientwarfare.npc.trade.TradeParser;
import cpw.mods.fml.common.registry.LanguageRegistry;

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
public static int npcCourierWorkTicks=50;//TODO add to config
public static int npcDefaultUpkeepWithdraw = 6000;//5 minutes

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
public static boolean loadDefaultSkinPack = true;
public static boolean overrideDefaultNames = false;

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
private HashMap<String, HashMap<String, Boolean>> factionVsFactionStandings = new HashMap<String, HashMap<String, Boolean>>();

public static final String factionNameSettings = "08_faction_and_type_names";
public static final String[] factionNames = new String[]{"bandit","viking","pirate","desert","native","custom_1","custom_2","custom_3"};
public static final String[] factionNpcSubtypes = new String[]{"soldier","soldier.elite","cavalry","archer","archer.elite","mounted_archer","leader","leader.elite","priest","trader","civilian.male","civilian.female"};
private HashMap<String, String> customNpcNames = new HashMap<String, String>();
private String[] overridenLanguages = new String[]{};


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
  		"Only the food items here will be useable as food for NPCs.  The value specified is\n" +
  		"the number of ticks that the food item will feed the NPC for.\n" +
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
  
  config.addCustomCategoryComment(factionNameSettings, "Faction Naming Options\n" +
  		"These settings effect the displayed name of NPCs and items in game.\n" +
  		"Client-side only option.\n" +
  		"These settings override the translation entries from language files for the currently\n" +
  		"loaded language.  These custom settings only take effect if 'override_default_names'=true\n" +
  		"and only apply to the language(s) specified in 'overriden_languages'(both are in 03_client_settings).");
  }

@Override
public void initializeValues()
  { 
  loadFoodValues();
  loadTargetValues();
  loadDefaultFactionStandings();
  initializeCustomNpcNames();
  
  maxNpcLevel = config.get(serverSettings, "npc_max_level", maxNpcLevel, "Max NPC Level\nDefault="+maxNpcLevel+"\n" +
  		"How high can NPCs level up?  Npcs gain more health, attack damage, and overall\n" +
  		"improved stats with each level.  Levels can go very high, but higher values may\n" +
  		"result in overpowered NPCs once leveled up.").getInt(maxNpcLevel);
  
  npcXpFromAttack = config.get(serverSettings, "npc_xp_per_attack", npcXpFromAttack, "XP Per Attack\nDefault="+npcXpFromAttack+"\n" +
  		"How much xp should an NPC gain each time they damage but do not kill an enemy?\n" +
  		"Higher values will result in faster npc leveling.\n" +
  		"Applies to both player-owned and faction-based NPCs.").getInt(npcXpFromAttack);
  
  npcXpFromKill = config.get(serverSettings, "npc_xp_per_kill", npcXpFromKill, "XP Per Killnefault="+npcXpFromKill+"\n" +
      "How much xp should an NPC gain each time they kill an enemy?\n" +
      "Higher values will result in faster npc leveling.\n" +
      "Applies to both player-owned and faction-based NPCs.").getInt(npcXpFromKill);
  
  npcXpFromTrade = config.get(serverSettings, "npc_xp_per_trade", npcXpFromTrade, "XP Per Trade\nDefault="+npcXpFromTrade+"\n" +
      "How much xp should an NPC gain each time are sucessfully traded with?\n" +
      "Higher values will result in faster npc leveling and unlock more trade recipes.\n" +
      "Applies to both player-owned and faction-based NPCs.").getInt(npcXpFromTrade);
  
  npcXpFromWork = config.get(serverSettings, "npc_xp_per_work", npcXpFromWork, "XP Per Work\nDefault="+npcXpFromWork+"\n" +
      "How much xp should an NPC gain each time do work at a worksite?\n" +
      "Higher values will result in faster npc leveling.\n" +
      "Applies to player-owned NPCs only.").getInt(npcXpFromWork);
  
  npcWorkTicks = config.get(serverSettings, "npc_work_ticks", npcWorkTicks, "Time Between Work Ticks\nDefault="+npcWorkTicks+"\n" +
  		"How many game ticks should pass between workers' processing work at a work-site.\n" +
  		"Lower values result in more work output, higher values result in less work output.").getInt(npcWorkTicks);
  
  factionLossOnDeath = config.get(factionSettings, "faction_loss_on_kill", 10, "Faction Loss On Kill\nDefault=10\n" +
  		"How much faction standing should be lost if you or one of your minions kills a faction\n" +
  		"based NPC.").getInt(10);
  
  factionGainOnTrade = config.get(factionSettings, "faction_gain_on_trade", 2, "Faction Gain On Trade\nDefault=2\n" +
  		"How much faction standing should be gained when you trade with a faction based trader.").getInt(2);
    
  loadDefaultSkinPack = config.get(clientSettings, "load_default_skin_pack", loadDefaultSkinPack, "Load Default Skin Pack\nDefault=true\n" +
  		"If true, default skin pack will be loaded.\n" +
  		"If false, default skin pack will NOT be loaded -- you will need to supply your own\n" +
  		"skin packs or all npcs will use the default skin.").getBoolean(loadDefaultSkinPack);
 
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
  
  for(String name : factionNames)
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
//  config.get(foodSettings, Item.itemRegistry.getNameForObject(Items.fish), 6);//TODO what to do about raw fish item subtypes?
  config.get(foodSettings, Item.itemRegistry.getNameForObject(Items.cooked_fished), 4500);
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
  String key;
  boolean val;
  for(String name : factionNames)
    {
    if(!this.factionVsFactionStandings.containsKey(name))
      {
      this.factionVsFactionStandings.put(name, new HashMap<String, Boolean>());
      }
    this.defaultFactionStandings.put(name, config.get(factionSettings, name+".starting_faction_standing", 0, "Default faction standing for: ["+name+"] for new players joining a game.").getInt(0));
    for(String name2 : factionNames)
      {
      if(name.equals(name2)){continue;}
      key = name+":"+name2;
      val = config.get(factionSettings, key, false, "How does: "+name+" faction view: "+name2+" faction?\n" +
      		"If true, "+name+"s will be hostile towards "+name2+"s").getBoolean(false);
      this.factionVsFactionStandings.get(name).put(name2, val);
      }
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

private void initializeCustomNpcNames()
  {      
  overrideDefaultNames = config.get(clientSettings, "override_default_names", overrideDefaultNames, "Override Default NPC Names\nDefault="+overrideDefaultNames+"\n" +
      "If true, default npc names will be overridden with the names specified in "+factionNameSettings+" for\n" +
      "the languages specified in overriden_languages").getBoolean(overrideDefaultNames);
  
  overridenLanguages = config.get(clientSettings, "overriden_languages", overridenLanguages, "Languages to Override With Custom Names\nDefault=>empty<\n" +
      "Any languages specified here will be overriden with the custom npc names specified in "+factionNameSettings+".\n" +
      "Only applicable if override_default_names=true.\n" +
      "Example language codes are: en_US, en_UK, en_CA -- see more information regarding minecraft language packs\n" +
      "for more codes.").getStringList();
  
  String key, fullKey;
  String value;
  for(String faction : factionNames)
    {
    for(String type : factionNpcSubtypes)
      {      
      key = faction+"."+type;//this is the lookup key used for all stored values      
      fullKey = "npc."+key+".name";
      
      value = StatCollector.translateToLocal(fullKey);//get the default value from MY lang file, use that as the base value          
      value = config.get(factionNameSettings, key, value).getString();//initialize the default value, and/or return the configured value            
      customNpcNames.put(fullKey, value);//set the returned value into the custom naming map for npc-type
      
     //update expanded key and set custom names for each of the other translation keys for spawner and registry name
      fullKey = "item.npc_spawner."+key+".name";
      customNpcNames.put(fullKey, value);      
      fullKey = "entity.ancientwarfarenpc."+key+".name";
      customNpcNames.put(fullKey, value);
      }
    }
  loadCustomNpcNames();
  }

public void loadCustomNpcNames()
  {
  if(!overrideDefaultNames){return;}
  for(String lang : overridenLanguages)
    {
    LanguageRegistry.instance().injectLanguage(lang, customNpcNames);
    }
  }

public boolean shouldFactionBeHostileTowards(String faction1, String faction2)
  {
  if(factionVsFactionStandings.containsKey(faction1) && factionVsFactionStandings.get(faction1).containsKey(faction2))
    {
    return factionVsFactionStandings.get(faction1).get(faction2);
    }
  return false;
  }

}
