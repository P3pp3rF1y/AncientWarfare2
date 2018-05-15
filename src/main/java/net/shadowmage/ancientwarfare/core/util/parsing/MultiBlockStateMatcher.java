package net.shadowmage.ancientwarfare.core.util.parsing;

import net.minecraft.block.state.IBlockState;

import java.util.function.Predicate;

public class MultiBlockStateMatcher implements Predicate<IBlockState> {
	private BlockStateMatcher[] blockStateMatchers;

	public MultiBlockStateMatcher(BlockStateMatcher... blockStateMatchers) {
		this.blockStateMatchers = blockStateMatchers;
	}

	@Override
	public boolean test(IBlockState state) {
		for (BlockStateMatcher blockStateMatcher : blockStateMatchers) {
			if (blockStateMatcher.test(state)) {
				return true;
			}
		}

		return false;
	}
}
