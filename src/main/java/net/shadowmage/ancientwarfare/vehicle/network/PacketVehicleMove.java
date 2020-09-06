package net.shadowmage.ancientwarfare.vehicle.network;

import io.netty.buffer.ByteBuf;
import net.shadowmage.ancientwarfare.vehicle.entity.VehicleBase;

import java.io.IOException;

public class PacketVehicleMove extends PacketVehicleBase {
	//TODO look into refactoring VehicleMoveHelper so that we don't need to synchronize data that is already on vehicle
	private double posX;
	private double posY;
	private double posZ;
	private boolean air;
	private float motion;
	private float yaw;
	private float pitch;

	public PacketVehicleMove() {}

	public PacketVehicleMove(VehicleBase vehicle, double posX, double posY, double posZ, boolean air, float motion, float yaw, float pitch) {
		super(vehicle);
		this.posX = posX;
		this.posY = posY;
		this.posZ = posZ;
		this.air = air;
		this.motion = motion;
		this.yaw = yaw;
		this.pitch = pitch;
	}

	@Override
	protected void writeToStream(ByteBuf data) {
		super.writeToStream(data);
		data.writeDouble(posX);
		data.writeDouble(posY);
		data.writeDouble(posZ);
		data.writeBoolean(air);
		data.writeFloat(motion);
		data.writeFloat(yaw);
		data.writeFloat(pitch);
	}

	@Override
	protected void readFromStream(ByteBuf data) throws IOException {
		super.readFromStream(data);
		posX = data.readDouble();
		posY = data.readDouble();
		posZ = data.readDouble();
		air = data.readBoolean();
		motion = data.readFloat();
		yaw = data.readFloat();
		pitch = data.readFloat();
	}

	@Override
	public void execute() {
		vehicle.moveHelper.updateMoveData(posX, posY, posZ, air, motion, yaw, pitch);
	}
}
