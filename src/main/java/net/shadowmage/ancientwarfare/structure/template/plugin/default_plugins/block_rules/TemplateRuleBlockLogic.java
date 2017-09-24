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
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.structure.api.IStructureBuilder;
import net.shadowmage.ancientwarfare.structure.block.BlockDataManager;

public class TemplateRuleBlockLogic extends TemplateRuleVanillaBlocks {

    public NBTTagCompound tag = new NBTTagCompound();

    public TemplateRuleBlockLogic(World world, BlockPos pos, Block block, int meta, int turns) {
        super(world, pos, block, meta, turns);
        TileEntity te = world.getTileEntity(pos);
        if(te!=null) {
            te.writeToNBT(tag);
            tag.removeTag("x");
            tag.removeTag("y");
            tag.removeTag("z");
        }
    }

    public TemplateRuleBlockLogic() {
    }

    @Override
    public void handlePlacement(World world, int turns, BlockPos pos, IStructureBuilder builder) {
        super.handlePlacement(world, turns, pos, builder);
        int localMeta = BlockDataManager.INSTANCE.getRotatedMeta(block, this.meta, turns);
        world.setBlockState(pos, block.getStateFromMeta(localMeta), 3);
        TileEntity te = world.getTileEntity(pos);
        if (te != null) {
            //TODO look into changing this so that the whole TE doesn't need reloading from custom NBT
            tag.setString("id", block.getRegistryName().toString());
            tag.setInteger("x", pos.getX());
            tag.setInteger("y", pos.getY());
            tag.setInteger("z", pos.getZ());
            te.readFromNBT(tag);
        }
    }

    @Override
    public boolean shouldReuseRule(World world, Block block, int meta, int turns, BlockPos pos) {
        return false;
    }

    @Override
    public void writeRuleData(NBTTagCompound tag) {
        super.writeRuleData(tag);
        tag.setTag("teData", this.tag);
    }

    @Override
    public void parseRuleData(NBTTagCompound tag) {
        super.parseRuleData(tag);
        this.tag = tag.getCompoundTag("teData");
    }
}
