package net.shadowmage.ancientwarfare.vehicle.missiles;

import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.vehicle.init.AWVehicleSounds;

public class AmmoBallistaBolt extends Ammo {

	public AmmoBallistaBolt() {
		super("ammo_ballista_bolt");
		ammoWeight = 2.f;
		renderScale = 0.3f;
		vehicleDamage = 18;
		entityDamage = 18;
		isArrow = true;
		isRocket = false;
		isPersistent = true;
		configName = "ballist_bolt";
		modelTexture = new ResourceLocation(AncientWarfareCore.MOD_ID, "textures/model/vehicle/ammo/arrow_wood.png");
	}

	@Override
	public void onImpactWorld(World world, float x, float y, float z, MissileBase missile, RayTraceResult hit) {
		if (!world.isRemote) {
			world.playSound(null, x, y, z, AWVehicleSounds.BALLISTA_BOLT_HIT_GROUND, SoundCategory.AMBIENT, 2, 1);
		}
	}

	@Override
	public void onImpactEntity(World world, Entity ent, float x, float y, float z, MissileBase missile) {
		if (!world.isRemote) {
			// using World.playSound instead of Entity.playSound, because Entity.playSound plays the sound to everyone nearby except(!) this player
			world.playSound(null, x, y, z, AWVehicleSounds.BALLISTA_BOLT_HIT_ENTITY, SoundCategory.NEUTRAL, 2, 1);
			ent.attackEntityFrom(DamageType.causeEntityMissileDamage(missile.shooterLiving, false, false), getEntityDamage());
		}
	}

}
