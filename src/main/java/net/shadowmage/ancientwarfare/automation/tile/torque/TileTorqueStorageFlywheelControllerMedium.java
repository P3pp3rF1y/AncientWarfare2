package net.shadowmage.ancientwarfare.automation.tile.torque;

import net.shadowmage.ancientwarfare.automation.config.AWAutomationStatics;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque.TorqueCell;

public class TileTorqueStorageFlywheelControllerMedium extends TileTorqueStorageFlywheelController
{
public TileTorqueStorageFlywheelControllerMedium()
  {
  double max = AWAutomationStatics.med_transfer_max;
  inputCell = new TorqueCell(max, max, max, 1.f);//TODO set default values from config
  outputCell = new TorqueCell(max, max, max, 1.f);//TODO set default values from config
  }
}
