package net.shadowmage.ancientwarfare.vehicle.network;

import io.netty.buffer.ByteBuf;
import net.shadowmage.ancientwarfare.vehicle.entity.VehicleBase;

import java.io.IOException;

public class PacketVehicleInput extends PacketVehicleBase {

	private boolean forward = false;
	private boolean reverse = false;
	private boolean left = false;
	private boolean right = false;
	private boolean pitchUp = false;
	private boolean pitchDown = false;
	private boolean turretLeft = false;
	private boolean turretRight = false;

	@Override
	protected void writeToStream(ByteBuf data) {
		super.writeToStream(data);
		data.writeBoolean(forward);
		data.writeBoolean(reverse);
		data.writeBoolean(left);
		data.writeBoolean(right);
		data.writeBoolean(pitchUp);
		data.writeBoolean(pitchDown);
		data.writeBoolean(turretLeft);
		data.writeBoolean(turretRight);
	}

	@Override
	protected void readFromStream(ByteBuf data) throws IOException {
		super.readFromStream(data);
		forward = data.readBoolean();
		reverse = data.readBoolean();
		left = data.readBoolean();
		right = data.readBoolean();
		pitchUp = data.readBoolean();
		pitchDown = data.readBoolean();
		turretLeft = data.readBoolean();
		turretRight = data.readBoolean();
	}

	public PacketVehicleInput(VehicleBase vehicle) {
		super(vehicle);

	}

	public PacketVehicleInput setForward() {
		forward = true;
		return this;
	}

	public PacketVehicleInput setReverse() {
		reverse = true;
		return this;
	}

	public PacketVehicleInput setLeft() {
		left = true;
		return this;
	}

	public PacketVehicleInput setRight() {
		right = true;
		return this;
	}

	public PacketVehicleInput setPitchUp() {
		pitchUp = true;
		return this;
	}

	public PacketVehicleInput setPitchDown() {
		pitchDown = true;
		return this;
	}

	public PacketVehicleInput setTurretLeft() {
		turretLeft = true;
		return this;
	}

	public PacketVehicleInput setTurretRight() {
		turretRight = true;
		return this;
	}

	@Override
	protected void execute() {

	}
}
