package net.shadowmage.ancientwarfare.automation.tile.torque;

import net.minecraftforge.common.util.ForgeDirection;
import net.shadowmage.ancientwarfare.automation.config.AWAutomationStatics;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque.SidedTorqueCell;

public class TileConduitLight extends TileTorqueSidedCell
{

public TileConduitLight()
  {
  double max = AWAutomationStatics.low_transfer_max;
  double eff = AWAutomationStatics.low_efficiency_factor;
  for(int i = 0; i <6; i++)
    {
    storage[i] = new SidedTorqueCell(max, max, max, eff, ForgeDirection.values()[i], this);//TODO set from config
    }
  }

}
