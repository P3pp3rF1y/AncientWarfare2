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
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;
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

    public static BlockPos getAverageOf(BlockPos... positions) {
        float x = 0;
        float y = 0;
        float z = 0;
        int count = 0;
        for (BlockPos pos : positions) {
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
        return new BlockPos(x, y, z);
    }

    /**
     * will return null if nothing is in range
     */
    @SuppressWarnings("rawtypes")
    public static BlockPos getBlockClickedOn(EntityPlayer player, World world, boolean offset) {
        float scaleFactor = 1.0F;
        float rotPitch = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * scaleFactor;
        float rotYaw = player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * scaleFactor;
        double testX = player.prevPosX + (player.posX - player.prevPosX) * scaleFactor;
        double testY = player.prevPosY + (player.posY - player.prevPosY) * scaleFactor + 1.62D - player.getYOffset();
        double testZ = player.prevPosZ + (player.posZ - player.prevPosZ) * scaleFactor;
        Vec3d testVector = new Vec3d(testX, testY, testZ);
        float var14 = MathHelper.cos(-rotYaw * 0.017453292F - (float) Math.PI);
        float var15 = MathHelper.sin(-rotYaw * 0.017453292F - (float) Math.PI);
        float var16 = -MathHelper.cos(-rotPitch * 0.017453292F);
        float vectorY = MathHelper.sin(-rotPitch * 0.017453292F);
        float vectorX = var15 * var16;
        float vectorZ = var14 * var16;
        double reachLength = 5.0D;
        Vec3d testVectorFar = testVector.addVector(vectorX * reachLength, vectorY * reachLength, vectorZ * reachLength);
        RayTraceResult testHitPosition = world.rayTraceBlocks(testVector, testVectorFar, true);

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
                AxisAlignedBB entityBB = testEntity.getEntityBoundingBox().expand(bbExpansionSize, bbExpansionSize, bbExpansionSize);
                /**
                 * if an entity is hit, return its position
                 */
                if (entityBB.isVecInside(testVector)) {
                    return new BlockPos(testEntity.posX, testEntity.posY, testEntity.posZ);
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
        return new BlockPos(var42, var43, var44);
    }

    public static BlockPos rotateAroundOrigin(BlockPos pos, int turns) {
        for (int i = 0; i < turns; i++) {
            pos = rotateAroundOrigin(pos);
        }
        return pos;
    }

    /**
     * rotate a position around its origin (0,0,0), in 90' clockwise steps
     */
    public static BlockPos rotateAroundOrigin(BlockPos pos) {
        return new BlockPos(-pos.z, pos.y, pos.x);
    }

    /**
     * checks to see if TEST lies somewhere in the cube bounded by pos1 and pos2
     *
     * @return true if it does
     */
    public static boolean isPositionWithinBounds(BlockPos test, BlockPos pos1, BlockPos pos2) {
        int min;
        int max;
        if (pos1.x < pos2.x) {
            min = pos1.x;
            max = pos2.x;
        } else {
            min = pos2.x;
            max = pos1.x;
        }
        if (test.x >= min && test.x <= max) {
            if (pos1.y < pos2.y) {
                min = pos1.y;
                max = pos2.y;
            } else {
                min = pos2.y;
                max = pos1.y;
            }
            if (test.y >= min && test.y <= max) {
                if (pos1.z < pos2.z) {
                    min = pos1.z;
                    max = pos2.z;
                } else {
                    min = pos2.z;
                    max = pos1.z;
                }
                if (test.z >= min && test.z <= max) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * return a new BlockPos containing the minimum coordinates from the two passed in BlockPos
     */
    public static BlockPos getMin(BlockPos pos1, BlockPos pos2) {
        return new BlockPos(Math.min(pos1.x, pos2.x), Math.min(pos1.y, pos2.y), Math.min(pos1.z, pos2.z));
    }
    /**
     * return a new BlockPos containing the maximum coordinates from the two passed in BlockPos
     */
    public static BlockPos getMax(BlockPos pos1, BlockPos pos2) {
        return new BlockPos(Math.max(pos1.x, pos2.x), Math.max(pos1.y, pos2.y), Math.max(pos1.z, pos2.z));
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

    public static EnumFacing getForgeDirectionFromFacing(int facing) {
        switch (facing) {
            case 0: {
                return EnumFacing.SOUTH;
            }
            case 1: {
                return EnumFacing.WEST;
            }
            case 2: {
                return EnumFacing.NORTH;
            }
            case 3: {
                return EnumFacing.EAST;
            }
            default: {
                return EnumFacing.NORTH;
            }
        }
    }

    /**
     * rotates a given block-position in a given area by the number of turns.  Used by templates
     * to get a relative position.
     */
    public static BlockPos rotateInArea(BlockPos pos, int xSize, int zSize, int turns) {
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
        return new BlockPos(x, pos.y, z);
    }

    public static boolean breakBlockAndDrop(World world, EntityPlayer player, BlockPos pos) {
        return breakBlock(world, player, pos, 0, true);
    }

    public static boolean breakBlock(World world, EntityPlayer player, BlockPos pos, int fortune, boolean doDrop) {
        if (world.isRemote) {
            return false;
        }
        IBlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        if (world.isAirBlock(pos) || state.getBlockHardness(world, pos) < 0) {
            return false;
        }
        if (doDrop) {
            if (!canBreakBlock(world, player, pos, state)) {
                return false;
            }
            block.dropBlockAsItem(world, pos, state, fortune);
        }
        return world.setBlockToAir(pos);
    }


    public static boolean canBreakBlock(World world, EntityPlayer player, BlockPos pos, IBlockState state) {
        return !AWCoreStatics.fireBlockBreakEvents || !MinecraftForge.EVENT_BUS.post(new BlockEvent.BreakEvent(world, pos, state, player));
    }

    public static void notifyBlockUpdate(World world, BlockPos pos) {
        IBlockState state = world.getBlockState(pos);
        world.notifyBlockUpdate(pos, state, state, 3);

    }

    public static void notifyBlockUpdate(TileEntity tile) {
        notifyBlockUpdate(tile.getWorld(), tile.getPos());
    }
}
