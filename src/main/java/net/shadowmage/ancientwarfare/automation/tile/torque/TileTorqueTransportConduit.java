package net.shadowmage.ancientwarfare.automation.tile.torque;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.ForgeDirection;
import net.shadowmage.ancientwarfare.automation.config.AWAutomationStatics;
import net.shadowmage.ancientwarfare.core.api.ModuleStatus;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque.ITorqueTile;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque.SidedTorqueCell;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque.TorqueCell;

public class TileTorqueTransportConduit extends TileTorqueTransportBase
{

boolean connections[] = null;
SidedTorqueCell[] storage = new SidedTorqueCell[6];

/**
 * client side this == 0.0 -> 1.0
 */
double clientEnergyState;

/**
 * server side this == 0 -> 100 (integer percent)
 * client side this == 0.0 -> 1.0 (actual percent)
 */
double clientDestEnergyState;

/**
 * used client side for rendering
 */
double rotation, prevRotation;

public TileTorqueTransportConduit()
  {
  for(int i = 0; i <6; i++)
    {
    storage[i] = new SidedTorqueCell(32, 32, 32, 1, ForgeDirection.values()[i], this);//TODO set from config
    }
  }

@Override
public void updateEntity()
  {
  super.updateEntity();
  if(!worldObj.isRemote)
    { 
    serverNetworkUpdate();   
    double d = getTotalTorque(); 
    torqueIn = d - prevEnergy;
    transferPower();
    torqueOut = d - getTotalTorque();
    prevEnergy = getTotalTorque();
    balanceStorage();
    }
  else
    {
    clientNetworkUpdate();
    updateRotation();
    }
  }

protected void transferPower()
  {
  transferPowerTo(getPrimaryFacing());
  }

protected void balanceStorage()
  {
  int face = getPrimaryFacing().ordinal();
  TorqueCell out = storage[face];  
  double total = 0;  
  TorqueCell in;
  for(int i = 0; i < 6; i++)
    {
    if(i==face){continue;}
    in = storage[i];
    total+=in.getEnergy();
    }  
  if(total>0)
    {
    double transfer = Math.min(total, out.getMaxEnergy()-out.getEnergy());  
    double percent = transfer / total;
    for(int i = 0; i < 6; i++)
      {
      if(i==face){continue;}
      in = storage[i];
      in.setEnergy(in.getEnergy() - percent*transfer);
      }
    out.setEnergy(out.getEnergy()+transfer);
    }  
  }

@Override
protected void serverNetworkSynch()
  {
  int percent = (int)(storage[getPrimaryFacing().ordinal()].getPercentFull()*100.d);
  percent += (int)(torqueOut / storage[getPrimaryFacing().ordinal()].getMaxOutput());
  if(percent>100){percent=100;}
  if(percent != clientDestEnergyState)
    {
    clientDestEnergyState = percent;
    sendSideRotation(getPrimaryFacing(), percent);    
    }
  }

@Override
protected void updateRotation()
  {
  prevRotation = rotation;
  if(clientEnergyState > 0)
    {
    double r = AWAutomationStatics.low_rpt * clientEnergyState;
    rotation += r;
    }
  }

@Override
protected void clientNetworkUpdate()
  {
  if(clientEnergyState != clientDestEnergyState)
    {
    if(networkUpdateTicks>0)
      {
      clientEnergyState += (clientDestEnergyState - clientEnergyState) / (double)networkUpdateTicks;
      }
    else
      {
      clientEnergyState = clientDestEnergyState;
      }
    }
  }

@Override
protected void handleClientRotationData(ForgeDirection side, int value)
  {
  clientDestEnergyState = ((double)value) * 0.01d;
  }

@Override
public boolean canInputTorque(ForgeDirection from)
  {
  return from!=orientation;
  }

@Override
public boolean canOutputTorque(ForgeDirection towards)
  {
  return towards==orientation;
  }

public boolean[] getConnections()
  {
  if(connections==null){buildConnections();}
  return connections;
  }

@Override
public void onNeighborTileChanged()
  {
  super.onNeighborTileChanged();
  connections=null;
  }

protected void buildConnections()
  {
  connections = new boolean[6];
  ITorqueTile[] cache = getTorqueCache();
  for(int i = 0; i < 6; i++)
    {
    if(cache[i]!=null){connections[i]=true;}
    }
  if(ModuleStatus.buildCraftLoaded)
    {
    TileEntity[] tes = getBCCache();
    for(int i = 0; i < 6; i++)
      {
      if(tes[i]!=null){connections[i]=true;}
      }
    }
  if(ModuleStatus.redstoneFluxEnabled)
    {
    TileEntity[] tes = getRFCache();
    for(int i = 0; i < 6; i++)
      {
      if(tes[i]!=null){connections[i]=true;}
      }
    }
  }

@Override
public double getMaxTorque(ForgeDirection from)
  {
  return storage[from.ordinal()].getMaxEnergy();
  }

@Override
public double getTorqueStored(ForgeDirection from)
  {
  return storage[from.ordinal()].getEnergy();
  }

@Override
public double addTorque(ForgeDirection from, double energy)
  {
  return storage[from.ordinal()].addEnergy(energy);
  }

@Override
public double drainTorque(ForgeDirection from, double energy)
  {
  return storage[from.ordinal()].drainEnergy(energy);
  }

@Override
public double getMaxTorqueOutput(ForgeDirection from)
  {
  return storage[from.ordinal()].getMaxTickOutput();
  }

@Override
public double getMaxTorqueInput(ForgeDirection from)
  {
  return storage[from.ordinal()].getMaxTickInput();
  }

@Override
public boolean useOutputRotation(ForgeDirection from)
  {
  return true;
  }

@Override
public float getClientOutputRotation(ForgeDirection from, float delta)
  {
  return getRotation(rotation, prevRotation, delta);
  }

@Override
public NBTTagCompound getDescriptionTag()
  {
  NBTTagCompound tag = super.getDescriptionTag();
  tag.setInteger("clientEnergy", (int)(clientDestEnergyState * 100.d));
  return tag;
  }

@Override
public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt)
  {  
  super.onDataPacket(net, pkt);
  NBTTagCompound tag = pkt.func_148857_g();
  clientDestEnergyState = ((double)tag.getInteger("clientEnergy")) / 100.d;
  }

@Override
public void readFromNBT(NBTTagCompound tag)
  {  
  super.readFromNBT(tag);
  NBTTagList list = tag.getTagList("energyList", Constants.NBT.TAG_COMPOUND);
  for(int i = 0; i < 6; i++)
    {
    storage[i].readFromNBT(list.getCompoundTagAt(i));
    }
  }

@Override
public void writeToNBT(NBTTagCompound tag)
  {
  super.writeToNBT(tag);
  NBTTagList list = new NBTTagList();
  for(int i = 0; i < 6; i++)
    {
    list.appendTag(storage[i].writeToNBT(new NBTTagCompound()));
    }
  tag.setTag("energyList", list);
  }

@Override
protected double getTotalTorque()
  {
  double d = 0;
  ForgeDirection dir;
  for(int i = 0; i < 6; i++)
    {
    dir = ForgeDirection.values()[i];
    if(canInputTorque(dir) || canOutputTorque(dir))
      {
      d+=storage[i].getEnergy();
      }
    }
  return d;
  }

}
