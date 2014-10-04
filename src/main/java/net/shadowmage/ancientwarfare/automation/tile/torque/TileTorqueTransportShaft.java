package net.shadowmage.ancientwarfare.automation.tile.torque;

import net.minecraftforge.common.util.ForgeDirection;
import net.shadowmage.ancientwarfare.automation.config.AWAutomationStatics;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque.ITorqueTile;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque.TorqueCell;

public class TileTorqueTransportShaft extends TileTorqueSingleCell
{

private TileTorqueTransportShaft prev, next;

private boolean prevNeighborInvalid = true;
private boolean nextNeighborInvalid = true;

public TileTorqueTransportShaft()
  {
  double max = AWAutomationStatics.low_transfer_max;
  torqueCell = new TorqueCell(max, max, max, 1);
  }

@Override
protected void serverNetworkSynch()
  {
  if(prev()==null)
    {
    TileTorqueTransportShaft last = this;
    TileTorqueTransportShaft n = next();
    double totalPower = torqueCell.getEnergy();
    double num = 1;
    while(n!=null)
      {
      totalPower += n.torqueCell.getEnergy();
      last = n;
      num++;
      n = n.next;      
      }
    double avg = totalPower / num;
    double perc = avg / torqueCell.getMaxEnergy();
    double torqueOut = last.torqueOut;     
    
    int percent = (int)( perc * 100.d );
    int percent2 = (int)((torqueOut / last.torqueCell.getMaxOutput()) * 100.d);
//    AWLog.logDebug("shaft net synch, p1, p2: "+percent+" :: "+percent2 + " avg: "+avg+" avgper: "+perc+" lo: "+last.torqueOut);
    percent = Math.max(percent, percent2);
    if(percent != (int)clientDestEnergyState)
      {
      clientDestEnergyState = percent;
      sendSideRotation(getPrimaryFacing(), percent);    
      }    
    }
  }

@Override
protected void updateRotation()
  {
  if(prev()==null)
    {
    prevRotation = rotation;
    if(clientEnergyState > 0)
      {
      double r = AWAutomationStatics.low_rpt * clientEnergyState * 0.01d;
      rotation += r;
      }   
    TileTorqueTransportShaft n = next;
    while(n!=null)
      {
      n.rotation = rotation;
      n.prevRotation = prevRotation;
      n = n.next;
      }    
    }
  }

protected void onNeighborCacheInvalidated()
  {
  prevNeighborInvalid = true;
  nextNeighborInvalid = true;
  if(next!=null){next.prev=null;}
  if(prev!=null){prev.next=null;}
  prev = next = null;
  }

public TileTorqueTransportShaft prev()
  {
  if(prevNeighborInvalid)
    {
    prevNeighborInvalid = false;
    ITorqueTile input = getTorqueCache()[orientation.getOpposite().ordinal()];
    if(input !=null && input.getClass()==this.getClass() && input.canOutputTorque(orientation))
      {
      prev = (TileTorqueTransportShaft) input;
      prev.next=this;
      } 
    }
  return prev;
  }

public TileTorqueTransportShaft next()
  {
  if(nextNeighborInvalid)
    {
    nextNeighborInvalid = false;
    ITorqueTile output = getTorqueCache()[orientation.ordinal()];
    if(output !=null && output.getClass()==this.getClass() && output.canInputTorque(orientation.getOpposite()))
      {
      next = (TileTorqueTransportShaft) output;
      next.prev=this;
      }
    }
  return next;
  }

@Override
public float getClientOutputRotation(ForgeDirection from, float delta)
  {
  return prev()==null ? getRotation(rotation, prevRotation, delta) : prev().getClientOutputRotation(from, delta);
  }

}
