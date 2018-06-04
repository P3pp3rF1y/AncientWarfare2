package net.shadowmage.ancientwarfare.npc.entity;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.shadowmage.ancientwarfare.core.api.AWItems;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.entity.AWEntityRegistry;
import net.shadowmage.ancientwarfare.core.entity.AWEntityRegistry.EntityDeclaration;
import net.shadowmage.ancientwarfare.npc.AncientWarfareNPC;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcFaction;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcFactionArcher;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcFactionArcherElite;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcFactionBard;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcFactionCivilianFemale;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcFactionCivilianMale;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcFactionLeader;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcFactionLeaderElite;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcFactionMountedArcher;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcFactionMountedSoldier;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcFactionPriest;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcFactionSoldier;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcFactionSoldierElite;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcFactionTrader;
import net.shadowmage.ancientwarfare.npc.entity.vehicle.NpcSiegeEngineer;
import net.shadowmage.ancientwarfare.npc.item.AWNPCItems;
import net.shadowmage.ancientwarfare.npc.item.ItemNpcSpawner;
import net.shadowmage.ancientwarfare.npc.registry.FactionDefinition;
import net.shadowmage.ancientwarfare.npc.registry.FactionRegistry;

import javax.annotation.Nonnull;
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
		for (FactionDefinition faction : FactionRegistry.getFactions()) {
			addFaction(faction);
		}
	}

	private static void addPlayerOwnedNpcs() {
		NpcDeclaration reg = new NpcDeclaration(NpcCombat.class, AWEntityRegistry.NPC_COMBAT, "combat", "soldier");
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

		reg = new NpcDeclaration(NpcSiegeEngineer.class, AWEntityRegistry.NPC_SIEGE_ENGINEER, "siege.engineer");
		addNpcRegistration(reg);
	}

	private static void addFaction(FactionDefinition faction) {
		NpcFactionDeclaration reg;
		/*
		 * BANDITS
         */
		reg = new NpcFactionDeclaration(NpcFactionArcher.class, faction.getName(), AWEntityRegistry.NPC_FACTION_ARCHER, "archer");
		addNpcRegistration(reg);

		reg = new NpcFactionDeclaration(NpcFactionSoldier.class, faction.getName(), AWEntityRegistry.NPC_FACTION_SOLDIER, "soldier");
		addNpcRegistration(reg);

		reg = new NpcFactionDeclaration(NpcFactionLeader.class, faction.getName(), AWEntityRegistry.NPC_FACTION_COMMANDER, "commander");
		addNpcRegistration(reg);

		reg = new NpcFactionDeclaration(NpcFactionPriest.class, faction.getName(), AWEntityRegistry.NPC_FACTION_PRIEST, "priest");
		addNpcRegistration(reg);

		reg = new NpcFactionDeclaration(NpcFactionTrader.class, faction.getName(), AWEntityRegistry.NPC_FACTION_TRADER, "trader");
		addNpcRegistration(reg);

		reg = new NpcFactionDeclaration(NpcFactionMountedSoldier.class, faction.getName(), AWEntityRegistry.NPC_FACTION_CAVALRY, "soldier");
		addNpcRegistration(reg);

		reg = new NpcFactionDeclaration(NpcFactionMountedArcher.class, faction.getName(), AWEntityRegistry.NPC_FACTION_MOUNTED_ARCHER, "archer");
		addNpcRegistration(reg);

		reg = new NpcFactionDeclaration(NpcFactionCivilianMale.class, faction.getName(), AWEntityRegistry.NPC_FACTION_CIVILIAN_MALE, "civilian_male");
		addNpcRegistration(reg);

		reg = new NpcFactionDeclaration(NpcFactionCivilianFemale.class, faction.getName(), AWEntityRegistry.NPC_FACTION_CIVILIAN_FEMALE, "civilian_female");
		addNpcRegistration(reg);

		reg = new NpcFactionDeclaration(NpcFactionArcherElite.class, faction.getName(), AWEntityRegistry.NPC_FACTION_ARCHER_ELITE, "archer");
		addNpcRegistration(reg);

		reg = new NpcFactionDeclaration(NpcFactionSoldierElite.class, faction.getName(), AWEntityRegistry.NPC_FACTION_SOLDIER_ELITE, "soldier");
		addNpcRegistration(reg);

		reg = new NpcFactionDeclaration(NpcFactionLeaderElite.class, faction.getName(), AWEntityRegistry.NPC_FACTION_LEADER_ELITE, "commander");
		addNpcRegistration(reg);

		reg = new NpcFactionDeclaration(NpcFactionBard.class, faction.getName(), AWEntityRegistry.NPC_FACTION_BARD, "bard");
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
		npcMap.put((reg.getFaction().isEmpty() ? "" : reg.getFaction() + ".") + reg.getType(), reg);
	}

	public static NpcBase createNpc(World world, String npcType, String npcSubtype, String faction) {
		String key = (faction.isEmpty() ? "" : faction + ".faction.") + npcType;
		if (!npcMap.containsKey(key)) {
			return null;
		}
		NpcDeclaration reg = npcMap.get(key);
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

	public static void getSpawnerSubItems(NonNullList<ItemStack> list) {
		for (NpcDeclaration dec : npcMap.values()) {
			if (dec.canSpawnBaseEntity) {
				list.add(ItemNpcSpawner.getStackForNpcType(dec.npcType.replace("faction.", ""), "", dec.getFaction()));
			}
			for (String sub : dec.subTypeModelVariants.keySet()) {
				list.add(ItemNpcSpawner.getStackForNpcType(dec.npcType, sub));
			}
		}
	}

	public static List<String> getNPCItemModelVariants() {
		return ImmutableList.of(
				"siege_engineer",
				"archer",
				"bard",
				"commander",
				"courier",
				"craftsman",
				"engineer",
				"farmer",
				"lumberjack",
				"medic",
				"miner",
				"priest",
				"researcher",
				"trader",
				"soldier",
				"civilian_female",
				"civilian_male"
		);
	}

	public static String remapToModelVariant(String npcType) {
		switch (npcType) {
			case "leader":
			case "leader.elite":
				return "commander";
			case "civilian.female":
				return "civilian_female";
			case "civilian.male":
				return "civilian_male";
			case "worker":
				return "miner";
			case "combat":
			case "cavalry":
			case "soldier.elite":
				return "soldier";
			case "siege.engineer":
				return "siege_engineer";
			case "mounted_archer":
			case "archer.elite":
				return "archer";
			default:
				return npcType;
		}
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

		public String getFaction() {
			return "";
		}

		public String getType() {
			return npcType;
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

		private final String factionName;

		public NpcFactionDeclaration(Class<? extends NpcFaction> entityClass, String factionName, String entityName, String itemModelVariant) {
			super(entityClass, entityName, entityName, itemModelVariant);
			this.factionName = factionName;
		}

		@Override
		public NpcFaction createEntity(World world, String subType) {
			try {
				return (NpcFaction) entityClass.getConstructor(World.class, String.class).newInstance(world, factionName);
			}
			catch (Exception e) {
				AWLog.logError("Couldn't create entity:" + e.getMessage());
			}
			return null;
		}

		@Override
		public String getFaction() {
			return factionName;
		}
	}

}
