package net.shadowmage.ancientwarfare.vehicle.inventory;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import net.shadowmage.ancientwarfare.vehicle.entity.VehicleBase;
import net.shadowmage.ancientwarfare.vehicle.missiles.IAmmo;
import net.shadowmage.ancientwarfare.vehicle.registry.AmmoRegistry;

import javax.annotation.Nonnull;

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

	@Nonnull
	@Override
	public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
		return isValidAmmo(stack) ? super.insertItem(slot, stack, simulate) : stack;
	}

	private boolean isValidAmmo(ItemStack stack) {
		IAmmo ammo = AmmoRegistry.instance().getAmmoForStack(stack);
		return ammo != null && vehicle.vehicleType.isAmmoValidForInventory(ammo);
	}
}
