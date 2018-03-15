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

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.network.PacketBase;
import net.shadowmage.ancientwarfare.vehicle.entity.VehicleBase;

import javax.annotation.Nullable;
import java.io.IOException;

public abstract class PacketVehicleBase extends PacketBase {
	//TODO call registerPacketType for all packet types

	private int entityID;
	protected VehicleBase vehicle = null;

	public PacketVehicleBase() {
	}

	public PacketVehicleBase(VehicleBase vehicle) {
		this.entityID = vehicle.getEntityId();
	}

	@Override
	protected void writeToStream(ByteBuf data) {
		data.writeInt(entityID);
	}

	@Override
	protected void readFromStream(ByteBuf data) throws IOException {
		entityID = data.readInt();
	}

	@Nullable
	private VehicleBase getVehicle(World world) {
		Entity vehicle = world.getEntityByID(entityID);
		return vehicle instanceof VehicleBase ? (VehicleBase) vehicle : null;
	}

	@Override
	protected void execute(EntityPlayer player) {
		vehicle = getVehicle(player.world);
		super.execute(player);
	}
}
