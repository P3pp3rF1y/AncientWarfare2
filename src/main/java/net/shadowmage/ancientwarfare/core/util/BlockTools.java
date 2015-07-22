/**
 Copyright 2012 John Cummens (aka Shadowmage, Shadowmage4513)
 This software is distributed under the terms of the GNU General Public License.
 Please see COPYING for precise license information.

 This file is part of Ancient Warfare.

 Ancient Warfare is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 Ancient Warfare is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with Ancient Warfare.  If not, see <http://www.gnu.org/licenses/>.


 */
package net.shadowmage.ancientwarfare.core.util;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.world.BlockEvent;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.core.config.AWCoreStatics;

import java.util.List;

public class BlockTools {

    /**
     * returns a direction right of the input
     */
    public static int turnRight(int dir) {
        return (dir + 1) % 4;
    }

    /**
     * returns a direction to the left of the input
     */
    public static int turnLeft(int dir) {
        return (dir + 3) % 4;
    }

    /**
     * returns a direction opposite of the input on the horizontal axis
     */
    public static int turnAround(int dir) {
        return (dir + 2) % 4;
    }

    /**
     * rotate a float X offset (-1<=x<=1) within a block
     */
    public static float rotateFloatX(float x, float z, int turns) {
        float x1, z1;
        x1 = x;
        z1 = z;
        for (int i = 0; i < turns; i++) {
            z = x1;
            x = 1.f - z1;
            x1 = x;
            z1 = z;
        }
        return x;
    }

    public static float rotateFloatZ(float x, float z, int turns) {
        float x1, z1;
        x1 = x;
        z1 = z;
        for (int i = 0; i < turns; i++) {
            z = x1;
            x = 1.f - z1;
            x1 = x;
            z1 = z;
        }
        return z;
    }

    public static BlockPosition getAverageOf(BlockPosition... positions) {
        float x = 0;
        float y = 0;
        float z = 0;
        int count = 0;
        for (BlockPosition pos : positions) {
            x += pos.x;
            y += pos.y;
            z += pos.z;
            count++;
        }
        if (count > 0) {
            x /= count;
            y /= count;
            z /= count;
        }
        return new BlockPosition(x, y, z);
    }

    /**
     * will return null if nothing is in range
     */
    @SuppressWarnings("rawtypes")
    public static BlockPosition getBlockClickedOn(EntityPlayer player, World world, boolean offset) {
        float scaleFactor = 1.0F;
        float rotPitch = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * scaleFactor;
        float rotYaw = player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * scaleFactor;
        double testX = player.prevPosX + (player.posX - player.prevPosX) * scaleFactor;
        double testY = player.prevPosY + (player.posY - player.prevPosY) * scaleFactor + 1.62D - player.yOffset;
        double testZ = player.prevPosZ + (player.posZ - player.prevPosZ) * scaleFactor;
        Vec3 testVector = Vec3.createVectorHelper(testX, testY, testZ);
        float var14 = MathHelper.cos(-rotYaw * 0.017453292F - (float) Math.PI);
        float var15 = MathHelper.sin(-rotYaw * 0.017453292F - (float) Math.PI);
        float var16 = -MathHelper.cos(-rotPitch * 0.017453292F);
        float vectorY = MathHelper.sin(-rotPitch * 0.017453292F);
        float vectorX = var15 * var16;
        float vectorZ = var14 * var16;
        double reachLength = 5.0D;
        Vec3 testVectorFar = testVector.addVector(vectorX * reachLength, vectorY * reachLength, vectorZ * reachLength);
        MovingObjectPosition testHitPosition = world.rayTraceBlocks(testVector, testVectorFar, true);

        /**
         * if nothing was hit, return null
         */
        if (testHitPosition == null) {
            return null;
        }

        Vec3 var25 = player.getLook(scaleFactor);
        float var27 = 1.0F;
        List<Entity> entitiesPossiblyHitByVector = world.getEntitiesWithinAABBExcludingEntity(player, player.boundingBox.addCoord(var25.xCoord * reachLength, var25.yCoord * reachLength, var25.zCoord * reachLength).expand(var27, var27, var27));
        for (Entity testEntity : entitiesPossiblyHitByVector) {
            if (testEntity.canBeCollidedWith()) {
                float bbExpansionSize = testEntity.getCollisionBorderSize();
                AxisAlignedBB entityBB = testEntity.boundingBox.expand(bbExpansionSize, bbExpansionSize, bbExpansionSize);
                /**
                 * if an entity is hit, return its position
                 */
                if (entityBB.isVecInside(testVector)) {
                    return new BlockPosition(testEntity.posX, testEntity.posY, testEntity.posZ);
                }
            }
        }
        /**
         * if no entity was hit, return the position impacted.
         */
        int var42 = testHitPosition.blockX;
        int var43 = testHitPosition.blockY;
        int var44 = testHitPosition.blockZ;
        /**
         * if should offset for side hit (block clicked IN)
         */
        if (offset) {
            switch (testHitPosition.sideHit) {
                case 0:
                    --var43;
                    break;
                case 1:
                    ++var43;
                    break;
                case 2:
                    --var44;
                    break;
                case 3:
                    ++var44;
                    break;
                case 4:
                    --var42;
                    break;
                case 5:
                    ++var42;
            }
        }
        return new BlockPosition(var42, var43, var44);
    }

    public static BlockPosition rotateAroundOrigin(BlockPosition pos, int turns) {
        for (int i = 0; i < turns; i++) {
            rotateAroundOrigin(pos);
        }
        return pos;
    }

    /**
     * rotate a position around its origin (0,0,0), in 90' clockwise steps
     */
    public static BlockPosition rotateAroundOrigin(BlockPosition pos) {
        int x = pos.x;
        int z = pos.z;
        pos.x = -z;
        pos.z = x;
//  boolean xNeg = x<0;
//  boolean zNeg = z<0;
//  if(!xNeg && !zNeg)//first quadrant
//    {
//    pos.x = -z;
//    pos.z = x;
//    }
//  else if(xNeg && !zNeg)//second quadrant
//    {
//    pos.x = -z;
//    pos.z = x;
//    }
//  else if(xNeg && zNeg)
//    {
//    pos.x = -z;
//    pos.z = x;
//    }
//  else//!xNeg && zNeg
//    {
//    pos.x = -z;
//    pos.z = x;
//    }  
        return pos;
    }

    /**
     * checks to see if TEST lies somewhere in the cube bounded by pos1 and pos2
     *
     * @return true if it does
     */
    public static boolean isPositionWithinBounds(BlockPosition test, BlockPosition pos1, BlockPosition pos2) {
        int minX;
        int maxX;
        int minY;
        int maxY;
        int minZ;
        int maxZ;
        if (pos1.x < pos2.x) {
            minX = pos1.x;
            maxX = pos2.x;
        } else {
            minX = pos2.x;
            maxX = pos1.x;
        }
        if (pos1.y < pos2.y) {
            minY = pos1.y;
            maxY = pos2.y;
        } else {
            minY = pos2.y;
            maxY = pos1.y;
        }
        if (pos1.z < pos2.z) {
            minZ = pos1.z;
            maxZ = pos2.z;
        } else {
            minZ = pos2.z;
            maxZ = pos1.z;
        }

        if (test.x >= minX && test.x <= maxX) {
            if (test.y >= minY && test.y <= maxY) {
                if (test.z >= minZ && test.z <= maxZ) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * return a new BlockPosition containing the minimum coordinates from the two passed in BlockPositions
     */
    public static BlockPosition getMin(BlockPosition pos1, BlockPosition pos2) {
        return new BlockPosition(Math.min(pos1.x, pos2.x), Math.min(pos1.y, pos2.y), Math.min(pos1.z, pos2.z));
    }

    public static BlockPosition getMin(BlockPosition pos1, BlockPosition pos2, BlockPosition out) {
        out.reassign(Math.min(pos1.x, pos2.x), Math.min(pos1.y, pos2.y), Math.min(pos1.z, pos2.z));
        return out;
    }

    /**
     * return a new BlockPosition containing the maximum coordinates from the two passed in BlockPositions
     */
    public static BlockPosition getMax(BlockPosition pos1, BlockPosition pos2) {
        return new BlockPosition(Math.max(pos1.x, pos2.x), Math.max(pos1.y, pos2.y), Math.max(pos1.z, pos2.z));
    }

    public static BlockPosition getMax(BlockPosition pos1, BlockPosition pos2, BlockPosition out) {
        out.reassign(Math.max(pos1.x, pos2.x), Math.max(pos1.y, pos2.y), Math.max(pos1.z, pos2.z));
        return out;
    }

    /**
     * return an MC directional facing int for a players rotationYaw
     *
     * @return (0-3) for south, west, north, east respectively
     */
    public static int getPlayerFacingFromYaw(float rotation) {
        double yaw = (double) rotation;
        while (yaw < 0.d) {
            yaw += 360.d;
        }
        while (yaw >= 360.d) {
            yaw -= 360.d;
        }
        double adjYaw = yaw + 45;
        adjYaw *= 4;//multiply by four
        adjYaw /= 360.d;
        return (MathHelper.floor_double(adjYaw)) % 4;
    }

    public static ForgeDirection getForgeDirectionFromFacing(int facing) {
        switch (facing) {
            case 0: {
                return ForgeDirection.SOUTH;
            }
            case 1: {
                return ForgeDirection.WEST;
            }
            case 2: {
                return ForgeDirection.NORTH;
            }
            case 3: {
                return ForgeDirection.EAST;
            }
            default: {
                return ForgeDirection.NORTH;
            }
        }
    }

    /**
     * rotates a given block-position in a given area by the number of turns.  Used by templates
     * to get a relative position.
     */
    public static BlockPosition rotateInArea(BlockPosition pos, int xSize, int zSize, int turns) {
        int xSize1 = xSize;
        int zSize1 = zSize;
        int x = pos.x;
        int z = pos.z;
        if (x >= xSize) {
            x = 0;
        }
        if (z >= zSize) {
            z = 0;
        }
        int x1 = x;
        int z1 = z;
        for (int i = 0; i < turns; i++) {
            x = zSize - 1 - z1;
            z = x1;
            x1 = x;
            z1 = z;
            xSize = zSize1;
            zSize = xSize1;
            xSize1 = xSize;
            zSize1 = zSize;
        }
        pos.x = x;
        pos.z = z;
        return pos;
    }

    public static boolean breakBlockAndDrop(World world, String name, int x, int y, int z) {
        return breakBlock(world, name, x, y, z, 0, true);
    }

    public static boolean breakBlock(World world, String playerName, int x, int y, int z, int fortune, boolean doDrop) {
        if (world.isRemote) {
            return false;
        }
        Block block = world.getBlock(x, y, z);
        if (block.isAir(world, x, y, z) || block.getBlockHardness(world, x, y, z) < 0) {
            return false;
        }
        if (doDrop) {
            int meta = world.getBlockMetadata(x, y, z);
            if (!canBreakBlock(world, playerName, x, y, z, block, meta)) {
                return false;
            }
            block.dropBlockAsItem(world, x, y, z, meta, fortune);
        }
        return world.setBlockToAir(x, y, z);
    }


    public static boolean canBreakBlock(World world, String playerName, int x, int y, int z, Block block, int meta) {
        if (AWCoreStatics.fireBlockBreakEvents) {
            return !MinecraftForge.EVENT_BUS.post(new BlockEvent.BreakEvent(x, y, z, world, block, meta, AncientWarfareCore.proxy.getFakePlayer(world, playerName)));
        }
        return true;
    }

}
