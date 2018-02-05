package net.shadowmage.ancientwarfare.vehicle.inventory;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import net.shadowmage.ancientwarfare.vehicle.entity.VehicleBase;
import net.shadowmage.ancientwarfare.vehicle.registry.VehicleUpgradeRegistry;
import net.shadowmage.ancientwarfare.vehicle.upgrades.IVehicleUpgradeType;

public class UpgradeStackHandler extends ItemStackHandler {
	private VehicleBase vehicle;

	public UpgradeStackHandler(VehicleBase vehicle, int size) {
		super(size);
		this.vehicle = vehicle;
	}

	public boolean isItemValid(ItemStack par1ItemStack) {
		IVehicleUpgradeType upgrade = VehicleUpgradeRegistry.instance().getUpgrade(par1ItemStack);
		if (upgrade != null) {
			return vehicle.vehicleType.isUpgradeValid(upgrade);
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
