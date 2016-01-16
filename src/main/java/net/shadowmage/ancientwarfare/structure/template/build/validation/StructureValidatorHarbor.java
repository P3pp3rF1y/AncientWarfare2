/**
 Copyright 2012-2013 John Cummens (aka Shadowmage, Shadowmage4513)
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
package net.shadowmage.ancientwarfare.structure.template.build.validation;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.structure.block.BlockDataManager;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplate;
import net.shadowmage.ancientwarfare.structure.template.build.StructureBB;
import net.shadowmage.ancientwarfare.structure.world_gen.WorldStructureGenerator;

import java.util.HashSet;
import java.util.Set;

public class StructureValidatorHarbor extends StructureValidator {

    BlockPosition testMin = new BlockPosition();
    BlockPosition testMax = new BlockPosition();

    Set<String> validTargetBlocks;
    Set<String> validTargetBlocksSide;
    Set<String> validTargetBlocksRear;

    public StructureValidatorHarbor() {
        super(StructureValidationType.HARBOR);
        validTargetBlocks = new HashSet<String>();
        validTargetBlocksSide = new HashSet<String>();
        validTargetBlocksRear = new HashSet<String>();
        validTargetBlocks.addAll(WorldStructureGenerator.defaultTargetBlocks);
        validTargetBlocksSide.addAll(WorldStructureGenerator.defaultTargetBlocks);
        validTargetBlocksRear.add(BlockDataManager.INSTANCE.getNameForBlock(Blocks.water));
        validTargetBlocksRear.add(BlockDataManager.INSTANCE.getNameForBlock(Blocks.flowing_water));
        validTargetBlocksSide.add(BlockDataManager.INSTANCE.getNameForBlock(Blocks.water));
        validTargetBlocksSide.add(BlockDataManager.INSTANCE.getNameForBlock(Blocks.flowing_water));
    }

    @Override
    protected void setDefaultSettings(StructureTemplate template) {

    }

    @Override
    public boolean shouldIncludeForSelection(World world, int x, int y, int z, int face, StructureTemplate template) {
        /**
         * testing that front target position is valid block
         * then test back target position to ensure that it has water at same level
         * or at an acceptable level difference
         */
        Block block = world.getBlock(x, y - 1, z);
        if (block != null && validTargetBlocks.contains(BlockDataManager.INSTANCE.getNameForBlock(block))) {
            testMin = new BlockPosition(x, y, z).moveForward(face, template.zOffset);
            int by = WorldStructureGenerator.getTargetY(world, testMin.x, testMin.z, false);
            if (y - by > getMaxFill()) {
                return false;
            }
            block = world.getBlock(testMin.x, by, testMin.z);
            if (block == Blocks.water || block == Blocks.flowing_water) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int getAdjustedSpawnY(World world, int x, int y, int z, int face, StructureTemplate template, StructureBB bb) {
        testMin = new BlockPosition(x, y, z).moveForward(face, template.zOffset);
        return WorldStructureGenerator.getTargetY(world, testMin.x, testMin.z, false) + 1;
    }

    @Override
    public boolean validatePlacement(World world, int x, int y, int z, int face, StructureTemplate template, StructureBB bb) {
        int bx, bz;

        int minY = getMinY(template, bb);
        int maxY = getMaxY(template, bb);
        StructureBB temp = bb.getFrontCorners(face, testMin, testMax);
        testMin = temp.min;
        testMax = temp.max;
        for (bx = testMin.x; bx <= testMax.x; bx++) {
            for (bz = testMin.z; bz <= testMax.z; bz++) {
                if (!validateBlockHeightAndType(world, bx, bz, minY, maxY, false, validTargetBlocks)) {
                    return false;
                }
            }
        }

        temp = bb.getRearCorners(face, testMin, testMax);
        testMin = temp.min;
        testMax = temp.max;
        for (bx = testMin.x; bx <= testMax.x; bx++) {
            for (bz = testMin.z; bz <= testMax.z; bz++) {
                if (!validateBlockHeightAndType(world, bx, bz, minY, maxY, false, validTargetBlocksRear)) {
                    return false;
                }
            }
        }

        temp = bb.getRightCorners(face, testMin, testMax);
        testMin = temp.min;
        testMax = temp.max;
        for (bx = testMin.x; bx <= testMax.x; bx++) {
            for (bz = testMin.z; bz <= testMax.z; bz++) {
                if (!validateBlockHeightAndType(world, bx, bz, minY, maxY, false, validTargetBlocksSide)) {
                    return false;
                }
            }
        }

        temp = bb.getLeftCorners(face, testMin, testMax);
        testMin = temp.min;
        testMax = temp.max;
        for (bx = testMin.x; bx <= testMax.x; bx++) {
            for (bz = testMin.z; bz <= testMax.z; bz++) {
                if (!validateBlockHeightAndType(world, bx, bz, minY, maxY, false, validTargetBlocksSide)) {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public void preGeneration(World world, BlockPosition pos, int face, StructureTemplate template, StructureBB bb) {
        prePlacementBorder(world, template, bb);
    }

    @Override
    public void handleClearAction(World world, int x, int y, int z, StructureTemplate template, StructureBB bb) {
        if (y >= bb.min.y + template.yOffset) {
            super.handleClearAction(world, x, y, z, template, bb);
        } else {
            world.setBlock(x, y, z, Blocks.water);
        }
    }

}
