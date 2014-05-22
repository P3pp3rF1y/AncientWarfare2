package net.shadowmage.ancientwarfare.automation.tile.torque;

import buildcraft.api.mj.MjBattery;

public class TileTorqueJunction extends TileTorqueTransportBase
{

private static final double maxEnergy = 1000;

@MjBattery(maxCapacity = maxEnergy)
double storedEnergy;

@Override
public String toString()
  {
  return "Torque Junction["+storedEnergy+"]";
  }

@Override
public void setEnergy(double energy)
  {
  storedEnergy = energy;
  }

@Override
public double getEnergyStored()
  {
  return storedEnergy;
  }

}
