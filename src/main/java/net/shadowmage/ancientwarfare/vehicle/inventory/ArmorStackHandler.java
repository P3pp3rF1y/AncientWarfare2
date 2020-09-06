package net.shadowmage.ancientwarfare.vehicle.inventory;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import net.shadowmage.ancientwarfare.vehicle.entity.VehicleBase;
import net.shadowmage.ancientwarfare.vehicle.registry.ArmorRegistry;

import javax.annotation.Nonnull;

public class ArmorStackHandler extends ItemStackHandler {
	private VehicleBase vehicle;

	public ArmorStackHandler(VehicleBase vehicle, int size) {
		super(size);
		this.vehicle = vehicle;
	}

	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
		return isItemValid(stack) ? super.insertItem(slot, stack, simulate) : stack;
	}

	public boolean isItemValid(ItemStack par1ItemStack) {
		return ArmorRegistry.getArmorForStack(par1ItemStack).map(armor -> vehicle.vehicleType.isArmorValid(armor)).orElse(false);
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
