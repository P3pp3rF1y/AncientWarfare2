package net.shadowmage.ancientwarfare.vehicle.missiles;

import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.vehicle.registry.AmmoRegistry;

public class AmmoPebbleShot extends Ammo {

	public AmmoPebbleShot(int weight) {
		super("ammo_pebble_shot_" + weight);
		this.isPersistent = false;
		this.isArrow = false;
		this.isRocket = false;
		this.ammoWeight = weight;
		this.secondaryAmmoCount = weight;
		float scaleFactor = weight + 45.f;
		this.renderScale = (weight / scaleFactor) * 2;
		this.configName = "pebble_shot_" + weight;
		this.modelTexture = new ResourceLocation(AncientWarfareCore.MOD_ID, "textures/model/vehicle/ammo/ammo_stone_shot.png");
	}

	@Override
	public boolean hasSecondaryAmmo() {
		return true;
	}

	@Override
	public IAmmo getSecondaryAmmoType() {
		return AmmoRegistry.ammoBallShot;
	}

	@Override
	public void onImpactWorld(World world, float x, float y, float z, MissileBase missile, RayTraceResult hit) {
		//noop
	}

	@Override
	public void onImpactEntity(World world, Entity ent, float x, float y, float z, MissileBase missile) {
		//noop
	}

}
