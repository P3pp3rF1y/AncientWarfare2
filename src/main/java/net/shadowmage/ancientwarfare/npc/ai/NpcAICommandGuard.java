package net.shadowmage.ancientwarfare.npc.ai;

import net.minecraft.entity.Entity;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;
import net.shadowmage.ancientwarfare.npc.npc_command.NpcCommand.Command;
import net.shadowmage.ancientwarfare.npc.npc_command.NpcCommand.CommandType;

public class NpcAICommandGuard extends NpcAI
{

double followDistance = 4.d*4.d;//4^2
Entity target;
Command command;
int startRecheckDelay = 0;
double moveSpeed = 1.d;
int moveRetryDelay = 0;

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
    Entity e = npc.worldObj.getEntityByID(cmd.x);
    if(e!=null)
      {
      target = e;
      startRecheckDelay = 10;
      command = cmd;
      return true;
      }
    npc.setCurrentCommand(null);
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
  
  }

@Override
public void updateTask()
  {
  moveRetryDelay--;
  if(moveRetryDelay<=0)
    {
    AWLog.logDebug("updating command guard ai..");
    double dist = npc.getDistanceSqToEntity(target);
    if(dist>followDistance)
      {
      npc.getNavigator().tryMoveToEntityLiving(target, moveSpeed);
      }    
    moveRetryDelay=10;//base .5 second retry delay
    if(dist>256){moveRetryDelay+=10;}//add .5 seconds if distance>16
    if(dist>1024){moveRetryDelay+=20;}//add another 1 second if distance>32
    }
  }

@Override
public void resetTask()
  {
  target = null;
  command = null;
  npc.getNavigator().clearPathEntity();
  }

}
