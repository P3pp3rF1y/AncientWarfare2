package net.shadowmage.ancientwarfare.structure.template.plugin.defaultplugins.blockrules;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.storage.WorldInfo;

public class RuleWorld extends World {
	private final IBlockState ruleState;

	protected RuleWorld(IBlockState ruleState) {
		super(null, new WorldInfo() {}, new WorldProvider() {
			@Override
			public DimensionType getDimensionType() {
				return DimensionType.OVERWORLD;
			}
		}, null, true);
		this.ruleState = ruleState;
	}

	@Override
	public IBlockState getBlockState(BlockPos pos) {
		return ruleState;
	}

	@Override
	protected IChunkProvider createChunkProvider() {
		return null;
	}

	@Override
	protected boolean isChunkLoaded(int x, int z, boolean allowEmpty) {
		return false;
	}
}
