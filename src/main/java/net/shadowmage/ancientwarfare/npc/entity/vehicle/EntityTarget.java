package net.shadowmage.ancientwarfare.npc.entity.vehicle;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

import java.lang.ref.WeakReference;

public class EntityTarget implements ITarget {
	private WeakReference<EntityLivingBase> entity;

	public EntityTarget(EntityLivingBase entity) {
		this.entity = new WeakReference<>(entity);
	}

	@Override
	public double getX() {
		EntityLivingBase e = entity.get();
		return e == null ? 0D : e.posX;
	}

	@Override
	public double getY() {
		EntityLivingBase e = entity.get();
		return e == null ? 0D : e.posY + e.getEyeHeight();
	}

	@Override
	public double getZ() {
		EntityLivingBase e = entity.get();
		return e == null ? 0D : e.posZ;
	}

	@Override
	public AxisAlignedBB getBoundigBox() {
		EntityLivingBase e = entity.get();
		return e == null ? new AxisAlignedBB(0, 0, 0, 0, 0, 0) : e.getEntityBoundingBox();
	}

	@Override
	public boolean exists(World world) {
		EntityLivingBase e = entity.get();
		return e != null && !e.isDead;
	}
}
