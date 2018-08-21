package net.shadowmage.ancientwarfare.core.compat;

import net.minecraftforge.fml.common.Loader;

import java.util.HashMap;
import java.util.Map;

public class CompatLoader {
	private static Map<String,ICompat> compats = new HashMap<>();

	public static void registerCompat(ICompat compat) {
		compats.put(compat.getModId(),compat);
	}

	public static boolean isCompatRegistered(String modId){
		return compats.containsKey(modId);
	}

	public static ICompat getCompat(String modId){
		return compats.get(modId);
	}

	public static void init() {
		for (ICompat compat : compats.values()) {
			if (Loader.isModLoaded(compat.getModId())) {
				compat.init();
			}
		}
	}
}

