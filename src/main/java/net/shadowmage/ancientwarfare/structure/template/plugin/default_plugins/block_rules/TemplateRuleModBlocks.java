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
package net.shadowmage.ancientwarfare.structure.template.plugin.default_plugins.block_rules;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.structure.api.IStructureBuilder;
import net.shadowmage.ancientwarfare.structure.api.TemplateRuleBlock;
import net.shadowmage.ancientwarfare.structure.block.BlockDataManager;

public class TemplateRuleModBlocks extends TemplateRuleBlock {

    public String blockName;
    public int meta;

    public TemplateRuleModBlocks(World world, BlockPos pos, Block block, int meta, int turns) {
        super(world, pos, block, meta, turns);
        this.blockName = BlockDataManager.INSTANCE.getNameForBlock(block);
        this.meta = meta;
    }

    public TemplateRuleModBlocks() {

    }

    @Override
    public boolean shouldReuseRule(World world, Block block, int meta, int turns, BlockPos pos) {
        return BlockDataManager.INSTANCE.getNameForBlock(block).equals(blockName) && meta == this.meta;
    }

    @Override
    public void handlePlacement(World world, int turns, BlockPos pos, IStructureBuilder builder) {
        Block block = BlockDataManager.INSTANCE.getBlockForName(blockName);
        world.setBlockState(pos, block.getStateFromMeta(meta), 3);
    }

    @Override
    public void writeRuleData(NBTTagCompound tag) {
        tag.setString("blockName", blockName);
        tag.setInteger("meta", meta);
    }

    @Override
    public void parseRuleData(NBTTagCompound tag) {
        blockName = tag.getString("blockName");
        meta = tag.getInteger("meta");
    }

    @Override
    public void addResources(NonNullList<ItemStack> resources) {
        /*
         * TODO
         */
    }

    @Override
    public boolean shouldPlaceOnBuildPass(World world, int turns, BlockPos pos, int buildPass) {
        return buildPass == 0;
    }

}
