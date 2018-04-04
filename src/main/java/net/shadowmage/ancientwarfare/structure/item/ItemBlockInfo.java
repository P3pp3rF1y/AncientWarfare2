package net.shadowmage.ancientwarfare.structure.item;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.structure.block.BlockDataManager;

public class ItemBlockInfo extends ItemBaseStructure {

	public ItemBlockInfo(String name) {
		super(name);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		if (!world.isRemote) {
			BlockPos pos = BlockTools.getBlockClickedOn(player, player.world, false);
			if (pos != null) {
				IBlockState state = world.getBlockState(pos);
				Block block = state.getBlock();
				AWLog.logDebug("block: " + BlockDataManager.INSTANCE.getNameForBlock(block) + ", meta: " + block.getMetaFromState(state)); //TODO print property values?
				if (block.hasTileEntity(state)) {
					AWLog.logDebug("tile: " + world.getTileEntity(pos).getClass());
				}
			}
		}
		return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
	}
}
