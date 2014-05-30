package net.shadowmage.ancientwarfare.npc.entity;

import java.util.HashMap;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.api.AWItems;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.entity.AWEntityRegistry;
import net.shadowmage.ancientwarfare.core.entity.AWEntityRegistry.EntityDeclaration;
import net.shadowmage.ancientwarfare.npc.AncientWarfareNPC;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcBanditArcher;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcBanditLeader;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcBanditPriest;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcBanditSoldier;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcBanditTrader;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcFaction;
import net.shadowmage.ancientwarfare.npc.item.AWNpcItemLoader;
import net.shadowmage.ancientwarfare.npc.item.ItemNpcSpawner;


public class AWNPCEntityLoader
{
private static int nextID = 0;

/**
 * Npc base type -> NpcDeclaration<br>
 * Used to retrieve declaration for creating entities<br>
 * NpcDeclaration also stores information pertaining to npc-sub-type basic setup
 * 
 */
private static HashMap<String, NpcDeclaration> npcMap = new HashMap<String, NpcDeclaration>();

public static void load()
  {
  NpcDeclaration reg = new NpcDeclaration(NpcCombat.class, AWEntityRegistry.NPC_COMBAT, nextID++, AncientWarfareNPC.instance, 120, 3, true, "combat")
    {
    @Override
    public Entity createEntity(World world)
      {
      return new NpcCombat(world);
      }
    };
  addNpcRegistration(reg, "combat", "ancientwarfare:npc/spawner_combat");
  addNpcSubtypeEntry("combat", "commander", "ancientwarfare:npc/spawner_commander");
  addNpcSubtypeEntry("combat", "soldier", "ancientwarfare:npc/spawner_combat");
  addNpcSubtypeEntry("combat", "archer", "ancientwarfare:npc/spawner_archer");
  addNpcSubtypeEntry("combat", "medic", "ancientwarfare:npc/spawner_medic");
  addNpcSubtypeEntry("combat", "engineer", "ancientwarfare:npc/spawner_engineer");
  
  reg = new NpcDeclaration(NpcWorker.class, AWEntityRegistry.NPC_WORKER, nextID++, AncientWarfareNPC.instance, 120, 3, true, "worker")
    {
    @Override
    public Entity createEntity(World world)
      {
      return new NpcWorker(world);
      }
    };
  addNpcRegistration(reg, "worker", "ancientwarfare:npc/spawner_miner");
  addNpcSubtypeEntry("worker", "miner", "ancientwarfare:npc/spawner_miner");
  addNpcSubtypeEntry("worker", "farmer", "ancientwarfare:npc/spawner_farmer");
  addNpcSubtypeEntry("worker", "lumberjack", "ancientwarfare:npc/spawner_lumberjack");
  addNpcSubtypeEntry("worker", "researcher", "ancientwarfare:npc/spawner_researcher");
  addNpcSubtypeEntry("worker", "craftsman", "ancientwarfare:npc/spawner_craftsman");
  
  reg = new NpcDeclaration(NpcCourier.class, AWEntityRegistry.NPC_COURIER, nextID++, AncientWarfareNPC.instance, 120, 3, true, "courier")
    {
    @Override
    public Entity createEntity(World world)
      {
      return new NpcCourier(world);
      }
    };
  addNpcRegistration(reg, "courier", "ancientwarfare:npc/spawner_courier");
  
  /**
   * HOSTILE NPCS
   */
  reg = new NpcFactionDeclaration(NpcBanditArcher.class, AWEntityRegistry.NPC_FACTION_BANDIT_ARCHER, nextID++, AncientWarfareNPC.instance, 120, 3, true, "bandit.archer")
    {    
    @Override
    public Entity createEntity(World world)
      {
      return new NpcBanditArcher(world);
      }
    };
  addNpcRegistration(reg, "bandit.archer", "ancientwarfare:npc/spawner_hostile_archer");
  addNpcSubtypeEntry("bandit.archer", "elite", "ancientwarfare:npc/spawner_hostile_archer");
  
  reg = new NpcFactionDeclaration(NpcBanditSoldier.class, AWEntityRegistry.NPC_FACTION_BANDIT_SOLDIER, nextID++, AncientWarfareNPC.instance, 120, 3, true, "bandit.soldier")
    {    
    @Override
    public Entity createEntity(World world)
      {
      return new NpcBanditSoldier(world);
      }
    };
  addNpcRegistration(reg, "bandit.soldier", "ancientwarfare:npc/spawner_hostile_soldier");
  addNpcSubtypeEntry("bandit.soldier", "elite", "ancientwarfare:npc/spawner_hostile_soldier");
  
  reg = new NpcFactionDeclaration(NpcBanditLeader.class, AWEntityRegistry.NPC_FACTION_BANDIT_COMMANDER, nextID++, AncientWarfareNPC.instance, 120, 3, true, "bandit.leader")
    {    
    @Override
    public Entity createEntity(World world)
      {
      return new NpcBanditLeader(world);
      }
    };
  addNpcRegistration(reg, "bandit.leader", "ancientwarfare:npc/spawner_hostile_commander");
  addNpcSubtypeEntry("bandit.leader", "elite", "ancientwarfare:npc/spawner_hostile_commander");
  
  reg = new NpcFactionDeclaration(NpcBanditPriest.class, AWEntityRegistry.NPC_FACTION_BANDIT_PRIEST, nextID++, AncientWarfareNPC.instance, 120, 3, true, "bandit.priest")
    {    
    @Override
    public Entity createEntity(World world)
      {
      return new NpcBanditPriest(world);
      }
    };
  addNpcRegistration(reg, "bandit.priest", "ancientwarfare:npc/spawner_hostile_priest");
  
  reg = new NpcFactionDeclaration(NpcBanditTrader.class, AWEntityRegistry.NPC_FACTION_BANDIT_TRADER, nextID++, AncientWarfareNPC.instance, 120, 3, true, "bandit.trader")
    {    
    @Override
    public Entity createEntity(World world)
      {
      return new NpcBanditTrader(world);
      }
    };
  addNpcRegistration(reg, "bandit.trader", "ancientwarfare:npc/spawner_hostile_trader");
  
  //TODO registrations for the rest of the factions....
  }

/**
 * has to be called during post-init so that all items/etc are fully initialzed
 */
public static void loadNpcSubtypeEquipment()
  {
  addNpcSubtypeEquipment("worker", "farmer", new ItemStack(Items.iron_hoe));
  addNpcSubtypeEquipment("worker", "miner", new ItemStack(Items.iron_pickaxe));
  addNpcSubtypeEquipment("worker", "lumberjack", new ItemStack(Items.iron_axe));  
  addNpcSubtypeEquipment("worker", "researcher", new ItemStack(Item.getItemFromBlock(Blocks.torch)));//TODO make quill item
  addNpcSubtypeEquipment("worker", "craftsman", new ItemStack(AWItems.automationHammer));
      
  addNpcSubtypeEquipment("combat", "commander", new ItemStack(Items.diamond_sword));//TODO commander -- scepter / mace?
  addNpcSubtypeEquipment("combat", "soldier", new ItemStack(Items.iron_sword));
  addNpcSubtypeEquipment("combat", "archer", new ItemStack(Items.bow));
  addNpcSubtypeEquipment("combat", "engineer", new ItemStack(AWItems.automationHammer));
  addNpcSubtypeEquipment("combat", "medic", new ItemStack(Items.iron_axe));  
  }

protected static void addNpcRegistration(NpcDeclaration reg, String npcName, String icon)
  {
  AWEntityRegistry.registerEntity(reg);
  if(reg.canSpawnBaseEntity)
    {
    AWNpcItemLoader.npcSpawner.addNpcType(npcName, icon);    
    }
  npcMap.put(npcName, reg);
  }

public static NpcBase createNpc(World world, String npcType, String npcSubtype)
  {
  if(!npcMap.containsKey(npcType)){return null;}
  NpcDeclaration reg = npcMap.get(npcType);
  return reg.createEntity(world, npcSubtype);
  }

protected static void addNpcSubtypeEntry(String npcType, String npcSubtype, String icon)
  {
  if(!npcMap.containsKey(npcType)){throw new IllegalArgumentException("npc type must first be mapped");}
  npcMap.get(npcType).addSubtype(npcSubtype, icon);
  AWNpcItemLoader.npcSpawner.addNpcType(npcType+"."+npcSubtype, icon);
  }

protected static void addNpcSubtypeEquipment(String npcType, String npcSubtype, ItemStack equipment)
  {
  if(!npcMap.containsKey(npcType)){throw new IllegalArgumentException("npc type must first be mapped");}
  NpcDeclaration reg = npcMap.get(npcType);
  if(!reg.subTypeIcons.containsKey(npcSubtype)){throw new IllegalArgumentException("npc subtype must first be mapped");}
  reg.spawnEquipment.put(npcSubtype, equipment);
  }

/**
 * used by npc spawner item to get the sub-items
 */
@SuppressWarnings({ "rawtypes", "unchecked" })//WHY THE FUCK DOES VANILLA DO RETARDED SHIT I HAVE TO SUPPRESS WARNINGS ON?
public static void getSpawnerSubItems(List list)
  {
  for(NpcDeclaration dec : npcMap.values())
    {
    if(dec.canSpawnBaseEntity)
      {
      list.add(ItemNpcSpawner.getStackForNpcType(dec.npcType, ""));
      }
    for(String sub : dec.subTypeIcons.keySet())
      {
      list.add(ItemNpcSpawner.getStackForNpcType(dec.npcType, sub));
      }
    }
  }

public static abstract class NpcDeclaration extends EntityDeclaration
{

private boolean canSpawnBaseEntity = true;
private final String npcType;
private HashMap<String, String> subTypeIcons = new HashMap<String, String>();
private HashMap<String, ItemStack> spawnEquipment = new HashMap<String, ItemStack>();

public NpcDeclaration(Class<? extends Entity> entityClass, String entityName, int id, Object mod, int trackingRange, int updateFrequency, boolean sendsVelocityUpdates, String npcType)
  {
  super(entityClass, entityName, id, mod, trackingRange, updateFrequency, sendsVelocityUpdates);
  this.npcType=npcType;
  }

public NpcDeclaration setCanSpawnBaseType(boolean can)
  {
  canSpawnBaseEntity = can;
  return this;
  }

public void addSubtype(String type, String icon)
  {
  subTypeIcons.put(type, icon);
  }

public NpcBase createEntity(World world, String subType)
  {
  NpcBase npc = (NpcBase) createEntity(world);
  if(!subType.isEmpty())
    {
    ItemStack stack = spawnEquipment.get(subType);
    if(stack!=null)
      {
      npc.setCurrentItemOrArmor(0, stack.copy());
      }
    }  
  return npc;
  }
}

public static abstract class NpcFactionDeclaration extends NpcDeclaration
{

public NpcFactionDeclaration(Class<? extends NpcFaction> entityClass, String entityName, int id, Object mod, int trackingRange,int updateFrequency, boolean sendsVelocityUpdates, String npcName)
  {
  super(entityClass, entityName, id, mod, trackingRange, updateFrequency, sendsVelocityUpdates, npcName);
  }

@Override
public NpcFaction createEntity(World world, String subType)
  {
  NpcFaction npc = (NpcFaction) createEntity(world);
  AWLog.logDebug("creating faction npc of subtype: "+subType);
  npc.setSubtype(subType);  
  return npc;
  }

}

}
