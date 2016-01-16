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
package net.shadowmage.ancientwarfare.structure.template.build;

import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.core.util.Zone;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplate;

public class StructureBB extends Zone{

    public StructureBB(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        super(new BlockPosition(minX, minY, minZ), new BlockPosition(maxX, maxY, maxZ));
    }

    public StructureBB(int x, int y, int z, int face, StructureTemplate template) {
        this(x, y, z, face, template.xSize, template.ySize, template.zSize, template.xOffset, template.yOffset, template.zOffset);
    }

    public StructureBB(int x, int y, int z, int face, int xSize, int ySize, int zSize, int xOffset, int yOffset, int zOffset) {
        /**
         * we simply take the clicked on position
         * and walk left/forward/down by the structure offsets
         */
        BlockPosition c1 = new BlockPosition(x, y, z).moveLeft(face, xOffset).moveForward(face, zOffset).moveUp(-yOffset);
        /**
         * the second corner starts as a copy of the first corner
         * which then walks right, backwards, and up to arrive at the actual second corner
         */
        BlockPosition c2 = c1.moveRight(face, xSize - 1).moveForward(face, -(zSize - 1)).moveUp(ySize - 1);
        /**
         * finally, set the min/max of this BB to the min/max of the two corners
         */
        this.min = BlockTools.getMin(c1, c2);
        this.max = BlockTools.getMax(c1, c2);
    }

    public StructureBB(BlockPosition pos1, BlockPosition pos2) {
        super(pos1, pos2);
    }

    private StructureBB(){

    }

    @Override
    public String toString() {
        return min.toString() + " : " + max.toString();
    }

    /**
     * can be used to contract by specifying negative amounts...
     */
    public StructureBB expand(int x, int y, int z) {
        min = min.sub(new BlockPosition(x, y, z));
        max = max.offset(x, y, z);
        return this;
    }

    public StructureBB offset(int x, int y, int z) {
        min = min.offset(x, y, z);
        max = max.offset(x, y, z);
        return this;
    }

    public int getXSize() {
        return max.x - min.x + 1;
    }

    public int getZSize() {
        return max.z - min.z + 1;
    }

    public int getCenterX() {
        return min.x + (getXSize() / 2) - 1;
    }

    public int getCenterZ() {
        return min.z + (getZSize() / 2) - 1;
    }

    /**
     * 0-- z++==forward x++==left
     * 1-- x--==forward z++==left
     * 2-- z--==forward x--==left
     * 3-- x++==forward z--==left
     */
    public StructureBB getFrontCorners(int face, BlockPosition min, BlockPosition max) {
        min = getFLCorner(face, min);
        max = getFRCorner(face, max);
        int minX = Math.min(min.x, max.x);
        int maxX = Math.max(min.x, max.x);
        int minZ = Math.min(min.z, max.z);
        int maxZ = Math.max(min.z, max.z);
        StructureBB result = new StructureBB();
        result.min = new BlockPosition(minX, min.y, minZ);
        result.max = new BlockPosition(maxX, max.y, maxZ);
        return result;
    }

    public StructureBB getLeftCorners(int face, BlockPosition min, BlockPosition max) {
        min = getFLCorner(face, min);
        max = getRLCorner(face, max);
        int minX = Math.min(min.x, max.x);
        int maxX = Math.max(min.x, max.x);
        int minZ = Math.min(min.z, max.z);
        int maxZ = Math.max(min.z, max.z);
        StructureBB result = new StructureBB();
        result.min = new BlockPosition(minX, min.y, minZ);
        result.max = new BlockPosition(maxX, max.y, maxZ);
        return result;
    }

    public StructureBB getRearCorners(int face, BlockPosition min, BlockPosition max) {
        min = getRLCorner(face, min);
        max = getRRCorner(face, max);
        int minX = Math.min(min.x, max.x);
        int maxX = Math.max(min.x, max.x);
        int minZ = Math.min(min.z, max.z);
        int maxZ = Math.max(min.z, max.z);
        StructureBB result = new StructureBB();
        result.min = new BlockPosition(minX, min.y, minZ);
        result.max = new BlockPosition(maxX, max.y, maxZ);
        return result;
    }

    public StructureBB getRightCorners(int face, BlockPosition min, BlockPosition max) {
        min = getFRCorner(face, min);
        max = getRRCorner(face, max);
        int minX = Math.min(min.x, max.x);
        int maxX = Math.max(min.x, max.x);
        int minZ = Math.min(min.z, max.z);
        int maxZ = Math.max(min.z, max.z);
        StructureBB result = new StructureBB();
        result.min = new BlockPosition(minX, min.y, minZ);
        result.max = new BlockPosition(maxX, max.y, maxZ);
        return result;
    }

    private BlockPosition getFLCorner(int face, BlockPosition out) {
        switch (face) {
            case 0:
                return new BlockPosition(max.x, min.y, min.z);

            case 1:
                return new BlockPosition(max.x, min.y, max.z);

            case 2:
                return new BlockPosition(min.x, min.y, max.z);

            case 3:
                return min;
        }
        return out;
    }

    private BlockPosition getFRCorner(int face, BlockPosition out) {
        switch (face) {
            case 0:
                return min;

            case 1:
                return new BlockPosition(max.x, min.y, min.z);

            case 2:
                return new BlockPosition(max.x, min.y, max.z);

            case 3:
                return new BlockPosition(min.x, min.y, max.z);
        }
        return out;
    }

    public BlockPosition getRLCorner(int face, BlockPosition out) {
        switch (face) {
            case 0:
                return new BlockPosition(max.x, min.y, max.z);

            case 1:
                return new BlockPosition(min.x, min.y, max.z);

            case 2:
                return min;

            case 3:
                return new BlockPosition(max.x, min.y, min.z);
        }
        return out;
    }

    private BlockPosition getRRCorner(int face, BlockPosition out) {
        switch (face) {
            case 0:
                return new BlockPosition(min.x, min.y, max.z);

            case 1:
                return min;

            case 2:
                return new BlockPosition(max.x, min.y, min.z);

            case 3:
                return new BlockPosition(max.x, min.y, max.z);
        }
        return out;
    }

    public StructureBB copy() {
        return new StructureBB(min.copy(), max.copy());
    }
}
