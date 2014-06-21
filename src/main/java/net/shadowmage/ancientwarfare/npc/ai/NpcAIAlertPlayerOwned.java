package net.shadowmage.ancientwarfare.npc.ai;

import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;
import net.shadowmage.ancientwarfare.npc.entity.NpcPlayerOwned;
import net.shadowmage.ancientwarfare.npc.npc_command.NpcCommand.Command;
import net.shadowmage.ancientwarfare.npc.npc_command.NpcCommand.CommandType;

public class NpcAIAlertPlayerOwned extends NpcAI
{

int alertDelay = 0;

boolean wasAlerted;
Command alertCommand;

public NpcAIAlertPlayerOwned(NpcBase npc)
  {
  super(npc);
  }

@Override
public boolean shouldExecute()
  {  
  return true;
  }

@Override
public boolean continueExecuting()
  {
  return true;
  }

@Override
public void startExecuting()
  { 
  alertDelay=0;
  }

@Override
public void updateTask()
  {
  alertDelay--;
  if(alertCommand!=null && alertDelay<=0)
    {
    if(npc.getCurrentCommand()==alertCommand)
      {
      npc.handlePlayerCommand(null);
      }
    alertCommand=null;
    }
  if(alertDelay<=0 && (npc.getAttackTarget()!=null || npc.getLastAttacker()!=null))
    {
    issueAlert();
    alertDelay=200;
    }
  }

@SuppressWarnings("unchecked")
protected void issueAlert()
  {
  AxisAlignedBB bb = npc.boundingBox.expand(40.d, 20.d, 40.d);
  List<NpcPlayerOwned> ownedNpcs = npc.worldObj.getEntitiesWithinAABB(NpcPlayerOwned.class, bb);
  ownedNpcs.remove(npc);
  EntityLivingBase target = npc.getAttackTarget()!=null ? npc.getAttackTarget() : npc.getLastAttacker();
  for(NpcPlayerOwned npc : ownedNpcs)
    {
    if(npc.canBeCommandedBy(this.npc.getOwnerName()))
      {
      npc.handleAlertBroadcast(this.npc, target);
      }
    }
  }

@Override
public void resetTask()
  {
  }

public void handleAlert(NpcBase broadcaster, EntityLivingBase target)
  {
  if(alertDelay<=0)
    {
    alertDelay = 200;
    //default implementation is for non-combat...overriden in combat npc for combat-oriented implementation
    if(npc.getTownHallPosition()!=null)//try to go to town hall
      {
      this.npc.handlePlayerCommand(alertCommand=(new Command(CommandType.MOVE, npc.getTownHallPosition().x, npc.getTownHallPosition().y, npc.getTownHallPosition().z)));
      }
    else if(npc.hasHome())//try to go to home position
      {
      this.npc.handlePlayerCommand(alertCommand=(new Command(CommandType.MOVE, npc.getHomePosition().posX, npc.getHomePosition().posY, npc.getHomePosition().posZ)));
      }
    else
      {
      //no clue...
      }    
    }
  }

}
