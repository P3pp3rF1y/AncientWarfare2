package net.shadowmage.ancientwarfare.npc.ai;

import net.shadowmage.ancientwarfare.npc.entity.NpcBase;

/**
 * TODO the entire thing....should I really rewrite the vanilla target-selector?
 * @author Shadowmage
 */
public class NpcAIFindTargetNearby extends NpcAI
{

public NpcAIFindTargetNearby(NpcBase npc)
  {
  super(npc); 
  
  }

@Override
public boolean shouldExecute()
  {
  return npc.getAttackTarget()==null && npc.ticksExisted%10==0;
  }

@Override
public boolean continueExecuting()
  {
  return false;
  }

@Override
public void startExecuting()
  {  
  super.startExecuting();
  }

}
