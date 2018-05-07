package net.shadowmage.ancientwarfare.automation.tile.worksite.cropfarm;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;

public class CropGourd extends CropBreakOnly {
	@Override
	public boolean matches(IBlockState state) {
		return state.getMaterial() == Material.GOURD;
	}
}
