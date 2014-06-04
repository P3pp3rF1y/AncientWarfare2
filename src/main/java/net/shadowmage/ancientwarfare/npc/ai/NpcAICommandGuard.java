package net.shadowmage.ancientwarfare.npc.ai;

import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityHorse;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;
import net.shadowmage.ancientwarfare.npc.npc_command.NpcCommand.Command;
import net.shadowmage.ancientwarfare.npc.npc_command.NpcCommand.CommandType;

public class NpcAICommandGuard extends NpcAI
{

double followDistance = 4.d*4.d;
Entity target;
Command command;
int startRecheckDelay = 0;
int mountRecheckDelay = 0;

public NpcAICommandGuard(NpcBase npc)
  {
  super(npc);
  }

@Override
public boolean shouldExecute()
  {
  if(startRecheckDelay>0)
    {
    startRecheckDelay--;
    return false;
    }
  if(npc.getAttackTarget()!=null)
    {
    return false;
    }
  Command cmd = npc.getCurrentCommand();
  if(cmd!=null && cmd.type==CommandType.GUARD)
    {
    Entity e = cmd.getEntityTarget(npc.worldObj);
    if(e!=null)
      {
      target = e;
      startRecheckDelay = 10;
      command = cmd;
      return true;
      }
    npc.handlePlayerCommand(null);
    command = null;
    }
  return false;
  }

@Override
public boolean continueExecuting()
  {
  if(npc.getCurrentCommand()!=command)
    {
    command = null;
    target = null;
    return false;
    }
  return command!=null && target!=null && !target.isDead && npc.getAttackTarget()==null;
  }

@Override
public void startExecuting()
  {
  npc.addAITask(TASK_GUARD);    
  }

@Override
public void updateTask()
  {
  double dist = npc.getDistanceSqToEntity(target);
  if(dist>followDistance)
    {
    npc.addAITask(TASK_MOVE);
    moveToEntity(target, dist);
    }
  else
    {
    npc.removeAITask(TASK_MOVE);
    mountRecheckDelay--;    
    if(mountRecheckDelay<=0 && npc.ridingEntity==null && target instanceof EntityHorse)
      {
      mountRecheckDelay=200;//TODO set from config?
      EntityHorse horse = (EntityHorse)target;
      if(horse.riddenByEntity==null && horse.isTame() && horse.isAdultHorse() && horse.isHorseSaddled())
        {
        npc.mountEntity(horse);
        command=null;
        target = null;
        npc.handlePlayerCommand(null);
        }
      }
    }
  }

@Override
public void resetTask()
  {
  target = null;
  command = null;
  npc.getNavigator().clearPathEntity();
  npc.removeAITask(TASK_GUARD);  
  }

}
