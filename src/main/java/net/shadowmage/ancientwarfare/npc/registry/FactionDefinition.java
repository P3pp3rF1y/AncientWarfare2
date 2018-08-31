package net.shadowmage.ancientwarfare.npc.registry;

import java.util.HashSet;
import java.util.Set;

public class FactionDefinition {
	private String name;
	private int color;
	private int playerDefaultStanding;
	private final Set<String> hostileTowards;

	FactionDefinition(int playerDefaultStanding, Set<String> hostileTowards) {
		this.playerDefaultStanding = playerDefaultStanding;
		this.hostileTowards = hostileTowards;
	}

	private FactionDefinition(String name, int color, int playerDefaultStanding, Set<String> hostileTowards) {
		this(playerDefaultStanding, hostileTowards);
		this.name = name;
		this.color = color;
	}

	public String getName() {
		return name;
	}

	public int getColor() {
		return color;
	}

	public boolean isHostileTowards(String otherFactionName) {
		return hostileTowards.contains(otherFactionName);
	}

	public CopyBuilder copy(String name, int color) {
		return new CopyBuilder(name, color, getPlayerDefaultStanding(), new HashSet<>(hostileTowards));
	}

	public int getPlayerDefaultStanding() {
		return playerDefaultStanding;
	}

	public static class CopyBuilder {
		private final String name;
		private final int color;
		private int playerDefaultStanding;
		private Set<String> hostileTowards;

		private CopyBuilder(String name, int color, int playerDefaultStanding, Set<String> hostileTowards) {
			this.name = name;
			this.color = color;
			this.playerDefaultStanding = playerDefaultStanding;
			this.hostileTowards = hostileTowards;
		}

		void setPlayerDefaultStanding(int playerDefaultStanding) {
			this.playerDefaultStanding = playerDefaultStanding;
		}

		CopyBuilder addHostileTowards(String faction) {
			hostileTowards.add(faction);
			return this;
		}

		CopyBuilder removeHostileTowards(String faction) {
			hostileTowards.remove(faction);
			return this;
		}

		public FactionDefinition build() {
			return new FactionDefinition(name, color, playerDefaultStanding, hostileTowards);
		}
	}
}