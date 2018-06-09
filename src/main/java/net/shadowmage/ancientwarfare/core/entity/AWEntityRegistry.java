package net.shadowmage.ancientwarfare.core.entity;

import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.shadowmage.ancientwarfare.core.config.AWLog;

public class AWEntityRegistry {

	/*
	 * NPC Module Registrations
	 */
	public static final String NPC_WORKER = "aw_npc_worker";
	public static final String NPC_COMBAT = "aw_npc_combat";
	public static final String NPC_COURIER = "aw_npc_courier";
	public static final String NPC_TRADER = "aw_npc_trader";
	public static final String NPC_PRIEST = "aw_npc_priest";
	public static final String NPC_BARD = "aw_npc_bard";
	public static final String NPC_SIEGE_ENGINEER = "aw_npc_siege_engineer";

	public static final String NPC_FACTION_ARCHER = "faction.archer";
	public static final String NPC_FACTION_SOLDIER = "faction.soldier";
	public static final String NPC_FACTION_PRIEST = "faction.priest";
	public static final String NPC_FACTION_TRADER = "faction.trader";
	public static final String NPC_FACTION_COMMANDER = "faction.leader";
	public static final String NPC_FACTION_CAVALRY = "faction.cavalry";
	public static final String NPC_FACTION_MOUNTED_ARCHER = "faction.mounted_archer";
	public static final String NPC_FACTION_CIVILIAN_MALE = "faction.civilian.male";
	public static final String NPC_FACTION_ARCHER_ELITE = "faction.archer.elite";
	public static final String NPC_FACTION_SOLDIER_ELITE = "faction.soldier.elite";
	public static final String NPC_FACTION_LEADER_ELITE = "faction.leader.elite";
	public static final String NPC_FACTION_CIVILIAN_FEMALE = "faction.civilian.female";
	public static final String NPC_FACTION_BARD = "faction.bard";
	public static final String NPC_FACTION_SIEGE_ENGINEER = "faction.siege_engineer";

    /*
	 * Vehicle Module Entity Registrations
     */

	public static final String VEHICLE_TEST = "vehicle";
	public static final String VEHICLE_CATAPULT = "catapult";
	public static final String MISSILE_TEST = "missile";

	/*
	 * Structure Module Entity Registrations
	 */
	//TODO add gates?? where are they registered at?
	public static final String AW_GATES = "aw_gate";

	public static void registerEntity(EntityDeclaration reg) {
		//TODO fix npc faction entities registration to not trigger this multiple times for same type and just different faction
		ResourceLocation registryName = new ResourceLocation(reg.modID, reg.entityName);
		if (!ForgeRegistries.ENTITIES.containsKey(registryName)) {
			EntityRegistry.registerModEntity(registryName, reg.entityClass, reg.entityName, reg.id, reg.mod(), reg.trackingRange(), reg.updateFrequency(), reg.sendsVelocityUpdates());
		}
	}

	/*
	 * The entityClass for this registration -must- match the class returned from createEntity, or weird desynch crap will happen as server/client may be using different classes
	 * and/or the entity will not be persistent due to class mismatch
	 *
	 * @author Shadowmage
	 */
	public static abstract class EntityDeclaration {

		protected final Class<? extends Entity> entityClass;
		final String entityName;
		final int id;
		final String modID;

		public EntityDeclaration(Class<? extends Entity> entityClass, String entityName, int id, String modID) {
			this.entityClass = entityClass;
			this.entityName = entityName;
			this.id = id;
			this.modID = modID;
		}

		public Entity createEntity(World world) {
			try {
				return entityClass.getConstructor(World.class).newInstance(world);
			}
			catch (Exception e) {
				AWLog.logError("Couldn't create entity:" + e.getMessage());
			}
			return null;
		}

		public String name() {
			return entityName;
		}

		public abstract Object mod();

		public abstract int trackingRange();

		public abstract int updateFrequency();

		public abstract boolean sendsVelocityUpdates();
	}

}
