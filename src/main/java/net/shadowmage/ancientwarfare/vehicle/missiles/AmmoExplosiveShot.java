package net.shadowmage.ancientwarfare.vehicle.missiles;

import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;

public class AmmoExplosiveShot extends Ammo {

	private boolean bigExplosion;

	public AmmoExplosiveShot(int weight, boolean bigExplosion) {
		super("ammo_explosive_shot_" + weight + (bigExplosion ? "_big" : ""));
		this.ammoWeight = weight;
		this.bigExplosion = bigExplosion;
		this.entityDamage = weight;
		this.vehicleDamage = weight;
		float scaleFactor = weight + 45.f;
		this.renderScale = (weight / scaleFactor) * 2;

		if (bigExplosion) {
			this.configName = "high_explosive_" + weight;
		} else {
			this.configName = "explosive_" + weight;
		}
		this.modelTexture = new ResourceLocation(AncientWarfareCore.MOD_ID, "textures/model/vehicle/ammo/ammo_stone_shot.png");
	}

	@Override
	public void onImpactWorld(World world, float x, float y, float z, MissileBase missile, RayTraceResult hit) {
		if (!world.isRemote) {
			Vec3i dirVec = hit.sideHit.getDirectionVec();
			Vec3d hitVec = hit.hitVec.addVector(dirVec.getX() * 0.2d, dirVec.getY() * 0.2d, dirVec.getZ() * 0.2d);
			explode(world, (float) hitVec.x, (float) hitVec.y, (float) hitVec.z, missile);
		}
	}

	@Override
	public void onImpactEntity(World world, Entity ent, float x, float y, float z, MissileBase missile) {
		if (!world.isRemote) {
			explode(world, x, y, z, missile);
		}
	}

	private void explode(World world, float x, float y, float z, MissileBase missile) {
		float maxPower = bigExplosion ? 7.f : 3.5f;
		float powerPercent = ammoWeight / 45.f;
		float power = maxPower * powerPercent;
		createExplosion(world, missile, x, y, z, power);
	}
}
