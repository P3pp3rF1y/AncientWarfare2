package net.shadowmage.ancientwarfare.automation.tile.torque;

import net.shadowmage.ancientwarfare.automation.config.AWAutomationStatics;

public class TileTorqueTransportConduitMedium extends TileTorqueTransportConduit
{

public TileTorqueTransportConduitMedium()
  {
  energyDrainFactor = AWAutomationStatics.med_drain_factor;
  maxEnergy = AWAutomationStatics.med_conduit_energy_max;
  maxOutput = AWAutomationStatics.med_transfer_max;
  maxInput = AWAutomationStatics.med_transfer_max;
  }

}
