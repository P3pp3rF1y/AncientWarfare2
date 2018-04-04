package net.shadowmage.ancientwarfare.vehicle.item;

import net.shadowmage.ancientwarfare.core.item.ItemBase;
import net.shadowmage.ancientwarfare.vehicle.AncientWarfareVehicles;

import static net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;

@ObjectHolder(AncientWarfareVehicles.modID)
public class AWVehicleItems {
	//TODO review which ones of these are actually needed, likely only spawner
	@ObjectHolder("spawner")
	public static ItemBase spawner;
	@ObjectHolder("flame_charge")
	public static ItemBase flameCharge;
	@ObjectHolder("explosive_charge")
	public static ItemBase explosiveCharge;
	@ObjectHolder("rocket_charge")
	public static ItemBase rocketCharge;
	@ObjectHolder("cluster_charge")
	public static ItemBase clusterCharge;
	@ObjectHolder("napalm_charge")
	public static ItemBase napalmCharge;
	@ObjectHolder("clay_casing")
	public static ItemBase clayCasing;
	@ObjectHolder("iron_casing")
	public static ItemBase ironCasing;
	@ObjectHolder("mobility_unit")
	public static ItemBase mobilityUnit;
	@ObjectHolder("turret_components")
	public static ItemBase turretComponents;
	@ObjectHolder("torsion_unit")
	public static ItemBase torsionUnit;
	@ObjectHolder("counter_weight_unit")
	public static ItemBase counterWeightUnit;
	@ObjectHolder("powder_case")
	public static ItemBase powderCase;
	@ObjectHolder("equipment_bay")
	public static ItemBase equipmentBay;
}
