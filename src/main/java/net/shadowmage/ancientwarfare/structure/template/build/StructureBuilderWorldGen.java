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
package net.shadowmage.ancientwarfare.structure.template.build;

import cpw.mods.fml.common.eventhandler.Event.Result;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.terraingen.BiomeEvent;
import net.minecraftforge.event.terraingen.BiomeEvent.GetVillageBlockID;
import net.minecraftforge.event.terraingen.BiomeEvent.GetVillageBlockMeta;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplate;
import net.shadowmage.ancientwarfare.structure.template.build.validation.StructureValidationType;

public class StructureBuilderWorldGen extends StructureBuilder {

    public StructureBuilderWorldGen(World world, StructureTemplate template, int face, int x, int y, int z) {
        super(world, template, face, x, y, z);
    }

    @Override
    public void placeBlock(int x, int y, int z, Block block, int meta, int priority) {
        if (template.getValidationSettings().isBlockSwap()) {
            BiomeGenBase biome = world.getBiomeGenForCoords(x, z);
            BiomeEvent.GetVillageBlockID evt1 = new GetVillageBlockID(biome, block, meta);
            MinecraftForge.EVENT_BUS.post(evt1);
            if (evt1.getResult() == Result.DENY && evt1.replacement != block) {
                block = evt1.replacement;
            } else {
                block = getBiomeSpecificBlock(block, meta, biome);
            }
            BiomeEvent.GetVillageBlockMeta evt2 = new GetVillageBlockMeta(biome, block, meta);
            MinecraftForge.EVENT_BUS.post(evt2);
            if (evt2.getResult() == Result.DENY) {
                meta = evt2.replacement;
            } else {
                meta = getBiomeSpecificBlockMetadata(block, meta, biome);
            }
        }
        super.placeBlock(x, y, z, block, meta, priority);
    }

    protected Block getBiomeSpecificBlock(Block par1, int par2, BiomeGenBase biome) {
        if (biome == BiomeGenBase.desert || biome == BiomeGenBase.desertHills || biome.topBlock == Blocks.sand) {
            if (par1 == Blocks.log) {
                return Blocks.sandstone;
            }

            if (par1 == Blocks.cobblestone) {
                return Blocks.sandstone;
            }

            if (par1 == Blocks.planks) {
                return Blocks.sandstone;
            }

            if (par1 == Blocks.oak_stairs) {
                return Blocks.sandstone_stairs;
            }

            if (par1 == Blocks.stone_stairs) {
                return Blocks.sandstone_stairs;
            }

            if (par1 == Blocks.gravel) {
                return Blocks.sandstone;
            }
        }

        return par1;
    }

    /**
     * Gets the replacement block metadata for the current biome
     */
    protected int getBiomeSpecificBlockMetadata(Block par1, int par2, BiomeGenBase biome) {
        if (biome == BiomeGenBase.desert || biome == BiomeGenBase.desertHills || biome.topBlock == Blocks.sand) {
            if (par1 == Blocks.log) {
                return 0;
            }
            if (par1 == Blocks.cobblestone) {
                return 0;
            }
            if (par1 == Blocks.planks) {
                return 2;
            }
        }
        return par2;
    }

    @Override
    public void instantConstruction() {
        template.getValidationSettings().preGeneration(world, buildOrigin.x, buildOrigin.y, buildOrigin.z, buildFace, template, bb);
        super.instantConstruction();
        if (template.getValidationSettings().validationType == StructureValidationType.GROUND) {
            BiomeGenBase biome = world.getBiomeGenForCoords(buildOrigin.x, buildOrigin.z);
            if (biome != null && biome.getEnableSnow()) {
                sprinkleSnow();
            }
        }
    }

    private void sprinkleSnow() {
        Block block;
        int y = 0;
        int border = template.getValidationSettings().getBorderSize();
        BlockPosition p1 = bb.min.copy();
        BlockPosition p2 = bb.max.copy();
        p1.offset(-border, 0, -border);
        p2.offset(border, 0, border);
        for (int x = p1.x; x <= p2.x; x++) {
            for (int z = p1.z; z <= p2.z; z++) {
                y = p2.y;
                while (y >= p1.y) {
                    block = world.getBlock(x, y, z);
                    if (block != null && block != Blocks.air && block.isSideSolid(world, x, y, z, ForgeDirection.UP)) {
                        y++;
                        if (world.isAirBlock(x, y, z) && world.canBlockSeeTheSky(x, y, z)) {
                            world.setBlock(x, y, z, Blocks.snow_layer);
                        }
                        break;
                    }
                    y--;
                }
            }
        }
    }

}
