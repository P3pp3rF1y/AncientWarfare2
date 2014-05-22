package net.shadowmage.ancientwarfare.automation.tile.torque;

import net.minecraftforge.common.util.ForgeDirection;

public class TileTorqueGeneratorHandCranked extends TileTorqueGeneratorBase
{

//TODO have this extends IWorkSite
@Override
public boolean canOutput(ForgeDirection towards)
  {
  return towards==ForgeDirection.getOrientation(getBlockMetadata());
  }

}
