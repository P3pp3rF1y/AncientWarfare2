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
import java.util.Optional;

public class ContainerUpkeepOrder extends ContainerBase {
	private static final String UPKEEP_ORDER_TAG = "upkeepOrder";
	private EnumHand hand;
	public final UpkeepOrder upkeepOrder;
	public final ItemStack upkeepBlock;
	private boolean hasChanged;

	@SuppressWarnings("unused") //used in reflection
	public ContainerUpkeepOrder(EntityPlayer player, int x, int y, int z) {
		super(player);
		this.hand = EntityTools.getHandHoldingItem(player, AWNPCItems.UPKEEP_ORDER);
		ItemStack stack = player.getHeldItem(hand);
		if (stack.isEmpty()) {
			throw new IllegalArgumentException("Cannot open Work Order GUI for null stack/item.");
		}
		Optional<UpkeepOrder> order = UpkeepOrder.getUpkeepOrder(stack);
		if (!order.isPresent()) {
			throw new IllegalArgumentException("Upkeep orders was missing for some reason");
		}
		upkeepOrder = order.get();
		upkeepBlock = upkeepOrder.getUpkeepPosition().map(blockPos -> new ItemStack(player.world.getBlockState(blockPos).getBlock())).orElse(ItemStack.EMPTY);
		addPlayerSlots();
		removeSlots();
	}

	@Override
	public void handlePacketData(NBTTagCompound tag) {
		if (tag.hasKey(UPKEEP_ORDER_TAG)) {
			upkeepOrder.deserializeNBT(tag.getCompoundTag(UPKEEP_ORDER_TAG));
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
		outer.setTag(UPKEEP_ORDER_TAG, upkeepOrder.serializeNBT());
		sendDataToServer(outer);
	}
}
