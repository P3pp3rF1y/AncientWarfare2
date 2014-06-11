package net.shadowmage.ancientwarfare.automation.tile.torque;

import net.minecraftforge.common.util.ForgeDirection;


public class TileTorqueTransportDistributor extends TileTorqueTransportBase
{

@Override
public boolean canInput(ForgeDirection from)
  {
  return from==orientation.getOpposite();
  }

@Override
public boolean canOutput(ForgeDirection towards)
  {
  return !canInput(towards);//towards!=orientation.getOpposite();
  }

}
