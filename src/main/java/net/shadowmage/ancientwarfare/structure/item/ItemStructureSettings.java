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
package net.shadowmage.ancientwarfare.structure.item;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public class ItemStructureSettings {

    boolean[] setKeys = new boolean[4];
    BlockPos pos1;
    BlockPos pos2;
    BlockPos key;
    EnumFacing buildFace;
    String name;

    private ItemStructureSettings() {

    }

    /*
     * @param stack to extract the info from
     */
    public static ItemStructureSettings getSettingsFor(ItemStack stack) {
        ItemStructureSettings settings = new ItemStructureSettings();
        NBTTagCompound tag;
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("structData")) {
            tag = stack.getTagCompound().getCompoundTag("structData");
        } else {
            tag = new NBTTagCompound();
        }
        for (int i = 0; i < settings.setKeys.length; i++) {
            settings.setKeys[i] = false;
        }
        if (tag.hasKey("pos1")) {
            settings.pos1 = BlockPos.fromLong(tag.getLong("pos1"));
            settings.setKeys[0] = true;
        }
        if (tag.hasKey("pos2")) {
            settings.pos2 = BlockPos.fromLong(tag.getLong("pos2"));
            settings.setKeys[1] = true;
        }
        if (tag.hasKey("buildKey")) {
            settings.key = BlockPos.fromLong(tag.getCompoundTag("buildKey").getLong("key"));
            settings.setKeys[2] = true;
            settings.buildFace = EnumFacing.VALUES[tag.getCompoundTag("buildKey").getByte("face")];
        }
        if (tag.hasKey("name")) {
            settings.name = tag.getString("name");
            settings.setKeys[3] = true;
        }
        return settings;
    }

    public static void setSettingsFor(ItemStack item, ItemStructureSettings settings) {
        NBTTagCompound tag = new NBTTagCompound();
        if (settings.setKeys[0]) {
            tag.setLong("pos1", settings.pos1.toLong());
        }
        if (settings.setKeys[1]) {
            tag.setLong("pos2", settings.pos2.toLong());
        }
        if (settings.setKeys[2]) {
            NBTTagCompound tag1 = new NBTTagCompound();
            tag1.setByte("face", (byte) settings.buildFace.ordinal());
            tag1.setLong("key", settings.key.toLong());
            tag.setTag("buildKey", tag1);
        }
        if (settings.setKeys[3]) {
            tag.setString("name", settings.name);
        }
        item.setTagInfo("structData", tag);
    }

    public void setPos1(BlockPos pos) {
        pos1 = pos;
        setKeys[0] = true;
    }

    public void setPos2(BlockPos pos) {
        pos2 = pos;
        setKeys[1] = true;
    }

    public void setBuildKey(BlockPos pos, EnumFacing face) {
        key = pos;
        buildFace = face;
        setKeys[2] = true;
    }

    public void setName(String name) {
        this.name = name;
        setKeys[3] = true;
    }

    public boolean hasPos1() {
        return setKeys[0];
    }

    public boolean hasPos2() {
        return setKeys[1];
    }

    public boolean hasBuildKey() {
        return setKeys[2];
    }

    public boolean hasName() {
        return setKeys[3];
    }

    public BlockPos pos1() {
        return pos1;
    }

    public BlockPos pos2() {
        return pos2;
    }

    public BlockPos buildKey() {
        return key;
    }

    public EnumFacing face() {
        return buildFace;
    }

    public String name() {
        return name;
    }

    public void clearSettings() {
        for (int i = 0; i < 3; i++) {
            this.setKeys[i] = false;
        }
    }

}
