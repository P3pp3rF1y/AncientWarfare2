package net.shadowmage.ancientwarfare.core.research;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
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

    public void removeResearchFrom(String playerName, int research) {
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

    public Set<Integer> getResearchableGoals(String playerName) {
        if (playerResearchEntries.containsKey(playerName)) {
            ResearchEntry entry = playerResearchEntries.get(playerName);
            return ResearchGoal.getResearchableGoalsFor(entry.completedResearch, entry.queuedResearch, entry.currentResearch);
        }
        return Collections.emptySet();
    }

    public Set<Integer> getResearchFor(String playerName) {
        if (playerResearchEntries.containsKey(playerName)) {
            return playerResearchEntries.get(playerName).completedResearch;
        }
        return Collections.emptySet();
    }

    public void addResearchTo(String playerName, int research) {
        if (!playerResearchEntries.containsKey(playerName)) {
            playerResearchEntries.put(playerName, new ResearchEntry());
        }
        this.playerResearchEntries.get(playerName).addResearch(research);
        markDirty();
    }

    public boolean hasPlayerCompletedResearch(String playerName, int research) {
        return playerResearchEntries.containsKey(playerName) && playerResearchEntries.get(playerName).knowsResearch(research);
    }

    public int getInProgressResearch(String playerName) {
        if (playerResearchEntries.containsKey(playerName)) {
            return playerResearchEntries.get(playerName).getInProgressResearch();
        }
        return -1;
    }

    public int getResearchProgress(String playerName) {
        if (playerResearchEntries.containsKey(playerName)) {
            return playerResearchEntries.get(playerName).getResearchProgress();
        }
        return 0;
    }

    public void startResearch(String playerName, int goal) {
        if (playerResearchEntries.containsKey(playerName)) {
            playerResearchEntries.get(playerName).startResearch(goal);
            markDirty();
        }
    }

    public void finishResearch(String playerName, int goal) {
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

    public void addQueuedResearch(String playerName, int goal) {
        if (playerResearchEntries.containsKey(playerName)) {
            playerResearchEntries.get(playerName).addQueuedResearch(goal);
            markDirty();
        }
    }

    public void removeQueuedResearch(String playerName, int goal) {
        if (playerResearchEntries.containsKey(playerName)) {
            playerResearchEntries.get(playerName).removeQueuedResearch(goal);
            markDirty();
        }
    }

    public List<Integer> getQueuedResearch(String playerName) {
        if (playerResearchEntries.containsKey(playerName)) {
            return playerResearchEntries.get(playerName).getResearchQueue();
        }
        return Collections.emptyList();
    }

    public boolean addProgress(String playerName, int amount) {
        boolean ret = false;
        if(playerResearchEntries.containsKey(playerName)) {
            ret = playerResearchEntries.get(playerName).addProgress(amount);
            markDirty();
        }

        return  ret;
    }

    public boolean hasResearchStarted(String playerName) {
        return playerResearchEntries.containsKey(playerName) && playerResearchEntries.get(playerName).hasResearchStarted();
    }

    private static final class ResearchEntry {
        private int currentResearch = -1;
        private int currentProgress = -1;
        private Set<Integer> completedResearch = new HashSet<>();
        private List<Integer> queuedResearch = new ArrayList<>();

        private boolean knowsResearch(int num) {
            return completedResearch.contains(num);
        }

        public boolean addProgress(int amount) {
            if (currentResearch >= 0) {
                currentProgress += amount;
                if (currentProgress >= ResearchGoal.getGoal(currentResearch).getTotalResearchTime()) {
                    finishResearch(currentResearch);
                }
                return true;
            }
            return false;
        }

        public void finishResearch(int goal) {
            if (goal == currentResearch) {
                completedResearch.add(goal);
                currentProgress = -1;
                currentResearch = -1;
            }
        }

        /*
         * should only be called after a goal from the queue has sucessfully been started -- items used/etc
         */
        public void startResearch(int goal) {
            if (currentResearch >= 0 || !queuedResearch.contains(Integer.valueOf(goal))) {
                return;
            }
            queuedResearch.remove(Integer.valueOf(goal));
            currentResearch = goal;
            currentProgress = 0;
        }

        public boolean hasResearchStarted() {
            return currentProgress >= 0 && currentResearch >= 0;
        }

        private void setResearchProgress(int progress) {
            this.currentProgress = progress;
        }

        private int getResearchProgress() {
            return currentProgress;
        }

        private int getInProgressResearch() {
            return this.currentResearch;
        }

        private void addResearch(int num) {
            this.completedResearch.add(num);
            if (this.queuedResearch.contains(Integer.valueOf(num))) {
                this.queuedResearch.remove(Integer.valueOf(num));
            }
            if (this.currentResearch == num) {
                this.currentResearch = -1;
                this.currentProgress = -1;
            }
        }

        private void removeResearch(int num) {
            this.completedResearch.remove(Integer.valueOf(num));
        }

        private void clearResearch() {
            this.completedResearch.clear();
            this.currentProgress = -1;
            this.currentResearch = -1;
            this.queuedResearch.clear();
        }

        private void fillResearch() {
            this.completedResearch.clear();
            this.currentProgress = -1;
            this.currentResearch = -1;
            this.queuedResearch.clear();
            for (ResearchGoal g : ResearchGoal.getResearchGoals()) {
                completedResearch.add(g.getId());
            }
        }

        private void addQueuedResearch(int num) {
            if (!queuedResearch.contains(Integer.valueOf(num))) {
                this.queuedResearch.add(num);
            }
        }

        private List<Integer> getResearchQueue() {
            return queuedResearch;
        }

        private void writeToNBT(NBTTagCompound tag) {
            tag.setInteger("currentResearch", currentResearch);
            tag.setInteger("currentProgress", currentProgress);
            int[] completedGoals = new int[completedResearch.size()];
            int index = 0;
            for (Integer i : completedResearch) {
                completedGoals[index] = i;
                index++;
            }
            tag.setIntArray("completedResearch", completedGoals);
            int[] queuedGoals = new int[queuedResearch.size()];
            index = 0;
            for (Integer i : queuedResearch) {
                queuedGoals[index] = i;
                index++;
            }
            tag.setIntArray("queuedResearch", queuedGoals);
        }

        private void readFromNBT(NBTTagCompound tag) {
            currentResearch = tag.getInteger("currentResearch");
            currentProgress = tag.getInteger("currentProgress");
            int[] in = tag.getIntArray("completedResearch");
            for (int k : in) {
                completedResearch.add(k);
            }
            in = tag.getIntArray("queuedResearch");
            for (int k : in) {
                queuedResearch.add(k);
            }
        }

        private void removeQueuedResearch(Integer goal) {
            if (!queuedResearch.contains(goal)) {
                return;
            }

            List<Integer> goalsToValidate = new ArrayList<>();

            Iterator<Integer> it = queuedResearch.iterator();
            Integer exam;
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

            Set<Integer> totalResearch = new HashSet<>();
            totalResearch.addAll(completedResearch);
            totalResearch.addAll(queuedResearch);
            if (currentResearch >= 0) {
                totalResearch.add(currentResearch);
            }

            ResearchGoal g;
            for (Integer g1 : goalsToValidate) {
                g = ResearchGoal.getGoal(g1);
                if (g != null && g.canResearch(totalResearch)) {
                    totalResearch.add(g1);
                    queuedResearch.add(g1);
                }
            }
        }

    }

}
