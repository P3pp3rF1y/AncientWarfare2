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
package net.shadowmage.ancientwarfare.structure.template.build;

import net.minecraft.block.Block;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.BiomeEvent;
import net.minecraftforge.event.terraingen.BiomeEvent.GetVillageBlockID;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplate;

public class StructureBuilderWorldGen extends StructureBuilder {
    private boolean containsStructurePart = false;
    private int overallMinX;
    private int overallMinZ;
    private int overallMaxX;
    private int overallMaxZ;


    public StructureBuilderWorldGen(World world, StructureTemplate template, EnumFacing face, BlockPos pos, StructureBB bb, int minX, int minZ, int maxX, int maxZ) {
        super(world, template, face, pos, bb, Math.max(minX, 0), Math.max(minZ, 0), Math.min(maxX, template.xSize - 1), Math.min(maxZ, template.zSize - 1));
        containsStructurePart = (minX > 0 && minX < template.xSize && minZ > 0 && minZ < template.zSize) || (maxX > 0 && maxX < template.xSize && maxZ > 0 && maxZ < template.zSize);
        overallMinX = minX;
        overallMinZ = minZ;
        overallMaxX = maxX;
        overallMaxZ = maxZ;
        currentPass = -1;
    }
    public StructureBuilderWorldGen(World world, StructureTemplate template, EnumFacing face, BlockPos pos) {
        super(world, template, face, pos);
        containsStructurePart = true;
    }

    @Override
    public void placeBlock(BlockPos pos, Block block, int meta, int priority) {
        if (template.getValidationSettings().isBlockSwap()) {
            Biome biome = world.getBiome(pos);
            BiomeEvent.GetVillageBlockID evt1 = new GetVillageBlockID(biome, block.getStateFromMeta(meta));
            MinecraftForge.EVENT_BUS.post(evt1);
            if (evt1.getResult() == Result.DENY && evt1.getReplacement().getBlock() != block) {
                block = evt1.getReplacement().getBlock();
            } else {
                block = getBiomeSpecificBlock(block, meta, biome);
            }
            BiomeEvent.GetVillageBlockID evt2 = new GetVillageBlockID(biome, block.getStateFromMeta(meta));
            MinecraftForge.EVENT_BUS.post(evt2);
            if (evt2.getResult() == Result.DENY) {
                meta = evt2.getReplacement().getBlock().getMetaFromState(evt2.getReplacement());
            } else {
                meta = getBiomeSpecificBlockMetadata(block, meta, biome);
            }
        }
        super.placeBlock(pos, block, meta, priority);
    }

    protected Block getBiomeSpecificBlock(Block par1, int par2, Biome biome) {
        if (biome == Biomes.DESERT || biome == Biomes.DESERT_HILLS || biome.topBlock == Blocks.SAND) {
            if (par1 == Blocks.LOG || par1 == Blocks.COBBLESTONE || par1 == Blocks.PLANKS || par1 == Blocks.GRAVEL) {
                return Blocks.SANDSTONE;
            }

            if (par1 == Blocks.OAK_STAIRS || par1 == Blocks.STONE_STAIRS) {
                return Blocks.SANDSTONE_STAIRS;
            }
        }

        return par1;
    }

    /*
     * Gets the replacement block metadata for the current biome
     */
    protected int getBiomeSpecificBlockMetadata(Block par1, int par2, Biome biome) {
        if (biome == Biomes.DESERT || biome == Biomes.DESERT_HILLS || biome.topBlock == Blocks.SAND) {
            if (par1 == Blocks.LOG || par1 == Blocks.COBBLESTONE) {
                return 0;
            }
            if (par1 == Blocks.PLANKS) {
                return 2;
            }
        }
        return par2;
    }

    @Override
    public void instantConstruction() {
        template.getValidationSettings().preGeneration(world, buildOrigin, buildFace, template, bb, bb.min.getX() + overallMinX, bb.min.getZ() + overallMinZ, bb.min.getX() + overallMaxX, bb.min.getZ() + overallMaxZ);
        if (containsStructurePart) {
            super.instantConstruction();
        }
        template.getValidationSettings().postGeneration(world, buildOrigin, bb, bb.min.getX() + overallMinX, bb.min.getZ() + overallMinZ, bb.min.getX() + overallMaxX, bb.min.getZ() + overallMaxZ);
    }

}
