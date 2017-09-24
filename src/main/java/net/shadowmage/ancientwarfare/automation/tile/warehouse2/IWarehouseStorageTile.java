package net.shadowmage.ancientwarfare.automation.tile.warehouse2;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.shadowmage.ancientwarfare.automation.container.ContainerWarehouseStorage;
import net.shadowmage.ancientwarfare.core.inventory.ItemQuantityMap;

import java.util.List;

public interface IWarehouseStorageTile {

    public int getStorageAdditionSize();

    public void onWarehouseInventoryUpdated(TileWarehouseBase warehouse);

    public List<WarehouseStorageFilter> getFilters();

    public void setFilters(List<WarehouseStorageFilter> filters);

    public void addItems(ItemQuantityMap map);

    int getQuantityStored(ItemStack filter);

    int getAvailableSpaceFor(ItemStack filter);

    int extractItem(ItemStack filter, int amount);

    int insertItem(ItemStack filter, int amount);

    public void addViewer(ContainerWarehouseStorage containerWarehouseStorage);

    public void removeViewer(ContainerWarehouseStorage containerWarehouseStorage);

    public void handleSlotClick(EntityPlayer player, ItemStack item, boolean isShiftClick);

    public ItemStack tryAdd(ItemStack stack);

}
