package net.shadowmage.ancientwarfare.automation.registry;

import net.minecraft.block.state.IBlockState;
import net.shadowmage.ancientwarfare.automation.tile.worksite.treefarm.ITree;
import net.shadowmage.ancientwarfare.automation.tile.worksite.treefarm.TreeDefault;

public class TreeFarmRegistry {
	public static ITree getTree(IBlockState state) {
		return new TreeDefault();
	}
}
