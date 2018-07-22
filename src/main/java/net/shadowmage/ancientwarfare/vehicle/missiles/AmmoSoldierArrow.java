package net.shadowmage.ancientwarfare.vehicle.missiles;

import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;

public class AmmoSoldierArrow extends Ammo {

	public AmmoSoldierArrow(int damage, boolean flaming) {
		super("ammo_soldier_arrow_" + damage + (flaming ? "_flaming" : ""));
		this.ammoWeight = 1.f;
		this.renderScale = 0.125f;
		this.vehicleDamage = damage;
		this.entityDamage = damage;
		this.isArrow = true;
		this.isRocket = false;
		this.isPersistent = true;
		this.isFlaming = flaming;
		this.isCraftable = false;

		if (flaming) {
			this.configName = "soldier_arrow_flame_" + damage;
		} else {
			this.configName = "soldier_arrow_" + damage;
		}
		if (damage <= 5) {
			this.modelTexture = new ResourceLocation(AncientWarfareCore.MOD_ID, "textures/model/vehicle/ammo/arrow_wood.png");
		} else {
			this.modelTexture = new ResourceLocation(AncientWarfareCore.MOD_ID, "textures/model/vehicle/ammo/arrow_iron.png");
		}
	}

	@Override
	public void onImpactWorld(World world, float x, float y, float z, MissileBase missile, RayTraceResult hit) {
		//noop
	}

	@Override
	public void onImpactEntity(World world, Entity ent, float x, float y, float z, MissileBase missile) {
		if (!world.isRemote) {
			ent.attackEntityFrom(DamageType.causeEntityMissileDamage(missile.shooterLiving, isFlaming, false), this.getEntityDamage());
		}
	}
}
