package net.shadowmage.ancientwarfare.structure.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.util.SongPlayData;
import net.shadowmage.ancientwarfare.structure.tile.TileSoundBlock;

public class ContainerSoundBlock extends ContainerBase
{

public SongPlayData data;
public TileSoundBlock tile;
public boolean redstoneInteraction;

public ContainerSoundBlock(EntityPlayer player, int x, int y, int z)
  {
  super(player, x, y, z);
  tile = (TileSoundBlock) player.worldObj.getTileEntity(x, y, z);
  data = tile.getTuneData();
  redstoneInteraction = tile.isRedstoneInteraction();
  }

@Override
public void sendInitData()
  {
  NBTTagCompound tag = new NBTTagCompound();
  tag.setTag("tuneData", data.writeToNBT(new NBTTagCompound()));
  tag.setBoolean("redstone", redstoneInteraction);
  sendDataToClient(tag);
  }

@Override
public void handlePacketData(NBTTagCompound tag)
  {
  if(tag.hasKey("tuneData"))
    {
    data.readFromNBT(tag.getCompoundTag("tuneData"));
    }
  redstoneInteraction = tag.getBoolean("redstone");
  tile.setRedstoneInteraction(redstoneInteraction);
  refreshGui();
  }

public void sendTuneDataToServer(EntityPlayer player)
  {
  if(player.worldObj.isRemote)//handles sending new/updated/changed data back to server on GUI close.  the last GUI to close will be the one whos data 'sticks'
    {
    NBTTagCompound tag = new NBTTagCompound();
    tag.setTag("tuneData", data.writeToNBT(new NBTTagCompound()));
    tag.setBoolean("redstone", redstoneInteraction);
    sendDataToServer(tag);
    }
  }
}
