package net.shadowmage.ancientwarfare.npc.ai;

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
  return npc.getIsAIEnabled();
  }

@Override
public boolean continueExecuting()
  {
  return npc.getIsAIEnabled();
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
      lastExecuted = npc.ticksExisted;
      int rng = npc.getRNG().nextInt(100);
      if(!bard.bardTune.isEmpty() && rng<bard.bardPlayChance)
        {
        String tune = bard.bardTune;
        npc.worldObj.playSoundAtEntity(npc, tune, 1.f, 1.f);
        playing = true;
        }
      }
    }
  }

}
