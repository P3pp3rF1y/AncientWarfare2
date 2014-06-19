package net.shadowmage.ancientwarfare.npc.entity;

import java.util.HashMap;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.api.AWItems;
import net.shadowmage.ancientwarfare.core.entity.AWEntityRegistry;
import net.shadowmage.ancientwarfare.core.entity.AWEntityRegistry.EntityDeclaration;
import net.shadowmage.ancientwarfare.npc.AncientWarfareNPC;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcBanditArcher;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcBanditCivilian;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcBanditLeader;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcBanditMountedArcher;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcBanditMountedSoldier;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcBanditPriest;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcBanditSoldier;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcBanditTrader;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcCustom_1Archer;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcCustom_1Civilian;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcCustom_1Leader;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcCustom_1MountedArcher;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcCustom_1MountedSoldier;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcCustom_1Priest;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcCustom_1Soldier;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcCustom_1Trader;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcCustom_2Archer;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcCustom_2Civilian;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcCustom_2Leader;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcCustom_2MountedArcher;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcCustom_2MountedSoldier;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcCustom_2Priest;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcCustom_2Soldier;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcCustom_2Trader;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcCustom_3Archer;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcCustom_3Civilian;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcCustom_3Leader;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcCustom_3MountedArcher;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcCustom_3MountedSoldier;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcCustom_3Priest;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcCustom_3Soldier;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcCustom_3Trader;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcDesertArcher;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcDesertCivilian;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcDesertLeader;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcDesertMountedArcher;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcDesertMountedSoldier;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcDesertPriest;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcDesertSoldier;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcDesertTrader;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcFaction;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcNativeArcher;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcNativeCivilian;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcNativeLeader;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcNativeMountedArcher;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcNativeMountedSoldier;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcNativePriest;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcNativeSoldier;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcNativeTrader;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcPirateArcher;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcPirateCivilian;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcPirateLeader;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcPirateMountedArcher;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcPirateMountedSoldier;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcPiratePriest;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcPirateSoldier;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcPirateTrader;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcVikingArcher;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcVikingCivilian;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcVikingLeader;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcVikingMountedArcher;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcVikingMountedSoldier;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcVikingPriest;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcVikingSoldier;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcVikingTrader;
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
  addPlayerOwnedNpcs();  
  addBandits();
  addDesertNatives();
  addJungleNatives();
  addPirates();
  addVikings();
  addCustom1();
  addCustom2();
  addCustom3();
  }

private static void addPlayerOwnedNpcs()
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
  
  reg = new NpcDeclaration(NpcTrader.class, AWEntityRegistry.NPC_TRADER, nextID++, AncientWarfareNPC.instance, 120, 3, true, "trader")
    {    
    @Override
    public Entity createEntity(World world)
      {
      return new NpcTrader(world);
      }
    };
  addNpcRegistration(reg, "trader", "ancientwarfare:npc/spawner_trader");
  
  reg = new NpcDeclaration(NpcPriest.class, AWEntityRegistry.NPC_PRIEST, nextID++, AncientWarfareNPC.instance, 120, 3, true, "priest")
    {    
    @Override
    public Entity createEntity(World world)
      {
      return new NpcPriest(world);
      }
    };
  addNpcRegistration(reg, "priest", "ancientwarfare:npc/spawner_priest");
  
  reg = new NpcDeclaration(NpcBard.class, AWEntityRegistry.NPC_BARD, nextID++, AncientWarfareNPC.instance, 120, 3, true, "bard")
    {    
    @Override
    public Entity createEntity(World world)
      {
      return new NpcBard(world);
      }
    };
  addNpcRegistration(reg, "bard", "ancientwarfare:npc/spawner_bard");
  }

private static void addBandits()
  {
  NpcFactionDeclaration reg;
  /**
   * BANDITS
   */
  reg = new NpcFactionDeclaration(NpcBanditArcher.class, AWEntityRegistry.NPC_FACTION_BANDIT_ARCHER, nextID++, AncientWarfareNPC.instance, 120, 3, true, "bandit.archer")
    {    
    @Override
    public Entity createEntity(World world)
      {
      return new NpcBanditArcher(world);
      }
    };
  addNpcRegistration(reg, "bandit.archer", "ancientwarfare:npc/spawner_bandit_archer");
  addNpcSubtypeEntry("bandit.archer", "elite", "ancientwarfare:npc/spawner_bandit_archer");
  
  reg = new NpcFactionDeclaration(NpcBanditSoldier.class, AWEntityRegistry.NPC_FACTION_BANDIT_SOLDIER, nextID++, AncientWarfareNPC.instance, 120, 3, true, "bandit.soldier")
    {    
    @Override
    public Entity createEntity(World world)
      {
      return new NpcBanditSoldier(world);
      }
    };
  addNpcRegistration(reg, "bandit.soldier", "ancientwarfare:npc/spawner_bandit_soldier");
  addNpcSubtypeEntry("bandit.soldier", "elite", "ancientwarfare:npc/spawner_bandit_soldier");
  
  reg = new NpcFactionDeclaration(NpcBanditLeader.class, AWEntityRegistry.NPC_FACTION_BANDIT_COMMANDER, nextID++, AncientWarfareNPC.instance, 120, 3, true, "bandit.leader")
    {    
    @Override
    public Entity createEntity(World world)
      {
      return new NpcBanditLeader(world);
      }
    };
  addNpcRegistration(reg, "bandit.leader", "ancientwarfare:npc/spawner_bandit_leader");
  addNpcSubtypeEntry("bandit.leader", "elite", "ancientwarfare:npc/spawner_bandit_leader");
  
  reg = new NpcFactionDeclaration(NpcBanditPriest.class, AWEntityRegistry.NPC_FACTION_BANDIT_PRIEST, nextID++, AncientWarfareNPC.instance, 120, 3, true, "bandit.priest")
    {    
    @Override
    public Entity createEntity(World world)
      {
      return new NpcBanditPriest(world);
      }
    };
  addNpcRegistration(reg, "bandit.priest", "ancientwarfare:npc/spawner_bandit_priest");
  
  reg = new NpcFactionDeclaration(NpcBanditTrader.class, AWEntityRegistry.NPC_FACTION_BANDIT_TRADER, nextID++, AncientWarfareNPC.instance, 120, 3, true, "bandit.trader")
    {    
    @Override
    public Entity createEntity(World world)
      {
      return new NpcBanditTrader(world);
      }
    };
  addNpcRegistration(reg, "bandit.trader", "ancientwarfare:npc/spawner_bandit_trader");
  
  reg = new NpcFactionDeclaration(NpcBanditMountedSoldier.class, AWEntityRegistry.NPC_FACTION_BANDIT_CAVALRY, nextID++, AncientWarfareNPC.instance, 120, 3, true, "bandit.cavalry")
    {    
    @Override
    public Entity createEntity(World world)
      {
      return new NpcBanditMountedSoldier(world);
      }
    };
  addNpcRegistration(reg, "bandit.cavalry", "ancientwarfare:npc/spawner_bandit_soldier");
  
  reg = new NpcFactionDeclaration(NpcBanditMountedArcher.class, AWEntityRegistry.NPC_FACTION_BANDIT_MOUNTED_ARCHER, nextID++, AncientWarfareNPC.instance, 120, 3, true, "bandit.mounted_archer")
    {    
    @Override
    public Entity createEntity(World world)
      {
      return new NpcBanditMountedArcher(world);
      }
    };
  addNpcRegistration(reg, "bandit.mounted_archer", "ancientwarfare:npc/spawner_bandit_archer");
  
  reg = new NpcFactionDeclaration(NpcBanditCivilian.class, AWEntityRegistry.NPC_FACTION_BANDIT_CIVILIAN, nextID++, AncientWarfareNPC.instance, 120, 3, true, "bandit.civilian")
    {     
    @Override
    public Entity createEntity(World world)
      {
      return new NpcBanditCivilian(world);
      }    
    };
  addNpcRegistration(reg, "bandit.civilian", "ancientwarfare:npc/spawner_bandit_civilian_male");
  addNpcSubtypeEntry("bandit.civilian", "male", "ancientwarfare:npc/spawner_bandit_civilian_male");
  addNpcSubtypeEntry("bandit.civilian", "female", "ancientwarfare:npc/spawner_bandit_civilian_female");
  reg.setCanSpawnBaseType(false);
  }

private static void addDesertNatives()
  {
  NpcFactionDeclaration reg;
  /**
   * DESERT NATIVES
   */
  reg = new NpcFactionDeclaration(NpcDesertArcher.class, AWEntityRegistry.NPC_FACTION_DESERT_ARCHER, nextID++, AncientWarfareNPC.instance, 120, 3, true, "desert.archer")
    {    
    @Override
    public Entity createEntity(World world)
      {
      return new NpcDesertArcher(world);
      }
    };
  addNpcRegistration(reg, "desert.archer", "ancientwarfare:npc/spawner_desert_archer");
  addNpcSubtypeEntry("desert.archer", "elite", "ancientwarfare:npc/spawner_desert_archer");
  
  reg = new NpcFactionDeclaration(NpcDesertSoldier.class, AWEntityRegistry.NPC_FACTION_DESERT_SOLDIER, nextID++, AncientWarfareNPC.instance, 120, 3, true, "desert.soldier")
    {    
    @Override
    public Entity createEntity(World world)
      {
      return new NpcDesertSoldier(world);
      }
    };
  addNpcRegistration(reg, "desert.soldier", "ancientwarfare:npc/spawner_desert_soldier");
  addNpcSubtypeEntry("desert.soldier", "elite", "ancientwarfare:npc/spawner_desert_soldier");
  
  reg = new NpcFactionDeclaration(NpcDesertLeader.class, AWEntityRegistry.NPC_FACTION_DESERT_COMMANDER, nextID++, AncientWarfareNPC.instance, 120, 3, true, "desert.leader")
    {    
    @Override
    public Entity createEntity(World world)
      {
      return new NpcDesertLeader(world);
      }
    };
  addNpcRegistration(reg, "desert.leader", "ancientwarfare:npc/spawner_desert_leader");
  addNpcSubtypeEntry("desert.leader", "elite", "ancientwarfare:npc/spawner_desert_leader");
  
  reg = new NpcFactionDeclaration(NpcDesertPriest.class, AWEntityRegistry.NPC_FACTION_DESERT_PRIEST, nextID++, AncientWarfareNPC.instance, 120, 3, true, "desert.priest")
    {    
    @Override
    public Entity createEntity(World world)
      {
      return new NpcDesertPriest(world);
      }
    };
  addNpcRegistration(reg, "desert.priest", "ancientwarfare:npc/spawner_desert_priest");
  
  reg = new NpcFactionDeclaration(NpcDesertTrader.class, AWEntityRegistry.NPC_FACTION_DESERT_TRADER, nextID++, AncientWarfareNPC.instance, 120, 3, true, "desert.trader")
    {    
    @Override
    public Entity createEntity(World world)
      {
      return new NpcDesertTrader(world);
      }
    };
  addNpcRegistration(reg, "desert.trader", "ancientwarfare:npc/spawner_desert_trader");
  
  reg = new NpcFactionDeclaration(NpcDesertMountedSoldier.class, AWEntityRegistry.NPC_FACTION_DESERT_CAVALRY, nextID++, AncientWarfareNPC.instance, 120, 3, true, "desert.cavalry")
    {    
    @Override
    public Entity createEntity(World world)
      {
      return new NpcDesertMountedSoldier(world);
      }
    };
  addNpcRegistration(reg, "desert.cavalry", "ancientwarfare:npc/spawner_desert_soldier");
  
  reg = new NpcFactionDeclaration(NpcDesertMountedArcher.class, AWEntityRegistry.NPC_FACTION_DESERT_MOUNTED_ARCHER, nextID++, AncientWarfareNPC.instance, 120, 3, true, "desert.mounted_archer")
    {    
    @Override
    public Entity createEntity(World world)
      {
      return new NpcDesertMountedArcher(world);
      }
    };
  addNpcRegistration(reg, "desert.mounted_archer", "ancientwarfare:npc/spawner_desert_archer");
  
  reg = new NpcFactionDeclaration(NpcDesertCivilian.class, AWEntityRegistry.NPC_FACTION_DESERT_CIVILIAN, nextID++, AncientWarfareNPC.instance, 120, 3, true, "desert.civilian")
    {     
    @Override
    public Entity createEntity(World world)
      {
      return new NpcDesertCivilian(world);
      }    
    };
  addNpcRegistration(reg, "desert.civilian", "ancientwarfare:npc/spawner_desert_civilian_male");
  addNpcSubtypeEntry("desert.civilian", "male", "ancientwarfare:npc/spawner_desert_civilian_male");
  addNpcSubtypeEntry("desert.civilian", "female", "ancientwarfare:npc/spawner_desert_civilian_female");
  reg.setCanSpawnBaseType(false);
  }

private static void addJungleNatives()
  {
  NpcFactionDeclaration reg;
  /**
   * JUNGLE NATIVES
   */
  reg = new NpcFactionDeclaration(NpcNativeArcher.class, AWEntityRegistry.NPC_FACTION_NATIVE_ARCHER, nextID++, AncientWarfareNPC.instance, 120, 3, true, "native.archer")
    {    
    @Override
    public Entity createEntity(World world)
      {
      return new NpcNativeArcher(world);
      }
    };
  addNpcRegistration(reg, "native.archer", "ancientwarfare:npc/spawner_native_archer");
  addNpcSubtypeEntry("native.archer", "elite", "ancientwarfare:npc/spawner_native_archer");
  
  reg = new NpcFactionDeclaration(NpcNativeSoldier.class, AWEntityRegistry.NPC_FACTION_NATIVE_SOLDIER, nextID++, AncientWarfareNPC.instance, 120, 3, true, "native.soldier")
    {    
    @Override
    public Entity createEntity(World world)
      {
      return new NpcNativeSoldier(world);
      }
    };
  addNpcRegistration(reg, "native.soldier", "ancientwarfare:npc/spawner_native_soldier");
  addNpcSubtypeEntry("native.soldier", "elite", "ancientwarfare:npc/spawner_native_soldier");
  
  reg = new NpcFactionDeclaration(NpcNativeLeader.class, AWEntityRegistry.NPC_FACTION_NATIVE_COMMANDER, nextID++, AncientWarfareNPC.instance, 120, 3, true, "native.leader")
    {    
    @Override
    public Entity createEntity(World world)
      {
      return new NpcNativeLeader(world);
      }
    };
  addNpcRegistration(reg, "native.leader", "ancientwarfare:npc/spawner_native_leader");
  addNpcSubtypeEntry("native.leader", "elite", "ancientwarfare:npc/spawner_native_leader");
  
  reg = new NpcFactionDeclaration(NpcNativePriest.class, AWEntityRegistry.NPC_FACTION_NATIVE_PRIEST, nextID++, AncientWarfareNPC.instance, 120, 3, true, "native.priest")
    {    
    @Override
    public Entity createEntity(World world)
      {
      return new NpcNativePriest(world);
      }
    };
  addNpcRegistration(reg, "native.priest", "ancientwarfare:npc/spawner_native_priest");
  
  reg = new NpcFactionDeclaration(NpcNativeTrader.class, AWEntityRegistry.NPC_FACTION_NATIVE_TRADER, nextID++, AncientWarfareNPC.instance, 120, 3, true, "native.trader")
    {    
    @Override
    public Entity createEntity(World world)
      {
      return new NpcNativeTrader(world);
      }
    };
  addNpcRegistration(reg, "native.trader", "ancientwarfare:npc/spawner_native_trader");
  
  reg = new NpcFactionDeclaration(NpcNativeMountedSoldier.class, AWEntityRegistry.NPC_FACTION_NATIVE_CAVALRY, nextID++, AncientWarfareNPC.instance, 120, 3, true, "native.cavalry")
    {    
    @Override
    public Entity createEntity(World world)
      {
      return new NpcNativeMountedSoldier(world);
      }
    };
  addNpcRegistration(reg, "native.cavalry", "ancientwarfare:npc/spawner_native_soldier");
  
  reg = new NpcFactionDeclaration(NpcNativeMountedArcher.class, AWEntityRegistry.NPC_FACTION_NATIVE_MOUNTED_ARCHER, nextID++, AncientWarfareNPC.instance, 120, 3, true, "native.mounted_archer")
    {    
    @Override
    public Entity createEntity(World world)
      {
      return new NpcNativeMountedArcher(world);
      }
    };
  addNpcRegistration(reg, "native.mounted_archer", "ancientwarfare:npc/spawner_native_archer");
  
  reg = new NpcFactionDeclaration(NpcNativeCivilian.class, AWEntityRegistry.NPC_FACTION_NATIVE_CIVILIAN, nextID++, AncientWarfareNPC.instance, 120, 3, true, "native.civilian")
    {     
    @Override
    public Entity createEntity(World world)
      {
      return new NpcNativeCivilian(world);
      }    
    };
  addNpcRegistration(reg, "native.civilian", "ancientwarfare:npc/spawner_native_civilian_male");    
  addNpcSubtypeEntry("native.civilian", "male", "ancientwarfare:npc/spawner_native_civilian_male");
  addNpcSubtypeEntry("native.civilian", "female", "ancientwarfare:npc/spawner_native_civilian_female");
  reg.setCanSpawnBaseType(false);
  }

private static void addPirates()
  {
  NpcFactionDeclaration reg;
  /**
   * PIRATES
   */
  reg = new NpcFactionDeclaration(NpcPirateArcher.class, AWEntityRegistry.NPC_FACTION_PIRATE_ARCHER, nextID++, AncientWarfareNPC.instance, 120, 3, true, "pirate.archer")
    {    
    @Override
    public Entity createEntity(World world)
      {
      return new NpcPirateArcher(world);
      }
    };
  addNpcRegistration(reg, "pirate.archer", "ancientwarfare:npc/spawner_pirate_archer");
  addNpcSubtypeEntry("pirate.archer", "elite", "ancientwarfare:npc/spawner_pirate_archer");
  
  reg = new NpcFactionDeclaration(NpcPirateSoldier.class, AWEntityRegistry.NPC_FACTION_PIRATE_SOLDIER, nextID++, AncientWarfareNPC.instance, 120, 3, true, "pirate.soldier")
    {    
    @Override
    public Entity createEntity(World world)
      {
      return new NpcPirateSoldier(world);
      }
    };
  addNpcRegistration(reg, "pirate.soldier", "ancientwarfare:npc/spawner_pirate_soldier");
  addNpcSubtypeEntry("pirate.soldier", "elite", "ancientwarfare:npc/spawner_pirate_soldier");
  
  reg = new NpcFactionDeclaration(NpcPirateLeader.class, AWEntityRegistry.NPC_FACTION_PIRATE_COMMANDER, nextID++, AncientWarfareNPC.instance, 120, 3, true, "pirate.leader")
    {    
    @Override
    public Entity createEntity(World world)
      {
      return new NpcPirateLeader(world);
      }
    };
  addNpcRegistration(reg, "pirate.leader", "ancientwarfare:npc/spawner_pirate_leader");
  addNpcSubtypeEntry("pirate.leader", "elite", "ancientwarfare:npc/spawner_pirate_leader");
  
  reg = new NpcFactionDeclaration(NpcPiratePriest.class, AWEntityRegistry.NPC_FACTION_PIRATE_PRIEST, nextID++, AncientWarfareNPC.instance, 120, 3, true, "pirate.priest")
    {    
    @Override
    public Entity createEntity(World world)
      {
      return new NpcPiratePriest(world);
      }
    };
  addNpcRegistration(reg, "pirate.priest", "ancientwarfare:npc/spawner_pirate_priest");
  
  reg = new NpcFactionDeclaration(NpcPirateTrader.class, AWEntityRegistry.NPC_FACTION_PIRATE_TRADER, nextID++, AncientWarfareNPC.instance, 120, 3, true, "pirate.trader")
    {    
    @Override
    public Entity createEntity(World world)
      {
      return new NpcPirateTrader(world);
      }
    };
  addNpcRegistration(reg, "pirate.trader", "ancientwarfare:npc/spawner_pirate_trader");
  
  reg = new NpcFactionDeclaration(NpcPirateMountedSoldier.class, AWEntityRegistry.NPC_FACTION_PIRATE_CAVALRY, nextID++, AncientWarfareNPC.instance, 120, 3, true, "pirate.cavalry")
    {    
    @Override
    public Entity createEntity(World world)
      {
      return new NpcPirateMountedSoldier(world);
      }
    };
  addNpcRegistration(reg, "pirate.cavalry", "ancientwarfare:npc/spawner_pirate_soldier");
  
  reg = new NpcFactionDeclaration(NpcPirateMountedArcher.class, AWEntityRegistry.NPC_FACTION_PIRATE_MOUNTED_ARCHER, nextID++, AncientWarfareNPC.instance, 120, 3, true, "pirate.mounted_archer")
    {    
    @Override
    public Entity createEntity(World world)
      {
      return new NpcPirateMountedArcher(world);
      }
    };
  addNpcRegistration(reg, "pirate.mounted_archer", "ancientwarfare:npc/spawner_pirate_archer");
  
  reg = new NpcFactionDeclaration(NpcPirateCivilian.class, AWEntityRegistry.NPC_FACTION_PIRATE_CIVILIAN, nextID++, AncientWarfareNPC.instance, 120, 3, true, "pirate.civilian")
    {     
    @Override
    public Entity createEntity(World world)
      {
      return new NpcPirateCivilian(world);
      }    
    };
  addNpcRegistration(reg, "pirate.civilian", "ancientwarfare:npc/spawner_pirate_civilian_male");    
  addNpcSubtypeEntry("pirate.civilian", "male", "ancientwarfare:npc/spawner_pirate_civilian_male");
  addNpcSubtypeEntry("pirate.civilian", "female", "ancientwarfare:npc/spawner_pirate_civilian_female");
  reg.setCanSpawnBaseType(false);
  }

private static void addVikings()
  {
  NpcFactionDeclaration reg;
  /**
   * VIKINGS
   */
  reg = new NpcFactionDeclaration(NpcVikingArcher.class, AWEntityRegistry.NPC_FACTION_VIKING_ARCHER, nextID++, AncientWarfareNPC.instance, 120, 3, true, "viking.archer")
    {    
    @Override
    public Entity createEntity(World world)
      {
      return new NpcVikingArcher(world);
      }
    };
  addNpcRegistration(reg, "viking.archer", "ancientwarfare:npc/spawner_viking_archer");
  addNpcSubtypeEntry("viking.archer", "elite", "ancientwarfare:npc/spawner_viking_archer");
  
  reg = new NpcFactionDeclaration(NpcVikingSoldier.class, AWEntityRegistry.NPC_FACTION_VIKING_SOLDIER, nextID++, AncientWarfareNPC.instance, 120, 3, true, "viking.soldier")
    {    
    @Override
    public Entity createEntity(World world)
      {
      return new NpcVikingSoldier(world);
      }
    };
  addNpcRegistration(reg, "viking.soldier", "ancientwarfare:npc/spawner_viking_soldier");
  addNpcSubtypeEntry("viking.soldier", "elite", "ancientwarfare:npc/spawner_viking_soldier");
  
  reg = new NpcFactionDeclaration(NpcVikingLeader.class, AWEntityRegistry.NPC_FACTION_VIKING_COMMANDER, nextID++, AncientWarfareNPC.instance, 120, 3, true, "viking.leader")
    {    
    @Override
    public Entity createEntity(World world)
      {
      return new NpcVikingLeader(world);
      }
    };
  addNpcRegistration(reg, "viking.leader", "ancientwarfare:npc/spawner_viking_leader");
  addNpcSubtypeEntry("viking.leader", "elite", "ancientwarfare:npc/spawner_viking_leader");
  
  reg = new NpcFactionDeclaration(NpcVikingPriest.class, AWEntityRegistry.NPC_FACTION_VIKING_PRIEST, nextID++, AncientWarfareNPC.instance, 120, 3, true, "viking.priest")
    {    
    @Override
    public Entity createEntity(World world)
      {
      return new NpcVikingPriest(world);
      }
    };
  addNpcRegistration(reg, "viking.priest", "ancientwarfare:npc/spawner_viking_priest");
  
  reg = new NpcFactionDeclaration(NpcVikingTrader.class, AWEntityRegistry.NPC_FACTION_VIKING_TRADER, nextID++, AncientWarfareNPC.instance, 120, 3, true, "viking.trader")
    {    
    @Override
    public Entity createEntity(World world)
      {
      return new NpcVikingTrader(world);
      }
    };
  addNpcRegistration(reg, "viking.trader", "ancientwarfare:npc/spawner_viking_trader");
  
  reg = new NpcFactionDeclaration(NpcVikingMountedSoldier.class, AWEntityRegistry.NPC_FACTION_VIKING_CAVALRY, nextID++, AncientWarfareNPC.instance, 120, 3, true, "viking.cavalry")
    {    
    @Override
    public Entity createEntity(World world)
      {
      return new NpcVikingMountedSoldier(world);
      }
    };
  addNpcRegistration(reg, "viking.cavalry", "ancientwarfare:npc/spawner_viking_soldier");
  
  reg = new NpcFactionDeclaration(NpcVikingMountedArcher.class, AWEntityRegistry.NPC_FACTION_VIKING_MOUNTED_ARCHER, nextID++, AncientWarfareNPC.instance, 120, 3, true, "viking.mounted_archer")
    {    
    @Override
    public Entity createEntity(World world)
      {
      return new NpcVikingMountedArcher(world);
      }
    };
  addNpcRegistration(reg, "viking.mounted_archer", "ancientwarfare:npc/spawner_viking_archer");
  
  reg = new NpcFactionDeclaration(NpcVikingCivilian.class, AWEntityRegistry.NPC_FACTION_VIKING_CIVILIAN, nextID++, AncientWarfareNPC.instance, 120, 3, true, "viking.civilian")
    {     
    @Override
    public Entity createEntity(World world)
      {
      return new NpcVikingCivilian(world);
      }    
    };
    
  addNpcRegistration(reg, "viking.civilian", "ancientwarfare:npc/spawner_viking_civilian_male");
  addNpcSubtypeEntry("viking.civilian", "male", "ancientwarfare:npc/spawner_viking_civilian_male");
  addNpcSubtypeEntry("viking.civilian", "female", "ancientwarfare:npc/spawner_viking_civilian_female");
  reg.setCanSpawnBaseType(false);
  }

private static void addCustom1()
  {
  NpcFactionDeclaration reg;
  /**
   * CUSTOM_1S
   */
  reg = new NpcFactionDeclaration(NpcCustom_1Archer.class, AWEntityRegistry.NPC_FACTION_CUSTOM_1_ARCHER, nextID++, AncientWarfareNPC.instance, 120, 3, true, "custom_1.archer")
    {    
    @Override
    public Entity createEntity(World world)
      {
      return new NpcCustom_1Archer(world);
      }
    };
  addNpcRegistration(reg, "custom_1.archer", "ancientwarfare:npc/spawner_custom_1_archer");
  addNpcSubtypeEntry("custom_1.archer", "elite", "ancientwarfare:npc/spawner_custom_1_archer");
  
  reg = new NpcFactionDeclaration(NpcCustom_1Soldier.class, AWEntityRegistry.NPC_FACTION_CUSTOM_1_SOLDIER, nextID++, AncientWarfareNPC.instance, 120, 3, true, "custom_1.soldier")
    {    
    @Override
    public Entity createEntity(World world)
      {
      return new NpcCustom_1Soldier(world);
      }
    };
  addNpcRegistration(reg, "custom_1.soldier", "ancientwarfare:npc/spawner_custom_1_soldier");
  addNpcSubtypeEntry("custom_1.soldier", "elite", "ancientwarfare:npc/spawner_custom_1_soldier");
  
  reg = new NpcFactionDeclaration(NpcCustom_1Leader.class, AWEntityRegistry.NPC_FACTION_CUSTOM_1_COMMANDER, nextID++, AncientWarfareNPC.instance, 120, 3, true, "custom_1.leader")
    {    
    @Override
    public Entity createEntity(World world)
      {
      return new NpcCustom_1Leader(world);
      }
    };
  addNpcRegistration(reg, "custom_1.leader", "ancientwarfare:npc/spawner_custom_1_leader");
  addNpcSubtypeEntry("custom_1.leader", "elite", "ancientwarfare:npc/spawner_custom_1_leader");
  
  reg = new NpcFactionDeclaration(NpcCustom_1Priest.class, AWEntityRegistry.NPC_FACTION_CUSTOM_1_PRIEST, nextID++, AncientWarfareNPC.instance, 120, 3, true, "custom_1.priest")
    {    
    @Override
    public Entity createEntity(World world)
      {
      return new NpcCustom_1Priest(world);
      }
    };
  addNpcRegistration(reg, "custom_1.priest", "ancientwarfare:npc/spawner_custom_1_priest");
  
  reg = new NpcFactionDeclaration(NpcCustom_1Trader.class, AWEntityRegistry.NPC_FACTION_CUSTOM_1_TRADER, nextID++, AncientWarfareNPC.instance, 120, 3, true, "custom_1.trader")
    {    
    @Override
    public Entity createEntity(World world)
      {
      return new NpcCustom_1Trader(world);
      }
    };
  addNpcRegistration(reg, "custom_1.trader", "ancientwarfare:npc/spawner_custom_1_trader");
  
  reg = new NpcFactionDeclaration(NpcCustom_1MountedSoldier.class, AWEntityRegistry.NPC_FACTION_CUSTOM_1_CAVALRY, nextID++, AncientWarfareNPC.instance, 120, 3, true, "custom_1.cavalry")
    {    
    @Override
    public Entity createEntity(World world)
      {
      return new NpcCustom_1MountedSoldier(world);
      }
    };
  addNpcRegistration(reg, "custom_1.cavalry", "ancientwarfare:npc/spawner_custom_1_soldier");
  
  reg = new NpcFactionDeclaration(NpcCustom_1MountedArcher.class, AWEntityRegistry.NPC_FACTION_CUSTOM_1_MOUNTED_ARCHER, nextID++, AncientWarfareNPC.instance, 120, 3, true, "custom_1.mounted_archer")
    {    
    @Override
    public Entity createEntity(World world)
      {
      return new NpcCustom_1MountedArcher(world);
      }
    };
  addNpcRegistration(reg, "custom_1.mounted_archer", "ancientwarfare:npc/spawner_custom_1_archer");
  
  reg = new NpcFactionDeclaration(NpcCustom_1Civilian.class, AWEntityRegistry.NPC_FACTION_CUSTOM_1_CIVILIAN, nextID++, AncientWarfareNPC.instance, 120, 3, true, "custom_1.civilian")
    {     
    @Override
    public Entity createEntity(World world)
      {
      return new NpcCustom_1Civilian(world);
      }    
    };
  addNpcRegistration(reg, "custom_1.civilian", "ancientwarfare:npc/spawner_custom_1_civilian_male");
  addNpcSubtypeEntry("custom_1.civilian", "male", "ancientwarfare:npc/spawner_custom_1_civilian_male");
  addNpcSubtypeEntry("custom_1.civilian", "female", "ancientwarfare:npc/spawner_custom_1_civilian_female");
  reg.setCanSpawnBaseType(false);
  }

private static void addCustom2()
  {
  NpcFactionDeclaration reg;
  /**
   * CUSTOM_2S
   */
  reg = new NpcFactionDeclaration(NpcCustom_2Archer.class, AWEntityRegistry.NPC_FACTION_CUSTOM_2_ARCHER, nextID++, AncientWarfareNPC.instance, 120, 3, true, "custom_2.archer")
    {    
    @Override
    public Entity createEntity(World world)
      {
      return new NpcCustom_2Archer(world);
      }
    };
  addNpcRegistration(reg, "custom_2.archer", "ancientwarfare:npc/spawner_custom_2_archer");
  addNpcSubtypeEntry("custom_2.archer", "elite", "ancientwarfare:npc/spawner_custom_2_archer");
  
  reg = new NpcFactionDeclaration(NpcCustom_2Soldier.class, AWEntityRegistry.NPC_FACTION_CUSTOM_2_SOLDIER, nextID++, AncientWarfareNPC.instance, 120, 3, true, "custom_2.soldier")
    {    
    @Override
    public Entity createEntity(World world)
      {
      return new NpcCustom_2Soldier(world);
      }
    };
  addNpcRegistration(reg, "custom_2.soldier", "ancientwarfare:npc/spawner_custom_2_soldier");
  addNpcSubtypeEntry("custom_2.soldier", "elite", "ancientwarfare:npc/spawner_custom_2_soldier");
  
  reg = new NpcFactionDeclaration(NpcCustom_2Leader.class, AWEntityRegistry.NPC_FACTION_CUSTOM_2_COMMANDER, nextID++, AncientWarfareNPC.instance, 120, 3, true, "custom_2.leader")
    {    
    @Override
    public Entity createEntity(World world)
      {
      return new NpcCustom_2Leader(world);
      }
    };
  addNpcRegistration(reg, "custom_2.leader", "ancientwarfare:npc/spawner_custom_2_leader");
  addNpcSubtypeEntry("custom_2.leader", "elite", "ancientwarfare:npc/spawner_custom_2_leader");
  
  reg = new NpcFactionDeclaration(NpcCustom_2Priest.class, AWEntityRegistry.NPC_FACTION_CUSTOM_2_PRIEST, nextID++, AncientWarfareNPC.instance, 120, 3, true, "custom_2.priest")
    {    
    @Override
    public Entity createEntity(World world)
      {
      return new NpcCustom_2Priest(world);
      }
    };
  addNpcRegistration(reg, "custom_2.priest", "ancientwarfare:npc/spawner_custom_2_priest");
  
  reg = new NpcFactionDeclaration(NpcCustom_2Trader.class, AWEntityRegistry.NPC_FACTION_CUSTOM_2_TRADER, nextID++, AncientWarfareNPC.instance, 120, 3, true, "custom_2.trader")
    {    
    @Override
    public Entity createEntity(World world)
      {
      return new NpcCustom_2Trader(world);
      }
    };
  addNpcRegistration(reg, "custom_2.trader", "ancientwarfare:npc/spawner_custom_2_trader");
  
  reg = new NpcFactionDeclaration(NpcCustom_2MountedSoldier.class, AWEntityRegistry.NPC_FACTION_CUSTOM_2_CAVALRY, nextID++, AncientWarfareNPC.instance, 120, 3, true, "custom_2.cavalry")
    {    
    @Override
    public Entity createEntity(World world)
      {
      return new NpcCustom_2MountedSoldier(world);
      }
    };
  addNpcRegistration(reg, "custom_2.cavalry", "ancientwarfare:npc/spawner_custom_2_soldier");
  
  reg = new NpcFactionDeclaration(NpcCustom_2MountedArcher.class, AWEntityRegistry.NPC_FACTION_CUSTOM_2_MOUNTED_ARCHER, nextID++, AncientWarfareNPC.instance, 120, 3, true, "custom_2.mounted_archer")
    {    
    @Override
    public Entity createEntity(World world)
      {
      return new NpcCustom_2MountedArcher(world);
      }
    };
  addNpcRegistration(reg, "custom_2.mounted_archer", "ancientwarfare:npc/spawner_custom_2_archer");
  
  reg = new NpcFactionDeclaration(NpcCustom_2Civilian.class, AWEntityRegistry.NPC_FACTION_CUSTOM_2_CIVILIAN, nextID++, AncientWarfareNPC.instance, 120, 3, true, "custom_2.civilian")
    {     
    @Override
    public Entity createEntity(World world)
      {
      return new NpcCustom_2Civilian(world);
      }    
    };
  addNpcRegistration(reg, "custom_2.civilian", "ancientwarfare:npc/spawner_custom_2_civilian_male");
  addNpcSubtypeEntry("custom_2.civilian", "male", "ancientwarfare:npc/spawner_custom_2_civilian_male");
  addNpcSubtypeEntry("custom_2.civilian", "female", "ancientwarfare:npc/spawner_custom_2_civilian_female");
  reg.setCanSpawnBaseType(false);
  }

private static void addCustom3()
  {

  NpcFactionDeclaration reg;
  /**
   * CUSTOM_3S
   */
  reg = new NpcFactionDeclaration(NpcCustom_3Archer.class, AWEntityRegistry.NPC_FACTION_CUSTOM_3_ARCHER, nextID++, AncientWarfareNPC.instance, 120, 3, true, "custom_3.archer")
    {    
    @Override
    public Entity createEntity(World world)
      {
      return new NpcCustom_3Archer(world);
      }
    };
  addNpcRegistration(reg, "custom_3.archer", "ancientwarfare:npc/spawner_custom_3_archer");
  addNpcSubtypeEntry("custom_3.archer", "elite", "ancientwarfare:npc/spawner_custom_3_archer");
  
  reg = new NpcFactionDeclaration(NpcCustom_3Soldier.class, AWEntityRegistry.NPC_FACTION_CUSTOM_3_SOLDIER, nextID++, AncientWarfareNPC.instance, 120, 3, true, "custom_3.soldier")
    {    
    @Override
    public Entity createEntity(World world)
      {
      return new NpcCustom_3Soldier(world);
      }
    };
  addNpcRegistration(reg, "custom_3.soldier", "ancientwarfare:npc/spawner_custom_3_soldier");
  addNpcSubtypeEntry("custom_3.soldier", "elite", "ancientwarfare:npc/spawner_custom_3_soldier");
  
  reg = new NpcFactionDeclaration(NpcCustom_3Leader.class, AWEntityRegistry.NPC_FACTION_CUSTOM_3_COMMANDER, nextID++, AncientWarfareNPC.instance, 120, 3, true, "custom_3.leader")
    {    
    @Override
    public Entity createEntity(World world)
      {
      return new NpcCustom_3Leader(world);
      }
    };
  addNpcRegistration(reg, "custom_3.leader", "ancientwarfare:npc/spawner_custom_3_leader");
  addNpcSubtypeEntry("custom_3.leader", "elite", "ancientwarfare:npc/spawner_custom_3_leader");
  
  reg = new NpcFactionDeclaration(NpcCustom_3Priest.class, AWEntityRegistry.NPC_FACTION_CUSTOM_3_PRIEST, nextID++, AncientWarfareNPC.instance, 120, 3, true, "custom_3.priest")
    {    
    @Override
    public Entity createEntity(World world)
      {
      return new NpcCustom_3Priest(world);
      }
    };
  addNpcRegistration(reg, "custom_3.priest", "ancientwarfare:npc/spawner_custom_3_priest");
  
  reg = new NpcFactionDeclaration(NpcCustom_3Trader.class, AWEntityRegistry.NPC_FACTION_CUSTOM_3_TRADER, nextID++, AncientWarfareNPC.instance, 120, 3, true, "custom_3.trader")
    {    
    @Override
    public Entity createEntity(World world)
      {
      return new NpcCustom_3Trader(world);
      }
    };
  addNpcRegistration(reg, "custom_3.trader", "ancientwarfare:npc/spawner_custom_3_trader");
  
  reg = new NpcFactionDeclaration(NpcCustom_3MountedSoldier.class, AWEntityRegistry.NPC_FACTION_CUSTOM_3_CAVALRY, nextID++, AncientWarfareNPC.instance, 120, 3, true, "custom_3.cavalry")
    {    
    @Override
    public Entity createEntity(World world)
      {
      return new NpcCustom_3MountedSoldier(world);
      }
    };
  addNpcRegistration(reg, "custom_3.cavalry", "ancientwarfare:npc/spawner_custom_3_soldier");
  
  reg = new NpcFactionDeclaration(NpcCustom_3MountedArcher.class, AWEntityRegistry.NPC_FACTION_CUSTOM_3_MOUNTED_ARCHER, nextID++, AncientWarfareNPC.instance, 120, 3, true, "custom_3.mounted_archer")
    {    
    @Override
    public Entity createEntity(World world)
      {
      return new NpcCustom_3MountedArcher(world);
      }
    };
  addNpcRegistration(reg, "custom_3.mounted_archer", "ancientwarfare:npc/spawner_custom_3_archer");
  
  reg = new NpcFactionDeclaration(NpcCustom_3Civilian.class, AWEntityRegistry.NPC_FACTION_CUSTOM_3_CIVILIAN, nextID++, AncientWarfareNPC.instance, 120, 3, true, "custom_3.civilian")
    {     
    @Override
    public Entity createEntity(World world)
      {
      return new NpcCustom_3Civilian(world);
      }    
    };
  addNpcRegistration(reg, "custom_3.civilian", "ancientwarfare:npc/spawner_custom_3_civilian_male");
  addNpcSubtypeEntry("custom_3.civilian", "male", "ancientwarfare:npc/spawner_custom_3_civilian_male");
  addNpcSubtypeEntry("custom_3.civilian", "female", "ancientwarfare:npc/spawner_custom_3_civilian_female");
  reg.setCanSpawnBaseType(false);
  }

/**
 * has to be called during post-init so that all items/etc are fully initialzed
 */
public static void loadNpcSubtypeEquipment()
  {
  addNpcSubtypeEquipment("worker", "farmer", new ItemStack(Items.iron_hoe));
  addNpcSubtypeEquipment("worker", "miner", new ItemStack(Items.iron_pickaxe));
  addNpcSubtypeEquipment("worker", "lumberjack", new ItemStack(Items.iron_axe));  
  addNpcSubtypeEquipment("worker", "researcher", new ItemStack(AWItems.quillIron));
  addNpcSubtypeEquipment("worker", "craftsman", new ItemStack(AWItems.automationHammerIron));
      
  addNpcSubtypeEquipment("combat", "commander", new ItemStack(AWNpcItemLoader.commandBatonIron));
  addNpcSubtypeEquipment("combat", "soldier", new ItemStack(Items.iron_sword));
  addNpcSubtypeEquipment("combat", "archer", new ItemStack(Items.bow));
  addNpcSubtypeEquipment("combat", "engineer", new ItemStack(AWItems.automationHammerIron));
  addNpcSubtypeEquipment("combat", "medic", new ItemStack(Items.iron_axe));  
  }

private static void addNpcRegistration(NpcDeclaration reg, String npcName, String icon)
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

private static void addNpcSubtypeEntry(String npcType, String npcSubtype, String icon)
  {
  if(!npcMap.containsKey(npcType)){throw new IllegalArgumentException("npc type must first be mapped");}
  npcMap.get(npcType).addSubtype(npcSubtype, icon);
  AWNpcItemLoader.npcSpawner.addNpcType(npcType+"."+npcSubtype, icon);
  }

private static void addNpcSubtypeEquipment(String npcType, String npcSubtype, ItemStack equipment)
  {
  if(!npcMap.containsKey(npcType)){throw new IllegalArgumentException("npc type must first be mapped");}
  NpcDeclaration reg = npcMap.get(npcType);
  if(!reg.subTypeIcons.containsKey(npcSubtype)){throw new IllegalArgumentException("npc subtype must first be mapped");}
  reg.spawnEquipment.put(npcSubtype, equipment);
  }

/**
 * used by npc spawner item to get the sub-items
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
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
  npc.setSubtype(subType);  
  return npc;
  }

}

}
