package net.shadowmage.ancientwarfare.npc.ai;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.ChunkCache;

public class NpcNavigator extends PathNavigate {
    private final EntityLiving entity;
    private boolean swim;
    private boolean doors = true;
    public NpcNavigator(EntityLiving living) {
        super(living, living.worldObj);
        this.entity = living;
    }

    @Override
    public void setCanSwim(boolean value) {
        super.setCanSwim(value);
        this.swim = value;
    }

    @Override
    public void setEnterDoors(boolean value) {
        super.setEnterDoors(value);
        this.doors = value;
    }

    public void onWorldChange(){
        this.worldObj = entity.worldObj;
    }

    @Override
    public PathEntity getPathToXYZ(double posX, double posY, double posZ)
    {
        return !this.canNavigate() ? null : pathToXYZ(MathHelper.floor_double(posX), (int) posY, MathHelper.floor_double(posZ));
    }

    @Override
    public PathEntity getPathToEntityLiving(Entity target)
    {
        return !this.canNavigate() ? null : pathToEntity(target);
    }

    private PathEntity pathToEntity(Entity target)
    {
        ChunkCache chunkcache = cachePath(1, 16);
        PathEntity pathentity = (new PathFind(chunkcache, doors, getCanBreakDoors(), getAvoidsWater(), swim)).createEntityPathTo(this.entity, target, this.getPathSearchRange());
        this.worldObj.theProfiler.endSection();
        return pathentity;
    }

    private PathEntity pathToXYZ(int x, int y, int z)
    {
        ChunkCache chunkcache = cachePath(0, 8);
        PathEntity pathentity = (new PathFind(chunkcache, doors, getCanBreakDoors(), getAvoidsWater(), swim)).createEntityPathTo(this.entity, x, y, z, this.getPathSearchRange());
        this.worldObj.theProfiler.endSection();
        return pathentity;
    }

    private ChunkCache cachePath(int h, int r){
        this.worldObj.theProfiler.startSection("pathfind");
        int i = MathHelper.floor_double(this.entity.posX);
        int j = MathHelper.floor_double(this.entity.posY + h);
        int k = MathHelper.floor_double(this.entity.posZ);
        int l = (int)(this.getPathSearchRange() + r);
        return new ChunkCache(this.worldObj, i - l, j - l, k - l, i + l, j + l, k + l, 0);
    }

    /**
     * Whether pathing can be done
     */
    @Override
    protected boolean canNavigate()
    {
        return super.canNavigate() || this.entity.ridingEntity!=null;
    }

    /**
     * Returns true when an entity could stand at a position, including solid blocks under the entire entity.
     */
    @Override
    protected boolean isSafeToStandAt(int xOffset, int yOffset, int zOffset, int xSize, int ySize, int zSize, Vec3 origin, double vecX, double vecZ)
    {
        int k1 = xOffset - xSize / 2;
        int l1 = zOffset - zSize / 2;

        if (!this.isPositionClear(k1, yOffset, l1, xSize, ySize, zSize, origin, vecX, vecZ))
        {
            return false;
        }
        else
        {
            for (int i2 = k1; i2 < k1 + xSize; ++i2)
            {
                for (int j2 = l1; j2 < l1 + zSize; ++j2)
                {
                    double d2 = (double)i2 + 0.5D - origin.xCoord;
                    double d3 = (double)j2 + 0.5D - origin.zCoord;

                    if (d2 * vecX + d3 * vecZ >= 0.0D)
                    {
                        Material material = this.worldObj.getBlock(i2, yOffset - 1, j2).getMaterial();

                        if (material == Material.air || material == Material.lava || material == Material.fire || material == Material.cactus)
                        {
                            return false;
                        }

                        if (material == Material.water && !this.entity.isInWater())
                        {
                            return false;
                        }
                    }
                }
            }

            return true;
        }
    }
}
