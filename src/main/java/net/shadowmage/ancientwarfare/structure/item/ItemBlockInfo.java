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
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.core.util.WorldTools;
import net.shadowmage.ancientwarfare.structure.AncientWarfareStructure;

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
				//noinspection ConstantConditions
				AncientWarfareStructure.LOG.info("block: " + block.getRegistryName().toString() + ", meta: " + block.getMetaFromState(state));
				if (block.hasTileEntity(state)) {
					AncientWarfareStructure.LOG.info("tile: " + WorldTools.getTile(world, pos).map(t -> t.getClass().toString()).orElse(""));
				}
			}
		}
		return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
	}
}
