package net.shadowmage.ancientwarfare.npc.registry;

import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.registry.EntityRegistry;

import java.util.HashSet;
import java.util.Set;

public class FactionDefinition {
	private String name;
	private int color;
	private int playerDefaultStanding;
	private final Set<String> hostileTowards;
	private final Set<String> targetList;

	FactionDefinition(int playerDefaultStanding, Set<String> hostileTowards, Set<String> targetList) {
		this.playerDefaultStanding = playerDefaultStanding;
		this.hostileTowards = hostileTowards;
		this.targetList = targetList;
	}

	private FactionDefinition(String name, int color, int playerDefaultStanding, Set<String> hostileTowards, Set<String> targetList) {
		this(playerDefaultStanding, hostileTowards, targetList);
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
		return new CopyBuilder(name, color, getPlayerDefaultStanding(), new HashSet<>(hostileTowards), new HashSet<>(targetList));
	}

	public int getPlayerDefaultStanding() {
		return playerDefaultStanding;
	}

	public boolean isTarget(Entity entity) {
		//noinspection ConstantConditions
		return EntityRegistry.getEntry(entity.getClass()) != null
				&& targetList.contains(EntityRegistry.getEntry(entity.getClass()).getRegistryName().toString());
	}

	public Set<String> getTargetList() {
		return targetList;
	}

	public static class CopyBuilder {
		private final String name;
		private final int color;
		private int playerDefaultStanding;
		private Set<String> hostileTowards;
		private Set<String> targetList;

		private CopyBuilder(String name, int color, int playerDefaultStanding, Set<String> hostileTowards, Set<String> targetList) {
			this.name = name;
			this.color = color;
			this.playerDefaultStanding = playerDefaultStanding;
			this.hostileTowards = hostileTowards;
			this.targetList = targetList;
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

		CopyBuilder overrideTargetList(Set<String> targetList) {
			this.targetList = targetList;
			return this;
		}

		public FactionDefinition build() {
			return new FactionDefinition(name, color, playerDefaultStanding, hostileTowards, targetList);
		}
	}
}