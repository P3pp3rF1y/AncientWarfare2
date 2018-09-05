package net.shadowmage.ancientwarfare.npc.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.npc.orders.UpkeepOrder;

import java.util.Collections;
import java.util.List;

public class ItemUpkeepOrder extends ItemOrders {

	public ItemUpkeepOrder() {
		super("upkeep_order");
	}

	@Override
	public List<BlockPos> getPositionsForRender(ItemStack stack) {
		return UpkeepOrder.getUpkeepOrder(stack).map(o -> o.getUpkeepPosition().map(Collections::singletonList).orElse(Collections.emptyList()))
				.orElse(Collections.emptyList());
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		if (!world.isRemote)
			NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_NPC_UPKEEP_ORDER, 0, 0, 0);
		return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
	}

	@Override
	public void onKeyAction(EntityPlayer player, ItemStack stack, ItemAltFunction altFunction) {
		UpkeepOrder.getUpkeepOrder(stack).ifPresent(upkeepOrder -> {
					BlockPos hit = BlockTools.getBlockClickedOn(player, player.world, false);
					if (hit != null) {
						if (upkeepOrder.addUpkeepPosition(player.world, hit)) {
							upkeepOrder.write(stack);
							player.sendMessage(new TextComponentTranslation("guistrings.npc.upkeep_point_set"));
						}
					} else
						NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_NPC_UPKEEP_ORDER, 0, 0, 0);
				}
		);
	}

}
