package net.shadowmage.ancientwarfare.npc.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.npc.entity.NpcBard;
import net.shadowmage.ancientwarfare.npc.entity.NpcBard.BardTuneData;

public class ContainerNpcBard extends ContainerBase
{

public BardTuneData data;
public NpcBard npc;
public ContainerNpcBard(EntityPlayer player, int x, int y, int z)
  {
  super(player, x, y, z);
  npc = (NpcBard) player.worldObj.getEntityByID(x);
  if(npc==null){throw new IllegalArgumentException("Npc must be a bard for bard container");}
  data = npc.getTuneData();
  }

@Override
public void sendInitData()
  {
  NBTTagCompound tag = new NBTTagCompound();
  tag.setTag("tuneData", data.writeToNBT(new NBTTagCompound()));
  sendDataToClient(tag);
  }

@Override
public void handlePacketData(NBTTagCompound tag)
  {
  if(tag.hasKey("tuneData"))
    {
    data.readFromNBT(tag.getCompoundTag("tuneData"));
    }
  refreshGui();
  }

public void sendTuneDataToServer(EntityPlayer player)
  {
  if(player.worldObj.isRemote)//handles sending new/updated/changed data back to server on GUI close.  the last GUI to close will be the one whos data 'sticks'
    {
    NBTTagCompound tag = new NBTTagCompound();
    tag.setTag("tuneData", data.writeToNBT(new NBTTagCompound()));
    sendDataToServer(tag);
    }
  }

}
