package net.shadowmage.ancientwarfare.npc.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.npc.orders.CombatOrder;

import java.util.ArrayList;
import java.util.List;

public class ItemCombatOrder extends ItemOrders {

	public ItemCombatOrder() {
		super("combat_order");
	}

	@Override
	public List<BlockPos> getPositionsForRender(ItemStack stack) {
		List<BlockPos> positionList = new ArrayList<>();
		CombatOrder order = CombatOrder.getCombatOrder(stack);
		if (order != null && !order.isEmpty()) {
			for (int i = 0; i < order.size(); i++) {
				positionList.add(order.get(i).up());
			}
		}
		return positionList;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		if (!world.isRemote)
			NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_NPC_COMBAT_ORDER, 0, 0, 0);
		return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
	}

	@Override
	public void onKeyAction(EntityPlayer player, ItemStack stack, ItemAltFunction altFunction) {
		CombatOrder order = CombatOrder.getCombatOrder(stack);
		if (order == null) {
			return;
		}
		if (player.isSneaking()) {
			order.clear();
			order.write(stack);
		} else {
			BlockPos pos = BlockTools.getBlockClickedOn(player, player.world, false);
			if (pos != null) {
				order.addPatrolPoint(player.world, pos);
				order.write(stack);
				addMessage(player);
			}
		}
	}

}
