package net.shadowmage.ancientwarfare.core.research;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;

public class ResearchData extends WorldSavedData
{

private HashMap<String, Set<Integer>> playerResearchData = new HashMap<String, Set<Integer>>();

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
  NBTTagList researchList = tag.getTagList("researchList", Constants.NBT.TAG_COMPOUND);
  NBTTagCompound playerEntry;
  String name;
  Set<Integer> research;
  int[] researchData;
  for(int i = 0; i < researchList.tagCount(); i++)
    {
    playerEntry = researchList.getCompoundTagAt(i);
    name = playerEntry.getString("playerName");
    researchData = playerEntry.getIntArray("researchSet");
    research = new HashSet<>();
    for(int k = 0; k < researchData.length; k++)
      {
      research.add(researchData[k]);
      }
    playerResearchData.put(name, research);
    this.markDirty();
    }
  }

@Override
public void writeToNBT(NBTTagCompound tag)
  {
  Set<Integer> research;
  NBTTagList researchList = new NBTTagList();
  NBTTagCompound playerEntry;
  NBTTagIntArray playerResearch;
  int[] researchData;
  int index;
  for(String name : playerResearchData.keySet())
    {
    research = playerResearchData.get(name);
    researchData = new int[research.size()];
    index = 0;
    for(Integer researchGoalNumber : research)
      {
      researchData[index]=researchGoalNumber;
      index++;
      }    
    playerEntry = new NBTTagCompound();
    playerEntry.setString("playerName", name);
    playerResearch = new NBTTagIntArray(researchData);    
    playerEntry.setTag("researchSet", playerResearch);
    researchList.appendTag(playerEntry);
    }
  tag.setTag("researchList", researchList);
  }

/*
 * returns true if the player does not know the input research<br>
 * and the player already knows all dependencies of the input research
 * @param playerName
 * @param research
 * @return
 */
public boolean canPlayerLearn(String playerName, int research)
  {
  if(!playerResearchData.containsKey(playerName))
    {
    playerResearchData.put(playerName, new HashSet<>());
    this.markDirty();
    }
  return playerResearchData.get(playerName).containsAll(ResearchGoal.resolveDependeciesFor(ResearchGoal.getGoal(research)));
  }

public Set<Integer> getResearchFor(String playerName)
  {
  if(playerResearchData.containsKey(playerName))
    {
    return playerResearchData.get(playerName);
    }
  else
    {
    Set<Integer> research = new HashSet<>();
    playerResearchData.put(playerName, research);
    this.markDirty();
    return research;
    }
  }

public void addResearchTo(String playerName, int research)
  {
  if(!playerResearchData.containsKey(playerName))
    {
    playerResearchData.put(playerName, new HashSet<>());
    }
  playerResearchData.get(playerName).add(research);
  this.markDirty();
  }

public boolean hasPlayerCompletedResearch(String playerName, int research)
  {
  if(playerResearchData.containsKey(playerName))
    {
    return playerResearchData.get(playerName).contains(research);
    }
  return false;
  }

}
