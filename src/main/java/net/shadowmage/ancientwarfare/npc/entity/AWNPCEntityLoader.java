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
import net.shadowmage.ancientwarfare.core.entity.AWEntityRegistry;
import net.shadowmage.ancientwarfare.core.entity.AWEntityRegistry.EntityDeclaration;
import net.shadowmage.ancientwarfare.npc.AncientWarfareNPC;
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
  //TODO fix with proper reg name, fix reg-names in core-entity registry
  NpcDeclaration reg = new NpcDeclaration(NpcCombat.class, AWEntityRegistry.NPC_COMBAT, nextID++, AncientWarfareNPC.instance, 120, 3, true, "combat")
    {
    @Override
    public Entity createEntity(World world)
      {
      return new NpcCombat(world);
      }
    };
  addPlayerOwnableNpcRegistration(reg, "combat", "ancientwarfare:npc/spawner_combat");
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
  addPlayerOwnableNpcRegistration(reg, "worker", "ancientwarfare:npc/spawner_miner");
  addNpcSubtypeEntry("worker", "farmer", "ancientwarfare:npc/spawner_farmer");
  addNpcSubtypeEntry("worker", "miner", "ancientwarfare:npc/spawner_miner");
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
  addPlayerOwnableNpcRegistration(reg, "courier", "ancientwarfare:npc/spawner_courier");
  
  //TODO hostile
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
  
  //TODO hostiles
  }

protected static void addPlayerOwnableNpcRegistration(NpcDeclaration reg, String npcName, String icon)
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
  NpcBase npc = (NpcBase) reg.createEntity(world);
  if(!npcSubtype.isEmpty())
    {
    ItemStack stack = reg.spawnEquipment.get(npcSubtype);
    if(stack!=null)
      {
      npc.setCurrentItemOrArmor(0, stack.copy());
      }
    }
  return npc;
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

}

}
