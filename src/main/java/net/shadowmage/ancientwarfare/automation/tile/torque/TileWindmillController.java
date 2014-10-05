package net.shadowmage.ancientwarfare.automation.tile.torque;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.shadowmage.ancientwarfare.automation.config.AWAutomationStatics;
import net.shadowmage.ancientwarfare.automation.tile.torque.multiblock.TileWindmillBlade;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque.TorqueCell;

public class TileWindmillController extends TileTorqueSingleCell
{

double bladeRpm = 20.d;
double bladeRpt = bladeRpm * AWAutomationStatics.rpmToRpt;

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
    TileWindmillBlade blade = getControlledBlade();
    if(blade!=null)
      {
      double d = 1.0 * blade.windmillSize;
      torqueCell.setEnergy(torqueCell.getEnergy()+d);
      }
    }
  }

private TileWindmillBlade getControlledBlade()
  {
  TileEntity te;  
  ForgeDirection d = getPrimaryFacing().getOpposite();
  int x = xCoord + d.offsetX;
  int y = yCoord + d.offsetY;
  int z = zCoord + d.offsetZ;
  if(worldObj.blockExists(x, y, z) && (te = worldObj.getTileEntity(x, y, z)) instanceof TileWindmillBlade)
    {
    TileWindmillBlade blade = (TileWindmillBlade)te;
    if(blade.isControl){return blade;}
    }  
  return null;
  }

@Override
protected void updateRotation()
  {
  super.updateRotation();
  TileWindmillBlade blade = getControlledBlade();
  if(blade!=null)
    {
//    blade.windmillDirection = getPrimaryFacing().ordinal();//they only spin in one direction 
    blade.prevRotation=blade.rotation;
    blade.rotation += bladeRpt;
    blade.hasController=true;
    }
  }

@Override
public boolean canInputTorque(ForgeDirection from)
  {
  return false;
  }

}
