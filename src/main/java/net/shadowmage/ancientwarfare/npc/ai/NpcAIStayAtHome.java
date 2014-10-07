package net.shadowmage.ancientwarfare.npc.ai;

import net.minecraft.util.ChunkCoordinates;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;

public class NpcAIStayAtHome extends NpcAI
{

BlockPosition target;

public NpcAIStayAtHome(NpcBase npc)
  {
  super(npc);
  setMutexBits(ATTACK + MOVE);
  }

@Override
public boolean shouldExecute()
  {
  return npc.getAttackTarget()==null || npc.getAttackTarget().isDead && npc.hasHome();
  }

@Override
public void startExecuting()
  {
  ChunkCoordinates cc = npc.getHomePosition();
  if(cc!=null)
    {
    target = new BlockPosition(cc.posX, cc.posY, cc.posZ);
    }
  }

@Override
public boolean continueExecuting()
  {
  return target!=null && npc.getAttackTarget()==null || npc.getAttackTarget().isDead && npc.hasHome();
  }

@Override
public void updateTask()
  {
  if(target==null){return;}
  double d = npc.getDistanceSq(target);
  if(d > 9)
    {
    npc.addAITask(TASK_MOVE);
    moveToPosition(target, d);
    }
  else
    {
    npc.removeAITask(TASK_MOVE);
    }
  }

@Override
public void resetTask()
  {
  target = null;
  npc.removeAITask(TASK_MOVE);
  }

}
