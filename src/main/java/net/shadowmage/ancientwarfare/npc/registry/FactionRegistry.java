package net.shadowmage.ancientwarfare.npc.registry;

import com.google.common.collect.ImmutableSet;

import java.util.Set;

public class FactionRegistry {
	private FactionRegistry() {}

	private static Set<FactionDefinition> factions = ImmutableSet.of(
			new FactionDefinition("bandit", Integer.parseInt("e01313", 16)),
			new FactionDefinition("pirate", Integer.parseInt("43c0c0", 16)),
			new FactionDefinition("desert", Integer.parseInt("d7d788", 16)),
			new FactionDefinition("native", Integer.parseInt("4fc458", 16)),
			new FactionDefinition("viking", Integer.parseInt("3d3d3d", 16)),
			new FactionDefinition("custom_1", Integer.parseInt("451d58", 16)),
			new FactionDefinition("custom_2", Integer.parseInt("1d4658", 16)),
			new FactionDefinition("custom_3", Integer.parseInt("58351d", 16))
	);

	public static Set<FactionDefinition> getFactions() {
		return factions;
	}
}
