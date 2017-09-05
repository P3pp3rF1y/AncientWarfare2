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
package net.shadowmage.ancientwarfare.structure.template;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;

import java.util.ArrayList;
import java.util.List;

public class StructureTemplateClient {

    public final String name;
    public final int xSize, ySize, zSize, xOffset, yOffset, zOffset;
    public final List<ItemStack> resourceList = new ArrayList<>();
    public boolean survival;

    public StructureTemplateClient(StructureTemplate template) {
        this.name = template.name;
        this.xSize = template.xSize;
        this.ySize = template.ySize;
        this.zSize = template.zSize;
        this.xOffset = template.xOffset;
        this.yOffset = template.yOffset;
        this.zOffset = template.zOffset;
        this.survival = template.getValidationSettings().isSurvival();
        if (this.survival) {
            resourceList.addAll(template.getResourceList());
        }
    }

    public StructureTemplateClient(String name, int x, int y, int z, int xo, int yo, int zo) {
        if (name == null) {
            throw new IllegalArgumentException("cannot have null name for structure");
        }
        this.name = name;
        this.xSize = x;
        this.ySize = y;
        this.zSize = z;
        this.xOffset = xo;
        this.yOffset = yo;
        this.zOffset = zo;
    }

    public void writeToNBT(NBTTagCompound tag) {
        tag.setString("name", name);
        tag.setInteger("x", xSize);
        tag.setInteger("y", ySize);
        tag.setInteger("z", zSize);
        tag.setInteger("xo", xOffset);
        tag.setInteger("yo", yOffset);
        tag.setInteger("zo", zOffset);
        tag.setBoolean("survival", survival);
        if (survival && !resourceList.isEmpty()) {
            NBTTagList stackList = new NBTTagList();
            NBTTagCompound stackTag;
            for (ItemStack stack : this.resourceList) {
                stackTag = new NBTTagCompound();
                stack.writeToNBT(stackTag);
                stackList.appendTag(stackTag);
            }
            tag.setTag("resourceList", stackList);
        }
    }

    public static StructureTemplateClient readFromNBT(NBTTagCompound tag) {
        String name = tag.getString("name");
        boolean survival = tag.getBoolean("survival");
        int x = tag.getInteger("x");
        int y = tag.getInteger("y");
        int z = tag.getInteger("z");
        int xo = tag.getInteger("xo");
        int yo = tag.getInteger("yo");
        int zo = tag.getInteger("zo");
        StructureTemplateClient template = new StructureTemplateClient(name, x, y, z, xo, yo, zo);
        template.survival = survival;

        if (tag.hasKey("resourceList")) {
            NBTTagList stackList = tag.getTagList("resourceList", Constants.NBT.TAG_COMPOUND);
            NBTTagCompound stackTag;
            @Nonnull ItemStack stack;
            for (int i = 0; i < stackList.tagCount(); i++) {
                stackTag = stackList.getCompoundTagAt(i);
                stack = new ItemStack(stackTag);
                if (!stack.isEmpty()) {
                    template.resourceList.add(stack);
                }
            }
        }
        return template;
    }


}
