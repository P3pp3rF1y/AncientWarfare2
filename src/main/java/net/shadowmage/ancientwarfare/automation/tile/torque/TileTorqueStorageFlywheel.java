package net.shadowmage.ancientwarfare.automation.tile.torque;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.shadowmage.ancientwarfare.automation.config.AWAutomationStatics;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque;


public class TileTorqueStorageFlywheel extends TileTorqueStorageBase
{

private List<TileTorqueStorageFlywheel> wheelsToBalance = new ArrayList<TileTorqueStorageFlywheel>();

public TileTorqueStorageFlywheel()
  {
  energyDrainFactor = AWAutomationStatics.low_drain_factor;
  maxEnergy = AWAutomationStatics.low_storage_energy_max;
  maxOutput = AWAutomationStatics.low_transfer_max;
  maxInput = AWAutomationStatics.low_transfer_max;
  }

@Override
public void updateEntity()
  {
  if(worldObj.isRemote){return;}
  if(!worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord))
    {
    ITorque.transferPower(worldObj, xCoord, yCoord, zCoord, this);    
    }
  ITorque.applyPowerDrain(this);
  tryBalancingFlywheels();    
  }

private void tryBalancingFlywheels()
  {
  TileEntity te = worldObj.getTileEntity(xCoord, yCoord+1, zCoord);
  if((te instanceof TileTorqueStorageFlywheel))
    {
    return;
    }
  int y = yCoord-1;
  te = worldObj.getTileEntity(xCoord, y, zCoord);
  while(y<156 && y>=1 && te instanceof TileTorqueStorageFlywheel)
    {
    wheelsToBalance.add((TileTorqueStorageFlywheel) te);
    y--;
    te = worldObj.getTileEntity(xCoord, y, zCoord);
    }
  wheelsToBalance.add(this);
  double totalPower = 0.d;
  double average;
  for(TileTorqueStorageFlywheel wheel : wheelsToBalance)
    {
    totalPower+=wheel.getEnergyStored();
    }
  average = totalPower / (double)wheelsToBalance.size();
  for(TileTorqueStorageFlywheel wheel : wheelsToBalance)
    {
    wheel.setEnergy(average);
    }
  wheelsToBalance.clear();
  }

@Override
public boolean canInput(ForgeDirection from)
  {
  return !canOutput(from);
  }

@Override
public boolean canOutput(ForgeDirection towards)
  {
  return towards==orientation;
  }

}
