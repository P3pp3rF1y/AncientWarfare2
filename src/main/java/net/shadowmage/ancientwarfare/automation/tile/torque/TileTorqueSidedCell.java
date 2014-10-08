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
import net.shadowmage.ancientwarfare.core.interfaces.ITorque.ITorqueTile;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque.SidedTorqueCell;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque.TorqueCell;

public abstract class TileTorqueSidedCell extends TileTorqueBase
{

boolean connections[] = null;
SidedTorqueCell[] storage = new SidedTorqueCell[6];

/**
 * client side this == 0.0 -> 100.0
 */
double clientEnergyState;

/**
 * server side this == 0 -> 100 (integer percent)
 * client side this == 0 -> 100 (integer percent)
 */
int clientDestEnergyState;

/**
 * used client side for rendering
 */
double rotation, prevRotation;

public TileTorqueSidedCell()
  {
  
  }

@Override
public void updateEntity()
  {
  super.updateEntity();
  if(!worldObj.isRemote)
    { 
    serverNetworkUpdate();
    torqueIn = getTotalTorque() - prevEnergy;
    balanceStorage();
    torqueOut = transferPower();
    prevEnergy = getTotalTorque();
    }
  else
    {
    clientNetworkUpdate();
    updateRotation();
    }
  }

protected double transferPower()
  {
  return transferPowerTo(getPrimaryFacing());
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
    total += storage[i].getEnergy();
    }  
  if(total>0)
    {
    double transfer = Math.min(total, out.getMaxEnergy() - out.getEnergy());  
    double percent = transfer / total;
    transfer = 0;
    double fromEach;
    for(int i = 0; i < 6; i++)
      {
      if(i==face){continue;}
      in = storage[i];
      fromEach = in.getEnergy() * percent;
      transfer+=fromEach;
      in.setEnergy(in.getEnergy() - fromEach);
      }
    out.setEnergy(out.getEnergy() + transfer);
    }    
  }

@Override
protected void serverNetworkSynch()
  {
  int percent = (int)(storage[getPrimaryFacing().ordinal()].getPercentFull()*100.d);
  int percent2 = (int)((torqueOut / storage[getPrimaryFacing().ordinal()].getMaxOutput())*100.d);
  percent = Math.max(percent, percent2);
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
    double r = AWAutomationStatics.low_rpt * clientEnergyState * 0.01d;
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
      clientEnergyState += (clientDestEnergyState - clientEnergyState) / ((double)networkUpdateTicks);
      networkUpdateTicks--;
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
  clientDestEnergyState = value;
  this.networkUpdateTicks = AWAutomationStatics.energyMinNetworkUpdateFrequency;
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
  if(cache==null){throw new RuntimeException("CACHE WAS NULL FROM BUILDING, SOMETHING IS FUCKED...REALLY FUCKED...");}
  if(worldObj==null){throw new RuntimeException("attempt to build neighbor connections on null world");}
  ForgeDirection dir;
  for(int i = 0; i < 6; i++)
    {
    dir = ForgeDirection.values()[i];
    if(cache[i]!=null)
      {
      connections[i]=(cache[i].canInputTorque(dir.getOpposite()) && canOutputTorque(dir)) || (cache[i].canOutputTorque(dir.getOpposite()) && canInputTorque(dir));
      }
    }
  if(ModuleStatus.buildCraftLoaded)
    {
    TileEntity[] tes = getBCCache();
    for(int i = 0; i < 6; i++)
      {
      if(cache[i]!=null){continue;}//already examined that side..
      if(tes[i]!=null){connections[i]=true;}
      }
    }
  if(ModuleStatus.redstoneFluxEnabled)
    {
    TileEntity[] tes = getRFCache();
    for(int i = 0; i < 6; i++)
      {
      if(cache[i]!=null){continue;}//already examined that side..
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
  tag.setInteger("clientEnergy", (int)clientDestEnergyState);
  return tag;
  }

@Override
public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt)
  {  
  super.onDataPacket(net, pkt);
  NBTTagCompound tag = pkt.func_148857_g();
  clientDestEnergyState = tag.getInteger("clientEnergy");
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
  clientDestEnergyState = tag.getInteger("clientEnergy");
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
  tag.setInteger("clientEnergy", clientDestEnergyState);
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
