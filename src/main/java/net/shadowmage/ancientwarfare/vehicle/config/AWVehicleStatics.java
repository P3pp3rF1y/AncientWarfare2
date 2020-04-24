package net.shadowmage.ancientwarfare.vehicle.config;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.shadowmage.ancientwarfare.core.config.ModConfiguration;
import net.shadowmage.ancientwarfare.vehicle.AncientWarfareVehicles;

@Config(modid = AncientWarfareVehicles.MOD_ID, name = "ancientwarfare\\AncientWarfareVehicle")
public class AWVehicleStatics extends ModConfiguration {

	@SuppressWarnings("unused")
	@Mod.EventBusSubscriber(modid = AncientWarfareVehicles.MOD_ID)
	private static class EventHandler {
		/**
		 * Inject the new values and save to the config file when the config has been changed from the GUI.
		 *
		 * @param event The event
		 */
		@SubscribeEvent
		public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
			if (event.getModID().equals(AncientWarfareVehicles.MOD_ID)) {
				ConfigManager.sync(AncientWarfareVehicles.MOD_ID, Config.Type.INSTANCE);
			}
		}
	}

	@Config.Ignore
	public static final String KEY_VEHICLE_FORWARD = "keybind.vehicle.forward";
	@Config.Ignore
	public static final String KEY_VEHICLE_REVERSE = "keybind.vehicle.reverse";
	@Config.Ignore
	public static final String KEY_VEHICLE_LEFT = "keybind.vehicle.left";
	@Config.Ignore
	public static final String KEY_VEHICLE_RIGHT = "keybind.vehicle.right";
	@Config.Ignore
	public static final String KEY_VEHICLE_FIRE = "keybind.vehicle.fire";
	@Config.Ignore
	public static final String KEY_VEHICLE_ASCEND_AIM_UP = "keybind.vehicle.ascend.aim.up";
	@Config.Ignore
	public static final String KEY_VEHICLE_DESCEND_AIM_DOWN = "keybind.vehicle.descend.aim.down";
	@Config.Ignore
	public static final String KEY_VEHICLE_AMMO_PREV = "keybind.vehicle.ammo.prev";
	@Config.Ignore
	public static final String KEY_VEHICLE_AMMO_NEXT = "keybind.vehicle.ammo.next";
	@Config.Ignore
	public static final String KEY_VEHICLE_TURRET_LEFT = "keybind.vehicle.turret.left";
	@Config.Ignore
	public static final String KEY_VEHICLE_TURRET_RIGHT = "keybind.vehicle.turret.right";
	@Config.Ignore
	public static final String KEY_VEHICLE_MOUSE_AIM = "keybind.vehicle.mouse.aim";
	@Config.Ignore
	public static final String KEY_VEHICLE_AMMO_SELECT = "keybind.vehicle.ammo.select";

	@Config.Ignore
	public static final String ClientAndServerSideNote = "Affect both client and server. These configs must match for client and server, or strange and probably BAD things WILL happen.";

	public AWVehicleStatics(String mod) {
		super(mod);
	}

	@Config.Name("General Settings")
	@Config.Comment(ClientAndServerSideNote)
	public static GeneralSettings generalSettings = new GeneralSettings();

	public static class GeneralSettings {

		@Config.Name("oversize_ammo_enabled")
		@Config.Comment("Determines whether over sized ammunition (30kg, 45kg) can be fired from the regular sized vehicles (e.g. not the Giant Trebuchet)\n" + ClientAndServerSideNote)
		@Config.RequiresMcRestart
		public boolean oversizeAmmoEnabled = true;

		@Config.Name("owned_soldiers_use_ammo")
		@Config.Comment("Makes PLAYER OWNED soldiers require ammo for firing vehicles\n" + ClientAndServerSideNote)
		@Config.RequiresMcRestart
		public boolean ownedSoldiersUseAmmo = true;

		@Config.Name("vehicles_tear_up_grass")
		@Config.Comment("Determines whether moving vehicles turn grass blocks into dirt under them by tearing grass\n" + ClientAndServerSideNote)
		public boolean vehiclesTearUpGrass = true;

		@Config.Name("shots_destroys_blocks")
		@Config.Comment("Determines whether vehicle shots can break blocks or not. Does NOT affect the Battering Ram's block breaking\n" + ClientAndServerSideNote)
		public boolean shotsDestroysBlocks = true;

		@Config.Name("battering_ram_breaks_blocks")
		@Config.Comment("Determines whether the Battering Ram can break blocks or not. (Doesn't affect it's ability to breach gates)\n" + ClientAndServerSideNote)
		@Config.RequiresMcRestart
		public boolean batteringRamBreaksBlocks = true;

		@Config.Name("battering_ram_block_break_percentage_chance")
		@Config.Comment("Determines the % chance how successfully the Battering Ram can break blocks, applies to each block individually in it's breaking radius. "
				+ "Doesn't affect it's ability to breach gates. Disregarded if batteringRamBreaksBlocks is false\n" + ClientAndServerSideNote)
		@Config.RequiresMcRestart
		@Config.RangeInt(min = 1, max = 100)
		public int batteringRamBlockBreakPercentageChance = 20;
		@Config.Name("block_fires")
		@Config.Comment("Determines whether the fire and explosive shots can light nearby blocks on fire\n" + ClientAndServerSideNote)
		@Config.RequiresMcRestart
		public boolean blockFires = true;

		@Config.Name("allow_friendly_fire")
		@Config.Comment("Determines whether vehicle shots can hurt friendly (player owned or allied) NPCs and friendly players\n" + ClientAndServerSideNote)
		@Config.RequiresMcRestart
		public boolean allowFriendlyFire = false;

		@Config.Name("use_vehicle_setup_time")
		@Config.Comment("Makes vehicles require a setup time after they are placed\n" + ClientAndServerSideNote)
		public boolean useVehicleSetupTime = true;
	}

	@Config.Name("Client Side Settings")
	@Config.Comment("Affect only client-side operations.  Many of these options can be set from the in-game Options GUI.\n"
			+ "# Server admins can ignore these settings.")
	public static ClientSettings clientSettings = new ClientSettings();

	public static class ClientSettings {
		@Config.Name("adjust_missiles_for_accuracy")
		@Config.Comment("") // TODO
		@Config.RequiresMcRestart
		public boolean adjustMissilesForAccuracy = true;

		@Config.Name("render_vehicles_in_first_person")
		@Config.Comment("Determines whether a vehicle should be rendered in first person view while a player rides it.")
		public boolean renderVehiclesInFirstPerson = true;

		@Config.Name("render_vehicle_nameplates")
		@Config.Comment("Renders the name plates above the vehicles")
		public boolean renderVehicleNameplates = true;

		@Config.Name("render_vehicle_nameplate_health")
		@Config.Comment("Determines whether to render the vehicle health in the name plate or not, requires render_vehicle_nameplates.")
		public boolean renderVehicleNameplateHealth = true;

		@Config.Name("render_overlay")
		@Config.Comment("Determines whether to show the vehicle info overlay while driving a vehicle.")
		public boolean renderOverlay = true;

		@Config.Name("render_adv_overlay")
		@Config.Comment("Determines whether to show the advanced vehicle info overlay while driving a vehicle.")
		public boolean renderAdvOverlay = true;

		@Config.Ignore // TODO: this probably needs code changes, unused now
		public boolean enableMouseAim = true;
	}

	@Config.Name("Vehicle Stats Settings")
	@Config.Comment("test comment")
	public static VehicleStats vehicleStats = new VehicleStats();

	public static class VehicleStats {
		@Config.Name("ammo_ballista_bolt_damage")
		@Config.Comment("Ballista Bolt damage")
		@Config.RangeInt(min = 6, max = 80)
		public int ammoBallistaBoltDamage = 18;

		@Config.Name("ammo_ballista_bolt_iron_damage")
		@Config.Comment("Ballista Iron Bolt damage")
		@Config.RangeInt(min = 6, max = 80)
		public int ammoBallistaBoltIronDamage = 30;

		@Config.Name("ammo_ballista_bolt_flame_damage")
		@Config.Comment("Ballista Flame Bolt damage")
		@Config.RangeInt(min = 6, max = 80)
		public int ammoBallistaBoltFlameDamage = 16;

		@Config.Name("ammo_ballista_bolt_explosive_damage")
		@Config.Comment("Ballista Explosive Bolt damage")
		@Config.RangeInt(min = 6, max = 80)
		public int ammoBallistaBoltExplosiveDamage = 15;

		@Config.Name("ammo_cannon_ball_5kg_damage")
		@Config.Comment("Cannon Ball 5kg Damage")
		@Config.RangeInt(min = 6, max = 80)
		public int ammoCannonBall5kgDamage = 10;

		@Config.Name("ammo_cannon_ball_10kg_damage")
		@Config.Comment("Cannon Ball 10kg Damage")
		@Config.RangeInt(min = 6, max = 80)
		public int ammoCannonBall10kgDamage = 15;

		@Config.Name("ammo_cannon_ball_15kg_damage")
		@Config.Comment("Cannon Ball 15kg Damage")
		@Config.RangeInt(min = 6, max = 80)
		public int ammoCannonBall15kgDamage = 30;

		@Config.Name("ammo_cannon_ball_25kg_damage")
		@Config.Comment("Cannon Ball 25kg Damage")
		@Config.RangeInt(min = 6, max = 80)
		public int ammoCannonBall25kgDamage = 45;

		@Config.Name("ammo_canister_damage")
		@Config.Comment("Canister Damage (Cannon ammo)")
		@Config.RangeInt(min = 6, max = 80)
		public int ammoCanisterDamage = 8;

		@Config.Name("ammo_hwacha_rocket_damage")
		@Config.Comment("Hwacha Rocket Damage")
		@Config.RangeInt(min = 1, max = 80)
		public int ammoHwachaRocketDamage = 6;

		@Config.Name("ammo_hwacha_rocket_flame_damage")
		@Config.Comment("Hwacha Rocket Flame Damage")
		@Config.RangeInt(min = 1, max = 80)
		public int ammoHwachaRocketFlameDamage = 5;

		@Config.Name("ammo_hwacha_rocket_explosive_damage")
		@Config.Comment("Hwacha Rocket Explosive Damage")
		@Config.RangeInt(min = 1, max = 80)
		public int ammoHwachaRocketExplosiveDamage = 4;

		@Config.Name("vehicle_ballista_health")
		@Config.Comment("Determines the health of the Ballista vehicles (mobile fixed, stand turret and stand fixed)")
		@Config.RangeInt(min = 50, max = 500)
		public int vehicleBallistaHealth = 100;

		@Config.Name("vehicle_ballista_health")
		@Config.Comment("Determines the health of the Ballista Boat vehicle")
		@Config.RangeInt(min = 50, max = 500)
		public int vehicleBallistaBoatHealth = 100;

		@Config.Name("vehicle_battering_ram_health ")
		@Config.Comment("Determines the health of the Battering Ram vehicle")
		@Config.RangeInt(min = 50, max = 500)
		public int vehicleBatteringRamHealth = 100;

		@Config.Name("vehicle_catapult_health")
		@Config.Comment("Determines the health of the Catapult vehicles (mobile fixed, stand turret and stand fixed)")
		@Config.RangeInt(min = 50, max = 500)
		public int vehicleCatapultHealth = 100;

		@Config.Name("vehicle_catapult_health")
		@Config.Comment("Determines the health of the Catapult Boat vehicle")
		@Config.RangeInt(min = 50, max = 500)
		public int vehicleCatapultBoatHealth = 100;

		@Config.Name("vehicle_cannon_health")
		@Config.Comment("Determines the health of the Cannon vehicles (mobile fixed, stand turret and stand fixed). There is no Boat Cannon in the mod.")
		@Config.RangeInt(min = 50, max = 500)
		public int vehicleCannonHealth = 100;

		@Config.Name("vehicle_chest_cart_health")
		@Config.Comment("Determines the health of the Chest Cart vehicle")
		@Config.RangeInt(min = 50, max = 500)
		public int vehicleChestCartHealth = 100;

		@Config.Name("vehicle_chest_boat_health")
		@Config.Comment("Determines the health of the Chest Boat vehicle (aka Transport Boat)")
		@Config.RangeInt(min = 50, max = 500)
		public int vehicleChestBoatHealth = 100;

		@Config.Name("vehicle_hwacha_health")
		@Config.Comment("Determines the health of the Hwacha vehicle")
		@Config.RangeInt(min = 50, max = 500)
		public int vehicleHwachaHealth = 100;

		@Config.Name("vehicle_trebuchet_health")
		@Config.Comment("Determines the health of the Trebuchet vehicles (mobile fixed, stand turret and stand fixed)")
		@Config.RangeInt(min = 50, max = 500)
		public int vehicleTrebuchetHealth = 100;

		@Config.Name("vehicle_giant_trebuchet_health")
		@Config.Comment("Determines the health of the Giant Trebuchet vehicle")
		@Config.RangeInt(min = 50, max = 500)
		public int vehicleGiantTrebuchetHealth = 175;
	}

	@Override
	public void initializeCategories() {

	}

	@Override
	public void initializeValues() {
	}

}
