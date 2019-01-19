package net.shadowmage.ancientwarfare.automation.tile.warehouse2;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.shadowmage.ancientwarfare.automation.container.ContainerWarehouseStorage;
import net.shadowmage.ancientwarfare.core.inventory.ItemQuantityMap;

import java.util.List;

public interface IWarehouseStorageTile {

	int getStorageAdditionSize();

	void onWarehouseInventoryUpdated(TileWarehouseBase warehouse);

	List<WarehouseStorageFilter> getFilters();

	void setFilters(List<WarehouseStorageFilter> filters);

	void addItems(ItemQuantityMap map);

	int getQuantityStored(ItemStack filter);

	int getAvailableSpaceFor(ItemStack filter);

	int extractItem(ItemStack filter, int amount);

	int insertItem(ItemStack filter, int amount);

	void addViewer(ContainerWarehouseStorage containerWarehouseStorage);

	void removeViewer(ContainerWarehouseStorage containerWarehouseStorage);

	void handleSlotClick(EntityPlayer player, ItemStack item, boolean isShiftClick, boolean isRightClick);

	ItemStack tryAdd(ItemStack stack);

}
