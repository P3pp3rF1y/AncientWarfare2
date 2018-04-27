package net.shadowmage.ancientwarfare.automation.tile.worksite.cropfarm;

import net.minecraft.block.Block;
import net.minecraft.block.IGrowable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.items.IItemHandler;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;

import java.util.Collections;
import java.util.List;

public class HarvestableDefault implements IHarvestable {
	@Override
	public List<BlockPos> getPositionsToHarvest(World world, BlockPos origin, IBlockState state) {
		if (state.getBlock() instanceof IGrowable && !((IGrowable) state.getBlock()).canGrow(world, origin, state, world.isRemote)) {
			return Collections.singletonList(origin);
		}
		return Collections.emptyList();
	}

	@Override
	public boolean canBeFertilized(IBlockState state, World world, BlockPos pos) {
		return state.getBlock() instanceof IGrowable && ((IGrowable) state.getBlock()).canGrow(world, pos, state, world.isRemote);
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

		ItemStack plantable = InventoryTools.removeItem(stacks, i -> i.getItem() instanceof IPlantable, 1);

		if (!plantable.isEmpty()) {
			BlockTools.placeItemBlock(plantable, world, posToHarvest, EnumFacing.UP);
		}

		InventoryTools.insertOrDropItems(inventory, stacks, world, posToHarvest);
		return true;
	}
}
