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

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.shadowmage.ancientwarfare.vehicle.entity.VehicleBase;
import net.shadowmage.ancientwarfare.vehicle.missiles.MissileBase;
import net.shadowmage.ancientwarfare.vehicle.render.missile.RenderArrow;
import net.shadowmage.ancientwarfare.vehicle.render.missile.RenderShot;
import net.shadowmage.ancientwarfare.vehicle.render.vehicle.RenderCatapultStandFixed;
import net.shadowmage.ancientwarfare.vehicle.render.vehicle.RenderVehicleBase;

import java.util.HashMap;

/**
 * handle render information
 *
 * @author Shadowmage
 */
public class RenderRegistry {

	private RenderRegistry() {
	}

	private static RenderRegistry INSTANCE;

	public static RenderRegistry instance() {
		if (INSTANCE == null) {
			INSTANCE = new RenderRegistry();
		}
		return INSTANCE;
	}

	/**
	 * dummy render to be used in case a vehicle render doesn't exist...
	 */
	private RenderCatapultStandFixed dummyRender = new RenderCatapultStandFixed();
	private RenderShot dummyMissileRender = new RenderShot();

	private RenderArrow arrowRender = new RenderArrow();
	private RenderShot shotRender = new RenderShot();

	private HashMap<Integer, Render> missileRenders = new HashMap<>();
	private HashMap<Integer, Render> gateRenders = new HashMap<>();

	private HashMap<Integer, ModelTEBase> teModels = new HashMap<Integer, ModelTEBase>();
	private HashMap<Integer, ResourceLocation> teModelTextures = new HashMap<>();

	public void loadRenders() {
		/**
		 * vehicles..
		 */
		RenderingRegistry.registerEntityRenderingHandler(VehicleBase.class, RenderVehicleHelper.instance());
		/**
		 * missiles...
		 */
		RenderingRegistry.registerEntityRenderingHandler(MissileBase.class, new RenderMissileHelper());
		this.addMissileRender(AmmoRegistry.ammoStoneShot10.getAmmoType(), shotRender);
		this.addMissileRender(AmmoRegistry.ammoStoneShot15.getAmmoType(), shotRender);
		this.addMissileRender(AmmoRegistry.ammoStoneShot30.getAmmoType(), shotRender);
		this.addMissileRender(AmmoRegistry.ammoStoneShot45.getAmmoType(), shotRender);
		this.addMissileRender(AmmoRegistry.ammoFireShot10.getAmmoType(), shotRender);
		this.addMissileRender(AmmoRegistry.ammoFireShot15.getAmmoType(), shotRender);
		this.addMissileRender(AmmoRegistry.ammoFireShot30.getAmmoType(), shotRender);
		this.addMissileRender(AmmoRegistry.ammoFireShot45.getAmmoType(), shotRender);
		this.addMissileRender(AmmoRegistry.ammoClusterShot10.getAmmoType(), shotRender);
		this.addMissileRender(AmmoRegistry.ammoClusterShot15.getAmmoType(), shotRender);
		this.addMissileRender(AmmoRegistry.ammoClusterShot30.getAmmoType(), shotRender);
		this.addMissileRender(AmmoRegistry.ammoClusterShot45.getAmmoType(), shotRender);
		this.addMissileRender(AmmoRegistry.ammoPebbleShot10.getAmmoType(), shotRender);
		this.addMissileRender(AmmoRegistry.ammoPebbleShot15.getAmmoType(), shotRender);
		this.addMissileRender(AmmoRegistry.ammoPebbleShot30.getAmmoType(), shotRender);
		this.addMissileRender(AmmoRegistry.ammoPebbleShot45.getAmmoType(), shotRender);
		this.addMissileRender(AmmoRegistry.ammoNapalm10.getAmmoType(), shotRender);
		this.addMissileRender(AmmoRegistry.ammoNapalm15.getAmmoType(), shotRender);
		this.addMissileRender(AmmoRegistry.ammoNapalm30.getAmmoType(), shotRender);
		this.addMissileRender(AmmoRegistry.ammoNapalm45.getAmmoType(), shotRender);
		this.addMissileRender(AmmoRegistry.ammoExplosive10.getAmmoType(), shotRender);
		this.addMissileRender(AmmoRegistry.ammoExplosive15.getAmmoType(), shotRender);
		this.addMissileRender(AmmoRegistry.ammoExplosive30.getAmmoType(), shotRender);
		this.addMissileRender(AmmoRegistry.ammoExplosive45.getAmmoType(), shotRender);
		this.addMissileRender(AmmoRegistry.ammoHE10.getAmmoType(), shotRender);
		this.addMissileRender(AmmoRegistry.ammoHE15.getAmmoType(), shotRender);
		this.addMissileRender(AmmoRegistry.ammoHE30.getAmmoType(), shotRender);
		this.addMissileRender(AmmoRegistry.ammoHE45.getAmmoType(), shotRender);

		this.addMissileRender(AmmoRegistry.ammoIronShot5.getAmmoType(), shotRender);
		this.addMissileRender(AmmoRegistry.ammoIronShot10.getAmmoType(), shotRender);
		this.addMissileRender(AmmoRegistry.ammoIronShot15.getAmmoType(), shotRender);
		this.addMissileRender(AmmoRegistry.ammoIronShot25.getAmmoType(), shotRender);
		this.addMissileRender(AmmoRegistry.ammoGrapeShot5.getAmmoType(), shotRender);
		this.addMissileRender(AmmoRegistry.ammoGrapeShot10.getAmmoType(), shotRender);
		this.addMissileRender(AmmoRegistry.ammoGrapeShot15.getAmmoType(), shotRender);
		this.addMissileRender(AmmoRegistry.ammoGrapeShot25.getAmmoType(), shotRender);
		this.addMissileRender(AmmoRegistry.ammoCanisterShot5.getAmmoType(), shotRender);
		this.addMissileRender(AmmoRegistry.ammoCanisterShot10.getAmmoType(), shotRender);
		this.addMissileRender(AmmoRegistry.ammoCanisterShot15.getAmmoType(), shotRender);
		this.addMissileRender(AmmoRegistry.ammoCanisterShot25.getAmmoType(), shotRender);

		this.addMissileRender(AmmoRegistry.ammoArrow.getAmmoType(), arrowRender);
		this.addMissileRender(AmmoRegistry.ammoArrowIron.getAmmoType(), arrowRender);
		this.addMissileRender(AmmoRegistry.ammoArrowFlame.getAmmoType(), arrowRender);
		this.addMissileRender(AmmoRegistry.ammoArrowIronFlame.getAmmoType(), arrowRender);
		this.addMissileRender(AmmoRegistry.ammoRocket.getAmmoType(), arrowRender);
		this.addMissileRender(AmmoRegistry.ammoHwachaRocketFlame.getAmmoType(), arrowRender);
		this.addMissileRender(AmmoRegistry.ammoHwachaRocketExplosive.getAmmoType(), arrowRender);
		this.addMissileRender(AmmoRegistry.ammoHwachaRocketAirburst.getAmmoType(), arrowRender);
		this.addMissileRender(AmmoRegistry.ammoBallistaBolt.getAmmoType(), arrowRender);
		this.addMissileRender(AmmoRegistry.ammoBallistaBoltExplosive.getAmmoType(), arrowRender);
		this.addMissileRender(AmmoRegistry.ammoBallistaBoltFlame.getAmmoType(), arrowRender);
		this.addMissileRender(AmmoRegistry.ammoBallistaBoltIron.getAmmoType(), arrowRender);

		this.addMissileRender(AmmoRegistry.ammoBallShot.getAmmoType(), shotRender);
		this.addMissileRender(AmmoRegistry.ammoBallIronShot.getAmmoType(), shotRender);

		this.addMissileRender(AmmoRegistry.ammoSoldierArrowWood.getAmmoType(), arrowRender);
		this.addMissileRender(AmmoRegistry.ammoSoldierArrowIron.getAmmoType(), arrowRender);
		this.addMissileRender(AmmoRegistry.ammoSoldierArrowWoodFlame.getAmmoType(), arrowRender);
		this.addMissileRender(AmmoRegistry.ammoSoldierArrowIronFlame.getAmmoType(), arrowRender);

		this.addMissileRender(AmmoRegistry.ammoTorpedo10.getAmmoType(), shotRender);
		this.addMissileRender(AmmoRegistry.ammoTorpedo15.getAmmoType(), shotRender);
		this.addMissileRender(AmmoRegistry.ammoTorpedo30.getAmmoType(), shotRender);
		this.addMissileRender(AmmoRegistry.ammoTorpedo45.getAmmoType(), shotRender);

	}

	public void addMissileRender(int type, Render rend) {
		this.missileRenders.put(type, rend);
	}

	public Render getRenderForMissile(int type) {
		if (!this.missileRenders.containsKey(type)) {
			return dummyMissileRender;
		}
		return this.missileRenders.get(type);
	}

	public static RenderVehicleBase getRenderForVehicle(int type) {
		if (!this.vehicleRenders.containsKey(type)) {
			return dummyRender;
		}
		return this.vehicleRenders.get(type);
	}

	public Render getGateRender(int type) {
		return this.gateRenders.get(type);
	}

	public void addGateRender(int type, Render rend) {
		this.gateRenders.put(type, rend);
	}

}
