package net.shadowmage.ancientwarfare.vehicle.network;

import io.netty.buffer.ByteBuf;
import net.shadowmage.ancientwarfare.vehicle.entity.VehicleBase;

import java.io.IOException;

//TODO figure out if this is needed at all?
public class PacketTurretAnglesUpdate extends PacketVehicleBase {
	private float pitch;
	private float rotation;

	public PacketTurretAnglesUpdate() {}

	public PacketTurretAnglesUpdate(VehicleBase vehicle, float pitch, float rotation) {
		super(vehicle);
		this.pitch = pitch;
		this.rotation = rotation;
	}

	@Override
	protected void writeToStream(ByteBuf data) {
		super.writeToStream(data);
		data.writeFloat(pitch);
		data.writeFloat(rotation);
	}

	@Override
	protected void readFromStream(ByteBuf data) throws IOException {
		super.readFromStream(data);
		pitch = data.readFloat();
		rotation = data.readFloat();
	}

	@Override
	public void execute() {
		vehicle.updateTurretAngles(pitch, rotation);
	}
}