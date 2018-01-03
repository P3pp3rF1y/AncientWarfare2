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

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.util.StringTools;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplate;
import net.shadowmage.ancientwarfare.structure.template.build.StructureBB;
import net.shadowmage.ancientwarfare.structure.world_gen.NoGenWorld;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;

public class StructureValidatorSky extends StructureValidator {

    int minGenerationHeight;
    int maxGenerationHeight;
    int minFlyingHeight;

    public StructureValidatorSky() {
        super(StructureValidationType.SKY);
    }

    @Override
    protected void readFromLines(List<String> lines) {
        for (String line : lines) {
            if (startLow(line, "mingenerationheight=")) {
                minGenerationHeight = StringTools.safeParseInt("=", line);
            } else if (startLow(line, "maxgenerationheight=")) {
                maxGenerationHeight = StringTools.safeParseInt("=", line);
            } else if (startLow(line, "minflyingheight=")) {
                minFlyingHeight = StringTools.safeParseInt("=", line);
            }
        }
    }

    @Override
    protected void write(BufferedWriter out) throws IOException {
        out.write("minGenerationHeight=" + minGenerationHeight);
        out.newLine();
        out.write("maxGenerationHeight=" + maxGenerationHeight);
        out.newLine();
        out.write("minFlyingHeight=" + minFlyingHeight);
        out.newLine();
    }

    @Override
    protected void setDefaultSettings(StructureTemplate template) {

    }

    @Override
    public boolean shouldIncludeForSelection(NoGenWorld world, int x, int y, int z, EnumFacing face, StructureTemplate template) {
        int remainingHeight = world.getActualHeight() - minFlyingHeight - (template.ySize - template.yOffset);
        return y < remainingHeight;
    }

    @Override
    public int getAdjustedSpawnY(NoGenWorld world, int x, int y, int z, EnumFacing face, StructureTemplate template, StructureBB bb) {
        int range = maxGenerationHeight - minGenerationHeight + 1;
        return y + minFlyingHeight + world.rand.nextInt(range);
    }

    @Override
    public boolean validatePlacement(NoGenWorld world, int x, int y, int z, EnumFacing face, StructureTemplate template, StructureBB bb) {
        int maxY = minGenerationHeight - minFlyingHeight;
        return validateBorderBlocks(world, template, bb, 0, maxY, false);
    }

    @Override
    public void preGeneration(World world, BlockPos pos, EnumFacing face, StructureTemplate template, StructureBB bb, int minX, int minZ, int maxX, int maxZ) {

    }
}
