package net.shadowmage.ancientwarfare.npc.ai;

import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.util.AxisAlignedBB;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcFaction;

public class NpcAIAlertFaction extends NpcAI
{

int alertDelay = 0;

public NpcAIAlertFaction(NpcBase npc)
  {
  super(npc);
  if(!(npc instanceof NpcFaction)){throw new IllegalArgumentException("Npc must be faction-based npc for faction alert ai");}
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
  }

@Override
public void updateTask()
  {
  alertDelay--;
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
  List<NpcFaction> ownedNpcs = npc.worldObj.getEntitiesWithinAABB(NpcFaction.class, bb);
  ownedNpcs.remove(npc);
  EntityLivingBase target = npc.getAttackTarget()!=null ? npc.getAttackTarget() : npc.getLastAttacker();
  for(NpcFaction npc : ownedNpcs)
    {
    if(npc.getFaction().equals(((NpcFaction)this.npc).getFaction()))
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
    double fr = npc.getEntityAttribute(SharedMonsterAttributes.followRange).getAttributeValue();
    if(npc.getAttackTarget()==null && npc.getDistanceSqToEntity(target) < fr*fr)
      {
      npc.setAttackTarget(target);
      }   
    }
  }

}
