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
import net.shadowmage.ancientwarfare.npc.orders.RoutingOrder;

import java.util.ArrayList;
import java.util.List;

public class ItemRoutingOrder extends ItemOrders {

	public ItemRoutingOrder() {
		super("routing_order");
	}

	@Override
	public List<BlockPos> getPositionsForRender(ItemStack stack) {
		List<BlockPos> positionList = new ArrayList<>();
		RoutingOrder order = RoutingOrder.getRoutingOrder(stack);
		if (order != null && !order.isEmpty()) {
			for (RoutingOrder.RoutePoint e : order.getEntries()) {
				positionList.add(e.getTarget());
			}
		}
		return positionList;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		if (!world.isRemote)
			NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_NPC_ROUTING_ORDER, 0, 0, 0);
		return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
	}

	@Override
	public void onKeyAction(EntityPlayer player, ItemStack stack, ItemAltFunction altFunction) {
		RoutingOrder order = RoutingOrder.getRoutingOrder(stack);
		if (order != null) {
			RayTraceResult hit = RayTraceUtils.getPlayerTarget(player, 5, 0);
			if (hit != null && hit.typeOfHit == RayTraceResult.Type.BLOCK) {
				order.addRoutePoint(hit.sideHit, hit.getBlockPos());
				order.write(stack);
				addMessage(player);
			}
		}
	}

}
