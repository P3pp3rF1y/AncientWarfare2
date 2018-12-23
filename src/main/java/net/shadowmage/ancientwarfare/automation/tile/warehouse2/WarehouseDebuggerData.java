package net.shadowmage.ancientwarfare.automation.tile.warehouse2;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.storage.WorldSavedData;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class WarehouseDebuggerData extends WorldSavedData {
	private static final int TRACKING_INTERVAL = 20;

	private Set<BlockPos> trackedWarehouses = new HashSet<>();
	private Map<BlockPos, Map<ItemStack, Integer>> itemCounts = new HashMap<>();

	private int currentCooldown = 0;

	public WarehouseDebuggerData(String name) {
		super(name);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {

	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		return compound;
	}

	public boolean decreaseAndCheckCooldown() {
		currentCooldown--;
		if (currentCooldown <= 0) {
			currentCooldown = TRACKING_INTERVAL;
			return true;
		}
		return false;
	}

	public Set<BlockPos> getTrackedWarehouses() {
		return trackedWarehouses;
	}

	public void removeTrackedWarehouse(BlockPos warehousePos) {
		trackedWarehouses.remove(warehousePos);
		itemCounts.remove(warehousePos);
	}

	public Map<ItemStack, Integer> getItemCounts(BlockPos warehousePos) {
		if (!itemCounts.containsKey(warehousePos)) {
			itemCounts.put(warehousePos, new HashMap<>());
		}
		return itemCounts.get(warehousePos);
	}

	public void addTrackedWarehouse(BlockPos pos) {
		trackedWarehouses.add(pos);
	}

	public void setItemCounts(BlockPos pos, Map<ItemStack, Integer> itemCounts) {
		this.itemCounts.put(pos, itemCounts);
	}

	public void removeItem(BlockPos warehousePos, ItemStack stack, int count) {
		if (!trackedWarehouses.contains(warehousePos)) {
			return;
		}

		Iterator<Map.Entry<ItemStack, Integer>> it = itemCounts.get(warehousePos).entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<ItemStack, Integer> entry = it.next();
			if (InventoryTools.doItemStacksMatch(entry.getKey(), stack)) {
				entry.setValue(entry.getValue() - count);
				if (entry.getValue() <= 0) {
					it.remove();
				}
				break;
			}
		}
	}

	public void addItem(BlockPos warehousePos, ItemStack stack, int count) {
		if (!trackedWarehouses.contains(warehousePos)) {
			return;
		}

		for (Map.Entry<ItemStack, Integer> entry : itemCounts.get(warehousePos).entrySet()) {
			if (InventoryTools.doItemStacksMatch(entry.getKey(), stack)) {
				entry.setValue(entry.getValue() + count);
				return;
			}
		}
		itemCounts.get(warehousePos).put(stack.copy(), count);
	}

	public boolean isTrackingWarehouse(BlockPos pos) {
		return trackedWarehouses.contains(pos);
	}
}
