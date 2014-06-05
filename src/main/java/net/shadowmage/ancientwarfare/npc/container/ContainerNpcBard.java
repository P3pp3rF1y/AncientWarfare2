package net.shadowmage.ancientwarfare.npc.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.npc.entity.NpcBard;

public class ContainerNpcBard extends ContainerBase
{

public int bardTuneNumber;
public int bardPlayLength;//num of ticks for the tune
public int bardPlayChance;//0-100, chance out of 100 to play a tune
public int bardPlayRecheckDelay;//how many ticks should pass between rechecking the play-delay?
public NpcBard npc;
public ContainerNpcBard(EntityPlayer player, int x, int y, int z)
  {
  super(player, x, y, z);
  npc = (NpcBard) player.worldObj.getEntityByID(x);
  if(npc==null){throw new IllegalArgumentException("Npc must be a bard for bard container");}
  }

@Override
public void sendInitData()
  {
  super.sendInitData();
  NBTTagCompound tag = new NBTTagCompound();
  tag.setInteger("tune", npc.bardTuneNumber);
  tag.setInteger("length", npc.bardPlayLength);
  tag.setInteger("chance", npc.bardPlayChance);
  tag.setInteger("delay", npc.bardPlayRecheckDelay);
  sendDataToClient(tag);
  }

@Override
public void handlePacketData(NBTTagCompound tag)
  {
  if(tag.hasKey("tune"))
    {
    bardTuneNumber = tag.getInteger("tune");
    if(!player.worldObj.isRemote){npc.bardTuneNumber=bardTuneNumber;}
    }
  if(tag.hasKey("length"))
    {
    bardPlayLength = tag.getInteger("length");
    if(!player.worldObj.isRemote){npc.bardPlayLength=bardPlayLength;}
    }
  if(tag.hasKey("chance"))
    {
    bardPlayChance = tag.getInteger("chance");
    if(!player.worldObj.isRemote){npc.bardPlayChance=bardPlayChance;}
    }
  if(tag.hasKey("delay"))
    {
    bardPlayRecheckDelay = tag.getInteger("delay");
    if(!player.worldObj.isRemote){npc.bardPlayRecheckDelay=bardPlayRecheckDelay;}
    }
  refreshGui();
  }

@Override
public void detectAndSendChanges()
  {  
  super.detectAndSendChanges();
  NBTTagCompound tag = null;
  if(bardTuneNumber!=npc.bardTuneNumber)
    {
    if(tag==null){tag=new NBTTagCompound();}
    bardTuneNumber=npc.bardTuneNumber;
    tag.setInteger("tune", npc.bardTuneNumber);
    tag.setInteger("chance", npc.bardPlayChance);
    tag.setInteger("delay", npc.bardPlayRecheckDelay);
    }
  if(bardPlayLength!=npc.bardPlayLength)
    {
    if(tag==null){tag=new NBTTagCompound();}
    bardPlayLength = npc.bardPlayLength;
    tag.setInteger("length", npc.bardPlayLength);
    }
  if(bardPlayChance!=npc.bardPlayChance)
    {
    if(tag==null){tag=new NBTTagCompound();}
    bardPlayChance = npc.bardPlayChance;
    tag.setInteger("chance", npc.bardPlayChance);
    }
  if(bardPlayRecheckDelay!=npc.bardPlayRecheckDelay)
    {
    if(tag==null){tag=new NBTTagCompound();}
    bardPlayRecheckDelay = npc.bardPlayRecheckDelay;
    tag.setInteger("delay", npc.bardPlayRecheckDelay);
    }  
  if(tag!=null)
    {
    sendDataToClient(tag);
    }
  }

}
