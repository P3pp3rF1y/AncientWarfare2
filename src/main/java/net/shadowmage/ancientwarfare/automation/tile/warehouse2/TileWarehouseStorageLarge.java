package net.shadowmage.ancientwarfare.automation.tile.warehouse2;

import net.shadowmage.ancientwarfare.core.inventory.InventorySlotlessBasic;

public class TileWarehouseStorageLarge extends TileWarehouseStorage {

    public TileWarehouseStorageLarge() {
        inventory = new InventorySlotlessBasic(27 * 64);
    }

}
