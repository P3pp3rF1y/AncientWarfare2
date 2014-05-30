package net.shadowmage.ancientwarfare.npc.ai;

import java.util.List;

import net.minecraft.entity.Entity;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;
import net.shadowmage.ancientwarfare.npc.entity.NpcPlayerOwned;

public class NpcAIFleeOnLowHealth extends NpcAI
{

double distanceFromEntity = 16;
Entity fleeTowardsTarget;

public NpcAIFleeOnLowHealth(NpcBase npc)
  {
  super(npc); 
  this.setMutexBits(ATTACK+MOVE);
  }

@Override
public boolean shouldExecute()
  {
  if(npc.getLastAttacker()==null || npc.getHealth() > npc.getMaxHealth()*0.25f)
    {
    return false;
    }
  
  List list = this.npc.worldObj.getEntitiesWithinAABB(NpcPlayerOwned.class, this.npc.boundingBox.expand((double)this.distanceFromEntity, 3.0D, (double)this.distanceFromEntity));
  if(list.isEmpty()){return false;}
  
  for(NpcBase npc : (List<NpcBase>)list)
    {
    if(npc==this.npc){continue;}
    if("medic".equals(npc.getNpcSubType()) && !npc.isHostileTowards(this.npc))
      {
      if(fleeTowardsTarget==null || this.npc.getDistanceSqToEntity(fleeTowardsTarget)<this.npc.getDistanceSqToEntity(npc))
        {
        fleeTowardsTarget = npc;
        }
      }
    }
  return fleeTowardsTarget!=null;
  }

/**
 * Returns whether an in-progress EntityAIBase should continue executing
 */
@Override
public boolean continueExecuting()
  {
  return !this.npc.getNavigator().noPath();
  }

@Override
public void startExecuting()
  {
  npc.getNavigator().tryMoveToEntityLiving(fleeTowardsTarget, 1.d);
  }

@Override
public void resetTask()
  {
  fleeTowardsTarget = null;
  npc.getNavigator().clearPathEntity();
  }

}
