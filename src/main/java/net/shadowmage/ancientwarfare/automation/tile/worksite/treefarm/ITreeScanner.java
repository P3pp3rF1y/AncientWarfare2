package net.shadowmage.ancientwarfare.automation.tile.worksite.treefarm;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface ITreeScanner {
	ITree scanTree(World world, BlockPos pos, BlockPos minPos, BlockPos maxPos);

	boolean matches(IBlockState state);
}
