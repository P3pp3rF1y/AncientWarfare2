package net.shadowmage.ancientwarfare.npc.ai;

import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;

public class NpcAIBard extends NpcAI
{

int lastExecuted = -1;
int playLength = 200;
int ticksToPlay = 0;

public NpcAIBard(NpcBase npc)
  {
  super(npc);
  }

@Override
public boolean shouldExecute()
  {
  return lastExecuted==-1 || npc.ticksExisted-lastExecuted>200;
  }

@Override
public boolean continueExecuting()
  {
  return ticksToPlay>0;
  }

@Override
public void startExecuting()
  {
  if(npc.getRNG().nextInt(10)<2)
    {
    ticksToPlay=playLength;
    int tune = npc.getRNG().nextInt(10);
    AWLog.logDebug("PLAYING SOUND AT BARD...: "+(tune+1));
    npc.worldObj.playSoundAtEntity(npc, "ancientwarfare:bard.tune.tune"+String.valueOf(tune+1), 1.f, 1.f);
    }
  }

@Override
public void updateTask()
  {
  ticksToPlay--;
  if(npc.ticksExisted%10==0){npc.swingItem();}
  if(ticksToPlay<=0)
    {
    lastExecuted = npc.ticksExisted;
    }
  }

}
