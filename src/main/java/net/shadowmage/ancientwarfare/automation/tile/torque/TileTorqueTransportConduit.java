package net.shadowmage.ancientwarfare.automation.tile.torque;

import net.minecraftforge.common.util.ForgeDirection;
import net.shadowmage.ancientwarfare.automation.config.AWAutomationStatics;

public class TileTorqueTransportConduit extends TileTorqueTransportBase
{

public TileTorqueTransportConduit()
  {
  
  }

@Override
public boolean canInputTorque(ForgeDirection from)
  {
  return from!=orientation;
  }

@Override
public boolean canOutputTorque(ForgeDirection towards)
  {
  return towards==orientation;
  }

public boolean[] getConnections()
  {
  // TODO Auto-generated method stub
  return null;
  }

}
