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
  super.sendInitData();
  //TODO send tune data to client
  }

@Override
public void handlePacketData(NBTTagCompound tag)
  {
  //TODO read tune data, set into tune-data instance
  //this will work for both directions....
  refreshGui();
  }

}
