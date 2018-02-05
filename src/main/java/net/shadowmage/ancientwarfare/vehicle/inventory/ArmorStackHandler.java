package net.shadowmage.ancientwarfare.vehicle.inventory;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import net.shadowmage.ancientwarfare.vehicle.armors.IVehicleArmor;
import net.shadowmage.ancientwarfare.vehicle.entity.VehicleBase;
import net.shadowmage.ancientwarfare.vehicle.registry.ArmorRegistry;

public class ArmorStackHandler extends ItemStackHandler {
	private VehicleBase vehicle;

	public ArmorStackHandler(VehicleBase vehicle, int size) {
		super(size);
		this.vehicle = vehicle;
	}

	public boolean isItemValid(ItemStack par1ItemStack) {
		IVehicleArmor armor = ArmorRegistry.getArmorForStack(par1ItemStack);
		if (armor != null) {
			return vehicle.vehicleType.isArmorValid(armor);
		}
		return false;
	}

	@Override
	public int getSlotLimit(int slot) {
		return 1;
	}

	@Override
	protected void onContentsChanged(int slot) {
		if (!vehicle.world.isRemote) {
			vehicle.upgradeHelper.updateUpgrades();
		}
	}
}
