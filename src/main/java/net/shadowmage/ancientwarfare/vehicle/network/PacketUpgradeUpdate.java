package net.shadowmage.ancientwarfare.vehicle.network;

import com.google.common.collect.Lists;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.shadowmage.ancientwarfare.vehicle.armors.IVehicleArmor;
import net.shadowmage.ancientwarfare.vehicle.entity.VehicleBase;
import net.shadowmage.ancientwarfare.vehicle.registry.ArmorRegistry;
import net.shadowmage.ancientwarfare.vehicle.registry.VehicleUpgradeRegistry;
import net.shadowmage.ancientwarfare.vehicle.upgrades.IVehicleUpgradeType;

import java.util.List;

public class PacketUpgradeUpdate extends PacketVehicle {
	private int[] upgradeIds;
	private String[] armorRegistryNames;

	public PacketUpgradeUpdate(VehicleBase vehicle) {
		super(vehicle);
		upgradeIds = serializeUpgrades(vehicle.upgradeHelper.getUpgrades());
		armorRegistryNames = serializeArmor(vehicle.upgradeHelper.getInstalledArmor());
	}

	private int[] serializeUpgrades(List<IVehicleUpgradeType> upgrades) {
		int len = upgrades.size();
		int[] upgradeIds = new int[len];
		for (int i = 0; i < upgrades.size(); i++) {
			upgradeIds[i] = upgrades.get(i).getUpgradeId();
		}
		return upgradeIds;
	}

	private String[] serializeArmor(List<IVehicleArmor> installedArmor) {
		String[] armor = new String[installedArmor.size()];

		for (int i = 0; i < installedArmor.size(); i++) {
			armor[i] = installedArmor.get(i).getRegistryName().toString();
		}
		return armor;
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
	protected void readFromStream(ByteBuf data) {
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
		List<IVehicleArmor> armor = Lists.newArrayList();
		for (String armorRegistryName : armorRegistryNames) {
			armor.add(ArmorRegistry.getArmorType(new ResourceLocation(armorRegistryName)));
		}

		List<IVehicleUpgradeType> upgrades = Lists.newArrayList();
		for (int upgradeId : upgradeIds) {
			upgrades.add(VehicleUpgradeRegistry.instance().getUpgrade(upgradeId));
		}
		vehicle.upgradeHelper.updateUpgrades(armor, upgrades);
	}
}
