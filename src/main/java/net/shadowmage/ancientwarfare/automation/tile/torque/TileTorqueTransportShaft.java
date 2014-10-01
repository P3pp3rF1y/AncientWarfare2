package net.shadowmage.ancientwarfare.automation.tile.torque;

import net.minecraftforge.common.util.ForgeDirection;
import net.shadowmage.ancientwarfare.automation.config.AWAutomationStatics;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque.ITorqueTile;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque.TorqueCell;

public class TileTorqueTransportShaft extends TileTorqueTransportBase
{

private TileTorqueTransportShaft prev, next;

private TorqueCell input, output, store;

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
  input = new TorqueCell(32, 32, 32, 1);
  output = new TorqueCell(32, 32, 32, 1);
  store = new TorqueCell(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE, 1);
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
  if(next()==null)
    {
    transferPowerTo(getPrimaryFacing());    
    }
  }

protected void balanceStorage()
  {
  if(prev()==null)
    {
    double total = output.getEnergy() + input.getEnergy();
    TileTorqueTransportShaft n = next;
    TileTorqueTransportShaft last = this;//might also be end of the line...
    int num = 1;
    while(n!=null)
      {
      total+=n.output.getEnergy();
      num++;
      last = n;
      n = n.next;
      } 
    double perTile = total / (double)num;
        
    if(perTile>output.getMaxEnergy())//too much
      {
      double extra;
      perTile = output.getMaxEnergy();
      extra = total - (double)num * perTile;
      input.setEnergy(extra);
      }
    else
      {
      input.setEnergy(0);
      }
    //start at beginning of loop, set energy of output in each tile to perTile value
    n = next;
    while(n!=null)
      {
      n.output.setEnergy(perTile);
      n = n.next;
      }
    }
  }

//@Override
//protected void outputPower()
//{
//if(prev==null)//head of the line
//  {

//  ITorque.transferPower(worldObj, last.xCoord, last.yCoord, last.zCoord, last); 
//  }
//}

@Override
protected void serverNetworkSynch()
  {
  if(prev()==null)
    {
    int percent = (int)(output.getPercentFull()*100.d);
    percent += (int)(torqueOut / output.getMaxOutput());
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

TorqueCell getCell(ForgeDirection dir)
  {
  return dir==orientation? output : dir==orientation.getOpposite() ? input : null;
  }

@Override
public double getMaxTorque(ForgeDirection from)
  {
  TorqueCell cell = getCell(from);
  return cell==null ? 0 : cell.getMaxEnergy();
  }

@Override
public double getTorqueStored(ForgeDirection from)
  {
  TorqueCell cell = getCell(from);
  return cell==null ? 0 : cell.getEnergy();
  }

@Override
public double addTorque(ForgeDirection from, double energy)
  {
  TorqueCell cell = getCell(from);
  return cell==null ? 0 : cell.addEnergy(energy);
  }

@Override
public double drainTorque(ForgeDirection from, double energy)
  {
  TorqueCell cell = getCell(from);
  return cell==null ? 0 : cell.drainEnergy(energy);
  }

@Override
public double getMaxTorqueOutput(ForgeDirection from)
  {
  TorqueCell cell = getCell(from);
  return cell==null ? 0 : cell.getMaxTickOutput();
  }

@Override
public double getMaxTorqueInput(ForgeDirection from)
  {
  TorqueCell cell = getCell(from);
  return cell==null ? 0 : cell.getMaxTickInput();
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
  return input.getEnergy()+output.getEnergy()+store.getEnergy();
  }

}
