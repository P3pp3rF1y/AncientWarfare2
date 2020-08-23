package net.shadowmage.ancientwarfare.npc.inventory;

import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;
import net.shadowmage.ancientwarfare.npc.item.ItemUpkeepOrder;

public class NpcEquipmentHandler implements IItemHandlerModifiable {

	private static final int SIZE_INVENTORY = 8;
	private final NpcBase npc;

	public NpcEquipmentHandler(NpcBase npc) {
		this.npc = npc;
	}

	@Override
	public int getSlots() {
		return SIZE_INVENTORY;
	}

	private int validateSlotIndex(final int slot) {
		if (slot < 0 || slot >= SIZE_INVENTORY) { throw new IllegalArgumentException("Slot " + slot + " not in valid range - [0," + SIZE_INVENTORY + ")"); }
		return slot;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return npc.getItemStackFromSlot(validateSlotIndex(slot));
	}

	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
		if (stack.isEmpty()) {
			return ItemStack.EMPTY;
		}

		validateSlotIndex(slot);

		ItemStack existing = npc.getItemStackFromSlot(slot);

		int limit = getStackLimit(slot, stack);

		if (!existing.isEmpty()) {
			if (!ItemHandlerHelper.canItemStacksStack(stack, existing)) {
				return stack;
			}

			limit -= existing.getCount();
		}

		if (limit <= 0) {
			return stack;
		}

		boolean reachedLimit = stack.getCount() > limit;

		if (!simulate) {
			if (existing.isEmpty()) {
				npc.setItemStackToSlot(slot, reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, limit) : stack);
			} else {
				existing.grow(reachedLimit ? limit : stack.getCount());
			}
		}

		return reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, stack.getCount() - limit) : ItemStack.EMPTY;

	}

	private int getStackLimit(final int slot, ItemStack stack) {
		return Math.min(getSlotLimit(slot), stack.getMaxStackSize());
	}

	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		if (amount == 0) {
			return ItemStack.EMPTY;
		}

		validateSlotIndex(slot);

		ItemStack existing = npc.getItemStackFromSlot(slot);

		if (existing.isEmpty()) {
			return ItemStack.EMPTY;
		}

		final int toExtract = Math.min(amount, existing.getMaxStackSize());

		if (existing.getCount() <= toExtract) {
			if (!simulate) {
				npc.setItemStackToSlot(slot, ItemStack.EMPTY);
			}

			return existing;
		} else {
			if (!simulate) {
				npc.setItemStackToSlot(slot, ItemHandlerHelper.copyStackWithSize(existing, existing.getCount() - toExtract));
			}

			return ItemHandlerHelper.copyStackWithSize(existing, toExtract);
		}
	}

	@Override
	public int getSlotLimit(int slot) {
		return slot == NpcBase.UPKEEP_SLOT || slot == NpcBase.ORDER_SLOT || slot > 1 && slot < NpcBase.ORDER_SLOT ? 1 : 64;
	}

	@Override
	public boolean isItemValid(int slot, ItemStack stack) {
		if (slot == NpcBase.UPKEEP_SLOT) {
			return stack.getItem() instanceof ItemUpkeepOrder;
		} else if (slot == NpcBase.ORDER_SLOT) {
			return npc.isValidOrdersStack(stack);
		} else if (slot > 1 && slot < NpcBase.ORDER_SLOT) {
			return stack.getItem().isValidArmor(stack, EntityEquipmentSlot.values()[slot], npc);
		}
		return true;
	}

	@Override
	public void setStackInSlot(int slot, ItemStack stack) {
		validateSlotIndex(slot);
		if (ItemStack.areItemStacksEqual(npc.getItemStackFromSlot(slot), stack)) {
			return;
		}
		npc.setItemStackToSlot(slot, stack);
	}
}
