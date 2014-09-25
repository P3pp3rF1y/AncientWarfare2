package net.shadowmage.ancientwarfare.automation.tile.torque;

import net.shadowmage.ancientwarfare.automation.config.AWAutomationStatics;

public class TileTorqueTransportConduitHeavy extends TileTorqueTransportConduit
{

public TileTorqueTransportConduitHeavy()
  {
  energyDrainFactor = AWAutomationStatics.high_drain_factor;
  maxEnergy = AWAutomationStatics.high_conduit_energy_max;
  maxOutput = AWAutomationStatics.high_transfer_max;
  maxInput = AWAutomationStatics.high_transfer_max;
  this.maxRpm = 100;
  }

}
