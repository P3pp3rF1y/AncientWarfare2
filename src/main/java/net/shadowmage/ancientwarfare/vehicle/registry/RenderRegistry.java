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

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.MinecraftForgeClient;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.vehicle.entity.IVehicleType;
import net.shadowmage.ancientwarfare.vehicle.entity.VehicleBase;
import net.shadowmage.ancientwarfare.vehicle.missiles.Ammo;
import net.shadowmage.ancientwarfare.vehicle.missiles.MissileBase;
import net.shadowmage.ancientwarfare.vehicle.model.ModelBallistaMobile;
import net.shadowmage.ancientwarfare.vehicle.model.ModelBallistaStand;
import net.shadowmage.ancientwarfare.vehicle.model.ModelBatteringRam;
import net.shadowmage.ancientwarfare.vehicle.model.ModelBoatBallista;
import net.shadowmage.ancientwarfare.vehicle.model.ModelBoatCatapult;
import net.shadowmage.ancientwarfare.vehicle.model.ModelBoatTransport;
import net.shadowmage.ancientwarfare.vehicle.model.ModelCannonMobileFixed;
import net.shadowmage.ancientwarfare.vehicle.model.ModelCannonStandFixed;
import net.shadowmage.ancientwarfare.vehicle.model.ModelCannonStandTurret;
import net.shadowmage.ancientwarfare.vehicle.model.ModelCatapultMobileFixed;
import net.shadowmage.ancientwarfare.vehicle.model.ModelCatapultMobileTurret;
import net.shadowmage.ancientwarfare.vehicle.model.ModelCatapultStandFixed;
import net.shadowmage.ancientwarfare.vehicle.model.ModelCatapultStandTurret;
import net.shadowmage.ancientwarfare.vehicle.model.ModelChestCart;
import net.shadowmage.ancientwarfare.vehicle.model.ModelHelicopter;
import net.shadowmage.ancientwarfare.vehicle.model.ModelHwacha;
import net.shadowmage.ancientwarfare.vehicle.model.ModelSubmarine;
import net.shadowmage.ancientwarfare.vehicle.model.ModelTrebuchetMobileFixed;
import net.shadowmage.ancientwarfare.vehicle.model.ModelTrebuchetStandFixed;
import net.shadowmage.ancientwarfare.vehicle.model.ModelTrebuchetStandTurret;
import net.shadowmage.ancientwarfare.vehicle.render.RenderMissileHelper;
import net.shadowmage.ancientwarfare.vehicle.render.RenderVehicleBase;
import net.shadowmage.ancientwarfare.vehicle.render.RenderVehicleHelper;
import net.shadowmage.ancientwarfare.vehicle.render.missile.RenderArrow;
import net.shadowmage.ancientwarfare.vehicle.render.missile.RenderShot;
import net.shadowmage.ancientwarfare.vehicle.render.vehicle.RenderAircraft;
import net.shadowmage.ancientwarfare.vehicle.render.vehicle.RenderBallistaMobile;
import net.shadowmage.ancientwarfare.vehicle.render.vehicle.RenderBallistaStand;
import net.shadowmage.ancientwarfare.vehicle.render.vehicle.RenderBatteringRam;
import net.shadowmage.ancientwarfare.vehicle.render.vehicle.RenderBoatBallista;
import net.shadowmage.ancientwarfare.vehicle.render.vehicle.RenderBoatCatapult;
import net.shadowmage.ancientwarfare.vehicle.render.vehicle.RenderBoatTransport;
import net.shadowmage.ancientwarfare.vehicle.render.vehicle.RenderCannonMobileFixed;
import net.shadowmage.ancientwarfare.vehicle.render.vehicle.RenderCannonStandFixed;
import net.shadowmage.ancientwarfare.vehicle.render.vehicle.RenderCannonStandTurret;
import net.shadowmage.ancientwarfare.vehicle.render.vehicle.RenderCatapultMobileFixed;
import net.shadowmage.ancientwarfare.vehicle.render.vehicle.RenderCatapultMobileTurret;
import net.shadowmage.ancientwarfare.vehicle.render.vehicle.RenderCatapultStandFixed;
import net.shadowmage.ancientwarfare.vehicle.render.vehicle.RenderCatapultStandTurret;
import net.shadowmage.ancientwarfare.vehicle.render.vehicle.RenderChestCart;
import net.shadowmage.ancientwarfare.vehicle.render.vehicle.RenderHelicopter;
import net.shadowmage.ancientwarfare.vehicle.render.vehicle.RenderHwacha;
import net.shadowmage.ancientwarfare.vehicle.render.vehicle.RenderSubmarine;
import net.shadowmage.ancientwarfare.vehicle.render.vehicle.RenderTrebuchetLarge;
import net.shadowmage.ancientwarfare.vehicle.render.vehicle.RenderTrebuchetMobileFixed;
import net.shadowmage.ancientwarfare.vehicle.render.vehicle.RenderTrebuchetStandFixed;
import net.shadowmage.ancientwarfare.vehicle.render.vehicle.RenderTrebuchetStandTurret;
import shadowmage.ancient_warfare.client.model.ModelAirplane;
import shadowmage.ancient_warfare.client.model.ModelTEBase;
import shadowmage.ancient_warfare.client.model.ModelTable1;
import shadowmage.ancient_warfare.client.model.ModelTable2;
import shadowmage.ancient_warfare.client.model.ModelTable3;
import shadowmage.ancient_warfare.client.model.ModelTable4;
import shadowmage.ancient_warfare.client.model.ModelTable5;
import shadowmage.ancient_warfare.client.model.ModelTable6;
import shadowmage.ancient_warfare.client.model.ModelTable7;
import shadowmage.ancient_warfare.client.model.ModelTable8;
import shadowmage.ancient_warfare.client.model.ModelVehicleBase;
import shadowmage.ancient_warfare.client.render.RenderCraftingHelper;
import shadowmage.ancient_warfare.client.render.RenderGateHelper;
import shadowmage.ancient_warfare.client.render.RenderNpcHelper;
import shadowmage.ancient_warfare.client.render.civic.CivicItemRenderer;
import shadowmage.ancient_warfare.client.render.gate.RenderGateBasic;
import shadowmage.ancient_warfare.client.render.gate.RenderGateDouble;
import shadowmage.ancient_warfare.client.render.gate.RenderGateRotatingBridge;
import shadowmage.ancient_warfare.client.render.gate.RenderGateSingle;
import shadowmage.ancient_warfare.client.render.machine.RenderTEMotor;
import shadowmage.ancient_warfare.common.block.BlockLoader;
import shadowmage.ancient_warfare.common.crafting.TEAWCrafting;
import shadowmage.ancient_warfare.common.gates.EntityGate;
import shadowmage.ancient_warfare.common.item.ItemLoader;
import shadowmage.ancient_warfare.common.npcs.NpcBase;
import shadowmage.ancient_warfare.common.plugins.PluginProxy;

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

	private HashMap<Integer, Render> missileRenders = new HashMap<Integer, Render>();
	private HashMap<Integer, RenderVehicleBase> vehicleRenders = new HashMap<Integer, RenderVehicleBase>();
	private HashMap<Integer, ModelVehicleBase> vehicleModels = new HashMap<Integer, ModelVehicleBase>();

	private HashMap<Integer, Render> gateRenders = new HashMap<Integer, Render>();

	private HashMap<Integer, ModelTEBase> teModels = new HashMap<Integer, ModelTEBase>();
	private HashMap<Integer, ResourceLocation> teModelTextures = new HashMap<>();

	public void loadRenders() {
		/**
		 * vehicles..
		 */
		RenderingRegistry.registerEntityRenderingHandler(VehicleBase.class, RenderVehicleHelper.instance());
		this.addVehicleRender(VehicleRegistry.CATAPULT_STAND_FIXED, new RenderCatapultStandFixed(), new ModelCatapultStandFixed());
		this.addVehicleRender(VehicleRegistry.CATAPULT_STAND_TURRET, new RenderCatapultStandTurret(), new ModelCatapultStandTurret());
		this.addVehicleRender(VehicleRegistry.CATAPULT_MOBILE_FIXED, new RenderCatapultMobileFixed(), new ModelCatapultMobileFixed());
		this.addVehicleRender(VehicleRegistry.CATAPULT_MOBILE_TURRET, new RenderCatapultMobileTurret(), new ModelCatapultMobileTurret());
		this.addVehicleRender(VehicleRegistry.BALLISTA_STAND_FIXED, new RenderBallistaStand(), new ModelBallistaStand());
		this.addVehicleRender(VehicleRegistry.BALLISTA_STAND_TURRET, new RenderBallistaStand(), new ModelBallistaStand());
		this.addVehicleRender(VehicleRegistry.BALLISTA_MOBILE_FIXED, new RenderBallistaMobile(), new ModelBallistaMobile());
		this.addVehicleRender(VehicleRegistry.BALLISTA_MOBILE_TURRET, new RenderBallistaMobile(), new ModelBallistaMobile());
		this.addVehicleRender(VehicleRegistry.BATTERING_RAM, new RenderBatteringRam(), new ModelBatteringRam());
		this.addVehicleRender(VehicleRegistry.CANNON_STAND_FIXED, new RenderCannonStandFixed(), new ModelCannonStandFixed());
		this.addVehicleRender(VehicleRegistry.CANNON_STAND_TURRET, new RenderCannonStandTurret(), new ModelCannonStandTurret());
		this.addVehicleRender(VehicleRegistry.CANNON_MOBILE_FIXED, new RenderCannonMobileFixed(), new ModelCannonMobileFixed());
		this.addVehicleRender(VehicleRegistry.HWACHA, new RenderHwacha(), new ModelHwacha());
		this.addVehicleRender(VehicleRegistry.TREBUCHET_STAND_FIXED, new RenderTrebuchetStandFixed(), new ModelTrebuchetStandFixed());
		this.addVehicleRender(VehicleRegistry.TREBUCHET_STAND_TURRET, new RenderTrebuchetStandTurret(), new ModelTrebuchetStandTurret());
		this.addVehicleRender(VehicleRegistry.TREBUCHET_MOBILE_FIXED, new RenderTrebuchetMobileFixed(), new ModelTrebuchetMobileFixed());
		this.addVehicleRender(VehicleRegistry.TREBUCHET_LARGE, new RenderTrebuchetLarge(), new ModelTrebuchetStandFixed());
		this.addVehicleRender(VehicleRegistry.CHEST_CART, new RenderChestCart(), new ModelChestCart());
		this.addVehicleRender(VehicleRegistry.BOAT_BALLISTA, new RenderBoatBallista(), new ModelBoatBallista());
		this.addVehicleRender(VehicleRegistry.BOAT_CATAPULT, new RenderBoatCatapult(), new ModelBoatCatapult());
		this.addVehicleRender(VehicleRegistry.BOAT_TRANSPORT, new RenderBoatTransport(), new ModelBoatTransport());
		this.addVehicleRender(VehicleRegistry.AIR_BOMBER, new RenderAircraft(), new ModelAirplane());
		this.addVehicleRender(VehicleRegistry.AIR_FIGHTER, new RenderAircraft(), new ModelAirplane());
		this.addVehicleRender(VehicleRegistry.AIR_HELICOPTER, new RenderHelicopter(), new ModelHelicopter());
		this.addVehicleRender(VehicleRegistry.SUBMARINE_TEST, new RenderSubmarine(), new ModelSubmarine());
		/**
		 * missiles...
		 */
		RenderingRegistry.registerEntityRenderingHandler(MissileBase.class, new RenderMissileHelper());
		this.addMissileRender(Ammo.ammoStoneShot10.getAmmoType(), shotRender);
		this.addMissileRender(Ammo.ammoStoneShot15.getAmmoType(), shotRender);
		this.addMissileRender(Ammo.ammoStoneShot30.getAmmoType(), shotRender);
		this.addMissileRender(Ammo.ammoStoneShot45.getAmmoType(), shotRender);
		this.addMissileRender(Ammo.ammoFireShot10.getAmmoType(), shotRender);
		this.addMissileRender(Ammo.ammoFireShot15.getAmmoType(), shotRender);
		this.addMissileRender(Ammo.ammoFireShot30.getAmmoType(), shotRender);
		this.addMissileRender(Ammo.ammoFireShot45.getAmmoType(), shotRender);
		this.addMissileRender(Ammo.ammoClusterShot10.getAmmoType(), shotRender);
		this.addMissileRender(Ammo.ammoClusterShot15.getAmmoType(), shotRender);
		this.addMissileRender(Ammo.ammoClusterShot30.getAmmoType(), shotRender);
		this.addMissileRender(Ammo.ammoClusterShot45.getAmmoType(), shotRender);
		this.addMissileRender(Ammo.ammoPebbleShot10.getAmmoType(), shotRender);
		this.addMissileRender(Ammo.ammoPebbleShot15.getAmmoType(), shotRender);
		this.addMissileRender(Ammo.ammoPebbleShot30.getAmmoType(), shotRender);
		this.addMissileRender(Ammo.ammoPebbleShot45.getAmmoType(), shotRender);
		this.addMissileRender(Ammo.ammoNapalm10.getAmmoType(), shotRender);
		this.addMissileRender(Ammo.ammoNapalm15.getAmmoType(), shotRender);
		this.addMissileRender(Ammo.ammoNapalm30.getAmmoType(), shotRender);
		this.addMissileRender(Ammo.ammoNapalm45.getAmmoType(), shotRender);
		this.addMissileRender(Ammo.ammoExplosive10.getAmmoType(), shotRender);
		this.addMissileRender(Ammo.ammoExplosive15.getAmmoType(), shotRender);
		this.addMissileRender(Ammo.ammoExplosive30.getAmmoType(), shotRender);
		this.addMissileRender(Ammo.ammoExplosive45.getAmmoType(), shotRender);
		this.addMissileRender(Ammo.ammoHE10.getAmmoType(), shotRender);
		this.addMissileRender(Ammo.ammoHE15.getAmmoType(), shotRender);
		this.addMissileRender(Ammo.ammoHE30.getAmmoType(), shotRender);
		this.addMissileRender(Ammo.ammoHE45.getAmmoType(), shotRender);

		this.addMissileRender(Ammo.ammoIronShot5.getAmmoType(), shotRender);
		this.addMissileRender(Ammo.ammoIronShot10.getAmmoType(), shotRender);
		this.addMissileRender(Ammo.ammoIronShot15.getAmmoType(), shotRender);
		this.addMissileRender(Ammo.ammoIronShot25.getAmmoType(), shotRender);
		this.addMissileRender(Ammo.ammoGrapeShot5.getAmmoType(), shotRender);
		this.addMissileRender(Ammo.ammoGrapeShot10.getAmmoType(), shotRender);
		this.addMissileRender(Ammo.ammoGrapeShot15.getAmmoType(), shotRender);
		this.addMissileRender(Ammo.ammoGrapeShot25.getAmmoType(), shotRender);
		this.addMissileRender(Ammo.ammoCanisterShot5.getAmmoType(), shotRender);
		this.addMissileRender(Ammo.ammoCanisterShot10.getAmmoType(), shotRender);
		this.addMissileRender(Ammo.ammoCanisterShot15.getAmmoType(), shotRender);
		this.addMissileRender(Ammo.ammoCanisterShot25.getAmmoType(), shotRender);

		this.addMissileRender(Ammo.ammoArrow.getAmmoType(), arrowRender);
		this.addMissileRender(Ammo.ammoArrowIron.getAmmoType(), arrowRender);
		this.addMissileRender(Ammo.ammoArrowFlame.getAmmoType(), arrowRender);
		this.addMissileRender(Ammo.ammoArrowIronFlame.getAmmoType(), arrowRender);
		this.addMissileRender(Ammo.ammoRocket.getAmmoType(), arrowRender);
		this.addMissileRender(Ammo.ammoHwachaRocketFlame.getAmmoType(), arrowRender);
		this.addMissileRender(Ammo.ammoHwachaRocketExplosive.getAmmoType(), arrowRender);
		this.addMissileRender(Ammo.ammoHwachaRocketAirburst.getAmmoType(), arrowRender);
		this.addMissileRender(Ammo.ammoBallistaBolt.getAmmoType(), arrowRender);
		this.addMissileRender(Ammo.ammoBallistaBoltExplosive.getAmmoType(), arrowRender);
		this.addMissileRender(Ammo.ammoBallistaBoltFlame.getAmmoType(), arrowRender);
		this.addMissileRender(Ammo.ammoBallistaBoltIron.getAmmoType(), arrowRender);

		this.addMissileRender(Ammo.ammoBallShot.getAmmoType(), shotRender);
		this.addMissileRender(Ammo.ammoBallIronShot.getAmmoType(), shotRender);

		this.addMissileRender(Ammo.ammoSoldierArrowWood.getAmmoType(), arrowRender);
		this.addMissileRender(Ammo.ammoSoldierArrowIron.getAmmoType(), arrowRender);
		this.addMissileRender(Ammo.ammoSoldierArrowWoodFlame.getAmmoType(), arrowRender);
		this.addMissileRender(Ammo.ammoSoldierArrowIronFlame.getAmmoType(), arrowRender);

		this.addMissileRender(Ammo.ammoTorpedo10.getAmmoType(), shotRender);
		this.addMissileRender(Ammo.ammoTorpedo15.getAmmoType(), shotRender);
		this.addMissileRender(Ammo.ammoTorpedo30.getAmmoType(), shotRender);
		this.addMissileRender(Ammo.ammoTorpedo45.getAmmoType(), shotRender);

		/**
		 * gate renders
		 */
		RenderingRegistry.registerEntityRenderingHandler(EntityGate.class, new RenderGateHelper());
		this.addGateRender(0, new RenderGateBasic());
		this.addGateRender(1, new RenderGateBasic());
		this.addGateRender(4, new RenderGateSingle());
		this.addGateRender(5, new RenderGateSingle());
		this.addGateRender(8, new RenderGateDouble());
		this.addGateRender(9, new RenderGateDouble());
		this.addGateRender(12, new RenderGateRotatingBridge());

		/**
		 * load up crafting TE models, renders, etc
		 */
		ClientRegistry.bindTileEntitySpecialRenderer(TEAWCrafting.class, RenderCraftingHelper.instance());
		MinecraftForgeClient.registerItemRenderer(BlockLoader.crafting.blockID, RenderCraftingHelper.instance());
		this.addTEModel(0, new ModelTable1());
		this.addTEModel(1, new ModelTable2());
		this.addTEModel(2, new ModelTable3());
		this.addTEModel(3, new ModelTable4());
		this.addTEModel(4, new ModelTable5());
		this.addTEModel(5, new ModelTable6());
		this.addTEModel(6, new ModelTable7());
		this.addTEModel(7, new ModelTable8());

		this.teModelTextures.put(0, new ResourceLocation(AncientWarfareCore.modID, "model/vehicle/crafting/teResearchTable.png"));
		this.teModelTextures.put(1, new ResourceLocation(AncientWarfareCore.modID, "model/vehicle/crafting/teEngineeringStation.png"));
		this.teModelTextures.put(2, new ResourceLocation(AncientWarfareCore.modID, "model/vehicle/crafting/teCivilEngineeringStation.png"));
		this.teModelTextures.put(3, new ResourceLocation(AncientWarfareCore.modID, "model/vehicle/crafting/teVehicleStation.png"));
		this.teModelTextures.put(4, new ResourceLocation(AncientWarfareCore.modID, "model/vehicle/crafting/teAmmoStation.png"));
		this.teModelTextures.put(5, new ResourceLocation(AncientWarfareCore.modID, "model/vehicle/crafting/teNpcStation.png"));
		this.teModelTextures.put(6, new ResourceLocation(AncientWarfareCore.modID, "model/vehicle/crafting/teAlchemyStation.png"));
		this.teModelTextures.put(7, new ResourceLocation(AncientWarfareCore.modID, "model/vehicle/crafting/teAutoCrafting.png"));

		/**
		 * load up the vehicle item renderer...
		 */
		MinecraftForgeClient.registerItemRenderer(ItemLoader.vehicleSpawner.itemID, RenderVehicleHelper.instance());

		/**
		 * npcs...
		 */
		RenderingRegistry.registerEntityRenderingHandler(NpcBase.class, new RenderNpcHelper(new ModelBiped(), 1.0f));

		/**
		 * civic item render (render item as block)
		 */
		MinecraftForgeClient.registerItemRenderer(ItemLoader.civicPlacer.itemID, new CivicItemRenderer());
		RenderTEMotor engineRender = new RenderTEMotor();
		MinecraftForgeClient.registerItemRenderer(BlockLoader.engineBlock.blockID, engineRender);
		ClientRegistry.bindTileEntitySpecialRenderer(PluginProxy.bcProxy.getHandCrankEngineClass(), engineRender);

	}

	public void addTEModel(int type, ModelTEBase model) {
		this.teModels.put(type, model);
	}

	public ModelTEBase getTEModel(int type) {
		return this.teModels.get(type);
	}

	public String getTEModelTexture(int type) {
		return this.teModelTextures.get(type);
	}

	public void addVehicleRender(IVehicleType type, RenderVehicleBase rend, ModelVehicleBase model) {
		this.vehicleRenders.put(type.getGlobalVehicleType(), rend);
		this.vehicleModels.put(type.getGlobalVehicleType(), model);
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

	public RenderVehicleBase getRenderForVehicle(int type) {
		if (!this.vehicleRenders.containsKey(type)) {
			return dummyRender;
		}
		return this.vehicleRenders.get(type);
	}

	public ModelVehicleBase getVehicleModel(int type) {
		return this.vehicleModels.get(type);
	}

	public Render getGateRender(int type) {
		return this.gateRenders.get(type);
	}

	public void addGateRender(int type, Render rend) {
		this.gateRenders.put(type, rend);
	}

}
