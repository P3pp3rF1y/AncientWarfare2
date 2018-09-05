package net.shadowmage.ancientwarfare.npc.entity;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

import java.util.Optional;

public interface IKeepFood {

	int getUpkeepAmount();

	EnumFacing getUpkeepBlockSide();

	int getUpkeepDimensionId();

	void setUpkeepAutoPosition(BlockPos pos);

	Optional<BlockPos> getUpkeepPoint();
}
