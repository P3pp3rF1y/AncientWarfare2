package net.shadowmage.ancientwarfare.npc.ai;

import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.pathfinding.PathFinder;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;

public class PathFind extends PathFinder{
    private static final int RAIL = 9, FENCE = 11;
    private final boolean doorAllowed;
    private final boolean closedPath;
    public PathFind(IBlockAccess world, boolean openDoor, boolean closedDoor, boolean water, boolean drown) {
        super(world, openDoor, closedDoor, water, drown);
        this.doorAllowed = openDoor;
        this.closedPath = closedDoor;
    }

    /**
     * Checks if an entity collides with blocks at a position.
     * Returns 1 if clear, 0 for colliding with any solid block,
     * -1 for water(if avoiding water) but otherwise clear, -2 for lava, -3 for fence, -4 for closed trapdoor,
     * 2 if otherwise clear except for open trapdoor or water(if not avoiding)
     */
    @Override
    public int getVerticalOffset(Entity entity, int posX, int posY, int posZ, PathPoint target){
        boolean trapOrWater = false;

        for (int l = posX; l < posX + target.xCoord; ++l)
        {
            for (int i1 = posY; i1 < posY + target.yCoord; ++i1)
            {
                for (int j1 = posZ; j1 < posZ + target.zCoord; ++j1)
                {
                    Block block = entity.worldObj.getBlock(l, i1, j1);

                    if (block.getMaterial() != Material.air)
                    {
                        if (block instanceof BlockTrapDoor)
                        {
                            trapOrWater = true;
                        }
                        else if (block.getMaterial() == Material.water)
                        {
                            if (isPathingInWater)
                            {
                                return -1;
                            }
                            trapOrWater = true;
                        }
                        else
                        {
                            if (!doorAllowed && isDoor(block))
                            {
                                return 0;
                            }
                        }

                        int k1 = block.getRenderType();
                        if (k1 == RAIL)
                        {
                            int j2 = MathHelper.floor_double(entity.posX);
                            int l1 = MathHelper.floor_double(entity.posY);
                            int i2 = MathHelper.floor_double(entity.posZ);
                            if (entity.worldObj.getBlock(j2, l1, i2).getRenderType() != RAIL && entity.worldObj.getBlock(j2, l1 - 1, i2).getRenderType() != RAIL)
                            {
                                return -3;
                            }
                        }
                        else if (!block.getBlocksMovement(entity.worldObj, l, i1, j1) && (!closedPath || !isDoor(block)))
                        {
                            if (k1 == FENCE || block instanceof BlockFenceGate || block instanceof BlockWall)
                            {
                                return -3;
                            }
                            if (block instanceof BlockTrapDoor)
                            {
                                return -4;
                            }
                            if (block.getMaterial() != Material.lava)
                            {
                                return 0;
                            }
                            if (!entity.handleLavaMovement())
                            {
                                return -2;
                            }
                        }
                    }
                }
            }
        }

        return trapOrWater ? 2 : 1;
    }

    private boolean isDoor(Block block) {
        return block instanceof BlockDoor || block instanceof BlockFenceGate;
    }
}
