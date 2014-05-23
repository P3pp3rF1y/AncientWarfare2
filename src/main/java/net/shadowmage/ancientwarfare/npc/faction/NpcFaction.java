package net.shadowmage.ancientwarfare.npc.faction;

import net.minecraft.nbt.NBTTagCompound;

/**
 * NPC faction entry to be stored in/by FactionData
 * 
 * @author John
 */
public class NpcFaction
{

String factionName;
//map of player names to faction standing
//map of team names to faction standing ??

public NpcFaction(String name)
  {
  this.factionName = name;
  }

public float getStandingForTeam(String teamName)
  {
  return 0.f;
  }

public float getStandingForPlayer(String playerName)
  {
  return 0.f;
  }

public NBTTagCompound writeToNBT(NBTTagCompound tag)
  {
  return tag;
  }

public void readFromNBT(NBTTagCompound tag)
  {
  
  }

}
