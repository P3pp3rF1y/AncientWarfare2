package net.shadowmage.ancientwarfare.npc.entity;

import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.api.AWItems;
import net.shadowmage.ancientwarfare.core.entity.AWEntityRegistry;
import net.shadowmage.ancientwarfare.core.entity.AWEntityRegistry.EntityDeclaration;
import net.shadowmage.ancientwarfare.npc.AncientWarfareNPC;
import net.shadowmage.ancientwarfare.npc.entity.faction.*;
import net.shadowmage.ancientwarfare.npc.item.AWNpcItemLoader;
import net.shadowmage.ancientwarfare.npc.item.ItemNpcSpawner;

import java.util.HashMap;
import java.util.List;


public class AWNPCEntityLoader {
    private static int nextID = 0;

    /**
     * Npc base type -> NpcDeclaration<br>
     * Used to retrieve declaration for creating entities<br>
     * NpcDeclaration also stores information pertaining to npc-sub-type basic setup
     */
    private static HashMap<String, NpcDeclaration> npcMap = new HashMap<String, NpcDeclaration>();

    public static void load() {
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

    private static void addPlayerOwnedNpcs() {
        NpcDeclaration reg = new NpcDeclaration(NpcCombat.class, AWEntityRegistry.NPC_COMBAT, nextID++, AncientWarfareNPC.instance, 120, 3, true, "combat") {
            @Override
            public Entity createEntity(World world) {
                return new NpcCombat(world);
            }
        };
        addNpcRegistration(reg, "combat", "ancientwarfare:npc/spawner_combat");
        addNpcSubtypeEntry("combat", "commander", "ancientwarfare:npc/spawner_commander");
        addNpcSubtypeEntry("combat", "soldier", "ancientwarfare:npc/spawner_combat");
        addNpcSubtypeEntry("combat", "archer", "ancientwarfare:npc/spawner_archer");
        addNpcSubtypeEntry("combat", "medic", "ancientwarfare:npc/spawner_medic");
        addNpcSubtypeEntry("combat", "engineer", "ancientwarfare:npc/spawner_engineer");

        reg = new NpcDeclaration(NpcWorker.class, AWEntityRegistry.NPC_WORKER, nextID++, AncientWarfareNPC.instance, 120, 3, true, "worker") {
            @Override
            public Entity createEntity(World world) {
                return new NpcWorker(world);
            }
        };
        addNpcRegistration(reg, "worker", "ancientwarfare:npc/spawner_miner");
        addNpcSubtypeEntry("worker", "miner", "ancientwarfare:npc/spawner_miner");
        addNpcSubtypeEntry("worker", "farmer", "ancientwarfare:npc/spawner_farmer");
        addNpcSubtypeEntry("worker", "lumberjack", "ancientwarfare:npc/spawner_lumberjack");
        addNpcSubtypeEntry("worker", "researcher", "ancientwarfare:npc/spawner_researcher");
        addNpcSubtypeEntry("worker", "craftsman", "ancientwarfare:npc/spawner_craftsman");

        reg = new NpcDeclaration(NpcCourier.class, AWEntityRegistry.NPC_COURIER, nextID++, AncientWarfareNPC.instance, 120, 3, true, "courier") {
            @Override
            public Entity createEntity(World world) {
                return new NpcCourier(world);
            }
        };
        addNpcRegistration(reg, "courier", "ancientwarfare:npc/spawner_courier");

        reg = new NpcDeclaration(NpcTrader.class, AWEntityRegistry.NPC_TRADER, nextID++, AncientWarfareNPC.instance, 120, 3, true, "trader") {
            @Override
            public Entity createEntity(World world) {
                return new NpcTrader(world);
            }
        };
        addNpcRegistration(reg, "trader", "ancientwarfare:npc/spawner_trader");

        reg = new NpcDeclaration(NpcPriest.class, AWEntityRegistry.NPC_PRIEST, nextID++, AncientWarfareNPC.instance, 120, 3, true, "priest") {
            @Override
            public Entity createEntity(World world) {
                return new NpcPriest(world);
            }
        };
        addNpcRegistration(reg, "priest", "ancientwarfare:npc/spawner_priest");

        reg = new NpcDeclaration(NpcBard.class, AWEntityRegistry.NPC_BARD, nextID++, AncientWarfareNPC.instance, 120, 3, true, "bard") {
            @Override
            public Entity createEntity(World world) {
                return new NpcBard(world);
            }
        };
        addNpcRegistration(reg, "bard", "ancientwarfare:npc/spawner_bard");
    }

    private static void addBandits() {
        NpcFactionDeclaration reg;
        /**
         * BANDITS
         */
        reg = new NpcFactionDeclaration(NpcBanditArcher.class, AWEntityRegistry.NPC_FACTION_BANDIT_ARCHER, nextID++, AncientWarfareNPC.instance, 120, 3, true, "bandit.archer") {
            @Override
            public Entity createEntity(World world) {
                return new NpcBanditArcher(world);
            }
        };
        addNpcRegistration(reg, "bandit.archer", "ancientwarfare:npc/spawner_bandit_archer");

        reg = new NpcFactionDeclaration(NpcBanditSoldier.class, AWEntityRegistry.NPC_FACTION_BANDIT_SOLDIER, nextID++, AncientWarfareNPC.instance, 120, 3, true, "bandit.soldier") {
            @Override
            public Entity createEntity(World world) {
                return new NpcBanditSoldier(world);
            }
        };
        addNpcRegistration(reg, "bandit.soldier", "ancientwarfare:npc/spawner_bandit_soldier");

        reg = new NpcFactionDeclaration(NpcBanditLeader.class, AWEntityRegistry.NPC_FACTION_BANDIT_COMMANDER, nextID++, AncientWarfareNPC.instance, 120, 3, true, "bandit.leader") {
            @Override
            public Entity createEntity(World world) {
                return new NpcBanditLeader(world);
            }
        };
        addNpcRegistration(reg, "bandit.leader", "ancientwarfare:npc/spawner_bandit_leader");

        reg = new NpcFactionDeclaration(NpcBanditPriest.class, AWEntityRegistry.NPC_FACTION_BANDIT_PRIEST, nextID++, AncientWarfareNPC.instance, 120, 3, true, "bandit.priest") {
            @Override
            public Entity createEntity(World world) {
                return new NpcBanditPriest(world);
            }
        };
        addNpcRegistration(reg, "bandit.priest", "ancientwarfare:npc/spawner_bandit_priest");

        reg = new NpcFactionDeclaration(NpcBanditTrader.class, AWEntityRegistry.NPC_FACTION_BANDIT_TRADER, nextID++, AncientWarfareNPC.instance, 120, 3, true, "bandit.trader") {
            @Override
            public Entity createEntity(World world) {
                return new NpcBanditTrader(world);
            }
        };
        addNpcRegistration(reg, "bandit.trader", "ancientwarfare:npc/spawner_bandit_trader");

        reg = new NpcFactionDeclaration(NpcBanditMountedSoldier.class, AWEntityRegistry.NPC_FACTION_BANDIT_CAVALRY, nextID++, AncientWarfareNPC.instance, 120, 3, true, "bandit.cavalry") {
            @Override
            public Entity createEntity(World world) {
                return new NpcBanditMountedSoldier(world);
            }
        };
        addNpcRegistration(reg, "bandit.cavalry", "ancientwarfare:npc/spawner_bandit_soldier");

        reg = new NpcFactionDeclaration(NpcBanditMountedArcher.class, AWEntityRegistry.NPC_FACTION_BANDIT_MOUNTED_ARCHER, nextID++, AncientWarfareNPC.instance, 120, 3, true, "bandit.mounted_archer") {
            @Override
            public Entity createEntity(World world) {
                return new NpcBanditMountedArcher(world);
            }
        };
        addNpcRegistration(reg, "bandit.mounted_archer", "ancientwarfare:npc/spawner_bandit_archer");

        reg = new NpcFactionDeclaration(NpcBanditCivilianMale.class, AWEntityRegistry.NPC_FACTION_BANDIT_CIVILIAN_MALE, nextID++, AncientWarfareNPC.instance, 120, 3, true, "bandit.civilian.male") {
            @Override
            public Entity createEntity(World world) {
                return new NpcBanditCivilianMale(world);
            }
        };
        addNpcRegistration(reg, "bandit.civilian.male", "ancientwarfare:npc/spawner_bandit_civilian_male");

        reg = new NpcFactionDeclaration(NpcBanditCivilianFemale.class, AWEntityRegistry.NPC_FACTION_BANDIT_CIVILIAN_FEMALE, nextID++, AncientWarfareNPC.instance, 120, 3, true, "bandit.civilian.female") {
            @Override
            public Entity createEntity(World world) {
                return new NpcBanditCivilianFemale(world);
            }
        };
        addNpcRegistration(reg, "bandit.civilian.female", "ancientwarfare:npc/spawner_bandit_civilian_female");

        reg = new NpcFactionDeclaration(NpcBanditArcherElite.class, AWEntityRegistry.NPC_FACTION_BANDIT_ARCHER_ELITE, nextID++, AncientWarfareNPC.instance, 120, 3, true, "bandit.archer.elite") {
            @Override
            public Entity createEntity(World world) {
                return new NpcBanditArcherElite(world);
            }
        };
        addNpcRegistration(reg, "bandit.archer.elite", "ancientwarfare:npc/spawner_bandit_archer");

        reg = new NpcFactionDeclaration(NpcBanditSoldierElite.class, AWEntityRegistry.NPC_FACTION_BANDIT_SOLDIER_ELITE, nextID++, AncientWarfareNPC.instance, 120, 3, true, "bandit.soldier.elite") {
            @Override
            public Entity createEntity(World world) {
                return new NpcBanditSoldierElite(world);
            }
        };
        addNpcRegistration(reg, "bandit.soldier.elite", "ancientwarfare:npc/spawner_bandit_soldier");

        reg = new NpcFactionDeclaration(NpcBanditLeaderElite.class, AWEntityRegistry.NPC_FACTION_BANDIT_LEADER_ELITE, nextID++, AncientWarfareNPC.instance, 120, 3, true, "bandit.leader.elite") {
            @Override
            public Entity createEntity(World world) {
                return new NpcBanditLeaderElite(world);
            }
        };
        addNpcRegistration(reg, "bandit.leader.elite", "ancientwarfare:npc/spawner_bandit_leader");

        reg = new NpcFactionDeclaration(NpcBanditBard.class, AWEntityRegistry.NPC_FACTION_BANDIT_BARD, nextID++, AncientWarfareNPC.instance, 120, 3, true, AWEntityRegistry.NPC_FACTION_BANDIT_BARD) {
            @Override
            public Entity createEntity(World world) {
                return new NpcBanditBard(world);
            }
        };
        addNpcRegistration(reg, AWEntityRegistry.NPC_FACTION_BANDIT_BARD, "ancientwarfare:npc/spawner_bandit_bard");
    }

    private static void addDesertNatives() {
        NpcFactionDeclaration reg;
        /**
         * DESERT NATIVES
         */
        reg = new NpcFactionDeclaration(NpcDesertArcher.class, AWEntityRegistry.NPC_FACTION_DESERT_ARCHER, nextID++, AncientWarfareNPC.instance, 120, 3, true, "desert.archer") {
            @Override
            public Entity createEntity(World world) {
                return new NpcDesertArcher(world);
            }
        };
        addNpcRegistration(reg, "desert.archer", "ancientwarfare:npc/spawner_desert_archer");

        reg = new NpcFactionDeclaration(NpcDesertSoldier.class, AWEntityRegistry.NPC_FACTION_DESERT_SOLDIER, nextID++, AncientWarfareNPC.instance, 120, 3, true, "desert.soldier") {
            @Override
            public Entity createEntity(World world) {
                return new NpcDesertSoldier(world);
            }
        };
        addNpcRegistration(reg, "desert.soldier", "ancientwarfare:npc/spawner_desert_soldier");

        reg = new NpcFactionDeclaration(NpcDesertLeader.class, AWEntityRegistry.NPC_FACTION_DESERT_COMMANDER, nextID++, AncientWarfareNPC.instance, 120, 3, true, "desert.leader") {
            @Override
            public Entity createEntity(World world) {
                return new NpcDesertLeader(world);
            }
        };
        addNpcRegistration(reg, "desert.leader", "ancientwarfare:npc/spawner_desert_leader");

        reg = new NpcFactionDeclaration(NpcDesertPriest.class, AWEntityRegistry.NPC_FACTION_DESERT_PRIEST, nextID++, AncientWarfareNPC.instance, 120, 3, true, "desert.priest") {
            @Override
            public Entity createEntity(World world) {
                return new NpcDesertPriest(world);
            }
        };
        addNpcRegistration(reg, "desert.priest", "ancientwarfare:npc/spawner_desert_priest");

        reg = new NpcFactionDeclaration(NpcDesertTrader.class, AWEntityRegistry.NPC_FACTION_DESERT_TRADER, nextID++, AncientWarfareNPC.instance, 120, 3, true, "desert.trader") {
            @Override
            public Entity createEntity(World world) {
                return new NpcDesertTrader(world);
            }
        };
        addNpcRegistration(reg, "desert.trader", "ancientwarfare:npc/spawner_desert_trader");

        reg = new NpcFactionDeclaration(NpcDesertMountedSoldier.class, AWEntityRegistry.NPC_FACTION_DESERT_CAVALRY, nextID++, AncientWarfareNPC.instance, 120, 3, true, "desert.cavalry") {
            @Override
            public Entity createEntity(World world) {
                return new NpcDesertMountedSoldier(world);
            }
        };
        addNpcRegistration(reg, "desert.cavalry", "ancientwarfare:npc/spawner_desert_soldier");

        reg = new NpcFactionDeclaration(NpcDesertMountedArcher.class, AWEntityRegistry.NPC_FACTION_DESERT_MOUNTED_ARCHER, nextID++, AncientWarfareNPC.instance, 120, 3, true, "desert.mounted_archer") {
            @Override
            public Entity createEntity(World world) {
                return new NpcDesertMountedArcher(world);
            }
        };
        addNpcRegistration(reg, "desert.mounted_archer", "ancientwarfare:npc/spawner_desert_archer");

        reg = new NpcFactionDeclaration(NpcDesertCivilianMale.class, AWEntityRegistry.NPC_FACTION_DESERT_CIVILIAN_MALE, nextID++, AncientWarfareNPC.instance, 120, 3, true, "desert.civilian.male") {
            @Override
            public Entity createEntity(World world) {
                return new NpcDesertCivilianMale(world);
            }
        };
        addNpcRegistration(reg, "desert.civilian.male", "ancientwarfare:npc/spawner_desert_civilian_male");

        reg = new NpcFactionDeclaration(NpcDesertCivilianFemale.class, AWEntityRegistry.NPC_FACTION_DESERT_CIVILIAN_FEMALE, nextID++, AncientWarfareNPC.instance, 120, 3, true, "desert.civilian.female") {
            @Override
            public Entity createEntity(World world) {
                return new NpcDesertCivilianFemale(world);
            }
        };
        addNpcRegistration(reg, "desert.civilian.female", "ancientwarfare:npc/spawner_desert_civilian_female");

        reg = new NpcFactionDeclaration(NpcDesertArcherElite.class, AWEntityRegistry.NPC_FACTION_DESERT_ARCHER_ELITE, nextID++, AncientWarfareNPC.instance, 120, 3, true, "desert.archer.elite") {
            @Override
            public Entity createEntity(World world) {
                return new NpcDesertArcherElite(world);
            }
        };
        addNpcRegistration(reg, "desert.archer.elite", "ancientwarfare:npc/spawner_desert_archer");

        reg = new NpcFactionDeclaration(NpcDesertSoldierElite.class, AWEntityRegistry.NPC_FACTION_DESERT_SOLDIER_ELITE, nextID++, AncientWarfareNPC.instance, 120, 3, true, "desert.soldier.elite") {
            @Override
            public Entity createEntity(World world) {
                return new NpcDesertSoldierElite(world);
            }
        };
        addNpcRegistration(reg, "desert.soldier.elite", "ancientwarfare:npc/spawner_desert_soldier");

        reg = new NpcFactionDeclaration(NpcDesertLeaderElite.class, AWEntityRegistry.NPC_FACTION_DESERT_LEADER_ELITE, nextID++, AncientWarfareNPC.instance, 120, 3, true, "desert.leader.elite") {
            @Override
            public Entity createEntity(World world) {
                return new NpcDesertLeaderElite(world);
            }
        };
        addNpcRegistration(reg, "desert.leader.elite", "ancientwarfare:npc/spawner_desert_leader");

        reg = new NpcFactionDeclaration(NpcDesertBard.class, AWEntityRegistry.NPC_FACTION_DESERT_BARD, nextID++, AncientWarfareNPC.instance, 120, 3, true, AWEntityRegistry.NPC_FACTION_DESERT_BARD) {
            @Override
            public Entity createEntity(World world) {
                return new NpcDesertBard(world);
            }
        };
        addNpcRegistration(reg, AWEntityRegistry.NPC_FACTION_DESERT_BARD, "ancientwarfare:npc/spawner_desert_bard");
    }

    private static void addJungleNatives() {
        NpcFactionDeclaration reg;
        /**
         * JUNGLE NATIVES
         */
        reg = new NpcFactionDeclaration(NpcNativeArcher.class, AWEntityRegistry.NPC_FACTION_NATIVE_ARCHER, nextID++, AncientWarfareNPC.instance, 120, 3, true, "native.archer") {
            @Override
            public Entity createEntity(World world) {
                return new NpcNativeArcher(world);
            }
        };
        addNpcRegistration(reg, "native.archer", "ancientwarfare:npc/spawner_native_archer");

        reg = new NpcFactionDeclaration(NpcNativeSoldier.class, AWEntityRegistry.NPC_FACTION_NATIVE_SOLDIER, nextID++, AncientWarfareNPC.instance, 120, 3, true, "native.soldier") {
            @Override
            public Entity createEntity(World world) {
                return new NpcNativeSoldier(world);
            }
        };
        addNpcRegistration(reg, "native.soldier", "ancientwarfare:npc/spawner_native_soldier");

        reg = new NpcFactionDeclaration(NpcNativeLeader.class, AWEntityRegistry.NPC_FACTION_NATIVE_COMMANDER, nextID++, AncientWarfareNPC.instance, 120, 3, true, "native.leader") {
            @Override
            public Entity createEntity(World world) {
                return new NpcNativeLeader(world);
            }
        };
        addNpcRegistration(reg, "native.leader", "ancientwarfare:npc/spawner_native_leader");

        reg = new NpcFactionDeclaration(NpcNativePriest.class, AWEntityRegistry.NPC_FACTION_NATIVE_PRIEST, nextID++, AncientWarfareNPC.instance, 120, 3, true, "native.priest") {
            @Override
            public Entity createEntity(World world) {
                return new NpcNativePriest(world);
            }
        };
        addNpcRegistration(reg, "native.priest", "ancientwarfare:npc/spawner_native_priest");

        reg = new NpcFactionDeclaration(NpcNativeTrader.class, AWEntityRegistry.NPC_FACTION_NATIVE_TRADER, nextID++, AncientWarfareNPC.instance, 120, 3, true, "native.trader") {
            @Override
            public Entity createEntity(World world) {
                return new NpcNativeTrader(world);
            }
        };
        addNpcRegistration(reg, "native.trader", "ancientwarfare:npc/spawner_native_trader");

        reg = new NpcFactionDeclaration(NpcNativeMountedSoldier.class, AWEntityRegistry.NPC_FACTION_NATIVE_CAVALRY, nextID++, AncientWarfareNPC.instance, 120, 3, true, "native.cavalry") {
            @Override
            public Entity createEntity(World world) {
                return new NpcNativeMountedSoldier(world);
            }
        };
        addNpcRegistration(reg, "native.cavalry", "ancientwarfare:npc/spawner_native_soldier");

        reg = new NpcFactionDeclaration(NpcNativeMountedArcher.class, AWEntityRegistry.NPC_FACTION_NATIVE_MOUNTED_ARCHER, nextID++, AncientWarfareNPC.instance, 120, 3, true, "native.mounted_archer") {
            @Override
            public Entity createEntity(World world) {
                return new NpcNativeMountedArcher(world);
            }
        };
        addNpcRegistration(reg, "native.mounted_archer", "ancientwarfare:npc/spawner_native_archer");

        reg = new NpcFactionDeclaration(NpcNativeCivilianMale.class, AWEntityRegistry.NPC_FACTION_NATIVE_CIVILIAN_MALE, nextID++, AncientWarfareNPC.instance, 120, 3, true, "native.civilian.male") {
            @Override
            public Entity createEntity(World world) {
                return new NpcNativeCivilianMale(world);
            }
        };
        addNpcRegistration(reg, "native.civilian.male", "ancientwarfare:npc/spawner_native_civilian_male");

        reg = new NpcFactionDeclaration(NpcNativeCivilianFemale.class, AWEntityRegistry.NPC_FACTION_NATIVE_CIVILIAN_FEMALE, nextID++, AncientWarfareNPC.instance, 120, 3, true, "native.civilian.female") {
            @Override
            public Entity createEntity(World world) {
                return new NpcNativeCivilianFemale(world);
            }
        };
        addNpcRegistration(reg, "native.civilian.female", "ancientwarfare:npc/spawner_native_civilian_female");

        reg = new NpcFactionDeclaration(NpcNativeArcherElite.class, AWEntityRegistry.NPC_FACTION_NATIVE_ARCHER_ELITE, nextID++, AncientWarfareNPC.instance, 120, 3, true, "native.archer.elite") {
            @Override
            public Entity createEntity(World world) {
                return new NpcNativeArcherElite(world);
            }
        };
        addNpcRegistration(reg, "native.archer.elite", "ancientwarfare:npc/spawner_native_archer");

        reg = new NpcFactionDeclaration(NpcNativeSoldierElite.class, AWEntityRegistry.NPC_FACTION_NATIVE_SOLDIER_ELITE, nextID++, AncientWarfareNPC.instance, 120, 3, true, "native.soldier.elite") {
            @Override
            public Entity createEntity(World world) {
                return new NpcNativeSoldierElite(world);
            }
        };
        addNpcRegistration(reg, "native.soldier.elite", "ancientwarfare:npc/spawner_native_soldier");

        reg = new NpcFactionDeclaration(NpcNativeLeaderElite.class, AWEntityRegistry.NPC_FACTION_NATIVE_LEADER_ELITE, nextID++, AncientWarfareNPC.instance, 120, 3, true, "native.leader.elite") {
            @Override
            public Entity createEntity(World world) {
                return new NpcNativeLeaderElite(world);
            }
        };
        addNpcRegistration(reg, "native.leader.elite", "ancientwarfare:npc/spawner_native_leader");

        reg = new NpcFactionDeclaration(NpcNativeBard.class, AWEntityRegistry.NPC_FACTION_NATIVE_BARD, nextID++, AncientWarfareNPC.instance, 120, 3, true, AWEntityRegistry.NPC_FACTION_NATIVE_BARD) {
            @Override
            public Entity createEntity(World world) {
                return new NpcNativeBard(world);
            }
        };
        addNpcRegistration(reg, AWEntityRegistry.NPC_FACTION_NATIVE_BARD, "ancientwarfare:npc/spawner_native_bard");
    }

    private static void addPirates() {
        NpcFactionDeclaration reg;
        /**
         * PIRATES
         */
        reg = new NpcFactionDeclaration(NpcPirateArcher.class, AWEntityRegistry.NPC_FACTION_PIRATE_ARCHER, nextID++, AncientWarfareNPC.instance, 120, 3, true, "pirate.archer") {
            @Override
            public Entity createEntity(World world) {
                return new NpcPirateArcher(world);
            }
        };
        addNpcRegistration(reg, "pirate.archer", "ancientwarfare:npc/spawner_pirate_archer");

        reg = new NpcFactionDeclaration(NpcPirateSoldier.class, AWEntityRegistry.NPC_FACTION_PIRATE_SOLDIER, nextID++, AncientWarfareNPC.instance, 120, 3, true, "pirate.soldier") {
            @Override
            public Entity createEntity(World world) {
                return new NpcPirateSoldier(world);
            }
        };
        addNpcRegistration(reg, "pirate.soldier", "ancientwarfare:npc/spawner_pirate_soldier");

        reg = new NpcFactionDeclaration(NpcPirateLeader.class, AWEntityRegistry.NPC_FACTION_PIRATE_COMMANDER, nextID++, AncientWarfareNPC.instance, 120, 3, true, "pirate.leader") {
            @Override
            public Entity createEntity(World world) {
                return new NpcPirateLeader(world);
            }
        };
        addNpcRegistration(reg, "pirate.leader", "ancientwarfare:npc/spawner_pirate_leader");

        reg = new NpcFactionDeclaration(NpcPiratePriest.class, AWEntityRegistry.NPC_FACTION_PIRATE_PRIEST, nextID++, AncientWarfareNPC.instance, 120, 3, true, "pirate.priest") {
            @Override
            public Entity createEntity(World world) {
                return new NpcPiratePriest(world);
            }
        };
        addNpcRegistration(reg, "pirate.priest", "ancientwarfare:npc/spawner_pirate_priest");

        reg = new NpcFactionDeclaration(NpcPirateTrader.class, AWEntityRegistry.NPC_FACTION_PIRATE_TRADER, nextID++, AncientWarfareNPC.instance, 120, 3, true, "pirate.trader") {
            @Override
            public Entity createEntity(World world) {
                return new NpcPirateTrader(world);
            }
        };
        addNpcRegistration(reg, "pirate.trader", "ancientwarfare:npc/spawner_pirate_trader");

        reg = new NpcFactionDeclaration(NpcPirateMountedSoldier.class, AWEntityRegistry.NPC_FACTION_PIRATE_CAVALRY, nextID++, AncientWarfareNPC.instance, 120, 3, true, "pirate.cavalry") {
            @Override
            public Entity createEntity(World world) {
                return new NpcPirateMountedSoldier(world);
            }
        };
        addNpcRegistration(reg, "pirate.cavalry", "ancientwarfare:npc/spawner_pirate_soldier");

        reg = new NpcFactionDeclaration(NpcPirateMountedArcher.class, AWEntityRegistry.NPC_FACTION_PIRATE_MOUNTED_ARCHER, nextID++, AncientWarfareNPC.instance, 120, 3, true, "pirate.mounted_archer") {
            @Override
            public Entity createEntity(World world) {
                return new NpcPirateMountedArcher(world);
            }
        };
        addNpcRegistration(reg, "pirate.mounted_archer", "ancientwarfare:npc/spawner_pirate_archer");

        reg = new NpcFactionDeclaration(NpcPirateCivilianMale.class, AWEntityRegistry.NPC_FACTION_PIRATE_CIVILIAN_MALE, nextID++, AncientWarfareNPC.instance, 120, 3, true, "pirate.civilian.male") {
            @Override
            public Entity createEntity(World world) {
                return new NpcPirateCivilianMale(world);
            }
        };
        addNpcRegistration(reg, "pirate.civilian.male", "ancientwarfare:npc/spawner_pirate_civilian_male");

        reg = new NpcFactionDeclaration(NpcPirateCivilianFemale.class, AWEntityRegistry.NPC_FACTION_PIRATE_CIVILIAN_FEMALE, nextID++, AncientWarfareNPC.instance, 120, 3, true, "pirate.civilian.female") {
            @Override
            public Entity createEntity(World world) {
                return new NpcPirateCivilianFemale(world);
            }
        };
        addNpcRegistration(reg, "pirate.civilian.female", "ancientwarfare:npc/spawner_pirate_civilian_female");

        reg = new NpcFactionDeclaration(NpcPirateArcherElite.class, AWEntityRegistry.NPC_FACTION_PIRATE_ARCHER_ELITE, nextID++, AncientWarfareNPC.instance, 120, 3, true, "pirate.archer.elite") {
            @Override
            public Entity createEntity(World world) {
                return new NpcPirateArcherElite(world);
            }
        };
        addNpcRegistration(reg, "pirate.archer.elite", "ancientwarfare:npc/spawner_pirate_archer");

        reg = new NpcFactionDeclaration(NpcPirateSoldierElite.class, AWEntityRegistry.NPC_FACTION_PIRATE_SOLDIER_ELITE, nextID++, AncientWarfareNPC.instance, 120, 3, true, "pirate.soldier.elite") {
            @Override
            public Entity createEntity(World world) {
                return new NpcPirateSoldierElite(world);
            }
        };
        addNpcRegistration(reg, "pirate.soldier.elite", "ancientwarfare:npc/spawner_pirate_soldier");

        reg = new NpcFactionDeclaration(NpcPirateLeaderElite.class, AWEntityRegistry.NPC_FACTION_PIRATE_LEADER_ELITE, nextID++, AncientWarfareNPC.instance, 120, 3, true, "pirate.leader.elite") {
            @Override
            public Entity createEntity(World world) {
                return new NpcPirateLeaderElite(world);
            }
        };
        addNpcRegistration(reg, "pirate.leader.elite", "ancientwarfare:npc/spawner_pirate_leader");

        reg = new NpcFactionDeclaration(NpcPirateBard.class, AWEntityRegistry.NPC_FACTION_PIRATE_BARD, nextID++, AncientWarfareNPC.instance, 120, 3, true, AWEntityRegistry.NPC_FACTION_PIRATE_BARD) {
            @Override
            public Entity createEntity(World world) {
                return new NpcPirateBard(world);
            }
        };
        addNpcRegistration(reg, AWEntityRegistry.NPC_FACTION_PIRATE_BARD, "ancientwarfare:npc/spawner_pirate_bard");
    }

    private static void addVikings() {
        NpcFactionDeclaration reg;
        /**
         * VIKINGS
         */
        reg = new NpcFactionDeclaration(NpcVikingArcher.class, AWEntityRegistry.NPC_FACTION_VIKING_ARCHER, nextID++, AncientWarfareNPC.instance, 120, 3, true, "viking.archer") {
            @Override
            public Entity createEntity(World world) {
                return new NpcVikingArcher(world);
            }
        };
        addNpcRegistration(reg, "viking.archer", "ancientwarfare:npc/spawner_viking_archer");

        reg = new NpcFactionDeclaration(NpcVikingSoldier.class, AWEntityRegistry.NPC_FACTION_VIKING_SOLDIER, nextID++, AncientWarfareNPC.instance, 120, 3, true, "viking.soldier") {
            @Override
            public Entity createEntity(World world) {
                return new NpcVikingSoldier(world);
            }
        };
        addNpcRegistration(reg, "viking.soldier", "ancientwarfare:npc/spawner_viking_soldier");

        reg = new NpcFactionDeclaration(NpcVikingLeader.class, AWEntityRegistry.NPC_FACTION_VIKING_COMMANDER, nextID++, AncientWarfareNPC.instance, 120, 3, true, "viking.leader") {
            @Override
            public Entity createEntity(World world) {
                return new NpcVikingLeader(world);
            }
        };
        addNpcRegistration(reg, "viking.leader", "ancientwarfare:npc/spawner_viking_leader");

        reg = new NpcFactionDeclaration(NpcVikingPriest.class, AWEntityRegistry.NPC_FACTION_VIKING_PRIEST, nextID++, AncientWarfareNPC.instance, 120, 3, true, "viking.priest") {
            @Override
            public Entity createEntity(World world) {
                return new NpcVikingPriest(world);
            }
        };
        addNpcRegistration(reg, "viking.priest", "ancientwarfare:npc/spawner_viking_priest");

        reg = new NpcFactionDeclaration(NpcVikingTrader.class, AWEntityRegistry.NPC_FACTION_VIKING_TRADER, nextID++, AncientWarfareNPC.instance, 120, 3, true, "viking.trader") {
            @Override
            public Entity createEntity(World world) {
                return new NpcVikingTrader(world);
            }
        };
        addNpcRegistration(reg, "viking.trader", "ancientwarfare:npc/spawner_viking_trader");

        reg = new NpcFactionDeclaration(NpcVikingMountedSoldier.class, AWEntityRegistry.NPC_FACTION_VIKING_CAVALRY, nextID++, AncientWarfareNPC.instance, 120, 3, true, "viking.cavalry") {
            @Override
            public Entity createEntity(World world) {
                return new NpcVikingMountedSoldier(world);
            }
        };
        addNpcRegistration(reg, "viking.cavalry", "ancientwarfare:npc/spawner_viking_soldier");

        reg = new NpcFactionDeclaration(NpcVikingMountedArcher.class, AWEntityRegistry.NPC_FACTION_VIKING_MOUNTED_ARCHER, nextID++, AncientWarfareNPC.instance, 120, 3, true, "viking.mounted_archer") {
            @Override
            public Entity createEntity(World world) {
                return new NpcVikingMountedArcher(world);
            }
        };
        addNpcRegistration(reg, "viking.mounted_archer", "ancientwarfare:npc/spawner_viking_archer");

        reg = new NpcFactionDeclaration(NpcVikingCivilianMale.class, AWEntityRegistry.NPC_FACTION_VIKING_CIVILIAN_MALE, nextID++, AncientWarfareNPC.instance, 120, 3, true, "viking.civilian.male") {
            @Override
            public Entity createEntity(World world) {
                return new NpcVikingCivilianMale(world);
            }
        };
        addNpcRegistration(reg, "viking.civilian.male", "ancientwarfare:npc/spawner_viking_civilian_male");

        reg = new NpcFactionDeclaration(NpcVikingCivilianFemale.class, AWEntityRegistry.NPC_FACTION_VIKING_CIVILIAN_FEMALE, nextID++, AncientWarfareNPC.instance, 120, 3, true, "viking.civilian.female") {
            @Override
            public Entity createEntity(World world) {
                return new NpcVikingCivilianFemale(world);
            }
        };
        addNpcRegistration(reg, "viking.civilian.female", "ancientwarfare:npc/spawner_viking_civilian_female");

        reg = new NpcFactionDeclaration(NpcVikingArcherElite.class, AWEntityRegistry.NPC_FACTION_VIKING_ARCHER_ELITE, nextID++, AncientWarfareNPC.instance, 120, 3, true, "viking.archer.elite") {
            @Override
            public Entity createEntity(World world) {
                return new NpcVikingArcherElite(world);
            }
        };
        addNpcRegistration(reg, "viking.archer.elite", "ancientwarfare:npc/spawner_viking_archer");

        reg = new NpcFactionDeclaration(NpcVikingSoldierElite.class, AWEntityRegistry.NPC_FACTION_VIKING_SOLDIER_ELITE, nextID++, AncientWarfareNPC.instance, 120, 3, true, "viking.soldier.elite") {
            @Override
            public Entity createEntity(World world) {
                return new NpcVikingSoldierElite(world);
            }
        };
        addNpcRegistration(reg, "viking.soldier.elite", "ancientwarfare:npc/spawner_viking_soldier");

        reg = new NpcFactionDeclaration(NpcVikingLeaderElite.class, AWEntityRegistry.NPC_FACTION_VIKING_LEADER_ELITE, nextID++, AncientWarfareNPC.instance, 120, 3, true, "viking.leader.elite") {
            @Override
            public Entity createEntity(World world) {
                return new NpcVikingLeaderElite(world);
            }
        };
        addNpcRegistration(reg, "viking.leader.elite", "ancientwarfare:npc/spawner_viking_leader");

        reg = new NpcFactionDeclaration(NpcVikingBard.class, AWEntityRegistry.NPC_FACTION_VIKING_BARD, nextID++, AncientWarfareNPC.instance, 120, 3, true, AWEntityRegistry.NPC_FACTION_VIKING_BARD) {
            @Override
            public Entity createEntity(World world) {
                return new NpcVikingBard(world);
            }
        };
        addNpcRegistration(reg, AWEntityRegistry.NPC_FACTION_VIKING_BARD, "ancientwarfare:npc/spawner_viking_bard");
    }

    private static void addCustom1() {
        NpcFactionDeclaration reg;
        /**
         * CUSTOM_1S
         */
        reg = new NpcFactionDeclaration(NpcCustom_1Archer.class, AWEntityRegistry.NPC_FACTION_CUSTOM_1_ARCHER, nextID++, AncientWarfareNPC.instance, 120, 3, true, "custom_1.archer") {
            @Override
            public Entity createEntity(World world) {
                return new NpcCustom_1Archer(world);
            }
        };
        addNpcRegistration(reg, "custom_1.archer", "ancientwarfare:npc/spawner_custom_1_archer");

        reg = new NpcFactionDeclaration(NpcCustom_1Soldier.class, AWEntityRegistry.NPC_FACTION_CUSTOM_1_SOLDIER, nextID++, AncientWarfareNPC.instance, 120, 3, true, "custom_1.soldier") {
            @Override
            public Entity createEntity(World world) {
                return new NpcCustom_1Soldier(world);
            }
        };
        addNpcRegistration(reg, "custom_1.soldier", "ancientwarfare:npc/spawner_custom_1_soldier");

        reg = new NpcFactionDeclaration(NpcCustom_1Leader.class, AWEntityRegistry.NPC_FACTION_CUSTOM_1_COMMANDER, nextID++, AncientWarfareNPC.instance, 120, 3, true, "custom_1.leader") {
            @Override
            public Entity createEntity(World world) {
                return new NpcCustom_1Leader(world);
            }
        };
        addNpcRegistration(reg, "custom_1.leader", "ancientwarfare:npc/spawner_custom_1_leader");

        reg = new NpcFactionDeclaration(NpcCustom_1Priest.class, AWEntityRegistry.NPC_FACTION_CUSTOM_1_PRIEST, nextID++, AncientWarfareNPC.instance, 120, 3, true, "custom_1.priest") {
            @Override
            public Entity createEntity(World world) {
                return new NpcCustom_1Priest(world);
            }
        };
        addNpcRegistration(reg, "custom_1.priest", "ancientwarfare:npc/spawner_custom_1_priest");

        reg = new NpcFactionDeclaration(NpcCustom_1Trader.class, AWEntityRegistry.NPC_FACTION_CUSTOM_1_TRADER, nextID++, AncientWarfareNPC.instance, 120, 3, true, "custom_1.trader") {
            @Override
            public Entity createEntity(World world) {
                return new NpcCustom_1Trader(world);
            }
        };
        addNpcRegistration(reg, "custom_1.trader", "ancientwarfare:npc/spawner_custom_1_trader");

        reg = new NpcFactionDeclaration(NpcCustom_1MountedSoldier.class, AWEntityRegistry.NPC_FACTION_CUSTOM_1_CAVALRY, nextID++, AncientWarfareNPC.instance, 120, 3, true, "custom_1.cavalry") {
            @Override
            public Entity createEntity(World world) {
                return new NpcCustom_1MountedSoldier(world);
            }
        };
        addNpcRegistration(reg, "custom_1.cavalry", "ancientwarfare:npc/spawner_custom_1_soldier");

        reg = new NpcFactionDeclaration(NpcCustom_1MountedArcher.class, AWEntityRegistry.NPC_FACTION_CUSTOM_1_MOUNTED_ARCHER, nextID++, AncientWarfareNPC.instance, 120, 3, true, "custom_1.mounted_archer") {
            @Override
            public Entity createEntity(World world) {
                return new NpcCustom_1MountedArcher(world);
            }
        };
        addNpcRegistration(reg, "custom_1.mounted_archer", "ancientwarfare:npc/spawner_custom_1_archer");

        reg = new NpcFactionDeclaration(NpcCustom_1CivilianMale.class, AWEntityRegistry.NPC_FACTION_CUSTOM_1_CIVILIAN_MALE, nextID++, AncientWarfareNPC.instance, 120, 3, true, "custom_1.civilian.male") {
            @Override
            public Entity createEntity(World world) {
                return new NpcCustom_1CivilianMale(world);
            }
        };
        addNpcRegistration(reg, "custom_1.civilian.male", "ancientwarfare:npc/spawner_custom_1_civilian_male");

        reg = new NpcFactionDeclaration(NpcCustom_1CivilianFemale.class, AWEntityRegistry.NPC_FACTION_CUSTOM_1_CIVILIAN_FEMALE, nextID++, AncientWarfareNPC.instance, 120, 3, true, "custom_1.civilian.female") {
            @Override
            public Entity createEntity(World world) {
                return new NpcCustom_1CivilianFemale(world);
            }
        };
        addNpcRegistration(reg, "custom_1.civilian.female", "ancientwarfare:npc/spawner_custom_1_civilian_female");

        reg = new NpcFactionDeclaration(NpcCustom_1ArcherElite.class, AWEntityRegistry.NPC_FACTION_CUSTOM_1_ARCHER_ELITE, nextID++, AncientWarfareNPC.instance, 120, 3, true, "custom_1.archer.elite") {
            @Override
            public Entity createEntity(World world) {
                return new NpcCustom_1ArcherElite(world);
            }
        };
        addNpcRegistration(reg, "custom_1.archer.elite", "ancientwarfare:npc/spawner_custom_1_archer");

        reg = new NpcFactionDeclaration(NpcCustom_1SoldierElite.class, AWEntityRegistry.NPC_FACTION_CUSTOM_1_SOLDIER_ELITE, nextID++, AncientWarfareNPC.instance, 120, 3, true, "custom_1.soldier.elite") {
            @Override
            public Entity createEntity(World world) {
                return new NpcCustom_1SoldierElite(world);
            }
        };
        addNpcRegistration(reg, "custom_1.soldier.elite", "ancientwarfare:npc/spawner_custom_1_soldier");

        reg = new NpcFactionDeclaration(NpcCustom_1LeaderElite.class, AWEntityRegistry.NPC_FACTION_CUSTOM_1_LEADER_ELITE, nextID++, AncientWarfareNPC.instance, 120, 3, true, "custom_1.leader.elite") {
            @Override
            public Entity createEntity(World world) {
                return new NpcCustom_1LeaderElite(world);
            }
        };
        addNpcRegistration(reg, "custom_1.leader.elite", "ancientwarfare:npc/spawner_custom_1_leader");

        reg = new NpcFactionDeclaration(NpcCustom_1Bard.class, AWEntityRegistry.NPC_FACTION_CUSTOM_1_BARD, nextID++, AncientWarfareNPC.instance, 120, 3, true, AWEntityRegistry.NPC_FACTION_CUSTOM_1_BARD) {
            @Override
            public Entity createEntity(World world) {
                return new NpcCustom_1Bard(world);
            }
        };
        addNpcRegistration(reg, AWEntityRegistry.NPC_FACTION_CUSTOM_1_BARD, "ancientwarfare:npc/spawner_custom_1_bard");
    }

    private static void addCustom2() {
        NpcFactionDeclaration reg;
        /**
         * CUSTOM_2S
         */
        reg = new NpcFactionDeclaration(NpcCustom_2Archer.class, AWEntityRegistry.NPC_FACTION_CUSTOM_2_ARCHER, nextID++, AncientWarfareNPC.instance, 120, 3, true, "custom_2.archer") {
            @Override
            public Entity createEntity(World world) {
                return new NpcCustom_2Archer(world);
            }
        };
        addNpcRegistration(reg, "custom_2.archer", "ancientwarfare:npc/spawner_custom_2_archer");

        reg = new NpcFactionDeclaration(NpcCustom_2Soldier.class, AWEntityRegistry.NPC_FACTION_CUSTOM_2_SOLDIER, nextID++, AncientWarfareNPC.instance, 120, 3, true, "custom_2.soldier") {
            @Override
            public Entity createEntity(World world) {
                return new NpcCustom_2Soldier(world);
            }
        };
        addNpcRegistration(reg, "custom_2.soldier", "ancientwarfare:npc/spawner_custom_2_soldier");

        reg = new NpcFactionDeclaration(NpcCustom_2Leader.class, AWEntityRegistry.NPC_FACTION_CUSTOM_2_COMMANDER, nextID++, AncientWarfareNPC.instance, 120, 3, true, "custom_2.leader") {
            @Override
            public Entity createEntity(World world) {
                return new NpcCustom_2Leader(world);
            }
        };
        addNpcRegistration(reg, "custom_2.leader", "ancientwarfare:npc/spawner_custom_2_leader");

        reg = new NpcFactionDeclaration(NpcCustom_2Priest.class, AWEntityRegistry.NPC_FACTION_CUSTOM_2_PRIEST, nextID++, AncientWarfareNPC.instance, 120, 3, true, "custom_2.priest") {
            @Override
            public Entity createEntity(World world) {
                return new NpcCustom_2Priest(world);
            }
        };
        addNpcRegistration(reg, "custom_2.priest", "ancientwarfare:npc/spawner_custom_2_priest");

        reg = new NpcFactionDeclaration(NpcCustom_2Trader.class, AWEntityRegistry.NPC_FACTION_CUSTOM_2_TRADER, nextID++, AncientWarfareNPC.instance, 120, 3, true, "custom_2.trader") {
            @Override
            public Entity createEntity(World world) {
                return new NpcCustom_2Trader(world);
            }
        };
        addNpcRegistration(reg, "custom_2.trader", "ancientwarfare:npc/spawner_custom_2_trader");

        reg = new NpcFactionDeclaration(NpcCustom_2MountedSoldier.class, AWEntityRegistry.NPC_FACTION_CUSTOM_2_CAVALRY, nextID++, AncientWarfareNPC.instance, 120, 3, true, "custom_2.cavalry") {
            @Override
            public Entity createEntity(World world) {
                return new NpcCustom_2MountedSoldier(world);
            }
        };
        addNpcRegistration(reg, "custom_2.cavalry", "ancientwarfare:npc/spawner_custom_2_soldier");

        reg = new NpcFactionDeclaration(NpcCustom_2MountedArcher.class, AWEntityRegistry.NPC_FACTION_CUSTOM_2_MOUNTED_ARCHER, nextID++, AncientWarfareNPC.instance, 120, 3, true, "custom_2.mounted_archer") {
            @Override
            public Entity createEntity(World world) {
                return new NpcCustom_2MountedArcher(world);
            }
        };
        addNpcRegistration(reg, "custom_2.mounted_archer", "ancientwarfare:npc/spawner_custom_2_archer");

        reg = new NpcFactionDeclaration(NpcCustom_2CivilianMale.class, AWEntityRegistry.NPC_FACTION_CUSTOM_2_CIVILIAN_MALE, nextID++, AncientWarfareNPC.instance, 120, 3, true, "custom_2.civilian.male") {
            @Override
            public Entity createEntity(World world) {
                return new NpcCustom_2CivilianMale(world);
            }
        };
        addNpcRegistration(reg, "custom_2.civilian.male", "ancientwarfare:npc/spawner_custom_2_civilian_male");

        reg = new NpcFactionDeclaration(NpcCustom_2CivilianFemale.class, AWEntityRegistry.NPC_FACTION_CUSTOM_2_CIVILIAN_FEMALE, nextID++, AncientWarfareNPC.instance, 120, 3, true, "custom_2.civilian.female") {
            @Override
            public Entity createEntity(World world) {
                return new NpcCustom_2CivilianFemale(world);
            }
        };
        addNpcRegistration(reg, "custom_2.civilian.female", "ancientwarfare:npc/spawner_custom_2_civilian_female");

        reg = new NpcFactionDeclaration(NpcCustom_2ArcherElite.class, AWEntityRegistry.NPC_FACTION_CUSTOM_2_ARCHER_ELITE, nextID++, AncientWarfareNPC.instance, 120, 3, true, "custom_2.archer.elite") {
            @Override
            public Entity createEntity(World world) {
                return new NpcCustom_2ArcherElite(world);
            }
        };
        addNpcRegistration(reg, "custom_2.archer.elite", "ancientwarfare:npc/spawner_custom_2_archer");

        reg = new NpcFactionDeclaration(NpcCustom_2SoldierElite.class, AWEntityRegistry.NPC_FACTION_CUSTOM_2_SOLDIER_ELITE, nextID++, AncientWarfareNPC.instance, 120, 3, true, "custom_2.soldier.elite") {
            @Override
            public Entity createEntity(World world) {
                return new NpcCustom_2SoldierElite(world);
            }
        };
        addNpcRegistration(reg, "custom_2.soldier.elite", "ancientwarfare:npc/spawner_custom_2_soldier");

        reg = new NpcFactionDeclaration(NpcCustom_2LeaderElite.class, AWEntityRegistry.NPC_FACTION_CUSTOM_2_LEADER_ELITE, nextID++, AncientWarfareNPC.instance, 120, 3, true, "custom_2.leader.elite") {
            @Override
            public Entity createEntity(World world) {
                return new NpcCustom_2LeaderElite(world);
            }
        };
        addNpcRegistration(reg, "custom_2.leader.elite", "ancientwarfare:npc/spawner_custom_2_leader");

        reg = new NpcFactionDeclaration(NpcCustom_2Bard.class, AWEntityRegistry.NPC_FACTION_CUSTOM_2_BARD, nextID++, AncientWarfareNPC.instance, 120, 3, true, AWEntityRegistry.NPC_FACTION_CUSTOM_2_BARD) {
            @Override
            public Entity createEntity(World world) {
                return new NpcCustom_2Bard(world);
            }
        };
        addNpcRegistration(reg, AWEntityRegistry.NPC_FACTION_CUSTOM_2_BARD, "ancientwarfare:npc/spawner_custom_2_bard");
    }

    private static void addCustom3() {

        NpcFactionDeclaration reg;
        /**
         * CUSTOM_3S
         */
        reg = new NpcFactionDeclaration(NpcCustom_3Archer.class, AWEntityRegistry.NPC_FACTION_CUSTOM_3_ARCHER, nextID++, AncientWarfareNPC.instance, 120, 3, true, "custom_3.archer") {
            @Override
            public Entity createEntity(World world) {
                return new NpcCustom_3Archer(world);
            }
        };
        addNpcRegistration(reg, "custom_3.archer", "ancientwarfare:npc/spawner_custom_3_archer");

        reg = new NpcFactionDeclaration(NpcCustom_3Soldier.class, AWEntityRegistry.NPC_FACTION_CUSTOM_3_SOLDIER, nextID++, AncientWarfareNPC.instance, 120, 3, true, "custom_3.soldier") {
            @Override
            public Entity createEntity(World world) {
                return new NpcCustom_3Soldier(world);
            }
        };
        addNpcRegistration(reg, "custom_3.soldier", "ancientwarfare:npc/spawner_custom_3_soldier");

        reg = new NpcFactionDeclaration(NpcCustom_3Leader.class, AWEntityRegistry.NPC_FACTION_CUSTOM_3_COMMANDER, nextID++, AncientWarfareNPC.instance, 120, 3, true, "custom_3.leader") {
            @Override
            public Entity createEntity(World world) {
                return new NpcCustom_3Leader(world);
            }
        };
        addNpcRegistration(reg, "custom_3.leader", "ancientwarfare:npc/spawner_custom_3_leader");

        reg = new NpcFactionDeclaration(NpcCustom_3Priest.class, AWEntityRegistry.NPC_FACTION_CUSTOM_3_PRIEST, nextID++, AncientWarfareNPC.instance, 120, 3, true, "custom_3.priest") {
            @Override
            public Entity createEntity(World world) {
                return new NpcCustom_3Priest(world);
            }
        };
        addNpcRegistration(reg, "custom_3.priest", "ancientwarfare:npc/spawner_custom_3_priest");

        reg = new NpcFactionDeclaration(NpcCustom_3Trader.class, AWEntityRegistry.NPC_FACTION_CUSTOM_3_TRADER, nextID++, AncientWarfareNPC.instance, 120, 3, true, "custom_3.trader") {
            @Override
            public Entity createEntity(World world) {
                return new NpcCustom_3Trader(world);
            }
        };
        addNpcRegistration(reg, "custom_3.trader", "ancientwarfare:npc/spawner_custom_3_trader");

        reg = new NpcFactionDeclaration(NpcCustom_3MountedSoldier.class, AWEntityRegistry.NPC_FACTION_CUSTOM_3_CAVALRY, nextID++, AncientWarfareNPC.instance, 120, 3, true, "custom_3.cavalry") {
            @Override
            public Entity createEntity(World world) {
                return new NpcCustom_3MountedSoldier(world);
            }
        };
        addNpcRegistration(reg, "custom_3.cavalry", "ancientwarfare:npc/spawner_custom_3_soldier");

        reg = new NpcFactionDeclaration(NpcCustom_3MountedArcher.class, AWEntityRegistry.NPC_FACTION_CUSTOM_3_MOUNTED_ARCHER, nextID++, AncientWarfareNPC.instance, 120, 3, true, "custom_3.mounted_archer") {
            @Override
            public Entity createEntity(World world) {
                return new NpcCustom_3MountedArcher(world);
            }
        };
        addNpcRegistration(reg, "custom_3.mounted_archer", "ancientwarfare:npc/spawner_custom_3_archer");

        reg = new NpcFactionDeclaration(NpcCustom_3CivilianMale.class, AWEntityRegistry.NPC_FACTION_CUSTOM_3_CIVILIAN_MALE, nextID++, AncientWarfareNPC.instance, 120, 3, true, "custom_3.civilian.male") {
            @Override
            public Entity createEntity(World world) {
                return new NpcCustom_3CivilianMale(world);
            }
        };
        addNpcRegistration(reg, "custom_3.civilian.male", "ancientwarfare:npc/spawner_custom_3_civilian_male");

        reg = new NpcFactionDeclaration(NpcCustom_3CivilianFemale.class, AWEntityRegistry.NPC_FACTION_CUSTOM_3_CIVILIAN_FEMALE, nextID++, AncientWarfareNPC.instance, 120, 3, true, "custom_3.civilian.female") {
            @Override
            public Entity createEntity(World world) {
                return new NpcCustom_3CivilianFemale(world);
            }
        };
        addNpcRegistration(reg, "custom_3.civilian.female", "ancientwarfare:npc/spawner_custom_3_civilian_female");

        reg = new NpcFactionDeclaration(NpcCustom_3ArcherElite.class, AWEntityRegistry.NPC_FACTION_CUSTOM_3_ARCHER_ELITE, nextID++, AncientWarfareNPC.instance, 120, 3, true, "custom_3.archer.elite") {
            @Override
            public Entity createEntity(World world) {
                return new NpcCustom_3ArcherElite(world);
            }
        };
        addNpcRegistration(reg, "custom_3.archer.elite", "ancientwarfare:npc/spawner_custom_3_archer");

        reg = new NpcFactionDeclaration(NpcCustom_3SoldierElite.class, AWEntityRegistry.NPC_FACTION_CUSTOM_3_SOLDIER_ELITE, nextID++, AncientWarfareNPC.instance, 120, 3, true, "custom_3.soldier.elite") {
            @Override
            public Entity createEntity(World world) {
                return new NpcCustom_3SoldierElite(world);
            }
        };
        addNpcRegistration(reg, "custom_3.soldier.elite", "ancientwarfare:npc/spawner_custom_3_soldier");

        reg = new NpcFactionDeclaration(NpcCustom_3LeaderElite.class, AWEntityRegistry.NPC_FACTION_CUSTOM_3_LEADER_ELITE, nextID++, AncientWarfareNPC.instance, 120, 3, true, "custom_3.leader.elite") {
            @Override
            public Entity createEntity(World world) {
                return new NpcCustom_3LeaderElite(world);
            }
        };
        addNpcRegistration(reg, "custom_3.leader.elite", "ancientwarfare:npc/spawner_custom_3_leader");

        reg = new NpcFactionDeclaration(NpcCustom_3Bard.class, AWEntityRegistry.NPC_FACTION_CUSTOM_3_BARD, nextID++, AncientWarfareNPC.instance, 120, 3, true, AWEntityRegistry.NPC_FACTION_CUSTOM_3_BARD) {
            @Override
            public Entity createEntity(World world) {
                return new NpcCustom_3Bard(world);
            }
        };
        addNpcRegistration(reg, AWEntityRegistry.NPC_FACTION_CUSTOM_3_BARD, "ancientwarfare:npc/spawner_custom_3_bard");
    }

    /**
     * has to be called during post-init so that all items/etc are fully initialzed
     */
    public static void loadNpcSubtypeEquipment() {
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

    private static void addNpcRegistration(NpcDeclaration reg, String npcName, String icon) {
        AWEntityRegistry.registerEntity(reg);
        if (reg.canSpawnBaseEntity) {
            AWNpcItemLoader.npcSpawner.addNpcType(npcName, icon);
        }
        npcMap.put(npcName, reg);
    }

    public static NpcBase createNpc(World world, String npcType, String npcSubtype) {
        if (!npcMap.containsKey(npcType)) {
            return null;
        }
        NpcDeclaration reg = npcMap.get(npcType);
        return reg.createEntity(world, npcSubtype);
    }

    private static void addNpcSubtypeEntry(String npcType, String npcSubtype, String icon) {
        if (!npcMap.containsKey(npcType)) {
            throw new IllegalArgumentException("npc type must first be mapped");
        }
        npcMap.get(npcType).addSubtype(npcSubtype, icon);
        AWNpcItemLoader.npcSpawner.addNpcType(npcType + "." + npcSubtype, icon);
    }

    private static void addNpcSubtypeEquipment(String npcType, String npcSubtype, ItemStack equipment) {
        if (!npcMap.containsKey(npcType)) {
            throw new IllegalArgumentException("npc type must first be mapped");
        }
        NpcDeclaration reg = npcMap.get(npcType);
        if (!reg.subTypeIcons.containsKey(npcSubtype)) {
            throw new IllegalArgumentException("npc subtype must first be mapped");
        }
        reg.spawnEquipment.put(npcSubtype, equipment);
    }

    /**
     * used by npc spawner item to get the sub-items
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void getSpawnerSubItems(List list) {
        for (NpcDeclaration dec : npcMap.values()) {
            if (dec.canSpawnBaseEntity) {
                list.add(ItemNpcSpawner.getStackForNpcType(dec.npcType, ""));
            }
            for (String sub : dec.subTypeIcons.keySet()) {
                list.add(ItemNpcSpawner.getStackForNpcType(dec.npcType, sub));
            }
        }
    }

    public static abstract class NpcDeclaration extends EntityDeclaration {

        private boolean canSpawnBaseEntity = true;
        private final String npcType;
        private HashMap<String, String> subTypeIcons = new HashMap<String, String>();
        private HashMap<String, ItemStack> spawnEquipment = new HashMap<String, ItemStack>();

        public NpcDeclaration(Class<? extends Entity> entityClass, String entityName, int id, Object mod, int trackingRange, int updateFrequency, boolean sendsVelocityUpdates, String npcType) {
            super(entityClass, entityName, id, mod, trackingRange, updateFrequency, sendsVelocityUpdates);
            this.npcType = npcType;
        }

        public NpcDeclaration setCanSpawnBaseType(boolean can) {
            canSpawnBaseEntity = can;
            return this;
        }

        public void addSubtype(String type, String icon) {
            subTypeIcons.put(type, icon);
        }

        public NpcBase createEntity(World world, String subType) {
            NpcBase npc = (NpcBase) createEntity(world);
            if (!subType.isEmpty()) {
                ItemStack stack = spawnEquipment.get(subType);
                if (stack != null) {
                    npc.setCurrentItemOrArmor(0, stack.copy());
                }
            }
            return npc;
        }
    }

    public static abstract class NpcFactionDeclaration extends NpcDeclaration {

        public NpcFactionDeclaration(Class<? extends NpcFaction> entityClass, String entityName, int id, Object mod, int trackingRange, int updateFrequency, boolean sendsVelocityUpdates, String npcName) {
            super(entityClass, entityName, id, mod, trackingRange, updateFrequency, sendsVelocityUpdates, npcName);
        }

        @Override
        public NpcFaction createEntity(World world, String subType) {
            NpcFaction npc = (NpcFaction) createEntity(world);
            return npc;
        }

    }

}
