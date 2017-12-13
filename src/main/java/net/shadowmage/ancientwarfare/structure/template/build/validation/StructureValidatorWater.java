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
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplate;
import net.shadowmage.ancientwarfare.structure.template.build.StructureBB;
import net.shadowmage.ancientwarfare.structure.world_gen.NoGenWorld;

public class StructureValidatorWater extends StructureValidator {

    public StructureValidatorWater() {
        super(StructureValidationType.WATER);
    }

    @Override
    public boolean shouldIncludeForSelection(NoGenWorld world, int x, int y, int z, EnumFacing face, StructureTemplate template) {
        Block block = world.getBlockState(new BlockPos(x, y - 1, z)).getBlock();
        return block == Blocks.WATER || block == Blocks.FLOWING_WATER;
    }

    @Override
    public boolean validatePlacement(World world, int x, int y, int z, EnumFacing face, StructureTemplate template, StructureBB bb) {
        int minY = getMinY(template, bb);
        return validateBorderBlocks(world, template, bb, 0, minY, true);
    }

    @Override
    public void preGeneration(World world, BlockPos pos, EnumFacing face, StructureTemplate template, StructureBB bb) {

    }

    @Override
    public void handleClearAction(World world, BlockPos pos, StructureTemplate template, StructureBB bb) {
        if (pos.getY() < bb.min.getY() + template.yOffset) {
            world.setBlockState(pos, Blocks.WATER.getDefaultState());
        } else {
            super.handleClearAction(world, pos, template, bb);
        }
    }
}
