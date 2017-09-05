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
package net.shadowmage.ancientwarfare.structure.api;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.structure.api.TemplateParsingException.TemplateRuleParsingException;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public abstract class TemplateRuleEntity extends TemplateRule {

    private int x, y, z;

    /*
     * Called by reflection
     * @param world
     * @param entity
     * @param turns
     * @param x
     * @param y
     * @param z
     */
    public TemplateRuleEntity(World world, Entity entity, int turns, int x, int y, int z) {

    }
    /*
     * Called by reflection
     */
    public TemplateRuleEntity() {

    }

    public final void writeRule(BufferedWriter out) throws IOException {
        out.write("position=" + NBTTools.getCSVStringForArray(new int[]{x, y, z}));
        out.newLine();
        super.writeRule(out);
    }

    public final void parseRule(int ruleNumber, List<String> lines) throws TemplateRuleParsingException {
        this.ruleNumber = ruleNumber;
        for (String line : lines) {
            if (line.toLowerCase(Locale.ENGLISH).startsWith("position=")) {
                int[] pos = NBTTools.safeParseIntArray("=", line);
                x = pos[0];
                y = pos[1];
                z = pos[2];
                break;
            }
        }
        NBTTagCompound tag = readTag(lines);
        parseRuleData(tag);
    }

    public final void setPosition(BlockPos pos){
        this.x = pos.x;
        this.y = pos.y;
        this.z = pos.z;
    }

    public final BlockPos getPosition(){
        return new BlockPos(x, y, z);
    }

}
