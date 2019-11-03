package net.shadowmage.ancientwarfare.vehicle.network;

import io.netty.buffer.ByteBuf;
import net.shadowmage.ancientwarfare.vehicle.entity.VehicleBase;

import java.io.IOException;

public class PacketVehicleInput extends PacketVehicleBase {
	private byte forwardInput = 0;
	private byte turnInput = 0;
	private byte powerInput = 0;
	private byte rotationInput = 0;

	public PacketVehicleInput() {}

	public PacketVehicleInput(VehicleBase vehicle) {
		super(vehicle);
	}

	@Override
	protected void writeToStream(ByteBuf data) {
		super.writeToStream(data);
		data.writeByte(forwardInput);
		data.writeByte(turnInput);
		data.writeByte(powerInput);
		data.writeByte(rotationInput);
	}

	@Override
	protected void readFromStream(ByteBuf data) throws IOException {
		super.readFromStream(data);
		forwardInput = data.readByte();
		turnInput = data.readByte();
		powerInput = data.readByte();
		rotationInput = data.readByte();
	}

	public void setForwardInput(byte input) {
		forwardInput = input;
	}

	public void setTurnInput(byte input) {
		turnInput = input;
	}

	public void setPowerInput(byte input) {
		powerInput = input;
	}

	public void setRotationInput(byte input) {
		rotationInput = input;
	}

	@Override
	protected void execute() {
		vehicle.moveHelper.handleInputData(forwardInput, turnInput, powerInput, rotationInput);
	}
}
