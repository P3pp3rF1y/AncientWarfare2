package net.shadowmage.ancientwarfare.automation.tile.warehouse2;

import com.google.common.collect.Lists;
import net.minecraft.item.ItemStack;
import net.shadowmage.ancientwarfare.core.inventory.ItemHashEntry;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class WarehouseStorageMap {

	Set<IWarehouseStorageTile> unfilteredStorage = new HashSet<>();
	Set<IWarehouseStorageTile> filteredStorage = new HashSet<>();
	Map<ItemHashEntry, Set<IWarehouseStorageTile>> storageMap = new HashMap<>();

	public final void addStorageTile(IWarehouseStorageTile tile) {
		addTileFilters(tile, tile.getFilters());
	}

	public final void removeStorageTile(IWarehouseStorageTile tile) {
		removeTileFilters(tile, tile.getFilters());
	}

	public final void updateTileFilters(IWarehouseStorageTile tile, List<WarehouseStorageFilter> oldFilters, List<WarehouseStorageFilter> newFilters) {
		removeTileFilters(tile, oldFilters);
		addTileFilters(tile, newFilters);
	}

	public final Set<IWarehouseStorageTile> getFilterSetFor(ItemStack filter) {
		return getOrCreateStorageSet(new ItemHashEntry(filter));
	}

	public final Set<IWarehouseStorageTile> getUnFilteredSet() {
		return unfilteredStorage;
	}

	public final List<IWarehouseStorageTile> getDestinations() {
		List<IWarehouseStorageTile> out = Lists.newArrayList();

		out.addAll(filteredStorage);
		out.addAll(unfilteredStorage);

		return out;
	}

	public final List<IWarehouseStorageTile> getDestinations(ItemStack filter) {
		List<IWarehouseStorageTile> out = Lists.newArrayList();
		ItemHashEntry key = new ItemHashEntry(filter);
		Set<IWarehouseStorageTile> set = getOrCreateStorageSet(key);
		out.addAll(set);
		out.addAll(unfilteredStorage);

		return out;
	}

	private Set<IWarehouseStorageTile> getOrCreateStorageSet(ItemHashEntry key) {
		Set<IWarehouseStorageTile> set = storageMap.computeIfAbsent(key, k -> new HashSet<>());
		return set;
	}

	private void removeTileFilters(IWarehouseStorageTile tile, List<WarehouseStorageFilter> filters) {
		if (filters.isEmpty()) {
			unfilteredStorage.remove(tile);
		} else {
			filteredStorage.remove(tile);
			for (WarehouseStorageFilter filter : filters) {
				if (filter.hashKey == null) {
					continue;
				}
				getOrCreateStorageSet(filter.hashKey).remove(tile);
			}
		}
	}

	private void addTileFilters(IWarehouseStorageTile tile, List<WarehouseStorageFilter> filters) {
		if (filters.isEmpty()) {
			unfilteredStorage.add(tile);
		} else {
			filteredStorage.add(tile);
			for (WarehouseStorageFilter filter : filters) {
				if (filter.hashKey == null) {
					continue;
				}
				getOrCreateStorageSet(filter.hashKey).add(tile);
			}
		}
	}

}
