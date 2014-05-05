package net.shadowmage.ancientwarfare.automation.tile;

import java.util.List;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.shadowmage.ancientwarfare.automation.tile.TileWarehouseStorageBase.WarehouseItemFilter;

public interface IWarehouseStorageTile extends IInventory
{

public List<WarehouseItemFilter> getFilters();

public void setFilterList(List<WarehouseItemFilter> filters);

public boolean isItemValid(ItemStack item);

}
