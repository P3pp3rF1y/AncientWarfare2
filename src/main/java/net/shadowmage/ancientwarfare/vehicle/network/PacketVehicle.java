/**
 * Copyright 2012 John Cummens (aka Shadowmage, Shadowmage4513)
 * This software is distributed under the terms of the GNU General Public License.
 * Please see COPYING for precise license information.
 * <p>
 * This file is part of Ancient Warfare.
 * <p>
 * Ancient Warfare is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * Ancient Warfare is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with Ancient Warfare.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.shadowmage.ancientwarfare.vehicle.network;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.network.PacketBase;
import net.shadowmage.ancientwarfare.vehicle.entity.VehicleBase;

import javax.annotation.Nullable;

public abstract class PacketVehicle extends PacketBase {
	//TODO call registerPacketType for all packet types

	private int entityID;
	protected VehicleBase vehicle = null;

	public PacketVehicle(VehicleBase vehicle) {
		this.entityID = vehicle.getEntityId();
	}

	@Override
	protected void writeToStream(ByteBuf data) {
		data.writeInt(entityID);
	}

	@Override
	protected void readFromStream(ByteBuf data) {
		entityID = data.readInt();
	}

	@Nullable
	private VehicleBase getVehicle(World world) {
		Entity vehicle = world.getEntityByID(entityID);
		return vehicle instanceof VehicleBase ? (VehicleBase) vehicle : null;
	}

	public void setParams(Entity ent) {
		this.entityID = ent.entityId;
	}

	public void setInputData(NBTTagCompound tag) {
		this.packetData.setTag("input", tag);
	}

	public void setAmmoData(NBTTagCompound tag) {
		this.packetData.setTag("ammo", tag);
	}

	public void setAmmoSelect(NBTTagCompound tag) {
		this.packetData.setTag("ammoSel", tag);
	}

	public void setAmmoUpdate(NBTTagCompound tag) {
		this.packetData.setTag("ammoUpd", tag);
	}

	public void setPackCommand() {
		this.packetData.setBoolean("pack", true);
	}

	public void setTurretParams(NBTTagCompound tag) {
		this.packetData.setCompoundTag("turret", tag);
	}

	public void setMoveUpdate(VehicleBase vehicle, boolean pos, boolean airData, boolean rot) {
		this.entityID = vehicle.entityId;
		this.packetData.setBoolean("moveData", true);
		if (pos) {
			this.packetData.setFloat("px", (float) vehicle.posX);
			this.packetData.setFloat("py", (float) vehicle.posY);
			this.packetData.setFloat("pz", (float) vehicle.posZ);
		}
		if (airData) {
			this.packetData.setFloat("tr", vehicle.moveHelper.throttle);
		} else {
			this.packetData.setFloat("fm", vehicle.moveHelper.forwardMotion);
		}
		if (rot) {
			this.packetData.setFloat("ry", vehicle.rotationYaw);
			this.packetData.setFloat("rp", vehicle.rotationPitch);
		}
	}

	@Override
	public int getPacketType() {
		return 2;
	}

	@Override
	public void writeDataToStream(ByteArrayDataOutput data) {
		data.writeInt(entityID);
	}

	@Override
	public void readDataStream(ByteArrayDataInput data) {
		this.entityID = data.readInt();
	}

	@Override
	protected void execute(EntityPlayer player) {
		vehicle = getVehicle(player.world);
		super.execute(player);
	}

	@Override
	public void execute() {
		VehicleBase vehicle = (VehicleBase) world.getEntityByID(entityID);
		if (vehicle != null) {
			vehicle.handlePacketUpdate(packetData);
		}
	}

}
