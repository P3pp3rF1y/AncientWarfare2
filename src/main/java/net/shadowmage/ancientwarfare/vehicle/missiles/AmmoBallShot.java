package net.shadowmage.ancientwarfare.vehicle.missiles;

import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;

public class AmmoBallShot extends Ammo {

	public AmmoBallShot() {
		super("ammo_ball_shot");
		this.renderScale = 0.15f;
		this.ammoWeight = 1.f;
		this.entityDamage = 5;
		this.vehicleDamage = 5;
		this.isPersistent = false;
		this.configName = "ball_shot";
		this.isCraftable = false;
		this.modelTexture = new ResourceLocation(AncientWarfareCore.MOD_ID, "textures/model/vehicle/ammo/ammo_stone_shot.png");
	}

	@Override
	public void onImpactWorld(World world, float x, float y, float z, MissileBase missile, RayTraceResult hit) {
		//NOOP
	}

	@Override
	public void onImpactEntity(World world, Entity ent, float x, float y, float z, MissileBase missile) {
		if (!world.isRemote) {
			ent.attackEntityFrom(DamageType.causeEntityMissileDamage(missile.shooterLiving, false, false), this.getEntityDamage());
		}
	}
}
