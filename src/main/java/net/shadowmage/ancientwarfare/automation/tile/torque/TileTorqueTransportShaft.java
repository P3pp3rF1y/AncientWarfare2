package net.shadowmage.ancientwarfare.automation.tile.torque;

import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.shadowmage.ancientwarfare.automation.config.AWAutomationStatics;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque.ITorqueTile;

public class TileTorqueTransportShaft extends TileTorqueTransportBase
{

public TileTorqueTransportShaft prev, next;

public TileTorqueTransportShaft()
  {
  energyDrainFactor = AWAutomationStatics.low_drain_factor;
  maxEnergy = AWAutomationStatics.low_conduit_energy_max;
  maxOutput = AWAutomationStatics.low_transfer_max;
  maxInput = AWAutomationStatics.low_transfer_max;
  maxRpm = AWAutomationStatics.low_rpm_max;
  }

@Override
public void onBlockUpdated()
  {
  super.onBlockUpdated();
  prev = next = null;
  ITorqueTile output = neighborTorqueTileCache[orientation.ordinal()];
  if(output !=null && output.getClass()==this.getClass() && output.canInput(orientation.getOpposite()))
    {
    next = (TileTorqueTransportShaft) output;
    next.prev=this;
    }
  ITorqueTile input = neighborTorqueTileCache[orientation.getOpposite().ordinal()];
  if(input !=null && input.getClass()==this.getClass() && input.canOutput(orientation))
    {
    prev = (TileTorqueTransportShaft) input;
    prev.next=this;
    }
  String n = String.valueOf(next==null ? "null" : next.hashCode()+":"+next.orientation);
  String p = String.valueOf(prev==null ? "null" : prev.hashCode()+":"+prev.orientation);
  
  AWLog.logDebug("prev: "+p+" this: "+this.hashCode()+":"+this.orientation+" next: "+n);
  }

@Override
protected void outputPower()
  {
  if(prev==null)//head of the line
    {
    double total = storedEnergy;
    TileTorqueTransportShaft n = next;
    TileTorqueTransportShaft last = this;//might also be end of the line...
    int num = 1;
    while(n!=null)
      {
      total+=n.storedEnergy;
      num++;
      last = n;
      n = n.next;
      }    
    total /= (double)num;
    storedEnergy = total;
    n = next;
    while(n!=null)
      {
      n.storedEnergy = total;
      n = n.next;
      }
    ITorque.transferPower(worldObj, last.xCoord, last.yCoord, last.zCoord, last); 
    }
  }

@Override
protected void updateRotation()
  {
  if(prev==null)
    {
    super.updateRotation();    
    TileTorqueTransportShaft n = next;
    while(n!=null)
      {
      AWLog.logDebug("updating rotation for: "+n.hashCode());
      n.rotation = rotation;
      n.prevRotation = prevRotation;
      n = n.next;
      }    
    }
  }

@Override
public boolean canOutput(ForgeDirection towards)
  {
  return towards==orientation;
  }

@Override
public boolean canInput(ForgeDirection from)
  {
  return from==orientation.getOpposite();
  }

@Override
public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt)
  {
  super.onDataPacket(net, pkt);
  onBlockUpdated();
//  worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
  worldObj.func_147453_f(xCoord, yCoord, zCoord, getBlockType());
  }

}
