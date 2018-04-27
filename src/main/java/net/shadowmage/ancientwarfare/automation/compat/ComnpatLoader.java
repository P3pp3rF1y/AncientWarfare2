package net.shadowmage.ancientwarfare.automation.compat;

import net.minecraftforge.fml.common.Loader;
import net.shadowmage.ancientwarfare.automation.compat.agricraft.AgricraftCompat;

import java.util.HashSet;
import java.util.Set;

public class ComnpatLoader {
	private static Set<ICompat> compats = new HashSet<>();

	static {
		compats.add(new AgricraftCompat());
	}

	public static void init() {
		for (ICompat compat : compats) {
			if (Loader.isModLoaded(compat.getModId())) {
				compat.init();
			}
		}
	}
}
