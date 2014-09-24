package net.shadowmage.ancientwarfare.automation.tile.torque;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.shadowmage.ancientwarfare.automation.config.AWAutomationStatics;
import net.shadowmage.ancientwarfare.automation.proxy.BCProxy;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque.ITorqueTile;

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
      if(te instanceof ITorqueTile)
        {
        ITorqueTile rec = (ITorqueTile)te;
        if(rec.canInput(d.getOpposite()))
          {
          connections[i]=true;          
          }         
        }
      }
    else if(canInput(d))
      {
      if(te instanceof ITorqueTile)
        {
        ITorqueTile gen = (ITorqueTile)te;
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

}
