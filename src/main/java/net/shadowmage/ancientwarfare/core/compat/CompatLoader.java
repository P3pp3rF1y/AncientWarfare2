package net.shadowmage.ancientwarfare.core.compat;

import net.minecraftforge.fml.common.Loader;

import java.util.HashSet;
import java.util.Set;

public class CompatLoader {
	private static Set<ICompat> compats = new HashSet<>();

	public static void registerCompat(ICompat compat) {
		compats.add(compat);
	}

	public static void init() {
		for (ICompat compat : compats) {
			if (Loader.isModLoaded(compat.getModId())) {
				compat.init();
			}
		}
	}
}

