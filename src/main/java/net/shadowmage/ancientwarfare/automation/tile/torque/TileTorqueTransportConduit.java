package net.shadowmage.ancientwarfare.automation.tile.torque;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.shadowmage.ancientwarfare.automation.config.AWAutomationStatics;
import net.shadowmage.ancientwarfare.automation.proxy.BCProxy;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque.ITorqueGenerator;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque.ITorqueReceiver;

public class TileTorqueTransportConduit extends TileTorqueTransportBase
{

boolean[] connections;

public TileTorqueTransportConduit()
  {
  energyDrainFactor = AWAutomationStatics.low_drain_factor;
  maxEnergy = AWAutomationStatics.low_conduit_energy_max;
  maxOutput = AWAutomationStatics.low_transfer_max;
  maxInput = AWAutomationStatics.low_transfer_max;
  }

@Override
public void updateEntity()
  {
  super.updateEntity();
  }

@Override
protected void buildNeighborCache()
  {  
  if(worldObj.isRemote){return;}
  connections = new boolean[6];
  neighborTileCache = new TileEntity[6];
  worldObj.theProfiler.startSection("AWPowerTileNeighborUpdate");
  ForgeDirection d, face;
  TileEntity te;
  face = getPrimaryFacing();
  for(int i = 0; i < 6; i++)
    {
    d = ForgeDirection.getOrientation(i);
    te = worldObj.getTileEntity(xCoord+d.offsetX, yCoord+d.offsetY, zCoord+d.offsetZ);  
    if(BCProxy.instance.isPowerPipe(worldObj, te))//always connect to BC pipes, who knows what direction the power is flowing....
      {
      connections[i]=true;      
      }
    else if(face==d)//output side, only connect to receivers
      {
      if(te instanceof ITorqueReceiver)
        {
        ITorqueReceiver gen = (ITorqueReceiver)te;
        if(gen.canInput(d))
          {
          connections[i]=true;          
          }         
        }
      }
    else if(te instanceof ITorqueGenerator)//else an input side, only connect if the other tile is an output
      {
      ITorqueGenerator gen = (ITorqueGenerator)te;
      if(gen.canOutput(d.getOpposite()))
        {
        connections[i]=true;        
        }
      }    
    this.neighborTileCache[i] = te;
    }
  worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
  worldObj.theProfiler.endSection();
  }

public boolean[] getConnections()
  {
  if(connections==null)
    {
    connections = new boolean[6];
    }
  return connections;
  }

private int getConnectionsInt()
  {
  if(connections==null)
    {
    buildNeighborCache();
    }  
  int con = 0;
  int c;
  for(int i = 0; i < 6; i++)
    {
    c = connections[i]==true? 1: 0;
    con = con + (c<<i);
    }  
//  String out = "";
//  for(boolean val : connections)
//    {
//    out = out+val+",";
//    }
//  out = out.substring(0, out.length()-1);
//  AWLog.logDebug("connections out: "+out);
  return con;
  }

private void readConnectionsInt(int con)
  {
  int c;
  if(connections==null){connections = new boolean[6];}
  for(int i = 0; i < 6; i++)
    {
    c = (con>>i) & 0x1;
    connections[i] = c==1;
    }
//  String out = "";
//  for(boolean val : connections)
//    {
//    out = out+val+",";
//    }
//  out = out.substring(0, out.length()-1);
//  AWLog.logDebug("connections in: "+out);
  }

@Override
public boolean receiveClientEvent(int a, int b)
  {
  super.receiveClientEvent(a, b);
  if(!worldObj.isRemote){return true;}
  if(a==0){readConnectionsInt(b);}  
  return true;
  }

@Override
public Packet getDescriptionPacket()
  {
  NBTTagCompound tag = new NBTTagCompound();
  tag.setInteger("connections", getConnectionsInt());
  tag.setInteger("orientation", orientation.ordinal());
  return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 0, tag);
  }

@Override
public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt)
  {  
  if(pkt.func_148857_g().hasKey("connections"))
    {
    readConnectionsInt(pkt.func_148857_g().getInteger("connections"));
    }
  orientation = ForgeDirection.getOrientation(pkt.func_148857_g().getInteger("orientation"));
  this.worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
  }

}
