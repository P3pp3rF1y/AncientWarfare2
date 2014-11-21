package net.shadowmage.ancientwarfare.npc.ai.owned;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.shadowmage.ancientwarfare.core.util.SongPlayData;
import net.shadowmage.ancientwarfare.core.util.SongPlayData.SongEntry;
import net.shadowmage.ancientwarfare.npc.ai.NpcAI;
import net.shadowmage.ancientwarfare.npc.entity.NpcBard;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;

public class NpcAIPlayerOwnedBard extends NpcAI
{

private boolean playing = false;//if currently playing a tune.
private int currentDelay;//the current cooldown delay.  if not playing, this delay will be incremented before attempting to start next song
private int tuneIndex = -1;//will be incremented to 0 before first song selected
private int playerCheckDelay;//used to not check for players -every- tick. checks every 10 ticks
private int playTime;//tracking current play time.  when this exceeds length, cooldown delay is triggered
private int maxPlayTime;

private NpcBard bard;

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
  SongPlayData data = bard.getTuneData();
  if(playing)
    {
    playTime++;
    if(playTime>=maxPlayTime)
      {
      playTime = 0;
      playing = false;
      int d = data.getMaxDelay()-data.getMinDelay();
      currentDelay = data.getMinDelay() + (d > 0? npc.getRNG().nextInt(d) : 0);
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
  //TODO pick random song if random is selected... (ensure it was not the last song played?)
  SongPlayData data = bard.getTuneData();
  tuneIndex++;
  if(tuneIndex>=data.size()){tuneIndex=0;}  
  }

private void startSong()
  {
  SongPlayData data = bard.getTuneData();
  SongEntry entry = data.get(tuneIndex);
  maxPlayTime = (int)(entry.length() * 20.f * 60.f);//convert minutes into ticks
  float volume = (float)entry.volume() * 0.01f;
  bard.worldObj.playSoundAtEntity(bard, entry.name(), 3.0f * volume, 1.f);
  playing=true;
  playTime=0;
  }

}
