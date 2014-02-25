/**
   Copyright 2012-2013 John Cummens (aka Shadowmage, Shadowmage4513)
   This software is distributed under the terms of the GNU General Public License.
   Please see COPYING for precise license information.

   This file is part of Ancient Warfare.

   Ancient Warfare is free software: you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation, either version 3 of the License, or
   (at your option) any later version.

   Ancient Warfare is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with Ancient Warfare.  If not, see <http://www.gnu.org/licenses/>.
 */
package shadowmage.ancient_framework.common.teams;

import java.util.Collection;
import java.util.HashMap;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import shadowmage.ancient_framework.common.gamedata.GameData;

public class TeamData extends GameData
{
public static String defaultTeamName = "defaultTeam";
public static String defaultLeaderName = "defaultLeader";
private HashMap<String, TeamEntry> entriesByTeamName = new HashMap<String, TeamEntry>();
private HashMap<String, TeamEntry> entriesByPlayerName = new HashMap<String, TeamEntry>();

public TeamData()
  {
  super("AWTeamData");
  }

public TeamData(String par1Str)
  {
  super(par1Str);
  }

@Override
public void readFromNBT(NBTTagCompound tag)
  {
  entriesByPlayerName.clear();
  entriesByTeamName.clear();
  NBTTagList entryList = tag.getTagList("entryList");
  NBTTagCompound entryTag;  
  for(int i = 0; i < entryList.tagCount(); i++)
    {
    entryTag = (NBTTagCompound) entryList.tagAt(i);
    TeamEntry entry = new TeamEntry();
    entry.readFromNBT(entryTag);
    this.entriesByTeamName.put(entry.teamName, entry);
    for(TeamPlayerEntry playerEntry : entry.playerEntries.values())
      {
      this.entriesByPlayerName.put(playerEntry.playerName, entry);
      }
    }
  }

@Override
public void writeToNBT(NBTTagCompound tag)
  {
  NBTTagList entryList = new NBTTagList();
  NBTTagCompound entryTag;
  for(TeamEntry entry : entriesByTeamName.values())
    {
    entryTag = new NBTTagCompound();
    entry.writeToNBT(entryTag);
    entryList.appendTag(entryTag);
    }
  tag.setTag("entryList", entryList);
  }

public boolean isHostileTowards(String offenseTeam, String defenseTeam)
  {
  TeamEntry teamA = entriesByTeamName.get(offenseTeam);
  if(teamA!=null){return teamA.isHostileTowardsTeam(defenseTeam);}
  return false;
  }

public boolean createNewTeam(String teamName, String playerName, int teamColor)
  {
  if(!"".equals(teamName) && !this.entriesByTeamName.containsKey(teamName))
    {    
    TeamEntry originalEntry = entriesByPlayerName.get(playerName);
    originalEntry.removePlayer(playerName);
    
    TeamEntry newTeam = new TeamEntry(teamName, playerName, teamColor);
    newTeam.addPlayer(playerName, 10);
    entriesByTeamName.put(teamName, newTeam);
    entriesByPlayerName.put(playerName, newTeam);    
    return true;
    }
  return false;
  }

public TeamEntry getTeamFor(String playerName)
  {
  return this.entriesByPlayerName.get(playerName);
  }

public void handlePlayerLogin(String playerName)  
  {
  if(!this.entriesByPlayerName.containsKey(playerName))
    {
    if(!this.entriesByTeamName.containsKey(defaultTeamName))
      {
      this.entriesByTeamName.put(defaultTeamName, new TeamEntry(defaultTeamName, "__default__", 0xffffffff));            
      }
    TeamEntry defaultTeam = entriesByTeamName.get(defaultTeamName);
    defaultTeam.addPlayer(playerName, 0);
    this.entriesByPlayerName.put(playerName, defaultTeam);
    }    
  }

@Override
public void handlePacketData(NBTTagCompound data)
  {
  
  }

public Collection<TeamEntry> getTeamEntries()
  {
  return entriesByTeamName.values();
  }

}
