package net.shadowmage.ancientwarfare.automation.tile.warehouse2;

import net.shadowmage.ancientwarfare.core.inventory.InventorySlotlessBasic;

public class TileWarehouseStorageMedium extends TileWarehouseStorage {

    public TileWarehouseStorageMedium() {
        inventory = new InventorySlotlessBasic(18 * 64);
    }

}
