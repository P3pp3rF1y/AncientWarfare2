package net.shadowmage.ancientwarfare.npc.ai;

import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;
import net.shadowmage.ancientwarfare.npc.npc_command.NpcCommand.Command;
import net.shadowmage.ancientwarfare.npc.npc_command.NpcCommand.CommandType;

public class NpcAICommandMove extends NpcAI
{

Command command;
int startRecheckDelay = 0;
double moveSpeed = 1.d;
int moveRetryDelay = 0;

public NpcAICommandMove(NpcBase npc)
  {
  super(npc);
  this.setMutexBits(ATTACK+MOVE);
  }

@Override
public boolean shouldExecute()
  {
  if(startRecheckDelay>0)
    {
    startRecheckDelay--;
    return false;
    }
  Command cmd = npc.getCurrentCommand();
  if(cmd!=null && (cmd.type==CommandType.MOVE || cmd.type==CommandType.ATTACK_AREA))
    {
    if(cmd.type==CommandType.MOVE && npc.getAttackTarget()!=null)
      {
      return false;
      }
    command = cmd;
    return true;
    }
  return false;
  }

@Override
public boolean continueExecuting()
  {
  if(command==null || npc.getCurrentCommand()!=command)
    {
    command = null;
    return false;
    }
  return (command.type==CommandType.MOVE && npc.getAttackTarget()==null) || (command.type==CommandType.ATTACK_AREA);
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
    AWLog.logDebug("updating command move ai..");
    double dist = npc.getDistanceSq(command.x+0.5d, command.y, command.z+0.5d);
    if(dist>16.d)
      {
      npc.getNavigator().tryMoveToXYZ(command.x+0.5d, command.y, command.z+0.5d, moveSpeed);
      moveRetryDelay=10;//base .5 second retry delay
      if(dist>256){moveRetryDelay+=10;}//add .5 seconds if distance>16
      if(dist>1024){moveRetryDelay+=20;}//add another 1 second if distance>32
      }
    else
      {
      if(npc.getCurrentCommand()==command)
        {
        npc.setCurrentCommand(null);        
        }
      }
    }
  }

@Override
public void resetTask()
  {
  command = null;
  npc.getNavigator().clearPathEntity();
  }


}
