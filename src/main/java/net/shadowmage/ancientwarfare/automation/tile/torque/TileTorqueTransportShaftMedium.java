package net.shadowmage.ancientwarfare.automation.tile.torque;

import net.shadowmage.ancientwarfare.automation.config.AWAutomationStatics;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque.TorqueCell;


public class TileTorqueTransportShaftMedium extends TileTorqueTransportShaft
{

public TileTorqueTransportShaftMedium()
  {
  double max = AWAutomationStatics.med_transfer_max;
  torqueCell = new TorqueCell(max, max, max, 1);
  }
}
