package net.shadowmage.ancientwarfare.automation.tile.worksite.treefarm;

import net.minecraft.util.math.BlockPos;

import java.util.List;

public interface ITree {
	List<BlockPos> getTrunkPositions();

	List<BlockPos> getLeafPositions();
}
