package net.shadowmage.ancientwarfare.structure.api;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;

public interface IStructureBuilder {

	void placeBlock(BlockPos pos, Block block, int meta, int priority);
}
