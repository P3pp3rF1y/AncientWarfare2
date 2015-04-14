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
import net.shadowmage.ancientwarfare.core.util.StringTools;
import net.shadowmage.ancientwarfare.structure.block.BlockDataManager;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplate;
import net.shadowmage.ancientwarfare.structure.template.build.StructureBB;
import net.shadowmage.ancientwarfare.structure.world_gen.WorldStructureGenerator;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;
import java.util.Set;

public class StructureValidatorIsland extends StructureValidator {

    int minWaterDepth;
    int maxWaterDepth;

    public StructureValidatorIsland() {
        super(StructureValidationType.ISLAND);
    }

    @Override
    protected void readFromLines(List<String> lines) {
        for (String line : lines) {
            if (line.toLowerCase().startsWith("minwaterdepth=")) {
                minWaterDepth = StringTools.safeParseInt("=", line);
            } else if (line.toLowerCase().startsWith("maxwaterdepth=")) {
                maxWaterDepth = StringTools.safeParseInt("=", line);
            }
        }
    }

    @Override
    protected void write(BufferedWriter out) throws IOException {
        out.write("minWaterDepth=" + minWaterDepth);
        out.newLine();
        out.write("minWaterDepth=" + maxWaterDepth);
        out.newLine();
    }

    @Override
    protected void setDefaultSettings(StructureTemplate template) {
//  this.validTargetBlocks.addAll(WorldStructureGenerator.defaultTargetBlocks);
        this.minWaterDepth = template.yOffset / 2;
        this.maxWaterDepth = template.yOffset;
    }

    @Override
    public boolean shouldIncludeForSelection(World world, int x, int y, int z, int face, StructureTemplate template) {
        int water = 0;
        int startY = y - 1;
        y = WorldStructureGenerator.getTargetY(world, x, z, true) + 1;
        water = startY - y + 1;
        if (water < minWaterDepth || water > maxWaterDepth) {
            return false;
        }
        return true;
    }

    @Override
    public boolean validatePlacement(World world, int x, int y, int z, int face, StructureTemplate template, StructureBB bb) {
        int minY = y - maxWaterDepth;
        int maxY = y - minWaterDepth;
        return validateBorderBlocks(world, template, bb, minY, maxY, true);
    }

    @Override
    public void preGeneration(World world, int x, int y, int z, int face, StructureTemplate template, StructureBB bb) {
        Block block;
        Set<String> validTargetBlocks = getTargetBlocks();
        for (int bx = bb.min.x; bx <= bb.max.x; bx++) {
            for (int bz = bb.min.z; bz <= bb.max.z; bz++) {
                for (int by = bb.min.y - 1; by > 0; by--) {
                    block = world.getBlock(bx, by, bz);
                    if (block != null && validTargetBlocks.contains(BlockDataManager.INSTANCE.getNameForBlock(block))) {
                        break;
                    } else {
                        world.setBlock(bx, by, bz, Blocks.dirt);
                    }
                }
            }
        }
    }

    @Override
    public void handleClearAction(World world, int x, int y, int z, StructureTemplate template, StructureBB bb) {
        int maxWaterY = bb.min.y + template.yOffset - 1;
        if (y <= maxWaterY) {
            world.setBlock(x, y, z, Blocks.water);
        } else {
            world.setBlock(x, y, z, Blocks.air);
        }
    }

}
