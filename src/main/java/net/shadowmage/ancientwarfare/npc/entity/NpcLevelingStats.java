package net.shadowmage.ancientwarfare.npc.entity;

import java.util.HashMap;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.npc.config.AWNPCStatics;

public class NpcLevelingStats
{

private HashMap<String, ExperienceEntry> experienceMap = new HashMap<String, ExperienceEntry>();
private int xp;//'generic' xp, always incremented for all xp-types
private int level;
private NpcBase npc;

public NpcLevelingStats(NpcBase npc)
  {
  this.npc = npc;
  }

public int getExperience(String type)
  {
  if(experienceMap.containsKey(type)){return experienceMap.get(type).xp;}
  return 0;
  }

public int getLevel(String type)
  {
  if(experienceMap.containsKey(type)){return experienceMap.get(type).level;}
  return 0;
  }

public int getBaseExperience()
  {
  return xp;
  }

public void addExperience(String type, int xp)
  {
  if(!experienceMap.containsKey(type)){experienceMap.put(type, new ExperienceEntry());}
  ExperienceEntry entry = experienceMap.get(type);
  entry.xp+=xp;
  while(entry.level < AWNPCStatics.maxNpcLevel && entry.xp>=getXPToLevel(entry.level+1))
    {
    entry.xp -= getXPToLevel(entry.level+1);
    entry.level++;
    }
 
  xp+=xp;
  while(level < AWNPCStatics.maxNpcLevel && xp>=getXPToLevel(level+1))
    {
    xp -= getXPToLevel(level+1);
    level++;
    onBaseLevelGained(level);
    }
  
  }

private void onBaseLevelGained(int newLevel)
  {
  if(newLevel<=10)//TODO set max-cap level from config...or just let diminishing xp gain taper it off....
    {
    int health = 20 + newLevel;
    npc.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(health);
    AWLog.logDebug("npc max health increased from level up to: "+health+" for npc: "+npc);    
    }
  }

private int getXPToLevel(int level)
  {
  return (level+3)*(level+3);
  }

public NBTTagCompound writeToNBT(NBTTagCompound tag)
  {
  tag.setInteger("xp", xp);
  tag.setInteger("level", level);
  NBTTagList entryList = new NBTTagList();
  NBTTagCompound xpTag;
  for(String key : this.experienceMap.keySet())
    {
    xpTag = new NBTTagCompound();
    xpTag.setString("type", key);
    xpTag.setInteger("xp", experienceMap.get(key).xp);
    xpTag.setInteger("level", experienceMap.get(key).level);
    entryList.appendTag(xpTag);
    }
  tag.setTag("entryList", entryList);
  return tag;
  }

public void readFromNBT(NBTTagCompound tag)
  {
  experienceMap.clear();  
  xp = tag.getInteger("xp");
  level = tag.getInteger("level");
  NBTTagList entryList = tag.getTagList("entryList", Constants.NBT.TAG_COMPOUND);
  NBTTagCompound xpTag;
  ExperienceEntry entry;
  for(int i = 0; i < entryList.tagCount(); i++)
    {
    xpTag = entryList.getCompoundTagAt(i);
    entry = new ExperienceEntry();
    entry.xp = xpTag.getInteger("xp");
    entry.level = xpTag.getInteger("level");
    experienceMap.put(xpTag.getString("type"), entry);
    }
  }

private class ExperienceEntry
{
int xp;
int level;
}

}
