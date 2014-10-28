package net.shadowmage.ancientwarfare.npc.ai;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.shadowmage.ancientwarfare.npc.entity.NpcBard;
import net.shadowmage.ancientwarfare.npc.entity.NpcBard.BardTuneData;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;

public class NpcAIPlayerOwnedBard extends NpcAI
{

boolean playing = false;//if currently playing a tune.
int currentDelay;//the current cooldown delay.  if not playing, this delay will be incremented before attempting to start next song
int tuneIndex = -1;//will be incremented to 0 before first song selected
int playerCheckDelay;//used to not check for players -every- tick. checks every 10 ticks
int playTime;//tracking current play time.  when this exceeds length, cooldown delay is triggered

NpcBard bard;

public NpcAIPlayerOwnedBard(NpcBase npc)
  {
  super(npc);
  this.bard = (NpcBard)npc;
  }

@Override
public boolean shouldExecute()
  {
  return npc.getIsAIEnabled() && bard.getTuneData().size()>0;
  }

@Override
public boolean continueExecuting()
  {
  return npc.getIsAIEnabled() && bard.getTuneData().size()>0;
  }

@Override
public void startExecuting(){}

@Override
public void updateTask()
  {
  BardTuneData data = bard.getTuneData();
  if(playing)
    {
    playTime++;
    if(playTime>=data.get(tuneIndex).length())
      {
      playTime=0;
      playing = false;
      int d = data.getMaxDelay()-data.getMinDelay();
      currentDelay = data.getMinDelay() + d > 0? npc.getRNG().nextInt(d) : 0;
      }
    }
  else if(currentDelay>0)
    {
    currentDelay--;
    }
  else
    {
    if(data.getPlayOnPlayerEntry())
      {
      playerCheckDelay--;
      if(playerCheckDelay<=0)
        {
        playerCheckDelay = 10;
        //TODO look for players in nearby area...20 blocks or so?
        AxisAlignedBB aabb = npc.boundingBox.copy().expand(20, 20, 20);
        @SuppressWarnings("unchecked")
        List<EntityPlayer> list = npc.worldObj.getEntitiesWithinAABB(EntityPlayer.class, aabb);
        if(list!=null && !list.isEmpty())
          {
          setNextSong();
          startSong();          
          }        
        }
      }
    else
      {
      setNextSong();
      startSong();
      }
    }
  }

private void setNextSong()
  {
  BardTuneData data = bard.getTuneData();
  tuneIndex++;
  if(tuneIndex>=data.size()){tuneIndex=0;}  
  }

private void startSong()
  {
  playing=true;
  playTime=0;
  }

}
