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

import net.minecraft.nbt.NBTTagCompound;

public class TeamPlayerEntry
{

String playerName;
int rank;

/**
 * nbt-constructor
 */
public TeamPlayerEntry()
  {

  }

/**
 * @param player
 * @param rank2
 */
public TeamPlayerEntry(String player, int rank)
  {
  this.playerName = player;
  this.rank = rank;
  }

@Override
public int hashCode()
  {
  return playerName.hashCode();
  }

public String getPlayerName()
  {
  return playerName;
  }

public int getPlayerRank()
  {
  return rank;
  }

@Override
public boolean equals(Object obj)
  {
  if (this == obj)
    return true;
  if (obj == null)
    return false;
  if (getClass() != obj.getClass())
    return false;
  TeamPlayerEntry other = (TeamPlayerEntry) obj;
  if (!playerName.equals(other.playerName))
    return false;
  return true;
  }

public void readFromNBT(NBTTagCompound tag)
  {
  this.playerName = tag.getString("name");
  this.rank = tag.getInteger("rank");
  }

public void writeToNBT(NBTTagCompound tag)
  {
  tag.setString("name", this.playerName);
  tag.setInteger("rank", rank);
  }


}
