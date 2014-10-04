package net.shadowmage.ancientwarfare.automation.tile.torque;

import net.minecraftforge.common.util.ForgeDirection;
import net.shadowmage.ancientwarfare.automation.config.AWAutomationStatics;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque.SidedTorqueCell;


public class TileDistributorMedium extends TileDistributor
{
public TileDistributorMedium()
  {
  double max = AWAutomationStatics.med_transfer_max;
  for(int i = 0; i <6; i++)
    {
    storage[i] = new SidedTorqueCell(max, max, max, 1, ForgeDirection.values()[i], this);//TODO set from config
    }
  }
}
