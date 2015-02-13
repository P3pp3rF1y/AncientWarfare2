/**
 Copyright 2012 John Cummens (aka Shadowmage, Shadowmage4513)
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
package net.shadowmage.ancientwarfare.structure.tile;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.shadowmage.ancientwarfare.structure.entity.EntityGate;

import java.util.List;
import java.util.UUID;

public class TEGateProxy extends TileEntity {

public class TEGateProxy extends TileEntity {

    private EntityGate owner = null;
    private UUID entityID = null;
    private int noParentTicks = 0;

    public void setOwner(EntityGate gate) {
        this.owner = gate;
        this.entityID = owner.getPersistentID();
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        if (tag.hasKey("msb") && tag.hasKey("lsb")) {
            long msb = tag.getLong("msb");
            long lsb = tag.getLong("lsb");
            entityID = new UUID(msb, lsb);
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        if (this.entityID != null) {
            tag.setLong("msb", entityID.getMostSignificantBits());
            tag.setLong("lsb", entityID.getLeastSignificantBits());
        }
        super.writeToNBT(tag);
    }

    public void onBlockClicked(EntityPlayer player) {
        if (this.owner != null && this.worldObj != null && !this.worldObj.isRemote) {
            this.owner.interactFirst(player);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void updateEntity() {
        super.updateEntity();
        if (this.worldObj == null || this.worldObj.isRemote) {
            return;
        }
        if (this.owner == null && this.entityID != null) {
            this.noParentTicks++;
            List<Entity> entities = this.worldObj.loadedEntityList;
            for (Entity ent : entities) {
                if (ent.getPersistentID() != null && ent.getPersistentID().equals(entityID) && ent instanceof EntityGate) {
                    this.owner = (EntityGate) ent;
                    this.noParentTicks = 0;
                }
            }
        } else if (this.entityID == null) {
            this.noParentTicks++;
        }
        if (this.noParentTicks >= 100 || (owner != null && owner.isDead)) {
            this.worldObj.setBlockToAir(xCoord, yCoord, zCoord);
        }
    }

    @Override
    public boolean canUpdate() {
        return true;
    }

}
