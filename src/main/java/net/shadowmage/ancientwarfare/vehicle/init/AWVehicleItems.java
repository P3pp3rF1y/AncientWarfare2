package net.shadowmage.ancientwarfare.vehicle.init;

import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;
import net.shadowmage.ancientwarfare.core.item.ItemBase;
import net.shadowmage.ancientwarfare.core.util.InjectionTools;
import net.shadowmage.ancientwarfare.vehicle.AncientWarfareVehicles;
import net.shadowmage.ancientwarfare.vehicle.item.ItemMisc;
import net.shadowmage.ancientwarfare.vehicle.item.ItemSpawner;
import net.shadowmage.ancientwarfare.vehicle.registry.AmmoRegistry;
import net.shadowmage.ancientwarfare.vehicle.registry.ArmorRegistry;
import net.shadowmage.ancientwarfare.vehicle.registry.UpgradeRegistry;
import net.shadowmage.ancientwarfare.vehicle.registry.VehicleRegistry;

import static net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;

@ObjectHolder(AncientWarfareVehicles.MOD_ID)
@Mod.EventBusSubscriber(modid = AncientWarfareVehicles.MOD_ID)
public class AWVehicleItems {
	private AWVehicleItems() {}

	public static final ItemBase SPAWNER = InjectionTools.nullValue();

	@SubscribeEvent
	public static void register(RegistryEvent.Register<Item> event) {
		IForgeRegistry<Item> registry = event.getRegistry();

		registry.register(new ItemSpawner());
		//TODO do we really need items that are duplicates of ammo just for crafting?
		registry.register(new ItemMisc("flame_charge", ItemMisc.VehicleItemType.AMMO_MATERIAL));
		registry.register(new ItemMisc("explosive_charge", ItemMisc.VehicleItemType.AMMO_MATERIAL));
		registry.register(new ItemMisc("rocket_charge", ItemMisc.VehicleItemType.AMMO_MATERIAL));
		registry.register(new ItemMisc("cluster_charge", ItemMisc.VehicleItemType.AMMO_MATERIAL));
		registry.register(new ItemMisc("napalm_charge", ItemMisc.VehicleItemType.AMMO_MATERIAL));
		registry.register(new ItemMisc("clay_casing", ItemMisc.VehicleItemType.AMMO_MATERIAL));
		registry.register(new ItemMisc("iron_casing", ItemMisc.VehicleItemType.AMMO_MATERIAL));
		registry.register(new ItemMisc("mobility_unit", ItemMisc.VehicleItemType.VEHICLE_COMPONENT));
		registry.register(new ItemMisc("turret_components", ItemMisc.VehicleItemType.VEHICLE_COMPONENT));
		registry.register(new ItemMisc("torsion_unit", ItemMisc.VehicleItemType.VEHICLE_COMPONENT));
		registry.register(new ItemMisc("counter_weight_unit", ItemMisc.VehicleItemType.VEHICLE_COMPONENT));
		registry.register(new ItemMisc("powder_case", ItemMisc.VehicleItemType.VEHICLE_COMPONENT));
		registry.register(new ItemMisc("equipment_bay", ItemMisc.VehicleItemType.VEHICLE_COMPONENT));
		registry.register(new ItemMisc("rough_wood", ItemMisc.VehicleItemType.VEHICLE_COMPONENT));
		registry.register(new ItemMisc("treated_wood", ItemMisc.VehicleItemType.VEHICLE_COMPONENT));
		registry.register(new ItemMisc("ironshod_wood", ItemMisc.VehicleItemType.VEHICLE_COMPONENT));
		registry.register(new ItemMisc("iron_core_wood", ItemMisc.VehicleItemType.VEHICLE_COMPONENT));
		registry.register(new ItemMisc("rough_iron", ItemMisc.VehicleItemType.VEHICLE_COMPONENT));
		registry.register(new ItemMisc("fine_iron", ItemMisc.VehicleItemType.VEHICLE_COMPONENT));
		registry.register(new ItemMisc("tempered_iron", ItemMisc.VehicleItemType.VEHICLE_COMPONENT));
		registry.register(new ItemMisc("minor_alloy", ItemMisc.VehicleItemType.VEHICLE_COMPONENT));
		registry.register(new ItemMisc("major_alloy", ItemMisc.VehicleItemType.VEHICLE_COMPONENT));

		AmmoRegistry.registerAmmo(registry);
		ArmorRegistry.registerArmorTypes(registry);
		UpgradeRegistry.registerUpgrades(registry);
		VehicleRegistry.registerVehicles();
	}
}
