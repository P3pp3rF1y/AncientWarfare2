package net.shadowmage.ancientwarfare.vehicle.missiles;

import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;

public class AmmoTorpedo extends Ammo {

	public AmmoTorpedo(int weight) {
		super("ammo_torpedo_" + weight);
		this.isEnabled = false;
		this.isPersistent = false;
		this.isArrow = true;
		this.isRocket = false;
		this.isTorpedo = true;
		this.ammoWeight = weight;
		this.configName = "torpedo_" + weight;
		this.entityDamage = weight * 2;
		this.vehicleDamage = weight * 2;
		float scaleFactor = weight + 45.f;
		this.renderScale = (weight / scaleFactor) * 2;
		this.modelTexture = new ResourceLocation(AncientWarfareCore.MOD_ID, "textures/model/vehicle/ammo/ammo_stone_shot.png");
	}

	@Override
	public void onImpactWorld(World world, float x, float y, float z, MissileBase missile, RayTraceResult hit) {
		if (!world.isRemote) {
			float maxPower = 7.f;
			float powerPercent = ammoWeight / 45.f;
			float power = maxPower * powerPercent;
			this.createExplosion(world, missile, x, y, z, power);
		}
	}

	@Override
	public void onImpactEntity(World world, Entity ent, float x, float y, float z, MissileBase missile) {
		if (!world.isRemote) {
			float maxPower = 7.f;
			float powerPercent = ammoWeight / 45.f;
			float power = maxPower * powerPercent;
			this.createExplosion(world, missile, x, y, z, power);
		}
	}

}
