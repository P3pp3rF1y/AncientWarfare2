package net.shadowmage.ancientwarfare.automation.tile.torque;

import net.shadowmage.ancientwarfare.automation.config.AWAutomationStatics;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque.TorqueCell;

public class TileTorqueShaftLight extends TileTorqueShaft
{

public TileTorqueShaftLight()
  {
  double max = AWAutomationStatics.low_transfer_max;
  torqueCell = new TorqueCell(max, max, max, AWAutomationStatics.low_efficiency_factor);
  }

}
