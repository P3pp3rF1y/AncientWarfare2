package net.shadowmage.ancientwarfare.vehicle.missiles;

import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;

public class AmmoFlameShot extends Ammo {

	public AmmoFlameShot(int weight) {
		super("ammo_flame_shot_" + weight);
		this.isPersistent = false;
		this.isArrow = false;
		this.isRocket = false;
		this.isFlaming = true;
		this.ammoWeight = weight;
		float scaleFactor = weight + 45.f;
		this.renderScale = (weight / scaleFactor) * 2;
		this.configName = "flame_shot_" + weight;
		this.vehicleDamage = 8;
		this.entityDamage = 8;
		this.modelTexture = new ResourceLocation(AncientWarfareCore.MOD_ID, "textures/model/vehicle/ammo/ammo_stone_shot.png");
	}

	@Override
	public void onImpactWorld(World world, float x, float y, float z, MissileBase missile, RayTraceResult hit) {
		if (!world.isRemote) {
			int bx = (int) x;
			int by = (int) y + 2;
			int bz = (int) z;
			this.igniteBlock(world, bx, by, bz, 5);
			if (this.ammoWeight >= 15) {
				this.igniteBlock(world, bx - 1, by, bz, 5);
				this.igniteBlock(world, bx + 1, by, bz, 5);
				this.igniteBlock(world, bx, by, bz - 1, 5);
				this.igniteBlock(world, bx, by, bz + 1, 5);
			}
			if (ammoWeight >= 30) {
				this.igniteBlock(world, bx - 1, by, bz - 1, 5);
				this.igniteBlock(world, bx - 1, by, bz + 1, 5);
				this.igniteBlock(world, bx + 1, by, bz - 1, 5);
				this.igniteBlock(world, bx + 1, by, bz + 1, 5);
				this.igniteBlock(world, bx - 2, by, bz, 5);
				this.igniteBlock(world, bx + 2, by, bz, 5);
				this.igniteBlock(world, bx, by, bz - 2, 5);
				this.igniteBlock(world, bx, by, bz + 2, 5);
			}
			if (ammoWeight >= 45) {
				this.igniteBlock(world, bx - 1, by, bz - 2, 5);
				this.igniteBlock(world, bx + 1, by, bz - 2, 5);
				this.igniteBlock(world, bx - 1, by, bz + 2, 5);
				this.igniteBlock(world, bx + 1, by, bz + 2, 5);
				this.igniteBlock(world, bx - 2, by, bz - 1, 5);
				this.igniteBlock(world, bx - 2, by, bz + 1, 5);
				this.igniteBlock(world, bx + 2, by, bz - 1, 5);
				this.igniteBlock(world, bx + 2, by, bz + 1, 5);
			}
		}
	}

	@Override
	public void onImpactEntity(World world, Entity ent, float x, float y, float z, MissileBase missile) {
		if (!world.isRemote) {
			ent.attackEntityFrom(DamageType.causeEntityMissileDamage(missile.shooterLiving, true, false), this.getEntityDamage());
			ent.setFire(3);
			onImpactWorld(world, x, y, z, missile, null);
		}
	}

}
