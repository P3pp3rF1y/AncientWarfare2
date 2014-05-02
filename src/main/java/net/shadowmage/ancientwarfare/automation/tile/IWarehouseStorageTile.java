package net.shadowmage.ancientwarfare.automation.tile;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.shadowmage.ancientwarfare.automation.tile.TileWarehouseStorageBase.WarehouseItemFilter;

public interface IWarehouseStorageTile
{

public List<WarehouseItemFilter> getFilters();

public boolean isItemValid(ItemStack item);

}
