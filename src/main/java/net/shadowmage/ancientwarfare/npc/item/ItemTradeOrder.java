package net.shadowmage.ancientwarfare.npc.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.util.RayTraceUtils;
import net.shadowmage.ancientwarfare.npc.orders.TradeOrder;

import java.util.ArrayList;
import java.util.List;

public class ItemTradeOrder extends ItemOrders {

	public ItemTradeOrder() {
		super("trade_order");
	}

	@Override
	public List<BlockPos> getPositionsForRender(ItemStack stack) {
		List<BlockPos> positionList = new ArrayList<>();
		TradeOrder order = TradeOrder.getTradeOrder(stack);
		if (order != null && order.getRoute().size() > 0) {
			for (int i = 0; i < order.getRoute().size(); i++) {
				positionList.add(order.getRoute().get(i).getPosition());
			}
		}
		return positionList;
	}

	@Override
	public boolean onKeyActionClient(EntityPlayer player, ItemStack stack, ItemAltFunction altFunction) {
		return altFunction == ItemAltFunction.ALT_FUNCTION_1 || altFunction == ItemAltFunction.ALT_FUNCTION_2 || altFunction == ItemAltFunction.ALT_FUNCTION_3;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		if (!world.isRemote)
			NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_NPC_TRADE_ORDER, 0, 0, 0);
		return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
	}

	@Override
	public void onKeyAction(EntityPlayer player, ItemStack stack, ItemAltFunction altFunction) {
		RayTraceResult hit = RayTraceUtils.getPlayerTarget(player, 5, 0);
		if (hit == null || hit.typeOfHit != RayTraceResult.Type.BLOCK) {
			return;
		}
		TradeOrder order = TradeOrder.getTradeOrder(stack);
		if (altFunction == ItemAltFunction.ALT_FUNCTION_1) {
			order.getRoute().addRoutePoint(hit.getBlockPos());
			order.write(stack);
		} else if (altFunction == ItemAltFunction.ALT_FUNCTION_2) {
			order.getRestockData().setDepositPoint(hit.getBlockPos(), hit.sideHit);
			order.write(stack);
		} else if (altFunction == ItemAltFunction.ALT_FUNCTION_3) {
			order.getRestockData().setWithdrawPoint(hit.getBlockPos(), hit.sideHit);
			order.write(stack);
		}
	}

}
