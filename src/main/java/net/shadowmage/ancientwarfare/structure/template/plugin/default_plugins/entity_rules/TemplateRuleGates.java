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
package net.shadowmage.ancientwarfare.structure.template.plugin.default_plugins.entity_rules;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.structure.api.IStructureBuilder;
import net.shadowmage.ancientwarfare.structure.api.TemplateRuleEntity;
import net.shadowmage.ancientwarfare.structure.entity.EntityGate;
import net.shadowmage.ancientwarfare.structure.gates.types.Gate;
import net.shadowmage.ancientwarfare.structure.template.build.StructureBuildingException;
import net.shadowmage.ancientwarfare.structure.template.build.StructureBuildingException.EntityPlacementException;

public class TemplateRuleGates extends TemplateRuleEntity {

    String gateType;
    EnumFacing orientation;
    BlockPos pos1 = BlockPos.ORIGIN;
    BlockPos pos2 = BlockPos.ORIGIN;

    /*
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

        this.pos1 = BlockTools.rotateAroundOrigin(gate.pos1.add(-x, -y, -z), turns);
        this.pos2 = BlockTools.rotateAroundOrigin(gate.pos2.add(-x, -y, -z), turns);
        this.orientation = EnumFacing.HORIZONTALS[(gate.gateOrientation.ordinal() + turns) % 4];
        this.gateType = Gate.getGateNameFor(gate);
    }

    public TemplateRuleGates() {

    }

    @Override
    public void handlePlacement(World world, int turns, BlockPos pos, IStructureBuilder builder) throws EntityPlacementException {
        BlockPos p1 = BlockTools.rotateAroundOrigin(pos1, turns).add(pos);
        BlockPos p2 = BlockTools.rotateAroundOrigin(pos2, turns).add(pos);

        BlockPos min = BlockTools.getMin(p1, p2);
        BlockPos max = BlockTools.getMax(p1, p2);
        for (int x1 = min.getX(); x1 <= max.getX(); x1++) {
            for (int y1 = min.getY(); y1 <= max.getY(); y1++) {
                for (int z1 = min.getZ(); z1 <= max.getZ(); z1++) {
                    world.setBlockToAir(new BlockPos(x1, y1, z1));
                }
            }
        }

        EntityGate gate = Gate.constructGate(world, p1, p2, Gate.getGateByName(gateType), EnumFacing.VALUES[((orientation.ordinal() + turns) % 4)]);
        if (gate == null) {
            throw new StructureBuildingException.EntityPlacementException("Could not create gate for type: " + gateType);
        }
        world.spawnEntity(gate);
    }

    @Override
    public void parseRuleData(NBTTagCompound tag) {
        gateType = tag.getString("gateType");
        orientation = EnumFacing.VALUES[tag.getByte("orientation")];
        pos1 = getBlockPosFromNBT(tag.getCompoundTag("pos1"));
        pos2 = getBlockPosFromNBT(tag.getCompoundTag("pos2"));
    }

    @Override
    public void writeRuleData(NBTTagCompound tag) {
        tag.setString("gateType", gateType);
        tag.setByte("orientation", (byte) orientation.ordinal());
        tag.setTag("pos1", writeBlockPosToNBT(new NBTTagCompound(), pos1));
        tag.setTag("pos2", writeBlockPosToNBT(new NBTTagCompound(), pos2));
    }

    @Override
    public void addResources(NonNullList<ItemStack> resources) {
        resources.add(Gate.getItemToConstruct(Gate.getGateByName(gateType).getGlobalID()));
    }

    @Override
    public boolean shouldPlaceOnBuildPass(World world, int turns, BlockPos pos, int buildPass) {
        return buildPass == 3;
    }

}
