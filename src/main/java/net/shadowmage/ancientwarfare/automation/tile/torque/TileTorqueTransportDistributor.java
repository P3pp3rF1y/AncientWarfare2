package net.shadowmage.ancientwarfare.automation.tile.torque;

import net.minecraftforge.common.util.ForgeDirection;
import net.shadowmage.ancientwarfare.automation.config.AWAutomationStatics;


public class TileTorqueTransportDistributor extends TileTorqueTransportConduit
{

public TileTorqueTransportDistributor()
  {
  energyDrainFactor = AWAutomationStatics.low_drain_factor;
  maxEnergy = AWAutomationStatics.low_conduit_energy_max;
  maxOutput = AWAutomationStatics.low_transfer_max;
  maxInput = AWAutomationStatics.low_transfer_max;
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
