package net.shadowmage.ancientwarfare.automation.tile;

import java.util.List;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.shadowmage.ancientwarfare.core.util.InventoryTools.ItemQuantityMap;

public interface IWarehouseStorageTile
{

/**
 * user-set name for this inventory
 * @return
 */
public String getInventoryName();

/**
 * @return the max number of filters that this tile can have
 */
public int getMaxFilterCount();

/**
 * @return a list containing the filters for this tile
 */
public List<WarehouseItemFilter> getFilters();

/**
 * set this tiles filter list to the input filter-list.<br>
 * any filters beyond getMaxFilterCount() may be discarded
 * @param filters
 */
public void setFilterList(List<WarehouseItemFilter> filters);

/**
 * Is the input item valid for this storage-tile
 * @param item
 * @return
 */
public boolean isItemValid(ItemStack item);

/**
 * called on-block-break from block, tile should drop any items contained in its
 * inventory into the world.  Only called server-side
 */
public void dropInventoryInWorld();

public void addInventoryContentsToMap(ItemQuantityMap itemMap);

/**
 * remove the input item up to the input quantity.
 * @param filter
 * @param quantity
 * @return the number that was actually removed from the inventory
 */
public int removeItem(ItemStack filter, int quantity);

/**
 * merge the input item into this tiles inventory
 * @param item the properly formed item-stack to merge into this inventory
 * @return any unused/unmerged item, null for completely merged
 */
public ItemStack addItem(ItemStack item);

}
