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
		Entity ret = world.getEntityByID(entityID);
		return ret instanceof VehicleBase ? (VehicleBase) ret : null;
	}

	@Override
	protected void execute(EntityPlayer player) {
		vehicle = getVehicle(player.world);
		super.execute(player);
	}
}
