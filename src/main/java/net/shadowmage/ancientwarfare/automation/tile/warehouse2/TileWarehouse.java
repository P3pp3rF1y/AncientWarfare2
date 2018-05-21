package net.shadowmage.ancientwarfare.automation.tile.warehouse2;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;
import net.shadowmage.ancientwarfare.core.util.InventoryTools.ComparatorItemStack.SortOrder;
import net.shadowmage.ancientwarfare.core.util.InventoryTools.ComparatorItemStack.SortType;
import net.shadowmage.ancientwarfare.core.util.WorldTools;

import javax.annotation.Nonnull;
import java.util.List;

public class TileWarehouse extends TileWarehouseBase {

	private SortType sortType = SortType.NAME;
	private SortOrder sortOrder = SortOrder.DESCENDING;

	public TileWarehouse() {

	}

	@Override
	public void invalidate() {
		BlockPos max = getWorkBoundsMax();
		if (max == null)
			return;
		BlockPos min = getWorkBoundsMin();
		if (min == null)
			return;
		List<TileEntity> tiles = WorldTools.getTileEntitiesInArea(world, min.getX(), min.getY(), min.getZ(), max.getX(), max.getY(), max.getZ());
		for (TileEntity te : tiles) {
			if (te instanceof IControlledTile && ((IControlledTile) te).getController() == this) {
				((IControlledTile) te).setController(null);
			}
		}
	}

	@Override
	public void handleSlotClick(EntityPlayer player, ItemStack filter, boolean shiftClick, boolean rightClick) {
		if (!shiftClick && !player.inventory.getItemStack().isEmpty()) {
			tryAddItem(player, player.inventory.getItemStack());
		} else if (!filter.isEmpty()) {
			tryGetItem(player, filter, shiftClick, rightClick);
		}
	}

	private void tryAddItem(EntityPlayer player, ItemStack cursorStack) {
		if (cursorStack.isEmpty()) {
			return;
		}
		ItemStack result = tryAddItem(cursorStack, cursorStack.getCount());
		if (result.getCount() != cursorStack.getCount()) {
			player.inventory.setItemStack(result);
			((EntityPlayerMP) player).updateHeldItem();
		}
	}

	private ItemStack tryAddItem(ItemStack stack, int count) {
		List<IWarehouseStorageTile> destinations = storageMap.getDestinations(stack);
		int addedTotal = 0;
		for (IWarehouseStorageTile tile : destinations) {
			int moved = tile.insertItem(stack, count - addedTotal);
			addedTotal += moved;
			changeCachedQuantity(stack, moved);
			updateViewers();
			if (addedTotal >= count) {
				return ItemStack.EMPTY;
			}
		}

		ItemStack result = stack.copy();
		result.shrink(addedTotal);
		return result;
	}

	private void tryGetItem(EntityPlayer player, ItemStack filter, boolean shiftClick, boolean rightClick) {
		int stackSize = 0;
		if (!player.inventory.getItemStack().isEmpty()) {
			stackSize = player.inventory.getItemStack().getCount();
			ItemStack comparableStack = player.inventory.getItemStack().copy();
			comparableStack.setCount(filter.getCount());
			if (!ItemStack.areItemStacksEqual(filter, comparableStack))
				return;
		}

		List<IWarehouseStorageTile> destinations = storageMap.getDestinations();
		int toMoveMax = filter.getMaxStackSize();
		if (rightClick && (toMoveMax > 1)) {
			if (shiftClick) {
				toMoveMax = Math.min(stackSize + 1, toMoveMax);
			} else {
				int available = 0;
				for (IWarehouseStorageTile tile : destinations) {
					available += tile.getQuantityStored(filter);
				}
				if (toMoveMax > available) {
					toMoveMax = available;
				}
				toMoveMax = (int) Math.ceil(toMoveMax / 2.0);
			}
		}
		int toRemove = toMoveMax - stackSize;
		ItemStack removedStack = tryGetItem(filter, toRemove);

		ItemStack newCursorStack = filter.copy();
		newCursorStack.setCount(stackSize + removedStack.getCount());
		InventoryTools.updateCursorItem((EntityPlayerMP) player, newCursorStack, !rightClick && shiftClick);
	}

	private ItemStack tryGetItem(ItemStack filter, int toRemove) {
		if (world.isRemote) {
			return tryGetItemClient(filter, toRemove);
		}

		int removed = 0;
		for (IWarehouseStorageTile tile : storageMap.getDestinations()) {
			int count = tile.getQuantityStored(filter);
			int removeFromTile = Math.min(toRemove - removed, count);
			if (removeFromTile > 0) {
				removed += removeFromTile;
				tile.extractItem(filter, removeFromTile);
				changeCachedQuantity(filter, -removeFromTile);
				updateViewers();
			}
			if (removed >= toRemove) {
				break;
			}
		}

		if (removed == 0) {
			return ItemStack.EMPTY;
		}
		ItemStack result = filter.copy();
		result.setCount(removed);
		return result;
	}

	private ItemStack tryGetItemClient(ItemStack filter, int toRemove) {
		int maxRemove = cachedItemMap.getCount(filter);
		int removed = Math.min(toRemove, maxRemove);
		cachedItemMap.decreaseCount(filter, removed);
		ItemStack result = filter.copy();
		result.setCount(removed);
		return result;
	}

	@Override
	protected void writeUpdateNBT(NBTTagCompound tag) {
		super.writeUpdateNBT(tag);
		writeSort(tag);
	}

	@Override
	protected void handleUpdateNBT(NBTTagCompound tag) {
		super.handleUpdateNBT(tag);
		readSort(tag);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		writeSort(tag);
		return tag;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		readSort(tag);
	}

	private void writeSort(NBTTagCompound tag) {
		tag.setByte("sortOrder", (byte) getSortOrder().ordinal());
		tag.setByte("sortType", (byte) getSortType().ordinal());
	}

	private void readSort(NBTTagCompound tag) {
		setSortOrder(SortOrder.values()[tag.getByte("sortOrder")]);
		setSortType(SortType.values()[tag.getByte("sortType")]);
	}

	public SortType getSortType() {
		return sortType;
	}

	public void setSortType(SortType sortType) {
		this.sortType = sortType;
	}

	public SortOrder getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(SortOrder sortOrder) {
		this.sortOrder = sortOrder;
	}

	public IItemHandlerModifiable getItemHandler() {
		int additionalSlots = 10;
		NonNullList<ItemStack> cachedItems = cachedItemMap.getItems();

		for (int i = 0; i < additionalSlots; i++) {
			cachedItems.add(ItemStack.EMPTY);
		}

		return new IItemHandlerModifiable() {
			@Override
			public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
				ItemStack currentStack = cachedItems.get(slot);

				if (currentStack.isEmpty()) {
					insertItem(slot, stack, false);
				} else {
					int countChange = stack.getCount() - currentStack.getCount();

					if (countChange == 0) {
						return;
					}

					if (countChange > 0) {
						ItemStack copy = stack.copy();
						copy.setCount(countChange);
						insertItem(slot, copy, false);
					} else {
						extractItem(slot, -countChange, false);
					}
				}
			}

			@Override
			public int getSlots() {
				return cachedItems.size();
			}

			@Nonnull
			@Override
			public ItemStack getStackInSlot(int slot) {
				return slot < cachedItems.size() ? cachedItems.get(slot) : ItemStack.EMPTY;
			}

			@Nonnull
			@Override
			public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
				ItemStack cachedStack = cachedItems.get(slot);
				if (cachedStack.isEmpty() || InventoryTools.doItemStacksMatchRelaxed(stack, cachedStack)) {
					int maxToAdd = Math.min(stack.getMaxStackSize() - cachedStack.getCount(), stack.getCount());
					if (!simulate) {
						cachedStack.setCount(cachedStack.getCount() + maxToAdd);
						return tryAddItem(stack, maxToAdd);
					}
					ItemStack ret = ItemStack.EMPTY;
					if (maxToAdd < stack.getCount()) {
						ret = stack.copy();
						ret.setCount(stack.getCount() - maxToAdd);
					}

					return ret;
				}

				return stack;
			}

			@Nonnull
			@Override
			public ItemStack extractItem(int slot, int amount, boolean simulate) {
				ItemStack cachedStack = cachedItems.get(slot);

				if (cachedStack.isEmpty()) {
					return ItemStack.EMPTY;
				}

				int maxToRemove = Math.min(cachedStack.getCount(), amount);

				if (!simulate) {
					ItemStack result = tryGetItem(cachedStack, maxToRemove);
					cachedStack.setCount(cachedStack.getCount() - maxToRemove);
					return result;
				}

				ItemStack ret = cachedStack.copy();
				ret.setCount(maxToRemove);
				return ret;
			}

			@Override
			public int getSlotLimit(int slot) {
				return 64;
			}
		};
	}
}
