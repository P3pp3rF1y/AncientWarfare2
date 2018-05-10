package net.shadowmage.ancientwarfare.automation.tile.worksite.treefarm;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3i;

public class VecHelper {
	public static Vec3i combineIntoVector(EnumFacing... facings) {
		int x = 0;
		int y = 0;
		int z = 0;

		for (EnumFacing facing : facings) {
			x += facing.getFrontOffsetX();
			y += facing.getFrontOffsetY();
			z += facing.getFrontOffsetZ();
		}
		return new Vec3i(x, y, z);
	}
}
