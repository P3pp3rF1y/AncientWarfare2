package net.shadowmage.ancientwarfare.automation.tile.worksite.fruitfarm;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;

public interface IFruit {
	boolean matches(IBlockState state);

	default boolean matches(ItemStack stack) {
		return false;
	}

	boolean isRipe(IBlockState state);

	boolean pick(World world, IBlockState state, BlockPos pos, int fortune, IItemHandler inventory);

	boolean isPlantable();

	default boolean canPlant(World world, BlockPos currentPos, IBlockState state) {
		return false;
	}

	default boolean plant(World world, BlockPos plantPos) {
		return false;
	}
}
