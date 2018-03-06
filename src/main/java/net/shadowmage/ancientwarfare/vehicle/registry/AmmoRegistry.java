/**
 * Copyright 2012 John Cummens (aka Shadowmage, Shadowmage4513)
 * This software is distributed under the terms of the GNU General Public License.
 * Please see COPYING for precise license information.
 * <p>
 * This file is part of Ancient Warfare.
 * <p>
 * Ancient Warfare is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * Ancient Warfare is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with Ancient Warfare.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.shadowmage.ancientwarfare.vehicle.registry;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import net.shadowmage.ancientwarfare.vehicle.item.ItemAmmo;
import net.shadowmage.ancientwarfare.vehicle.missiles.AmmoArrow;
import net.shadowmage.ancientwarfare.vehicle.missiles.AmmoArrowFlame;
import net.shadowmage.ancientwarfare.vehicle.missiles.AmmoArrowIron;
import net.shadowmage.ancientwarfare.vehicle.missiles.AmmoArrowIronFlame;
import net.shadowmage.ancientwarfare.vehicle.missiles.AmmoBallShot;
import net.shadowmage.ancientwarfare.vehicle.missiles.AmmoBallistaBolt;
import net.shadowmage.ancientwarfare.vehicle.missiles.AmmoBallistaBoltExplosive;
import net.shadowmage.ancientwarfare.vehicle.missiles.AmmoBallistaBoltFlame;
import net.shadowmage.ancientwarfare.vehicle.missiles.AmmoBallistaBoltIron;
import net.shadowmage.ancientwarfare.vehicle.missiles.AmmoCanisterShot;
import net.shadowmage.ancientwarfare.vehicle.missiles.AmmoClusterShot;
import net.shadowmage.ancientwarfare.vehicle.missiles.AmmoExplosiveShot;
import net.shadowmage.ancientwarfare.vehicle.missiles.AmmoFlameShot;
import net.shadowmage.ancientwarfare.vehicle.missiles.AmmoGrapeShot;
import net.shadowmage.ancientwarfare.vehicle.missiles.AmmoHwachaRocket;
import net.shadowmage.ancientwarfare.vehicle.missiles.AmmoHwachaRocketAirburst;
import net.shadowmage.ancientwarfare.vehicle.missiles.AmmoHwachaRocketExplosive;
import net.shadowmage.ancientwarfare.vehicle.missiles.AmmoHwachaRocketFlame;
import net.shadowmage.ancientwarfare.vehicle.missiles.AmmoIronBallShot;
import net.shadowmage.ancientwarfare.vehicle.missiles.AmmoIronShot;
import net.shadowmage.ancientwarfare.vehicle.missiles.AmmoNapalmShot;
import net.shadowmage.ancientwarfare.vehicle.missiles.AmmoPebbleShot;
import net.shadowmage.ancientwarfare.vehicle.missiles.AmmoSoldierArrow;
import net.shadowmage.ancientwarfare.vehicle.missiles.AmmoStoneShot;
import net.shadowmage.ancientwarfare.vehicle.missiles.AmmoTorpedo;
import net.shadowmage.ancientwarfare.vehicle.missiles.IAmmo;

import java.util.HashMap;
import java.util.Map;

public class AmmoRegistry {

	/**
	 * procedure to make new ammo type:
	 * create ammo class
	 * create static instance below (or anywhere really)
	 * register the render in renderRegistry (or register it with renderregistry during startup)
	 * add ammo to applicable vehicle type constructors
	 */

	public static IAmmo ammoBallShot;
	public static IAmmo ammoBallIronShot;
	public static IAmmo ammoStoneShot10;
	public static IAmmo ammoStoneShot15;
	public static IAmmo ammoStoneShot30;
	public static IAmmo ammoStoneShot45;
	public static IAmmo ammoFireShot10;
	public static IAmmo ammoFireShot15;
	public static IAmmo ammoFireShot30;
	public static IAmmo ammoFireShot45;
	public static IAmmo ammoExplosive10;
	public static IAmmo ammoExplosive15;
	public static IAmmo ammoExplosive30;
	public static IAmmo ammoExplosive45;
	public static IAmmo ammoHE10;
	public static IAmmo ammoHE15;
	public static IAmmo ammoHE30;
	public static IAmmo ammoHE45;
	public static IAmmo ammoNapalm10;
	public static IAmmo ammoNapalm15;
	public static IAmmo ammoNapalm30;
	public static IAmmo ammoNapalm45;
	public static IAmmo ammoClusterShot10;
	public static IAmmo ammoClusterShot15;
	public static IAmmo ammoClusterShot30;
	public static IAmmo ammoClusterShot45;
	public static IAmmo ammoPebbleShot10;
	public static IAmmo ammoPebbleShot15;
	public static IAmmo ammoPebbleShot30;
	public static IAmmo ammoPebbleShot45;
	public static IAmmo ammoIronShot5;
	public static IAmmo ammoIronShot10;
	public static IAmmo ammoIronShot15;
	public static IAmmo ammoIronShot25;
	public static IAmmo ammoCanisterShot5;
	public static IAmmo ammoCanisterShot10;
	public static IAmmo ammoCanisterShot15;
	public static IAmmo ammoCanisterShot25;
	public static IAmmo ammoGrapeShot5;
	public static IAmmo ammoGrapeShot10;
	public static IAmmo ammoGrapeShot15;
	public static IAmmo ammoGrapeShot25;
	public static IAmmo ammoArrow;
	public static IAmmo ammoArrowFlame;
	public static IAmmo ammoArrowIron;
	public static IAmmo ammoArrowIronFlame;
	public static IAmmo ammoBallistaBolt;
	public static IAmmo ammoBallistaBoltFlame;
	public static IAmmo ammoBallistaBoltExplosive;
	public static IAmmo ammoBallistaBoltIron;
	public static IAmmo ammoRocket;
	public static IAmmo ammoHwachaRocketFlame;
	public static IAmmo ammoHwachaRocketExplosive;
	public static IAmmo ammoHwachaRocketAirburst;
	public static IAmmo ammoSoldierArrowWood;
	public static IAmmo ammoSoldierArrowIron;
	public static IAmmo ammoSoldierArrowWoodFlame;
	public static IAmmo ammoSoldierArrowIronFlame;
	public static IAmmo ammoTorpedo10;
	public static IAmmo ammoTorpedo15;
	public static IAmmo ammoTorpedo30;
	public static IAmmo ammoTorpedo45;


	private AmmoRegistry() {
	}

	private static Map<ResourceLocation, IAmmo> ammoInstances = new HashMap<>();
	private static Map<ResourceLocation, ItemAmmo> ammoItemInstances = new HashMap<>();

	public static void registerAmmo(IForgeRegistry<Item> registry) {

		ammoBallShot = registerAmmoType(new AmmoBallShot(), registry);
		ammoBallIronShot = registerAmmoType(new AmmoIronBallShot(), registry);
		ammoStoneShot10 = registerAmmoType(new AmmoStoneShot(10), registry);
		ammoStoneShot15 = registerAmmoType(new AmmoStoneShot(15), registry);
		ammoStoneShot30 = registerAmmoType(new AmmoStoneShot(30), registry);
		ammoStoneShot45 = registerAmmoType(new AmmoStoneShot(45), registry);
		ammoFireShot10 = registerAmmoType(new AmmoFlameShot(10), registry);
		ammoFireShot15 = registerAmmoType(new AmmoFlameShot(15), registry);
		ammoFireShot30 = registerAmmoType(new AmmoFlameShot(30), registry);
		ammoFireShot45 = registerAmmoType(new AmmoFlameShot(45), registry);
		ammoExplosive10 = registerAmmoType(new AmmoExplosiveShot(10, false), registry);
		ammoExplosive15 = registerAmmoType(new AmmoExplosiveShot(15, false), registry);
		ammoExplosive30 = registerAmmoType(new AmmoExplosiveShot(30, false), registry);
		ammoExplosive45 = registerAmmoType(new AmmoExplosiveShot(45, false), registry);
		ammoHE10 = registerAmmoType(new AmmoExplosiveShot(10, true), registry);
		ammoHE15 = registerAmmoType(new AmmoExplosiveShot(15, true), registry);
		ammoHE30 = registerAmmoType(new AmmoExplosiveShot(30, true), registry);
		ammoHE45 = registerAmmoType(new AmmoExplosiveShot(45, true), registry);
		ammoNapalm10 = registerAmmoType(new AmmoNapalmShot(10), registry);
		ammoNapalm15 = registerAmmoType(new AmmoNapalmShot(15), registry);
		ammoNapalm30 = registerAmmoType(new AmmoNapalmShot(30), registry);
		ammoNapalm45 = registerAmmoType(new AmmoNapalmShot(45), registry);
		ammoClusterShot10 = registerAmmoType(new AmmoClusterShot(10), registry);
		ammoClusterShot15 = registerAmmoType(new AmmoClusterShot(15), registry);
		ammoClusterShot30 = registerAmmoType(new AmmoClusterShot(30), registry);
		ammoClusterShot45 = registerAmmoType(new AmmoClusterShot(45), registry);
		ammoPebbleShot10 = registerAmmoType(new AmmoPebbleShot(10), registry);
		ammoPebbleShot15 = registerAmmoType(new AmmoPebbleShot(15), registry);
		ammoPebbleShot30 = registerAmmoType(new AmmoPebbleShot(30), registry);
		ammoPebbleShot45 = registerAmmoType(new AmmoPebbleShot(45), registry);
		ammoIronShot5 = registerAmmoType(new AmmoIronShot(5, 10), registry);
		ammoIronShot10 = registerAmmoType(new AmmoIronShot(10, 15), registry);
		ammoIronShot15 = registerAmmoType(new AmmoIronShot(15, 30), registry);
		ammoIronShot25 = registerAmmoType(new AmmoIronShot(25, 45), registry);
		ammoCanisterShot5 = registerAmmoType(new AmmoCanisterShot(5), registry);
		ammoCanisterShot10 = registerAmmoType(new AmmoCanisterShot(10), registry);
		ammoCanisterShot15 = registerAmmoType(new AmmoCanisterShot(15), registry);
		ammoCanisterShot25 = registerAmmoType(new AmmoCanisterShot(25), registry);
		ammoGrapeShot5 = registerAmmoType(new AmmoGrapeShot(5), registry);
		ammoGrapeShot10 = registerAmmoType(new AmmoGrapeShot(10), registry);
		ammoGrapeShot15 = registerAmmoType(new AmmoGrapeShot(15), registry);
		ammoGrapeShot25 = registerAmmoType(new AmmoGrapeShot(25), registry);
		ammoArrow = registerAmmoType(new AmmoArrow(), registry);
		ammoArrowFlame = registerAmmoType(new AmmoArrowFlame(), registry);
		ammoArrowIron = registerAmmoType(new AmmoArrowIron(), registry);
		ammoArrowIronFlame = registerAmmoType(new AmmoArrowIronFlame(), registry);
		ammoBallistaBolt = registerAmmoType(new AmmoBallistaBolt(), registry);
		ammoBallistaBoltFlame = registerAmmoType(new AmmoBallistaBoltFlame(), registry);
		ammoBallistaBoltExplosive = registerAmmoType(new AmmoBallistaBoltExplosive(), registry);
		ammoBallistaBoltIron = registerAmmoType(new AmmoBallistaBoltIron(), registry);
		ammoRocket = registerAmmoType(new AmmoHwachaRocket(), registry);
		ammoHwachaRocketFlame = registerAmmoType(new AmmoHwachaRocketFlame(), registry);
		ammoHwachaRocketExplosive = registerAmmoType(new AmmoHwachaRocketExplosive(), registry);
		ammoHwachaRocketAirburst = registerAmmoType(new AmmoHwachaRocketAirburst(), registry);
		ammoSoldierArrowWood = registerAmmoType(new AmmoSoldierArrow(5, false), registry);
		ammoSoldierArrowIron = registerAmmoType(new AmmoSoldierArrow(7, false), registry);
		ammoSoldierArrowWoodFlame = registerAmmoType(new AmmoSoldierArrow(5, true), registry);
		ammoSoldierArrowIronFlame = registerAmmoType(new AmmoSoldierArrow(7, true), registry);
		ammoTorpedo10 = registerAmmoType(new AmmoTorpedo(10), registry);
		ammoTorpedo15 = registerAmmoType(new AmmoTorpedo(15), registry);
		ammoTorpedo30 = registerAmmoType(new AmmoTorpedo(30), registry);
		ammoTorpedo45 = registerAmmoType(new AmmoTorpedo(45), registry);
	}

	public static IAmmo registerAmmoType(IAmmo ammo, IForgeRegistry<Item> registry) {
		ammoInstances.put(ammo.getRegistryName(), ammo);
		ItemAmmo item = new ItemAmmo(ammo.getRegistryName());
		ammoItemInstances.put(ammo.getRegistryName(), item);
		registry.register(item);
		return ammo;
	}

	public static IAmmo getAmmoForStack(ItemStack stack) {
		return ammoInstances.get(stack.getItem().getRegistryName());
	}

	public static IAmmo getAmmo(ResourceLocation registryName) {
		return ammoInstances.get(registryName);
	}

	public static ItemAmmo getItemForAmmo(IAmmo ammo) {
		return ammoItemInstances.get(ammo.getRegistryName());
	}
}
