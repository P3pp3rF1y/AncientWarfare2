package net.shadowmage.ancientwarfare.automation.tile.warehouse2;

import net.shadowmage.ancientwarfare.core.inventory.InventorySlotlessBasic;

public final class TileWarehouseStorageLarge extends TileWarehouseStorage {

    public TileWarehouseStorageLarge() {

    }

    @Override
    public int getStorageAdditionSize() {
        return 3 * super.getStorageAdditionSize();
    }
}
