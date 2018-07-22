package net.shadowmage.ancientwarfare.vehicle.missiles;

import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;

public class AmmoIronShot extends Ammo {

	public AmmoIronShot(int weight, int damage) {
		super("ammo_iron_shot_" + weight + "_" + damage);
		this.ammoWeight = weight;
		this.entityDamage = damage;
		this.vehicleDamage = damage;
		float scaleFactor = weight + 45.f;
		this.renderScale = (weight / scaleFactor) * 2;
		this.configName = "iron_shot_" + weight;
		this.modelTexture = new ResourceLocation(AncientWarfareCore.MOD_ID, "textures/model/vehicle/ammo/ammo_stone_shot.png");
	}

	@Override
	public void onImpactWorld(World world, float x, float y, float z, MissileBase missile, RayTraceResult hit) {
		if (ammoWeight >= 10 && !world.isRemote) {
			int bx = (int) x;
			int by = (int) y;
			int bz = (int) z;
			this.breakBlockAndDrop(world, bx, by, bz);
			if (ammoWeight >= 15) {
				this.breakBlockAndDrop(world, bx, by - 1, bz);
				this.breakBlockAndDrop(world, bx - 1, by, bz);
				this.breakBlockAndDrop(world, bx + 1, by, bz);
				this.breakBlockAndDrop(world, bx, by, bz - 1);
				this.breakBlockAndDrop(world, bx, by, bz + 1);
			}
			if (ammoWeight >= 25) {
				this.breakBlockAndDrop(world, bx - 1, by, bz - 1);
				this.breakBlockAndDrop(world, bx + 1, by, bz - 1);
				this.breakBlockAndDrop(world, bx - 1, by, bz + 1);
				this.breakBlockAndDrop(world, bx + 1, by, bz + 1);
			}
		}
	}

	@Override
	public void onImpactEntity(World world, Entity ent, float x, float y, float z, MissileBase missile) {
		if (!world.isRemote) {
			ent.attackEntityFrom(DamageType.causeEntityMissileDamage(missile.shooterLiving, false, false), this.getEntityDamage());
		}
	}

}
