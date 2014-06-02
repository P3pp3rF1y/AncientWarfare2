package net.shadowmage.ancientwarfare.automation.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.common.ForgeChunkManager.Type;
import net.shadowmage.ancientwarfare.automation.AncientWarfareAutomation;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.interfaces.IInteractableTile;

public class TileChunkLoaderSimple extends TileEntity implements IInteractableTile
{

Ticket chunkTicket = null;
boolean init = false;

public TileChunkLoaderSimple()
  {
  
  }

@Override
public boolean canUpdate()
  {
  return true;
  }

@Override
public void updateEntity()
  {
  if(!worldObj.isRemote && !init)
    {
    setupInitialTicket();
    }
  }

public void setTicketFromCallback(Ticket tk)
  {
  if(this.chunkTicket!=null)
    {
    for(ChunkCoordIntPair ccip : tk.getChunkList())
      {
      ForgeChunkManager.unforceChunk(tk, ccip);    
      }
    }
  this.chunkTicket = tk;
  if(tk!=null)
    {
    for(ChunkCoordIntPair ccip : tk.getChunkList())
      {
      ForgeChunkManager.forceChunk(tk, ccip);      
      }    
    }
  AWLog.logDebug("set ticket from loading callback....ticket: "+tk);
  if(tk!=null)
    {
    AWLog.logDebug("ticket now has forced chunks of: "+tk.getChunkList());
    }
  }

protected void setupInitialTicket()
  {
  AWLog.logDebug("setting init ticket for chunk loader simple...");
  Ticket tk = ForgeChunkManager.requestTicket(AncientWarfareAutomation.instance, worldObj, Type.NORMAL);
  if(tk!=null)
    {
    setInitialTicket(tk);
    }
  else
    {
    AWLog.logDebug("TICKET WAS NULL...");
    }    
  AWLog.logDebug("ticket now has chunks: "+tk.getChunkList());
  init = true;
  }

protected void setInitialTicket(Ticket tk)
  {
  NBTTagCompound posTag = new NBTTagCompound();
  posTag.setInteger("x", xCoord);
  posTag.setInteger("y", yCoord);
  posTag.setInteger("z", zCoord);
  tk.getModData().setTag("tilePosition", posTag);
  forceInitialChunks();
  }

protected void forceInitialChunks()
  {
  int cx = xCoord>>4;
  int cz = zCoord>>4;
  for(int x = cx-1; x<=cx+1; x++)
    {
    for(int z = cz-1; z<=cz+1; z++)
      {
      ChunkCoordIntPair ccip = new ChunkCoordIntPair(x, z);
      ForgeChunkManager.forceChunk(chunkTicket, ccip);
      }
    }
  }

@Override
public boolean onBlockClicked(EntityPlayer player)
  {
  if(!player.worldObj.isRemote)
    {
    //TODO create gui/container
    //TODO open proper GUI
    }
  return false;
  }

@Override
public void readFromNBT(NBTTagCompound tag)
  {
  super.readFromNBT(tag);
  init = tag.getBoolean("init");
  }

@Override
public void writeToNBT(NBTTagCompound tag)
  {
  super.writeToNBT(tag);
  tag.setBoolean("init", init);
  }

}
