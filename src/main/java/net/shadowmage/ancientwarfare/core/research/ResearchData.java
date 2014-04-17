package net.shadowmage.ancientwarfare.core.research;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
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

public void onPlayerLogin(EntityPlayer player)
  {
  if(!playerResearchEntries.containsKey(player.getCommandSenderName()))
    {
    playerResearchEntries.put(player.getCommandSenderName(), new ResearchEntry());
    this.markDirty();
    }
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

public Set<Integer> getResearchableGoals(String playerName)
  {  
  if(playerResearchEntries.containsKey(playerName))
    {
    ResearchEntry entry = playerResearchEntries.get(playerName);
    return ResearchGoal.getResearchableGoalsFor(entry.completedResearch, entry.queuedResearch, entry.currentResearch);    
    }
  return Collections.emptySet();
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

public void startResearch(String playerName, int goal)
  {
  if(playerResearchEntries.containsKey(playerName))
    {
    playerResearchEntries.get(playerName).startResearch(goal);
    this.markDirty();
    }
  }

public void finishResearch(String playerName, int goal)
  {
  if(playerResearchEntries.containsKey(playerName))
    {
    playerResearchEntries.get(playerName).finishResearch(goal);
    this.markDirty();
    }
  }

public void setCurrentResearchProgress(String playerName, int progress)
  {
  if(playerResearchEntries.containsKey(playerName))
    {
    playerResearchEntries.get(playerName).setResearchProgress(progress);
    this.markDirty();
    }
  }

public void addQueuedResearch(String playerName, int goal)
  {
  if(playerResearchEntries.containsKey(playerName))
    {
    playerResearchEntries.get(playerName).addQueuedResearch(goal);
    }
  }

public void removeQueuedResearch(String playerName, int goal)
  {
  if(playerResearchEntries.containsKey(playerName))
    {
    playerResearchEntries.get(playerName).removeQueuedResearch(goal);
    this.markDirty();
    }
  }

public List<Integer> getQueuedResearch(String playerName)
  {
  if(playerResearchEntries.containsKey(playerName))
    {
    return playerResearchEntries.get(playerName).getResearchQueue();
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

public void finishResearch(int goal)
  {  
  if(goal==currentResearch)
    {
    completedResearch.add(goal);
    currentProgress = 0;
    currentResearch = -1;
    if(!queuedResearch.isEmpty())
      {
      Integer g = queuedResearch.remove(0);
      currentResearch = g.intValue();
      }
    }
  }

public void startResearch(int goal)
  {
  if(currentResearch>=0 || !queuedResearch.contains(Integer.valueOf(goal))){return;}
  queuedResearch.remove(Integer.valueOf(goal));
  currentResearch = goal;
  currentProgress = 0;
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
  if(!queuedResearch.contains(Integer.valueOf(num)))
    {
    this.queuedResearch.add(num);    
    }
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


private void removeQueuedResearch(int goal)
  {
  Integer goalObject = Integer.valueOf(goal);
  if(!queuedResearch.contains(goalObject)){return;}
  
  List<Integer> goalsToValidate = new ArrayList<Integer>();
  
  Iterator<Integer> it = queuedResearch.iterator();
  Integer exam;
  boolean found = false;
  while(it.hasNext() && (exam = it.next())!=null)
    {
    if(found)
      {
      goalsToValidate.add(exam);
      it.remove();      
      }
    else if(!found && exam.intValue()==goalObject.intValue())
      {
      found = true;
      it.remove();
      }
    }
  
  Set<Integer> totalResearch = new HashSet<Integer>();
  totalResearch.addAll(completedResearch);
  totalResearch.addAll(queuedResearch);
  if(currentResearch>=0)
    {
    totalResearch.add(currentResearch);    
    }
  
  ResearchGoal g;
  for(Integer g1 : goalsToValidate)
    {
    g = ResearchGoal.getGoal(g1);
    if(g==null){continue;}
    else if(g.canResearch(totalResearch))
      {
      totalResearch.add(g1);
      queuedResearch.add(g1);
      }
    }
  }

}

}
