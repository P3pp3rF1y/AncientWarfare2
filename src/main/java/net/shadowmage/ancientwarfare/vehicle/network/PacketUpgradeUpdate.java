package net.shadowmage.ancientwarfare.vehicle.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.PacketBuffer;
import net.shadowmage.ancientwarfare.vehicle.entity.VehicleBase;

import java.io.IOException;

public class PacketUpgradeUpdate extends PacketVehicleBase {
	private String[] upgradeRegistryNames;
	private String[] armorRegistryNames;

	public PacketUpgradeUpdate() {}

	public PacketUpgradeUpdate(VehicleBase vehicle) {
		super(vehicle);
		upgradeRegistryNames = vehicle.upgradeHelper.serializeUpgrades();
		armorRegistryNames = vehicle.upgradeHelper.serializeInstalledArmors();
	}

	@Override
	protected void writeToStream(ByteBuf data) {
		super.writeToStream(data);
		PacketBuffer pb = new PacketBuffer(data);
		pb.writeInt(upgradeRegistryNames.length);
		for (String upgrade : upgradeRegistryNames) {
			pb.writeString(upgrade);
		}
		pb.writeInt(armorRegistryNames.length);
		for (String armor : armorRegistryNames) {
			pb.writeString(armor);
		}
	}

	@Override
	protected void readFromStream(ByteBuf data) throws IOException {
		super.readFromStream(data);
		PacketBuffer pb = new PacketBuffer(data);
		upgradeRegistryNames = new String[pb.readInt()];
		for (int i = 0; i < upgradeRegistryNames.length; i++) {
			upgradeRegistryNames[i] = pb.readString(64);
		}
		armorRegistryNames = new String[pb.readInt()];
		for (int i = 0; i < armorRegistryNames.length; i++) {
			armorRegistryNames[i] = pb.readString(64);
		}
	}

	@Override
	public void execute() {
		vehicle.upgradeHelper.updateUpgrades(armorRegistryNames, upgradeRegistryNames);
	}
}
