package net.shadowmage.ancientwarfare.structure.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.SlotItemHandler;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.structure.tile.TileStructureScanner;

public class ContainerStructureScannerBlock extends ContainerBase {
	private TileStructureScanner scanner;

	public ContainerStructureScannerBlock(EntityPlayer player, int x, int y, int z) {
		super(player);

		scanner = (TileStructureScanner) player.world.getTileEntity(new BlockPos(x, y, z));
		addPlayerSlots();

		Slot slot = new SlotItemHandler(scanner.getScannerInventory(), 0, 8, 8);

		addSlotToContainer(slot);
	}

}
