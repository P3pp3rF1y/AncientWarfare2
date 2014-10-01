package net.shadowmage.ancientwarfare.automation.tile.torque;

import net.minecraftforge.common.util.ForgeDirection;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque.TorqueCell;

public class TileTorqueTransportDistributor extends TileTorqueTransportConduit
{

public TileTorqueTransportDistributor()
  {
  
  }

@Override
protected void transferPower()
  {
  int in = getPrimaryFacing().getOpposite().ordinal();  
  for(int i = 0; i< 6; i++)
    {
    if(i==in){continue;}
    transferPowerTo(ForgeDirection.values()[i]);
    }
  }

@Override
protected void balanceStorage()
  {
  int in = getPrimaryFacing().getOpposite().ordinal();  
  TorqueCell input = storage[in];  
  double totalRequested = 0;
  
  TorqueCell out;
  for(int i = 0; i < 6; i++)
    {
    if(i==in){continue;}
    out = storage[i];
    totalRequested += out.getMaxEnergy() - out.getEnergy();
    }  
  if(totalRequested>0 && input.getEnergy()>0)
    {
    double transfer = Math.min(totalRequested, input.getEnergy());  
    double percent = transfer / totalRequested;
    for(int i = 0; i < 6; i++)
      {
      if(i==in){continue;}
      out = storage[i];     
      out.setEnergy(out.getEnergy() + percent*transfer);
      }
    input.setEnergy(input.getEnergy() - transfer);
    }  
  }

@Override
public boolean canInputTorque(ForgeDirection from)
  {
  return from==orientation.getOpposite();
  }

@Override
public boolean canOutputTorque(ForgeDirection towards)
  {
  return !canInputTorque(towards);//towards!=orientation.getOpposite();
  }

}
