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

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import shadowmage.ancient_framework.AWFramework;
import shadowmage.ancient_framework.common.config.AWLog;
import shadowmage.ancient_framework.common.container.ContainerBase;
import shadowmage.ancient_framework.common.container.ContainerTeamControl;
import shadowmage.ancient_framework.common.gamedata.AWGameData;
import shadowmage.ancient_framework.common.network.Packet05Team;
import cpw.mods.fml.common.IPlayerTracker;

public class TeamTracker implements IPlayerTracker
{

private TeamTracker(){}
private static TeamTracker instance = new TeamTracker();
public static TeamTracker instance(){return instance;}

TeamData clientTeamData;

@Override
public void onPlayerLogin(EntityPlayer player)
  {
  if(player.worldObj.isRemote){return;}
  TeamData data = AWGameData.get(player.worldObj, "AWTeamData", TeamData.class);
  if(data==null){return;}
  data.handlePlayerLogin(player.getEntityName());  
  this.sendTeamData(data);
  }

public boolean createNewTeam(World world, String teamName, String leaderName, int teamColor)
  {
  if(world.isRemote)
    {
    AWLog.logError("attempt to create new team on client world");
    }
  TeamData data = AWGameData.get(world, "AWTeamData", TeamData.class);
  if(data.createNewTeam(teamName, leaderName, teamColor))
    {
    this.sendTeamData(data);
    return true;
    }
  return false;
  }

public boolean isHostileTowards(World world, String offenseTeam, String defenseTeam)
  {
  if(world.isRemote){return clientTeamData!=null ? clientTeamData.isHostileTowards(offenseTeam, defenseTeam) : false;}
  TeamData data = AWGameData.get(world, "AWTeamData", TeamData.class);
  if(data!=null)
    {
    return data.isHostileTowards(offenseTeam, defenseTeam);
    }
  return false;
  }

public TeamEntry getTeamFor(World world, String playerName)
  {
  if(world.isRemote){return clientTeamData!=null ? clientTeamData.getTeamFor(playerName) : null;}
  TeamData data = AWGameData.get(world, "AWTeamData", TeamData.class);
  if(data!=null)
    {
    return data.getTeamFor(playerName);
    }
  return null;
  }

@Override
public void onPlayerLogout(EntityPlayer player){}

@Override
public void onPlayerChangedDimension(EntityPlayer player){}

@Override
public void onPlayerRespawn(EntityPlayer player){}

public TeamData getTeamData(World world)
  {
  if(world.isRemote)
    {
    return clientTeamData;
    }
  return AWGameData.get(world, "AWTeamData", TeamData.class);
  }

public void handlePacketData(NBTTagCompound tag)
  {
  if(tag.hasKey("teamData"))
    {
    TeamData data = new TeamData();
    data.readFromNBT(tag.getCompoundTag("teamData"));
    this.clientTeamData = data;
    EntityPlayer player = AWFramework.proxy.getClientPlayer();
    if(player!=null && player.openContainer instanceof ContainerTeamControl)
      {
      ((ContainerBase)player.openContainer).refreshGui();
      }
    }
  }

public void sendTeamData(TeamData data)
  {
  Packet05Team pkt = new Packet05Team();
  NBTTagCompound tag = new NBTTagCompound();  
  data.writeToNBT(tag);
  pkt.packetData.setTag("teamData", tag);
  pkt.sendPacketToAllPlayers();  
  }

}
