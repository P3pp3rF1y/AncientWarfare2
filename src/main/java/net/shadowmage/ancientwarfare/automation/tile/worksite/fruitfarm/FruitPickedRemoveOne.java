package net.shadowmage.ancientwarfare.automation.tile.worksite.fruitfarm;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;
import net.shadowmage.ancientwarfare.core.util.parsing.BlockStateMatcher;
import net.shadowmage.ancientwarfare.core.util.parsing.PropertyState;
import net.shadowmage.ancientwarfare.core.util.parsing.PropertyStateMatcher;

public class FruitPickedRemoveOne implements IFruit {
	private BlockStateMatcher stateMatcher;
	private PropertyStateMatcher ripeStateMatcher;
	private PropertyState newState;

	public FruitPickedRemoveOne(BlockStateMatcher stateMatcher, PropertyStateMatcher ripeStateMatcher, PropertyState newState) {
		this.stateMatcher = stateMatcher;
		this.ripeStateMatcher = ripeStateMatcher;
		this.newState = newState;
	}

	@Override
	public boolean matches(IBlockState state) {
		return stateMatcher.test(state);
	}

	@Override
	public boolean isRipe(IBlockState state) {
		return ripeStateMatcher.test(state);
	}

	@Override
	public boolean pick(World world, IBlockState state, BlockPos pos, EntityPlayer player, int fortune, IItemHandler inventory) {
		//TODO using deprecated getDrops here just because of pam's harvestcraft change to proper one in the future
		NonNullList<ItemStack> drops = InventoryTools.toNonNullList(state.getBlock().getDrops(world, pos, state, fortune));

		if (drops.isEmpty() || !InventoryTools.canInventoryHold(inventory, drops)) {
			return false;
		}

		world.setBlockState(pos, newState.update(state));

		InventoryTools.removeItem(drops, s -> InventoryTools.doItemStacksMatchRelaxed(s, drops.get(0)), 1);

		InventoryTools.insertOrDropItems(inventory, drops, world, pos);

		return true;
	}

	@Override
	public boolean isPlantable() {
		return false;
	}
}
