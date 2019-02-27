package net.shadowmage.ancientwarfare.structure.tile;

import net.shadowmage.ancientwarfare.core.tile.IBlockBreakHandler;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;

public class TileLootBasket extends TileAdvancedLootChest implements IBlockBreakHandler {
	@Override
	public void onBlockBroken() {
		InventoryTools.getItemHandlerFrom(this, null).ifPresent(inv -> InventoryTools.dropItemsInWorld(world, inv, pos));
	}
}
