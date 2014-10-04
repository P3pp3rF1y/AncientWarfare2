package net.shadowmage.ancientwarfare.automation.tile.torque;

import net.shadowmage.ancientwarfare.automation.config.AWAutomationStatics;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque.TorqueCell;


public class TileTorqueTransportShaftHeavy extends TileTorqueTransportShaft
{

public TileTorqueTransportShaftHeavy()
  {
  double max = AWAutomationStatics.high_transfer_max;
  torqueCell = new TorqueCell(max, max, max, 1);
  }

}
