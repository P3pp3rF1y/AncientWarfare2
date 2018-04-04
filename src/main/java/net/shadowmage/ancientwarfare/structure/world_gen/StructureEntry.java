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

package net.shadowmage.ancientwarfare.structure.world_gen;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplate;
import net.shadowmage.ancientwarfare.structure.template.build.StructureBB;

public class StructureEntry {

    public String name;
    private int value;
    public final StructureBB bb;

    public StructureEntry(int x, int y, int z, EnumFacing face, StructureTemplate template) {
        name = template.name;
        bb = new StructureBB(new BlockPos(x, y, z), face, template.xSize, template.ySize, template.zSize, template.xOffset, template.yOffset, template.zOffset);
        value = template.getValidationSettings().getClusterValue();
    }

    public StructureEntry(StructureBB bb, String name, int value) {
        this.name = name;
        this.bb = bb;
        this.value = value;
    }

    public StructureEntry() {
        bb = new StructureBB(BlockPos.ORIGIN, BlockPos.ORIGIN);
    }//NBT constructor

    public void writeToNBT(NBTTagCompound tag) {
        tag.setString("name", name);
        tag.setInteger("value", value);
        tag.setIntArray("bb", new int[] {bb.min.getX(), bb.min.getY(), bb.min.getZ(), bb.max.getX(), bb.max.getY(), bb.max.getZ()});
    }

    public void readFromNBT(NBTTagCompound tag) {
        name = tag.getString("name");
        value = tag.getInteger("value");
        int[] datas = tag.getIntArray("bb");
        if (datas.length >= 6) {
            bb.min = new BlockPos(datas[0], datas[1], datas[2]);
            bb.max = new BlockPos(datas[3], datas[4], datas[5]);
        }
    }

    public String getName() {
        return name;
    }

    public int getValue() {
        return value;
    }

    public StructureBB getBB() {
        return bb;
    }

}
