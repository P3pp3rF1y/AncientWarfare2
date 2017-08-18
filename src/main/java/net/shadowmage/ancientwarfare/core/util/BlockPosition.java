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
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;

public final class BlockPosition {
    //TODO replace with BlockPos
    public final int x, y, z;

    public BlockPosition() {
        this(0, 0, 0);
    }

    public BlockPosition(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * @param dir MC direction (0=south, 1=west, 2=north, 3=east)
     */
    public BlockPosition(int x, int y, int z, int dir) {
        this.y = y;
        if (dir == 0) {
            this.x = x;
            this.z = z + 1;
        } else if (dir == 1) {
            this.x = x - 1;
            this.z = z;
        } else if (dir == 2) {
            this.x = x;
            this.z = z - 1;
        } else {
            this.x = x + 1;
            this.z = z;
        }
    }

    public BlockPosition(double x, double y, double z) {
        this.x = MathHelper.floor_double(x);
        this.y = MathHelper.floor_double(y);
        this.z = MathHelper.floor_double(z);
    }

    public BlockPosition(NBTTagCompound tag) {
        this.x = tag.getInteger("x");
        this.y = tag.getInteger("y");
        this.z = tag.getInteger("z");
    }

    public BlockPosition(BlockPosition pos) {
        this.x = pos.x;
        this.y = pos.y;
        this.z = pos.z;
    }

    /**
     * offset towards the side clicked on.<br>
     * 0=down<br>
     * 1=up<br>
     * 2=north<br>
     * 3=south<br>
     * 4=west<br>
     * 5=east<br>
     */
    public BlockPosition(RayTraceResult pos) {
        switch (pos.sideHit) {
            case 0:
                this.x = pos.blockX;
                this.y = pos.blockY - 1;
                this.z = pos.blockZ;
                break;
            case 1:
                this.x = pos.blockX;
                this.y = pos.blockY + 1;
                this.z = pos.blockZ;
                break;
            case 2:
                this.x = pos.blockX;
                this.y = pos.blockY;
                this.z = pos.blockZ - 1;
                break;
            case 3:
                this.x = pos.blockX;
                this.y = pos.blockY;
                this.z = pos.blockZ + 1;
                break;
            case 4:
                this.x = pos.blockX - 1;
                this.y = pos.blockY;
                this.z = pos.blockZ;
                break;
            default:
                this.x = pos.blockX + 1;
                this.y = pos.blockY;
                this.z = pos.blockZ;
        }
    }

    /**
     * return the distance of the CENTER of this block from the input position
     */
    public final float getCenterDistanceFrom(double x, double y, double z) {
        return Trig.getDistance(x, y, z, this.x + 0.5d, this.y, this.z + 0.5d);
    }

    public final NBTTagCompound writeToNBT(NBTTagCompound tag) {
        tag.setInteger("x", x);
        tag.setInteger("y", y);
        tag.setInteger("z", z);
        return tag;
    }

    /**
     * offsets THIS blockPosition by the passed in offset
     */
    public final BlockPosition offsetBy(BlockPosition offsetVector) {
        return new BlockPosition(this.x + offsetVector.x, this.y + offsetVector.y, this.z + offsetVector.z);
    }

    public final BlockPosition offset(int x, int y, int z) {
        return new BlockPosition(this.x + x, this.y + y, this.z + z);
    }

    public final BlockPosition sub(BlockPosition sub){
        return new BlockPosition(this.x - sub.x, this.y - sub.y, this.z - sub.z);
    }

    /**
     * moves the blocks position right by the input amount, relative to the input direction
     *
     * @param facing the direction that is 'forward'
     */
    public final BlockPosition moveRight(int facing, int amt) {
        return this.moveForward(BlockTools.turnRight(facing), amt);
    }

    /**
     * moves the blocks position backwards the input amount, relative to the input direction
     *
     * @param facing the direction that is 'forward'
     */
    public final BlockPosition moveBack(int facing, int amt) {
        return this.moveForward(BlockTools.turnAround(facing), amt);
    }

    /**
     * moves the blocks position left the input amount, relative to the input direction
     *
     * @param facing the direction that is 'forward'
     */
    public final BlockPosition moveLeft(int facing, int amt) {
        return this.moveForward(BlockTools.turnLeft(facing), amt);
    }

    /**
     * moves the blocks position forwards the input amount, relative to the input direction
     *
     * @param facing the direction that is 'forward' MC direction (0=south, 1=west, 2=north, 3=east)
     * @param amt       how far to move in the given direction
     */
    public final BlockPosition moveForward(int facing, int amt) {
        if (facing == 0) {
            return new BlockPosition(x, y, z + amt);
        } else if (facing == 1) {
            return new BlockPosition(x - amt, y, z);
        } else if (facing == 2) {
            return new BlockPosition(x, y, z - amt);
        } else {
            return new BlockPosition(x + amt, y, z);
        }
    }

    public final BlockPosition moveUp(int amt){
        return new BlockPosition(x, y + amt, z);
    }

    public final boolean equals(BlockPosition pos) {
        return pos != null && this.x == pos.x && this.y == pos.y && this.z == pos.z;
    }

    public final BlockPosition copy() {
        return new BlockPosition(this.x, this.y, this.z);
    }

    @Override
    public final String toString() {
        return "X:" + this.x + " Y:" + this.y + " Z:" + this.z;
    }


    public Block get(int dimension){
        World world = AncientWarfareCore.proxy.getWorld(dimension);
        if(world==null)
            return null;
        return world.getBlock(x, y, z);
    }

    public BlockPosition getMin(BlockPosition position){
        int minX = position.x < this.x ? position.x : this.x;
        int minY = position.y < this.y ? position.y : this.y;
        int minZ = position.z < this.z ? position.z : this.z;
        return new BlockPosition(minX, minY, minZ);
    }

    public BlockPosition getMax(BlockPosition position){
        int maxX = position.x > this.x ? position.x : this.x;
        int maxY = position.y > this.y ? position.y : this.y;
        int maxZ = position.z > this.z ? position.z : this.z;
        return new BlockPosition(maxX, maxY, maxZ);
    }

    @Override
    public final int hashCode() {
        final int prime = 16777619;
        long result = prime ^ x;
        result = (prime * result) ^ z;
        result = (prime * result) ^ y + 17;
        return ((Long)result).hashCode();
    }

    @Override
    public final boolean equals(Object obj) {
        return this == obj || obj instanceof BlockPosition && this.equals((BlockPosition) obj);
    }
}

