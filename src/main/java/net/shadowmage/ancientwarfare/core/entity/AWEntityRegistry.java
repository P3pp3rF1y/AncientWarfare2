package net.shadowmage.ancientwarfare.core.entity;

import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.EntityRegistry;
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

	public static final String NPC_FACTION_BANDIT_ARCHER = "bandit.archer";
	public static final String NPC_FACTION_BANDIT_SOLDIER = "bandit.soldier";
	public static final String NPC_FACTION_BANDIT_PRIEST = "bandit.priest";
	public static final String NPC_FACTION_BANDIT_TRADER = "bandit.trader";
	public static final String NPC_FACTION_BANDIT_COMMANDER = "bandit.leader";
	public static final String NPC_FACTION_BANDIT_CAVALRY = "bandit.cavalry";
	public static final String NPC_FACTION_BANDIT_MOUNTED_ARCHER = "bandit.mounted_archer";
	public static final String NPC_FACTION_BANDIT_CIVILIAN_MALE = "bandit.civilian.male";
	public static final String NPC_FACTION_BANDIT_ARCHER_ELITE = "bandit.archer.elite";
	public static final String NPC_FACTION_BANDIT_SOLDIER_ELITE = "bandit.soldier.elite";
	public static final String NPC_FACTION_BANDIT_LEADER_ELITE = "bandit.leader.elite";
	public static final String NPC_FACTION_BANDIT_CIVILIAN_FEMALE = "bandit.civilian.female";
	public static final String NPC_FACTION_PIRATE_ARCHER = "pirate.archer";
	public static final String NPC_FACTION_PIRATE_SOLDIER = "pirate.soldier";
	public static final String NPC_FACTION_PIRATE_PRIEST = "pirate.priest";
	public static final String NPC_FACTION_PIRATE_TRADER = "pirate.trader";
	public static final String NPC_FACTION_PIRATE_COMMANDER = "pirate.leader";
	public static final String NPC_FACTION_PIRATE_CAVALRY = "pirate.cavalry";
	public static final String NPC_FACTION_PIRATE_MOUNTED_ARCHER = "pirate.mounted_archer";
	public static final String NPC_FACTION_PIRATE_CIVILIAN_MALE = "pirate.civilian.male";
	public static final String NPC_FACTION_PIRATE_ARCHER_ELITE = "pirate.archer.elite";
	public static final String NPC_FACTION_PIRATE_SOLDIER_ELITE = "pirate.soldier.elite";
	public static final String NPC_FACTION_PIRATE_LEADER_ELITE = "pirate.leader.elite";
	public static final String NPC_FACTION_PIRATE_CIVILIAN_FEMALE = "pirate.civilian.female";
	public static final String NPC_FACTION_NATIVE_ARCHER = "native.archer";
	public static final String NPC_FACTION_NATIVE_SOLDIER = "native.soldier";
	public static final String NPC_FACTION_NATIVE_PRIEST = "native.priest";
	public static final String NPC_FACTION_NATIVE_TRADER = "native.trader";
	public static final String NPC_FACTION_NATIVE_COMMANDER = "native.leader";
	public static final String NPC_FACTION_NATIVE_CAVALRY = "native.cavalry";
	public static final String NPC_FACTION_NATIVE_MOUNTED_ARCHER = "native.mounted_archer";
	public static final String NPC_FACTION_NATIVE_CIVILIAN_MALE = "native.civilian.male";
	public static final String NPC_FACTION_NATIVE_ARCHER_ELITE = "native.archer.elite";
	public static final String NPC_FACTION_NATIVE_SOLDIER_ELITE = "native.soldier.elite";
	public static final String NPC_FACTION_NATIVE_LEADER_ELITE = "native.leader.elite";
	public static final String NPC_FACTION_NATIVE_CIVILIAN_FEMALE = "native.civilian.female";
	public static final String NPC_FACTION_DESERT_ARCHER = "desert.archer";
	public static final String NPC_FACTION_DESERT_SOLDIER = "desert.soldier";
	public static final String NPC_FACTION_DESERT_PRIEST = "desert.priest";
	public static final String NPC_FACTION_DESERT_TRADER = "desert.trader";
	public static final String NPC_FACTION_DESERT_COMMANDER = "desert.leader";
	public static final String NPC_FACTION_DESERT_CAVALRY = "desert.cavalry";
	public static final String NPC_FACTION_DESERT_MOUNTED_ARCHER = "desert.mounted_archer";
	public static final String NPC_FACTION_DESERT_CIVILIAN_MALE = "desert.civilian.male";
	public static final String NPC_FACTION_DESERT_ARCHER_ELITE = "desert.archer.elite";
	public static final String NPC_FACTION_DESERT_SOLDIER_ELITE = "desert.soldier.elite";
	public static final String NPC_FACTION_DESERT_LEADER_ELITE = "desert.leader.elite";
	public static final String NPC_FACTION_DESERT_CIVILIAN_FEMALE = "desert.civilian.female";
	public static final String NPC_FACTION_VIKING_ARCHER = "viking.archer";
	public static final String NPC_FACTION_VIKING_SOLDIER = "viking.soldier";
	public static final String NPC_FACTION_VIKING_PRIEST = "viking.priest";
	public static final String NPC_FACTION_VIKING_TRADER = "viking.trader";
	public static final String NPC_FACTION_VIKING_COMMANDER = "viking.leader";
	public static final String NPC_FACTION_VIKING_CAVALRY = "viking.cavalry";
	public static final String NPC_FACTION_VIKING_MOUNTED_ARCHER = "viking.mounted_archer";
	public static final String NPC_FACTION_VIKING_CIVILIAN_MALE = "viking.civilian.male";
	public static final String NPC_FACTION_VIKING_ARCHER_ELITE = "viking.archer.elite";
	public static final String NPC_FACTION_VIKING_SOLDIER_ELITE = "viking.soldier.elite";
	public static final String NPC_FACTION_VIKING_LEADER_ELITE = "viking.leader.elite";
	public static final String NPC_FACTION_VIKING_CIVILIAN_FEMALE = "viking.civilian.female";
	public static final String NPC_FACTION_CUSTOM_1_ARCHER = "custom_1.archer";
	public static final String NPC_FACTION_CUSTOM_1_SOLDIER = "custom_1.soldier";
	public static final String NPC_FACTION_CUSTOM_1_PRIEST = "custom_1.priest";
	public static final String NPC_FACTION_CUSTOM_1_TRADER = "custom_1.trader";
	public static final String NPC_FACTION_CUSTOM_1_COMMANDER = "custom_1.leader";
	public static final String NPC_FACTION_CUSTOM_1_CAVALRY = "custom_1.cavalry";
	public static final String NPC_FACTION_CUSTOM_1_MOUNTED_ARCHER = "custom_1.mounted_archer";
	public static final String NPC_FACTION_CUSTOM_1_CIVILIAN_MALE = "custom_1.civilian.male";
	public static final String NPC_FACTION_CUSTOM_1_ARCHER_ELITE = "custom_1.archer.elite";
	public static final String NPC_FACTION_CUSTOM_1_SOLDIER_ELITE = "custom_1.soldier.elite";
	public static final String NPC_FACTION_CUSTOM_1_LEADER_ELITE = "custom_1.leader.elite";
	public static final String NPC_FACTION_CUSTOM_1_CIVILIAN_FEMALE = "custom_1.civilian.female";
	public static final String NPC_FACTION_CUSTOM_2_ARCHER = "custom_2.archer";
	public static final String NPC_FACTION_CUSTOM_2_SOLDIER = "custom_2.soldier";
	public static final String NPC_FACTION_CUSTOM_2_PRIEST = "custom_2.priest";
	public static final String NPC_FACTION_CUSTOM_2_TRADER = "custom_2.trader";
	public static final String NPC_FACTION_CUSTOM_2_COMMANDER = "custom_2.leader";
	public static final String NPC_FACTION_CUSTOM_2_CAVALRY = "custom_2.cavalry";
	public static final String NPC_FACTION_CUSTOM_2_MOUNTED_ARCHER = "custom_2.mounted_archer";
	public static final String NPC_FACTION_CUSTOM_2_CIVILIAN_MALE = "custom_2.civilian.male";
	public static final String NPC_FACTION_CUSTOM_2_ARCHER_ELITE = "custom_2.archer.elite";
	public static final String NPC_FACTION_CUSTOM_2_SOLDIER_ELITE = "custom_2.soldier.elite";
	public static final String NPC_FACTION_CUSTOM_2_LEADER_ELITE = "custom_2.leader.elite";
	public static final String NPC_FACTION_CUSTOM_2_CIVILIAN_FEMALE = "custom_2.civilian.female";
	public static final String NPC_FACTION_CUSTOM_3_ARCHER = "custom_3.archer";
	public static final String NPC_FACTION_CUSTOM_3_SOLDIER = "custom_3.soldier";
	public static final String NPC_FACTION_CUSTOM_3_PRIEST = "custom_3.priest";
	public static final String NPC_FACTION_CUSTOM_3_TRADER = "custom_3.trader";
	public static final String NPC_FACTION_CUSTOM_3_COMMANDER = "custom_3.leader";
	public static final String NPC_FACTION_CUSTOM_3_CAVALRY = "custom_3.cavalry";
	public static final String NPC_FACTION_CUSTOM_3_MOUNTED_ARCHER = "custom_3.mounted_archer";
	public static final String NPC_FACTION_CUSTOM_3_CIVILIAN_MALE = "custom_3.civilian.male";
	public static final String NPC_FACTION_CUSTOM_3_ARCHER_ELITE = "custom_3.archer.elite";
	public static final String NPC_FACTION_CUSTOM_3_SOLDIER_ELITE = "custom_3.soldier.elite";
	public static final String NPC_FACTION_CUSTOM_3_LEADER_ELITE = "custom_3.leader.elite";
	public static final String NPC_FACTION_CUSTOM_3_CIVILIAN_FEMALE = "custom_3.civilian.female";

	public static final String NPC_FACTION_BANDIT_BARD = "bandit.bard";
	public static final String NPC_FACTION_DESERT_BARD = "desert.bard";
	public static final String NPC_FACTION_NATIVE_BARD = "native.bard";
	public static final String NPC_FACTION_PIRATE_BARD = "pirate.bard";
	public static final String NPC_FACTION_VIKING_BARD = "viking.bard";
	public static final String NPC_FACTION_CUSTOM_1_BARD = "custom_1.bard";
	public static final String NPC_FACTION_CUSTOM_2_BARD = "custom_2.bard";
	public static final String NPC_FACTION_CUSTOM_3_BARD = "custom_3.bard";

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
		EntityRegistry
				.registerModEntity(new ResourceLocation(reg.modID, reg.entityName), reg.entityClass, reg.entityName, reg.id, reg.mod(), reg.trackingRange(),
						reg.updateFrequency(), reg.sendsVelocityUpdates());
	}

	/*
	 * The entityClass for this registration -must- match the class returned from createEntity, or weird desynch crap will happen as server/client may be using different classes
	 * and/or the entity will not be persistent due to class mismatch
	 *
	 * @author Shadowmage
	 */
	public static abstract class EntityDeclaration {

		final Class<? extends Entity> entityClass;
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
