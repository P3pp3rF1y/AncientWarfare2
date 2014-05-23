package net.shadowmage.ancientwarfare.npc.entity;

import java.util.HashMap;

import net.minecraft.nbt.NBTTagCompound;

public class NpcLevelingStats
{

private HashMap<String, ExperienceEntry> experienceMap = new HashMap<String, ExperienceEntry>();

public NpcLevelingStats()
  {
  }

public float getExperience(String type)
  {
  if(experienceMap.containsKey(type)){return experienceMap.get(type).xp;}
  return 0.f;
  }

public void addExperience(String type, float xp)
  {
  if(!experienceMap.containsKey(type)){experienceMap.put(type, new ExperienceEntry());}
  experienceMap.get(type).xp+=xp;
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
