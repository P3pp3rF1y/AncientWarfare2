package net.shadowmage.ancientwarfare.automation.tile.warehouse2;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;
import net.shadowmage.ancientwarfare.core.util.InventoryTools.ComparatorItemStack.SortOrder;
import net.shadowmage.ancientwarfare.core.util.InventoryTools.ComparatorItemStack.SortType;
import net.shadowmage.ancientwarfare.core.util.WorldTools;

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
		} else {
			tryGetItem(player, filter, shiftClick, rightClick);
		}
	}

	private void tryAddItem(EntityPlayer player, ItemStack cursorStack) {
		if (cursorStack.isEmpty()) {
			return;
		}
		List<IWarehouseStorageTile> destinations = storageMap.getDestinations(cursorStack);
		int stackSize = cursorStack.getCount();
		int moved;
		for (IWarehouseStorageTile tile : destinations) {
			moved = tile.insertItem(cursorStack, cursorStack.getCount());
			ItemStack filter = cursorStack.copy();
			filter.setCount(1);
			changeCachedQuantity(filter, moved);
			updateViewers();
			cursorStack.shrink(moved);
			if (cursorStack.getCount() <= 0) {
				break;
			}
		}
		if (stackSize != cursorStack.getCount()) {
			((EntityPlayerMP) player).updateHeldItem();
		}
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
		int count;
		int toMove;
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
		int newStackSize = stackSize;
		for (IWarehouseStorageTile tile : destinations) {
			count = tile.getQuantityStored(filter);
			toMove = toMoveMax - newStackSize;
			toMove = toMove > count ? count : toMove;
			if (toMove > 0) {
				newStackSize += toMove;
				tile.extractItem(filter, toMove);
				changeCachedQuantity(filter, -toMove);
				updateViewers();
			}
			if (newStackSize >= toMoveMax) {
				break;
			}
		}

		ItemStack newCursorStack = filter.copy();
		newCursorStack.setCount(newStackSize);
		InventoryTools.updateCursorItem((EntityPlayerMP) player, newCursorStack, !rightClick && shiftClick);
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
}
