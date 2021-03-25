package net.shadowmage.ancientwarfare.vehicle.missiles;

import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;

public class AmmoIronShot extends Ammo {

	public AmmoIronShot(int weight, int damage) {
		super("ammo_iron_shot_" + weight);
		ammoWeight = weight;
		entityDamage = damage;
		vehicleDamage = damage;
		float scaleFactor = weight + 45.f;
		renderScale = (weight / scaleFactor) * 2;
		configName = "iron_shot_" + weight;
		modelTexture = new ResourceLocation(AncientWarfareCore.MOD_ID, "textures/model/vehicle/ammo/ammo_stone_shot.png");
	}

	@Override
	public void onImpactWorld(World world, float x, float y, float z, MissileBase missile, RayTraceResult hit) {
		if (world.isRemote) {
			return;
		}

		BlockPos origin = new BlockPos(x, y, z);
		float maxHardness = 5 + (ammoWeight * 0.2f + ammoWeight * 0.8f * world.rand.nextFloat());

		breakAroundOnLevel(world, origin, origin, maxHardness);
		breakAroundOnLevel(world, origin, origin.up(), maxHardness);
		breakAroundOnLevel(world, origin, origin.down(), maxHardness);
	}

	@Override
	public void onImpactEntity(World world, Entity ent, float x, float y, float z, MissileBase missile) {
		if (!world.isRemote) {
			ent.attackEntityFrom(DamageType.causeEntityMissileDamage(missile.shooterLiving, false, false), getEntityDamage());
		}
	}

}
