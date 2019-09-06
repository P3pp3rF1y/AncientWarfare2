package net.shadowmage.ancientwarfare.vehicle.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.PacketBuffer;
import net.shadowmage.ancientwarfare.vehicle.entity.VehicleBase;

import java.io.IOException;

public class PacketSingleAmmoUpdate extends PacketVehicleBase {
	private String ammoRegistryName;
	private int count;

	public PacketSingleAmmoUpdate() {}

	public PacketSingleAmmoUpdate(VehicleBase vehicle, String ammoRegistryName, int count) {
		super(vehicle);
		this.ammoRegistryName = ammoRegistryName;
		this.count = count;
	}

	@Override
	protected void writeToStream(ByteBuf data) {
		super.writeToStream(data);
		PacketBuffer pb = new PacketBuffer(data);
		pb.writeString(ammoRegistryName);
		pb.writeInt(count);
	}

	@Override
	protected void readFromStream(ByteBuf data) throws IOException {
		super.readFromStream(data);
		PacketBuffer pb = new PacketBuffer(data);
		ammoRegistryName = pb.readString(64);
		count = pb.readInt();
	}

	@Override
	public void execute() {
		vehicle.ammoHelper.updateAmmoCount(ammoRegistryName, count);
	}
}
