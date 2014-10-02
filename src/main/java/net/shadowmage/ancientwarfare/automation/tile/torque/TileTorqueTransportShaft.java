package net.shadowmage.ancientwarfare.automation.tile.torque;

import net.minecraftforge.common.util.ForgeDirection;
import net.shadowmage.ancientwarfare.automation.config.AWAutomationStatics;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque.ITorqueTile;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque.TorqueCell;

public class TileTorqueTransportShaft extends TileTorqueTransportBase
{

private TileTorqueTransportShaft prev, next;

private TorqueCell torqueCell;

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

public TileTorqueTransportShaft()
  {
  torqueCell = new TorqueCell(32, 32, 32, 1);
  }

@Override
public void updateEntity()
  {
  super.updateEntity();
  if(!worldObj.isRemote)
    { 
    serverNetworkUpdate(); 
    torqueIn = getTotalTorque() - prevEnergy;
    torqueOut = transferPowerTo(getPrimaryFacing());
    prevEnergy = getTotalTorque();
    }
  else
    {
    clientNetworkUpdate();
    updateRotation();
    }
  }

@Override
protected void serverNetworkSynch()
  {
  if(prev()==null)
    {
    int percent = (int)(torqueCell.getPercentFull()*100.d);
    percent += (int)(torqueOut / torqueCell.getMaxOutput());
    if(percent>100){percent=100;}
    if(percent != clientDestEnergyState)
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
      double r = AWAutomationStatics.low_rpt * clientEnergyState;
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

@Override
protected void clientNetworkUpdate()
  {
  if(clientEnergyState != clientDestEnergyState)
    {
    if(networkUpdateTicks>=0)
      {
      clientEnergyState += (clientDestEnergyState - clientEnergyState) / ((double)networkUpdateTicks+1.d);
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
  clientDestEnergyState = ((double)value) * 0.01d;
  }

@Override
public void onNeighborTileChanged()
  {
  super.onNeighborTileChanged();
  prev = next = null;
  ITorqueTile output = getTorqueCache()[orientation.ordinal()];
  if(output !=null && output.getClass()==this.getClass() && output.canInputTorque(orientation.getOpposite()))
    {
    next = (TileTorqueTransportShaft) output;
    next.prev=this;
    }
  ITorqueTile input = getTorqueCache()[orientation.getOpposite().ordinal()];
  if(input !=null && input.getClass()==this.getClass() && input.canOutputTorque(orientation))
    {
    prev = (TileTorqueTransportShaft) input;
    prev.next=this;
    }
  }

public TileTorqueTransportShaft prev()
  {
  return prev;
  }

public TileTorqueTransportShaft next()
  {
  return next;
  }

@Override
public boolean canOutputTorque(ForgeDirection towards)
  {
  return towards==orientation;
  }

@Override
public boolean canInputTorque(ForgeDirection from)
  {
  return from==orientation.getOpposite();
  }

@Override
public double getMaxTorque(ForgeDirection from)
  {
  return torqueCell.getMaxEnergy();
  }

@Override
public double getTorqueStored(ForgeDirection from)
  {
  return torqueCell.getEnergy();
  }

@Override
public double addTorque(ForgeDirection from, double energy)
  {
  return torqueCell.addEnergy(energy);
  }

@Override
public double drainTorque(ForgeDirection from, double energy)
  {
  return torqueCell.drainEnergy(energy);
  }

@Override
public double getMaxTorqueOutput(ForgeDirection from)
  {
  return torqueCell.getMaxTickOutput();
  }

@Override
public double getMaxTorqueInput(ForgeDirection from)
  {
  return torqueCell.getMaxTickInput();
  }

@Override
public boolean useOutputRotation(ForgeDirection from)
  {
  return true;
  }

@Override
public float getClientOutputRotation(ForgeDirection from, float delta)
  {
  return prev()==null ? getRotation(rotation, prevRotation, delta) : prev().getClientOutputRotation(from, delta);
  }

@Override
protected double getTotalTorque()
  {
  return torqueCell.getEnergy();
  }

}
