package net.shadowmage.ancientwarfare.npc.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.util.EntityTools;
import net.shadowmage.ancientwarfare.npc.item.AWNPCItems;
import net.shadowmage.ancientwarfare.npc.orders.WorkOrder;

import javax.annotation.Nonnull;

public class ContainerWorkOrder extends ContainerBase {

	private EnumHand hand;
	public final WorkOrder wo;
	private boolean hasChanged;

	public ContainerWorkOrder(EntityPlayer player, int x, int y, int z) {
		super(player);
		hand = EntityTools.getHandHoldingItem(player, AWNPCItems.workOrder);
		@Nonnull ItemStack stack = player.getHeldItem(hand);
		if (stack.isEmpty()) {
			throw new IllegalArgumentException("Cannot open Work Order GUI for empty stack/item.");
		}
		wo = WorkOrder.getWorkOrder(stack);
		if (wo == null) {
			throw new IllegalArgumentException("Work orders was null for some reason");
		}
	}

	@Override
	public void handlePacketData(NBTTagCompound tag) {
		if (tag.hasKey("wo")) {
			wo.deserializeNBT(tag.getCompoundTag("wo"));
			hasChanged = true;
		}
	}

	@Override
	public void onContainerClosed(EntityPlayer par1EntityPlayer) {
		super.onContainerClosed(par1EntityPlayer);
		if (hasChanged && !player.world.isRemote) {
			wo.write(player.getHeldItem(hand));
		}
	}

	public void onClose() {
		NBTTagCompound outer = new NBTTagCompound();
		outer.setTag("wo", wo.serializeNBT());
		sendDataToServer(outer);
	}
}
