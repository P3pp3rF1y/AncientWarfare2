package net.shadowmage.ancientwarfare.core.research;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.WorldSavedData;
import net.minecraftforge.common.util.Constants;

public class ResearchData extends WorldSavedData
{

private HashMap<String, ResearchEntry> playerResearchEntries = new HashMap<String, ResearchEntry>();

public static final String name = "AWResearchData";

public ResearchData()
  {
  super(name);
  }

public ResearchData(String par1Str)
  {
  super(name);
  }

@Override
public void readFromNBT(NBTTagCompound tag)
  {
  playerResearchEntries.clear();
  
  NBTTagList entryList = tag.getTagList("entryList", Constants.NBT.TAG_COMPOUND);
  
  ResearchEntry entry;
  NBTTagCompound entryTag;
  String name;
  for(int i = 0; i < entryList.tagCount(); i++)
    {
    entry = new ResearchEntry();
    entryTag = entryList.getCompoundTagAt(i);
    name = entryTag.getString("playerName");
    entry.readFromNBT(entryTag);
    playerResearchEntries.put(name, entry);
    }
  }

@Override
public void writeToNBT(NBTTagCompound tag)
  {
  NBTTagList entryList = new NBTTagList();
  ResearchEntry entry;
  
  NBTTagCompound entryTag;
  for(String name : this.playerResearchEntries.keySet())
    {    
    entry = this.playerResearchEntries.get(name);
    entryTag = new NBTTagCompound();
    entryTag.setString("playerName", name);
    entry.writeToNBT(entryTag);
    entryList.appendTag(entryTag);
    }  
  tag.setTag("entryList", entryList);  
  }

/**
 * returns true if the player does not know the input research<br>
 * and the player already knows all dependencies of the input research
 * @param playerName
 * @param research
 * @return
 */
public boolean canPlayerLearn(String playerName, int research)
  {
  if(!playerResearchEntries.containsKey(playerName))
    {
    return false;
    }
  return playerResearchEntries.get(playerName).canEnqueu(research);
  }

public Set<Integer> getResearchFor(String playerName)
  {
  if(playerResearchEntries.containsKey(playerName))
    {
    return playerResearchEntries.get(playerName).completedResearch;
    }
  return Collections.emptySet();
  }

public void addResearchTo(String playerName, int research)
  {
  if(!playerResearchEntries.containsKey(playerName))
    {
    playerResearchEntries.put(playerName, new ResearchEntry());
    }
  this.playerResearchEntries.get(playerName).addResearch(research);
  this.markDirty();
  }

public boolean hasPlayerCompletedResearch(String playerName, int research)
  {
  if(playerResearchEntries.containsKey(playerName))
    {
    return playerResearchEntries.get(playerName).knowsResearch(research);
    }
  return false;
  }

public int getInProgressResearch(String playerName)  
  {
  if(playerResearchEntries.containsKey(playerName))
    {
    return playerResearchEntries.get(playerName).getInProgressResearch();
    }
  return -1;
  }

public int getResearchProgress(String playerName)
  {
  if(playerResearchEntries.containsKey(playerName))
    {
    return playerResearchEntries.get(playerName).getResearchProgress();
    }
  return 0;
  }

public void setCurrentResearch(String playerName, int goal)
  {
  if(playerResearchEntries.containsKey(playerName))
    {
    playerResearchEntries.get(playerName).setCurrentResearch(goal);
    }
  }

public void setCurrentResearchProgress(String playerName, int progress)
  {
  if(playerResearchEntries.containsKey(playerName))
    {
    playerResearchEntries.get(playerName).setResearchProgress(progress);
    }
  }

public void addQueuedResearch(String playerName, int goal)
  {
  if(playerResearchEntries.containsKey(playerName))
    {
    playerResearchEntries.get(playerName).addQueuedResearch(goal);
    }
  }

public List<Integer> getQueuedResearch(String playerName)
  {
  if(playerResearchEntries.containsKey(playerName))
    {
    return playerResearchEntries.get(playerName).queuedResearch;
    }
  return Collections.emptyList();
  }


private static final class ResearchEntry
{
private int currentResearch = -1;
private int currentProgress;
private Set<Integer> completedResearch = new HashSet<Integer>();
private List<Integer> queuedResearch = new ArrayList<Integer>();

private boolean knowsResearch(int num)
  {
  return completedResearch.contains(num);
  }

private boolean canEnqueu(int num)
  {
  if(completedResearch.contains(num) || queuedResearch.contains(num))
    {
    return false;
    }
  Set<Integer> deps = ResearchGoal.resolveDependeciesFor(ResearchGoal.getGoal(num));
  Set<Integer> combinedKnowledge = new HashSet<Integer>();
  combinedKnowledge.addAll(completedResearch);
  combinedKnowledge.addAll(queuedResearch);
  return combinedKnowledge.containsAll(deps);
  }

private void setCurrentResearch(int research)
  {
  this.currentResearch = research;
  }

/**
 * SERVER-ONLY -- values must be synched through container
 */
private void setResearchProgress(int progress)
  {
  this.currentProgress = progress;
  }

private int getResearchProgress()
  {
  return currentProgress;
  }

private int getInProgressResearch()
  {
  return this.currentResearch;
  }

private void addResearch(int num)
  {
  this.completedResearch.add(num);
  }

private void addQueuedResearch(int num)
  {
  this.queuedResearch.add(num);
  }

private List<Integer> getResearchQueue()
  {
  return queuedResearch;
  }

private void writeToNBT(NBTTagCompound tag)
  {
  tag.setInteger("currentResearch", currentResearch);
  tag.setInteger("currentProgress", currentProgress);
  int[] completedGoals = new int[completedResearch.size()];
  int index = 0;
  for(Integer i : completedResearch)
    {
    completedGoals[index] = i;
    index++;
    }
  tag.setIntArray("completedResearch", completedGoals);
  int[] queuedGoals = new int[queuedResearch.size()];
  index = 0;
  for(Integer i : queuedResearch)
    {
    queuedGoals[index] = i;
    index++;
    }
  tag.setIntArray("queuedResearch", queuedGoals);
  }

private void readFromNBT(NBTTagCompound tag)
  {
  currentResearch = tag.getInteger("currentResearch");
  currentProgress = tag.getInteger("currentProgress");
  int[] in = tag.getIntArray("completedResearch");
  for(int k : in)
    {
    completedResearch.add(k);
    }
  in = tag.getIntArray("queuedResearch");
  for(int k : in)
    {
    queuedResearch.add(k);
    }
  }
}

}
