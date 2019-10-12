package net.shadowmage.ancientwarfare.npc.registry;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.shadowmage.ancientwarfare.npc.AncientWarfareNPC;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class FactionDefinition {
	private String name;
	private int color;
	private int playerDefaultStanding;
	private final Set<String> hostileTowards;
	private final Set<String> targetList;
	private Map<String, NBTTagCompound> themedBlocksTags = new HashMap<>();
	private Map<String, Integer> standingChanges = new HashMap<>();

	FactionDefinition(int playerDefaultStanding, Set<String> hostileTowards, Set<String> targetList, Map<String, Integer> standingChanges) {
		this.playerDefaultStanding = playerDefaultStanding;
		this.hostileTowards = hostileTowards;
		this.targetList = targetList;
		this.standingChanges = standingChanges;
	}

	private FactionDefinition(String name, int color, int playerDefaultStanding, Set<String> hostileTowards, Set<String> targetList, Map<String, NBTTagCompound> themedBlocksTags, Map<String, Integer> standingChanges) {
		this(playerDefaultStanding, hostileTowards, targetList, standingChanges);
		this.name = name;
		this.color = color;
		this.themedBlocksTags = themedBlocksTags;
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
		return new CopyBuilder(name, color, getPlayerDefaultStanding(), new HashSet<>(hostileTowards), new HashSet<>(targetList), new HashMap<>(themedBlocksTags), new HashMap<>(standingChanges));
	}

	public int getPlayerDefaultStanding() {
		return playerDefaultStanding;
	}

	public boolean isTarget(Entity entity) {
		//noinspection ConstantConditions
		return EntityRegistry.getEntry(entity.getClass()) != null
				&& targetList.contains(EntityRegistry.getEntry(entity.getClass()).getRegistryName().toString());
	}

	public Map<String, NBTTagCompound> getThemedBlocksTags() {
		return themedBlocksTags;
	}

	public Integer getStandingChange(String changeName) {
		if (!standingChanges.containsKey(changeName)) {
			AncientWarfareNPC.LOG.error("{} standing change doesn't have value defined, using 0", changeName);
			return 0;
		}
		return standingChanges.get(changeName);
	}

	public static class CopyBuilder {
		private final String name;
		private final int color;
		private int playerDefaultStanding;
		private Set<String> hostileTowards;
		private Set<String> targetList;
		private Map<String, NBTTagCompound> themedBlocksTags;
		private Map<String, Integer> standingChanges;

		private CopyBuilder(String name, int color, int playerDefaultStanding, Set<String> hostileTowards, Set<String> targetList, Map<String, NBTTagCompound> themedBlocksTags, Map<String, Integer> standingChanges) {
			this.name = name;
			this.color = color;
			this.playerDefaultStanding = playerDefaultStanding;
			this.hostileTowards = hostileTowards;
			this.targetList = targetList;
			this.themedBlocksTags = themedBlocksTags;
			this.standingChanges = standingChanges;
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

		void overrideThemedBlocksTags(Map<String, NBTTagCompound> themedBlocksTags) {
			this.themedBlocksTags = themedBlocksTags;
		}

		void overrideStandingChanges(Map<String, Integer> standingChanges) {
			this.standingChanges.putAll(standingChanges);
		}

		public FactionDefinition build() {
			return new FactionDefinition(name, color, playerDefaultStanding, hostileTowards, targetList, themedBlocksTags, standingChanges);
		}
	}
}