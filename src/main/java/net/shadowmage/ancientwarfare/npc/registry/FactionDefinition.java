package net.shadowmage.ancientwarfare.npc.registry;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.shadowmage.ancientwarfare.npc.AncientWarfareNPC;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class FactionDefinition {
	private String name;
	private int color;
	private final Set<String> hostileTowards;
	private final Set<String> targetList;
	private Map<String, NBTTagCompound> themedBlocksTags = new HashMap<>();
	private StandingSettings standingSettings;

	FactionDefinition(StandingSettings standingSettings, Set<String> hostileTowards, Set<String> targetList) {
		this.standingSettings = standingSettings;
		this.hostileTowards = hostileTowards;
		this.targetList = targetList;
	}

	private FactionDefinition(String name, int color, StandingSettings standingSettings, Set<String> hostileTowards, Set<String> targetList, Map<String, NBTTagCompound> themedBlocksTags) {
		this(standingSettings, hostileTowards, targetList);
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
		return new CopyBuilder(name, color, standingSettings.copy(), new HashSet<>(hostileTowards), new HashSet<>(targetList), new HashMap<>(themedBlocksTags));
	}

	public StandingSettings getStandingSettings() {
		return standingSettings;
	}

	public boolean isTarget(Entity entity) {
		//noinspection ConstantConditions
		return EntityRegistry.getEntry(entity.getClass()) != null
				&& targetList.contains(EntityRegistry.getEntry(entity.getClass()).getRegistryName().toString());
	}

	public Map<String, NBTTagCompound> getThemedBlocksTags() {
		return themedBlocksTags;
	}

	public static class StandingSettings {
		private int playerDefaultStanding;

		private boolean standingCanChange;

		private final Map<String, Integer> standingChanges;

		public StandingSettings(int playerDefaultStanding, boolean standingCanChange, Map<String, Integer> standingChanges) {
			this.playerDefaultStanding = playerDefaultStanding;
			this.standingCanChange = standingCanChange;
			this.standingChanges = standingChanges;
		}

		public StandingSettings copy() {
			return new StandingSettings(playerDefaultStanding, standingCanChange, new HashMap<>(standingChanges));
		}

		public int getPlayerDefaultStanding() {
			return playerDefaultStanding;
		}

		public void setPlayerDefaultStanding(int playerDefaultStanding) {
			this.playerDefaultStanding = playerDefaultStanding;
		}

		public boolean canPlayerStandingChange() {
			return standingCanChange;
		}

		public void setStandingCanChange(boolean standingCanChange) {
			this.standingCanChange = standingCanChange;
		}

		public Integer getStandingChange(String changeName) {
			if (!standingChanges.containsKey(changeName)) {
				AncientWarfareNPC.LOG.error("{} standing change doesn't have value defined, using 0", changeName);
				return 0;
			}
			return standingChanges.get(changeName);
		}

		void overrideStandingChanges(Map<String, Integer> standingChanges) {
			this.standingChanges.putAll(standingChanges);
		}
	}

	public static class CopyBuilder {
		private final String name;
		private final int color;
		private final Set<String> hostileTowards;
		private Set<String> targetList;
		private Map<String, NBTTagCompound> themedBlocksTags;
		private final StandingSettings standingSettings;

		private CopyBuilder(String name, int color, StandingSettings standingSettings, Set<String> hostileTowards, Set<String> targetList, Map<String, NBTTagCompound> themedBlocksTags) {
			this.name = name;
			this.color = color;
			this.standingSettings = standingSettings;
			this.hostileTowards = hostileTowards;
			this.targetList = targetList;
			this.themedBlocksTags = themedBlocksTags;
		}

		public void setStandingSettings(Consumer<StandingSettings> setValues) {
			setValues.accept(standingSettings);
		}

		void addHostileTowards(String faction) {
			hostileTowards.add(faction);
		}

		void removeHostileTowards(String faction) {
			hostileTowards.remove(faction);
		}

		void overrideTargetList(Set<String> targetList) {
			this.targetList = targetList;
		}

		void overrideThemedBlocksTags(Map<String, NBTTagCompound> themedBlocksTags) {
			this.themedBlocksTags = themedBlocksTags;
		}

		public FactionDefinition build() {
			return new FactionDefinition(name, color, standingSettings, hostileTowards, targetList, themedBlocksTags);
		}
	}
}