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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import net.minecraft.entity.EntityList;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.shadowmage.ancientwarfare.core.config.AWCoreStatics;
import net.shadowmage.ancientwarfare.core.config.ModConfiguration;

public class AWNPCStatics extends ModConfiguration
{

/**
 * shared settings:
 * NONE?
 */
/************************************************shared SETTINGS*************************************************/
public static final String sharedSettings = "01_shared_settings";

/**
 * server settings:
 * npc worker tick rate / ticks per work unit
 */
/************************************************SERVER SETTINGS*************************************************/
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
public static boolean exportEntityNames = false;

/**
 * TODO add these to config
 */
public static int npcAttackDamage = 1;//base attack damage for npcs--further multiplied by their equipped weapon
public static float npcLevelDamageMultiplier = 0.05f;//damage bonus per npc level.  @ level 10 they do 2x the damage as at lvl 0
public static int npcArcherAttackDamage = 3;//damage for npc archers...can be increased via enchanted weapons
public static double npcPathfindRange = 60.d;//max pathfind range

/************************************************CLIENT SETTINGS*************************************************/
public static final String clientSettings = "03_client_settings";
public static boolean loadDefaultSkinPack = true;

/************************************************RECIPE SETTINGS*************************************************/
public static final String recipeSettings = "04_recipe_settings";

/************************************************FOOD CONFIG*************************************************/
/************************************************FOOD SETTINGS*************************************************/
public static final String foodSettings = "01_food_settings";
private HashMap<String, Integer> foodValues = new HashMap<String, Integer>();


/************************************************TARGET CONFIG*************************************************/
/************************************************TARGET SETTINGS*************************************************/
public static final String targetSettings = "01_target_settings";
private HashMap<String, List<String>> entityTargetSettings = new HashMap<String, List<String>>();
private List<String> entitiesToTargetNpcs = new ArrayList<String>();


/************************************************FACTIONS CONFIG*************************************************/
/************************************************FACTION STARTING VALUE SETTINGS*************************************************/
public static final String factionSettings = "01_faction_settings";
public static int factionLossOnDeath = 10;//how much faction standing is lost when you (or one of your npcs) kills an enemy faction-based npc
public static int factionGainOnTrade = 2;//how much faction standing is gained when you complete a trade with a faction-based trader-npc
private HashMap<String, Integer> defaultFactionStandings = new HashMap<String, Integer>();
private HashMap<String, HashMap<String, Boolean>> factionVsFactionStandings = new HashMap<String, HashMap<String, Boolean>>();


/************************************************NAMES CONFIG*************************************************/
/************************************************CUSTOM NAME SETTINGS*************************************************/

public static final String[] factionNames = new String[]{"bandit","viking","pirate","desert","native","custom_1","custom_2","custom_3"};
public static final String[] factionNpcSubtypes = new String[]{"soldier","soldier.elite","cavalry","archer","archer.elite","mounted_archer","leader","leader.elite","priest","trader","civilian.male","civilian.female"};


/************************************************HEALTH CONFIG*************************************************/
/************************************************NPC HEALTH SETTINGS*************************************************/
public static final String npcDefaultHealthSettings = "01_npc_base_health";
private HashMap<String, Integer> healthValues = new HashMap<String, Integer>();


/************************************************EQUIPMENT CONFIG*************************************************/
/************************************************NPC WEAPON SETTINGS*************************************************/

public static final String npcDefaultWeapons = "01_npc_weapons";
public static final String npcOffhandItems = "02_npc_offhand";
public static final String npcArmorHead = "03_npc_helmet";
public static final String npcArmorChest = "04_npc_chest";
public static final String npcArmorLegs = "05_npc_legs";
public static final String npcArmorBoots= "06_npc_boots";
public static final String npcWorkItem = "07_npc_work_slot";
public static final String npcUpkeepItem = "08_npc_upkeep_slot";

public Configuration equipmentConfig;
public Configuration targetConfig;
public Configuration healthConfig;
public Configuration foodConfig;
public Configuration factionConfig;

public static Property renderAI, renderWorkPoints, renderFriendlyNames, renderHostileNames, renderFriendlyHealth, renderHostileHealth, renderTeamColors;

public AWNPCStatics(Configuration config)
  {
  super(config);
  equipmentConfig = AWCoreStatics.getConfigFor("AncientWarfareNpcEquipment");
  targetConfig = AWCoreStatics.getConfigFor("AncientWarfareNpcTargeting");
  healthConfig = AWCoreStatics.getConfigFor("AncientWarfareNpcHealth");
  foodConfig = AWCoreStatics.getConfigFor("AncientWarfareNpcFood");
  factionConfig = AWCoreStatics.getConfigFor("AncientWarfareNpcFactionStandings");  
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
  
  config.addCustomCategoryComment(recipeSettings, "Recipe Options\n" +
      "Enable / Disable specific recipes, or remove the research requirements from specific recipes.\n" +
      "Affect only server-side operations.  Will need to be set for dedicated servers, and single\n" +
      "player (or LAN worlds).  Clients playing on remote servers can ignore these settings.");
  
  foodConfig.addCustomCategoryComment(foodSettings, "Food Value Options\n" +
  		"Only the food items here will be useable as food for NPCs.  The value specified is\n" +
  		"the number of ticks that the food item will feed the NPC for.\n" +
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
public void initializeValues()
  { 
  loadFoodValues();
  loadTargetValues();
  loadDefaultFactionStandings();
  initializeCustomHealthValues();
  initializeNpcEquipmentConfigs();
  
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
  
  factionLossOnDeath = factionConfig.get(factionSettings, "faction_loss_on_kill", 10, "Faction Loss On Kill\nDefault=10\n" +
  		"How much faction standing should be lost if you or one of your minions kills a faction\n" +
  		"based NPC.").getInt(10);
  
  factionGainOnTrade = factionConfig.get(factionSettings, "faction_gain_on_trade", 2, "Faction Gain On Trade\nDefault=2\n" +
  		"How much faction standing should be gained when you trade with a faction based trader.").getInt(2);
    
  loadDefaultSkinPack = config.get(clientSettings, "load_default_skin_pack", loadDefaultSkinPack, "Load Default Skin Pack\nDefault=true\n" +
  		"If true, default skin pack will be loaded.\n" +
  		"If false, default skin pack will NOT be loaded -- you will need to supply your own\n" +
  		"skin packs or all npcs will use the default skin.").getBoolean(loadDefaultSkinPack);
 
  exportEntityNames = config.get(serverSettings, "export_entity_names", false, "Export entity name list\nDefault="+exportEntityNames+"\n" +
  		"If true, a text file will be created in the main ancientwarfare config directory containing a list of all registered in-game entity names.\n" +
  		"These names may be used to populate the NPC target lists.").getBoolean(exportEntityNames);
  
  renderAI = config.get(clientSettings, "render_npc_ai", true);
  renderWorkPoints = config.get(clientSettings, "render_work_points", true);  
  renderFriendlyNames = config.get(clientSettings, "render_friendly_nameplates", true);
  renderHostileNames = config.get(clientSettings, "render_hostile_nameplates", true);
  renderFriendlyHealth = config.get(clientSettings, "render_friendly_health", true);
  renderHostileHealth = config.get(clientSettings, "render_hostile_health", true);
  renderTeamColors = config.get(clientSettings, "render_team_colors", true);
  this.config.save();
  }

public void postInitCallback()
  {
  if(exportEntityNames)
    {
    File file = new File("config/ancientwarfare");
    file.mkdirs();
    file = new File(file, "entity_names.txt");
    FileWriter wr = null;
    try
      {
      wr = new FileWriter(file);
      for(Object obj : EntityList.stringToClassMapping.keySet())
        {
        wr.write(String.valueOf(obj)+"\n");
        }
      } 
    catch (IOException e)
      {    
      e.printStackTrace();
      }
    finally
      {
      if(wr!=null)
        {
        try
          {
          wr.close();
          } 
        catch (IOException e)
          {
          e.printStackTrace();
          }
        }
      }    
    }
  }

private void loadTargetValues()
  {
  String[] defaultTargets = new String[]{"Zombie","Skeleton","Slime"};
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
  
  for(String name : factionNames)
    {
    targets = targetConfig.get(targetSettings, name+".archer.targets", defaultTargets, "Default targets for: "+name+" archers").getStringList();
    addTargetMapping(name, "archer", targets);
    
    targets = targetConfig.get(targetSettings, name+".archer.elite.targets", defaultTargets, "Default targets for: "+name+" elite archers").getStringList();
    addTargetMapping(name, "archer.elite", targets);
    
    targets = targetConfig.get(targetSettings, name+".soldier.targets", defaultTargets, "Default targets for: "+name+" soldiers").getStringList();
    addTargetMapping(name, "soldier", targets);
    
    targets = targetConfig.get(targetSettings, name+".soldier.elite.targets", defaultTargets, "Default targets for: "+name+" elite soldiers").getStringList();
    addTargetMapping(name, "soldier.elite", targets);
    
    targets = targetConfig.get(targetSettings, name+".leader.targets", defaultTargets, "Default targets for: "+name+" leaders").getStringList();
    addTargetMapping(name, "leader", targets);  
    
    targets = targetConfig.get(targetSettings, name+".leader.elite.targets", defaultTargets, "Default targets for: "+name+" elite leaders").getStringList();
    addTargetMapping(name, "leader.elite", targets);  
    
    targets = targetConfig.get(targetSettings, name+".priest.targets", defaultTargets, "Default targets for: "+name+" priests").getStringList();
    addTargetMapping(name, "priest", targets); 
    }  
  
  targets = targetConfig.get(targetSettings, "enemies_to_target_npcs", defaultTargets, "What mob types should have AI inserted to enable them to target NPCs?\n" +
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
  foodConfig.get(foodSettings, Item.itemRegistry.getNameForObject(Items.apple), 3000);
  foodConfig.get(foodSettings, Item.itemRegistry.getNameForObject(Items.mushroom_stew), 4500);
  foodConfig.get(foodSettings, Item.itemRegistry.getNameForObject(Items.bread), 3750);
  foodConfig.get(foodSettings, Item.itemRegistry.getNameForObject(Items.carrot), 3000);
  foodConfig.get(foodSettings, Item.itemRegistry.getNameForObject(Items.potato), 1750);
  foodConfig.get(foodSettings, Item.itemRegistry.getNameForObject(Items.baked_potato), 4500);
  foodConfig.get(foodSettings, Item.itemRegistry.getNameForObject(Items.beef), 2250);
  foodConfig.get(foodSettings, Item.itemRegistry.getNameForObject(Items.cooked_beef), 6000);
  foodConfig.get(foodSettings, Item.itemRegistry.getNameForObject(Items.chicken), 1500);
  foodConfig.get(foodSettings, Item.itemRegistry.getNameForObject(Items.cooked_chicken), 4500);
  foodConfig.get(foodSettings, Item.itemRegistry.getNameForObject(Items.cooked_fished), 4500);
  foodConfig.get(foodSettings, Item.itemRegistry.getNameForObject(Items.porkchop), 2250);
  foodConfig.get(foodSettings, Item.itemRegistry.getNameForObject(Items.cooked_porkchop), 6000);
  foodConfig.get(foodSettings, Item.itemRegistry.getNameForObject(Items.cookie), 1500);
  foodConfig.get(foodSettings, Item.itemRegistry.getNameForObject(Items.pumpkin_pie), 6000);
  
  
  ConfigCategory category = foodConfig.getCategory(foodSettings);
  
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
    this.defaultFactionStandings.put(name, factionConfig.get(factionSettings, name+".starting_faction_standing", 0, "Default faction standing for: ["+name+"] for new players joining a game.").getInt(0));
    for(String name2 : factionNames)
      {
      if(name.equals(name2)){continue;}
      key = name+":"+name2;
      val = factionConfig.get(factionSettings, key, false, "How does: "+name+" faction view: "+name2+" faction?\n" +
      		"If true, "+name+"s will be hostile towards "+name2+"s").getBoolean(false);
      this.factionVsFactionStandings.get(name).put(name2, val);
      }
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

private void initializeCustomHealthValues()
  {
  String key;
  for(String name : factionNames)
    {
    for(String type : factionNpcSubtypes)
      {
      key = name+"."+type;
      healthValues.put(key, healthConfig.get(npcDefaultHealthSettings, key, 20).getInt(20));
      }
    }
  healthValues.put("combat", healthConfig.get(npcDefaultHealthSettings, "combat", 20).getInt(20));
  healthValues.put("worker",healthConfig.get(npcDefaultHealthSettings, "worker", 20).getInt(20));                                                                                                                      
  healthValues.put("courier", healthConfig.get(npcDefaultHealthSettings, "courier", 20).getInt(20));
  healthValues.put("trader", healthConfig.get(npcDefaultHealthSettings, "trader", 20).getInt(20));
  healthValues.put("priest", healthConfig.get(npcDefaultHealthSettings, "priest", 20).getInt(20));
  healthValues.put("bard", healthConfig.get(npcDefaultHealthSettings, "bard", 20).getInt(20));
  }

public int getMaxHealthFor(String type)
  {
  return healthValues.get(type);
  }

public boolean shouldFactionBeHostileTowards(String faction1, String faction2)
  {
  if(factionVsFactionStandings.containsKey(faction1) && factionVsFactionStandings.get(faction1).containsKey(faction2))
    {
    return factionVsFactionStandings.get(faction1).get(faction2);
    }
  return false;
  }

private HashMap<String, String[]> eqmp = new HashMap<String, String[]>();

public void initializeNpcEquipmentConfigs()
  {
  String fullType;
  for(String faction : factionNames)
    {
    for(String type : factionNpcSubtypes)
      {
      fullType = faction+"."+type;
      eqmp.put(fullType, new String[8]);//allocate empty string array for each npc type to hold item names for their equipment
      }
    eqmp.get(faction+".soldier")[0]="minecraft:iron_sword";
    eqmp.get(faction+".soldier.elite")[0]="minecraft:iron_sword";
    eqmp.get(faction+".cavalry")[0]="minecraft:iron_sword";
    eqmp.get(faction+".archer")[0]="minecraft:bow";
    eqmp.get(faction+".archer.elite")[0]="minecraft:bow";
    eqmp.get(faction+".mounted_archer")[0]="minecraft:bow";
    eqmp.get(faction+".leader")[0]="minecraft:diamond_sword";
    eqmp.get(faction+".leader.elite")[0]="minecraft:diamond_sword";
    eqmp.get(faction+".trader")[0]="minecraft:book";
    eqmp.get(faction+".priest")[0]="minecraft:book";
    }    
    
  String[] array;
  String item;
  for(String key : eqmp.keySet())
    {
    array = eqmp.get(key);
    item = array[0];
    item=item==null? "null" : item;
    item=equipmentConfig.get(npcDefaultWeapons, key, item).getString();
    array[0]=item;
    
    item = array[4];
    item=item==null? "null" : item;
    item=equipmentConfig.get(npcArmorHead, key, item).getString();
    array[4]=item;
    
    item = array[3];
    item=item==null? "null" : item;
    item=equipmentConfig.get(npcArmorChest, key, item).getString();
    array[3]=item;
        
    item = array[2];
    item=item==null? "null" : item;
    item=equipmentConfig.get(npcArmorLegs, key, item).getString();
    array[2]=item;
    
    item = array[1];
    item=item==null? "null" : item;
    item=equipmentConfig.get(npcArmorBoots, key, item).getString();
    array[1]=item;
    
    item = array[5];
    item=item==null? "null" : item;
    item=equipmentConfig.get(npcWorkItem, key, item).getString();
    array[5]=item;
    
    item = array[6];
    item=item==null? "null" : item;
    item=equipmentConfig.get(npcUpkeepItem, key, item).getString();
    array[6]=item;
    
    item = array[7];
    item=item==null? "null" : item;
    item=equipmentConfig.get(npcOffhandItems, key, item).getString();
    array[7]=item;
    }
  }

public ItemStack getStartingEquipmentForSlot(String type, int slot)
  {
  String itemName = null;  
  if(eqmp.containsKey(type)){itemName = eqmp.get(type)[slot];}
  if(itemName!=null && !itemName.isEmpty() && !itemName.equals("null"))
    {
    Item item = (Item) Item.itemRegistry.getObject(itemName);
    if(item!=null)
      {
      return new ItemStack(item);
      }
    }
  return null;
  }

public void save()
  {
  config.save();
  equipmentConfig.save();
  targetConfig.save();
  healthConfig.save();
  foodConfig.save();
  factionConfig.save();
  }

}
