package net.shadowmage.ancientwarfare.vehicle.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.PacketBuffer;
import net.shadowmage.ancientwarfare.vehicle.entity.VehicleBase;

import java.io.IOException;

public class PacketAmmoSelect extends PacketVehicleBase {
	private String ammoRegistryName;

	public PacketAmmoSelect() {}

	public PacketAmmoSelect(VehicleBase vehicle, String ammoRegistryName) {
		super(vehicle);
		this.ammoRegistryName = ammoRegistryName;
	}

	@Override
	protected void writeToStream(ByteBuf data) {
		super.writeToStream(data);
		PacketBuffer pb = new PacketBuffer(data);
		pb.writeString(ammoRegistryName);
	}

	@Override
	protected void readFromStream(ByteBuf data) throws IOException {
		super.readFromStream(data);
		PacketBuffer pb = new PacketBuffer(data);
		ammoRegistryName = pb.readString(64);
	}

	@Override
	public void execute() {
		vehicle.ammoHelper.updateSelectedAmmo(ammoRegistryName);
		super.execute();
	}
}
