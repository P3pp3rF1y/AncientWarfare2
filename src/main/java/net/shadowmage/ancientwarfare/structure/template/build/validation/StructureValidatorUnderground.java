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

import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.core.util.StringTools;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplate;
import net.shadowmage.ancientwarfare.structure.template.build.StructureBB;
import net.shadowmage.ancientwarfare.structure.world_gen.WorldStructureGenerator;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;

public class StructureValidatorUnderground extends StructureValidator {

    int minGenerationDepth;
    int maxGenerationDepth;
    int minOverfill;

    public StructureValidatorUnderground() {
        super(StructureValidationType.UNDERGROUND);
    }

    @Override
    protected void readFromLines(List<String> lines) {
        for (String line : lines) {
            if (startLow(line, "mingenerationdepth=")) {
                minGenerationDepth = StringTools.safeParseInt("=", line);
            } else if (startLow(line, "maxgenerationdepth=")) {
                maxGenerationDepth = StringTools.safeParseInt("=", line);
            } else if (startLow(line, "minoverfill=")) {
                minOverfill = StringTools.safeParseInt("=", line);
            }
        }
    }

    @Override
    protected void write(BufferedWriter out) throws IOException {
        out.write("minGenerationDepth=" + minGenerationDepth);
        out.newLine();
        out.write("maxGenerationDepth=" + maxGenerationDepth);
        out.newLine();
        out.write("minOverfill=" + minOverfill);
        out.newLine();
    }

    @Override
    protected void setDefaultSettings(StructureTemplate template) {

    }

    @Override
    public boolean shouldIncludeForSelection(World world, int x, int y, int z, int face, StructureTemplate template) {
        y = WorldStructureGenerator.getTargetY(world, x, z, true);
        int tHeight = (template.ySize - template.yOffset);
        int low = minGenerationDepth + tHeight + minOverfill;
        return y > low;
    }

    @Override
    public int getAdjustedSpawnY(World world, int x, int y, int z, int face, StructureTemplate template, StructureBB bb) {
        y = WorldStructureGenerator.getTargetY(world, x, z, true);
        int range = maxGenerationDepth - minGenerationDepth + 1;
        int tHeight = (template.ySize - template.yOffset);
        return y - minOverfill - world.rand.nextInt(range) - tHeight;
    }

    @Override
    public boolean validatePlacement(World world, int x, int y, int z, int face, StructureTemplate template, StructureBB bb) {
        int minY = bb.min.y + template.yOffset + minOverfill;
        int topBlockY;
        for (int bx = bb.min.x; bx <= bb.max.x; bx++) {
            for (int bz = bb.min.z; bz <= bb.max.z; bz++) {
                topBlockY = WorldStructureGenerator.getTargetY(world, bx, bz, true);
                if (topBlockY <= minY) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void preGeneration(World world, BlockPosition pos, int face, StructureTemplate template, StructureBB bb) {
//  /**
//   * TODO remove debug stuff
//   */
//  int by = WorldStructureGenerator.getTargetY(world, x, z, false);
//  for(int cy = by; cy<=by+5; cy++)
//    {
//    world.setBlock(x, cy, z, Block.obsidian.blockID);
//    }
    }

}
