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
import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.EntityList;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.structure.api.IStructureBuilder;
import net.shadowmage.ancientwarfare.structure.template.build.StructureBuildingException.EntityPlacementException;

public class TemplateRuleEntityHanging extends TemplateRuleVanillaEntity {

	public NBTTagCompound tag = new NBTTagCompound();
	public EnumFacing direction;

	BlockPos hangTarget = BlockPos.ORIGIN;//cached location for use during placement

	public TemplateRuleEntityHanging(World world, Entity entity, int turns, int x, int y, int z) {
		super(world, entity, turns, x, y, z);
		EntityHanging hanging = (EntityHanging) entity;
		entity.writeToNBT(tag);
		this.direction = EnumFacing.HORIZONTALS[(hanging.facingDirection.ordinal() + turns) % 4];
		tag.removeTag("UUIDMost");
		tag.removeTag("UUIDLeast");
	}

	public TemplateRuleEntityHanging() {

	}

	@Override
	public void handlePlacement(World world, int turns, BlockPos pos, IStructureBuilder builder) throws EntityPlacementException {
		Entity e = EntityList.createEntityByIDFromName(registryName, world);
		if (e == null) {
			throw new EntityPlacementException("Could not create entity for type: " + registryName.toString());
		}
		hangTarget = pos;
		EnumFacing direction = EnumFacing.HORIZONTALS[(this.direction.ordinal() + turns) % 4];
/*
		hangTarget = pos.offset(direction);
*/
		tag.setByte("Facing", (byte) direction.getHorizontalIndex());
		NBTTagList posList = new NBTTagList();
		posList.appendTag(new NBTTagDouble(hangTarget.getX()));
		posList.appendTag(new NBTTagDouble(hangTarget.getY()));
		posList.appendTag(new NBTTagDouble(hangTarget.getZ()));
		tag.setTag("Pos", posList);
		tag.setInteger("TileX", hangTarget.getX());
		tag.setInteger("TileY", hangTarget.getY());
		tag.setInteger("TileZ", hangTarget.getZ());
		e.readFromNBT(tag);
		world.spawnEntity(e);
	}

	@Override
	public void writeRuleData(NBTTagCompound tag) {
		super.writeRuleData(tag);
		tag.setByte("direction", (byte) direction.ordinal());
		tag.setTag("entityData", this.tag);
	}

	@Override
	public void parseRuleData(NBTTagCompound tag) {
		super.parseRuleData(tag);
		this.tag = tag.getCompoundTag("entityData");
		this.direction = EnumFacing.VALUES[tag.getByte("direction")];
	}

}
