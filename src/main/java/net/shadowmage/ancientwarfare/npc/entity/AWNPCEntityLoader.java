package net.shadowmage.ancientwarfare.npc.entity;

import com.google.common.collect.Maps;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.shadowmage.ancientwarfare.core.api.AWItems;
import net.shadowmage.ancientwarfare.core.entity.AWEntityRegistry;
import net.shadowmage.ancientwarfare.core.entity.AWEntityRegistry.EntityDeclaration;
import net.shadowmage.ancientwarfare.npc.AncientWarfareNPC;
import net.shadowmage.ancientwarfare.npc.entity.faction.*;
import net.shadowmage.ancientwarfare.npc.item.AWNPCItems;
import net.shadowmage.ancientwarfare.npc.item.ItemNpcSpawner;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mod.EventBusSubscriber(modid = AncientWarfareNPC.modID)
public class AWNPCEntityLoader {
    private static int nextID = 0;
    private static Map<String, String> modelVariants = Maps.newHashMap();

    /*
     * Npc base type -> NpcDeclaration<br>
     * Used to retrieve declaration for creating entities<br>
     * NpcDeclaration also stores information pertaining to npc-sub-type basic setup
     */
    private static HashMap<String, NpcDeclaration> npcMap = new HashMap<>();

    @SubscribeEvent
    public static void register(RegistryEvent.Register<EntityEntry> event) {
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
        NpcDeclaration reg = new NpcDeclaration(NpcCombat.class, AWEntityRegistry.NPC_COMBAT, "combat");
        addNpcRegistration(reg);
        addNpcSubtypeEntry("combat", "commander", "commander");
        addNpcSubtypeEntry("combat", "soldier", "combat");
        addNpcSubtypeEntry("combat", "archer", "archer");
        addNpcSubtypeEntry("combat", "medic", "medic");
        addNpcSubtypeEntry("combat", "engineer", "engineer");

        reg = new NpcDeclaration(NpcWorker.class, AWEntityRegistry.NPC_WORKER, "worker", "miner");
        addNpcRegistration(reg);
        addNpcSubtypeEntry("worker", "miner", "miner");
        addNpcSubtypeEntry("worker", "farmer", "farmer");
        addNpcSubtypeEntry("worker", "lumberjack", "lumberjack");
        addNpcSubtypeEntry("worker", "researcher", "researcher");
        addNpcSubtypeEntry("worker", "craftsman", "craftsman");

        reg = new NpcDeclaration(NpcCourier.class, AWEntityRegistry.NPC_COURIER, "courier");
        addNpcRegistration(reg);

        reg = new NpcDeclaration(NpcTrader.class, AWEntityRegistry.NPC_TRADER, "trader");
        addNpcRegistration(reg);

        reg = new NpcDeclaration(NpcPriest.class, AWEntityRegistry.NPC_PRIEST, "priest");
        addNpcRegistration(reg);

        reg = new NpcDeclaration(NpcBard.class, AWEntityRegistry.NPC_BARD, "bard");
        addNpcRegistration(reg);
    }

    private static void addBandits() {
        NpcFactionDeclaration reg;
        /*
         * BANDITS
         */
        reg = new NpcFactionDeclaration(NpcBanditArcher.class, AWEntityRegistry.NPC_FACTION_BANDIT_ARCHER);
        addNpcRegistration(reg);

        reg = new NpcFactionDeclaration(NpcBanditSoldier.class, AWEntityRegistry.NPC_FACTION_BANDIT_SOLDIER);
        addNpcRegistration(reg);

        reg = new NpcFactionDeclaration(NpcBanditLeader.class, AWEntityRegistry.NPC_FACTION_BANDIT_COMMANDER);
        addNpcRegistration(reg);

        reg = new NpcFactionDeclaration(NpcBanditPriest.class, AWEntityRegistry.NPC_FACTION_BANDIT_PRIEST);
        addNpcRegistration(reg);

        reg = new NpcFactionDeclaration(NpcBanditTrader.class, AWEntityRegistry.NPC_FACTION_BANDIT_TRADER);
        addNpcRegistration(reg);

        reg = new NpcFactionDeclaration(NpcBanditMountedSoldier.class, AWEntityRegistry.NPC_FACTION_BANDIT_CAVALRY, "bandit_soldier");
        addNpcRegistration(reg);

        reg = new NpcFactionDeclaration(NpcBanditMountedArcher.class, AWEntityRegistry.NPC_FACTION_BANDIT_MOUNTED_ARCHER, "bandit_archer");
        addNpcRegistration(reg);

        reg = new NpcFactionDeclaration(NpcBanditCivilianMale.class, AWEntityRegistry.NPC_FACTION_BANDIT_CIVILIAN_MALE);
        addNpcRegistration(reg);

        reg = new NpcFactionDeclaration(NpcBanditCivilianFemale.class, AWEntityRegistry.NPC_FACTION_BANDIT_CIVILIAN_FEMALE);
        addNpcRegistration(reg);

        reg = new NpcFactionDeclaration(NpcBanditArcherElite.class, AWEntityRegistry.NPC_FACTION_BANDIT_ARCHER_ELITE, "bandit_archer");
        addNpcRegistration(reg);

        reg = new NpcFactionDeclaration(NpcBanditSoldierElite.class, AWEntityRegistry.NPC_FACTION_BANDIT_SOLDIER_ELITE, "bandit_soldier");
        addNpcRegistration(reg);

        reg = new NpcFactionDeclaration(NpcBanditLeaderElite.class, AWEntityRegistry.NPC_FACTION_BANDIT_LEADER_ELITE, "bandit_leader");
        addNpcRegistration(reg);

        reg = new NpcFactionDeclaration(NpcBanditBard.class, AWEntityRegistry.NPC_FACTION_BANDIT_BARD);
        addNpcRegistration(reg);
    }

    private static void addDesertNatives() {
        NpcFactionDeclaration reg;
        /*
         * DESERT NATIVES
         */
        reg = new NpcFactionDeclaration(NpcDesertArcher.class, AWEntityRegistry.NPC_FACTION_DESERT_ARCHER);
        addNpcRegistration(reg);

        reg = new NpcFactionDeclaration(NpcDesertSoldier.class, AWEntityRegistry.NPC_FACTION_DESERT_SOLDIER);
        addNpcRegistration(reg);

        reg = new NpcFactionDeclaration(NpcDesertLeader.class, AWEntityRegistry.NPC_FACTION_DESERT_COMMANDER);
        addNpcRegistration(reg);

        reg = new NpcFactionDeclaration(NpcDesertPriest.class, AWEntityRegistry.NPC_FACTION_DESERT_PRIEST);
        addNpcRegistration(reg);

        reg = new NpcFactionDeclaration(NpcDesertTrader.class, AWEntityRegistry.NPC_FACTION_DESERT_TRADER);
        addNpcRegistration(reg);

        reg = new NpcFactionDeclaration(NpcDesertMountedSoldier.class, AWEntityRegistry.NPC_FACTION_DESERT_CAVALRY, "desert_soldier");
        addNpcRegistration(reg);

        reg = new NpcFactionDeclaration(NpcDesertMountedArcher.class, AWEntityRegistry.NPC_FACTION_DESERT_MOUNTED_ARCHER, "desert_archer");
        addNpcRegistration(reg);

        reg = new NpcFactionDeclaration(NpcDesertCivilianMale.class, AWEntityRegistry.NPC_FACTION_DESERT_CIVILIAN_MALE);
        addNpcRegistration(reg);

        reg = new NpcFactionDeclaration(NpcDesertCivilianFemale.class, AWEntityRegistry.NPC_FACTION_DESERT_CIVILIAN_FEMALE);
        addNpcRegistration(reg);

        reg = new NpcFactionDeclaration(NpcDesertArcherElite.class, AWEntityRegistry.NPC_FACTION_DESERT_ARCHER_ELITE, "desert_archer");
        addNpcRegistration(reg);

        reg = new NpcFactionDeclaration(NpcDesertSoldierElite.class, AWEntityRegistry.NPC_FACTION_DESERT_SOLDIER_ELITE, "desert_soldier");
        addNpcRegistration(reg);

        reg = new NpcFactionDeclaration(NpcDesertLeaderElite.class, AWEntityRegistry.NPC_FACTION_DESERT_LEADER_ELITE, "desert_leader");
        addNpcRegistration(reg);

        reg = new NpcFactionDeclaration(NpcDesertBard.class, AWEntityRegistry.NPC_FACTION_DESERT_BARD);
        addNpcRegistration(reg);
    }

    private static void addJungleNatives() {
        NpcFactionDeclaration reg;
        /*
         * JUNGLE NATIVES
         */
        reg = new NpcFactionDeclaration(NpcNativeArcher.class, AWEntityRegistry.NPC_FACTION_NATIVE_ARCHER);
        addNpcRegistration(reg);

        reg = new NpcFactionDeclaration(NpcNativeSoldier.class, AWEntityRegistry.NPC_FACTION_NATIVE_SOLDIER);
        addNpcRegistration(reg);

        reg = new NpcFactionDeclaration(NpcNativeLeader.class, AWEntityRegistry.NPC_FACTION_NATIVE_COMMANDER);
        addNpcRegistration(reg);

        reg = new NpcFactionDeclaration(NpcNativePriest.class, AWEntityRegistry.NPC_FACTION_NATIVE_PRIEST);
        addNpcRegistration(reg);

        reg = new NpcFactionDeclaration(NpcNativeTrader.class, AWEntityRegistry.NPC_FACTION_NATIVE_TRADER);
        addNpcRegistration(reg);

        reg = new NpcFactionDeclaration(NpcNativeMountedSoldier.class, AWEntityRegistry.NPC_FACTION_NATIVE_CAVALRY, "native_soldier");
        addNpcRegistration(reg);

        reg = new NpcFactionDeclaration(NpcNativeMountedArcher.class, AWEntityRegistry.NPC_FACTION_NATIVE_MOUNTED_ARCHER, "native_archer");
        addNpcRegistration(reg);

        reg = new NpcFactionDeclaration(NpcNativeCivilianMale.class, AWEntityRegistry.NPC_FACTION_NATIVE_CIVILIAN_MALE);
        addNpcRegistration(reg);

        reg = new NpcFactionDeclaration(NpcNativeCivilianFemale.class, AWEntityRegistry.NPC_FACTION_NATIVE_CIVILIAN_FEMALE);
        addNpcRegistration(reg);

        reg = new NpcFactionDeclaration(NpcNativeArcherElite.class, AWEntityRegistry.NPC_FACTION_NATIVE_ARCHER_ELITE, "native_archer");
        addNpcRegistration(reg);

        reg = new NpcFactionDeclaration(NpcNativeSoldierElite.class, AWEntityRegistry.NPC_FACTION_NATIVE_SOLDIER_ELITE, "native_soldier");
        addNpcRegistration(reg);

        reg = new NpcFactionDeclaration(NpcNativeLeaderElite.class, AWEntityRegistry.NPC_FACTION_NATIVE_LEADER_ELITE, "native_leader");
        addNpcRegistration(reg);

        reg = new NpcFactionDeclaration(NpcNativeBard.class, AWEntityRegistry.NPC_FACTION_NATIVE_BARD);
        addNpcRegistration(reg);
    }

    private static void addPirates() {
        NpcFactionDeclaration reg;
        /*
         * PIRATES
         */
        reg = new NpcFactionDeclaration(NpcPirateArcher.class, AWEntityRegistry.NPC_FACTION_PIRATE_ARCHER);
        addNpcRegistration(reg);

        reg = new NpcFactionDeclaration(NpcPirateSoldier.class, AWEntityRegistry.NPC_FACTION_PIRATE_SOLDIER);
        addNpcRegistration(reg);

        reg = new NpcFactionDeclaration(NpcPirateLeader.class, AWEntityRegistry.NPC_FACTION_PIRATE_COMMANDER);
        addNpcRegistration(reg);

        reg = new NpcFactionDeclaration(NpcPiratePriest.class, AWEntityRegistry.NPC_FACTION_PIRATE_PRIEST);
        addNpcRegistration(reg);

        reg = new NpcFactionDeclaration(NpcPirateTrader.class, AWEntityRegistry.NPC_FACTION_PIRATE_TRADER);
        addNpcRegistration(reg);

        reg = new NpcFactionDeclaration(NpcPirateMountedSoldier.class, AWEntityRegistry.NPC_FACTION_PIRATE_CAVALRY, "pirate_soldier");
        addNpcRegistration(reg);

        reg = new NpcFactionDeclaration(NpcPirateMountedArcher.class, AWEntityRegistry.NPC_FACTION_PIRATE_MOUNTED_ARCHER, "pirate_archer");
        addNpcRegistration(reg);

        reg = new NpcFactionDeclaration(NpcPirateCivilianMale.class, AWEntityRegistry.NPC_FACTION_PIRATE_CIVILIAN_MALE);
        addNpcRegistration(reg);

        reg = new NpcFactionDeclaration(NpcPirateCivilianFemale.class, AWEntityRegistry.NPC_FACTION_PIRATE_CIVILIAN_FEMALE);
        addNpcRegistration(reg);

        reg = new NpcFactionDeclaration(NpcPirateArcherElite.class, AWEntityRegistry.NPC_FACTION_PIRATE_ARCHER_ELITE, "pirate_archer");
        addNpcRegistration(reg);

        reg = new NpcFactionDeclaration(NpcPirateSoldierElite.class, AWEntityRegistry.NPC_FACTION_PIRATE_SOLDIER_ELITE, "pirate_soldier");
        addNpcRegistration(reg);

        reg = new NpcFactionDeclaration(NpcPirateLeaderElite.class, AWEntityRegistry.NPC_FACTION_PIRATE_LEADER_ELITE, "pirate_leader");
        addNpcRegistration(reg);

        reg = new NpcFactionDeclaration(NpcPirateBard.class, AWEntityRegistry.NPC_FACTION_PIRATE_BARD);
        addNpcRegistration(reg);
    }

    private static void addVikings() {
        NpcFactionDeclaration reg;
        /*
         * VIKINGS
         */
        reg = new NpcFactionDeclaration(NpcVikingArcher.class, AWEntityRegistry.NPC_FACTION_VIKING_ARCHER);
        addNpcRegistration(reg);

        reg = new NpcFactionDeclaration(NpcVikingSoldier.class, AWEntityRegistry.NPC_FACTION_VIKING_SOLDIER);
        addNpcRegistration(reg);

        reg = new NpcFactionDeclaration(NpcVikingLeader.class, AWEntityRegistry.NPC_FACTION_VIKING_COMMANDER);
        addNpcRegistration(reg);

        reg = new NpcFactionDeclaration(NpcVikingPriest.class, AWEntityRegistry.NPC_FACTION_VIKING_PRIEST);
        addNpcRegistration(reg);

        reg = new NpcFactionDeclaration(NpcVikingTrader.class, AWEntityRegistry.NPC_FACTION_VIKING_TRADER);
        addNpcRegistration(reg);

        reg = new NpcFactionDeclaration(NpcVikingMountedSoldier.class, AWEntityRegistry.NPC_FACTION_VIKING_CAVALRY, "viking_soldier");
        addNpcRegistration(reg);

        reg = new NpcFactionDeclaration(NpcVikingMountedArcher.class, AWEntityRegistry.NPC_FACTION_VIKING_MOUNTED_ARCHER, "viking_archer");
        addNpcRegistration(reg);

        reg = new NpcFactionDeclaration(NpcVikingCivilianMale.class, AWEntityRegistry.NPC_FACTION_VIKING_CIVILIAN_MALE);
        addNpcRegistration(reg);

        reg = new NpcFactionDeclaration(NpcVikingCivilianFemale.class, AWEntityRegistry.NPC_FACTION_VIKING_CIVILIAN_FEMALE);
        addNpcRegistration(reg);

        reg = new NpcFactionDeclaration(NpcVikingArcherElite.class, AWEntityRegistry.NPC_FACTION_VIKING_ARCHER_ELITE, "viking_archer");
        addNpcRegistration(reg);

        reg = new NpcFactionDeclaration(NpcVikingSoldierElite.class, AWEntityRegistry.NPC_FACTION_VIKING_SOLDIER_ELITE, "viking_soldier");
        addNpcRegistration(reg);

        reg = new NpcFactionDeclaration(NpcVikingLeaderElite.class, AWEntityRegistry.NPC_FACTION_VIKING_LEADER_ELITE, "viking_leader");
        addNpcRegistration(reg);

        reg = new NpcFactionDeclaration(NpcVikingBard.class, AWEntityRegistry.NPC_FACTION_VIKING_BARD);
        addNpcRegistration(reg);
    }

    private static void addCustom1() {
        NpcFactionDeclaration reg;
        /*
         * CUSTOM_1S
         */
        reg = new NpcFactionDeclaration(NpcCustom_1Archer.class, AWEntityRegistry.NPC_FACTION_CUSTOM_1_ARCHER);
        addNpcRegistration(reg);

        reg = new NpcFactionDeclaration(NpcCustom_1Soldier.class, AWEntityRegistry.NPC_FACTION_CUSTOM_1_SOLDIER);
        addNpcRegistration(reg);

        reg = new NpcFactionDeclaration(NpcCustom_1Leader.class, AWEntityRegistry.NPC_FACTION_CUSTOM_1_COMMANDER);
        addNpcRegistration(reg);

        reg = new NpcFactionDeclaration(NpcCustom_1Priest.class, AWEntityRegistry.NPC_FACTION_CUSTOM_1_PRIEST);
        addNpcRegistration(reg);

        reg = new NpcFactionDeclaration(NpcCustom_1Trader.class, AWEntityRegistry.NPC_FACTION_CUSTOM_1_TRADER);
        addNpcRegistration(reg);

        reg = new NpcFactionDeclaration(NpcCustom_1MountedSoldier.class, AWEntityRegistry.NPC_FACTION_CUSTOM_1_CAVALRY, "custom_1_soldier");
        addNpcRegistration(reg);

        reg = new NpcFactionDeclaration(NpcCustom_1MountedArcher.class, AWEntityRegistry.NPC_FACTION_CUSTOM_1_MOUNTED_ARCHER, "custom_1_archer");
        addNpcRegistration(reg);

        reg = new NpcFactionDeclaration(NpcCustom_1CivilianMale.class, AWEntityRegistry.NPC_FACTION_CUSTOM_1_CIVILIAN_MALE);
        addNpcRegistration(reg);

        reg = new NpcFactionDeclaration(NpcCustom_1CivilianFemale.class, AWEntityRegistry.NPC_FACTION_CUSTOM_1_CIVILIAN_FEMALE);
        addNpcRegistration(reg);

        reg = new NpcFactionDeclaration(NpcCustom_1ArcherElite.class, AWEntityRegistry.NPC_FACTION_CUSTOM_1_ARCHER_ELITE, "custom_1_archer");
        addNpcRegistration(reg);

        reg = new NpcFactionDeclaration(NpcCustom_1SoldierElite.class, AWEntityRegistry.NPC_FACTION_CUSTOM_1_SOLDIER_ELITE, "custom_1_soldier");
        addNpcRegistration(reg);

        reg = new NpcFactionDeclaration(NpcCustom_1LeaderElite.class, AWEntityRegistry.NPC_FACTION_CUSTOM_1_LEADER_ELITE, "custom_1_leader");
        addNpcRegistration(reg);

        reg = new NpcFactionDeclaration(NpcCustom_1Bard.class, AWEntityRegistry.NPC_FACTION_CUSTOM_1_BARD);
        addNpcRegistration(reg);
    }

    private static void addCustom2() {
        NpcFactionDeclaration reg;
        /*
         * CUSTOM_2S
         */
        reg = new NpcFactionDeclaration(NpcCustom_2Archer.class, AWEntityRegistry.NPC_FACTION_CUSTOM_2_ARCHER);
        addNpcRegistration(reg);

        reg = new NpcFactionDeclaration(NpcCustom_2Soldier.class, AWEntityRegistry.NPC_FACTION_CUSTOM_2_SOLDIER);
        addNpcRegistration(reg);

        reg = new NpcFactionDeclaration(NpcCustom_2Leader.class, AWEntityRegistry.NPC_FACTION_CUSTOM_2_COMMANDER);
        addNpcRegistration(reg);

        reg = new NpcFactionDeclaration(NpcCustom_2Priest.class, AWEntityRegistry.NPC_FACTION_CUSTOM_2_PRIEST);
        addNpcRegistration(reg);

        reg = new NpcFactionDeclaration(NpcCustom_2Trader.class, AWEntityRegistry.NPC_FACTION_CUSTOM_2_TRADER);
        addNpcRegistration(reg);

        reg = new NpcFactionDeclaration(NpcCustom_2MountedSoldier.class, AWEntityRegistry.NPC_FACTION_CUSTOM_2_CAVALRY, "custom_2_soldier");
        addNpcRegistration(reg);

        reg = new NpcFactionDeclaration(NpcCustom_2MountedArcher.class, AWEntityRegistry.NPC_FACTION_CUSTOM_2_MOUNTED_ARCHER, "custom_2_archer");
        addNpcRegistration(reg);

        reg = new NpcFactionDeclaration(NpcCustom_2CivilianMale.class, AWEntityRegistry.NPC_FACTION_CUSTOM_2_CIVILIAN_MALE);
        addNpcRegistration(reg);

        reg = new NpcFactionDeclaration(NpcCustom_2CivilianFemale.class, AWEntityRegistry.NPC_FACTION_CUSTOM_2_CIVILIAN_FEMALE);
        addNpcRegistration(reg);

        reg = new NpcFactionDeclaration(NpcCustom_2ArcherElite.class, AWEntityRegistry.NPC_FACTION_CUSTOM_2_ARCHER_ELITE, "custom_2_archer");
        addNpcRegistration(reg);

        reg = new NpcFactionDeclaration(NpcCustom_2SoldierElite.class, AWEntityRegistry.NPC_FACTION_CUSTOM_2_SOLDIER_ELITE, "custom_2_soldier");
        addNpcRegistration(reg);

        reg = new NpcFactionDeclaration(NpcCustom_2LeaderElite.class, AWEntityRegistry.NPC_FACTION_CUSTOM_2_LEADER_ELITE, "custom_2_leader");
        addNpcRegistration(reg);

        reg = new NpcFactionDeclaration(NpcCustom_2Bard.class, AWEntityRegistry.NPC_FACTION_CUSTOM_2_BARD);
        addNpcRegistration(reg);
    }

    private static void addCustom3() {

        NpcFactionDeclaration reg;
        /*
         * CUSTOM_3S
         */
        reg = new NpcFactionDeclaration(NpcCustom_3Archer.class, AWEntityRegistry.NPC_FACTION_CUSTOM_3_ARCHER);
        addNpcRegistration(reg);

        reg = new NpcFactionDeclaration(NpcCustom_3Soldier.class, AWEntityRegistry.NPC_FACTION_CUSTOM_3_SOLDIER);
        addNpcRegistration(reg);

        reg = new NpcFactionDeclaration(NpcCustom_3Leader.class, AWEntityRegistry.NPC_FACTION_CUSTOM_3_COMMANDER);
        addNpcRegistration(reg);

        reg = new NpcFactionDeclaration(NpcCustom_3Priest.class, AWEntityRegistry.NPC_FACTION_CUSTOM_3_PRIEST);
        addNpcRegistration(reg);

        reg = new NpcFactionDeclaration(NpcCustom_3Trader.class, AWEntityRegistry.NPC_FACTION_CUSTOM_3_TRADER);
        addNpcRegistration(reg);

        reg = new NpcFactionDeclaration(NpcCustom_3MountedSoldier.class, AWEntityRegistry.NPC_FACTION_CUSTOM_3_CAVALRY, "custom_3_soldier");
        addNpcRegistration(reg);

        reg = new NpcFactionDeclaration(NpcCustom_3MountedArcher.class, AWEntityRegistry.NPC_FACTION_CUSTOM_3_MOUNTED_ARCHER, "custom_3_archer");
        addNpcRegistration(reg);

        reg = new NpcFactionDeclaration(NpcCustom_3CivilianMale.class, AWEntityRegistry.NPC_FACTION_CUSTOM_3_CIVILIAN_MALE);
        addNpcRegistration(reg);

        reg = new NpcFactionDeclaration(NpcCustom_3CivilianFemale.class, AWEntityRegistry.NPC_FACTION_CUSTOM_3_CIVILIAN_FEMALE);
        addNpcRegistration(reg);

        reg = new NpcFactionDeclaration(NpcCustom_3ArcherElite.class, AWEntityRegistry.NPC_FACTION_CUSTOM_3_ARCHER_ELITE, "custom_3_archer");
        addNpcRegistration(reg);

        reg = new NpcFactionDeclaration(NpcCustom_3SoldierElite.class, AWEntityRegistry.NPC_FACTION_CUSTOM_3_SOLDIER_ELITE, "custom_3_soldier");
        addNpcRegistration(reg);

        reg = new NpcFactionDeclaration(NpcCustom_3LeaderElite.class, AWEntityRegistry.NPC_FACTION_CUSTOM_3_LEADER_ELITE, "custom_3_leader");
        addNpcRegistration(reg);

        reg = new NpcFactionDeclaration(NpcCustom_3Bard.class, AWEntityRegistry.NPC_FACTION_CUSTOM_3_BARD);
        addNpcRegistration(reg);
    }

    /*
     * has to be called during post-init so that all items/etc are fully initialized
     */
    public static void loadNpcSubtypeEquipment() {
        addNpcSubtypeEquipment("worker", "farmer", new ItemStack(Items.IRON_HOE));
        addNpcSubtypeEquipment("worker", "miner", new ItemStack(Items.IRON_PICKAXE));
        addNpcSubtypeEquipment("worker", "lumberjack", new ItemStack(Items.IRON_AXE));
        addNpcSubtypeEquipment("worker", "researcher", new ItemStack(AWItems.quillIron));
        addNpcSubtypeEquipment("worker", "craftsman", new ItemStack(AWItems.automationHammerIron));

        addNpcSubtypeEquipment("combat", "commander", new ItemStack(AWNPCItems.commandBatonIron));
        addNpcSubtypeEquipment("combat", "soldier", new ItemStack(Items.IRON_SWORD));
        addNpcSubtypeEquipment("combat", "archer", new ItemStack(Items.BOW));
        addNpcSubtypeEquipment("combat", "engineer", new ItemStack(AWItems.automationHammerIron));
        addNpcSubtypeEquipment("combat", "medic", new ItemStack(Items.IRON_AXE));
    }

    private static void addNpcRegistration(NpcDeclaration reg) {
        AWEntityRegistry.registerEntity(reg);
        npcMap.put(reg.getType(), reg);
    }

    public static NpcBase createNpc(World world, String npcType, String npcSubtype) {
        if (!npcMap.containsKey(npcType)) {
            return null;
        }
        NpcDeclaration reg = npcMap.get(npcType);
        return reg.createEntity(world, npcSubtype);
    }

    private static void addNpcSubtypeEntry(String npcType, String npcSubtype, String modelVariant) {
        if (!npcMap.containsKey(npcType)) {
            throw new IllegalArgumentException("npc type must first be mapped");
        }
        npcMap.get(npcType).addSubtype(npcSubtype, modelVariant);
    }

    private static void addNpcSubtypeEquipment(String npcType, String npcSubtype, ItemStack equipment) {
        if (!npcMap.containsKey(npcType)) {
            throw new IllegalArgumentException("npc type must first be mapped");
        }
        NpcDeclaration reg = npcMap.get(npcType);
        if (!reg.subTypeModelVariants.containsKey(npcSubtype)) {
            throw new IllegalArgumentException("npc subtype must first be mapped");
        }
        reg.spawnEquipment.put(npcSubtype, equipment);
    }

    /*
     * used by npc spawner item to get the sub-items
     */

    public static void getSpawnerSubItems(List<ItemStack> list) {
        for (NpcDeclaration dec : npcMap.values()) {
            if (dec.canSpawnBaseEntity) {
                list.add(ItemNpcSpawner.getStackForNpcType(dec.npcType, ""));
            }
            for (String sub : dec.subTypeModelVariants.keySet()) {
                list.add(ItemNpcSpawner.getStackForNpcType(dec.npcType, sub));
            }
        }
    }

    public static NpcDeclaration getNpcDeclaration(String npcType) {
        return npcMap.get(npcType);
    }

    public static Collection<NpcDeclaration> getAllNpcDeclarations() {
        return npcMap.values();
    }

    public static class NpcDeclaration extends EntityDeclaration {

        private final String itemModelVariant;
        private boolean canSpawnBaseEntity = true;
        private final String npcType;
        private final HashMap<String, String> subTypeModelVariants = new HashMap<>();
        private final HashMap<String, ItemStack> spawnEquipment = new HashMap<>();

        public NpcDeclaration(Class<? extends Entity> entityClass, String entityName, String npcType) {
            this(entityClass, entityName, npcType, npcType.replace(".", "_"));
        }
        public NpcDeclaration(Class<? extends Entity> entityClass, String entityName, String npcType, String itemModelVariant) {
            super(entityClass, entityName, nextID++, AncientWarfareNPC.modID);
            this.npcType = npcType;
            this.itemModelVariant = itemModelVariant;
        }

        public String getType() {
            return npcType;
        }

        public NpcDeclaration setCanSpawnBaseType(boolean can) {
            canSpawnBaseEntity = can;
            return this;
        }

        public void addSubtype(String type, String itemModelVariant) {
            subTypeModelVariants.put(type, itemModelVariant);
        }

        public NpcBase createEntity(World world, String subType) {
            NpcBase npc = (NpcBase) createEntity(world);
            if (!subType.isEmpty()) {
                @Nonnull ItemStack stack = spawnEquipment.get(subType);
                if (!stack.isEmpty()) {
                    npc.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, stack.copy());
                }
            }
            return npc;
        }

        @Override
        public final Object mod() {
            return AncientWarfareNPC.instance;
        }

        @Override
        public final int trackingRange() {
            return 120;
        }

        @Override
        public final int updateFrequency() {
            return 3;
        }

        @Override
        public final boolean sendsVelocityUpdates() {
            return true;
        }

        public String getItemModelVariant() {
            return itemModelVariant;
        }

        public String getSubTypeModelVariant(String subType) {
            return subTypeModelVariants.get(subType);
        }

        public boolean getCanSpawnBaseType() {
            return canSpawnBaseEntity;
        }

        public Map<String, String> getSubTypeModelVariants() {
            return subTypeModelVariants;
        }
    }

    public static class NpcFactionDeclaration extends NpcDeclaration {

        public NpcFactionDeclaration(Class<? extends NpcFaction> entityClass, String entityName) {
            super(entityClass, entityName, entityName);
        }
        public NpcFactionDeclaration(Class<? extends NpcFaction> entityClass, String entityName, String itemModelVariant) {
            super(entityClass, entityName, entityName, itemModelVariant);
        }

        @Override
        public NpcFaction createEntity(World world, String subType) {
            return (NpcFaction) createEntity(world);
        }
    }

}
