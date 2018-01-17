/*
 Copyright 2012-2014 John Cummens (aka Shadowmage, Shadowmage4513)
 This software is distributed under the terms of the GNU General Public License.
 Please see COPYING for precise license information.

 This file is part of Ancient Warfare 2.

 Ancient Warfare 2 is free software: you can redistribute it and/or modify
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
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.structure.api.IStructureBuilder;
import net.shadowmage.ancientwarfare.structure.block.BlockDataManager;

public class TemplateRuleBlockDoors extends TemplateRuleVanillaBlocks {

    byte sideFlag = 0;
    boolean isTop = false;

    public TemplateRuleBlockDoors(World world, BlockPos pos, Block block, int meta, int turns) {
        super(world, pos, block, meta, turns);
        if (world.getBlockState(pos.up()) == block) {
            IBlockState state = world.getBlockState(pos.up());
            sideFlag = (byte) state.getBlock().getMetaFromState(state);
        }
    }

    public TemplateRuleBlockDoors() {
    }

    @Override
    public void handlePlacement(World world, int turns, BlockPos pos, IStructureBuilder builder) {
        Block block = BlockDataManager.INSTANCE.getBlockForName(blockName);
        int localMeta = BlockDataManager.INSTANCE.getRotatedMeta(block, this.meta, turns);
        if (world.getBlockState(pos.down()).getBlock() != block)//this is the bottom door block, call placeDoor from our block...
        {
            world.setBlockState(pos, block.getStateFromMeta(localMeta), 2);
            world.setBlockState(pos.up(), block.getStateFromMeta(sideFlag == 0 ? 8 : sideFlag), 2);
        }
    }

    @Override
    public void writeRuleData(NBTTagCompound tag) {
        tag.setString("blockName", blockName);
        tag.setInteger("meta", meta);
        tag.setInteger("buildPass", buildPass);
        tag.setByte("sideFlag", sideFlag);
    }

    @Override
    public void parseRuleData(NBTTagCompound tag) {
        this.blockName = tag.getString("blockName");
        this.meta = tag.getInteger("meta");
        this.buildPass = tag.getInteger("buildPass");
        this.sideFlag = tag.getByte("sideFlag");
    }

    @Override
    public boolean shouldReuseRule(World world, Block block, int meta, int turns, BlockPos pos) {
        IBlockState state = world.getBlockState(pos.up());
        Block block1 = state.getBlock();
        return block1 != null && blockName.equals(BlockDataManager.INSTANCE.getNameForBlock(block1)) && block1.getMetaFromState(state) == sideFlag;
    }

    @Override
    public void addResources(NonNullList<ItemStack> resources) {
        if (sideFlag > 0) {
            super.addResources(resources);
        }
    }

}
