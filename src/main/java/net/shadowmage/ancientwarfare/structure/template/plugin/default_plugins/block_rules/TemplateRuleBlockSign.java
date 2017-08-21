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
package net.shadowmage.ancientwarfare.structure.template.plugin.default_plugins.block_rules;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.structure.api.IStructureBuilder;
import net.shadowmage.ancientwarfare.structure.block.BlockDataManager;

public class TemplateRuleBlockSign extends TemplateRuleVanillaBlocks {

    public String signContents[];

    public TemplateRuleBlockSign(World world, int x, int y, int z, Block block, int meta, int turns) {
        super(world, x, y, z, block, meta, turns);
        TileEntitySign te = (TileEntitySign) world.getTileEntity(pos);
        signContents = new String[te.signText.length];
        for (int i = 0; i < signContents.length; i++) {
            signContents[i] = te.signText[i];
        }
        if (block == Blocks.standing_sign) {
            this.meta = (meta + 4 * turns) % 16;
        }
    }

    public TemplateRuleBlockSign() {
    }

    @Override
    public void handlePlacement(World world, int turns, BlockPos pos, IStructureBuilder builder) {
        Block block = Block.getBlockFromName(blockName);
//  Block block = wall? Blocks.wall_sign : Blocks.standing_sign;//BlockDataManager.getBlockByName(blockName);
        int meta = 0;
        if (block == Blocks.STANDING_SIGN) {
            meta = (this.meta + 4 * turns) % 16;
        } else {
            meta = BlockDataManager.INSTANCE.getRotatedMeta(block, this.meta, turns);
        }
        if (world.setBlock(x, y, z, block, meta, 2)) {
            TileEntitySign te = (TileEntitySign) world.getTileEntity(pos);
            if (te != null) {
                for (int i = 0; i < this.signContents.length; i++) {
                    te.signText[i] = this.signContents[i];
                }
            }
            BlockTools.notifyBlockUpdate(world, pos);
        }
    }

    @Override
    public boolean shouldReuseRule(World world, Block block, int meta, int turns, int x, int y, int z) {
        return false;
    }

    @Override
    public void writeRuleData(NBTTagCompound tag) {
        super.writeRuleData(tag);
        for (int i = 0; i < 4; i++) {
            tag.setString("signContents" + i, signContents[i]);
        }
    }

    @Override
    public void parseRuleData(NBTTagCompound tag) {
        super.parseRuleData(tag);
        this.signContents = new String[4];
        for (int i = 0; i < 4; i++) {
            this.signContents[i] = tag.getString("signContents" + i);
        }
    }

}
