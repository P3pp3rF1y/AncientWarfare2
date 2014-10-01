package net.shadowmage.ancientwarfare.automation.tile.torque;

import net.minecraftforge.common.util.ForgeDirection;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque.ITorqueTile;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque.TorqueCell;

public class TileTorqueTransportShaft extends TileTorqueTransportBase
{

public TileTorqueTransportShaft prev, next;

TorqueCell input, output, store;

public TileTorqueTransportShaft()
  {
  input = new TorqueCell(32, 32, 32, 1);
  output = new TorqueCell(32, 32, 32, 1);
  store = new TorqueCell(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE, 1);
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
  return cell==null ? 0 : cell.getMaxOutput();
  }

@Override
public double getMaxTorqueInput(ForgeDirection from)
  {
  TorqueCell cell = getCell(from);
  return cell==null ? 0 : cell.getMaxInput();
  }

@Override
public boolean useOutputRotation(ForgeDirection from)
  {
  // TODO Auto-generated method stub
  return true;
  }

@Override
public double getClientOutputRotation(ForgeDirection from)
  {
  // TODO Auto-generated method stub
  return 0;
  }

@Override
public double getPrevClientOutputRotation(ForgeDirection from)
  {
  // TODO Auto-generated method stub
  return 0;
  }

//@Override
//protected void outputPower()
//  {
//  if(prev==null)//head of the line
//    {
//    double total = storedEnergy;
//    TileTorqueTransportShaft n = next;
//    TileTorqueTransportShaft last = this;//might also be end of the line...
//    int num = 1;
//    while(n!=null)
//      {
//      total+=n.storedEnergy;
//      num++;
//      last = n;
//      n = n.next;
//      }    
//    total /= (double)num;
//    storedEnergy = total;
//    n = next;
//    while(n!=null)
//      {
//      n.storedEnergy = total;
//      n = n.next;
//      }
//    ITorque.transferPower(worldObj, last.xCoord, last.yCoord, last.zCoord, last); 
//    }
//  }
//
//@Override
//protected void updateRotation()
//  {
//  if(prev==null)
//    {
//    super.updateRotation();    
//    TileTorqueTransportShaft n = next;
//    while(n!=null)
//      {
//      n.rotation = rotation;
//      n.prevRotation = prevRotation;
//      n = n.next;
//      }    
//    }
//  }

}
