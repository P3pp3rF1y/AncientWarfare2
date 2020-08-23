package net.shadowmage.ancientwarfare.npc.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.util.EntityTools;
import net.shadowmage.ancientwarfare.npc.init.AWNPCItems;
import net.shadowmage.ancientwarfare.npc.orders.CombatOrder;

import javax.annotation.Nonnull;

public class ContainerCombatOrder extends ContainerBase {

	private boolean hasChanged;
	private EnumHand hand;
	public final CombatOrder combatOrder;

	public ContainerCombatOrder(EntityPlayer player, int x, int y, int z) {
		super(player);
		this.hand = EntityTools.getHandHoldingItem(player, AWNPCItems.COMBAT_ORDER);
		ItemStack stack = player.getHeldItem(hand);
		if (stack.isEmpty()) {
			throw new IllegalArgumentException("Cannot open Combat Order GUI for empty stack/item.");
		}
		combatOrder = CombatOrder.getCombatOrder(stack);
		if (combatOrder == null) {
			throw new IllegalArgumentException("Combat orders was null for some reason");
		}
		addPlayerSlots();
		removeSlots();
	}

	@Override
	public void handlePacketData(NBTTagCompound tag) {
		if (tag.hasKey("combatOrder")) {
			combatOrder.deserializeNBT(tag.getCompoundTag("combatOrder"));
			hasChanged = true;
		}
	}

	@Override
	public void onContainerClosed(EntityPlayer par1EntityPlayer) {
		super.onContainerClosed(par1EntityPlayer);
		if (hasChanged && !player.world.isRemote) {
			combatOrder.write(player.getHeldItem(hand));
		}
	}

	public void close() {
		NBTTagCompound outer = new NBTTagCompound();
		outer.setTag("combatOrder", combatOrder.serializeNBT());
		sendDataToServer(outer);
	}
}
