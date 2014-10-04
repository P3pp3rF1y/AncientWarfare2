package net.shadowmage.ancientwarfare.automation.tile.torque;

import net.minecraftforge.common.util.ForgeDirection;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque.TorqueCell;

public class TileWindmillController extends TileTorqueSingleCell
{

public TileWindmillController()
  {
  torqueCell = new TorqueCell(32,32,32,1);
  }

@Override
public void updateEntity()
  {
  super.updateEntity();
  if(!worldObj.isRemote)
    {
    //TODO generate energy from windmill if present
    }
  }

@Override
protected void updateRotation()
  {
  super.updateRotation();
  //TODO update windmill blade rotation/direction
  }

@Override
public boolean canInputTorque(ForgeDirection from)
  {
  return false;
  }

}
