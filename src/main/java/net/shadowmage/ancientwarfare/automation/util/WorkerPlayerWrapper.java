package net.shadowmage.ancientwarfare.automation.util;

import java.util.EnumSet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.scoreboard.Team;
import net.shadowmage.ancientwarfare.core.interfaces.IWorkSite.WorkType;
import net.shadowmage.ancientwarfare.core.interfaces.IWorker;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;

public class WorkerPlayerWrapper implements IWorker
{

EntityPlayer player;

public WorkerPlayerWrapper(EntityPlayer player)
  {
  this.player = player;
  }

@Override
public float getWorkEffectiveness()
  {
  return 2;
  }

@Override
public Team getTeam()
  {
  return player.getTeam();
  }

@Override
public EnumSet<WorkType> getWorkTypes()
  {
  return EnumSet.allOf(WorkType.class);
  }

@Override
public BlockPosition getPosition()
  {
  return new BlockPosition(player.posX, player.posY, player.posZ);
  }

}
