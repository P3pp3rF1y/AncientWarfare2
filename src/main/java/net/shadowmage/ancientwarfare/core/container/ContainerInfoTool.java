package net.shadowmage.ancientwarfare.core.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.shadowmage.ancientwarfare.core.init.AWCoreItems;
import net.shadowmage.ancientwarfare.core.util.EntityTools;

public class ContainerInfoTool extends ContainerBase {
	private final ItemStack infoTool;

	@SuppressWarnings("unused") //parameters used in reflection
	public ContainerInfoTool(EntityPlayer player, int x, int y, int z) {
		super(player);

		infoTool = player.getHeldItem(EntityTools.getHandHoldingItem(player, AWCoreItems.INFO_TOOL));

		addPlayerSlots(8);
	}

	public void printItemInfo(int slotId) {
		if (slotId >= 0 && slotId < inventoryItemStacks.size()) {
			ItemStack stack = inventorySlots.get(slotId).getStack();
			if (!stack.isEmpty()) {
				AWCoreItems.INFO_TOOL.printItemInfo(player, infoTool, stack);
			}
		}
	}
}
