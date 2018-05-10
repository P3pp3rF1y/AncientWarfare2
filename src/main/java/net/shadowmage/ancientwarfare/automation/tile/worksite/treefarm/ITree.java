package net.shadowmage.ancientwarfare.automation.tile.worksite.treefarm;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public interface ITree {
	List<BlockPos> getTrunkBlocks(IBlockState blockType, World world, BlockPos pos);

	List<BlockPos> getLeafBlocks(IBlockState blockType, World world, BlockPos pos);

	boolean bottomBlockMatches(IBlockState state);
}
