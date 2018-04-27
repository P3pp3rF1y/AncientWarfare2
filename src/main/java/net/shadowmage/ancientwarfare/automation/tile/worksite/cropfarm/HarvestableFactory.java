package net.shadowmage.ancientwarfare.automation.tile.worksite.cropfarm;

import net.minecraft.block.BlockStem;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;

public class HarvestableFactory {
	public static IHarvestable getHarvestable(IBlockState state) {
		if (state.getMaterial() == Material.GOURD) {
			return new HarvestableGourd();
		} else if (state.getBlock() instanceof BlockStem) {
			return new HarvestableStem();
		}
		return new HarvestableDefault();
	}
}
