package net.shadowmage.ancientwarfare.vehicle.missiles;

import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.vehicle.registry.AmmoRegistry;

public class AmmoHwachaRocketAirburst extends Ammo {

	public AmmoHwachaRocketAirburst() {
		super("ammo_hwacha_rocket_airburst");
		this.entityDamage = 0;
		this.vehicleDamage = 0;
		this.isArrow = true;
		this.isPersistent = false;
		this.isRocket = true;
		this.isProximityAmmo = true;
		this.groundProximity = 12.f;
		this.entityProximity = 10f;
		this.ammoWeight = 1.4f;
		this.renderScale = 0.2f;
		this.configName = "hwacha_rocket_airburst";
		this.modelTexture = new ResourceLocation(AncientWarfareCore.MOD_ID, "textures/model/vehicle/ammo/arrow_wood.png");
	}

	@Override
	public void onImpactWorld(World world, float x, float y, float z, MissileBase missile, RayTraceResult hit) {
		if (!world.isRemote) {
			this.spawnAirBurst(world, x, y, z, 10, AmmoRegistry.ammoBallShot, 4, missile.shooterLiving);
			missile.setDead();
		}
	}

	@Override
	public void onImpactEntity(World world, Entity ent, float x, float y, float z, MissileBase missile) {
		if (!world.isRemote) {
			this.spawnAirBurst(world, x, y, z, 10, AmmoRegistry.ammoBallShot, 4, missile.shooterLiving);
			missile.setDead();
		}
	}

}
