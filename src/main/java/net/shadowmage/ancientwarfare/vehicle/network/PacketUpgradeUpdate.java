package net.shadowmage.ancientwarfare.vehicle.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.PacketBuffer;
import net.shadowmage.ancientwarfare.vehicle.entity.VehicleBase;

import java.io.IOException;

public class PacketUpgradeUpdate extends PacketVehicleBase {
	private int[] upgradeIds;
	private String[] armorRegistryNames;

	public PacketUpgradeUpdate(VehicleBase vehicle) {
		super(vehicle);
		upgradeIds = vehicle.upgradeHelper.serializeUpgrades();
		armorRegistryNames = vehicle.upgradeHelper.serializeInstalledArmors();
	}

	@Override
	protected void writeToStream(ByteBuf data) {
		super.writeToStream(data);
		PacketBuffer pb = new PacketBuffer(data);
		pb.writeVarIntArray(upgradeIds);
		pb.writeInt(armorRegistryNames.length);
		for (String armor : armorRegistryNames) {
			pb.writeString(armor);
		}
	}

	@Override
	protected void readFromStream(ByteBuf data) throws IOException {
		super.readFromStream(data);
		PacketBuffer pb = new PacketBuffer(data);
		upgradeIds = pb.readVarIntArray();
		armorRegistryNames = new String[pb.readInt()];
		for (int i = 0; i < armorRegistryNames.length; i++) {
			armorRegistryNames[i] = pb.readString(64);
		}
	}

	@Override
	public void execute() {
		vehicle.upgradeHelper.updateUpgrades(armorRegistryNames, upgradeIds);
	}
}
