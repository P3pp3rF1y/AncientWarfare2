package net.shadowmage.ancientwarfare.automation.tile.torque;

import net.shadowmage.ancientwarfare.automation.config.AWAutomationStatics;

public class TileTorqueStorageFlywheelControllerMedium extends TileTorqueStorageFlywheelController
{
public TileTorqueStorageFlywheelControllerMedium()
  {
  energyDrainFactor = AWAutomationStatics.med_drain_factor;
  maxEnergy = AWAutomationStatics.med_storage_energy_max;
  maxOutput = AWAutomationStatics.med_transfer_max;
  maxInput = AWAutomationStatics.med_transfer_max;
  maxRpm = 200;
  }
}
