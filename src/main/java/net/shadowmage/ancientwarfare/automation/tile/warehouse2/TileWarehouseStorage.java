package net.shadowmage.ancientwarfare.automation.tile.warehouse2;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.shadowmage.ancientwarfare.automation.container.ContainerWarehouseStorage;
import net.shadowmage.ancientwarfare.core.interfaces.IInteractableTile;
import net.shadowmage.ancientwarfare.core.inventory.InventorySlotlessBasic;
import net.shadowmage.ancientwarfare.core.inventory.ItemQuantityMap;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.tile.IBlockBreakHandler;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;
import net.shadowmage.ancientwarfare.core.util.NBTHelper;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TileWarehouseStorage extends TileControlled implements IWarehouseStorageTile, IInteractableTile, IBlockBreakHandler {

	private InventorySlotlessBasic inventory;
	private final List<WarehouseStorageFilter> filters = new ArrayList<>();

	private final Set<ContainerWarehouseStorage> viewers = new HashSet<>();

	public TileWarehouseStorage() {
		inventory = new InventorySlotlessBasic(getStorageAdditionSize());
	}

	@Override
	public ItemStack tryAdd(ItemStack cursorStack) {
		int moved = insertItem(cursorStack, cursorStack.getCount());
		getController().ifPresent(controller -> {
			ItemStack filter = cursorStack.copy();
			filter.setCount(1);
			controller.changeCachedQuantity(filter, moved);
		});
		cursorStack.shrink(moved);
		if (cursorStack.getCount() <= 0) {
			return ItemStack.EMPTY;
		}
		return cursorStack;
	}

	@Override
	protected void updateTile() {
		//noop
	}

	@Override
	public void onBlockBroken() {
		ItemQuantityMap qtm = new ItemQuantityMap();
		addItems(qtm);
		NonNullList<ItemStack> list = qtm.getItems();
		for (ItemStack stack : list) {
			InventoryTools.dropItemInWorld(world, stack, pos);
		}
	}

	@Override
	public int getStorageAdditionSize() {
		return 9 * 64;
	}

	@Override
	public void onWarehouseInventoryUpdated(TileWarehouseBase warehouse) {
		//noop
	}

	@Override
	public List<WarehouseStorageFilter> getFilters() {
		return filters;
	}

	@Override
	public void setFilters(List<WarehouseStorageFilter> filters) {
		List<WarehouseStorageFilter> old = new ArrayList<>();
		old.addAll(this.filters);
		this.filters.clear();
		this.filters.addAll(filters);
		getController().ifPresent(controller -> controller.onStorageFilterChanged(this, old, this.filters));
		updateViewers();
		markDirty();
	}

	@Override
	public void addItems(ItemQuantityMap map) {
		inventory.getItems(map);
	}

	@Override
	public int getQuantityStored(ItemStack filter) {
		return inventory.getQuantityStored(filter);
	}

	@Override
	public int getAvailableSpaceFor(ItemStack filter) {
		return inventory.getAvailableSpaceFor(filter);
	}

	@Override
	public int extractItem(ItemStack filter, int amount) {
		int removed = inventory.extractItem(filter, amount);
		updateViewersForInventory();
		if (removed > 0) {
			markDirty();
		}
		return removed;
	}

	@Override
	public int insertItem(ItemStack filter, int amount) {
		int inserted = inventory.insertItem(filter, amount);
		updateViewersForInventory();
		if (inserted > 0) {
			markDirty();
		}
		return inserted;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		inventory.readFromNBT(tag.getCompoundTag("inventory"));
		filters.addAll(NBTHelper.deserializeListFrom(tag, "filterList", WarehouseStorageFilter::new));
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setTag("inventory", inventory.writeToNBT(new NBTTagCompound()));
		NBTHelper.writeSerializablesTo(tag, "filterList", filters);
		return tag;
	}

	@Override
	public void addViewer(ContainerWarehouseStorage containerWarehouseStorage) {
		if (!hasWorld() || world.isRemote) {
			return;
		}
		viewers.add(containerWarehouseStorage);
	}

	@Override
	public void removeViewer(ContainerWarehouseStorage containerWarehouseStorage) {
		viewers.remove(containerWarehouseStorage);
	}

	private void updateViewers() {
		for (ContainerWarehouseStorage viewer : viewers) {
			viewer.onFilterListUpdated();
		}
	}

	private void updateViewersForInventory() {
		for (ContainerWarehouseStorage viewer : viewers) {
			viewer.onStorageInventoryUpdated();
		}
	}

	@Override
	public boolean onBlockClicked(EntityPlayer player, @Nullable EnumHand hand) {
		if (!player.world.isRemote) {
			NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_WAREHOUSE_STORAGE, pos);
		}
		return true;
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
		int stackSize = cursorStack.getCount();
		int moved;
		moved = insertItem(cursorStack, cursorStack.getCount());
		getController().ifPresent(controller -> {
			ItemStack filter = cursorStack.copy();
			filter.setCount(1);
			controller.changeCachedQuantity(filter, moved);
		});
		cursorStack.shrink(moved);
		if (cursorStack.getCount() <= 0) {
			player.inventory.setItemStack(ItemStack.EMPTY);
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

		int count = getQuantityStored(filter);
		int toMoveMax = filter.getMaxStackSize();
		if (rightClick && (toMoveMax > 1)) {
			if (shiftClick) {
				toMoveMax = Math.min(stackSize + 1, toMoveMax);
			} else {
				if (toMoveMax > count) {
					toMoveMax = count;
				}
				toMoveMax = (int) Math.ceil(toMoveMax / 2.0);
			}
		}
		int toMove = toMoveMax - stackSize;
		toMove = toMove > count ? count : toMove;
		if (toMove > 0) {
			extractItem(filter, toMove);
			int cacheChange = toMove; //because we need final variable for lambda
			getController().ifPresent(controller -> controller.changeCachedQuantity(filter, -cacheChange));
		}
		ItemStack newCursorStack = filter.copy();
		newCursorStack.setCount(stackSize + toMove);
		InventoryTools.updateCursorItem((EntityPlayerMP) player, newCursorStack, !rightClick && shiftClick);
	}
}
