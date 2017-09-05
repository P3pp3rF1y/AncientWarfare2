/*
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
import net.minecraft.world.biome.BiomeGenBase;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.structure.block.BlockDataManager;
import net.shadowmage.ancientwarfare.structure.config.AWStructureStatics;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplate;
import net.shadowmage.ancientwarfare.structure.template.build.StructureBB;
import net.shadowmage.ancientwarfare.structure.world_gen.WorldStructureGenerator;

import java.util.Set;

public class StructureValidatorGround extends StructureValidator {

    public StructureValidatorGround() {
        super(StructureValidationType.GROUND);
    }

    @Override
    public boolean shouldIncludeForSelection(World world, int x, int y, int z, int face, StructureTemplate template) {
        Block block = world.getBlock(x, y - 1, z);
        Set<String> validTargetBlocks = getTargetBlocks();
        String name = BlockDataManager.INSTANCE.getNameForBlock(block);
        if (block == null || !validTargetBlocks.contains(name)) {
            AWLog.logDebug("Rejecting due to target block mismatch of: " + name + " at: " + x + "," + y + "," + z + " Valid blocks are: " + validTargetBlocks);
            return false;
        }
        return true;
    }

    @Override
    public boolean validatePlacement(World world, int x, int y, int z, int face, StructureTemplate template, StructureBB bb) {
        int minY = getMinY(template, bb);
        int maxY = getMaxY(template, bb);
        return validateBorderBlocks(world, template, bb, minY, maxY, false);
    }

    @Override
    public void preGeneration(World world, BlockPos pos, int face, StructureTemplate template, StructureBB bb) {
        prePlacementBorder(world, template, bb);
        prePlacementUnderfill(world, template, bb);
    }

    @Override
    public void postGeneration(World world, BlockPos origin, StructureBB bb) {
        BiomeGenBase biome = world.getBiomeGenForCoords(origin.x, origin.z);
        if (biome != null && biome.getEnableSnow()) {
            WorldStructureGenerator.sprinkleSnow(world, bb, getBorderSize());
        }
    }

    @Override
    protected void borderLeveling(World world, int x, int z, StructureTemplate template, StructureBB bb) {
        if (getMaxLeveling() <= 0) {
            return;
        }
        int topFilledY = WorldStructureGenerator.getTargetY(world, x, z, true);
        int step = WorldStructureGenerator.getStepNumber(x, z, bb.min.x, bb.max.x, bb.min.z, bb.max.z);
        for (int y = bb.min.y + template.yOffset + step; y <= topFilledY; y++) {
            handleClearAction(world, x, y, z, template, bb);
        }
        BiomeGenBase biome = world.getBiomeGenForCoords(x, z);
        Block fillBlock = Blocks.GRASS;
        if (biome != null && biome.topBlock != null) {
            fillBlock = biome.topBlock;
        }
        int y = bb.min.y + template.yOffset + step - 1;
        Block block = world.getBlock(x, y, z);
        if (block != null && block != Blocks.AIR && block != Blocks.FLOWING_WATER && block != Blocks.WATER && !AWStructureStatics.skippableBlocksContains(block)) {
            world.setBlock(x, y, z, fillBlock);
        }

        int skipCount = 0;
        for (int y1 = y + 1; y1 < world.getHeight(); y1++)//lazy clear block handling
        {
            block = world.getBlock(x, y1, z);
            if (block == Blocks.AIR) {
                skipCount++;
                if (skipCount >= 10)//exit out if 10 blocks are found that are not clearable
                {
                    break;
                }
                continue;
            }
            skipCount = 0;//if we didn't skip this block, reset skipped count
            if (AWStructureStatics.skippableBlocksContains(block)) {
                world.setBlockToAir(x, y1, z);
            }
        }
    }

}
