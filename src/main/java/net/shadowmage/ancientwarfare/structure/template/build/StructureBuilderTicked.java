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

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.structure.api.TemplateRule;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplate;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplateManager;

public class StructureBuilderTicked extends StructureBuilder {

    public boolean invalid = false;
    private boolean hasClearedArea;
    private int clearX, clearY, clearZ;

    public StructureBuilderTicked(World world, StructureTemplate template, int face, int x, int y, int z) {
        super(world, template, face, x, y, z);
        clearX = bb.min.x;
        clearY = bb.min.y;
        clearZ = bb.min.z;
    }

    public StructureBuilderTicked()//nbt-constructor
    {

    }

    public void tick() {
        if (!hasClearedArea) {
            while (!breakClearTargetBlock()) {
                if (!incrementClear()) {
                    hasClearedArea = true;
                    break;
                }
            }
            if (!incrementClear()) {
                hasClearedArea = true;
            }
        } else if (!this.isFinished()) {
            boolean placed = false;
            while (!placed && !this.isFinished()) {
                TemplateRule rule = template.getRuleAt(currentX, currentY, currentZ);
                if (rule == null || !rule.shouldPlaceOnBuildPass(world, turns, destination.x, destination.y, destination.z, currentPriority)) {
                    increment();//skip that position, was either air/null rule, or could not be placed on current pass, auto-increment to next
                } else//place it...
                {
                    placed = true;
                    this.placeCurrentPosition(rule);
                }
            }
            increment();//finally, increment to next position (will trigger isFinished if actually done, has no problems if already finished)
        }
    }

    protected boolean breakClearTargetBlock() {
        return BlockTools.breakBlockAndDrop(world, clearX, clearY, clearZ, 0);
    }

    protected boolean incrementClear() {
        clearX++;
        if (clearX > bb.max.x) {
            clearX = bb.min.x;
            clearZ++;
            if (clearZ > bb.max.z) {
                clearY++;
                if (clearY > bb.max.y) {
                    return false;
                }
            }
        }
        return true;
    }

    //break/drop any existing blocks from ticked builder
    protected void tryBreakTargetBlock() {
        BlockTools.breakBlockAndDrop(world, destination.x, destination.y, destination.z, 0);
    }

    public void setWorld(World world)//should be called on first-update of the TE (after its world is set)
    {
        this.world = world;
    }

    public World getWorld() {
        return world;
    }

    public void readFromNBT(NBTTagCompound tag)//should be called immediately after construction
    {
        String name = tag.getString("name");
        StructureTemplate template = StructureTemplateManager.INSTANCE.getTemplate(name);
        if (template != null) {
            this.template = template;
            this.currentX = tag.getInteger("x");
            this.currentY = tag.getInteger("y");
            this.currentZ = tag.getInteger("z");
            this.clearX = tag.getInteger("cx");
            this.clearY = tag.getInteger("cy");
            this.clearZ = tag.getInteger("cz");
            this.hasClearedArea = tag.getBoolean("cleared");
            this.turns = tag.getInteger("turns");
            this.buildFace = tag.getInteger("buildFace");
            this.maxPriority = tag.getInteger("maxPriority");
            this.currentPriority = tag.getInteger("currentPriority");

            this.bb = new StructureBB(new BlockPosition(tag.getCompoundTag("bbMin")), new BlockPosition(tag.getCompoundTag("bbMax")));
            this.buildOrigin = new BlockPosition(tag.getCompoundTag("buildOrigin"));
            this.incrementDestination();
        } else {
            invalid = true;
        }
    }

    public void writeToNBT(NBTTagCompound tag) {
        tag.setString("name", template.name);
        tag.setInteger("face", buildFace);
        tag.setInteger("turns", turns);
        tag.setInteger("maxPriority", maxPriority);
        tag.setInteger("currentPriority", currentPriority);
        tag.setInteger("x", currentX);
        tag.setInteger("y", currentY);
        tag.setInteger("z", currentZ);
        tag.setInteger("cx", clearX);
        tag.setInteger("cy", clearY);
        tag.setInteger("cz", clearZ);
        tag.setBoolean("cleared", hasClearedArea);

        NBTTagCompound originTag = new NBTTagCompound();
        buildOrigin.writeToNBT(originTag);
        tag.setTag("buildOrigin", originTag);


        NBTTagCompound bbMin = new NBTTagCompound();
        NBTTagCompound bbMax = new NBTTagCompound();
        bb.min.writeToNBT(bbMin);
        bb.max.writeToNBT(bbMax);
        tag.setTag("bbMin", bbMin);
        tag.setTag("bbMax", bbMax);
    }

    /**
     * @return
     */
    public StructureTemplate getTemplate() {
        return template;
    }

}
