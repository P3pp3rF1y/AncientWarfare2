package net.shadowmage.ancientwarfare.npc.ai;

import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;
import net.shadowmage.ancientwarfare.npc.npc_command.NpcCommand.Command;
import net.shadowmage.ancientwarfare.npc.npc_command.NpcCommand.CommandType;

public class NpcAICommandMove extends NpcAI
{

Command command;
int startRecheckDelay = 0;

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
    if(cmd.type==CommandType.MOVE || npc.getAttackTarget()==null)
      {
      command = cmd;
      return true;
      }    
    }
  return false;
  }

@Override
public boolean continueExecuting()
  {
  Command cmd = command;
  if(cmd!=npc.getCurrentCommand())
    {
    command = null;
    return false;
    }
  else if(cmd!=null && (cmd.type==CommandType.MOVE || cmd.type==CommandType.ATTACK_AREA))
    {
    if(cmd.type==CommandType.MOVE || npc.getAttackTarget()==null)
      {
      return true;
      }
    }
  return false;
  }

@Override
public void startExecuting()
  {
  }

@Override
public void updateTask()
  {  
  double dist = npc.getDistanceSq(command.x+0.5d, command.y, command.z+0.5d);
  if(dist>4.d*4.d)
    {
    npc.addAITask(TASK_MOVE);
    moveToPosition(command.x, command.y, command.z, dist);
    }
  else
    {
    npc.removeAITask(TASK_MOVE);    
    }
  }

@Override
public void resetTask()
  {
  command = null;
  npc.getNavigator().clearPathEntity();
  npc.removeAITask(TASK_MOVE);
  }


}
