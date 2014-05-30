package net.shadowmage.ancientwarfare.npc.ai;

import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;

public class NpcAIBard extends NpcAI
{

int playDelay = 100;

public NpcAIBard(NpcBase npc)
  {
  super(npc);
  }

@Override
public boolean shouldExecute()
  {
  playDelay--;
  if(playDelay<=0)
    {
    playDelay = 20*60*4;
    return true;
    }
  return false;
  }

@Override
public boolean continueExecuting()
  {
  return true;
  }

@Override
public void startExecuting()
  {
  AWLog.logDebug("PLAYING SOUND AT BARD...");
  npc.worldObj.playSoundAtEntity(npc, "ancientwarfare:bard.tune.tune1", 1.f, 1.f);
  }

@Override
public void updateTask()
  {
  AWLog.logDebug("ticks existed: "+npc.ticksExisted);
  }

}
