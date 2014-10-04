package net.shadowmage.ancientwarfare.automation.tile.torque;

import net.minecraftforge.common.util.ForgeDirection;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque.TorqueCell;

public abstract class TileDistributor extends TileTorqueSidedCell
{

public TileDistributor()
  {
  
  }

@Override
protected double transferPower()
  {
  int in = getPrimaryFacing().getOpposite().ordinal();
  double out = 0;
  for(int i = 0; i< 6; i++)
    {
    if(i==in){continue;}
    out += transferPowerTo(ForgeDirection.values()[i]);
    }
  return out;
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
    double request, trans;
    double transferred = 0;
    for(int i = 0; i < 6; i++)
      {
      if(i==in){continue;}
      out = storage[i];
      request = out.getMaxEnergy() - out.getEnergy();
      trans = request * percent;      
      transferred += trans;
      out.setEnergy(out.getEnergy() + trans);
      }
    input.setEnergy(input.getEnergy() - transferred);
    }  
  }

@Override
public boolean canInputTorque(ForgeDirection from)
  {
  return from==orientation.getOpposite();
  }

@Override
public boolean canOutputTorque(ForgeDirection from)
  {
  return from!=orientation.getOpposite();
  }

}
