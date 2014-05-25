package net.shadowmage.ancientwarfare.npc.entity;

import java.util.HashMap;

import net.minecraft.nbt.NBTTagCompound;

public class NpcLevelingStats
{

private HashMap<String, ExperienceEntry> experienceMap = new HashMap<String, ExperienceEntry>();
private float baseExperience;//'generic' xp, always incremented for all xp-types

public NpcLevelingStats()
  {
  }

public float getExperience(String type)
  {
  if(experienceMap.containsKey(type)){return experienceMap.get(type).xp;}
  return 0.f;
  }

public float getBaseExperience()
  {
  return baseExperience;
  }

public void addExperience(String type, float xp)
  {
  if(!experienceMap.containsKey(type)){experienceMap.put(type, new ExperienceEntry());}
  experienceMap.get(type).xp+=xp;
  baseExperience+=xp;
  }

public NBTTagCompound writeToNBT(NBTTagCompound tag)
  {
  //TODO
  return tag;
  }

public void readFromNBT(NBTTagCompound tag)
  {
  //TODO
  }

private class ExperienceEntry
{
float xp;
}

}
