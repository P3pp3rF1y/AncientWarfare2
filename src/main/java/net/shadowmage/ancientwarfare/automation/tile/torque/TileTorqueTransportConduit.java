package net.shadowmage.ancientwarfare.automation.tile.torque;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.shadowmage.ancientwarfare.automation.proxy.BCProxy;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque.ITorqueGenerator;

public class TileTorqueTransportConduit extends TileTorqueTransportBase
{

boolean[] connections;

@Override
protected void buildNeighborCache()
  {  
  connections = new boolean[6];
  neighborTileCache = new TileEntity[6];
  worldObj.theProfiler.startSection("AWPowerTileNeighborUpdate");
  ForgeDirection d;
  TileEntity te;
  for(int i = 0; i < 6; i++)
    {
    d = ForgeDirection.getOrientation(i);
    te = worldObj.getTileEntity(xCoord+d.offsetX, yCoord+d.offsetY, zCoord+d.offsetZ);
    if((te instanceof ITorqueGenerator || BCProxy.instance.isPowerPipe(worldObj, te)))
      {
      connections[i]=true;
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
    buildNeighborCache();
    }
  return connections;
  }

private int getConnectionsInt()
  {
  int con = 0;
  int c;
  for(int i = 0; i < 6; i++)
    {
    c = connections[i]==true? 1: 0;
    con = con + (c<<i);
    }  
  String out = "";
  for(boolean val : connections)
    {
    out = out+val+",";
    }
  out = out.substring(0, out.length()-1);
  AWLog.logDebug("connections out: "+out);
  return con;
  }

private void readConnectionsInt(int con)
  {
  int c;
  if(connections==null){connections = new boolean[6];}
  for(int i = 0; i < 6; i++)
    {
    c = (con>>i) & 0x1;
    connections[i]=c==1;
    }
  String out = "";
  for(boolean val : connections)
    {
    out = out+val+",";
    }
  out = out.substring(0, out.length()-1);
  AWLog.logDebug("connections in: "+out);
  }

@Override
public boolean receiveClientEvent(int a, int b)
  {
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
  super.onDataPacket(net, pkt);
  if(pkt.func_148857_g().hasKey("connections"))
    {
    readConnectionsInt(pkt.func_148857_g().getInteger("connections"));
    }
  orientation = ForgeDirection.getOrientation(pkt.func_148857_g().getInteger("orientation"));
  }

}
