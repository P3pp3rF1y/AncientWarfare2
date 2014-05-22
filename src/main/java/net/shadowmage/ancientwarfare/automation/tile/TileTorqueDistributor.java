package net.shadowmage.ancientwarfare.automation.tile;

import net.minecraftforge.common.util.ForgeDirection;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque.ITorqueTransport;
import buildcraft.api.mj.MjBattery;


public class TileTorqueDistributor extends TileTorqueTransportBase implements ITorqueTransport
{

@MjBattery(maxCapacity = TileTorqueConduit.maxEnergy)
double storedEnergy;

@Override
public void setEnergy(double energy)
  {
  this.storedEnergy = energy;
  }

@Override
public double getEnergyStored()
  {
  return storedEnergy;
  }

@Override
public double getMaxOutput()
  {
  return TileTorqueConduit.maxOutput;
  }

@Override
public double getMaxInput()
  {
  return TileTorqueConduit.maxInput;
  }

@Override
public boolean canInput(ForgeDirection from)
  {
  return from==ForgeDirection.getOrientation(getBlockMetadata()).getOpposite();
  }

@Override
public boolean canOutput(ForgeDirection towards)
  {
  return towards!=ForgeDirection.getOrientation(getBlockMetadata()).getOpposite();
  }

@Override
public String toString()
  {
  return "Torque Distributor["+storedEnergy+"]";
  }

}
