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

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;

public final class BlockPosition {

    public int x;
    public int y;
    public int z;

    public BlockPosition() {

    }

    public BlockPosition(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public BlockPosition(double x, double y, double z) {
        this.x = MathHelper.floor_double(x);
        this.y = MathHelper.floor_double(y);
        this.z = MathHelper.floor_double(z);
    }

    public BlockPosition(NBTTagCompound tag) {
        read(tag);
    }

    public BlockPosition(BlockPosition pos) {
        this.x = pos.x;
        this.y = pos.y;
        this.z = pos.z;
    }

    public final BlockPosition reassign(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    public final BlockPosition read(NBTTagCompound tag) {
        this.x = tag.getInteger("x");
        this.y = tag.getInteger("y");
        this.z = tag.getInteger("z");
        return this;
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
        this.x += offsetVector.x;
        this.y += offsetVector.y;
        this.z += offsetVector.z;
        return this;
    }

    public final BlockPosition offset(int x, int y, int z) {
        this.x += x;
        this.y += y;
        this.z += z;
        return this;
    }

    /**
     * moves the blocks position right by the input amount, relative to the input direction
     *
     * @param facing the direction that is 'forward'
     */
    public final void moveRight(int facing, int amt) {
        this.offsetForHorizontalDirection(BlockTools.turnRight(facing), amt);
    }

    /**
     * moves the blocks position backwards the input amount, relative to the input direction
     *
     * @param facing the direction that is 'forward'
     */
    public final void moveBack(int facing, int amt) {
        this.offsetForHorizontalDirection(BlockTools.turnAround(facing), amt);
    }

    /**
     * moves the blocks position left the input amount, relative to the input direction
     *
     * @param facing the direction that is 'forward'
     */
    public final void moveLeft(int facing, int amt) {
        this.offsetForHorizontalDirection(BlockTools.turnLeft(facing), amt);
    }

    /**
     * moves the blocks position forwards the input amount, relative to the input direction
     *
     * @param facing the direction that is 'forward'
     */
    public final void moveForward(int facing, int amt) {
        this.offsetForHorizontalDirection(facing, amt);
    }

    /**
     * @param direction MC direction (0=south, 1=west, 2=north, 3=east)
     */
    public final void offsetForHorizontalDirection(int direction) {
        if (direction == 0) {
            this.z++;
        } else if (direction == 1) {
            this.x--;
        } else if (direction == 2) {
            this.z--;
        } else if (direction == 3) {
            this.x++;
        }
    }

    /**
     * @param direction MC direction (0=south, 1=west, 2=north, 3=east)
     * @param amt       how far to move in the given direction
     */
    public final void offsetForHorizontalDirection(int direction, int amt) {
        if (direction == 0) {
            this.z += amt;
        } else if (direction == 1) {
            this.x -= amt;
        } else if (direction == 2) {
            this.z -= amt;
        } else if (direction == 3) {
            this.x += amt;
        }
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

    /**
     * offset towards the side clicked on.<br>
     * 0=down<br>
     * 1=up<br>
     * 2=north<br>
     * 3=south<br>
     * 4=east<br>
     * 5=west<br>
     */
    public final void offsetForMCSide(int mcSide) {
        switch (mcSide) {
            case 0:
                --y;
                break;
            case 1:
                ++y;
                break;
            case 2:
                --z;
                break;
            case 3:
                ++z;
                break;
            case 4:
                --x;
                break;
            case 5:
                ++x;
        }
    }

    @Override
    public final int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + x;
        result = prime * result + y;
        result = prime * result + z;
        return result;
    }

    @Override
    public final boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        BlockPosition other = (BlockPosition) obj;
        return x == other.x && y == other.y && z == other.z;
    }

}

