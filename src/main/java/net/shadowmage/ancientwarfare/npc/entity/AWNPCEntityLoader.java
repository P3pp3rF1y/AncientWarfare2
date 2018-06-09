package net.shadowmage.ancientwarfare.npc.entity;

import com.google.common.collect.ImmutableList;
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
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcFactionSiegeEngineer;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcFactionSoldier;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcFactionSoldierElite;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcFactionTrader;
import net.shadowmage.ancientwarfare.npc.entity.vehicle.NpcSiegeEngineer;
import net.shadowmage.ancientwarfare.npc.item.AWNPCItems;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Mod.EventBusSubscriber(modid = AncientWarfareNPC.modID)
public class AWNPCEntityLoader {
	private static int nextID = 0;

	/*
	 * Npc base type -> NpcDeclaration<br>
	 * Used to retrieve declaration for creating entities<br>
	 * NpcDeclaration also stores information pertaining to npc-sub-type basic setup
	 */
	private static final HashMap<String, NpcDeclaration> npcMap = new HashMap<>();

	@SubscribeEvent
	public static void register(RegistryEvent.Register<EntityEntry> event) {
		addPlayerOwnedNpcs();
		addFaction();
	}

	private static void addPlayerOwnedNpcs() {
		NpcDeclaration reg = new NpcDeclaration(NpcCombat.class, AWEntityRegistry.NPC_COMBAT, "combat", "soldier");
		reg.addSubTypes("commander", "soldier", "archer", "medic", "engineer");
		addNpcRegistration(reg);

		reg = new NpcDeclaration(NpcWorker.class, AWEntityRegistry.NPC_WORKER, "worker", "miner");
		reg.addSubTypes("miner", "farmer", "lumberjack", "researcher", "craftsman");
		addNpcRegistration(reg);

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

	private static void addFaction() {
		NpcFactionDeclaration reg;
		/*
		 * BANDITS
         */
		reg = new NpcFactionDeclaration(NpcFactionArcher.class, AWEntityRegistry.NPC_FACTION_ARCHER, "archer");
		addNpcRegistration(reg);

		reg = new NpcFactionDeclaration(NpcFactionSoldier.class, AWEntityRegistry.NPC_FACTION_SOLDIER, "soldier");
		addNpcRegistration(reg);

		reg = new NpcFactionDeclaration(NpcFactionLeader.class, AWEntityRegistry.NPC_FACTION_COMMANDER, "commander");
		addNpcRegistration(reg);

		reg = new NpcFactionDeclaration(NpcFactionPriest.class, AWEntityRegistry.NPC_FACTION_PRIEST, "priest");
		addNpcRegistration(reg);

		reg = new NpcFactionDeclaration(NpcFactionTrader.class, AWEntityRegistry.NPC_FACTION_TRADER, "trader");
		addNpcRegistration(reg);

		reg = new NpcFactionDeclaration(NpcFactionMountedSoldier.class, AWEntityRegistry.NPC_FACTION_CAVALRY, "soldier");
		addNpcRegistration(reg);

		reg = new NpcFactionDeclaration(NpcFactionMountedArcher.class, AWEntityRegistry.NPC_FACTION_MOUNTED_ARCHER, "archer");
		addNpcRegistration(reg);

		reg = new NpcFactionDeclaration(NpcFactionCivilianMale.class, AWEntityRegistry.NPC_FACTION_CIVILIAN_MALE, "civilian_male");
		addNpcRegistration(reg);

		reg = new NpcFactionDeclaration(NpcFactionCivilianFemale.class, AWEntityRegistry.NPC_FACTION_CIVILIAN_FEMALE, "civilian_female");
		addNpcRegistration(reg);

		reg = new NpcFactionDeclaration(NpcFactionArcherElite.class, AWEntityRegistry.NPC_FACTION_ARCHER_ELITE, "archer");
		addNpcRegistration(reg);

		reg = new NpcFactionDeclaration(NpcFactionSoldierElite.class, AWEntityRegistry.NPC_FACTION_SOLDIER_ELITE, "soldier");
		addNpcRegistration(reg);

		reg = new NpcFactionDeclaration(NpcFactionLeaderElite.class, AWEntityRegistry.NPC_FACTION_LEADER_ELITE, "commander");
		addNpcRegistration(reg);

		reg = new NpcFactionDeclaration(NpcFactionBard.class, AWEntityRegistry.NPC_FACTION_BARD, "bard");
		addNpcRegistration(reg);

		reg = new NpcFactionDeclaration(NpcFactionSiegeEngineer.class, AWEntityRegistry.NPC_FACTION_SIEGE_ENGINEER, "siege_engineer");
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
		getNpcMap().put(reg.getType(), reg);
	}

	public static NpcBase createNpc(World world, String npcType, String npcSubtype, String faction) {
		if (!getNpcMap().containsKey(npcType)) {
			return null;
		}
		NpcDeclaration reg = getNpcMap().get(npcType);
		return reg.createEntity(world, npcSubtype, faction);
	}

	private static void addNpcSubtypeEquipment(String npcType, String npcSubtype, ItemStack equipment) {
		if (!getNpcMap().containsKey(npcType)) {
			throw new IllegalArgumentException("npc type must first be mapped");
		}
		NpcDeclaration reg = getNpcMap().get(npcType);
		reg.addSubtypeEquipment(npcSubtype, equipment);
	}

	public static Map<String, NpcDeclaration> getNpcMap() {
		return npcMap;
	}

	public static NpcDeclaration getNpcDeclaration(String npcType) {
		return npcMap.get(npcType);
	}

	public static class NpcDeclaration extends EntityDeclaration {

		private final String itemModelVariant;
		private boolean spawnBaseEntity = true;
		private final String npcType;
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

		private void addSubTypes(String... subTypes) {
			Arrays.stream(subTypes).forEach(s -> addSubtypeEquipment(s, ItemStack.EMPTY));
		}

		private void addSubtypeEquipment(String type, ItemStack equipment) {
			spawnEquipment.put(type, equipment);
		}

		public NpcBase createEntity(World world, String subType, String factionName) {
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

		public List<String> getItemModelVariants() {
			return new ImmutableList.Builder<String>().addAll(spawnEquipment.keySet()).add(itemModelVariant).build();
		}

		public String getItemModelVariant(String npcSubType) {
			if (npcSubType.isEmpty()) {
				return itemModelVariant;
			}
			return npcSubType;
		}

		public Set<String> getSubTypes() {
			return spawnEquipment.keySet();
		}

		public boolean canSpawnBaseEntity() {
			return spawnBaseEntity;
		}

		public String getNpcType() {
			return npcType;
		}
	}

	public static class NpcFactionDeclaration extends NpcDeclaration {
		public NpcFactionDeclaration(Class<? extends NpcFaction> entityClass, String entityName, String itemModelVariant) {
			super(entityClass, entityName, entityName, itemModelVariant);
		}

		@Override
		public NpcFaction createEntity(World world, String subType, String factionName) {
			try {
				return (NpcFaction) entityClass.getConstructor(World.class, String.class).newInstance(world, factionName);
			}
			catch (Exception e) {
				AWLog.logError("Couldn't create entity:" + e.getMessage());
			}
			return null;
		}

	}

}
