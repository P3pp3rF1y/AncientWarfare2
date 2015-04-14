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
package net.shadowmage.ancientwarfare.structure.template.plugin.default_plugins.entity_rules;

import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.structure.api.IStructureBuilder;
import net.shadowmage.ancientwarfare.structure.api.TemplateRuleEntity;
import net.shadowmage.ancientwarfare.structure.entity.EntityGate;
import net.shadowmage.ancientwarfare.structure.gates.types.Gate;
import net.shadowmage.ancientwarfare.structure.template.build.StructureBuildingException;
import net.shadowmage.ancientwarfare.structure.template.build.StructureBuildingException.EntityPlacementException;

import java.util.List;

public class TemplateRuleGates extends TemplateRuleEntity {

    String gateType;
    int orientation;
    BlockPosition pos1 = new BlockPosition();
    BlockPosition pos2 = new BlockPosition();

    /**
     * scanner-constructor.  called when scanning an entity.
     *
     * @param world  the world containing the scanned area
     * @param entity the entity being scanned
     * @param turns  how many 90' turns to rotate entity for storage in template
     * @param x      world x-coord of the enitty (floor(posX)
     * @param y      world y-coord of the enitty (floor(posY)
     * @param z      world z-coord of the enitty (floor(posZ)
     */
    public TemplateRuleGates(World world, Entity entity, int turns, int x, int y, int z) {
        super(world, entity, turns, x, y, z);
        EntityGate gate = (EntityGate) entity;
        BlockPosition pos1 = gate.pos1.copy();
        pos1.offset(-x, -y, -z);
        BlockPosition pos2 = gate.pos2.copy();
        pos2.offset(-x, -y, -z);

        BlockTools.rotateAroundOrigin(pos1, turns);
        BlockTools.rotateAroundOrigin(pos2, turns);
        this.pos1 = pos1;
        this.pos2 = pos2;
        this.orientation = (gate.gateOrientation + turns) % 4;
        this.gateType = Gate.getGateNameFor(gate);
    }

    public TemplateRuleGates() {

    }

    @Override
    public void handlePlacement(World world, int turns, int x, int y, int z, IStructureBuilder builder) throws EntityPlacementException {
        BlockPosition p1 = pos1.copy();
        BlockPosition p2 = pos2.copy();

        BlockTools.rotateAroundOrigin(p1, turns);
        BlockTools.rotateAroundOrigin(p2, turns);

        p1.offset(x, y, z);
        p2.offset(x, y, z);

        BlockPosition min = BlockTools.getMin(p1, p2);
        BlockPosition max = BlockTools.getMax(p1, p2);
        for (int x1 = min.x; x1 <= max.x; x1++) {
            for (int y1 = min.y; y1 <= max.y; y1++) {
                for (int z1 = min.z; z1 <= max.z; z1++) {
                    world.setBlock(x1, y1, z1, Blocks.air);
                }
            }
        }

        EntityGate gate = Gate.constructGate(world, p1, p2, Gate.getGateByName(gateType), (byte) ((orientation + turns) % 4));
        if (gate == null) {
            throw new StructureBuildingException.EntityPlacementException("Could not create gate for type: " + gateType);
        }
        world.spawnEntityInWorld(gate);
    }

    @Override
    public void parseRuleData(NBTTagCompound tag) {
        gateType = tag.getString("gateType");
        orientation = tag.getByte("orientation");
        NBTTagCompound pTag = tag.getCompoundTag("pos1");
        pos1.read(pTag);
        pTag = tag.getCompoundTag("pos2");
        pos2.read(pTag);
    }

    @Override
    public void writeRuleData(NBTTagCompound tag) {
        tag.setString("gateType", gateType);
        tag.setByte("orientation", (byte) orientation);
        NBTTagCompound pTag = new NBTTagCompound();
        pos1.writeToNBT(pTag);
        tag.setTag("pos1", pTag);
        pTag = new NBTTagCompound();
        pos2.writeToNBT(pTag);
        tag.setTag("pos2", pTag);
    }

    @Override
    public void addResources(List<ItemStack> resources) {
        resources.add(Gate.getItemToConstruct(Gate.getGateByName(gateType).getGlobalID()));
    }

    @Override
    public boolean shouldPlaceOnBuildPass(World world, int turns, int x, int y, int z, int buildPass) {
        return buildPass == 3;
    }

}
