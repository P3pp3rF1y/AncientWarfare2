package net.shadowmage.ancientwarfare.vehicle.missiles;

import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;

public class AmmoHwachaRocketExplosive extends Ammo {

	public AmmoHwachaRocketExplosive() {
		super("ammo_hwacha_rocket_explosive");
		this.entityDamage = 4;
		this.vehicleDamage = 4;
		this.isArrow = true;
		this.isPersistent = false;
		this.isRocket = true;
		this.ammoWeight = 1.3f;
		this.renderScale = 0.2f;
		this.configName = "hwacha_rocket_explosive";
		this.modelTexture = new ResourceLocation(AncientWarfareCore.MOD_ID, "textures/model/vehicle/ammo/arrow_wood.png");
	}

	@Override
	public void onImpactWorld(World world, float x, float y, float z, MissileBase missile, RayTraceResult hit) {
		if (!world.isRemote) {
			createExplosion(world, missile, x, y, z, 0.6f);
		}
	}

	@Override
	public void onImpactEntity(World world, Entity ent, float x, float y, float z, MissileBase missile) {
		if (!world.isRemote) {
			ent.attackEntityFrom(DamageType.causeEntityMissileDamage(missile.shooterLiving, false, true), this.getEntityDamage());
			ent.setFire(3);
			createExplosion(world, missile, x, y, z, 0.8f);
		}
	}
}
