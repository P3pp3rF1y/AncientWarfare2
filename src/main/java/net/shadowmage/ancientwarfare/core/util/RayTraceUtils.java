package net.shadowmage.ancientwarfare.core.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class RayTraceUtils {

	public static RayTraceResult getPlayerTarget(EntityPlayer player, float range, float border) {
		HashSet<Entity> excluded = new HashSet<>();
		excluded.add(player);
		if (player.getRidingEntity() != null) {
			excluded.add(player.getRidingEntity());
		}
		float yOffset = player.getEyeHeight();
		Vec3d look = player.getLookVec();
		look = look.scale(range);
		look = look.addVector(player.posX, player.posY + yOffset, player.posZ);
		return tracePath(player.world, player.posX, player.posY + yOffset, player.posZ, look.x, look.y, look.z, border, excluded);
	}

	public static RayTraceResult tracePathWithYawPitch(World world, float x, float y, float z, float yaw, float pitch, float range, float borderSize, HashSet<Entity> excluded) {
		float tx = x + (Trig.sinDegrees(yaw + 180) * range * Trig.cosDegrees(pitch));
		float ty = (-Trig.sinDegrees(pitch) * range) + y;
		float tz = z + (Trig.cosDegrees(yaw) * range * Trig.cosDegrees(pitch));
		return tracePath(world, x, y, z, tx, ty, tz, borderSize, excluded);
	}

    /*
	 * @param x          startX
     * @param y          startY
     * @param z          startZ
     * @param tx         endX
     * @param ty         endY
     * @param tz         endZ
     * @param borderSize extra area to examine around line for entities
     * @param excluded   any excluded entities (the player, etc)
     * @return a RayTraceResult of either the block hit (no entity hit), the entity hit (hit an entity), or null for nothing hit
     */

	public static RayTraceResult tracePath(World world, double x, double y, double z, double tx, double ty, double tz, float borderSize, HashSet<Entity> excluded) {
		double minX = x < tx ? x : tx;
		double minY = y < ty ? y : ty;
		double minZ = z < tz ? z : tz;
		double maxX = x > tx ? x : tx;
		double maxY = y > ty ? y : ty;
		double maxZ = z > tz ? z : tz;
		AxisAlignedBB bb = new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ).expand(borderSize, borderSize, borderSize);
		List<Entity> allEntities = world.getEntitiesWithinAABBExcludingEntity(null, bb);
		Entity closestHitEntity = null;
		float closestHit = Float.POSITIVE_INFINITY;
		float currentHit;
		RayTraceResult intercept;
		Vec3d startVec = new Vec3d(x, y, z);
		Vec3d endVec = new Vec3d(tx, ty, tz);
		for (Entity ent : allEntities) {
			if (ent.canBeCollidedWith() && !excluded.contains(ent)) {
				AxisAlignedBB entityBb = ent.getEntityBoundingBox();
				if (entityBb != null) {
					float entBorder = ent.getCollisionBorderSize();
					intercept = entityBb.expand(entBorder, entBorder, entBorder).calculateIntercept(startVec, endVec);
					if (intercept != null) {
						currentHit = (float) intercept.hitVec.distanceTo(startVec);
						if (currentHit < closestHit || currentHit == 0) {
							closestHit = currentHit;
							closestHitEntity = ent;
						}
					}
				}
			}
		}
		if (closestHitEntity != null) {
			return new RayTraceResult(closestHitEntity);
		}
		startVec = new Vec3d(x, y, z);
		endVec = new Vec3d(tx, ty, tz);
		return world.rayTraceBlocks(startVec, endVec);
	}

	@Nullable
	public static RayTraceResult raytraceMultiAABB(List<AxisAlignedBB> aabbs, BlockPos pos, Vec3d start, Vec3d end) {
		List<RayTraceResult> list = new ArrayList<>();

		for (AxisAlignedBB axisalignedbb : aabbs) {
			list.add(rayTraceAABBIntercept(pos, start, end, axisalignedbb));
		}

		RayTraceResult ret = null;
		double closestHit = Integer.MAX_VALUE;

		for (RayTraceResult raytraceresult : list) {
			if (raytraceresult != null) {
				double distance = raytraceresult.hitVec.squareDistanceTo(start);

				if (distance < closestHit) {
					ret = raytraceresult;
					closestHit = distance;
				}
			}
		}

		return ret;
	}

	@Nullable
	public static <T> T raytraceMultiAABB(List<AxisAlignedBB> aabbs, BlockPos pos, Vec3d start, Vec3d end, Function2<RayTraceResult, AxisAlignedBB, T> getValue) {
		List<RayTraceResult> list = new ArrayList<>();

		for (AxisAlignedBB axisalignedbb : aabbs) {
			list.add(rayTraceAABBIntercept(pos, start, end, axisalignedbb));
		}

		T ret = null;
		double closestHit = Integer.MAX_VALUE;

		for (int i = 0; i < list.size(); i++) {
			RayTraceResult raytraceresult = list.get(i);
			if (raytraceresult != null) {
				double distance = raytraceresult.hitVec.squareDistanceTo(start);

				if (distance < closestHit) {
					ret = getValue.apply(raytraceresult, aabbs.get(i));
					closestHit = distance;
				}
			}
		}

		return ret;
	}

	@Nullable
	private static RayTraceResult rayTraceAABBIntercept(BlockPos pos, Vec3d start, Vec3d end, AxisAlignedBB boundingBox) {
		Vec3d vecA = start.subtract((double) pos.getX(), (double) pos.getY(), (double) pos.getZ());
		Vec3d vecB = end.subtract((double) pos.getX(), (double) pos.getY(), (double) pos.getZ());
		RayTraceResult raytraceresult = boundingBox.calculateIntercept(vecA, vecB);
		return raytraceresult == null ? null : new RayTraceResult(raytraceresult.hitVec.addVector((double) pos.getX(), (double) pos.getY(), (double) pos.getZ()), raytraceresult.sideHit, pos);
	}

}
