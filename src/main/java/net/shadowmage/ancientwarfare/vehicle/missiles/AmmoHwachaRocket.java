package net.shadowmage.ancientwarfare.vehicle.missiles;

import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.vehicle.config.AWVehicleStatics;

public class AmmoHwachaRocket extends Ammo {

	public static final float BURN_TIME_FACTOR = 3.f;
	public static final float ACCELERATION_FACTOR = 0.01f;

	public AmmoHwachaRocket() {
		super("ammo_hwacha_rocket");
		this.entityDamage = AWVehicleStatics.vehicleStats.ammoHwachaRocketDamage;
		this.vehicleDamage = AWVehicleStatics.vehicleStats.ammoHwachaRocketDamage;
		this.isArrow = true;
		this.isPersistent = true;
		this.isRocket = true;
		this.ammoWeight = 1.f;
		this.renderScale = 0.2f;
		this.configName = "hwacha_rocket";
		this.modelTexture = new ResourceLocation(AncientWarfareCore.MOD_ID, "textures/model/vehicle/ammo/arrow_wood.png");
	}

	@Override
	public void onImpactWorld(World world, float x, float y, float z, MissileBase missile, RayTraceResult hit) {
		//noop
	}

	@Override
	public void onImpactEntity(World world, Entity ent, float x, float y, float z, MissileBase missile) {
		if (!world.isRemote) {
			ent.attackEntityFrom(DamageType.causeEntityMissileDamage(missile.shooterLiving, false, false), this.getEntityDamage());
		}
	}

}
