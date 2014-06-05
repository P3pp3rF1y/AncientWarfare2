package net.shadowmage.ancientwarfare.npc.ai;

import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.npc.entity.NpcBard;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;

public class NpcAIBard extends NpcAI
{

int lastExecuted = -1;
boolean playing;
int playLength = 0;

NpcBard bard;
public NpcAIBard(NpcBase npc)
  {
  super(npc);
  this.bard = (NpcBard)npc;
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
  if(playing)
    {
    playLength++;
    if(playLength>bard.bardPlayLength)
      {
      playing = false;
      playLength = 0;
      }
    }
  else
    {
    if(lastExecuted<0 || npc.ticksExisted-lastExecuted>bard.bardPlayRecheckDelay)
      {
      AWLog.logDebug("checking if bard should play...");
      lastExecuted = npc.ticksExisted;
      int rng = npc.getRNG().nextInt(100);
      if(rng<bard.bardPlayChance)
        {
        int tune = bard.bardTuneNumber == -1? npc.getRNG().nextInt(10) : bard.bardTuneNumber;
        AWLog.logDebug("playing tune: "+tune);
        playing = true;
        }
      }
    }
  }

}
