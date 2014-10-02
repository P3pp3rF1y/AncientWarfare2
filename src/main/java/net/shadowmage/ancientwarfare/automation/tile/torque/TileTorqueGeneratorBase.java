package net.shadowmage.ancientwarfare.automation.tile.torque;

import net.minecraftforge.common.util.ForgeDirection;


public abstract class TileTorqueGeneratorBase extends TileTorqueBase
{

@Override
public boolean canInputTorque(ForgeDirection from)
  {
  return false;
  }

}
