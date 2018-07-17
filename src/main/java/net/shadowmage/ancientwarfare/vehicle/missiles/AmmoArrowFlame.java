package net.shadowmage.ancientwarfare.vehicle.missiles;

import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;

public class AmmoArrowFlame extends Ammo {

	public AmmoArrowFlame() {
		super("ammo_arrow_flame");
		this.ammoWeight = 1.2f;
		this.renderScale = 0.2f;
		this.vehicleDamage = 6;
		this.entityDamage = 6;
		this.isArrow = true;
		this.isRocket = false;
		this.isPersistent = true;
		this.isFlaming = true;
		this.configName = "arrow_flame";
		this.modelTexture = new ResourceLocation(AncientWarfareCore.MOD_ID, "textures/model/vehicle/ammo/arrow_wood.png");
	}

	@Override
	public void onImpactWorld(World world, float x, float y, float z, MissileBase missile, RayTraceResult hit) {
		if (!world.isRemote) {
			igniteBlock(world, (int) x, (int) y + 2, (int) z, 5);
		}
	}

	@Override
	public void onImpactEntity(World world, Entity ent, float x, float y, float z, MissileBase missile) {
		if (!world.isRemote) {
			ent.attackEntityFrom(DamageType.causeEntityMissileDamage(missile.shooterLiving, true, false), this.getEntityDamage());
			ent.setFire(3);
		}
	}

}
