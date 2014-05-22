package net.shadowmage.ancientwarfare.automation.tile.torque;

import net.minecraftforge.common.util.ForgeDirection;


public class TileTorqueDistributor extends TileTorqueTransportBase
{

@Override
public boolean canInput(ForgeDirection from)
  {
  return from==ForgeDirection.getOrientation(getBlockMetadata()).getOpposite();
  }

@Override
public boolean canOutput(ForgeDirection towards)
  {
  return towards!=ForgeDirection.getOrientation(getBlockMetadata()).getOpposite();
  }

}
