package net.shadowmage.ancientwarfare.automation.tile.warehouse2;

public final class TileWarehouseStorageLarge extends TileWarehouseStorage {

	public TileWarehouseStorageLarge() {

	}

	@Override
	public int getStorageAdditionSize() {
		return 3 * super.getStorageAdditionSize();
	}
}
