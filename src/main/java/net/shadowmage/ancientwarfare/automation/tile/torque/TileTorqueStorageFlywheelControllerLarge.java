package net.shadowmage.ancientwarfare.automation.tile.torque;

import net.shadowmage.ancientwarfare.automation.config.AWAutomationStatics;

public class TileTorqueStorageFlywheelControllerLarge extends TileTorqueStorageFlywheelController
{
public TileTorqueStorageFlywheelControllerLarge()
  {
  energyDrainFactor = AWAutomationStatics.high_drain_factor;
  maxEnergy = AWAutomationStatics.high_storage_energy_max;
  maxOutput = AWAutomationStatics.high_transfer_max;
  maxInput = AWAutomationStatics.high_transfer_max;
  maxRpm = 300;
  }
}
