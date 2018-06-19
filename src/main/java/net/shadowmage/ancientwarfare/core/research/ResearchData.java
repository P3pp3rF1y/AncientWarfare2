package net.shadowmage.ancientwarfare.core.research;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.StringUtils;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;
import net.shadowmage.ancientwarfare.core.datafixes.ResearchEntryIdNameFixer;
import net.shadowmage.ancientwarfare.core.registry.ResearchRegistry;
import net.shadowmage.ancientwarfare.core.util.StreamUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class ResearchData extends WorldSavedData {

	private HashMap<String, ResearchEntry> playerResearchEntries = new HashMap<>();

	public ResearchData(String par1Str) {
		super(par1Str);
	}

	public void onPlayerLogin(EntityPlayer player) {
		if (!playerResearchEntries.containsKey(player.getName())) {
			playerResearchEntries.put(player.getName(), new ResearchEntry());
			this.markDirty();
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		playerResearchEntries.clear();

		NBTTagList entryList = tag.getTagList("entryList", Constants.NBT.TAG_COMPOUND);

		ResearchEntry entry;
		NBTTagCompound entryTag;
		String name;
		for (int i = 0; i < entryList.tagCount(); i++) {
			entry = new ResearchEntry();
			entryTag = entryList.getCompoundTagAt(i);
			name = entryTag.getString("playerName");
			entry.readFromNBT(entryTag);
			playerResearchEntries.put(name, entry);
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		NBTTagList entryList = new NBTTagList();
		ResearchEntry entry;

		NBTTagCompound entryTag;
		for (String name : this.playerResearchEntries.keySet()) {
			entry = this.playerResearchEntries.get(name);
			entryTag = new NBTTagCompound();
			entryTag.setString("playerName", name);
			entry.writeToNBT(entryTag);
			entryList.appendTag(entryTag);
		}
		tag.setTag("entryList", entryList);
		return tag;
	}

	public void removeResearchFrom(String playerName, String research) {
		if (playerResearchEntries.containsKey(playerName)) {
			playerResearchEntries.get(playerName).removeResearch(research);
			markDirty();
		}
	}

	public void clearResearchFor(String playerName) {
		if (playerResearchEntries.containsKey(playerName)) {
			playerResearchEntries.get(playerName).clearResearch();
			markDirty();
		}
	}

	public void fillResearchFor(String playerName) {
		if (playerResearchEntries.containsKey(playerName)) {
			playerResearchEntries.get(playerName).fillResearch();
			markDirty();
		}
	}

	public Set<String> getResearchableGoals(String playerName) {
		if (playerResearchEntries.containsKey(playerName)) {
			ResearchEntry entry = playerResearchEntries.get(playerName);
			return getResearchableGoalsFor(entry);
		}
		return Collections.emptySet();
	}

	private static Set<String> getResearchableGoalsFor(ResearchEntry researchEntry) {
		Set<String> totalKnowledge = new HashSet<>();
		totalKnowledge.addAll(researchEntry.getCompletedResearch());
		totalKnowledge.addAll(researchEntry.getQueuedResearch());
		Optional<String> inProgress = researchEntry.getCurrentResearch();
		inProgress.ifPresent(totalKnowledge::add);
		Set<String> researchableGoals = new HashSet<>();
		for (ResearchGoal goal : ResearchRegistry.getAllResearchGoals()) {
			if (totalKnowledge.contains(goal.getName())) {
				continue;
			}

			if (goal.canResearch(totalKnowledge)) {
				researchableGoals.add(goal.getName());
			}
		}
		return researchableGoals;
	}

	public Set<String> getResearchFor(String playerName) {
		if (playerResearchEntries.containsKey(playerName)) {
			return playerResearchEntries.get(playerName).getCompletedResearch();
		}
		return Collections.emptySet();
	}

	public void addResearchTo(String playerName, String research) {
		if (!playerResearchEntries.containsKey(playerName)) {
			playerResearchEntries.put(playerName, new ResearchEntry());
		}
		this.playerResearchEntries.get(playerName).addResearch(research);
		markDirty();
	}

	public boolean hasPlayerCompletedResearch(String playerName, String research) {
		return playerResearchEntries.containsKey(playerName) && playerResearchEntries.get(playerName).knowsResearch(research);
	}

	public Optional<String> getInProgressResearch(String playerName) {
		if (playerResearchEntries.containsKey(playerName)) {
			return playerResearchEntries.get(playerName).getCurrentResearch();
		}
		return Optional.empty();
	}

	public int getResearchProgress(String playerName) {
		if (playerResearchEntries.containsKey(playerName)) {
			return playerResearchEntries.get(playerName).getResearchProgress();
		}
		return 0;
	}

	public void startResearch(String playerName, String goal) {
		if (playerResearchEntries.containsKey(playerName)) {
			playerResearchEntries.get(playerName).startResearch(goal);
			markDirty();
		}
	}

	public void finishResearch(String playerName, String goal) {
		if (playerResearchEntries.containsKey(playerName)) {
			playerResearchEntries.get(playerName).finishResearch(goal);
			markDirty();
		}
	}

	public void setCurrentResearchProgress(String playerName, int progress) {
		if (playerResearchEntries.containsKey(playerName)) {
			playerResearchEntries.get(playerName).setResearchProgress(progress);
			markDirty();
		}
	}

	public void addQueuedResearch(String playerName, String goal) {
		if (playerResearchEntries.containsKey(playerName)) {
			playerResearchEntries.get(playerName).addQueuedResearch(goal);
			markDirty();
		}
	}

	public void removeQueuedResearch(String playerName, String goal) {
		if (playerResearchEntries.containsKey(playerName)) {
			playerResearchEntries.get(playerName).removeQueuedResearch(goal);
			markDirty();
		}
	}

	public List<String> getQueuedResearch(String playerName) {
		if (playerResearchEntries.containsKey(playerName)) {
			return playerResearchEntries.get(playerName).getResearchQueue();
		}
		return Collections.emptyList();
	}

	public boolean addProgress(String playerName, int amount) {
		boolean ret = false;
		if (playerResearchEntries.containsKey(playerName)) {
			ret = playerResearchEntries.get(playerName).addProgress(amount);
			markDirty();
		}

		return ret;
	}

	public boolean hasResearchStarted(String playerName) {
		return playerResearchEntries.containsKey(playerName) && playerResearchEntries.get(playerName).hasResearchStarted();
	}

	public static final class ResearchEntry {
		private String currentResearch = null;
		private int currentProgress = -1;
		private Set<String> completedResearch = new HashSet<>();
		private List<String> queuedResearch = new ArrayList<>();

		private boolean knowsResearch(String researchName) {
			return getCompletedResearch().contains(researchName);
		}

		public Optional<String> getCurrentResearch() {
			return Optional.ofNullable(currentResearch);
		}

		private void resetCurrentResearch() {
			currentResearch = null;
		}

		public void setCurrentResearch(String currentResearch) {
			if (StringUtils.isNullOrEmpty(currentResearch)) {
				return;
			}
			this.currentResearch = currentResearch;
		}

		public boolean addProgress(int amount) {
			Optional<String> curResearch = getCurrentResearch();
			if (curResearch.isPresent()) {
				currentProgress += amount;
				if (currentProgress >= ResearchRegistry.getResearch(curResearch.get()).getTotalResearchTime()) {
					finishResearch(curResearch.get());
				}
				return true;
			}
			return false;
		}

		public void finishResearch(String researchName) {
			if (getCurrentResearch().map(r -> r.equals(researchName)).orElse(false)) {
				getCompletedResearch().add(researchName);
				currentProgress = -1;
				resetCurrentResearch();
			}
		}

		/*
		 * should only be called after a goal from the queue has sucessfully been started -- items used/etc
		 */
		public void startResearch(String goal) {
			if (getCurrentResearch().isPresent() || !queuedResearch.contains(goal)) {
				return;
			}
			queuedResearch.remove(goal);
			setCurrentResearch(goal);
			currentProgress = 0;
		}

		public boolean hasResearchStarted() {
			return currentProgress >= 0 && getCurrentResearch().isPresent();
		}

		private void setResearchProgress(int progress) {
			this.currentProgress = progress;
		}

		private int getResearchProgress() {
			return currentProgress;
		}

		private void addResearch(String researchName) {
			getCompletedResearch().add(researchName);
			if (queuedResearch.contains(researchName)) {
				queuedResearch.remove(researchName);
			}
			if (getCurrentResearch().map(r -> r.equals(researchName)).orElse(false)) {
				resetCurrentResearch();
				currentProgress = -1;
			}
		}

		private void removeResearch(String researchName) {
			this.getCompletedResearch().remove(researchName);
		}

		private void clearResearch() {
			getCompletedResearch().clear();
			currentProgress = -1;
			resetCurrentResearch();
			queuedResearch.clear();
		}

		private void fillResearch() {
			getCompletedResearch().clear();
			currentProgress = -1;
			resetCurrentResearch();
			queuedResearch.clear();
			for (ResearchGoal g : ResearchRegistry.getAllResearchGoals()) {
				getCompletedResearch().add(g.getName());
			}
		}

		private void addQueuedResearch(String researchName) {
			if (!queuedResearch.contains(researchName)) {
				queuedResearch.add(researchName);
			}
		}

		private List<String> getResearchQueue() {
			return queuedResearch;
		}

		private void writeToNBT(NBTTagCompound tag) {
			if (currentResearch != null) {
				tag.setString("currentResearch", currentResearch);
			}
			tag.setInteger("currentProgress", currentProgress);
			tag.setTag("completedResearch", getCompletedResearch().stream().map(NBTTagString::new).collect(StreamUtils.toNBTTagList));
			tag.setTag("queuedResearch", queuedResearch.stream().map(NBTTagString::new).collect(StreamUtils.toNBTTagList));
		}

		private void readFromNBT(NBTTagCompound tag) {
			NBTTagCompound fixedTag = ResearchEntryIdNameFixer.fix(tag);
			if (fixedTag.hasKey("currentResearch")) {
				currentResearch = fixedTag.getString("currentResearch");
			}
			currentProgress = fixedTag.getInteger("currentProgress");
			fixedTag.getTagList("completedResearch", Constants.NBT.TAG_STRING).forEach(t -> getCompletedResearch().add(((NBTTagString) t).getString()));
			fixedTag.getTagList("queuedResearch", Constants.NBT.TAG_STRING).forEach(t -> queuedResearch.add(((NBTTagString) t).getString()));
		}

		private void removeQueuedResearch(String goal) {
			if (!queuedResearch.contains(goal)) {
				return;
			}

			List<String> goalsToValidate = new ArrayList<>();

			Iterator<String> it = queuedResearch.iterator();
			String exam;
			boolean found = false;
			while (it.hasNext() && (exam = it.next()) != null) {
				if (found) {
					goalsToValidate.add(exam);
					it.remove();
				} else if (exam.equals(goal)) {
					found = true;
					it.remove();
				}
			}

			Set<String> totalResearch = new HashSet<>();
			totalResearch.addAll(getCompletedResearch());
			totalResearch.addAll(queuedResearch);
			Optional<String> currentResearch = getCurrentResearch();
			currentResearch.ifPresent(totalResearch::add);

			ResearchGoal g;
			for (String g1 : goalsToValidate) {
				g = ResearchRegistry.getResearch(g1);
				if (g != null && g.canResearch(totalResearch)) {
					totalResearch.add(g1);
					queuedResearch.add(g1);
				}
			}
		}

		public Set<String> getCompletedResearch() {
			return completedResearch;
		}

		public List<String> getQueuedResearch() {
			return queuedResearch;
		}
	}

}
