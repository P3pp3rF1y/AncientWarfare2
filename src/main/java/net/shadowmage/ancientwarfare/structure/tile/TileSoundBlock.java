package net.shadowmage.ancientwarfare.structure.tile;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.shadowmage.ancientwarfare.core.util.SongPlayData;
import net.shadowmage.ancientwarfare.core.util.SongPlayData.SongEntry;

public class TileSoundBlock extends TileEntity
{

private boolean playing = false;//if currently playing a tune.
private boolean redstoneInteraction = false;
private int currentDelay;//the current cooldown delay.  if not playing, this delay will be incremented before attempting to start next song
private int tuneIndex = -1;//the index of the song being played / to play, incremented/updated on songStart()
private int playerCheckDelay;//used to not check for players -every- tick. checks every 10 ticks
private int playTime;//tracking current play time.  when this exceeds length, cooldown delay is triggered
private SongPlayData tuneData;

public TileSoundBlock()
  {
  tuneData = new SongPlayData();
  }

@Override
public void updateEntity()
  {
  if(worldObj.isRemote){return;}
  if(playing)
    {
    playTime--;
    if(playTime<=0)
      {
      endSong();
      }
    }
  else
    {
    currentDelay--;
    if(currentDelay<=0)
      {
      if(tuneData.getPlayOnPlayerEntry())
        {
        if(playerCheckDelay<=0)
          {
          playerCheckDelay = 20;
          AxisAlignedBB aabb = AxisAlignedBB.getBoundingBox(xCoord-20, yCoord-20, zCoord-20, xCoord+21, yCoord+21, zCoord+21);
          @SuppressWarnings("unchecked")
          List<EntityPlayer> list = worldObj.getEntitiesWithinAABB(EntityPlayer.class, aabb);
          if(list!=null && !list.isEmpty())
            {     
            startSong();
            }  
          }
        }
      else if(isRedstoneInteraction())
        {
        if(worldObj.getBlockPowerInput(xCoord, yCoord, zCoord)>0)
          {
          startSong();          
          }
        }
      else
        {
        startSong();
        }
      }
    }
  }

private void startSong()
  {
  playing=true;
  playTime=0;
  if(tuneData.getIsRandom())
    {
    tuneIndex = 0;
    if(tuneData.size()>0){tuneIndex = worldObj.rand.nextInt(tuneData.size());}
    }
  else
    {
    tuneIndex++;
    if(tuneIndex>=tuneData.size()){tuneIndex=0;}
    }
  if(tuneData.size()<=0){return;}
  SongEntry entry = tuneData.get(tuneIndex);
  playTime = (int)(entry.length() * 60.f * 20.f);//minutes(decimal) to ticks conversion
  
  float volume = 3.f  * (float)entry.volume() * 0.01f;
  worldObj.playSoundEffect(xCoord+0.5d, yCoord+0.5d, zCoord+0.5d, entry.name(), volume, 1.f);
  }

private void endSong()
  {
  playing=false;
  playTime = 0;
  int delay = tuneData.getMinDelay();
  int diff = tuneData.getMaxDelay() - delay;
  if(diff>0){delay += worldObj.rand.nextInt(diff);}
  currentDelay = delay;
  }

@Override
public void readFromNBT(NBTTagCompound tag)
  {
  super.readFromNBT(tag);
  tuneData.readFromNBT(tag.getCompoundTag("tuneData"));
  redstoneInteraction = tag.getBoolean("redstone");
  }

@Override
public void writeToNBT(NBTTagCompound tag)
  {
  super.writeToNBT(tag);
  tag.setTag("tuneData", tuneData.writeToNBT(new NBTTagCompound()));
  tag.setBoolean("redstone", redstoneInteraction);
  }

public SongPlayData getTuneData()
  {
  return tuneData;
  }

public boolean isRedstoneInteraction()
  {
  return redstoneInteraction;
  }

public void setRedstoneInteraction(boolean redstoneInteraction)
  {
  this.redstoneInteraction = redstoneInteraction;
  }

}
