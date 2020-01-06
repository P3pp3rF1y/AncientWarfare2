package net.shadowmage.ancientwarfare.vehicle.missiles;

import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.vehicle.init.AWVehicleSounds;

public class AmmoBallistaBoltFlame extends Ammo {

	public AmmoBallistaBoltFlame() {
		super("ammo_ballista_bolt_flame");
		ammoWeight = 2.2f;
		renderScale = 0.3f;
		vehicleDamage = 16;
		entityDamage = 16;
		isArrow = true;
		isRocket = false;
		isPersistent = true;
		isFlaming = true;
		configName = "ballist_bolt_flame";
		modelTexture = new ResourceLocation(AncientWarfareCore.MOD_ID, "textures/model/vehicle/ammo/arrow_wood.png");
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
			// using World.playSound instead of Entity.playSound, because Entity.playSound plays the sound to everyone nearby except(!) this player
			world.playSound(null, x, y, z, AWVehicleSounds.BALLISTA_BOLT_HIT_ENTITY, SoundCategory.NEUTRAL, 2, 1);
			ent.attackEntityFrom(DamageType.causeEntityMissileDamage(missile.shooterLiving, true, false), getEntityDamage());
			ent.setFire(4);
		}
	}

}
