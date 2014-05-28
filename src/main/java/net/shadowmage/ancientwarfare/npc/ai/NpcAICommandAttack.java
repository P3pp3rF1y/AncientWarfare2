package net.shadowmage.ancientwarfare.npc.ai;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;
import net.shadowmage.ancientwarfare.npc.entity.NpcPlayerOwned;
import net.shadowmage.ancientwarfare.npc.npc_command.NpcCommand.Command;
import net.shadowmage.ancientwarfare.npc.npc_command.NpcCommand.CommandType;

public class NpcAICommandAttack extends NpcAI
{

Command command;
EntityLivingBase target;
int startRecheckDelay = 0;

public NpcAICommandAttack(NpcBase npc)
  {
  super(npc);
  this.setMutexBits(0);
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
  if(cmd!=null && cmd.type==CommandType.ATTACK)
    {
    Entity e = npc.worldObj.getEntityByID(cmd.x);
    if(e instanceof EntityLivingBase)
      {
      if(e instanceof NpcPlayerOwned && !npc.isHostileTowards(((EntityLivingBase) e).getTeam()))
        {
        npc.setCurrentCommand(null);
        AWLog.logDebug("setting cannot attack from command due to friendly!!: "+e);
        }
      else
        {
        target = (EntityLivingBase) e;
        npc.setAttackTarget(target);
        command = npc.getCurrentCommand();
        AWLog.logDebug("setting should attack from command: "+target);
        return true;              
        }
      }
    else
      {
      AWLog.logDebug("setting cannot attack from command due to invalid target!!: "+e);
      npc.setCurrentCommand(null);
      }
    startRecheckDelay=10;
    }
  return false;
  }

@Override
public boolean continueExecuting()
  {
  if(command!=npc.getCurrentCommand())
    {
    npc.setAttackTarget(null);
    target = null;
    AWLog.logDebug("resetting task due to new command in place in npc...");
    return false;
    }
  if(npc.getAttackTarget()!=target)
    {
    npc.setCurrentCommand(null);
    target = null;   
    return false;
    }
  if(target==null || target.isDead)
    {
    npc.setAttackTarget(null);
    npc.setCurrentCommand(null);
    target = null;    
    return false;
    }
  return true;
  }

@Override
public void resetTask()
  {
  command = null;
  target = null;
  }

}
