package net.shadowmage.ancientwarfare.automation.tile.torque;

import net.minecraftforge.common.util.ForgeDirection;

public class TileTorqueTransportDistributor extends TileTorqueTransportConduit
{

public TileTorqueTransportDistributor()
  {
  
  }

@Override
public boolean canInputTorque(ForgeDirection from)
  {
  return from==orientation.getOpposite();
  }

@Override
public boolean canOutputTorque(ForgeDirection towards)
  {
  return !canInputTorque(towards);//towards!=orientation.getOpposite();
  }

}
