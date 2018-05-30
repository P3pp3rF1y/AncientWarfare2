package net.shadowmage.ancientwarfare.automation.tile.worksite.cropfarm;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;

import java.util.List;

public interface ICrop {
	List<BlockPos> getPositionsToHarvest(World world, BlockPos pos, IBlockState state);

	boolean canBeFertilized(IBlockState state, World world, BlockPos pos);

	boolean harvest(World world, IBlockState state, BlockPos pos, int fortune, IItemHandler inventory);

	boolean matches(IBlockState state);

	default boolean matches(ItemStack stack) {
		return false;
	}

	default boolean isPlantable(ItemStack stack) { return false; }
}
