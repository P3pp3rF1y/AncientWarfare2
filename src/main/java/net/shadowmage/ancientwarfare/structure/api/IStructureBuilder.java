package net.shadowmage.ancientwarfare.structure.api;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;

public interface IStructureBuilder {

	boolean placeBlock(BlockPos pos, IBlockState state, int priority);
}
