package net.shadowmage.ancientwarfare.vehicle.network;

import io.netty.buffer.ByteBuf;
import net.shadowmage.ancientwarfare.vehicle.entity.VehicleBase;

import java.io.IOException;
import java.util.Optional;

public class PacketAimUpdate extends PacketVehicleBase {
	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	private Optional<Float> pitch;
	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	private Optional<Float> yaw;
	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	private Optional<Float> power;

	public PacketAimUpdate() {}

	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	public PacketAimUpdate(VehicleBase vehicle, Optional<Float> pitch, Optional<Float> yaw, Optional<Float> power) {
		super(vehicle);
		this.pitch = pitch;
		this.yaw = yaw;
		this.power = power;
	}

	@Override
	protected void writeToStream(ByteBuf data) {
		super.writeToStream(data);
		data.writeBoolean(pitch.isPresent());
		pitch.ifPresent(data::writeFloat);
		data.writeBoolean(yaw.isPresent());
		yaw.ifPresent(data::writeFloat);
		data.writeBoolean(power.isPresent());
		power.ifPresent(data::writeFloat);
	}

	@Override
	protected void readFromStream(ByteBuf data) throws IOException {
		super.readFromStream(data);
		pitch = data.readBoolean() ? Optional.of(data.readFloat()) : Optional.empty();
		yaw = data.readBoolean() ? Optional.of(data.readFloat()) : Optional.empty();
		power = data.readBoolean() ? Optional.of(data.readFloat()) : Optional.empty();

	}

	@Override
	public void execute() {
		vehicle.firingHelper.updateAim(pitch, yaw, power);
	}
}
