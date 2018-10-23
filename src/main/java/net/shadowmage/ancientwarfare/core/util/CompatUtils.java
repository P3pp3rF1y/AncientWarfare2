package net.shadowmage.ancientwarfare.core.util;

import net.minecraftforge.fml.common.Loader;

public class CompatUtils {
	private CompatUtils() {}

	public static boolean areModsLoaded(String[] mods) {
		for (String mod : mods) {
			if (!mod.isEmpty() && !Loader.isModLoaded(mod)) {
				return false;
			}
		}
		return true;
	}
}
