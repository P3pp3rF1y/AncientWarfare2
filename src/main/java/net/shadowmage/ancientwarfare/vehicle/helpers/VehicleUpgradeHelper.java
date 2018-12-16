package net.shadowmage.ancientwarfare.vehicle.helpers;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.vehicle.armors.IVehicleArmor;
import net.shadowmage.ancientwarfare.vehicle.entity.VehicleBase;
import net.shadowmage.ancientwarfare.vehicle.missiles.DamageType;
import net.shadowmage.ancientwarfare.vehicle.network.PacketUpgradeUpdate;
import net.shadowmage.ancientwarfare.vehicle.network.PacketVehicleBase;
import net.shadowmage.ancientwarfare.vehicle.registry.ArmorRegistry;
import net.shadowmage.ancientwarfare.vehicle.registry.UpgradeRegistry;
import net.shadowmage.ancientwarfare.vehicle.upgrades.IVehicleUpgradeType;

import java.util.ArrayList;
import java.util.List;

public class VehicleUpgradeHelper implements INBTSerializable<NBTTagCompound> {

	/**
	 * currently installed upgrades, will be iterated through linearly to call upgrade.applyEffects, multiple upgrades may have cumulative effects
	 */
	private List<IVehicleUpgradeType> upgrades = new ArrayList<>();
	private List<IVehicleArmor> installedArmor = new ArrayList<>();

	/**
	 * list of all upgrades that are valid for this vehicle, used by inventoryChecking to see whether it can be installed or not
	 */
	private List<IVehicleUpgradeType> validUpgrades = new ArrayList<>();
	private List<IVehicleArmor> validArmorTypes = new ArrayList<>();
	private VehicleBase vehicle;

	public VehicleUpgradeHelper(VehicleBase vehicle) {
		this.vehicle = vehicle;
	}

	/**
	 * SERVER ONLY
	 */
	public void updateUpgrades() {
		if (vehicle.world.isRemote) {
			return;
		}
		upgrades = vehicle.inventory.getInventoryUpgrades();

		NBTTagCompound tag = new NBTTagCompound();
		serializeUpgrades(tag);

		installedArmor = vehicle.inventory.getInventoryArmor();

		serializeInstalledArmors(tag);

		PacketVehicleBase pkt = new PacketUpgradeUpdate(vehicle);
		NetworkHandler.sendToAllTracking(vehicle, pkt);
		this.updateUpgradeStats();
	}

	private void serializeUpgrades(NBTTagCompound tag) {
		NBTTagList upgradesNbt = new NBTTagList();
		for (String upgrade : serializeUpgrades()) {
			upgradesNbt.appendTag(new NBTTagString(upgrade));
		}
		tag.setTag("upgrades", upgradesNbt);
	}

	public String[] serializeUpgrades() {
		int len = this.upgrades.size();
		String[] registryNames = new String[len];
		for (int i = 0; i < this.upgrades.size(); i++) {
			registryNames[i] = this.upgrades.get(i).getRegistryName().toString();
		}
		return registryNames;
	}

	private void serializeInstalledArmors(NBTTagCompound tag) {
		NBTTagList armorTypes = new NBTTagList();
		for (String armor : serializeInstalledArmors()) {
			armorTypes.appendTag(new NBTTagString(armor));
		}

		tag.setTag("armors", armorTypes);
	}

	public String[] serializeInstalledArmors() {
		String[] armors = new String[installedArmor.size()];
		for (int i = 0; i < installedArmor.size(); i++) {
			armors[i] = installedArmor.get(i).getRegistryName().toString();
		}
		return armors;
	}

	/**
	 * CLIENT ONLY..receives the packet sent above, and sets upgrade list directly from registry
	 */
	public void updateUpgrades(String[] armorRegistryNames, String[] upgradeRegistryNames) {
		deserializeInstalledArmor(armorRegistryNames);
		deserializeUpgrades(upgradeRegistryNames);

		updateUpgradeStats();
	}

	public List<IVehicleUpgradeType> getUpgrades() {
		return upgrades;
	}

	private void deserializeInstalledArmor(NBTTagCompound tag) {
		NBTTagList armorTypes = tag.getTagList("armors", Constants.NBT.TAG_STRING);
		String[] armorRegistryNames = new String[armorTypes.tagCount()];
		for (int i = 0; i < armorRegistryNames.length; i++) {
			armorRegistryNames[i] = ((NBTTagString) armorTypes.get(i)).getString();
		}
		deserializeInstalledArmor(armorRegistryNames);
	}

	private void deserializeInstalledArmor(String[] armorRegistryNames) {
		installedArmor.clear();
		for (String armorRegistryName : armorRegistryNames) {
			ArmorRegistry.getArmorType(new ResourceLocation(armorRegistryName)).ifPresent(armor -> installedArmor.add(armor));
		}
	}

	private void deserializeUpgrades(NBTTagCompound tag) {
		NBTTagList upgradeTypes = tag.getTagList("upgrades", Constants.NBT.TAG_STRING);
		String[] upgradeRegistryNames = new String[upgradeTypes.tagCount()];
		for (int i = 0; i < upgradeRegistryNames.length; i++) {
			upgradeRegistryNames[i] = ((NBTTagString) upgradeTypes.get(i)).getString();
		}
		deserializeUpgrades(upgradeRegistryNames);
	}

	private void deserializeUpgrades(String[] upgradeRegistryNames) {
		upgrades.clear();
		for (String upgradeRegistryName : upgradeRegistryNames) {
			UpgradeRegistry.getUpgrade(new ResourceLocation(upgradeRegistryName)).ifPresent(upgrades::add);
		}
	}

	/**
	 * reset stats to base stats
	 * iterate through upgrades, applying their effects each in turn (multiple same upgrades are cumulative)
	 */
	public void updateUpgradeStats() {
		vehicle.resetCurrentStats();
		for (IVehicleUpgradeType upgrade : this.upgrades) {
			upgrade.applyVehicleEffects(vehicle);
		}
		for (IVehicleArmor armor : this.installedArmor) {
			vehicle.currentExplosionResist += armor.getExplosiveDamageReduction();
			vehicle.currentFireResist += armor.getFireDamageReduction();
			vehicle.currentGenericResist += armor.getGeneralDamageReduction();
			vehicle.currentWeight += armor.getArmorWeight();
		}
	}

	public void addValidArmor(IVehicleArmor armor) {
		if (!this.validArmorTypes.contains(armor)) {
			this.validArmorTypes.add(armor);
		}
	}

	public void addValidUpgrade(IVehicleUpgradeType upgrade) {
		if (!this.validUpgrades.contains(upgrade)) {
			this.validUpgrades.add(upgrade);
		}
	}

	public float getScaledDamage(DamageSource src, float amt) {
		if (src == DamageType.explosiveMissile || src.isExplosion()) {
			return amt * (1 - (vehicle.currentExplosionResist * 0.01f));
		} else if (src == DamageType.fireMissile || src == DamageSource.IN_FIRE || src == DamageSource.LAVA || src == DamageSource.ON_FIRE || src.isFireDamage()) {
			return amt * (1 - (vehicle.currentFireResist * 0.01f));
		}
		return amt * (1 - (vehicle.currentGenericResist * 0.01f));
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound tag = new NBTTagCompound();

		serializeUpgrades(tag);
		serializeInstalledArmors(tag);

		return tag;
	}

	@Override
	public void deserializeNBT(NBTTagCompound tag) {
		deserializeUpgrades(tag);
		deserializeInstalledArmor(tag);
	}

	public boolean hasUpgrade(IVehicleUpgradeType upgrade) {
		return this.upgrades.contains(upgrade);
	}

}
