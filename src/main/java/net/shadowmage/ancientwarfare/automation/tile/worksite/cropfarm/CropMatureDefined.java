package net.shadowmage.ancientwarfare.automation.tile.worksite.cropfarm;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.util.parsing.BlockStateMatcher;
import net.shadowmage.ancientwarfare.core.util.parsing.PropertyStateMatcher;

import java.util.Collections;
import java.util.List;

public class CropMatureDefined extends CropDefault {
	private BlockStateMatcher stateMatcher;
	private PropertyStateMatcher matureStateMatcher;

	public CropMatureDefined(BlockStateMatcher stateMatcher, PropertyStateMatcher matureStateMatcher) {
		this.stateMatcher = stateMatcher;
		this.matureStateMatcher = matureStateMatcher;
	}

	@Override
	public boolean matches(IBlockState state) {
		return stateMatcher.test(state);
	}

	@Override
	public List<BlockPos> getPositionsToHarvest(World world, BlockPos pos, IBlockState state) {
		return matureStateMatcher.test(state) ? Collections.singletonList(pos) : Collections.emptyList();
	}
}
