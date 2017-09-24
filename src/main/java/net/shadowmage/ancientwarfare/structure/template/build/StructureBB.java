/*
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
package net.shadowmage.ancientwarfare.structure.template.build;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.core.util.Zone;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplate;

public class StructureBB extends Zone{

    public StructureBB(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        super(new BlockPos(minX, minY, minZ), new BlockPos(maxX, maxY, maxZ));
    }

    public StructureBB(BlockPos pos, EnumFacing face, StructureTemplate template) {
        this(pos, face, template.xSize, template.ySize, template.zSize, template.xOffset, template.yOffset, template.zOffset);
    }

    public StructureBB(BlockPos pos, EnumFacing face, int xSize, int ySize, int zSize, int xOffset, int yOffset, int zOffset) {
        /*
         * we simply take the clicked on position
         * and walk left/forward/down by the structure offsets
         */
        BlockPos c1 = pos.offset(face.rotateYCCW(), xOffset).offset(face, zOffset).up(-yOffset);
        /*
         * the second corner starts as a copy of the first corner
         * which then walks right, backwards, and up to arrive at the actual second corner
         */
        BlockPos c2 = c1.offset(face.rotateY(), xSize - 1).offset(face, -(zSize - 1)).up(ySize - 1);
        /*
         * finally, set the min/max of this BB to the min/max of the two corners
         */
        this.min = BlockTools.getMin(c1, c2);
        this.max = BlockTools.getMax(c1, c2);
    }

    public StructureBB(BlockPos pos1, BlockPos pos2) {
        super(pos1, pos2);
    }

    private StructureBB(){

    }

    @Override
    public String toString() {
        return min.toString() + " : " + max.toString();
    }

    /*
     * can be used to contract by specifying negative amounts...
     */
    public StructureBB expand(int x, int y, int z) {
        min = min.subtract(new Vec3i(x, y, z));
        max = max.add(x, y, z);
        return this;
    }

    public StructureBB add(int x, int y, int z) {
        min = min.add(x, y, z);
        max = max.add(x, y, z);
        return this;
    }

    public int getXSize() {
        return max.getX() - min.getX() + 1;
    }

    public int getZSize() {
        return max.getZ() - min.getZ() + 1;
    }

    public int getCenterX() {
        return min.getX() + (getXSize() / 2) - 1;
    }

    public int getCenterZ() {
        return min.getZ() + (getZSize() / 2) - 1;
    }

    /*
     * 0-- z++==forward x++==left
     * 1-- x--==forward z++==left
     * 2-- z--==forward x--==left
     * 3-- x++==forward z--==left
     */
    public StructureBB getFrontCorners(EnumFacing face, BlockPos min, BlockPos max) {
        min = getFLCorner(face, min);
        max = getFRCorner(face, max);
        int minX = Math.min(min.getX(), max.getX());
        int maxX = Math.max(min.getX(), max.getX());
        int minZ = Math.min(min.getZ(), max.getZ());
        int maxZ = Math.max(min.getZ(), max.getZ());
        StructureBB result = new StructureBB();
        result.min = new BlockPos(minX, min.getY(), minZ);
        result.max = new BlockPos(maxX, max.getY(), maxZ);
        return result;
    }

    public StructureBB getLeftCorners(EnumFacing face, BlockPos min, BlockPos max) {
        min = getFLCorner(face, min);
        max = getRLCorner(face, max);
        int minX = Math.min(min.getX(), max.getX());
        int maxX = Math.max(min.getX(), max.getX());
        int minZ = Math.min(min.getZ(), max.getZ());
        int maxZ = Math.max(min.getZ(), max.getZ());
        StructureBB result = new StructureBB();
        result.min = new BlockPos(minX, min.getY(), minZ);
        result.max = new BlockPos(maxX, max.getY(), maxZ);
        return result;
    }

    public StructureBB getRearCorners(EnumFacing face, BlockPos min, BlockPos max) {
        min = getRLCorner(face, min);
        max = getRRCorner(face, max);
        int minX = Math.min(min.getX(), max.getX());
        int maxX = Math.max(min.getX(), max.getX());
        int minZ = Math.min(min.getZ(), max.getZ());
        int maxZ = Math.max(min.getZ(), max.getZ());
        StructureBB result = new StructureBB();
        result.min = new BlockPos(minX, min.getY(), minZ);
        result.max = new BlockPos(maxX, max.getY(), maxZ);
        return result;
    }

    public StructureBB getRightCorners(EnumFacing face, BlockPos min, BlockPos max) {
        min = getFRCorner(face, min);
        max = getRRCorner(face, max);
        int minX = Math.min(min.getX(), max.getX());
        int maxX = Math.max(min.getX(), max.getX());
        int minZ = Math.min(min.getZ(), max.getZ());
        int maxZ = Math.max(min.getZ(), max.getZ());
        StructureBB result = new StructureBB();
        result.min = new BlockPos(minX, min.getY(), minZ);
        result.max = new BlockPos(maxX, max.getY(), maxZ);
        return result;
    }

    private BlockPos getFLCorner(EnumFacing face, BlockPos out) {
        switch (face) {
            case SOUTH:
                return new BlockPos(max.getX(), min.getY(), min.getZ());

            case WEST:
                return new BlockPos(max.getX(), min.getY(), max.getZ());

            case NORTH:
                return new BlockPos(min.getX(), min.getY(), max.getZ());

            case EAST:
                return min;
        }
        return out;
    }

    private BlockPos getFRCorner(EnumFacing face, BlockPos out) {
        switch (face) {
            case SOUTH:
                return min;

            case WEST:
                return new BlockPos(max.getX(), min.getY(), min.getZ());

            case NORTH:
                return new BlockPos(max.getX(), min.getY(), max.getZ());

            case EAST:
                return new BlockPos(min.getX(), min.getY(), max.getZ());
        }
        return out;
    }

    public BlockPos getRLCorner(EnumFacing face, BlockPos out) {
        switch (face) {
            case SOUTH:
                return new BlockPos(max.getX(), min.getY(), max.getZ());

            case WEST:
                return new BlockPos(min.getX(), min.getY(), max.getZ());

            case NORTH:
                return min;

            case EAST:
                return new BlockPos(max.getX(), min.getY(), min.getZ());
        }
        return out;
    }

    private BlockPos getRRCorner(EnumFacing face, BlockPos out) {
        switch (face) {
            case SOUTH:
                return new BlockPos(min.getX(), min.getY(), max.getZ());

            case WEST:
                return min;

            case NORTH:
                return new BlockPos(max.getX(), min.getY(), min.getZ());

            case EAST:
                return new BlockPos(max.getX(), min.getY(), max.getZ());
        }
        return out;
    }

    public StructureBB copy() {
        return new StructureBB(min, max);
    }
}
