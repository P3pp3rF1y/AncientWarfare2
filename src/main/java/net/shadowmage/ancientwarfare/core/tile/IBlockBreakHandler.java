package net.shadowmage.ancientwarfare.core.tile;

import net.minecraft.block.state.IBlockState;

public interface IBlockBreakHandler {
	void onBlockBroken(IBlockState state);
}
