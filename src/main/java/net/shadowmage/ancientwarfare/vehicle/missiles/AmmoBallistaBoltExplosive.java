package net.shadowmage.ancientwarfare.vehicle.missiles;

import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;

public class AmmoBallistaBoltExplosive extends Ammo {

	public AmmoBallistaBoltExplosive() {
		super("ammo_ballista_bolt_explosive");
		this.ammoWeight = 2.6f;
		this.renderScale = 0.3f;
		this.vehicleDamage = 15;
		this.entityDamage = 15;
		this.isArrow = true;
		this.isRocket = false;
		this.isPersistent = false;
		this.configName = "ballist_bolt_explosive";
		this.modelTexture = new ResourceLocation(AncientWarfareCore.MOD_ID, "textures/model/vehicle/ammo/arrow_wood.png");
	}

	@Override
	public void onImpactWorld(World world, float x, float y, float z, MissileBase missile, RayTraceResult hit) {
		if (!world.isRemote) {
			createExplosion(world, missile, x, y, z, 0.8f);
		}
	}

	@Override
	public void onImpactEntity(World world, Entity ent, float x, float y, float z, MissileBase missile) {
		if (!world.isRemote) {
			ent.attackEntityFrom(DamageType.causeEntityMissileDamage(missile.shooterLiving, false, true), this.getEntityDamage());
			ent.setFire(3);
			createExplosion(world, missile, x, y, z, 1.2f);
		}
	}
}
