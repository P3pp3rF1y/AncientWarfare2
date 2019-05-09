package net.shadowmage.ancientwarfare.structure.tile;

import net.minecraft.block.state.IBlockState;
import net.shadowmage.ancientwarfare.core.tile.IBlockBreakHandler;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;

public class TileLootBasket extends TileAdvancedLootChest implements IBlockBreakHandler {
	@Override
	public void onBlockBroken(IBlockState state) {
		InventoryTools.getItemHandlerFrom(this, null).ifPresent(inv -> InventoryTools.dropItemsInWorld(world, inv, pos));
	}
}
