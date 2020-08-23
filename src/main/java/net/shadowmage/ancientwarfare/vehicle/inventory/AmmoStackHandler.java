package net.shadowmage.ancientwarfare.vehicle.inventory;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import net.shadowmage.ancientwarfare.vehicle.entity.VehicleBase;
import net.shadowmage.ancientwarfare.vehicle.registry.AmmoRegistry;

public class AmmoStackHandler extends ItemStackHandler {
	private VehicleBase vehicle;

	public AmmoStackHandler(VehicleBase vehicle, int size) {
		super(size);
		this.vehicle = vehicle;
	}

	@Override
	protected void onContentsChanged(int slot) {
		if (!vehicle.world.isRemote) {
			vehicle.ammoHelper.updateAmmoCounts();
		}
	}

	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
		return isValidAmmo(stack) ? super.insertItem(slot, stack, simulate) : stack;
	}

	private boolean isValidAmmo(ItemStack stack) {
		return AmmoRegistry.getAmmoForStack(stack).map(ammo -> vehicle.vehicleType.isAmmoValidForInventory(ammo)).orElse(false);
	}
}
