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
import java.util.HashSet;
import java.util.Set;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;

public class TeamEntry
{

public String teamName;
String leaderName;
int teamColor;//RGBA hex color, e.g. 0xff00ffff==purple
boolean autoAcceptApplications = false;
HashMap<String, TeamPlayerEntry> playerEntries = new HashMap<String, TeamPlayerEntry>();
Set<String> warringTeams = new HashSet<String>();
Set<String> alliedTeams = new HashSet<String>();

Set<String> pendingApplicants = new HashSet<String>();

/**
 * nbt-constructor...should immediately read from NBT after construction to ensure things are setup properly
 */
public TeamEntry()
  {
  
  }

public TeamEntry(String teamName, String leaderName, int teamColor)
  {
  this.teamName = teamName;
  this.leaderName = leaderName;
  this.teamColor = teamColor;
  }

public int getRankOf(String playerName)
  {
  return playerEntries.containsKey(playerName) ? playerEntries.get(playerName).rank : 0;
  }

public void addPlayer(String player, int rank)
  {
  playerEntries.put(player, new TeamPlayerEntry(player, rank));
  }

public void removePlayer(String player)
  {
  playerEntries.remove(player);
  if(player.equals(this.leaderName))
    {
    this.leaderName = TeamData.defaultLeaderName;
    }
  }

public void addWarringTeam(String teamName)
  {
  this.warringTeams.add(teamName);
  if(this.alliedTeams.contains(teamName))
    {
    this.alliedTeams.remove(teamName);
    }
  }

public void removeWarringTeam(String teamName)
  {
  this.warringTeams.remove(teamName);
  }

public boolean isHostileTowardsTeam(String teamName)
  {
  return this.warringTeams.contains(teamName);
  }

public void setLeaderName(String playerName)
  {
  if(this.playerEntries.containsKey(playerName))
    {
    this.leaderName = playerName;
    }
  }

public Collection<TeamPlayerEntry> getPlayerEntries()
  {
  return this.playerEntries.values();
  }

public int getTeamColor()
  {
  return teamColor;
  }

public void readFromNBT(NBTTagCompound tag)
  {
  teamName = tag.getString("teamName");
  leaderName = tag.getString("leaderName");
  teamColor = tag.getInteger("teamColor");
  autoAcceptApplications = tag.getBoolean("autoAccept");
  
  NBTTagList list = tag.getTagList("playerList");
  NBTTagCompound entryTag;
  TeamPlayerEntry entry;
  for(int i = 0; i < list.tagCount(); i++)
    {
    entryTag = (NBTTagCompound) list.tagAt(i);
    entry = new TeamPlayerEntry();
    entry.readFromNBT(entryTag);
    this.playerEntries.put(entry.playerName, entry);
    } 

  NBTTagString stringTag;
  list = tag.getTagList("warList");
  for(int i = 0; i < list.tagCount(); i++)
    {
    stringTag = (NBTTagString) list.tagAt(i);
    this.warringTeams.add(stringTag.data);
    }
  
  list = tag.getTagList("allyList");
  for(int i = 0; i < list.tagCount(); i++)
    {
    stringTag = (NBTTagString) list.tagAt(i);
    this.alliedTeams.add(stringTag.data);
    }
  
  list = tag.getTagList("pendingList");
  for(int i = 0; i < list.tagCount(); i++)
    {
    stringTag = (NBTTagString) list.tagAt(i);
    this.pendingApplicants.add(stringTag.data);
    }
  }

public void writeToNBT(NBTTagCompound tag)
  {
  tag.setString("teamName", teamName);
  tag.setString("leaderName", leaderName);
  tag.setInteger("teamColor", teamColor);
  tag.setBoolean("autoAccept", autoAcceptApplications);
  NBTTagList list = new NBTTagList();
  
  NBTTagCompound entryTag;
  for(TeamPlayerEntry entry : this.playerEntries.values())
    {
    entryTag = new NBTTagCompound();
    entry.writeToNBT(entryTag);
    list.appendTag(entryTag);
    }
  tag.setTag("playerList", list);
  
  list = new NBTTagList();
  for(String teamName : warringTeams)
    {
    list.appendTag(new NBTTagString("name", teamName));
    }
  tag.setTag("warList", list);
  
  list = new NBTTagList();
  for(String teamName : alliedTeams)
    {
    list.appendTag(new NBTTagString("name", teamName));
    }
  tag.setTag("allyList", list);
  
  list = new NBTTagList();
  for(String teamName : pendingApplicants)
    {
    list.appendTag(new NBTTagString("name", teamName));
    }
  tag.setTag("pendingList", list);
  }

}
