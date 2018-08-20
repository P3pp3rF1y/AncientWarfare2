package net.shadowmage.ancientwarfare.npc.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.util.EntityTools;
import net.shadowmage.ancientwarfare.npc.init.AWNPCItems;
import net.shadowmage.ancientwarfare.npc.orders.UpkeepOrder;

import javax.annotation.Nonnull;

public class ContainerUpkeepOrder extends ContainerBase {

	private EnumHand hand;
	public final UpkeepOrder upkeepOrder;
	public final ItemStack upkeepBlock;
	private boolean hasChanged;

	public ContainerUpkeepOrder(EntityPlayer player, int x, int y, int z) {
		super(player);
		this.hand = EntityTools.getHandHoldingItem(player, AWNPCItems.UPKEEP_ORDER);
		@Nonnull ItemStack stack = player.getHeldItem(hand);
		if (stack.isEmpty()) {
			throw new IllegalArgumentException("Cannot open Work Order GUI for null stack/item.");
		}
		upkeepOrder = UpkeepOrder.getUpkeepOrder(stack);
		if (upkeepOrder == null) {
			throw new IllegalArgumentException("Upkeep orders was null for some reason");
		}
		if (upkeepOrder.getUpkeepPosition() != null) {
			upkeepBlock = new ItemStack(player.world.getBlockState(upkeepOrder.getUpkeepPosition()).getBlock());
		} else {
			upkeepBlock = ItemStack.EMPTY;
		}
		addPlayerSlots();
		removeSlots();
	}

	@Override
	public void handlePacketData(NBTTagCompound tag) {
		if (tag.hasKey("upkeepOrder")) {
			upkeepOrder.deserializeNBT(tag.getCompoundTag("upkeepOrder"));
			hasChanged = true;
		}
	}

	@Override
	public void onContainerClosed(EntityPlayer par1EntityPlayer) {
		super.onContainerClosed(par1EntityPlayer);
		if (hasChanged && !player.world.isRemote) {
			upkeepOrder.write(player.getHeldItem(hand));
		}
	}

	public void onClose() {
		NBTTagCompound outer = new NBTTagCompound();
		outer.setTag("upkeepOrder", upkeepOrder.serializeNBT());
		sendDataToServer(outer);
	}
}
