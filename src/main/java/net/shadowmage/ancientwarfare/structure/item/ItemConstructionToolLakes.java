package net.shadowmage.ancientwarfare.structure.item;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.util.BlockTools;

import java.util.Set;

public class ItemConstructionToolLakes extends ItemBaseStructure {

	public ItemConstructionToolLakes(String name) {
		super(name);
		setMaxStackSize(1);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		ItemStack stack = player.getHeldItem(hand);

		if (world.isRemote) {
			return new ActionResult<>(EnumActionResult.SUCCESS, stack);
		}
		BlockPos pos = BlockTools.getBlockClickedOn(player, world, true);
		if (pos == null) {
			return new ActionResult<>(EnumActionResult.PASS, stack);
		}
		if (!world.isAirBlock(pos)) {
			return new ActionResult<>(EnumActionResult.PASS, stack);
		}
		IBlockState state = world.getBlockState(pos);
		FloodFillPathfinder pf = new FloodFillPathfinder(player.world, pos, state.getBlock(), state, false, true);
		Set<BlockPos> blocks = pf.doFloodFill();
		for (BlockPos p : blocks) {
			player.world.setBlockState(p, Blocks.FLOWING_WATER.getDefaultState());
		}
		if (!player.capabilities.isCreativeMode) {
			stack.shrink(1);
		}
		return new ActionResult<>(EnumActionResult.SUCCESS, stack);
	}

}
