package net.shadowmage.ancientwarfare.automation.tile.worksite.cropfarm;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;

public class HarvestableGourd implements IHarvestable {
	@Override
	public boolean readyToHarvest(World world, IBlockState state, BlockPos pos) {
		return true;
	}

	@Override
	public boolean harvest(World world, IBlockState state, BlockPos posToHarvest, EntityPlayer player, int fortune, IItemHandler inventory) {
		Block block = state.getBlock();
		NonNullList<ItemStack> stacks = NonNullList.create();

		block.getDrops(stacks, world, posToHarvest, state, fortune);

		if (!InventoryTools.canInventoryHold(inventory, stacks)) {
			return false;
		}

		if (!BlockTools.breakBlockNoDrops(world, player, posToHarvest, state)) {
			return false;
		}

		InventoryTools.insertOrDropItems(inventory, stacks, world, posToHarvest);
		return true;
	}
}
