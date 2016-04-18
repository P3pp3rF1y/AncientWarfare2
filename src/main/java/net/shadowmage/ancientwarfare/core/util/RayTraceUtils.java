package net.shadowmage.ancientwarfare.core.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.List;

public class RayTraceUtils {

    public static MovingObjectPosition getPlayerTarget(EntityPlayer player, float range, float border) {
        HashSet<Entity> excluded = new HashSet<Entity>();
        excluded.add(player);
        if (player.ridingEntity != null) {
            excluded.add(player.ridingEntity);
        }
        float yOffset = player.worldObj.isRemote ? 0.f : 1.62F;
        Vec3 look = player.getLookVec();
        look.xCoord *= range;
        look.yCoord *= range;
        look.zCoord *= range;
        look.xCoord += player.posX;
        look.yCoord += player.posY + yOffset;
        look.zCoord += player.posZ;
        return tracePath(player.worldObj, player.posX, player.posY + yOffset, player.posZ, look.xCoord, look.yCoord, look.zCoord, border, excluded);
    }

    public static MovingObjectPosition tracePathWithYawPitch(World world, float x, float y, float z, float yaw, float pitch, float range, float borderSize, HashSet<Entity> excluded) {
        float tx = x + (Trig.sinDegrees(yaw + 180) * range * Trig.cosDegrees(pitch));
        float ty = (-Trig.sinDegrees(pitch) * range) + y;
        float tz = z + (Trig.cosDegrees(yaw) * range * Trig.cosDegrees(pitch));
        return tracePath(world, x, y, z, tx, ty, tz, borderSize, excluded);
    }

    /**
     * @param x          startX
     * @param y          startY
     * @param z          startZ
     * @param tx         endX
     * @param ty         endY
     * @param tz         endZ
     * @param borderSize extra area to examine around line for entities
     * @param excluded   any excluded entities (the player, etc)
     * @return a MovingObjectPosition of either the block hit (no entity hit), the entity hit (hit an entity), or null for nothing hit
     */
    @SuppressWarnings("unchecked")
    public static MovingObjectPosition tracePath(World world, double x, double y, double z, double tx, double ty, double tz, float borderSize, HashSet<Entity> excluded) {
        double minX = x < tx ? x : tx;
        double minY = y < ty ? y : ty;
        double minZ = z < tz ? z : tz;
        double maxX = x > tx ? x : tx;
        double maxY = y > ty ? y : ty;
        double maxZ = z > tz ? z : tz;
        AxisAlignedBB bb = AxisAlignedBB.getBoundingBox(minX, minY, minZ, maxX, maxY, maxZ).expand(borderSize, borderSize, borderSize);
        List<Entity> allEntities = world.getEntitiesWithinAABBExcludingEntity(null, bb);
        Entity closestHitEntity = null;
        float closestHit = Float.POSITIVE_INFINITY;
        float currentHit;
        MovingObjectPosition intercept;
        Vec3 startVec = Vec3.createVectorHelper(x, y, z);
        Vec3 endVec = Vec3.createVectorHelper(tx, ty, tz);
        for (Entity ent : allEntities) {
            if (ent.canBeCollidedWith() && !excluded.contains(ent)) {
                AxisAlignedBB entityBb = ent.boundingBox;
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
            return new MovingObjectPosition(closestHitEntity);
        }
        startVec = Vec3.createVectorHelper(x, y, z);
        endVec = Vec3.createVectorHelper(tx, ty, tz);
        return world.rayTraceBlocks(startVec, endVec);
    }

}
