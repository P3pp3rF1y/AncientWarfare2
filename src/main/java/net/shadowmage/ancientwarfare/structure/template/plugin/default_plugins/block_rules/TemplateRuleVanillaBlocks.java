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
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.structure.api.IStructureBuilder;
import net.shadowmage.ancientwarfare.structure.api.TemplateRuleBlock;
import net.shadowmage.ancientwarfare.structure.block.BlockDataManager;

import javax.annotation.Nonnull;

public class TemplateRuleVanillaBlocks extends TemplateRuleBlock {

    public String blockName;
    public Block block;
    public int meta;
    public int buildPass = 0;

    /*
     * constructor for dynamic construction.  passed world and coords so that the rule can handle its own logic internally
     */
    public TemplateRuleVanillaBlocks(World world, BlockPos pos, Block block, int meta, int turns) {
        super(world, pos, block, meta, turns);
        this.blockName = BlockDataManager.INSTANCE.getNameForBlock(block);
        this.block = block;
        this.meta = BlockDataManager.INSTANCE.getRotatedMeta(block, meta, turns);
        this.buildPass = BlockDataManager.INSTANCE.getPriorityForBlock(block);
    }

    public TemplateRuleVanillaBlocks() {

    }

    @Override
    public void handlePlacement(World world, int turns, BlockPos pos, IStructureBuilder builder) {
        int localMeta = BlockDataManager.INSTANCE.getRotatedMeta(block, this.meta, turns);
        builder.placeBlock(pos, block, localMeta, buildPass);
    }

    @Override
    public boolean shouldReuseRule(World world, Block block, int meta, int turns, BlockPos pos) {
        return block != null && block == this.block && BlockDataManager.INSTANCE.getRotatedMeta(block, meta, turns) == this.meta;
    }

    @Override
    public void addResources(NonNullList<ItemStack> resources) {
        if (block == null || block == Blocks.AIR) {
            return;
        }

        @Nonnull ItemStack stack = BlockDataManager.INSTANCE.getInventoryStackForBlock(block, meta);
        if (stack.isEmpty()) {
            throw new IllegalArgumentException("Could not create item for block: " + block + " (lookup name: " + blockName + ") meta: " + meta);
        }
        if (!stack.isEmpty()) {
            resources.add(stack);
        }
    }

    @Override
    public boolean shouldPlaceOnBuildPass(World world, int turns, BlockPos pos, int buildPass) {
        return buildPass == this.buildPass;
    }

    @Override
    public String toString() {
        return String.format("Vanilla Block Rule id: %s meta: %s buildPass: %s", blockName, meta, buildPass);
    }

    @Override
    public void writeRuleData(NBTTagCompound tag) {
        tag.setString("blockName", blockName);
        tag.setInteger("meta", meta);
        tag.setInteger("buildPass", buildPass);
    }

    @Override
    public void parseRuleData(NBTTagCompound tag) {
        this.blockName = tag.getString("blockName");
        this.block = BlockDataManager.INSTANCE.getBlockForName(blockName);
        this.meta = tag.getInteger("meta");
        this.buildPass = tag.getInteger("buildPass");
    }

}
