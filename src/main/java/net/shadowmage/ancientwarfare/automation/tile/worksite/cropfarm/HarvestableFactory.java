package net.shadowmage.ancientwarfare.automation.tile.worksite.cropfarm;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;

public class HarvestableFactory {
	public static IHarvestable getHarvestable(IBlockState state) {
		if (state.getMaterial() == Material.GOURD) {
			return new HarvestableGourd();
		}
		return new HarvestableDefault();
	}
}
