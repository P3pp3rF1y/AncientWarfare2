package net.shadowmage.ancientwarfare.automation.container;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraftforge.common.util.Constants;
import net.shadowmage.ancientwarfare.automation.tile.TileChunkLoaderDeluxe;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;

public class ContainerChunkLoaderDeluxe extends ContainerBase
{

public Set<ChunkCoordIntPair> ccipSet = new HashSet<ChunkCoordIntPair>();
public TileChunkLoaderDeluxe chunkLoader;

public ContainerChunkLoaderDeluxe(EntityPlayer player, int x, int y, int z)
  {
  super(player, x, y, z);
  chunkLoader = (TileChunkLoaderDeluxe) player.worldObj.getTileEntity(x, y, z);
  if(chunkLoader==null){throw new IllegalArgumentException("cannot have null CL tile:");}
  if(!player.worldObj.isRemote)
    {
    ccipSet.addAll(chunkLoader.getForcedChunks());
    chunkLoader.addViewer(this);
    }
  }

@Override
public void onContainerClosed(EntityPlayer par1EntityPlayer)
  {  
  super.onContainerClosed(par1EntityPlayer);
  chunkLoader.removeViewer(this);
  }

@Override
public void handlePacketData(NBTTagCompound tag)
  {
  if(tag.hasKey("chunkList"))
    {
    ccipSet.clear();
    NBTTagList list = tag.getTagList("chunkList", Constants.NBT.TAG_COMPOUND);
    NBTTagCompound ccipTag;
    ChunkCoordIntPair ccip;
    for(int i = 0; i < list.tagCount(); i++)
      {
      ccipTag = list.getCompoundTagAt(i);
      ccip = new ChunkCoordIntPair(ccipTag.getInteger("x"), ccipTag.getInteger("z"));
      ccipSet.add(ccip);
      }
//    AWLog.logDebug("received forced chunk set of: "+ccipSet);
    refreshGui();
    }
  if(tag.hasKey("forced"))
    {
    ChunkCoordIntPair ccip = new ChunkCoordIntPair(tag.getInteger("x"), tag.getInteger("z"));
    chunkLoader.addOrRemoveChunk(ccip);
    //should trigger an updateViewers and then a re-send of forced chunk list from tile
    }
  }

@Override
public void sendInitData()
  {
  sendChunkList();
  }

private void sendChunkList()
  {
  NBTTagCompound tag = new NBTTagCompound();
  NBTTagList list = new NBTTagList();
  NBTTagCompound ccipTag;
  for(ChunkCoordIntPair ccip : this.ccipSet)
    {
    ccipTag = new NBTTagCompound();
    ccipTag.setInteger("x", ccip.chunkXPos);
    ccipTag.setInteger("z", ccip.chunkZPos);
    list.appendTag(ccipTag);
    }  
  tag.setTag("chunkList", list);
  sendDataToClient(tag);
  }

public void onChunkLoaderSetUpdated(Set<ChunkCoordIntPair> ccipSet)
  {
  this.ccipSet.clear();
  this.ccipSet.addAll(ccipSet);
  sendChunkList();
  }

}
