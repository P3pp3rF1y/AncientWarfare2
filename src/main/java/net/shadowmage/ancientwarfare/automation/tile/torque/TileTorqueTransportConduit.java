package net.shadowmage.ancientwarfare.automation.tile.torque;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.shadowmage.ancientwarfare.automation.config.AWAutomationStatics;
import net.shadowmage.ancientwarfare.automation.proxy.BCProxy;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque.ITorqueGenerator;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque.ITorqueReceiver;

public class TileTorqueTransportConduit extends TileTorqueTransportBase
{

public TileTorqueTransportConduit()
  {
  energyDrainFactor = AWAutomationStatics.low_drain_factor;
  maxEnergy = AWAutomationStatics.low_conduit_energy_max;
  maxOutput = AWAutomationStatics.low_transfer_max;
  maxInput = AWAutomationStatics.low_transfer_max;
  }

@Override
protected void buildNeighborCache()
  {  
  if(worldObj.isRemote){return;}
  connections = new boolean[6];
  neighborTileCache = new TileEntity[6];
  worldObj.theProfiler.startSection("AWPowerTileNeighborUpdate");
  ForgeDirection d;
  TileEntity te;
  for(int i = 0; i < 6; i++)
    {
    d = ForgeDirection.getOrientation(i);
    te = worldObj.getTileEntity(xCoord+d.offsetX, yCoord+d.offsetY, zCoord+d.offsetZ);  
    this.neighborTileCache[i] = te;
    if(te==null){continue;}
    if(BCProxy.instance.isPowerPipe(worldObj, te))//always connect to BC pipes, who knows what direction the power is flowing....
      {
      connections[i]=true;      
      }
    else if(canOutput(d))
      {
      if(te instanceof ITorqueReceiver)
        {
        ITorqueReceiver rec = (ITorqueReceiver)te;
        if(rec.canInput(d.getOpposite()))
          {
          connections[i]=true;          
          }         
        }
      }
    else if(canInput(d))
      {
      if(te instanceof ITorqueGenerator)
        {
        ITorqueGenerator gen = (ITorqueGenerator)te;
        if(gen.canOutput(d.getOpposite()))
          {
          connections[i]=true;        
          }
        }
      } 
    }
  worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
  worldObj.theProfiler.endSection();
  }

@Override
public NBTTagCompound getDescriptionTag()
  {
  NBTTagCompound tag = new NBTTagCompound();
  tag.setInteger("connections", getConnectionsInt());
  tag.setInteger("orientation", orientation.ordinal());
  tag.setInteger("clientEnergy", clientEnergy);
  return tag;
  }

@Override
public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt)
  {  
  if(pkt.func_148857_g().hasKey("connections"))
    {
    readConnectionsInt(pkt.func_148857_g().getInteger("connections"));
    }
  orientation = ForgeDirection.getOrientation(pkt.func_148857_g().getInteger("orientation"));
  clientEnergy = pkt.func_148857_g().getInteger("clientEnergy");
  clientDestEnergy = clientEnergy;
  this.worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
  }

}
