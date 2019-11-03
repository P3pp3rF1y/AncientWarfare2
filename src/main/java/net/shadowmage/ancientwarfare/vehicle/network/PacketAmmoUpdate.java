package net.shadowmage.ancientwarfare.vehicle.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.shadowmage.ancientwarfare.vehicle.entity.VehicleBase;

import java.io.IOException;

public class PacketAmmoUpdate extends PacketVehicleBase {
	private NBTTagCompound ammoTag;

	public PacketAmmoUpdate() {}

	public PacketAmmoUpdate(VehicleBase vehicle, NBTTagCompound ammoTag) {
		super(vehicle);
		this.ammoTag = ammoTag;
	}

	@Override
	protected void writeToStream(ByteBuf data) {
		super.writeToStream(data);
		PacketBuffer pb = new PacketBuffer(data);
		pb.writeCompoundTag(ammoTag);
	}

	@Override
	protected void readFromStream(ByteBuf data) throws IOException {
		super.readFromStream(data);
		PacketBuffer pb = new PacketBuffer(data);
		ammoTag = pb.readCompoundTag();
	}

	@Override
	public void execute() {
		vehicle.ammoHelper.updateAmmo(ammoTag);
	}
}
